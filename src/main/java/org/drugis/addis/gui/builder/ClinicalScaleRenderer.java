package org.drugis.addis.gui.builder;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.analysis.OddsRatioToClinicalConverter;
import org.drugis.addis.presentation.BenefitRiskPM;
import org.drugis.addis.presentation.OddsRatioScalePresentation;
import org.drugis.common.gui.NumberAndIntervalFormat;
import org.drugis.common.gui.OneWayObjectFormat;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;

import fi.smaa.jsmaa.gui.views.ScaleRenderer;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.ScaleCriterion;

public class ClinicalScaleRenderer implements ScaleRenderer {

	private final BenefitRiskPM d_pm;

	public ClinicalScaleRenderer(BenefitRiskPM pm) {
		d_pm = pm;
	}

	public JComponent getScaleComponent(Criterion c) {
		if (c instanceof ScaleCriterion) {
			ScaleCriterion criterion = (ScaleCriterion)c;
			OutcomeMeasure outcome = d_pm.getOutcomeMeasureForCriterion(criterion);
			if (outcome.getType() == Variable.Type.RATE) {
				OddsRatioToClinicalConverter converter = new OddsRatioToClinicalConverter(d_pm.getBean(), outcome);
				OddsRatioScalePresentation cpm = new OddsRatioScalePresentation(criterion, converter);
				JPanel panel = buildOddsRatioClinicalView(cpm);
				return panel;
			} else {
				PresentationModel<ScaleCriterion> cpm = new PresentationModel<ScaleCriterion>((ScaleCriterion) c);
				JLabel orLabel = new JLabel("MD: ");
				JLabel scaleLabel = BasicComponentFactory.createLabel(cpm.getModel(ScaleCriterion.PROPERTY_SCALE), 
						new fi.smaa.jsmaa.gui.IntervalFormat());
				JPanel panel = new JPanel(new FlowLayout());
				panel.add(orLabel);
				panel.add(scaleLabel);
				return panel;
			}
		}
		return new JLabel("NA");
	}

	private JPanel buildOddsRatioClinicalView(OddsRatioScalePresentation cpm) {
		JPanel panel = new JPanel(new FlowLayout());
		addPropertyToPanel(cpm, panel, "OR: ", OddsRatioScalePresentation.PROPERTY_ODDS_RATIO);
		addPropertyToPanel(cpm, panel, "Risk: ", OddsRatioScalePresentation.PROPERTY_RISK);
		addPropertyToPanel(cpm, panel, "RD: ", OddsRatioScalePresentation.PROPERTY_RISK_DIFFERENCE);
		
		JLabel label = BasicComponentFactory.createLabel(cpm.getModel(OddsRatioScalePresentation.PROPERTY_NNT_LABEL));
		JLabel valueLabel = BasicComponentFactory.createLabel(cpm.getModel(OddsRatioScalePresentation.PROPERTY_NNT),
				new NumberAndIntervalFormat());
		panel.add(label);
		panel.add(valueLabel);
		
		return panel;
	}

	private void addPropertyToPanel(OddsRatioScalePresentation cpm, JPanel panel, String text, String property) {
		JLabel label = new JLabel(text);
		JLabel valueLabel = BasicComponentFactory.createLabel(cpm.getModel(property),
				new NumberAndIntervalFormat());
		panel.add(label);
		panel.add(valueLabel);
	}
}