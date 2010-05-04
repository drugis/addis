package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.wizard.BenefitRiskWizardPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fi.smaa.common.gui.LayoutUtil;

@SuppressWarnings("serial")
public class BenefitRiskWizard extends Wizard {
	
	public BenefitRiskWizard(Main parent, BenefitRiskWizardPresentation pm) {
		super(buildModel(pm, parent));
		
		setPreferredSize(new Dimension(750, 750));
	}

	private static WizardModel buildModel(BenefitRiskWizardPresentation pm, Main frame) {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIndicationWizardStep(pm));
		wizardModel.add(new SelectCriteriaAndAlternativesWizardStep(pm));
		
		return wizardModel;
	}
	
	private static class SelectCriteriaAndAlternativesWizardStep extends PanelWizardStep {
		
		private BenefitRiskWizardPresentation d_pm;

		public SelectCriteriaAndAlternativesWizardStep(BenefitRiskWizardPresentation pm){
			super("Select Criteria and Alternatives","In this step, you select the criteria (analyses on specific outcomemeasures) and the alternatives (drugs) to include in the benefit-risk analysis. To perform the analysis, at least two criteria and at least two alternatives must be included.");
			d_pm = pm;
			
			//prepare();
		}

		@Override
		public void prepare() {
			this.removeAll();
			add(buildPanel());
		}

		private JPanel buildPanel() {
			setLayout(new BorderLayout());
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

		private Component buildCriteriaPane(BenefitRiskWizardPresentation pm) {
			setLayout(new BorderLayout());
			FormLayout layout = new FormLayout(
					"left:pref",
					"p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			JLabel criteriaLabel = new JLabel("Criteria                                          ");
			criteriaLabel.setFont(new Font(Font.SERIF, Font.BOLD, 12));
			builder.add(criteriaLabel, cc.xy(1, 1));
			int row = 1;
			for(OutcomeMeasure out : d_pm.getOutcomesListModel().getValue()){
				// Add outcome measure checkbox
				row += 2;
				LayoutUtil.addRow(layout);
				JCheckBox checkBox = BasicComponentFactory.createCheckBox(d_pm.getOutcomeSelectedModel(out), out.getName());
				builder.add(checkBox, cc.xy(1, row));
				
				// Add radio-button panel
				row += 2;
				LayoutUtil.addRow(layout);
				builder.add(buildRadioButtonAnalysisPanel(out), cc.xy(1, row, CellConstraints.RIGHT, CellConstraints.DEFAULT));
			}
			
			return builder.getPanel();
		}

		private JPanel buildRadioButtonAnalysisPanel(OutcomeMeasure out) {
			// create the panel
			JPanel radioButtonPanel = new JPanel();
			radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel,BoxLayout.Y_AXIS));
			
			// Retrieve the valueModel to see whether we should enable the radio-buttons.
			ValueHolder<Boolean> enabledModel = d_pm.getOutcomeSelectedModel(out);
			
			// Add the radio buttons
			ButtonGroup group = new ButtonGroup();
			for(MetaAnalysis ma : d_pm.getMetaAnalyses(out)){
				JRadioButton radioButton = AuxComponentFactory.createDynamicEnabledRadioButton(ma.getName(), enabledModel);
				radioButtonPanel.add(radioButton);
				group.add(radioButton);
			}
			return radioButtonPanel;
		}

		private Component buildAlternativesPane(BenefitRiskWizardPresentation pm) {
			// TODO Auto-generated method stub
			return new JLabel("buildAlternativesPane not implemented yet");
		}

	}
}
