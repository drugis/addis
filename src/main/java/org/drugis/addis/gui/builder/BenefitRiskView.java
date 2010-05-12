package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.drugis.addis.entities.BenefitRiskAnalysis;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.addis.gui.AbstractTablePanel;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.BenefitRiskMeasurementTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.common.gui.ViewBuilder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

import com.jgoodies.binding.PresentationModel;
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
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.ExactMeasurement;
import fi.smaa.jsmaa.model.LogNormalMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.model.ScaleCriterion;
import fi.smaa.jsmaa.simulator.ResultsEvent;
import fi.smaa.jsmaa.simulator.SMAA2Results;
import fi.smaa.jsmaa.simulator.SMAA2SimulationThread;
import fi.smaa.jsmaa.simulator.SMAAResultsListener;
import fi.smaa.jsmaa.simulator.SMAASimulator;

public class BenefitRiskView implements ViewBuilder {

	private final class ProgressListener implements SMAAResultsListener {
		private final SMAASimulator d_simulator;
		private final JProgressBar d_bar;

		public ProgressListener(SMAASimulator simulator, JProgressBar bar) {
			d_simulator = simulator;
			d_bar = bar;
		}
		
		public void resultsChanged(ResultsEvent ev) {
			int progress = (d_simulator.getCurrentIteration() *100) / d_simulator.getTotalIterations();
			d_bar.setValue(progress);
		}
	}
	
	PresentationModel<BenefitRiskAnalysis> d_pm;
	private PresentationModelFactory d_pmf;
	private Main d_main;
	
	private Map<OutcomeMeasure, Criterion> d_outcomeCriterionMap;
	private Map<Drug, Alternative> d_drugAlternativeMap;
	private JProgressBar d_SMAAprogressBar;
	private SMAA2Results d_smaaModelResults;
	
	public BenefitRiskView(PresentationModel<BenefitRiskAnalysis> pm, PresentationModelFactory pmf, Main main) {
		d_pm = pm;
		d_pmf = pmf;
		d_main = main;
		
		d_outcomeCriterionMap = new HashMap<OutcomeMeasure, Criterion>();
		d_drugAlternativeMap  = new HashMap<Drug, Alternative>();
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
		builder.add(GUIFactory.createCollapsiblePanel(buildRankAcceptabilitiesPart()), cc.xy(1, 21));
		
		builder.addSeparator("central weigths", cc.xy(1, 23));
		builder.add(GUIFactory.createCollapsiblePanel(buildWeightsPart()), cc.xy(1, 25));
		
		return builder.getPanel();
	}

	private JComponent buildWeightsPart() {
		CentralWeightsDataset   cwData       = new CentralWeightsDataset(getSmaaModelResults());
		CentralWeightTableModel cwTableModel = new CentralWeightTableModel(getSmaaModelResults());

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
		
		SMAA2ResultsTableModel tableModel = new RankAcceptabilityTableModel(getSmaaModelResults());
		ResultsTable table = new ResultsTable(tableModel);
		
		RankAcceptabilitiesDataset rankData = new RankAcceptabilitiesDataset(getSmaaModelResults());
		final JFreeChart chart = ChartFactory.createStackedBarChart(
		        "", "Alternative", "Rank Acceptability",
		        rankData, PlotOrientation.VERTICAL, true, true, false);


		
		JPanel panel = new JPanel(new BorderLayout());
		fi.smaa.jsmaa.gui.views.ResultsView view = new fi.smaa.jsmaa.gui.views.ResultsView(d_main, "Rank acceptability indices", table, chart, "");
		panel.add(view.buildPanel(), BorderLayout.CENTER);
		panel.add(d_SMAAprogressBar, BorderLayout.NORTH);

		
		return panel;
	}

	private SMAA2Results getSmaaModelResults() {
		
		if (d_smaaModelResults != null)
			return d_smaaModelResults;

		BenefitRiskAnalysis brAnalysis = d_pm.getBean();
		SMAAModel smaaModel = new SMAAModel(brAnalysis.getName());

		Alternative baseLineAlt = getAlternative(brAnalysis.getBaseline());
		smaaModel.addAlternative(baseLineAlt);

		for(OutcomeMeasure om : brAnalysis.getOutcomeMeasures()){ // endpoints
			Criterion crit = getCriterion(om);
			smaaModel.addCriterion(crit);
			smaaModel.setMeasurement(crit, baseLineAlt, new ExactMeasurement(1.0));		
			for(Drug d : brAnalysis.getDrugs()){ // drugs
				smaaModel.addAlternative(getAlternative(d));
				RelativeEffect<? extends Measurement> relativeEffect = brAnalysis.getRelativeEffect(d, om);
//			TODO: 
				//if(dichotomous) then
				fi.smaa.jsmaa.model.Measurement m = new LogNormalMeasurement(relativeEffect.getRelativeEffect(), relativeEffect.getError());
				//else if(continuous) then the baseline is 0, and:
//				fi.smaa.jsmaa.model.Measurement m = new GaussianMeasurement(relativeEffect.getRelativeEffect(), relativeEffect.getError());
				smaaModel.setMeasurement( crit, getAlternative(d), m);		
			}
		}

		SMAA2SimulationThread simulationThread = new SMAA2SimulationThread(smaaModel, 500000);
		SMAASimulator simulator = new SMAASimulator(smaaModel, simulationThread);
		d_smaaModelResults = (SMAA2Results)simulator.getResults();
		d_SMAAprogressBar = new JProgressBar();

		d_smaaModelResults.addResultsListener(new ProgressListener(simulator, d_SMAAprogressBar));
		simulationThread.start();

		return d_smaaModelResults;
	}
	
	private Criterion getCriterion(OutcomeMeasure om) {
		if(d_outcomeCriterionMap.containsKey(om))
			return d_outcomeCriterionMap.get(om);
		ScaleCriterion c = new ScaleCriterion(om.getName());
		d_outcomeCriterionMap.put(om, c);
		return c;
	}
	
	private Alternative getAlternative(Drug d) {
		if(d_drugAlternativeMap.containsKey(d))
			return d_drugAlternativeMap.get(d);
		Alternative a = new Alternative(d.getName());
		d_drugAlternativeMap.put(d, a);
		return a;
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
		return new AbstractTablePanel(brTableModel);
	}

}
