package org.drugis.addis.gui.builder;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.presentation.AddStudyWizardPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddStudyWizard implements ViewBuilder{
	
	AddStudyWizardPresentation d_pm;
	
	public AddStudyWizard(AddStudyWizardPresentation pm) {
		d_pm = pm;
	}
	
	public Wizard buildPanel() {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIdTitleWizardStep());
		wizardModel.add(new SelectIndicationWizardStep());
		//wizardModel.add(new SelectDrugsWizardStep());
		//wizardModel.add(new SelectArmsWizardStep());
		//wizardModel.add(new OverviewWizardStep());
		Wizard wizard = new Wizard(wizardModel);
		wizard.setDefaultExitMode(Wizard.EXIT_ON_FINISH);
		wizard.setPreferredSize(new Dimension(950, 650));
		return wizard;
	}
	
	@SuppressWarnings("serial")
	public class SelectIndicationWizardStep extends PanelWizardStep {
		public SelectIndicationWizardStep () {
			super("Select Indications", "Select the endpoints for this study");
			FormLayout layout = new FormLayout(
					"center:pref, 3dlu, center:pref, 3dlu, center:pref",
					"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
					);	
		}
	}
	
	@SuppressWarnings("serial")
	public class SelectIdTitleWizardStep extends PanelWizardStep {
		JPanel d_me = this;
		private JTextField d_idField;
		private JTextField d_titleField;
		
		 public SelectIdTitleWizardStep() {
			super("Select ID and Title","Set the ID and title of the study. Studies can also be extracted from Clinicaltrials.gov using the NCT-id.");
			FormLayout layout = new FormLayout(
					"center:pref, 3dlu, center:pref, 3dlu, center:pref",
					"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
					);	
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			builder.addLabel("Source",cc.xy(1, 3));	
			JComponent sourceSelecter = AuxComponentFactory.createBoundComboBox(BasicStudyCharacteristic.Source.values(), d_pm.getSourceModel());
			sourceSelecter.setEnabled(false);
			builder.add(sourceSelecter, cc.xyw(3, 3, 3));
			
			builder.addLabel("ID",cc.xy(1, 5));
			d_idField = BasicComponentFactory.createTextField(d_pm.getIdModel(), false);
			d_idField.setColumns(30);
			builder.add(d_idField, cc.xy(3, 5));
			d_idField.addCaretListener(new CompleteListener());
			
			JButton btn = GUIFactory.createPlusButton("enter NCT id to retrieve study data from ClinicalTrials.gov");
			btn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					d_pm.importCT(d_me);
				}
			});
			builder.add(btn, cc.xy(5, 5));	
			
			JTextField idNote = BasicComponentFactory.createTextField(d_pm.getIdNoteModel(), true);
			idNote.setColumns(30);
			builder.add(idNote, cc.xy(3, 7));
			idNote.setEnabled(false);
			
			builder.addLabel("Title",cc.xy(1, 9));
			d_titleField = BasicComponentFactory.createTextField(d_pm.getTitleModel(), false);
			d_titleField.setColumns(30);
			builder.add(d_titleField, cc.xy(3, 9));
			d_titleField.addCaretListener(new CompleteListener());
			
			JTextField titleNote = BasicComponentFactory.createTextField(d_pm.getTitleNoteModel(), true);
			titleNote.setColumns(30);
			builder.add(titleNote, cc.xy(3, 11));
			titleNote.setEnabled(false);		
			
			add(builder.getPanel());

			//CompleteListener completeListener = new CompleteListener();	
			//d_pm.getIdModel().addValueChangeListener(completeListener);
			//d_pm.getTitleModel().addValueChangeListener(completeListener);
			
			//title
			
		 }
		 
		 private class CompleteListener implements CaretListener{

			public void caretUpdate(CaretEvent arg0) {
				//if (d_pm.getIdModel().getValue() != null && d_pm.getTitleModel().getValue() != null) {
				//setComplete( (!d_pm.getIdModel().getValue().equals("")) && 
			    //             (!d_pm.getTitleModel().getValue().equals("")   ));
				setComplete( (!d_idField.getText().equals("")) && 
							 (!d_titleField.getText().equals("")   ));
				//}
				//else
				//	setComplete(false);
				
			}
		 }
	}	
}
