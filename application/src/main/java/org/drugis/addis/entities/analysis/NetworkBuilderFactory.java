/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.entities.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.bidimap.TreeBidiMap;
import org.apache.commons.lang.StringUtils;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.mtc.data.DataType;
import org.drugis.mtc.model.ContinuousNetworkBuilder;
import org.drugis.mtc.model.DichotomousNetworkBuilder;
import org.drugis.mtc.model.NetworkBuilder;
import org.drugis.mtc.model.Treatment;

public class NetworkBuilderFactory {
	static final class DescriptionTransformer implements Transformer<TreatmentDefinition, String> {
		@Override
		public String transform(TreatmentDefinition input) {
			return input.getLabel();
		}
	}

	static final class NameTransformer implements Transformer<TreatmentDefinition, String> {
		private final BidiMap<Category, String> nameLookup = new TreeBidiMap<Category, String>();

		@Override
		public String transform(TreatmentDefinition input) {
			List<String> names = new ArrayList<String>();
			for (Category category : input.getContents()) {
				names.add(getCleanName(category));
			}
			return StringUtils.join(names, "_");
		}

		private String getCleanName(Category category) {
			if (!nameLookup.containsKey(category)) {
				insertUniqueName(category);
			}
			return nameLookup.get(category);
		}

		private void insertUniqueName(Category category) {
			String sanitized = sanitize(category.getLabel());
			String name = sanitized;
			int i = 1;
			while (nameLookup.containsValue(name)) {
				name = sanitized + ++i;
			}
			nameLookup.put(category, name);
		}

		private String sanitize(String dirtyString) {
			return dirtyString.replaceAll("[^a-zA-Z0-9]", "");
		}
	}

	private static final Transformer<TreatmentDefinition, String> s_descTransform = new DescriptionTransformer();
	private static final Transformer<TreatmentDefinition, String> s_transform = new NameTransformer();

	final static class NetworkBuilderStub extends NetworkBuilder<TreatmentDefinition> {
		NetworkBuilderStub() {
			super(s_transform, s_descTransform, DataType.NONE);
		}

		public Treatment addTreatment(TreatmentDefinition t) {
			return makeTreatment(t);
		}
	}

	public static NetworkBuilder<TreatmentDefinition> createBuilderStub(List<TreatmentDefinition> definitions) {
		NetworkBuilderStub builder = new NetworkBuilderStub();
		for(TreatmentDefinition d : definitions) {
			builder.addTreatment(d);
		}
		return builder;
	}

	public static NetworkBuilder<TreatmentDefinition> createBuilder(OutcomeMeasure outcomeMeasure, List<Study> studies, List<TreatmentDefinition> definitions, Map<Study, Map<TreatmentDefinition, Arm>> armMap) {
		if (isContinuous(outcomeMeasure)) {
			return createContinuousBuilder(outcomeMeasure, studies, definitions, armMap);
		} else {
			return createRateBuilder(outcomeMeasure, studies, definitions, armMap);
		}
	}

	private static NetworkBuilder<TreatmentDefinition> createContinuousBuilder(
			OutcomeMeasure outcomeMeasure,
			List<Study> studies,
			List<TreatmentDefinition> definitions,
			Map<Study, Map<TreatmentDefinition, Arm>> armMap) {
		ContinuousNetworkBuilder<TreatmentDefinition> builder = new ContinuousNetworkBuilder<TreatmentDefinition>(s_transform, s_descTransform);
		for(Study s : studies){
			for (TreatmentDefinition d : definitions) {
				if (armMap.get(s).containsKey(d)) {
					BasicContinuousMeasurement cm = (BasicContinuousMeasurement) s.getMeasurement(outcomeMeasure, armMap.get(s).get(d));
					builder.add(s.getName(), d, cm.getMean(), cm.getStdDev(), cm.getSampleSize());
				}
        	}
        }
		return builder;
	}

	private static NetworkBuilder<TreatmentDefinition> createRateBuilder(OutcomeMeasure outcomeMeasure, List<Study> studies, List<TreatmentDefinition> definitions, Map<Study, Map<TreatmentDefinition, Arm>> armMap) {
		DichotomousNetworkBuilder<TreatmentDefinition> builder = new DichotomousNetworkBuilder<TreatmentDefinition>(s_transform, s_descTransform);
		for(Study s : studies){
			for (TreatmentDefinition d : definitions) {
				if (armMap.get(s).containsKey(d)) {
					BasicRateMeasurement brm = (BasicRateMeasurement) s.getMeasurement(outcomeMeasure, armMap.get(s).get(d));
					builder.add(s.getName(), d, brm.getRate(), brm.getSampleSize());
				}
        	}
        }
		return builder;
	}

	public static boolean isContinuous(OutcomeMeasure outcome) {
		if (outcome.getVariableType() instanceof RateVariableType) {
			return false;
		} else if (outcome.getVariableType() instanceof ContinuousVariableType) {
			return true;
		} else {
			throw new IllegalStateException("Unexpected VariableType: " + outcome.getVariableType());
		}
	}
}
