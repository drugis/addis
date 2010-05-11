package org.drugis.addis.gui.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.drugis.addis.entities.BenefitRiskAnalysis;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.addis.gui.AbstractTablePanel;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.BenefitRiskMeasurementTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class BenefitRiskView implements ViewBuilder {

	PresentationModel<BenefitRiskAnalysis> d_pm;
	private PresentationModelFactory d_pmf;
	
	public BenefitRiskView(PresentationModel<BenefitRiskAnalysis> pm, PresentationModelFactory pmf) {
		d_pm = pm;
		d_pmf = pmf;
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator("Benefit-Risk Analysis", cc.xy(1, 1));
		builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()), cc.xy(1, 3));
		
		builder.addSeparator("Included Analyses", cc.xy(1, 7));
		builder.add(GUIFactory.createCollapsiblePanel(buildAnalysesPart()), cc.xy(1, 9));
		
		builder.addSeparator("Measurements", cc.xy(1, 11));
		builder.add(GUIFactory.createCollapsiblePanel(buildMeasurementsPart()), cc.xy(1, 13));
		
		builder.addSeparator("preferences", cc.xy(1, 15));
//		builder.add(GUIFactory.createCollapsiblePanel(buildPreferencesPart()), cc.xy(1, 17));
		
		builder.addSeparator("rank acceptabilities", cc.xy(1, 19));
//		builder.add(GUIFactory.createCollapsiblePanel(buildRankAcceptabilitiesPart()), cc.xy(1, 21));
		
		builder.addSeparator("central weigths", cc.xy(1, 23));
//		builder.add(GUIFactory.createCollapsiblePanel(buildWeightsPart()), cc.xy(1, 25));
		
		return builder.getPanel();
	}

	private JPanel buildOverviewPart() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("right:pref, 3dlu, left:pref:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		
		builder.addLabel("ID:", cc.xy(1, 1));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_NAME)),cc.xy(3, 1));
		
		builder.addLabel("Indication:", cc.xy(1, 3));
		builder.add(new JLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_INDICATION).getValue().toString()),cc.xy(3, 3));
		
		builder.addLabel("Criteria:", cc.xy(1, 5));
		builder.add(new JLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_OUTCOMEMEASURES).getValue().toString()),cc.xy(3, 5));
		
		builder.addLabel("Baseline:", cc.xy(1, 7));
		builder.add(new JLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_BASELINE).getValue().toString()),cc.xy(3, 7));
		
		builder.addLabel("Alternatives:", cc.xy(1, 9));
		builder.add(new JLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_DRUGS).getValue().toString()),cc.xy(3, 9));
		
		return builder.getPanel();	
	}
	
	private JComponent buildAnalysesPart() {
		
		List<PresentationModel<MetaAnalysis>> entitiesPMs = new ArrayList<PresentationModel<MetaAnalysis>>();
		for (MetaAnalysis a : d_pm.getBean().getMetaAnalyses())
			entitiesPMs.add(d_pmf.getModel(a));
		
		String[] formatter = {"name","type","indication","outcomeMeasure","drugs","studies","sampleSize"};
		return new EntitiesNodeView<MetaAnalysis>(Arrays.asList(formatter), entitiesPMs, null, null).buildPanel();
	}
	
	private JComponent buildMeasurementsPart() {
		BenefitRiskMeasurementTableModel brTableModel = new BenefitRiskMeasurementTableModel(d_pm.getBean(), d_pmf);
//		JTable jTable = new JTable(brTableModel);
//		jTable.getColumn(0).setHeaderValue("lhkgs");
		return new AbstractTablePanel(brTableModel);
	}

}
