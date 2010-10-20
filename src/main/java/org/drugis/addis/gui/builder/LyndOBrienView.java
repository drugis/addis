package org.drugis.addis.gui.builder;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.LyndOBrienChartFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.BuildViewWhenReadyComponent;
import org.drugis.addis.presentation.AbstractBenefitRiskPresentation;
import org.drugis.addis.presentation.LyndOBrienPresentation;
import org.drugis.common.gui.ChildComponenentHeightPropagater;
import org.drugis.common.gui.ViewBuilder;
import org.drugis.common.gui.task.TaskProgressBar;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class LyndOBrienView implements ViewBuilder {
	LyndOBrienPresentation<?,?> d_pm;
	AbstractBenefitRiskPresentation<?, ?> d_BRpm;
	private JPanel d_panel;
	
	public LyndOBrienView(AbstractBenefitRiskPresentation<?,?> pm, Main main) {
		d_pm = pm.getLyndOBrienPresentation();
		d_BRpm = pm;
		
		if (d_BRpm.getMeasurementsReadyModel().getValue()) {
			d_pm.startLyndOBrien();
		}
		
		d_BRpm.getMeasurementsReadyModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				d_pm.startLyndOBrien();
			}
		});
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();

		builder.addSeparator("Benefit-risk plane");
		builder.add(createWaiter(new ScatterplotBuilder()), cc.xy(1,3));

		String alternativeName = new String();
		String baselineName = new String();
		if(d_BRpm.getBean() instanceof StudyBenefitRiskAnalysis) {
			baselineName = ((Arm) d_BRpm.getBean().getAlternatives().get(0)).getDrug().toString();
			alternativeName = ((Arm) d_BRpm.getBean().getAlternatives().get(1)).getDrug().toString();
		} else if (d_BRpm.getBean() instanceof MetaBenefitRiskAnalysis) {
			baselineName = d_BRpm.getBean().getAlternatives().get(0).toString();
			alternativeName = d_BRpm.getBean().getAlternatives().get(1).toString();
		}
		builder.add(AuxComponentFactory.createNoteField("Results of Monte Carlo simulations based on the difference-distributions of" +
				" the alternatives and criteria. Results in the NW quadrant indicate that " + 
				alternativeName +" is better and" +
				" results in the SE quadrant indicate that "+ baselineName  + " is better."), cc.xy(1,5));
		builder.addSeparator("Benefit-Risk Aceptability curve", cc.xy(1, 7));
		builder.add(createWaiter(new PvalueplotBuilder()), cc.xy(1,9));
		builder.add(AuxComponentFactory.createNoteField("Probability for a given acceptability threshold " +
				"\u03BC that " + alternativeName + " is superior to " + baselineName +". Indicates the" +
				" proportion of datapoints in the Benefit-Risk" +
				" plane that lie below the line y = \u03BC x"), cc.xy(1,11));
		d_panel = builder.getPanel();
		ChildComponenentHeightPropagater.attachToContainer(d_panel);
		return d_panel;
	}

	private class ScatterplotBuilder implements ViewBuilder {
		
		public JComponent buildPanel() {
			FormLayout layout = new FormLayout(
					"pref:grow:fill",
					"p, 3dlu, p, 3dlu");
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc =  new CellConstraints();
			JProgressBar bar = new TaskProgressBar(d_pm.getTask());
			builder.add(bar,cc.xy(1, 1));
			draggableMuChartPanel component = new draggableMuChartPanel(LyndOBrienChartFactory.buildScatterPlot(d_pm.getModel()));
			builder.add(component, cc.xy(1,3));
			return builder.getPanel();
		}
	}
	
	private class PvalueplotBuilder implements ViewBuilder {
		public JComponent buildPanel() {
			return new ChartPanel(LyndOBrienChartFactory.buildRiskAcceptabilityCurve(d_pm.getModel()));
		}
		
	}
	
	@SuppressWarnings("serial")
	private class draggableMuChartPanel extends ChartPanel{

		public draggableMuChartPanel(JFreeChart chart) {
			super(chart);
		}
		
		public void mouseDragged(MouseEvent e){
			Point point = e.getPoint();
			Point2D.Double start, end;

			ChartRenderingInfo info = getChartRenderingInfo();
			Rectangle2D dataArea = info.getPlotInfo().getDataArea();
	        Point2D p = translateScreenToJava2D(
	                new Point(point.x, point.y));				
	        XYPlot plot = getChart().getXYPlot();
	        RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
	        RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
	        ValueAxis domainAxis = plot.getDomainAxis();
	        ValueAxis rangeAxis = plot.getRangeAxis();
		    double chartX = domainAxis.java2DToValue(p.getX(), dataArea,
	                domainAxisEdge);
	        double chartY = rangeAxis.java2DToValue(p.getY(), dataArea,
	                rangeAxisEdge);	
	        
			if (chartX > 0 && chartY > 0) {
				
				double mu = chartY / chartX;
				Range d = plot.getDomainAxis().getRange();
				Range r = plot.getRangeAxis().getRange();
				double lowerX = d.getLowerBound();
				double upperX = d.getUpperBound();
				double lowerY = r.getLowerBound();
				double upperY = r.getUpperBound();
				
				end = new Point2D.Double(Math.min(upperY / mu, upperX), Math.min(mu * upperX, upperY));
//				end = new Point2D.Double(upperY / mu, upperY);
//				if(end.x > upperX) {
//					end.x = upperX;
//					end.y = mu * upperX;
//				}

				start = new Point2D.Double(Math.max(lowerY / mu, lowerX), Math.max(mu * lowerX, lowerY));
//				if(start.x < lowerX) {
//					start.x = lowerX;
//					start.y = ;
//				}


				plot.addAnnotation(new XYLineAnnotation(start.x, start.y,end.x, end.y));
			}
		}
	};
	
	
	protected BuildViewWhenReadyComponent createWaiter(ViewBuilder builder) {
		
		return new BuildViewWhenReadyComponent(builder, d_BRpm.getMeasurementsReadyModel(), "");
	}

}
