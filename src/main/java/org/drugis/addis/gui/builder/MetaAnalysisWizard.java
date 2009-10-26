package org.drugis.addis.gui.builder;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import org.drugis.addis.presentation.MetaAnalysisWizardPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MetaAnalysisWizard implements ViewBuilder {

	private MetaAnalysisWizardPresentation d_pm;
	
	public MetaAnalysisWizard(MetaAnalysisWizardPresentation pm) {
		d_pm = pm;
	}
	
	public Wizard buildPanel() {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIndicationWizardStep());
		wizardModel.add(new SelectEndpointWizardStep());
		wizardModel.add(new SelectDrugsWizardStep());
		return new Wizard(wizardModel);
	}
	
	@SuppressWarnings("serial")
	public class SelectStudiesWizardStep extends PanelWizardStep {
		public SelectStudiesWizardStep() {
			super("Select Studies","Select an Endpoint that you want to use for this meta analysis.");
	
		}
	}
	
	@SuppressWarnings("serial")
	public class SelectDrugsWizardStep extends PanelWizardStep {
		public SelectDrugsWizardStep() {
			super("Select two drugs","Select two drugs to be used for meta analysis.");
			
			
			
			FormLayout layout = new FormLayout(
					"center:pref, 3dlu, center:pref, 3dlu, center:pref",
					"p, 3dlu, p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			
			CellConstraints cc = new CellConstraints();
			builder.addLabel("First Drug",cc.xy(1, 1));
			builder.addLabel("Second Drug",cc.xy(5, 1));
						
			JComboBox firstDrugBox = createDrugSelectionBox(d_pm.getFirstDrugModel());
			JComboBox secondDrugBox = createDrugSelectionBox(d_pm.getSecondDrugModel());
			
			builder.add(firstDrugBox,cc.xy(1, 3));
			builder.add(secondDrugBox,cc.xy(5, 3));
			builder.addLabel("VS",cc.xy(3, 3));
			
			add(builder.getPanel());
		}

		private JComboBox createDrugSelectionBox(ValueModel firstDrugModel) {
			JComboBox endPointBox = AuxComponentFactory.createBoundComboBox(d_pm.getDrugSet().toArray(), firstDrugModel);
			endPointBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					// FIXME
					setComplete(true);
					//setComplete(d_pm.getEndpointModel().getValue() != null);					
				}
			});
			return endPointBox;
		}
	}
	
	@SuppressWarnings("serial")
	public class SelectEndpointWizardStep extends PanelWizardStep {
		public SelectEndpointWizardStep() {
			super("Select Endpoint","Select an Endpoint that you want to use for this meta analysis.");
			JComboBox endPointBox = AuxComponentFactory.createBoundComboBox(d_pm.getEndpointListModel(), d_pm.getEndpointModel());
			add(endPointBox);
			endPointBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					// FIXME
					setComplete(true);
					//setComplete(d_pm.getEndpointModel().getValue() != null);					
				}
			});			
		}
	}
	
	@SuppressWarnings("serial")
	public class SelectIndicationWizardStep extends PanelWizardStep {
		 public SelectIndicationWizardStep() {
			 super("Select Indication","Select an Indication that you want to use for this meta analysis.");
			JComboBox indBox = AuxComponentFactory.createBoundComboBox(d_pm.getIndicationListModel(), d_pm.getIndicationModel());
			add(indBox);
			indBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					setComplete(d_pm.getIndicationModel().getValue() != null);					
				}
			});
		}
	}	
}
