/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

package org.drugis.addis.entities.relativeeffect;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.Set;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Measurement;
import org.drugis.common.Interval;

public abstract class AbstractRelativeEffect<T extends Measurement> extends AbstractEntity implements RelativeEffect<T>{

	protected T d_subject;
	protected T d_baseline;

	protected AbstractRelativeEffect(T subject, T baseline) {
		d_subject  = subject;
		d_baseline = baseline;
		connectListeners();
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		connectListeners();
	}	
	
	private void connectListeners() {
		MemberListener listener = new MemberListener();
		getSubject().addPropertyChangeListener(listener);
		getBaseline().addPropertyChangeListener(listener);
	}

	public Integer getSampleSize() {
		return getSubject().getSampleSize() + getBaseline().getSampleSize();
	}
	
	public T getSubject() {
		return d_subject;
	}

	public T getBaseline() {
		return d_baseline;
	}

	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}

	private class MemberListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange(PROPERTY_SAMPLESIZE, null, getSampleSize());
		}		
	}
	
	protected abstract Integer getDegreesOfFreedom();
	
	public boolean isDefined() {
		return getDegreesOfFreedom() > 0;
	}
	
	public AxisType getAxisType() {
		return getDistribution().getAxisType();
	}
	
	public Interval<Double> getConfidenceInterval() {
		if (!isDefined()) {
			return new Interval<Double>(Double.NaN, Double.NaN);
		}

		return new Interval<Double>(getDistribution().getQuantile(0.025), getDistribution().getQuantile(0.975));
	}
}