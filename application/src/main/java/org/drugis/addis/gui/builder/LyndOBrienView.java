/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.gui.builder;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.LyndOBrienChartFactory;
import org.drugis.addis.presentation.AbstractBenefitRiskPresentation;
import org.drugis.addis.presentation.LyndOBrienPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.common.gui.BuildViewWhenReadyComponent;
import org.drugis.common.gui.ViewBuilder;
import org.drugis.common.gui.task.TaskProgressBar;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
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
	private JLabel d_pvalueLabel;
	
	public LyndOBrienView(AbstractBenefitRiskPresentation<?,?> pm, AddisWindow mainWindow) {
		d_pm = pm.getLyndOBrienPresentation();
		d_BRpm = pm;
		d_pvalueLabel = new JLabel();
	}

	public JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, " +
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc =  new CellConstraints();

		builder.addSeparator("Benefit-risk plane");
		JScrollPane scatter = new JScrollPane(createWaiter(new ScatterplotBuilder()));
		scatter.setViewportBorder(null);
		builder.add(scatter, cc.xy(1,3));

		String alternativeName = "";
		String baselineName = "";
		Object baseline = d_BRpm.getBean().getBaseline();
		Object alternative = d_BRpm.getBean().getNonBaselineAlternatives().get(0);
		if(d_BRpm.getBean() instanceof StudyBenefitRiskAnalysis) {
			Study s = ((StudyBenefitRiskAnalysis) d_BRpm.getBean()).getStudy();
			Arm baseArm = (Arm)baseline;
			Arm altArm = (Arm)alternative;
			baselineName = s.getTreatment(baseArm).getLabel(); // FIXME: use TreatmentActivity?
			alternativeName = s.getTreatment(altArm).getLabel();
		} else if (d_BRpm.getBean() instanceof MetaBenefitRiskAnalysis) {
			baselineName = ((DrugSet)baseline).getLabel();
			alternativeName = ((DrugSet)alternative).getLabel();
		}
		
		builder.add(AuxComponentFactory.createHtmlField("Results of Monte Carlo simulations based on the difference-distributions of" +
				" the alternatives and criteria. Results in the NW quadrant indicate that " + 
				baselineName +" is better and" +
				" results in the SE quadrant indicate that "+ alternativeName  + " is better."), cc.xy(1,7));
		builder.addSeparator("Benefit-Risk Aceptability curve", cc.xy(1, 9));
		
		JScrollPane pvalue = new JScrollPane(createWaiter(new PvalueplotBuilder()));
		pvalue.setViewportBorder(null);
		builder.add(pvalue, cc.xy(1,11));
		
		builder.add(AuxComponentFactory.createHtmlField("Probability for a given acceptability threshold " +
				"\u03BC that " + alternativeName + " is superior to " + baselineName + ". Indicates the" +
				" proportion of datapoints in the Benefit-Risk" +
				" plane that lie below the line y = \u03BC x"), cc.xy(1,13));
		
		d_panel = builder.getPanel();
		
		return d_panel;
	}

	private class ScatterplotBuilder implements ViewBuilder, TaskListener {

		public JComponent buildPanel() {
			FormLayout layout = new FormLayout(
					"pref",
					"p, 3dlu, p, 3dlu, p");
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc =  new CellConstraints();
			
			JProgressBar bar = new TaskProgressBar(d_pm.getProgressModel());
			builder.add(bar,cc.xy(1, 1));
			
			final draggableMuChartPanel component = new draggableMuChartPanel(LyndOBrienChartFactory.buildScatterPlot(d_pm.getModel()));

			d_pm.getModel().getTask().addTaskListener(this);
			component.addListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					Double mu = component.getMu();
					setMuAndPValueLabel(mu);
					
				}
			});

			d_pm.getModel().getTask().addTaskListener(component);
			
			builder.add(component, cc.xy(1,3));
			setMuAndPValueLabel(1.0);
			builder.add(d_pvalueLabel, cc.xy(1,5));
			
			
			return builder.getPanel();
		}

		public void taskEvent(TaskEvent event) {
			if(event.getType() == EventType.TASK_PROGRESS || event.getType() == EventType.TASK_FINISHED) {
				Double mu = 1.0;
				setMuAndPValueLabel(mu);
			}
		}

		private void setMuAndPValueLabel(Double mu) {
			DecimalFormat df = new DecimalFormat("0.00");
			d_pvalueLabel.setText("\u03BC = " + df.format(mu) + ", P-value: " + df.format(d_pm.getModel().getPValue(mu)));
		}
	}
	
	private class PvalueplotBuilder implements ViewBuilder {
		public JComponent buildPanel() {
			return new ChartPanel(LyndOBrienChartFactory.buildRiskAcceptabilityCurve(d_pm.getModel()));
		}
		
	}
	
	@SuppressWarnings("serial")
	private class draggableMuChartPanel extends ChartPanel implements TaskListener {

		private XYAnnotation d_prevAnnotation = null;
		private ModifiableHolder<Double> d_mu;

		public draggableMuChartPanel(JFreeChart chart) {
			super(chart);
			d_mu = new ModifiableHolder<Double>(1.0);
			drawMuLine(chart.getXYPlot(), d_mu.getValue());
		}

		public void addListener(PropertyChangeListener l) {
			d_mu.addValueChangeListener(l);
		}
		
		Double getMu() {
			return d_mu.getValue();
		}

		private Point2D.Double convertToChartCoordinates(Point point) {
			ChartRenderingInfo info = getChartRenderingInfo();
			Rectangle2D dataArea = info.getPlotInfo().getDataArea();
	        Point2D p = translateScreenToJava2D(
	                new Point(point.x, point.y));				
	        XYPlot plot = getChart().getXYPlot();
	        RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
	        RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
	        ValueAxis domainAxis = plot.getDomainAxis();
	        ValueAxis rangeAxis = plot.getRangeAxis();
		    return new Point2D.Double(domainAxis.java2DToValue(p.getX(), dataArea,
	                domainAxisEdge), rangeAxis.java2DToValue(p.getY(), dataArea,
	    	                rangeAxisEdge));
		}
		
		public void mouseDragged(MouseEvent e){
			Point point = e.getPoint();
			Point2D.Double chartXY = convertToChartCoordinates(point);
			
	        // if the mouse is in the SW or NE quadrant, change mu so that it is drawn through the mouse position
	        if ((chartXY.x > 0 && chartXY.y > 0) ||
	        	(chartXY.x < 0 && chartXY.y < 0)) {
				double mu = chartXY.y / chartXY.x;
				drawMuLine(getChart().getXYPlot(), mu);
			}
		}

		private void drawMuLine(XYPlot plot, double mu) {
			Point2D.Double start;
			Point2D.Double end;
			d_mu.setValue(mu);
			Range d = plot.getDomainAxis().getRange();
			Range r = plot.getRangeAxis().getRange();
			double lowerX = d.getLowerBound();
			double upperX = d.getUpperBound();
			double lowerY = r.getLowerBound();
			double upperY = r.getUpperBound();
			
			end = new Point2D.Double(Math.min(upperY / mu, upperX), Math.min(mu * upperX, upperY));
			start = new Point2D.Double(Math.max(lowerY / mu, lowerX), Math.max(mu * lowerX, lowerY));

			if(d_prevAnnotation != null) {
				plot.removeAnnotation(d_prevAnnotation);
			}
			d_prevAnnotation = new XYLineAnnotation(start.x, start.y,end.x, end.y);
			plot.addAnnotation(d_prevAnnotation);
		}

		public void taskEvent(TaskEvent event) {
			if(event.getType() == EventType.TASK_PROGRESS) {
				drawMuLine(getChart().getXYPlot(), d_mu.getValue());
			}

		}
	};
	
	
	protected BuildViewWhenReadyComponent createWaiter(ViewBuilder builder) {
		return new BuildViewWhenReadyComponent(builder, d_BRpm.getMeasurementsReadyModel(), SMAAView.WAITING_MESSAGE);
	}

}
