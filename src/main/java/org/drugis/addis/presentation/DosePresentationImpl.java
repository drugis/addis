/**
 * 
 */
package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.common.Interval;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;

class DosePresentationImpl implements DosePresentationModel {
	private BasicPatientGroup d_pg;
	private ValueHolder d_min;
	private ValueHolder d_max;
	
	public DosePresentationImpl(
			BasicPatientGroupPresentation basicPatientGroupPresentation) {
		d_pg = basicPatientGroupPresentation.getBean();
		d_min = new ValueHolder(getMinDose(d_pg));
		d_max = new ValueHolder(getMaxDose(d_pg));
		
		d_min.addPropertyChangeListener(new DoseChangeListener());
		d_max.addPropertyChangeListener(new DoseChangeListener());
	}

	private double getMaxDose(BasicPatientGroup pg) {
		if (d_pg.getDose() instanceof FlexibleDose) {
			return ((FlexibleDose)d_pg.getDose()).getFlexibleDose().getUpperBound();
		} else if (d_pg.getDose() instanceof FixedDose) {
			return ((FixedDose)d_pg.getDose()).getQuantity();
		}
		return 0.0;
	}

	private double getMinDose(BasicPatientGroup pg) {
		if (d_pg.getDose() instanceof FlexibleDose) {
			return ((FlexibleDose)d_pg.getDose()).getFlexibleDose().getLowerBound();
		} else if (d_pg.getDose() instanceof FixedDose) {
			return ((FixedDose)d_pg.getDose()).getQuantity();
		}
		return 0.0;
	}

	public AbstractValueModel getMaxModel() {
		return d_max;
	}

	public AbstractValueModel getMinModel() {
		return d_min;
	}

	public AbstractValueModel getUnitModel() {
		return null;
	}
	
	private class DoseChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == d_min) {
				if ((Double)evt.getNewValue() > d_max.doubleValue()) {
					d_max.setValue(evt.getNewValue());
				}
			}
			if (d_min.doubleValue() == d_max.doubleValue()) {
				d_pg.setDose(new FixedDose(d_min.doubleValue(), d_pg.getDose().getUnit()));
			} else {
				Interval<Double> interval = new Interval<Double>(d_min.doubleValue(), d_max.doubleValue());
				d_pg.setDose(new FlexibleDose(interval , d_pg.getDose().getUnit()));
			}
		}
	}
}