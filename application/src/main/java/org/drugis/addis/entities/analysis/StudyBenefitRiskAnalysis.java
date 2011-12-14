/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.BasicStandardisedMeanDifference;
import org.drugis.addis.entities.relativeeffect.Beta;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.TransformedStudentT;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class StudyBenefitRiskAnalysis extends AbstractEntity implements BenefitRiskAnalysis<Arm> {
	public static String PROPERTY_STUDY = "study";
	public static String PROPERTY_ARMS = "arms";
	private Study d_study;
	private String d_name;
	private Indication d_indication;
	private List<OutcomeMeasure> d_criteria;
	private ObservableList<Arm> d_alternatives;
	private AnalysisType d_analysisType;
	private final Arm d_baseline;
	private DecisionContext d_decisionContext;
	
	private class StudyMeasurementSource extends AbstractMeasurementSource<Arm> {
	}
	
	public StudyBenefitRiskAnalysis(String name, Indication indication, Study study, 
			List<OutcomeMeasure> criteria, List<Arm> alternatives, AnalysisType analysisType) {
		this(name, indication, study, criteria, alternatives.get(0), alternatives, analysisType);
	}
	
	public StudyBenefitRiskAnalysis(String name, Indication indication, Study study, 
			List<OutcomeMeasure> criteria, Arm baseline, List<Arm> alternatives, AnalysisType analysisType) {
		this(name, indication, study, criteria, baseline, alternatives, analysisType, null);
	}

	public StudyBenefitRiskAnalysis(String name, Indication indication, Study study,
			List<OutcomeMeasure> criteria, Arm baseline, List<Arm> alternatives,
			AnalysisType analysisType, DecisionContext context) {
		d_baseline = baseline;
		assertMeasurementsPresent(study, criteria, alternatives);
		d_name = name;
		d_indication = indication;
		d_study = study;
		setCriteria(criteria);
		d_alternatives = new ArrayListModel<Arm>(Collections.unmodifiableList(alternatives));
		d_analysisType = analysisType;
		if(d_analysisType == AnalysisType.LyndOBrien && (d_criteria.size() != 2 || d_alternatives.size() != 2) ) {
			throw new IllegalArgumentException("Attempt to create Lynd & O'Brien analysis with not exactly 2 criteria and 2 alternatives");
		}
		d_decisionContext = context;
	}

	private void assertMeasurementsPresent(Study study, List<OutcomeMeasure> criteria, List<Arm> alternatives) {
		for (OutcomeMeasure om : criteria) {
			for (Arm a : alternatives) {
				if (study.getMeasurement(om, a) == null) {
					throw new IllegalArgumentException("Trying to create a StudyBR, but " + a + "," + om + " has no measurement in study " + study);
				}
			}
		}
	}

	private void setCriteria(List<OutcomeMeasure> criteria) {
		criteria = new ArrayList<OutcomeMeasure>(criteria);
		Collections.sort(criteria);
		d_criteria = Collections.unmodifiableList(criteria);
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		Set <Entity> deps = new HashSet<Entity>(d_study.getDependencies());
		deps.add(d_study);
		return deps;
	}
	
	public ObservableList<Arm> getArms() {
		return d_alternatives;
	}

	public ObservableList<Arm> getAlternatives() {
		return d_alternatives;
	}

	public Indication getIndication() {
		return d_indication;
	}

	public Distribution getMeasurement(OutcomeMeasure criterion, Arm alternative) {
		Measurement measurement = d_study.getMeasurement(criterion, alternative);
		if (measurement instanceof RateMeasurement) {
			RateMeasurement rateMeasurement = (RateMeasurement) measurement;
			return new Beta(1 + rateMeasurement.getRate(), 1 + rateMeasurement.getSampleSize() - rateMeasurement.getRate());
		} else if (measurement instanceof ContinuousMeasurement) {
			ContinuousMeasurement contMeasurement = (ContinuousMeasurement) measurement;
			return new TransformedStudentT(contMeasurement.getMean(), contMeasurement.getStdDev(), 
					contMeasurement.getSampleSize() - 1);
		} else {
			throw new IllegalStateException("Unknown measurement type " + measurement.getClass().getSimpleName());
		}
	}

	public String getName() {
		return d_name;
	}

	public List<OutcomeMeasure> getCriteria() {
		return d_criteria;
	}

	public int compareTo(BenefitRiskAnalysis<?> o) {
		if (o == null)
			return 1;
		return d_name.compareTo(o.getName());
	}

	public Study getStudy() {
		return d_study;
	}

	@Override
	public String toString() {
		return getName();
	}

	public MeasurementSource<Arm> getMeasurementSource() {
		return new StudyMeasurementSource();
	}

	public AnalysisType getAnalysisType() {
		return d_analysisType;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof StudyBenefitRiskAnalysis)) {
			return false;
		}
		StudyBenefitRiskAnalysis o = (StudyBenefitRiskAnalysis) other;
		return EqualsUtil.equal(getName(), o.getName());
	}
	
	@Override
	public boolean deepEquals(Entity other) {
		if (!equals(other)) {
			return false;
		}
		StudyBenefitRiskAnalysis o = (StudyBenefitRiskAnalysis) other;
		return EntityUtil.deepEqual(getStudy(), o.getStudy()) &&
			EntityUtil.deepEqual(getIndication(), o.getIndication()) &&
			EntityUtil.deepEqual(getBaseline(), o.getBaseline()) &&
			EntityUtil.deepEqual(getAlternatives(), o.getAlternatives()) &&
			EntityUtil.deepEqual(getCriteria(), o.getCriteria()) &&
			EntityUtil.deepEqual(getDecisionContext(), o.getDecisionContext());
	}

	public Arm getBaseline() {
		return d_baseline;
	}

	public DecisionContext getDecisionContext() {
		return d_decisionContext;
	}

	public Distribution getRelativeEffectDistribution(OutcomeMeasure om, Arm baseline, Arm subject) {
		BasicMeasurement baseMeas = getStudy().getMeasurement(om, baseline);
		BasicMeasurement subjMeas = getStudy().getMeasurement(om, subject);
		if (baseMeas instanceof BasicRateMeasurement) {
			return new BasicOddsRatio((RateMeasurement) baseMeas, (RateMeasurement) subjMeas).getDistribution();
		} 
		if (baseMeas instanceof BasicContinuousMeasurement) {
			return new BasicStandardisedMeanDifference((ContinuousMeasurement) baseMeas, (ContinuousMeasurement) subjMeas).getDistribution();
		}
		throw new IllegalStateException("Unknown error creating relative effect distribution in StudyBenefitRiskAnalysis");
	}
}
