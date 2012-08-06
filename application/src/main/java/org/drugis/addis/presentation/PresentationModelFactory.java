/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Unit;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.TreatmentDefinition;

import com.jgoodies.binding.PresentationModel;

public class PresentationModelFactory {
	private final Map<Object, PresentationModel<?>> d_cache = new	WeakHashMap<Object, PresentationModel<?>>();
	private final Domain d_domain;

	public PresentationModelFactory(final Domain domain) {
		d_domain = domain;
	}

	public <T> LabeledPresentation getLabeledModel(final T obj) {
		try {
			return (LabeledPresentation)getModel(obj);
		} catch (final ClassCastException e) {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> PresentationModel<T> getModel(final T obj) {
		PresentationModel mod = d_cache.get(obj);
		if ((mod != null) && (mod.getBean() == obj)) {
			return mod;
		}

		mod = createModel(obj);
		d_cache.put(obj, mod);
		return mod;
	}

	public void clearCache() {
		d_cache.clear();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private PresentationModel createModel(final Object obj) {
		if (obj instanceof Variable) {
			return new VariablePresentation((Variable)obj,
					d_domain.getStudies((Variable)obj), this);
		}
		if (obj instanceof Unit) {
			return new UnitPresentation((Unit) obj);
		}
		if (obj instanceof Study) {
			return new StudyPresentation((Study) obj, this);
		}
		if (obj instanceof Indication) {
			return new IndicationPresentation((Indication)obj, d_domain.getStudies());
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
		if (obj instanceof DrugTreatment) {
			return new DrugTreatmentPresentation((DrugTreatment)obj);
		}
		if (obj instanceof TreatmentCategorization) {
			return new TreatmentCategorizationPresentation((TreatmentCategorization)obj, d_domain);
		}
		if (obj instanceof Drug) {
			return new DrugPresentation((Drug) obj, d_domain);
		}
		if (obj instanceof TreatmentDefinition) {
			return new TreatmentCategorySetPresentation((TreatmentDefinition) obj, d_domain);
		}
		if (obj instanceof RandomEffectsMetaAnalysis) {
			return new PairWiseMetaAnalysisPresentation((RandomEffectsMetaAnalysis) obj, this);
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
