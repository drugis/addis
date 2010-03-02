package org.drugis.addis.gui.builder.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.SelectableStudyGraph;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class NetworkMetaAnalysisWizard extends Wizard {

	public NetworkMetaAnalysisWizard(Main parent, NetworkMetaAnalysisWizardPM model) {
		super(buildModel(model, parent));
		setDefaultExitMode(Wizard.EXIT_ON_FINISH);
		setPreferredSize(new Dimension(950, 650));
	}

	private static WizardModel buildModel(NetworkMetaAnalysisWizardPM pm, Main main) {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIndicationWizardStep(pm));
		wizardModel.add(new SelectEndpointWizardStep(pm));
		wizardModel.add(new SelectDrugsWizardStep(pm, main));
		SelectStudiesWizardStep selectStudiesStep = new SelectStudiesWizardStep(pm, main);
		wizardModel.add(selectStudiesStep);
		Bindings.bind(selectStudiesStep, "complete", pm.getStudySelectionCompleteModel());
		wizardModel.add(new SelectArmsWizardStep(pm));
		wizardModel.add(new OverviewWizardStep(pm, main));
		return wizardModel;
	}
	
	public static class SelectDrugsWizardStep extends PanelWizardStep {

		public SelectDrugsWizardStep(NetworkMetaAnalysisWizardPM pm, Main main) {
			super("Select Drugs","Select the drugs to be used for the network meta-analysis. To continue, (1) at least two drugs must be selected, and (2) all selected drugs must be connected.");
					
			setLayout(new BorderLayout());
			    
			FormLayout layout = new FormLayout(
					"center:pref:grow",
					"p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			builder.add(buildStudiesGraph(pm), cc.xy(1, 1));
			
			JScrollPane sp = new JScrollPane(builder.getPanel());
			add(sp);
			sp.getVerticalScrollBar().setUnitIncrement(16);
			
			Bindings.bind(this, "complete", pm.getConnectedDrugsSelectedModel());
		}
		
		private Component buildStudiesGraph(NetworkMetaAnalysisWizardPM pm) {
			SelectableStudyGraph panel = new SelectableStudyGraph(pm.getStudyGraphModel());
			panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			return panel;
		}
	}
	
	public static class OverviewWizardStep extends PanelWizardStep {
		private final NetworkMetaAnalysisWizardPM d_pm;
		private final Main d_main;

		public OverviewWizardStep(NetworkMetaAnalysisWizardPM pm, Main main) {
			super("Overview","Overview of selected Meta-analysis.");
			d_pm = pm;
			d_main = main;

			setLayout(new BorderLayout());
			    
			FormLayout layout = new FormLayout(
					"center:pref:grow",
					"p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			builder.add(buildStudiesGraph(), cc.xy(1, 1));
			
			JScrollPane sp = new JScrollPane(builder.getPanel());
			add(sp);
			sp.getVerticalScrollBar().setUnitIncrement(16);
			
			setComplete(true);
		}
		
		public void applyState() throws InvalidStateException {
			JOptionPane.showMessageDialog(d_main, 
					"Sorry, we are not yet able to save Network Meta-Analyses", 
					"Not Implemented", JOptionPane.WARNING_MESSAGE);
		}
		
		private Component buildStudiesGraph() {
			StudyGraph panel = new StudyGraph(d_pm.getSelectedStudyGraphModel());
			panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			return panel;
		}
	}
}
