package org.drugis.addis.entities.analysis;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.relativeeffect.Distribution;

public interface MeasurementSource<Alternative extends Entity> {
	public interface Listener {
		public void notifyMeasurementsChanged();
	}
	public Distribution getMeasurement(Alternative a, OutcomeMeasure criterion);
	
	public void addMeasurementsChangedListener(Listener propertyChangeListener);
}
