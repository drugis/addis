package org.drugis.addis.gui.builder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JProgressBar;

import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.LyndOBrienChartFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.BuildViewWhenReadyComponent;
import org.drugis.addis.presentation.AbstractBenefitRiskPresentation;
import org.drugis.addis.presentation.LyndOBrienPresentation;
import org.drugis.common.gui.ViewBuilder;
import org.jfree.chart.ChartPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class LyndOBrienView implements ViewBuilder {
	LyndOBrienPresentation<?,?> d_pm;
	AbstractBenefitRiskPresentation<?, ?> d_BRpm;
	
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
				"p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();

		builder.addSeparator("Benefit-risk plane");
		builder.add(createWaiter(new chartBuilder()), cc.xy(1,3));
		builder.add(AuxComponentFactory.createNoteField("Results of Monte Carlo simulations based on the difference-distributions of" +
				" the alternatives and criteria. Results in the NW quadrant indicate that that the alternative is better and" +
				" results in the SE quadrant indicate that the baseline drug is better."), cc.xy(1,5));
		return builder.getPanel();
	}

	private class chartBuilder implements ViewBuilder {
		
		public JComponent buildPanel() {
			FormLayout layout = new FormLayout(
					"pref:grow:fill",
					"p, 3dlu, p");
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
	
	protected BuildViewWhenReadyComponent createWaiter(ViewBuilder builder) {
		
		return new BuildViewWhenReadyComponent(builder, d_BRpm.getMeasurementsReadyModel(), "");
	}

}
