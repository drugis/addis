package org.drugis.addis.presentation;

import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.swing.JDialog;
import javax.swing.JTable;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.imports.ClinicaltrialsImporter;
import org.pietschy.wizard.InvalidStateException;

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
	private class EndpointListHolder extends AbstractListHolder<OutcomeMeasure> implements PropertyChangeListener, DomainListener {
		public EndpointListHolder() {
			d_domain.addListener(this);
		}
		
		@Override
		public List<OutcomeMeasure> getValue() {
			ArrayList<OutcomeMeasure> outcomeMeasures = new ArrayList<OutcomeMeasure>(getEndpoints());
			return outcomeMeasures;
		}
		
		public void propertyChange(PropertyChangeEvent event) {
			fireValueChange(null, getValue());
		}

		public void domainChanged(DomainEvent evt) {
			fireValueChange(null,getValue());
		}
	}
	
	@SuppressWarnings("serial")
	private class OutcomeMeasureHolder extends AbstractHolder<OutcomeMeasure> {

		public OutcomeMeasureHolder() {
			
		}
		
		@Override
		protected void checkArgument(Object newValue) {
			if (newValue != null)
				if (!getEndpoints().contains(newValue))
					throw new IllegalArgumentException("Endpoint not in the actual set!");
		}

		@Override
		protected void cascade() {
			/* If the endpoint that was selected is already selected somewhere else, reset the other selection */
			for (AbstractHolder<OutcomeMeasure> omHolder : d_selectedOutcomesList) {
				if ((!omHolder.equals(this)) && (omHolder.getValue() != null))
					if (omHolder.getValue().equals(getValue()))
						omHolder.setValue(null);
			}
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
	
	List<AbstractHolder<OutcomeMeasure>> d_selectedOutcomesList;
	List<BasicArmPresentation> d_selectedArmList;
	
	public AddStudyWizardPresentation(Domain d, PresentationModelFactory pmf) {
		d_domain = d;
		d_pmf = pmf;
		clearStudies();
	}
	
	public ValueModel getSourceModel() {
		return new MutableCharacteristicHolder(d_newStudyPM.getBean(), BasicStudyCharacteristic.SOURCE);
	}
	
	public ValueModel getSourceNoteModel() {
		return new StudyNoteHolder(d_oldStudyPM.getBean(), BasicStudyCharacteristic.SOURCE);
	}
	
	public ValueModel getIdModel() {
		return d_newStudyPM.getModel(Study.PROPERTY_ID);
	}
	
	public ValueModel getIdNoteModel() {
		return new StudyNoteHolder(d_oldStudyPM.getBean(), Study.PROPERTY_ID);
	}
	
	public ValueModel getTitleModel() {
		return new MutableCharacteristicHolder(d_newStudyPM.getBean(), BasicStudyCharacteristic.TITLE);
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
		d_newStudyPM.getBean().getCharacteristics().putAll(d_oldStudyPM.getBean().getCharacteristics());
		// Source
		getSourceModel().setValue(BasicStudyCharacteristic.Source.CLINICALTRIALS);
		// Id & Title
		getIdModel().setValue(studyID);
		getTitleModel().setValue(d_oldStudyPM.getBean().getCharacteristic(BasicStudyCharacteristic.TITLE));
		
		// Endpoints.
		d_selectedOutcomesList = new ArrayList<AbstractHolder<OutcomeMeasure>>();
		addEndpointModels(d_oldStudyPM.getBean().getOutcomeMeasures().size());
		
		// Arms & Dosage
		d_selectedArmList = new ArrayList<BasicArmPresentation>();
		addArmModels(d_oldStudyPM.getBean().getArms().size());

	}

	public ListHolder<Indication> getIndicationListModel() {
		return new IndicationListHolder();
	}
	
	public ValueModel getIndicationModel() {
		return d_newStudyPM.getModel(Study.PROPERTY_INDICATION);
	}

	public ValueModel getIndicationNoteModel() {
		return new StudyNoteHolder(d_oldStudyPM.getBean(), Study.PROPERTY_INDICATION);
	}
	
	public void clearStudies() {
		d_oldStudyPM = (StudyPresentationModel) new StudyPresentationModel(new Study("", new Indication(0l,"")),d_pmf);
		d_newStudyPM = (StudyPresentationModel) new StudyPresentationModel(new Study("", new Indication(0l,"")),d_pmf);
		getSourceModel().setValue(BasicStudyCharacteristic.Source.MANUAL);
		d_selectedOutcomesList = new ArrayList<AbstractHolder<OutcomeMeasure>>();
		d_selectedArmList = new ArrayList<BasicArmPresentation>();
		addEndpointModels(1);
		addArmModels(2);
		
	}
	
	
	public MutableCharacteristicHolder getCharacteristicModel(BasicStudyCharacteristic c) {
		return new MutableCharacteristicHolder(d_newStudyPM.getBean(),c);
	}

	public ValueModel getCharacteristicNoteModel(BasicStudyCharacteristic c) {
		return new StudyNoteHolder(d_oldStudyPM.getBean(), c);
	}

	private SortedSet<Endpoint> getEndpoints() {
		return d_domain.getEndpoints();
	}
	
	public ListHolder<OutcomeMeasure> getEndpointListModel() {
		return new EndpointListHolder();
	}

	public ValueModel getEndpointNoteModel(int i) {
		if(d_oldStudyPM.getEndpoints().size() <= i)
			return null;
		return new StudyNoteHolder(d_oldStudyPM.getBean(),new ArrayList<OutcomeMeasure>(d_oldStudyPM.getEndpoints()).get(i));
	}
	
	public int getNumberEndpoints() {
		return d_selectedOutcomesList.size();
	}
	
	public void addEndpointModels(int numEndpoints){
		for (int i=0; i<numEndpoints; ++i)
			d_selectedOutcomesList.add(new OutcomeMeasureHolder());
	}
	
	public ValueModel getEndpointModel(int i) {
		if (i >= d_selectedOutcomesList.size())
			throw new IndexOutOfBoundsException("no endpoint at index: "+i);
		
		return d_selectedOutcomesList.get(i);
	}

	public void removeEndpoint(int i) {
		d_selectedOutcomesList.remove(i);
		if ( d_oldStudyPM.getBean().getOutcomeMeasures().size() > i)
			d_oldStudyPM.getBean().getOutcomeMeasures().remove(d_oldStudyPM.getEndpoints().get(i));
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
		
		if( d_oldStudyPM.getBean().getArms().size() > armNum )
			d_oldStudyPM.getBean().getArms().remove(d_oldStudyPM.getBean().getArms().get(armNum));	
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
		return new StudyNoteHolder(d_oldStudyPM.getBean(),d_oldStudyPM.getBean().getArms().get(curArmNumber));
	}

	public JTable getMeasurementTableModel(JDialog frame) {
		commitOutcomesArmsToNew();
		return new MeasurementTable(d_newStudyPM.getBean(), d_pmf, (Window)frame);
	}
	
	
	private void commitOutcomesArmsToNew(){
		List<OutcomeMeasure> outcomeMeasures = new ArrayList<OutcomeMeasure>();
		for(AbstractHolder<OutcomeMeasure> outcomeHolder : d_selectedOutcomesList) {
			outcomeMeasures.add(outcomeHolder.getValue());
		}	
		d_newStudyPM.getBean().setOutcomeMeasures(outcomeMeasures);
		
		List<Arm> arms = new ArrayList<Arm>();
		for(BasicArmPresentation arm : d_selectedArmList) { 
			arms.add(arm.getBean());
		}
		d_newStudyPM.getBean().setArms(arms);
	}

	public void saveStudy() {
		if (d_selectedArmList.isEmpty()) 
			throw new IllegalStateException("No arms selected in study.");
		if (d_selectedOutcomesList.isEmpty()) 
			throw new IllegalStateException("No outcomes selected in study.");
		if (!checkID())
			throw new IllegalStateException("Study with this ID already exists in domain");
		
		// Add the study to the domain.
		d_domain.addStudy(d_newStudyPM.getBean());
	}

	public boolean checkID() {
		if (d_domain.getStudies().contains(d_newStudyPM.getBean())) {
				return false;
		}
		return true;
	}
	
	Study getStudy() {
		return d_newStudyPM.getBean();
	}
}
