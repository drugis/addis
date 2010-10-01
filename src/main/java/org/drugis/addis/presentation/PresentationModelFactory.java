/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import java.util.Map;
import java.util.WeakHashMap;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;

import com.jgoodies.binding.PresentationModel;

public class PresentationModelFactory {
	private Map<Object, PresentationModel<?>> d_cache = new	WeakHashMap<Object, PresentationModel<?>>();
	private Domain d_domain;
	
	public PresentationModelFactory(Domain domain) {
		d_domain = domain;
	}
	
	public <T> LabeledPresentation getLabeledModel(T obj) {
		try {
			return (LabeledPresentation)getModel(obj);
		} catch (ClassCastException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> PresentationModel<T> getModel(T obj) {
		PresentationModel mod = d_cache.get(obj);
		if ((mod != null) && (mod.getBean() == obj)) {
			return mod;
		}
	
		mod = createModel(obj);
		d_cache.put(obj, mod);
		return (PresentationModel<T>)mod;
	}
	
	public void clearCache() {
		d_cache.clear();
	}
	
	@SuppressWarnings("unchecked")
	public <T> PresentationModel<T> getCreationModel(T obj) {
		PresentationModel model = createCreationModel(obj);
		if (model != null) {
			return (PresentationModel<T>)model;
		}
		return getModel(obj);
	}
	
	@SuppressWarnings("unchecked")
	private PresentationModel createCreationModel(Object obj) {
		if (obj instanceof OutcomeMeasure) {
			return new OutcomeMeasureCreationPresentation((OutcomeMeasure)obj);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private PresentationModel createModel(Object obj) {
		if (obj instanceof Variable) {
			return new VariablePresentation((Variable)obj,
					d_domain.getStudies((Variable)obj), this);
		}
		if (obj instanceof Study) {
			return new StudyPresentation((Study) obj, this);
		}		
		if (obj instanceof Indication) {
			return new IndicationPresentation((Indication)obj, d_domain.getStudies((Indication)obj));
		}
		if (obj instanceof RelativeEffect) {
			return new RelativeEffectPresentation((RelativeEffect)obj);
		}
		if (obj instanceof RateMeasurement) {
			return new RateMeasurementPresentation((RateMeasurement)obj);
		}
		if (obj instanceof ContinuousMeasurement) {
			return new ContinuousMeasurementPresentation((ContinuousMeasurement)obj);
		}
		if (obj instanceof FrequencyMeasurement) {
			return new FrequencyMeasurementPresentation((FrequencyMeasurement)obj);
		}
		if (obj instanceof Arm) {
			return new BasicArmPresentation((Arm)obj, this);
		}
		if (obj instanceof Drug) {
			Drug d = (Drug) obj;
			return new DrugPresentation(d, d_domain.getStudies(d));
		}
		if (obj instanceof RandomEffectsMetaAnalysis) {
			return new RandomEffectsMetaAnalysisPresentation((RandomEffectsMetaAnalysis) obj, this);
		}
		if (obj instanceof NetworkMetaAnalysis) {
			return new NetworkMetaAnalysisPresentation((NetworkMetaAnalysis) obj, this);
		}
		if (obj instanceof MetaBenefitRiskAnalysis) {
			return new MetaBenefitRiskPresentation((MetaBenefitRiskAnalysis) obj, this);
		}
		if (obj instanceof StudyBenefitRiskAnalysis) {
			return new StudyBenefitRiskPresentation((StudyBenefitRiskAnalysis) obj, this);
		}
		if (obj instanceof Distribution) {
			return new DistributionPresentation((Distribution) obj);
		}
		return new PresentationModel(obj);
	}


}
