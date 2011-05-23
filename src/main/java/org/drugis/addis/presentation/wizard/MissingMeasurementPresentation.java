package org.drugis.addis.presentation.wizard;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class MissingMeasurementPresentation {

	private BasicMeasurement d_m;
	ValueModel d_missingModel = new AbstractValueModel() {
		private static final long serialVersionUID = 1897234897896897266L;

		@Override
		public Object getValue() {
			return d_study.getMeasurement(d_v, d_a) == null;
		}

		@Override
		public void setValue(Object newValue) {
			Object oldValue = getValue();
			if (newValue.equals(Boolean.TRUE)) {
				d_study.getMeasurements().remove(new Study.MeasurementKey(d_v, d_a));
			} else {
				d_study.getMeasurements().put(new Study.MeasurementKey(d_v, d_a), d_m);
			}
			fireValueChange(oldValue, newValue);
		}
	};
	private final Study d_study;
	private final Variable d_v;
	private final Arm d_a;
	
	public MissingMeasurementPresentation(final Study s, final Variable v, final Arm a) {
		d_study = s;
		d_v = v;
		d_a = a;
		d_m = d_study.getMeasurement(d_v, d_a) == null ? d_study.buildDefaultMeasurement(d_v, d_a) : d_study.getMeasurement(d_v, d_a);
	}
	
	public BasicMeasurement getMeasurement() {
		return d_m;
	}
	
	public ValueModel getMissingModel() {
		return d_missingModel;
	}
}
