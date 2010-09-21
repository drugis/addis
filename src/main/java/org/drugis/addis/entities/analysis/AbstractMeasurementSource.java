package org.drugis.addis.entities.analysis;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.relativeeffect.Distribution;

public abstract class AbstractMeasurementSource<Alternative extends Entity> implements MeasurementSource<Alternative> {
	List<Listener> d_listeners = new ArrayList<Listener>(); 

	public void addMeasurementsChangedListener(Listener l) {
		d_listeners.add(l);
	}
	
	protected void notifyListeners() {
		for(Listener l : d_listeners) {
			l.notifyMeasurementsChanged();
		}
	}

	abstract public Distribution getMeasurement(Alternative a, OutcomeMeasure criterion);
	
}