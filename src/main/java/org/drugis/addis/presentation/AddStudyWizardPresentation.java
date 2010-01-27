package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

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

import com.jgoodies.binding.value.ValueModel;

public class AddStudyWizardPresentation {
	
	@SuppressWarnings("serial")
	private class DrugHolder extends AbstractHolder<Drug> {
		@Override
		protected void checkArgument(Object newValue) {
		}

		protected void cascade() {
		}
	}
	
	@SuppressWarnings("serial")
	private class DrugListHolder extends AbstractListHolder<Drug> implements PropertyChangeListener {
		public DrugListHolder() {
		}
		
		@Override
		public List<Drug> getValue() {
			return new ArrayList<Drug>(d_domain.getDrugs());
		}

		public void propertyChange(PropertyChangeEvent evt) {
			fireValueChange(null, getValue());
		}
	}
	
	@SuppressWarnings("serial")
	private class OutcomeListHolder extends AbstractListHolder<OutcomeMeasure> implements PropertyChangeListener {
		public OutcomeListHolder() {
			getIndicationModel().addValueChangeListener(this);
		}
		
		@Override
		public List<OutcomeMeasure> getValue() {
			return new ArrayList<OutcomeMeasure>(getEndpoints());
		}

		public void propertyChange(PropertyChangeEvent event) {
			fireValueChange(null, getValue());
		}
	}
	
	@SuppressWarnings("serial")
	private class OutcomeMeasureHolder extends AbstractHolder<OutcomeMeasure> {
		@Override
		protected void checkArgument(Object newValue) {
			if (newValue != null)
				if (!getEndpoints().contains(newValue))
					throw new IllegalArgumentException("Endpoint not in the actual set!");
		}

		@Override
		protected void cascade() {
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
	//List<Arm> d_armHoldersList;
	List<DrugHolder> d_selectedDrugMap;
	
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
	
	public ValueModel getTitleNoteModel() {
		return new StudyNoteHolder(d_oldStudyPM.getBean(), BasicStudyCharacteristic.TITLE);
	}

	public void importCT(JPanel frame) {
		Object studyID = getIdModel().getValue();
		String url = "http://clinicaltrials.gov/show/"+studyID+"?displayxml=true";
		try {
			d_oldStudyPM = (StudyPresentationModel) d_pmf.getModel(ClinicaltrialsImporter.getClinicaltrialsData(url));
			d_newStudyPM = (StudyPresentationModel) d_pmf.getModel(new Study("", new Indication(0l,"")));
			migrateImportToNew(studyID);
			
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(frame, "Invalid NCT ID: "+ d_newStudyPM.getBean());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Couldn't find ID " + d_newStudyPM.getBean() + " on ClinicalTrials.gov");
		}

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
		
		// Arms.
		d_selectedDrugMap = new ArrayList<DrugHolder>();
		addArms(d_oldStudyPM.getBean().getArms().size());
		
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
		d_oldStudyPM = (StudyPresentationModel) d_pmf.getModel(new Study("", new Indication(0l,"")));
		d_newStudyPM = (StudyPresentationModel) d_pmf.getModel(new Study("", new Indication(0l,"")));
		getSourceModel().setValue(BasicStudyCharacteristic.Source.MANUAL);
		d_selectedOutcomesList = new ArrayList<AbstractHolder<OutcomeMeasure>>();
		addEndpointModels(1);
		
		d_selectedDrugMap = new ArrayList<DrugHolder>();
	}
	
	
	public ValueModel getCharacteristicModel(BasicStudyCharacteristic c) {
		return new MutableCharacteristicHolder(d_newStudyPM.getBean(),c);
	}
	
	public ValueModel getCharacteristicModelAsString(BasicStudyCharacteristic c) {
		return new MutableIntegerCharacteristicHolder(d_newStudyPM.getBean(),c);
	}

	public ValueModel getCharacteristicNoteModel(BasicStudyCharacteristic c) {
		return new StudyNoteHolder(d_oldStudyPM.getBean(), c);
	}

	private SortedSet<Endpoint> getEndpoints() {
		return d_domain.getEndpoints();
	}
	
	public OutcomeListHolder getOutcomeListModel() {
		return new OutcomeListHolder();
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
			d_oldStudyPM.getBean().getOutcomeMeasures().remove(new ArrayList<OutcomeMeasure>(d_oldStudyPM.getEndpoints()).get(i));
	}

	
	private void addArms(int numArms) {
		for(int i = 0; i<numArms; ++i)
			d_selectedDrugMap.add(new DrugHolder());
	}
	
	public int getArms(){
		return d_selectedDrugMap.size();
	}
	
	public DrugListHolder getDrugsModel(){
		return new DrugListHolder();
	}
	
	public DrugHolder getDrugModel(int armNumber){
		return d_selectedDrugMap.get(armNumber);
	}
	
	/*
	public List<AbstractHolder<Arm>> getArmModels() {
		return d_armHoldersList;
	}*/
}
