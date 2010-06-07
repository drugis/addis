package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.gui.AbstractTablePanel;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.BenefitRiskPM;
import org.drugis.common.gui.LayoutUtil;
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
import fi.smaa.jsmaa.gui.presentation.PreferencePresentationModel;
import fi.smaa.jsmaa.gui.views.PreferenceInformationView;
import fi.smaa.jsmaa.gui.views.ResultsView;

public class BenefitRiskView implements ViewBuilder {
	
	private BenefitRiskPM d_pm;
	private Main d_main;
	private PanelBuilder d_builder;
	
	public BenefitRiskView(BenefitRiskPM pm, Main main) {
		d_pm = pm;
		d_main = main;
		d_pm.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(BenefitRiskPM.PROPERTY_ALLMODELSREADY)){
					d_main.reloadRightPanel();
				}
			}
		});
	}
	
	public JComponent buildPanel() {
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
		
		if(d_pm.allModelsReady()){
			d_builder.addSeparator("Preferences", cc.xy(1, 15));
			d_builder.add(GUIFactory.createCollapsiblePanel(buildPreferencesPart()), cc.xy(1, 17));

			d_builder.addSeparator("Rank Acceptabilities", cc.xy(1, 19));

			d_builder.add(GUIFactory.createCollapsiblePanel(buildRankAcceptabilitiesPart()), cc.xy(1, 21));

			d_builder.addSeparator("Central Weigths", cc.xy(1, 23));
			d_builder.add(GUIFactory.createCollapsiblePanel(buildWeightsPart()), cc.xy(1, 25));
		} else {
			d_builder.add(GUIFactory.createCollapsiblePanel(buildAnalyzingPart()), cc.xy(1, 15));
		}
		return d_builder.getPanel();
	}

	private JComponent buildPreferencesPart() {
		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		final PreferencePresentationModel ppm = d_pm.getPreferencePresentationModel();
		ppm.addPropertyChangeListener(PreferencePresentationModel.PREFERENCE_TYPE,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent arg0) {
						panel.removeAll();
						panel.add(new PreferenceInformationView(ppm).buildPanel());
						d_main.pack();
					}			
		});
		JComponent prefPanel = new PreferenceInformationView(ppm).buildPanel();
		panel.add(prefPanel);
		return panel;
	}

	private JComponent buildAnalyzingPart() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator("Running MTC models ... please wait",cc.xy(1,1));
		int row = 1;
		for (int i=0; i<d_pm.getNumProgBars(); ++i){
			LayoutUtil.addRow(layout);
			row += 2;
			JProgressBar bar = new JProgressBar();
			bar.setStringPainted(true);
			d_pm.attachProgBar(bar,i);
			builder.add(bar,cc.xy(1, row));
		}
		
		return builder.getPanel();
	}

	private JComponent buildWeightsPart() {
		final JFreeChart chart = ChartFactory.createLineChart(
		        "", "Criterion", "Central Weight",
		        d_pm.getCentralWeightsDataSet(), PlotOrientation.VERTICAL, true, true, false);
		LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, true);
		chart.getCategoryPlot().setRenderer(renderer);
		ResultsTable table = new ResultsTable(d_pm.getCentralWeightsTableModel());
		table.setDefaultRenderer(Object.class, new ResultsCellRenderer(1.0));
		
		// FIXME: FileNames.ICON_SCRIPT was replaced by "". Should be filename of an icon 
		return new ResultsView(d_main, table, chart, "").buildPanel(); 
	}

	private JComponent buildRankAcceptabilitiesPart() {
		ResultsTable table = new ResultsTable(d_pm.getRankAcceptabilitiesTableModel());
	
		final JFreeChart chart = ChartFactory.createStackedBarChart(
		        "", "Alternative", "Rank Acceptability",
		        d_pm.getRankAcceptabilityDataSet(), PlotOrientation.VERTICAL, true, true, false);

		JPanel panel = new JPanel(new BorderLayout());
		fi.smaa.jsmaa.gui.views.ResultsView view = new fi.smaa.jsmaa.gui.views.ResultsView(d_main, table, chart, "");
		panel.add(view.buildPanel(), BorderLayout.CENTER);
		panel.add(d_pm.getSmaaSimulationProgressBar(), BorderLayout.NORTH);

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
		
		builder.addLabel("Alternatives:", cc.xy(1, 9));
		builder.add(new JLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_DRUGS).getValue().toString()),cc.xy(3, 9));
		
		return builder.getPanel();	
	}
	
	private JComponent buildAnalysesPart() {	
		String[] formatter = {"name","type","indication","outcomeMeasure","drugs","studies","sampleSize"};
		return new EntitiesNodeView<MetaAnalysis>(Arrays.asList(formatter), d_pm.getAnalysesPMList(), null, null).buildPanel();
	}
	
	private JComponent buildMeasurementsPart() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("left:pref:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		
		builder.addLabel("All measurements are relative to "+d_pm.getBean().getBaseline(), cc.xy(1, 1));
		builder.add(new AbstractTablePanel(d_pm.getMeasurementTableModel(true)), cc.xy(1, 3));
	
		builder.addLabel("All measurements using assumption for absolute measurement on "+d_pm.getBean().getBaseline(), cc.xy(1, 5));
		builder.add(new AbstractTablePanel(d_pm.getMeasurementTableModel(false)), cc.xy(1, 7));
	
		return builder.getPanel();
	}
}
