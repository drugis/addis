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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.common.Interval;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

import fi.smaa.jsmaa.model.ScaleCriterion;


@SuppressWarnings("serial")
public class RiskScalePresentation extends PresentationModel<ScaleCriterion> {
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
		NNTLabelValueModel() {
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
	
	public static final String PROPERTY_RISK = "risk";
	public static final String PROPERTY_RISK_DIFFERENCE = "riskDifference";
	public static final String PROPERTY_NNT = "numberNeededToTreat";
	public static final String PROPERTY_NNT_LABEL = "numberNeededToTreatLabel";
	
	public RiskScalePresentation(ScaleCriterion criterion) {
		super(criterion);
	}
	
	@Override
	public AbstractValueModel getModel(String property) {
		if (property.equals(PROPERTY_RISK)) {	
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
	
	protected Interval<Double> getRisk(){
		return convertInterval(getBean().getScale());
	}

	protected double getRiskDifference() {
		return getRisk().getUpperBound() - getRisk().getLowerBound();
	}
	
	protected double getNumberNeededToTreat() {
		return 1D / getRiskDifference();
	}
	
	protected String getNumberNeededToTreatLabel() {
		return getBean().getAscending() ? "NNT" : "NNH";
	}
	
	protected static Interval<Double> convertInterval(fi.smaa.jsmaa.model.Interval interval) {
		return new Interval<Double>(interval.getStart(), interval.getEnd());
	}
}
