package org.drugis.addis.gui.builder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.LyndOBrienChartFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.BuildViewWhenReadyComponent;
import org.drugis.addis.presentation.AbstractBenefitRiskPresentation;
import org.drugis.addis.presentation.LyndOBrienPresentation;
import org.drugis.common.gui.ChildComponenentHeightPropagater;
import org.drugis.common.gui.ViewBuilder;
import org.jfree.chart.ChartPanel;

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
			JProgressBar bar = new JProgressBar();
			bar.setStringPainted(true);
			d_pm.attachProgBar(bar);
			builder.add(bar,cc.xy(1, 1));
			builder.add(new ChartPanel(LyndOBrienChartFactory.buildScatterPlot(d_pm.getModel())), cc.xy(1,3));
			return builder.getPanel();
		}
	}
	
	private class PvalueplotBuilder implements ViewBuilder {
		public JComponent buildPanel() {
			return new ChartPanel(LyndOBrienChartFactory.buildRiskAcceptabilityCurve(d_pm.getModel()));
		}
		
	}
	
	
	protected BuildViewWhenReadyComponent createWaiter(ViewBuilder builder) {
		
		return new BuildViewWhenReadyComponent(builder, d_BRpm.getMeasurementsReadyModel(), "");
	}

}
