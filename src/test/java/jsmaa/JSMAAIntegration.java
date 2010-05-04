package jsmaa;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

import fi.smaa.jsmaa.gui.components.ResultsTable;
import fi.smaa.jsmaa.gui.jfreechart.RankAcceptabilitiesDataset;
import fi.smaa.jsmaa.gui.presentation.RankAcceptabilityTableModel;
import fi.smaa.jsmaa.gui.presentation.SMAA2ResultsTableModel;
import fi.smaa.jsmaa.gui.views.ResultsView;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.model.ScaleCriterion;
import fi.smaa.jsmaa.simulator.ResultsEvent;
import fi.smaa.jsmaa.simulator.SMAA2Results;
import fi.smaa.jsmaa.simulator.SMAA2SimulationThread;
import fi.smaa.jsmaa.simulator.SMAAResultsListener;
import fi.smaa.jsmaa.simulator.SMAASimulator;

public class JSMAAIntegration {
	
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

	private SMAAModel d_smaaModel;
	private NetworkMetaAnalysis d_HAMDMetaAnalysis;
	private NetworkMetaAnalysis d_ConvulsionMetaAnalysis;
	private ArrayList<Drug> d_intersectDrugs;
	
	private JSMAAIntegration() {
		d_HAMDMetaAnalysis = ExampleData.buildNetworkMetaAnalysis();
		d_ConvulsionMetaAnalysis = ExampleData.buildNetworkMetaAnalysisAlternative();
		
		d_smaaModel = new SMAAModel("test");
	}
	
	public static void main(String [] args) {
		JSMAAIntegration integr = new JSMAAIntegration();
		System.out.println("adding drugs");
		integr.addDrugs();
		System.out.println("adding outcomes");
		integr.addOutcomes();
		System.out.println("add measurements");
		integr.addModelResults();
		System.out.println("showing results");
		integr.showResults();
		System.out.println("done");
	}

	public void addDrugs() {
		// these are the alternatives in the BR analysis
		d_intersectDrugs = new ArrayList<Drug>();
		d_intersectDrugs.addAll( d_ConvulsionMetaAnalysis.getIncludedDrugs());
		
		d_intersectDrugs.retainAll(d_HAMDMetaAnalysis.getIncludedDrugs());
		
		for(Drug d : d_intersectDrugs){
			d_smaaModel.addAlternative(new Alternative(d.getName()));
		}
		d_smaaModel.getAlternatives();
	}

	public void addOutcomes() {
		// these are the criteria in the BR analysis
		OutcomeMeasure hamd = d_HAMDMetaAnalysis.getOutcomeMeasure();
		ScaleCriterion hamdCrit = new ScaleCriterion(hamd.getName());
		d_smaaModel.addCriterion(hamdCrit);
		
		OutcomeMeasure conv = d_ConvulsionMetaAnalysis.getOutcomeMeasure();
		ScaleCriterion convCrit = new ScaleCriterion(conv.getName());
		d_smaaModel.addCriterion(convCrit);

	}

	public void addModelResults() {
//		// the outputs of addis::metaanalysis, these are the inputs for BR analysis
//		for(MetaAnalysis ma : d_analyses){
//			d_smaaModel.addCriterion(new ScaleCriterion(ma.getOutcomeMeasure().getName()));
//			for(Drug d : ma.getIncludedDrugs()){
//				
//			}
//		}
//		d_ConvulsionMetaAnalysis.getInconsistencyModel().get
			
		// CONSISTENCYMODELS!!
		
		
		for(Criterion c : d_smaaModel.getCriteria()){
			for(Alternative a : d_smaaModel.getAlternatives()){
				fi.smaa.jsmaa.model.Measurement m = new GaussianMeasurement(5.0, 1.0);
				d_smaaModel.setMeasurement(c,a, m);
			}
		}
	}
	
	public void showResults() {
		SMAA2SimulationThread draadje = new SMAA2SimulationThread(d_smaaModel, 500000);
		SMAASimulator simulator = new SMAASimulator(d_smaaModel, draadje);
		SMAA2Results results = (SMAA2Results)simulator.getResults();
		JProgressBar bar = new JProgressBar();
		
		results.addResultsListener(new ProgressListener(simulator, bar));
		
		SMAA2ResultsTableModel tableModel = new RankAcceptabilityTableModel(results);
		ResultsTable table = new ResultsTable(tableModel);
		
		RankAcceptabilitiesDataset rankData = new RankAcceptabilitiesDataset(results);
		final JFreeChart chart = ChartFactory.createStackedBarChart(
		        "", "Alternative", "Rank Acceptability",
		        rankData, PlotOrientation.VERTICAL, true, true, false);
		
		JFrame frame = new JFrame();
		JPanel panel = new JPanel(new BorderLayout());
		ResultsView view = new ResultsView(frame, "Rank acceptability indices", table, chart);
		panel.add(view.buildPanel(), BorderLayout.CENTER);
		panel.add(bar, BorderLayout.NORTH);
		frame.add(panel);
		
		frame.pack();

		draadje.start();
		
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
