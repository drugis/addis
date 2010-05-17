package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.drugis.addis.entities.BenefitRiskAnalysis;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.addis.gui.AbstractTablePanel;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.BenefitRiskPM;
import org.drugis.common.gui.ViewBuilder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fi.smaa.jsmaa.gui.components.ResultsCellRenderer;
import fi.smaa.jsmaa.gui.components.ResultsTable;
import fi.smaa.jsmaa.gui.jfreechart.CentralWeightsDataset;
import fi.smaa.jsmaa.gui.jfreechart.RankAcceptabilitiesDataset;
import fi.smaa.jsmaa.gui.presentation.CentralWeightTableModel;
import fi.smaa.jsmaa.gui.presentation.RankAcceptabilityTableModel;
import fi.smaa.jsmaa.gui.presentation.SMAA2ResultsTableModel;
import fi.smaa.jsmaa.gui.views.ResultsView;

public class BenefitRiskView implements ViewBuilder {
	
	private BenefitRiskPM d_pm;
	private Main d_main;
	private JProgressBar d_SMAAprogressBar;
	private PanelBuilder d_builder;
	
	public BenefitRiskView(BenefitRiskPM pm, Main main) {
		d_pm = pm;
		d_main = main;
		d_SMAAprogressBar = new JProgressBar();
		d_pm.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(BenefitRiskPM.PROPERTY_ALLMODELSREADY)){
					System.out.println("All models ready");
					d_main.reloadRightPanel();
				}
			}
		});
	}
	
	public JComponent buildPanel() {
		System.out.println("Building panel");
		
		if (d_builder != null)
			d_builder.getPanel().removeAll();
		
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		d_builder = new PanelBuilder(layout);
		d_builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		d_builder.addSeparator("Benefit-Risk Analysis", cc.xy(1, 1));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()), cc.xy(1, 3));
		
		d_builder.addSeparator("Included Analyses", cc.xy(1, 7));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildAnalysesPart()), cc.xy(1, 9));
		
		d_builder.addSeparator("Measurements", cc.xy(1, 11));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildMeasurementsPart()), cc.xy(1, 13));
		
		d_builder.addSeparator("preferences", cc.xy(1, 15));
		//builder.add(GUIFactory.createCollapsiblePanel(buildPreferencesPart()), cc.xy(1, 17));
		
		d_builder.addSeparator("rank acceptabilities", cc.xy(1, 19));
		if(d_pm.allModelsReady())
			d_builder.add(GUIFactory.createCollapsiblePanel(buildRankAcceptabilitiesPart()), cc.xy(1, 21));
		
		d_builder.addSeparator("central weigths", cc.xy(1, 23));
		if(d_pm.allModelsReady())
			d_builder.add(GUIFactory.createCollapsiblePanel(buildWeightsPart()), cc.xy(1, 25));
		
		return d_builder.getPanel();
	}

	private JComponent buildWeightsPart() {
		CentralWeightsDataset   cwData       = new CentralWeightsDataset(d_pm.getSmaaModelResults(d_SMAAprogressBar));
		CentralWeightTableModel cwTableModel = new CentralWeightTableModel(d_pm.getSmaaModelResults(d_SMAAprogressBar));

		final JFreeChart chart = ChartFactory.createLineChart(
		        "", "Criterion", "Central Weight",
		        cwData, PlotOrientation.VERTICAL, true, true, false);
		LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, true);
		chart.getCategoryPlot().setRenderer(renderer);
		ResultsTable table = new ResultsTable(cwTableModel);
		table.setDefaultRenderer(Object.class, new ResultsCellRenderer(1.0));
		
		// FIXME: FileNames.ICON_SCRIPT was replaced by "". Should be filename of an icon 
		return new ResultsView(d_main, "Central weight vectors", table, chart, "").buildPanel(); 
	}

	private JComponent buildRankAcceptabilitiesPart() {
		
		SMAA2ResultsTableModel tableModel = new RankAcceptabilityTableModel(d_pm.getSmaaModelResults(d_SMAAprogressBar));
		ResultsTable table = new ResultsTable(tableModel);
		
		RankAcceptabilitiesDataset rankData = new RankAcceptabilitiesDataset(d_pm.getSmaaModelResults(d_SMAAprogressBar));
		final JFreeChart chart = ChartFactory.createStackedBarChart(
		        "", "Alternative", "Rank Acceptability",
		        rankData, PlotOrientation.VERTICAL, true, true, false);

		JPanel panel = new JPanel(new BorderLayout());
		fi.smaa.jsmaa.gui.views.ResultsView view = new fi.smaa.jsmaa.gui.views.ResultsView(d_main, "Rank acceptability indices", table, chart, "");
		panel.add(view.buildPanel(), BorderLayout.CENTER);
		panel.add(d_SMAAprogressBar, BorderLayout.NORTH);

		return panel;
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
		String[] formatter = {"name","type","indication","outcomeMeasure","drugs","studies","sampleSize"};
		return new EntitiesNodeView<MetaAnalysis>(Arrays.asList(formatter), d_pm.getAnalysesPMList(), null, null).buildPanel();
	}
	
	private JComponent buildMeasurementsPart() {
		return new AbstractTablePanel(d_pm.getMeasurementTableModel());
	}
}
