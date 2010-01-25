package org.drugis.addis.presentation;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.MutableCharacteristicHolder;
import org.drugis.addis.imports.ClinicaltrialsImporter;

import com.jgoodies.binding.value.ValueModel;

public class AddStudyWizardPresentation {

	@SuppressWarnings("serial")
	private class GenericHolder<T> extends AbstractHolder<T> {

		@Override
		protected void cascade() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void checkArgument(Object newValue) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private Domain d_domain;
	private PresentationModelFactory d_pmf;
	private StudyPresentationModel d_newStudyPM;
	private StudyPresentationModel d_oldStudyPM;
	
	public AddStudyWizardPresentation(Domain d, PresentationModelFactory pmf) {
		d_domain = d;
		d_pmf = pmf;
		d_oldStudyPM = (StudyPresentationModel) pmf.getModel(new Study("", new Indication(0l,"")));
		d_newStudyPM = (StudyPresentationModel) pmf.getModel(new Study("", new Indication(0l,"")));
		getSourceModel().setValue(BasicStudyCharacteristic.Source.MANUAL);
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
		String url = "http://clinicaltrials.gov/show/"+getIdModel().getValue()+"?displayxml=true";
		try {
			ClinicaltrialsImporter.getClinicaltrialsData(d_oldStudyPM.getBean(),url);
			getSourceModel().setValue(BasicStudyCharacteristic.Source.CLINICALTRIALS);
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(frame, "Invalid NCT ID: "+ d_newStudyPM.getBean());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Couldn't find ID " + d_newStudyPM.getBean() + " on ClinicalTrials.gov");
		}
	}
	
}
