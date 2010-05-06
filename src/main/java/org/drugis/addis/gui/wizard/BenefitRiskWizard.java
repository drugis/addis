package org.drugis.addis.gui.wizard;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.wizard.BenefitRiskWizardPM;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.DynamicModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class BenefitRiskWizard extends Wizard {
	
	public BenefitRiskWizard(Main parent, BenefitRiskWizardPM pm) {
		super(buildModel(pm, parent));
		getTitleComponent().setPreferredSize(new Dimension(750, 100));
		
		
		setPreferredSize(new Dimension(750, 750));
	}

	private static WizardModel buildModel(BenefitRiskWizardPM pm, Main frame) {
		DynamicModel wizardModel = new DynamicModel();
		wizardModel.add(new SelectIndicationWizardStep(pm));
		wizardModel.add(new SelectCriteriaAndAlternativesWizardStep(pm));
		
		return wizardModel;
	}
	
	private static class SelectCriteriaAndAlternativesWizardStep extends PanelWizardStep {
		
		private BenefitRiskWizardPM d_pm;

		public SelectCriteriaAndAlternativesWizardStep(BenefitRiskWizardPM pm){
			super("Select Criteria and Alternatives","In this step, you select the criteria (analyses on specific outcomemeasures) and the alternatives (drugs) to include in the benefit-risk analysis. To perform the analysis, at least two criteria and at least two alternatives must be included.");
			d_pm = pm;
		}

		@Override
		public void prepare() {
			this.removeAll();
			add(buildPanel());
			Bindings.bind(this, "complete", d_pm.getCompleteModel());
		}

		private JPanel buildPanel() {
			FormLayout layout = new FormLayout(
					"left:pref, 3dlu, left:pref",
					"p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			builder.add(buildCriteriaPane(d_pm), cc.xy(1, 1));
			builder.add(buildAlternativesPane(d_pm), cc.xy(3, 1));
			
			return builder.getPanel();
		}

		private Component buildCriteriaPane(BenefitRiskWizardPM pm) {
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
			for(OutcomeMeasure out : d_pm.getOutcomesListModel().getValue()){
				if(d_pm.getMetaAnalyses(out).isEmpty())
					continue;

				// Add outcome measure checkbox
				row += 2;
				LayoutUtil.addRow(layout);
				JCheckBox checkBox = BasicComponentFactory.createCheckBox(d_pm.getOutcomeSelectedModel(out), out.getName());
				builder.add(checkBox, cc.xyw(1, row, 3));
				
				// Add radio-button panel
				row += 2;
				LayoutUtil.addRow(layout);
				builder.add(buildRadioButtonAnalysisPanel(out), cc.xy(3, row, CellConstraints.LEFT, CellConstraints.DEFAULT));
			}
			
			return AuxComponentFactory.createInScrollPane(builder, 350, 550);
		}

		private JPanel buildRadioButtonAnalysisPanel(OutcomeMeasure out) {
			// create the panel
			JPanel radioButtonPanel = new JPanel();
			radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel,BoxLayout.Y_AXIS));
			
			// Retrieve the valueModel to see whether we should enable the radio-buttons.
			ValueHolder<Boolean> enabledModel = d_pm.getOutcomeSelectedModel(out);
			
			// Add the radio buttons
			for(MetaAnalysis ma : d_pm.getMetaAnalyses(out)){
				ValueModel selectedModel = d_pm.getMetaAnalysesSelectedModel(out);
				JRadioButton radioButton = AuxComponentFactory.createDynamicEnabledRadioButton(ma.getName(), ma, selectedModel, enabledModel);
				radioButtonPanel.add(radioButton);
			}
			return radioButtonPanel;
		}

		private Component buildAlternativesPane(BenefitRiskWizardPM pm) {
			FormLayout layout = new FormLayout(
					"left:pref, 3dlu, left:pref",
					"p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			JLabel alternativesLabel = new JLabel("Alternatives");
			alternativesLabel.setFont(alternativesLabel.getFont().deriveFont(Font.BOLD));
			builder.add(alternativesLabel, cc.xy(1, 1));
			
			int row = 1;
			for( Drug d : d_pm.getAlternativesListModel().getValue() ){
				LayoutUtil.addRow(layout);
				ValueHolder<Boolean> enabledModel  = d_pm.getAlternativeEnabledModel(d);
				ValueHolder<Boolean> selectedModel = d_pm.getAlternativeSelectedModel(d);
				
				JCheckBox drugCheckbox = AuxComponentFactory.createDynamicEnabledBoundCheckbox(d.getName(), enabledModel, selectedModel);
				builder.add(drugCheckbox, cc.xy(1, row += 2));
			}
			
			return AuxComponentFactory.createInScrollPane(builder, 350, 550);
		}
	}
}
