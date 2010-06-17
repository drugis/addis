package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.BuildViewWhenReadyComponent;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.BenefitRiskPM;
import org.drugis.addis.util.HtmlWordWrapper;
import org.drugis.common.gui.ChildComponenentHeightPropagater;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OneWayObjectFormat;
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
import fi.smaa.jsmaa.gui.views.ResultsView;

public class BenefitRiskView implements ViewBuilder {
	
	private static final String WAITING_MESSAGE = "Please wait while the sub-analyses run";
	private BenefitRiskPM d_pm;
	private Main d_main;
	private PanelBuilder d_builder;

	
	public BenefitRiskView(BenefitRiskPM pm, Main main) {
		d_pm = pm;
		d_main = main;

	}
	
	public JComponent buildPanel() {
		if (d_builder != null)
			d_builder.getPanel().removeAll();
		
		final FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, " + // 1-3 
				"3dlu, p, 3dlu, p, " + // 4-7
				"3dlu, p, 3dlu, p, " + // 8-11 
				"3dlu, p, 3dlu, p, " + // 12-15
				"3dlu, p, 3dlu, p, " + // 16-19
				"3dlu, p, 3dlu, p," + // 20-23
				"3dlu, p"
				);
		
		d_builder = new PanelBuilder(layout);
		d_builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		d_builder.addSeparator("Benefit-Risk Analysis", cc.xy(1, 1));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()), cc.xy(1, 3));
		
		final JComponent progressBars = buildProgressBars();
		d_builder.add(progressBars, cc.xy(1, 5));
		
		d_pm.getAllModelsReadyModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (progressBars != null) {
					progressBars.setVisible(false);
					d_pm.startSMAA();
				}
			}
		});
		
		d_builder.addSeparator("Included Analyses", cc.xy(1, 7));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildAnalysesPart()), cc.xy(1, 9));
		
		d_builder.addSeparator("Measurements", cc.xy(1, 11));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildMeasurementsPart()), cc.xy(1, 13));
		
		d_builder.addSeparator("Preferences", cc.xy(1, 15));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildPreferencesPart()), cc.xy(1, 17));
		
		d_builder.addSeparator("Rank Acceptabilities", cc.xy(1, 19));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildRankAcceptabilitiesPart()), cc.xy(1, 21));
		
		d_builder.addSeparator("Central Weigths", cc.xy(1, 23));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildCentralWeightsPart()), cc.xy(1, 25));
		
		final JPanel panel = d_builder.getPanel();
		ChildComponenentHeightPropagater.attachToContainer(panel);
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// We would love to listen to componentShown(), but that isn't triggered. Hooray!
				d_pm.startAllSimulations();
			}
		});
		return panel;
	}
	
	private class PreferencesBuilder implements ViewBuilder {

		public JComponent buildPanel() {
			final JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			final PreferencePresentationModel ppm = d_pm.getPreferencePresentationModel();
			ppm.addPropertyChangeListener(PreferencePresentationModel.PREFERENCE_TYPE,
					new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent arg0) {
							panel.removeAll();
							panel.add(new ModifiedPrefInfoView(ppm, new ClinicalScaleRenderer(d_pm)).buildPanel());
							d_main.pack();
						}			
			});
			JComponent prefPanel = new ModifiedPrefInfoView(ppm, new ClinicalScaleRenderer(d_pm)).buildPanel();
			panel.add(prefPanel);
			return panel;
		}
		
	}
	
	private JComponent buildPreferencesPart() {
		return createWaiter(new PreferencesBuilder());
	}

	private JComponent buildProgressBars() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
		
		builder.addLabel("Running sub-analyses. Please wait.",cc.xy(1,1));
		int row = 1;
		for (int i=0; i<d_pm.getNumNMAProgBars(); ++i){
			LayoutUtil.addRow(layout);
			row += 2;
			JProgressBar bar = new JProgressBar();
			bar.setStringPainted(true);
			d_pm.attachNMAProgBar(bar,i);
			builder.add(bar,cc.xy(1, row));
		}

		for (int i=0; i<d_pm.getNumBaselineProgBars(); ++i){
			LayoutUtil.addRow(layout);
			row += 2;
			JProgressBar bar = new JProgressBar();
			bar.setStringPainted(true);
			d_pm.attachBaselineProgBar(bar,i);
			builder.add(bar,cc.xy(1, row));
		}
		
		return builder.getPanel();
	}
	
	private class CentralWeightsBuilder implements ViewBuilder {
		public JComponent buildPanel() {
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
	}

	private JComponent buildCentralWeightsPart() {
		return createWaiter(new CentralWeightsBuilder());
	}
	
	private class RankAcceptabilitiesBuilder implements ViewBuilder {

		public JComponent buildPanel() {
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
		
	}

	private JComponent buildRankAcceptabilitiesPart() {
		return createWaiter(new RankAcceptabilitiesBuilder());
	}

	private BuildViewWhenReadyComponent createWaiter(ViewBuilder builder) {
		return new BuildViewWhenReadyComponent(builder, d_pm.getAllModelsReadyModel(),
				WAITING_MESSAGE);
	}
	
	private JPanel buildOverviewPart() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("right:pref, 3dlu, left:pref:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		
		builder.addLabel("ID:", cc.xy(1, 1));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_NAME)), cc.xy(3, 1));
		
		builder.addLabel("Indication:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_INDICATION), new OneWayObjectFormat()), 
				cc.xy(3, 3));
		
		builder.addLabel("Criteria:", cc.xy(1, 5));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_OUTCOMEMEASURES), new OneWayObjectFormat()), 
				cc.xy(3, 5));
		
		builder.addLabel("Alternatives:", cc.xy(1, 9));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_DRUGS), new OneWayObjectFormat()), 
				cc.xy(3, 9));
		
		return builder.getPanel();	
	}
	
	private JComponent buildAnalysesPart() {	
		String[] formatter = {"name","type","indication","outcomeMeasure","drugs","studies","sampleSize"};
		return new EntitiesNodeView<MetaAnalysis>(Arrays.asList(formatter), d_pm.getAnalysesPMList(), null, null).buildPanel();
	}
	
	private JComponent buildMeasurementsPart() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		
		builder.add(HtmlWordWrapper.createHtmlPane(
				HtmlWordWrapper.wordWrap(
				"Relative measurements: odds ratio or mean difference, with "
						+ d_pm.getBean().getBaseline() +" as the common comparator.", true)),
				cc.xy(1, 1));
		builder.add(new TablePanel(new EnhancedTable(d_pm.getMeasurementTableModel(true))), cc.xy(1, 3));
	
		builder.add(HtmlWordWrapper.createHtmlPane(
				HtmlWordWrapper.wordWrap(
				"Absolute measurements: odds or mean calculated from the assumed odds or mean for " + 
				d_pm.getBean().getBaseline() + ". The method used to derive the assumed odds or mean are heuristic, "
				+ "and the absolute values should be interpreted with care.", true)),
				cc.xy(1, 5));
		builder.add(new TablePanel(new EnhancedTable(d_pm.getMeasurementTableModel(false))), cc.xy(1, 9));

		return builder.getPanel();
	}
}
