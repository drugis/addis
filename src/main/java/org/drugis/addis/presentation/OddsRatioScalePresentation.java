package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.analysis.OddsRatioToClinicalConverter;
import org.drugis.common.Interval;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

import fi.smaa.jsmaa.model.ScaleCriterion;


@SuppressWarnings("serial")
public class OddsRatioScalePresentation extends PresentationModel<ScaleCriterion> {
	public abstract class ScaleConvertingValueModel extends AbstractValueModel {
		public ScaleConvertingValueModel() {
			this(true, false);
		}
		
		public ScaleConvertingValueModel(final boolean listenScale, final boolean listenDirection) {
			getBean().addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(ScaleCriterion.PROPERTY_SCALE) && listenScale) {
						fireValueChange(null, getValue());
					} else if (evt.getPropertyName().equals(ScaleCriterion.PROPERTY_ASCENDING) && listenDirection) {
						fireValueChange(null, getValue());
					}
				}
			});
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("Not able to set");
		}
	}


	public final class NNTLabelValueModel extends ScaleConvertingValueModel {
		private NNTLabelValueModel() {
			super(false, true);
		}

		public Object getValue() {
			return getNumberNeededToTreatLabel();
		}
	}

	public final class NNTValueModel extends ScaleConvertingValueModel {
		public Object getValue() {
			return getNumberNeededToTreat();
		}
	}

	public final class RiskDifferenceValueModel extends
			ScaleConvertingValueModel {
		public Object getValue() {
			return getRiskDifference();
		}
	}

	public final class RiskValueModel extends ScaleConvertingValueModel {
		public Object getValue() {
			return getRisk();
		}
	}

	public final class OddsRatioValueModel extends ScaleConvertingValueModel {
		public Object getValue() {
			return getOddsRatio();
		}
	}
	
	public static final String PROPERTY_ODDS_RATIO = "oddsRatio";
	public static final String PROPERTY_RISK = "risk";
	public static final String PROPERTY_RISK_DIFFERENCE = "riskDifference";
	public static final String PROPERTY_NNT = "numberNeededToTreat";
	public static final String PROPERTY_NNT_LABEL = "numberNeededToTreatLabel";
	
	private final OddsRatioToClinicalConverter d_converter;

	public OddsRatioScalePresentation(ScaleCriterion criterion, OddsRatioToClinicalConverter converter) {
		super(criterion);
		d_converter = converter;
	}
	
	@Override
	public AbstractValueModel getModel(String property) {
		if (property.equals(PROPERTY_ODDS_RATIO)) {	
			return new OddsRatioValueModel();
		} else if (property.equals(PROPERTY_RISK)) {	
			return new RiskValueModel();
		} else if (property.equals(PROPERTY_RISK_DIFFERENCE)) {	
			return new RiskDifferenceValueModel();
		} else if (property.equals(PROPERTY_NNT)) {	
			return new NNTValueModel();
		} else if (property.equals(PROPERTY_NNT_LABEL)) {	
			return new NNTLabelValueModel();
		}
		return super.getModel(property);
	}
	
	private Interval<Double> getOddsRatio(){
		return d_converter.getOddsRatio(convertInterval(getBean().getScale()));
	}
	
	private Interval<Double> getRisk(){
		return d_converter.getRisk(convertInterval(getBean().getScale()));
	}

	private double getRiskDifference() {
		return d_converter.getRiskDifference(convertInterval(getBean().getScale()));
	}
	
	private double getNumberNeededToTreat() {
		return d_converter.getNumberNeededToTreat(convertInterval(getBean().getScale()));
	}
	
	private String getNumberNeededToTreatLabel() {
		return getBean().getAscending() ? "NNT" : "NNH";
	}
	
	private static Interval<Double> convertInterval(fi.smaa.jsmaa.model.Interval interval) {
		return new Interval<Double>(interval.getStart(), interval.getEnd());
	}
}
