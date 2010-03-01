package org.drugis.addis.gui.builder.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.SelectableStudyGraph;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class NetworkMetaAnalysisWizard implements ViewBuilder {
	
	private NetworkMetaAnalysisWizardPM d_pm;
	private Main d_frame;

	public NetworkMetaAnalysisWizard(Main parent, NetworkMetaAnalysisWizardPM model) {
		this.d_pm = model;
		this.d_frame = parent;
	}

	public Wizard buildPanel() {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIndicationWizardStep(d_pm));
		wizardModel.add(new SelectEndpointWizardStep(d_pm));
		wizardModel.add(new SelectDrugsWizardStep());
		SelectStudiesWizardStep step = new SelectStudiesWizardStep(d_pm, d_frame);
		wizardModel.add(step);

		wizardModel.add(new SelectArmsWizardStep(d_pm));
		wizardModel.add(new OverviewWizardStep());

		Wizard wizard = new Wizard(wizardModel);		
		wizard.setDefaultExitMode(Wizard.EXIT_ON_FINISH);
		wizard.setPreferredSize(new Dimension(950, 650));

		step.setComplete(true);
		return wizard;
	}
	
	@SuppressWarnings("serial")
	public class SelectDrugsWizardStep extends PanelWizardStep {

		public SelectDrugsWizardStep() {
			super("Select Drugs","Select the drugs to be used for the network meta-analysis. To continue, (1) at least two drugs must be selected, and (2) all selected drugs must be connected.");
					
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
			
			Bindings.bind(this, "complete", d_pm.getConnectedDrugsSelectedModel());
		}
		
		private Component buildStudiesGraph() {
			SelectableStudyGraph panel = new SelectableStudyGraph(d_pm.getStudyGraphModel());
			panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			return panel;
		}
	}
	
	@SuppressWarnings("serial")
	public class OverviewWizardStep extends PanelWizardStep {
		
		public OverviewWizardStep() {
			super("Overview","Overview of selected Meta-analysis.");

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
		
		private Component buildStudiesGraph() {
			StudyGraph panel = new StudyGraph(d_pm.getSelectedStudyGraphModel());
			panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			return panel;
		}
	}
}
