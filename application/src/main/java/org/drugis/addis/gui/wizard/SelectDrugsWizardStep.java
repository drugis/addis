package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.drugis.addis.gui.SelectableStudyGraph;
import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SelectDrugsWizardStep extends AbstractSelectTreatmentWizardStep {

	private static final long serialVersionUID = 5310567692551793030L;

	public SelectDrugsWizardStep(NetworkMetaAnalysisWizardPM pm) {
		super("Select Drugs","Select the drugs to be used for the network meta-analysis. Click to select (green) or deselect (gray).  To continue, (1) at least two drugs must be selected, and (2) all selected drugs must be connected.");
		d_pm = pm;
		d_studyGraph = new SelectableStudyGraph(pm.getRawStudyGraphModel());

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
		
		Bindings.bind(this, "complete", pm.getRawConnectedDrugsSelectedModel());
	}
	
	
	@Override
	public void prepare() {
		d_pm.updateStudyGraphModels();
		d_studyGraph.layoutGraph();
	}
}