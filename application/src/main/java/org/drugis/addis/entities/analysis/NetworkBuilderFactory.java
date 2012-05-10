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
	
	private static final Transformer<DrugSet, String> s_descTransform = new Transformer<DrugSet, String>() {
		@Override
		public String transform(DrugSet input) {
			return input.getLabel();
		}
	};
	
	private static final Transformer<DrugSet, String> s_transform = new Transformer<DrugSet, String>() {
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
	};
}
