package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.table.TableModel;

import org.drugis.addis.entities.AdverseEvent;
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
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.Main;
import org.drugis.addis.imports.ClinicaltrialsImporter;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.BasicArmPresentation;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.MeasurementTableModel;
import org.drugis.addis.presentation.MutableCharacteristicHolder;
import org.drugis.addis.presentation.PopulationCharTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectAdverseEventsPresentation;
import org.drugis.addis.presentation.SelectFromFiniteListPresentationModel;
import org.drugis.addis.presentation.SelectPopulationCharsPresentation;
import org.drugis.addis.presentation.StudyNoteHolder;
import org.drugis.addis.presentation.StudyPresentationModel;
import org.drugis.addis.presentation.ModifiableHolder;

import com.jgoodies.binding.value.ValueModel;

public class AddStudyWizardPresentation {
	
	public interface OutcomeMeasurementsModel {
		TableModel getMeasurementTableModel();
	}
	
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
	private abstract static class OutcomeListHolder<T extends OutcomeMeasure>
	extends AbstractListHolder<T> 
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
	private static class EndpointListHolder extends OutcomeListHolder<Endpoint> {
		public EndpointListHolder(Domain domain) {
			super(domain);
		}
		
		@Override
		public List<Endpoint> getValue() {
			return new ArrayList<Endpoint>(d_domain.getEndpoints());
		}
	}

	@SuppressWarnings("serial")
	private static class AdverseEventListHolder extends OutcomeListHolder<AdverseEvent> {
		public AdverseEventListHolder(Domain domain) {
			super(domain);
		}
		
		@Override
		public List<AdverseEvent> getValue() {
			return new ArrayList<AdverseEvent>(d_domain.getAdverseEvents());
		}
	}
	
	@SuppressWarnings("serial")
	private static class OutcomeMeasureHolder<T extends OutcomeMeasure> extends ModifiableHolder<T> {
		private List<ModifiableHolder<T>> d_selectionModelList;
		
		/**
		 * OutcomeMeasureHolder that validates it's values against a list of outcomes and
		 * that makes sure each outcome is selected only once.
		 * @param modelList
		 */
		public OutcomeMeasureHolder(List<ModifiableHolder<T>> modelList) {
			d_selectionModelList = modelList;
		}
		
		@Override
		public void setValue(Object newValue) {
			super.setValue(newValue);
			// If the outcome that was selected is already selected somewhere else, reset the other selection
			for (ModifiableHolder<T> omHolder : d_selectionModelList) {
				if ((!omHolder.equals(this)) && (omHolder.getValue() != null))
					if (omHolder.getValue().equals(getValue()))
						omHolder.setValue(null);
			}
		}
	}
	
	@SuppressWarnings("serial")
	private class EndpointHolder extends OutcomeMeasureHolder<Endpoint> {
		public EndpointHolder() {
			super(d_selectedEndpointsList);
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
	
	List<ModifiableHolder<Endpoint>> d_selectedEndpointsList;
	List<BasicArmPresentation> d_selectedArmList;
	private ListHolder<Endpoint> d_endpointListHolder;
	private ListHolder<AdverseEvent> d_adverseEventListHolder;
	private ListHolder<PopulationCharacteristic> d_populationCharsListHolder;
	private SelectAdverseEventsPresentation d_adverseEventSelect;
	private SelectFromFiniteListPresentationModel<PopulationCharacteristic> d_populationCharSelect;
	
	public AddStudyWizardPresentation(Domain d, PresentationModelFactory pmf, Main main) {
		d_domain = d;
		d_pmf = pmf;
		d_endpointListHolder = new EndpointListHolder(d_domain);
		d_adverseEventListHolder = new AdverseEventListHolder(d_domain);
		d_populationCharsListHolder = d_domain.getVariablesHolder();
		d_adverseEventSelect = new SelectAdverseEventsPresentation(d_adverseEventListHolder, main);
		d_populationCharSelect = new SelectPopulationCharsPresentation(d_populationCharsListHolder, main);
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
	
	public Domain getDomain() {
		return d_domain;
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
		getSourceModel().setValue(Source.CLINICALTRIALS);
		// Id & Title
		getIdModel().setValue(getOldStudy().getStudyId());
		getTitleModel().setValue(getOldStudy().getCharacteristic(BasicStudyCharacteristic.TITLE));
		
		// Endpoints.
		d_selectedEndpointsList = new ArrayList<ModifiableHolder<Endpoint>>();
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
		getSourceModel().setValue(Source.MANUAL);
		d_selectedEndpointsList = new ArrayList<ModifiableHolder<Endpoint>>();
		while (d_adverseEventSelect.countSlots() > 0) {
			d_adverseEventSelect.removeSlot(0);
		}
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
	
	public ListHolder<Endpoint> getEndpointListModel() {
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
		if ( getOldStudy().getEndpoints().size() > i)
			getOldStudy().removeEndpoint(i);
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

	public MeasurementTableModel getEndpointMeasurementTableModel() {
		commitOutcomesArmsToNew();
		return new MeasurementTableModel(getNewStudy(),d_pmf, Endpoint.class);
	}
	
	
	public Study saveStudy() {
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
		Study study = getNewStudy();
		d_domain.addStudy(study);
		return study;
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
		List<Endpoint> outcomeMeasures = new ArrayList<Endpoint>();
		for(ModifiableHolder<Endpoint> outcomeHolder : d_selectedEndpointsList) {
			outcomeMeasures.add(outcomeHolder.getValue());
		}	
		getNewStudy().setEndpoints(outcomeMeasures);
		
		List<Arm> arms = new ArrayList<Arm>();
		for(BasicArmPresentation arm : d_selectedArmList) { 
			arms.add(arm.getBean());
		}
		
		getNewStudy().setArms(arms);
	}
	
	private void commitAdverseEventsToStudy() {
		List<AdverseEvent> outcomeMeasures = new ArrayList<AdverseEvent>();
		for(ModifiableHolder<AdverseEvent> outcomeHolder : d_adverseEventSelect.getSlots()) {
			outcomeMeasures.add(outcomeHolder.getValue());
		}	
		getNewStudy().setAdverseEvents(outcomeMeasures);
	}
	
	private void commitPopulationCharsToStudy() {
		List<PopulationCharacteristic> outcomeMeasures = new ArrayList<PopulationCharacteristic>();
		for(ModifiableHolder<PopulationCharacteristic> outcomeHolder : d_populationCharSelect.getSlots()) {
			outcomeMeasures.add(outcomeHolder.getValue());
		}	
		getNewStudy().setPopulationCharacteristics(outcomeMeasures);
	}
	
	private void transferNotes() {
		for (Entry<Object,Note> noteEntry : getOldStudy().getNotes().entrySet()) {
			Object key = noteEntry.getKey();
			Note value = noteEntry.getValue();
			/* If notes are keyed by either Characteristic or Property (String), we can just copy them */
			if ((key instanceof BasicStudyCharacteristic) || (key instanceof String)) {
				getNewStudy().putNote(key,value);
			} else if (key instanceof Endpoint) {
				int outcomeIndex = getOldStudy().getEndpoints().indexOf(key);
				if (outcomeIndex > -1) {
					Object newKey =  getNewStudy().getEndpoints().get(outcomeIndex);
					getNewStudy().putNote(newKey,value);
				}
			} else if (key instanceof Arm) {
				int armIndex = getOldStudy().getArms().indexOf(key);
				if (armIndex > -1) {
					Object newKey =  getNewStudy().getArms().get(armIndex);
					getNewStudy().putNote(newKey,value);
				}
			}
		}
	}

	private Study getNewStudy() {
		return d_newStudyPM.getBean();
	}
	
	public StudyPresentationModel getNewStudyPM() {
		return d_newStudyPM;
	}

	private Study getOldStudy() {
		return d_oldStudyPM.getBean();
	}

	private MeasurementTableModel getAdverseEventMeasurementTableModel() {
		commitAdverseEventsToStudy();
		return new MeasurementTableModel(getNewStudy(),d_pmf, AdverseEvent.class);
	}
	
	private PopulationCharTableModel getPopulationCharMeasurementTableModel() {
		commitPopulationCharsToStudy();
		return d_newStudyPM.getPopulationCharTableModel();
	}

	public OutcomeMeasurementsModel getAdverseEventsModel() {
		return new OutcomeMeasurementsModel() {
			public MeasurementTableModel getMeasurementTableModel() {
				return getAdverseEventMeasurementTableModel();
			}
		};
	} 
	
	public OutcomeMeasurementsModel getEndpointsModel() {
		return new OutcomeMeasurementsModel() {
			public MeasurementTableModel getMeasurementTableModel() {
				return getEndpointMeasurementTableModel();
			}
		};
	}
	
	public OutcomeMeasurementsModel getPopulationCharsModel() {
		return new OutcomeMeasurementsModel() {
			public TableModel getMeasurementTableModel() {
				return getPopulationCharMeasurementTableModel();
			}
		};
	} 

	public SelectFromFiniteListPresentationModel<AdverseEvent> getAdverseEventSelectModel() {
		return d_adverseEventSelect;
	}

	public SelectFromFiniteListPresentationModel<PopulationCharacteristic> getPopulationCharSelectModel() {
		return d_populationCharSelect;
	} 
}
