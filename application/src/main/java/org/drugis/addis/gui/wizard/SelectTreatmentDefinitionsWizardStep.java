package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SelectTreatmentDefinitionsWizardStep extends AbstractSelectTreatmentWizardStep {
	private static final long serialVersionUID = 2928649302800999758L;

	public SelectTreatmentDefinitionsWizardStep(NetworkMetaAnalysisWizardPM pm) {
		super("Select defintions",
				"Select the treatment to be used for the network meta-analysis. Click to select (green) or deselect (gray).  To continue, (1) at least two definitions must be selected, and (2) all selected definitions must be connected.",
				pm.getRefinedAlternativesGraph());
		d_pm = pm;

		setLayout(new BorderLayout());
		    
		FormLayout layout = new FormLayout(
				"center:pref:grow",
				"p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		builder.add(d_studyGraph, cc.xy(1, 1));
		
		JScrollPane sp = new JScrollPane(builder.getPanel());
		add(sp);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		
		Bindings.bind(this, "complete", pm.getRefinedConnectedDrugsSelectedModel());
	}
	
	@Override
	public void prepare() {
		System.out.println("Requesting rebuild");
		d_pm.rebuildRefinedAlternativesGraph();
		d_studyGraph.layoutGraph();
	}
}