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
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Study;
import org.drugis.mtc.ContinuousNetworkBuilder;
import org.drugis.mtc.DichotomousNetworkBuilder;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.data.DataType;
import org.drugis.mtc.model.Treatment;

public class NetworkBuilderFactory {
	static final class DescriptionTransformer implements Transformer<DrugSet, String> {
		@Override
		public String transform(DrugSet input) {
			return input.getLabel();
		}
	}

	static final class NameTranformer implements Transformer<DrugSet, String> {
		private final BidiMap<Drug, String> nameLookup = new TreeBidiMap<Drug, String>();

		@Override
		public String transform(DrugSet input) {
			List<String> names = new ArrayList<String>();
			for (Drug drug : input.getContents()) {
				names.add(getCleanName(drug));
			}
			return StringUtils.join(names, "_");
		}

		private String getCleanName(Drug drug) {
			if (!nameLookup.containsKey(drug)) {
				insertUniqueName(drug);
			}
			return nameLookup.get(drug);
		}

		private void insertUniqueName(Drug drug) {
			String sanitized = sanitize(drug.getName());
			String name = sanitized;
			int i = 1;
			while (nameLookup.containsValue(name)) {
				name = sanitized + ++i;
			}
			nameLookup.put(drug, name);
		}

		private String sanitize(String dirtyString) {
			return dirtyString.replaceAll("[^a-zA-Z0-9]", "");
		}
	}
	
	private static final Transformer<DrugSet, String> s_descTransform = new DescriptionTransformer();
	private static final Transformer<DrugSet, String> s_transform = new NameTranformer();

	final static class NetworkBuilderStub extends NetworkBuilder<DrugSet> {
		NetworkBuilderStub() {
			super(s_transform, s_descTransform, DataType.NONE);
		}

		public Treatment addTreatment(DrugSet t) {
			return makeTreatment(t);
		}
	}
	
	public static NetworkBuilder<DrugSet> createBuilderStub(List<DrugSet> drugs) {
		NetworkBuilderStub builder = new NetworkBuilderStub();
		for(DrugSet s : drugs) { 
			builder.addTreatment(s);
		}
		return builder;
	}

	public static NetworkBuilder<DrugSet> createBuilder(OutcomeMeasure outcomeMeasure, List<Study> studies, List<DrugSet> drugs, Map<Study, Map<DrugSet, Arm>> armMap) {
		if (isContinuous(outcomeMeasure)) {
			return createContinuousBuilder(outcomeMeasure, studies, drugs, armMap);
		} else {
			return createRateBuilder(outcomeMeasure, studies, drugs, armMap);
		}
	}
	
	private static NetworkBuilder<DrugSet> createContinuousBuilder(OutcomeMeasure outcomeMeasure, List<Study> studies, List<DrugSet> drugs, Map<Study, Map<DrugSet, Arm>> armMap) {
		ContinuousNetworkBuilder<DrugSet> builder = new ContinuousNetworkBuilder<DrugSet>(s_transform, s_descTransform);
		for(Study s : studies){
			for (DrugSet d : drugs) {
				if (armMap.get(s).containsKey(d)) {
					BasicContinuousMeasurement cm = (BasicContinuousMeasurement) s.getMeasurement(outcomeMeasure, armMap.get(s).get(d));
					builder.add(s.getName(), s.getDrugs(armMap.get(s).get(d)), cm.getMean(), cm.getStdDev(), cm.getSampleSize());
				}
        	}
        }
		return builder;
	}

	private static NetworkBuilder<DrugSet> createRateBuilder(OutcomeMeasure outcomeMeasure, List<Study> studies, List<DrugSet> drugs, Map<Study, Map<DrugSet, Arm>> armMap) {
		DichotomousNetworkBuilder<DrugSet> builder = new DichotomousNetworkBuilder<DrugSet>(s_transform, s_descTransform);
		for(Study s : studies){
			for (DrugSet d : drugs) {
				if (armMap.get(s).containsKey(d)) {
					BasicRateMeasurement brm = (BasicRateMeasurement) s.getMeasurement(outcomeMeasure, armMap.get(s).get(d));
					builder.add(s.getName(), s.getDrugs(armMap.get(s).get(d)), brm.getRate(), brm.getSampleSize());
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
