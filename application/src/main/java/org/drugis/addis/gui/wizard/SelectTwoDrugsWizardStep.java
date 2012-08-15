package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.presentation.SelectableTreatmentDefinitionsGraphModel;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.wizard.PairWiseMetaAnalysisWizardPresentation;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SelectTwoDrugsWizardStep extends PanelWizardStep {
	private static final long serialVersionUID = 3407052127448073388L;
	PairWiseMetaAnalysisWizardPresentation d_pm;
	private StudyGraph d_studyGraph;
	private SelectableTreatmentDefinitionsGraphModel d_graph;
	private ValueHolder<TreatmentDefinition> d_firstDefinition;
	private ValueHolder<TreatmentDefinition> d_secondDefinition;
	private Runnable d_rebuildGraph;

	public SelectTwoDrugsWizardStep(PairWiseMetaAnalysisWizardPresentation pm, 
			SelectableTreatmentDefinitionsGraphModel graph, 
			ValueHolder<TreatmentDefinition> firstDefinition, 
			ValueHolder<TreatmentDefinition> secondDefinition, 
			Runnable rebuildGraph, String title, String description) {
		super(title, description);
		
		d_pm = pm;
		d_graph = graph;
		d_firstDefinition = firstDefinition;
		d_secondDefinition = secondDefinition;
		d_rebuildGraph = rebuildGraph;
		
		setLayout(new BorderLayout());
		FormLayout layout = new FormLayout(
				"center:pref:grow",
				"p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		builder.add(buildSelectDrugsPanel(), cc.xy(1, 1));			
		builder.add(BasicComponentFactory.createLabel(d_pm.getStudiesMeasuringLabelModel()),
				cc.xy(1, 3));
		builder.setBorder(BorderFactory.createEmptyBorder());
		builder.add(buildStudiesGraph(), cc.xy(1, 5));
		
		JScrollPane sp = new JScrollPane(builder.getPanel());
		add(sp);
		sp.getVerticalScrollBar().setUnitIncrement(16);			
		
		Bindings.bind(this, "complete", graph.getSelectionCompleteModel());
	}
	
	private Component buildStudiesGraph() {
		TreatmentDefinitionsGraphModel pm = d_graph;
		d_studyGraph = new StudyGraph(pm);
		return d_studyGraph;
	}
	
	@Override
	public void prepare() {
		d_rebuildGraph.run();
		d_studyGraph.layoutGraph();
	}

	private JPanel buildSelectDrugsPanel() {
		FormLayout layout = new FormLayout(
				"center:pref, 3dlu, center:pref, 3dlu, center:pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		builder.addLabel("First Drug",cc.xy(1, 1));
		builder.addLabel("Second Drug",cc.xy(5, 1));
					
		JComboBox firstDrugBox = AuxComponentFactory.createBoundComboBox(d_graph.getDefinitions(), d_firstDefinition, true);
		JComboBox secondDrugBox = AuxComponentFactory.createBoundComboBox(d_graph.getDefinitions(), d_secondDefinition, true);
		
		builder.add(firstDrugBox,cc.xy(1, 3));
		builder.add(secondDrugBox,cc.xy(5, 3));
		builder.addLabel("VS",cc.xy(3, 3));
		JPanel panel = builder.getPanel();			
		
		return panel;
	}
}