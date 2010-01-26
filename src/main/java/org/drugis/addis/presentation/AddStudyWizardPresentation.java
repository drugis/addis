package org.drugis.addis.presentation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.imports.ClinicaltrialsImporter;

import com.jgoodies.binding.value.ValueModel;

public class AddStudyWizardPresentation {
	
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
		d_newStudyPM.getBean().getCharacteristics().putAll(d_oldStudyPM.getBean().getCharacteristics());
		getSourceModel().setValue(BasicStudyCharacteristic.Source.CLINICALTRIALS);
		getIdModel().setValue(studyID);
		getTitleModel().setValue(d_oldStudyPM.getBean().getCharacteristic(BasicStudyCharacteristic.TITLE));
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
	
}
