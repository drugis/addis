/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.PooledPatientGroup;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.RelativeEffectRate;

import com.jgoodies.binding.PresentationModel;

public class PresentationModelFactory {
	private Map<Object, PresentationModel<?>> d_cache = new
		HashMap<Object, PresentationModel<?>>();
	private Domain d_domain;
	
	public PresentationModelFactory(Domain domain) {
		d_domain = domain;
	}
	
	public <T> LabeledPresentationModel getLabeledModel(T obj) {
		try {
			return (LabeledPresentationModel)getModel(obj);
		} catch (ClassCastException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> PresentationModel<T> getModel(T obj) {
		PresentationModel mod = d_cache.get(obj);
		if (mod != null) {
			return mod;
		}
		mod = createModel(obj);
		d_cache.put(obj, mod);
		return (PresentationModel<T>)mod;
	}

	@SuppressWarnings("unchecked")
	private PresentationModel createModel(Object obj) {
		if (obj instanceof MetaStudy) {
			return new MetaStudyPresentationModel((MetaStudy) obj);
		}
		if (obj instanceof Indication) {
			return new IndicationPresentation((Indication)obj, d_domain.getStudies((Indication)obj));
		}
		if (obj instanceof RelativeEffectRate) {
			return new RelativeEffectRatePresentation((RelativeEffectRate)obj, this);
		}
		if (obj instanceof RateMeasurement) {
			return new RateMeasurementPresentation((RateMeasurement)obj);
		}
		if (obj instanceof ContinuousMeasurement) {
			return new ContinuousMeasurementPresentation((ContinuousMeasurement)obj);
		}
		if (obj instanceof RateMeasurement) {
			return new RateMeasurementPresentation((RateMeasurement)obj);
		}
		if (obj instanceof BasicPatientGroup) {
			return new BasicPatientGroupPresentation((BasicPatientGroup)obj);
		}
		if (obj instanceof PooledPatientGroup) {
			return new PooledPatientGroupPresentation((PooledPatientGroup)obj);
		}
		if (obj instanceof Drug) {
			Drug d = (Drug) obj;
			return new DrugPresentationModel(d, d_domain.getStudies(d));
		}
		return new PresentationModel(obj);
	}
}
