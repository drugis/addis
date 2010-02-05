package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.Map.Entry;

import javax.swing.table.TableModel;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.imports.ClinicaltrialsImporter;

import com.jgoodies.binding.value.ValueModel;

public class AddStudyWizardPresentation {
	
	@SuppressWarnings("serial")
	private class DrugListHolder extends AbstractListHolder<Drug> implements PropertyChangeListener, DomainListener {
		public DrugListHolder() {
			d_domain.addListener(this);
		}
		
		@Override
		public List<Drug> getValue() {
			return new ArrayList<Drug>(d_domain.getDrugs());
		}

		public void propertyChange(PropertyChangeEvent evt) {
			fireValueChange(null, getValue());
		}

		public void domainChanged(DomainEvent evt) {
			fireValueChange(null, getValue());
		}
	}
	
	@SuppressWarnings("serial")
	private abstract static class OutcomeListHolder 
	extends AbstractListHolder<OutcomeMeasure> 
	implements PropertyChangeListener, DomainListener {
		protected Domain d_domain;
		
		public OutcomeListHolder(Domain domain) {
			domain.addListener(this);
			d_domain = domain;
		}

		public void propertyChange(PropertyChangeEvent arg0) {
			fireValueChange(null, getValue());			
		}

		public void domainChanged(DomainEvent evt) {
			fireValueChange(null, getValue());			
		}
	}
	
	@SuppressWarnings("serial")
	private static class EndpointListHolder extends OutcomeListHolder {
		public EndpointListHolder(Domain domain) {
			super(domain);
		}
		
		@Override
		public List<OutcomeMeasure> getValue() {
			return new ArrayList<OutcomeMeasure>(d_domain.getEndpoints());
		}
	}

	@SuppressWarnings("serial")
	private static class ADEListHolder extends OutcomeListHolder {
		public ADEListHolder(Domain domain) {
			super(domain);
		}
		
		@Override
		public List<OutcomeMeasure> getValue() {
			return new ArrayList<OutcomeMeasure>(d_domain.getAdes());
		}
	}
	
	@SuppressWarnings("serial")
	private static class OutcomeMeasureHolder extends AbstractHolder<OutcomeMeasure> {
		private List<AbstractHolder<OutcomeMeasure>> d_selectionModelList;
		private ListHolder<OutcomeMeasure> d_validValues;
		
		/**
		 * OutcomeMeasureHolder that validates it's values against a list of outcomes and
		 * that makes sure each outcome is selected only once.
		 * @param modelList
		 * @param validValues
		 */
		public OutcomeMeasureHolder(List<AbstractHolder<OutcomeMeasure>> modelList,
				ListHolder<OutcomeMeasure> validValues) {
			d_selectionModelList = modelList;
			d_validValues = validValues;
		}
		
		@Override
		protected void checkArgument(Object newValue) {
			if (newValue != null)
				if (!d_validValues.getValue().contains(newValue))
					throw new IllegalArgumentException(newValue + " is not in the set of allowed values.");
		}
		
		@Override
		protected void cascade() {
			// If the outcome that was selected is already selected somewhere else, reset the other selection
			for (AbstractHolder<OutcomeMeasure> omHolder : d_selectionModelList) {
				if ((!omHolder.equals(this)) && (omHolder.getValue() != null))
					if (omHolder.getValue().equals(getValue()))
						omHolder.setValue(null);
			}
		}
	}
	
	@SuppressWarnings("serial")
	private class EndpointHolder extends OutcomeMeasureHolder {
		public EndpointHolder() {
			super(d_selectedEndpointsList, getEndpointListModel());
		}
	}
	
	@SuppressWarnings("serial")
	private class AdverseEventHolder extends OutcomeMeasureHolder {
		public AdverseEventHolder() {
			super(d_selectedADEsList, getADEListModel());
		}
	}
	
	@SuppressWarnings("serial")
	private class IndicationListHolder extends AbstractListHolder<Indication> {
		public IndicationListHolder() {
			d_domain.addListener(new DomainListener() {
				public void domainChanged(DomainEvent evt) {
					fireValueChange(null, getValue());
				}
			});
		}
		
		@Override
		public List<Indication> getValue() {
			return new ArrayList<Indication>(d_domain.getIndications());
		}
	}
	
	private Domain d_domain;
	private PresentationModelFactory d_pmf;
	private StudyPresentationModel d_newStudyPM;
	private StudyPresentationModel d_oldStudyPM;
	
	List<AbstractHolder<OutcomeMeasure>> d_selectedEndpointsList;
	List<AbstractHolder<OutcomeMeasure>> d_selectedADEsList;
	List<BasicArmPresentation> d_selectedArmList;
	private ListHolder<OutcomeMeasure> d_endpointListHolder;
	private ListHolder<OutcomeMeasure> d_adverseEventListHolder;
	
	public AddStudyWizardPresentation(Domain d, PresentationModelFactory pmf) {
		d_domain = d;
		d_pmf = pmf;
		d_endpointListHolder = new EndpointListHolder(d_domain);
		d_adverseEventListHolder = new ADEListHolder(d_domain);
		clearStudies();
	}
	
	public ValueModel getSourceModel() {
		return new MutableCharacteristicHolder(getNewStudy(), BasicStudyCharacteristic.SOURCE);
	}
	
	public ValueModel getSourceNoteModel() {
		return new StudyNoteHolder(getOldStudy(), BasicStudyCharacteristic.SOURCE);
	}
	
	public ValueModel getIdModel() {
		return d_newStudyPM.getModel(Study.PROPERTY_ID);
	}
	
	public ValueModel getIdNoteModel() {
		return new StudyNoteHolder(getOldStudy(), Study.PROPERTY_ID);
	}
	
	public ValueModel getTitleModel() {
		return new MutableCharacteristicHolder(getNewStudy(), BasicStudyCharacteristic.TITLE);
	}

	public void importCT() throws MalformedURLException, IOException{
		Object studyID = getIdModel().getValue();
		String url = "http://clinicaltrials.gov/show/"+studyID+"?displayxml=true";
		d_oldStudyPM = (StudyPresentationModel) new StudyPresentationModel(ClinicaltrialsImporter.getClinicaltrialsData(url),d_pmf);
		d_newStudyPM = (StudyPresentationModel) new StudyPresentationModel(new Study("", new Indication(0l,"")),d_pmf);
		migrateImportToNew(studyID);
	}

	private void migrateImportToNew(Object studyID) {
		// Characteristics
		getNewStudy().getCharacteristics().putAll(getOldStudy().getCharacteristics());
		// Source
		getSourceModel().setValue(BasicStudyCharacteristic.Source.CLINICALTRIALS);
		// Id & Title
		getIdModel().setValue(studyID);
		getTitleModel().setValue(getOldStudy().getCharacteristic(BasicStudyCharacteristic.TITLE));
		
		// Endpoints.
		d_selectedEndpointsList = new ArrayList<AbstractHolder<OutcomeMeasure>>();
		addEndpointModels(getOldStudy().getOutcomeMeasures().size());
		
		// Arms & Dosage
		d_selectedArmList = new ArrayList<BasicArmPresentation>();
		addArmModels(getOldStudy().getArms().size());

	}

	public ListHolder<Indication> getIndicationListModel() {
		return new IndicationListHolder();
	}
	
	public ValueModel getIndicationModel() {
		return d_newStudyPM.getModel(Study.PROPERTY_INDICATION);
	}

	public ValueModel getIndicationNoteModel() {
		return new StudyNoteHolder(getOldStudy(), Study.PROPERTY_INDICATION);
	}
	
	public void clearStudies() {
		d_oldStudyPM = (StudyPresentationModel) new StudyPresentationModel(new Study("", new Indication(0l,"")),d_pmf);
		d_newStudyPM = (StudyPresentationModel) new StudyPresentationModel(new Study("", new Indication(0l,"")),d_pmf);
		getSourceModel().setValue(BasicStudyCharacteristic.Source.MANUAL);
		d_selectedEndpointsList = new ArrayList<AbstractHolder<OutcomeMeasure>>();
		d_selectedADEsList = new ArrayList<AbstractHolder<OutcomeMeasure>>();
		d_selectedArmList = new ArrayList<BasicArmPresentation>();
		addEndpointModels(1);
		addArmModels(2);
		
	}
	
	
	public MutableCharacteristicHolder getCharacteristicModel(BasicStudyCharacteristic c) {
		return new MutableCharacteristicHolder(getNewStudy(),c);
	}

	public ValueModel getCharacteristicNoteModel(BasicStudyCharacteristic c) {
		return new StudyNoteHolder(getOldStudy(), c);
	}
	
	public ListHolder<OutcomeMeasure> getEndpointListModel() {
		return d_endpointListHolder;
	}

	public ValueModel getEndpointNoteModel(int i) {
		if(d_oldStudyPM.getEndpoints().size() <= i)
			return null;
		return new StudyNoteHolder(getOldStudy(),new ArrayList<OutcomeMeasure>(d_oldStudyPM.getEndpoints()).get(i));
	}
	
	public int getNumberEndpoints() {
		return d_selectedEndpointsList.size();
	}
	
	public void addEndpointModels(int numEndpoints){
		for (int i=0; i<numEndpoints; ++i)
			d_selectedEndpointsList.add(new EndpointHolder());
	}
	
	public ValueModel getEndpointModel(int i) {
		if (i >= d_selectedEndpointsList.size())
			throw new IndexOutOfBoundsException("no endpoint at index: "+i);
		
		return d_selectedEndpointsList.get(i);
	}

	public void removeEndpoint(int i) {
		d_selectedEndpointsList.remove(i);
		if ( getOldStudy().getOutcomeMeasures().size() > i)
			getOldStudy().getOutcomeMeasures().remove(d_oldStudyPM.getEndpoints().get(i));
	}

	
	public void addArmModels(int numArms) {
		for(int i = 0; i<numArms; ++i){
			d_selectedArmList.add(new BasicArmPresentation(new Arm(new Drug("", ""), new FixedDose(0l, SIUnit.MILLIGRAMS_A_DAY),0), d_pmf));
		}
	}
	
	public int getNumberArms(){
		return d_selectedArmList.size();
	}
	
	public void removeArm(int armNum){
		d_selectedArmList.remove(armNum);
		
		if( getOldStudy().getArms().size() > armNum )
			getOldStudy().getArms().remove(getOldStudy().getArms().get(armNum));	
	}
	
	public DrugListHolder getDrugsModel(){
		return new DrugListHolder();
	}
	
	public BasicArmPresentation getArmModel(int armNumber){
		return d_selectedArmList.get(armNumber);
	}
	
	public ValueModel getArmNoteModel(int curArmNumber) {
		if(d_oldStudyPM.getArms().size() <= curArmNumber)
			return null;
		return new StudyNoteHolder(getOldStudy(),getOldStudy().getArms().get(curArmNumber));
	}

	public TableModel getMeasurementTableModel() {
		commitOutcomesArmsToNew();
		return new MeasurementTableModel(getNewStudy(),d_pmf);
	}
	
	
	public void saveStudy() {
		if (d_selectedArmList.isEmpty()) 
			throw new IllegalStateException("No arms selected in study.");
		if (d_selectedEndpointsList.isEmpty()) 
			throw new IllegalStateException("No outcomes selected in study.");
		if (!checkID())
			throw new IllegalStateException("Study with this ID already exists in domain");
		
		// transfer the notes from the imported study to the new one.
		if (d_oldStudyPM != null)
			transferNotes();
		
		// Add the study to the domain.
		d_domain.addStudy(getNewStudy());
	}

	public boolean checkID() {
		if (d_domain.getStudies().contains(getNewStudy())) {
				return false;
		}
		return true;
	}
	
	Study getStudy() {
		return getNewStudy();
	}
	
	void commitOutcomesArmsToNew(){
		List<OutcomeMeasure> outcomeMeasures = new ArrayList<OutcomeMeasure>();
		for(AbstractHolder<OutcomeMeasure> outcomeHolder : d_selectedEndpointsList) {
			outcomeMeasures.add(outcomeHolder.getValue());
		}	
		getNewStudy().setOutcomeMeasures(outcomeMeasures);
		
		List<Arm> arms = new ArrayList<Arm>();
		for(BasicArmPresentation arm : d_selectedArmList) { 
			arms.add(arm.getBean());
		}
		
		getNewStudy().setArms(arms);
	}
	
	private void transferNotes() {
		for (Entry<Object,Note> noteEntry : getOldStudy().getNotes().entrySet()) {
			Object key = noteEntry.getKey();
			Note value = noteEntry.getValue();
			/* If notes are keyed by either Characteristic or Property (String), we can just copy them */
			if ((key instanceof BasicStudyCharacteristic) || (key instanceof String)) {
				getNewStudy().putNote(key,value);
			} else if (key instanceof OutcomeMeasure) {
				int outcomeIndex = getOldStudy().getOutcomeMeasures().indexOf(key);
				Object newKey =  getNewStudy().getOutcomeMeasures().get(outcomeIndex);
				getNewStudy().putNote(newKey,value);
			} else if (key instanceof Arm) {
				int armIndex = getOldStudy().getArms().indexOf(key);
				Object newKey =  getNewStudy().getArms().get(armIndex);
				getNewStudy().putNote(newKey,value);
			}
		}
	}

	private Study getNewStudy() {
		return d_newStudyPM.getBean();
	}

	private Study getOldStudy() {
		return d_oldStudyPM.getBean();
	}

	public int getNumberADEs() {
		return d_selectedADEsList.size();
	}

	public void addADEModels(int n) {
		for (int i = 0; i < n; ++i) {
			d_selectedADEsList.add(new AdverseEventHolder());
		}
	}

	public ListHolder<OutcomeMeasure> getADEListModel() {
		return d_adverseEventListHolder;
	}

	public void removeADE(int i) {
		d_selectedADEsList.remove(i);
	}

	public ValueModel getADEModel(int i) {
		return d_selectedADEsList.get(i);
	}
}
