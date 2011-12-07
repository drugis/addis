/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.components.AddisScrollPane;
import org.drugis.addis.presentation.AbstractBenefitRiskPresentation;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.wizard.BenefitRiskWizardPM;
import org.drugis.addis.presentation.wizard.CriteriaAndAlternativesPresentation;
import org.drugis.addis.presentation.wizard.MetaCriteriaAndAlternativesPresentation;
import org.drugis.addis.presentation.wizard.StudyCriteriaAndAlternativesPresentation;
import org.drugis.addis.presentation.wizard.BenefitRiskWizardPM.BRAType;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.Condition;
import org.pietschy.wizard.models.DynamicModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class BenefitRiskWizard extends Wizard {
	private static final long serialVersionUID = -8142319787888932L;

	private static final Dimension PREFERRED_COLUMN_SIZE = new Dimension(330, 370);

	public BenefitRiskWizard(AddisWindow mainWindow, BenefitRiskWizardPM pm) {
		super(buildModel(pm, mainWindow));
		
		getTitleComponent().setPreferredSize(new Dimension(700 , 100));
		setPreferredSize(new Dimension(700, 550));
		setMinimumSize(new Dimension(700, 550));
		
		setDefaultExitMode(Wizard.EXIT_ON_FINISH);
	}

	private static WizardModel buildModel(final BenefitRiskWizardPM pm, AddisWindow mainWindow) {
		DynamicModel wizardModel = new DynamicModel();
		wizardModel.add(new SelectIndicationWizardStep(pm));
		wizardModel.add(new DescriptivesStep(pm), new Condition() {
			public boolean evaluate(WizardModel model) {
				return pm.getIncludeDescriptivesModel().getValue();
			}
		});
		wizardModel.add(new SelectStudyWizardStep(pm.getStudyBRPresentation(), mainWindow), new Condition() {
			public boolean evaluate(WizardModel model) {
				return pm.getEvidenceTypeHolder().getValue() == BRAType.SingleStudy;
			}
		});
		wizardModel.add(new SelectOutcomeMeasuresAndArmsWizardStep(pm, pm.getStudyBRPresentation(), mainWindow), new Condition() {
			public boolean evaluate(WizardModel model) {
				return pm.getEvidenceTypeHolder().getValue() == BRAType.SingleStudy;
			}
		});
		wizardModel.add(new SelectCriteriaAndAlternativesWizardStep((BenefitRiskWizardPM)pm, mainWindow), new Condition() {
			public boolean evaluate(WizardModel model) {
				return pm.getEvidenceTypeHolder().getValue() == BRAType.Synthesis;
			}
		});
		
		return wizardModel;
		
		
	}


	private static <Alternative extends Comparable<Alternative> & Entity> Component buildAlternativesPanel(FormLayout layout, CriteriaAndAlternativesPresentation<Alternative> critAltPM) {
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		JLabel alternativesLabel = new JLabel("Alternatives");
		alternativesLabel.setFont(alternativesLabel.getFont().deriveFont(Font.BOLD));
		builder.add(alternativesLabel, cc.xy(1, 1));
		
		int row = 1;
		for(final Alternative a : critAltPM.getAlternativesListModel()){
			LayoutUtil.addRow(layout);

			final ValueHolder<Boolean> selectedModel = critAltPM.getAlternativeSelectedModel(a);
			final ValueHolder<Boolean> enabledModel  = critAltPM.getAlternativeEnabledModel(a);
			
			JCheckBox armCheckbox = AuxComponentFactory.createDynamicEnabledBoundCheckbox(a.getLabel(), enabledModel, selectedModel);				
			builder.add(armCheckbox, cc.xy(1, row += 2));
		}

		row = LayoutUtil.addRow(layout, row, "10dlu");
		builder.add(new JLabel("Baseline:"), cc.xy(1, row += 2));
		ValueModel model = critAltPM.getBaselineModel();
		builder.add(AuxComponentFactory.createBoundComboBox(critAltPM.getSelectedAlternatives(), model, true), cc.xy(1, row += 2));

		return AuxComponentFactory.createInScrollPane(builder, PREFERRED_COLUMN_SIZE);
	}	
	

	private static <Alternative extends Comparable<Alternative>> int addCriterionCheckbox(OutcomeMeasure out,
			CriteriaAndAlternativesPresentation<Alternative> critAltPM, FormLayout layout, PanelBuilder builder,
			CellConstraints cc, int row) {
		// Add outcome measure checkbox
		row = LayoutUtil.addRow(layout, row);
		
		builder.add(AuxComponentFactory.createDynamicEnabledBoundCheckbox(out.getName(), 
				critAltPM.getCriterionEnabledModel(out), 
				critAltPM.getCriterionSelectedModel(out)), cc.xyw(1, row, 3));
		return row;
	}

	private static class DescriptivesStep extends PanelWizardStep {
		private static final long serialVersionUID = 5441828903910494369L;

		public DescriptivesStep(BenefitRiskWizardPM pm) {
			super("BRAT descriptives", 
					"Define the decision context according to the BRAT framework");
			setComplete(true);
			
			FormLayout layout = new FormLayout(
					"right:pref, 3dlu, fill:0:grow",
					"p"
			);
	
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			builder.setDefaultDialogBorder();
			
			int row = 1;
			
			for (AbstractBenefitRiskPresentation.DecisionContextField field : pm.getDecisionContextFields()) {
				builder.addLabel(field.getName() + ": ", cc.xy(1, row));
				builder.add(AuxComponentFactory.createTextArea(field.getModel(), true), cc.xy(3, row));
				row = LayoutUtil.addRow(layout, row);
				builder.add(AuxComponentFactory.createHtmlField(field.getHelpText()), cc.xy(3, row));
				row = LayoutUtil.addRow(layout, row, "7dlu");
			}
			
			this.setLayout(new BorderLayout());
			JScrollPane scrollPane = new AddisScrollPane(builder.getPanel());
		
			add(scrollPane, BorderLayout.CENTER);
		}
	}
	
	private static class SelectIndicationWizardStep extends PanelWizardStep {
		private static final long serialVersionUID = 2986876155242979527L;

		public SelectIndicationWizardStep(BenefitRiskWizardPM pm) {
			super("Select indication",
					"Select the indication, evidence type and analysis type that you want to use for this benefit-risk analysis.");

			FormLayout layout = new FormLayout(
					"right:pref, 3dlu, left:pref",
					"p, 7dlu, p, 7dlu, p, 7dlu, p"
			);	
	
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			int row = 1;
			JComboBox indBox = AuxComponentFactory.createBoundComboBox(pm.getIndicationsModel(), pm.getIndicationModel(), true);
			builder.add(new JLabel("Indication : "), cc.xy(row, row));
			builder.add(indBox, cc.xy(3, row));
			
			pm.getIndicationModel().addValueChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					setComplete(evt.getNewValue() != null);
				}
			});

			row += 2;
			builder.add(BasicComponentFactory.createCheckBox(pm.getIncludeDescriptivesModel(), "Include BRAT decision context definition"), cc.xyw(1, row, 3));
			
			row += 2;
			builder.add(new JLabel("Study type : "), cc.xy(1, row));
			JPanel studyTypeRadioButtonPanel = new JPanel();
			studyTypeRadioButtonPanel.setLayout(new BoxLayout(studyTypeRadioButtonPanel,BoxLayout.Y_AXIS));
			JRadioButton MetaAnalysisButton = BasicComponentFactory.createRadioButton(pm.getEvidenceTypeHolder(), BRAType.Synthesis, "Evidence synthesis");
			JRadioButton StudyButton = BasicComponentFactory.createRadioButton(pm.getEvidenceTypeHolder(), BRAType.SingleStudy, "Single study");
			studyTypeRadioButtonPanel.add(MetaAnalysisButton);
		    studyTypeRadioButtonPanel.add(StudyButton);
		    builder.add(studyTypeRadioButtonPanel, cc.xy(3, row));
		    
			row += 2;
			builder.add(new JLabel("Analysis type : "), cc.xy(1, row));
			JPanel analysisTypeRadioButtonPanel = new JPanel();
			analysisTypeRadioButtonPanel.setLayout(new BoxLayout(analysisTypeRadioButtonPanel,BoxLayout.Y_AXIS));
			JRadioButton SMAAButton = BasicComponentFactory.createRadioButton(pm.getAnalysisTypeHolder(), AnalysisType.SMAA, "SMAA");
			JRadioButton LyndOBrienButton = BasicComponentFactory.createRadioButton(pm.getAnalysisTypeHolder(), AnalysisType.LyndOBrien, "Lynd & O'Brien");
			analysisTypeRadioButtonPanel.add(SMAAButton);
		    analysisTypeRadioButtonPanel.add(LyndOBrienButton);
		    builder.add(analysisTypeRadioButtonPanel, cc.xy(3, row));
		    
			add(builder.getPanel());
		}
	}

	private static class SelectStudyWizardStep extends PanelWizardStep {
		private static final long serialVersionUID = -6351673911830174693L;

		public SelectStudyWizardStep(final StudyCriteriaAndAlternativesPresentation pm, AddisWindow mainWindow){
			super("Select Study","In this step you select which study you use as a basis for your analysis.");
			add(new JLabel("Study : "));

			JComboBox studyBox = AuxComponentFactory.createBoundComboBox(pm.getStudiesWithIndication(), pm.getStudyModel(), true);
			add(studyBox);
			pm.getStudyModel().addValueChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					setComplete(evt.getNewValue() != null);
				}
			});
		}
	}

	private static class SelectOutcomeMeasuresAndArmsWizardStep extends PanelWizardStep {
		private static final long serialVersionUID = -6712176504045317313L;
		private AddisWindow d_main;
		private StudyCriteriaAndAlternativesPresentation d_studyPM;
		private final BenefitRiskWizardPM d_pm;
	
		public SelectOutcomeMeasuresAndArmsWizardStep(BenefitRiskWizardPM pm, StudyCriteriaAndAlternativesPresentation studyCriteriaAndAlternativesPresentation, AddisWindow main) {
			super("Select OutcomeMeasures and Arms","In this step you select the criteria (specific outcomemeasures) " +
					"and the alternatives (drugs) to include in the benefit-risk analysis. To perform the analysis, at least " +
					"two criteria and at least two alternatives must be included.");
			d_pm = pm;
			d_main = main;
			d_studyPM = studyCriteriaAndAlternativesPresentation;	
		}
		
		@Override
		public void prepare() {
			removeAll();
			add(buildPanel());
			Bindings.bind(this, "complete", d_pm.getCompleteModel());
		}
		
		@Override
		public void applyState() throws InvalidStateException {
			saveAsAnalysis();
		}
		
		private void saveAsAnalysis() throws InvalidStateException {
			String res = JOptionPane.showInputDialog(this.getTopLevelAncestor(),
					"Input name for new analysis", 
					"Save analysis", JOptionPane.QUESTION_MESSAGE);
			if (res != null) {
				try {
					d_main.leftTreeFocus(d_studyPM.saveAnalysis(d_main.getDomain(), res, d_pm.getDecisionContext()));
				} catch (EntityIdExistsException e) {
					JOptionPane.showMessageDialog(this.getTopLevelAncestor(), 
							"There already exists an analysis with the given name, input another name",
							"Unable to save analysis", JOptionPane.ERROR_MESSAGE);
					saveAsAnalysis();
				}
			} else {
				throw new InvalidStateException();
			}
		}
		private JPanel buildPanel() {
			FormLayout layout = new FormLayout(
					"left:pref, 3dlu, left:pref",
					"p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			builder.add(buildOutcomeMeasuresPane(), cc.xy(1, 1));
			builder.add(buildAlternativesPane(), cc.xy(3, 1));
			
			return builder.getPanel();
		}

		private Component buildOutcomeMeasuresPane() {
			FormLayout layout = new FormLayout(
					"left:pref:grow, 3dlu, left:pref",
					"p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			JLabel outcomeMeasuresLabel = new JLabel("Criteria");
			outcomeMeasuresLabel.setFont(outcomeMeasuresLabel.getFont().deriveFont(Font.BOLD));
			builder.add(outcomeMeasuresLabel, cc.xyw(1, 1, 3));
			int row = 1;
			for (OutcomeMeasure out : d_studyPM.getCriteriaListModel()) {
				// Add outcome measure checkbox
				row = addCriterionCheckbox(out, d_studyPM, layout, builder, cc, row);
			}
			
			return AuxComponentFactory.createInScrollPane(builder, PREFERRED_COLUMN_SIZE);
		}

		private Component buildAlternativesPane() {
			FormLayout layout = new FormLayout(
					"left:pref, 3dlu, left:pref",
					"p, 3dlu, p, 3dlu, p"
					);	

			return buildAlternativesPanel(layout, d_studyPM);
		}
	
	}

	public static class SelectCriteriaAndAlternativesWizardStep extends PanelWizardStep {
		private static final long serialVersionUID = -7893619121154004229L;

		private AddisWindow d_mainWindow;
		private BenefitRiskWizardPM d_pm;
		private MetaCriteriaAndAlternativesPresentation d_metaPM;

		public SelectCriteriaAndAlternativesWizardStep(BenefitRiskWizardPM pm, AddisWindow main) {
			super("Select Criteria and Alternatives","In this step, you select the criteria (analyses on specific outcomemeasures) " +
				  "and the alternatives (drugs) to include in the benefit-risk analysis. To perform the analysis, at least two criteria " +
				  "and at least two alternatives must be included.");
			d_mainWindow = main;
			d_pm = pm;
			d_metaPM = d_pm.getMetaBRPresentation(); 
		}

		@Override
		public void prepare() {
			this.removeAll();
			add(buildPanel());
			Bindings.bind(this, "complete", d_pm.getCompleteModel());
		}
		
		@Override
		public void applyState() throws InvalidStateException {
			saveAsAnalysis();
		}

		private void saveAsAnalysis() throws InvalidStateException {
			String res = JOptionPane.showInputDialog(this.getTopLevelAncestor(),
					"Input name for new analysis", 
					"Save analysis", JOptionPane.QUESTION_MESSAGE);
			if (res != null) {
				try {
					d_mainWindow.leftTreeFocus(d_metaPM.saveAnalysis(d_mainWindow.getDomain(), res, d_pm.getDecisionContext()));
				} catch (EntityIdExistsException e) {
					JOptionPane.showMessageDialog(this.getTopLevelAncestor(), 
							"There already exists an analysis with the given name, input another name",
							"Unable to save analysis", JOptionPane.ERROR_MESSAGE);
					saveAsAnalysis();
				}
			} else {
				throw new InvalidStateException();
			}
		}

		private JComponent buildPanel() {
			GridLayout layout = new GridLayout(1, 2, 15, 0);
			
			JPanel panel = new JPanel(layout);
			
			panel.add(buildCriteriaPane());
			panel.add(buildAlternativesPane());
			
			return panel;
		}

		private Component buildCriteriaPane() {
			FormLayout layout = new FormLayout(
					"left:pref, 3dlu, left:pref",
					"p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			JLabel criteriaLabel = new JLabel("Criteria");
			criteriaLabel.setFont(
				criteriaLabel.getFont().deriveFont(Font.BOLD));
			builder.add(criteriaLabel, cc.xy(1, 1));
			
			int row = 1;
			for(OutcomeMeasure out : d_metaPM.getCriteriaListModel()){
				if(d_metaPM.getMetaAnalyses(out).isEmpty())
					continue;

				row = addCriterionCheckbox(out, d_metaPM, layout, builder, cc, row);
				
				// Add radio-button panel
				row = LayoutUtil.addRow(layout, row);
				builder.add(buildRadioButtonAnalysisPanel(out), cc.xy(3, row, CellConstraints.LEFT, CellConstraints.DEFAULT));
			}
			
			return AuxComponentFactory.createInScrollPane(builder, PREFERRED_COLUMN_SIZE);
		}

		private JPanel buildRadioButtonAnalysisPanel(OutcomeMeasure out) {
			// create the panel
			JPanel radioButtonPanel = new JPanel();
			radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel,BoxLayout.Y_AXIS));
			
			// Retrieve the valueModel to see whether we should enable the radio-buttons.
			ValueHolder<Boolean> enabledModel = d_metaPM.getCriterionSelectedModel(out);
			
			// Add the radio buttons
			for(MetaAnalysis ma : d_metaPM.getMetaAnalyses(out)){
				ValueHolder<MetaAnalysis> selectedModel = d_metaPM.getMetaAnalysesSelectedModel(out);
				JRadioButton radioButton = AuxComponentFactory.createDynamicEnabledRadioButton(ma.getName(), ma, selectedModel, enabledModel);
				radioButtonPanel.add(radioButton);
			}
			return radioButtonPanel;
		}

		private Component buildAlternativesPane() {
			FormLayout layout = new FormLayout(
					"left:pref",
					"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
					);	
			
			return buildAlternativesPanel(layout, d_metaPM);
		}
	}
}
