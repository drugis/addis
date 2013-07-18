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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.BasicRelativeEffect;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.RandomEffectMetaAnalysisRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RandomEffectsRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffectFactory;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.util.EntityUtil;
import org.drugis.mtc.summary.MultivariateNormalSummary;
import org.drugis.mtc.summary.SimpleMultivariateNormalSummary;

public class RandomEffectsMetaAnalysis extends AbstractMetaAnalysis implements PairWiseMetaAnalysis {

	private static final String ANALYSIS_TYPE = "DerSimonian-Laird Random Effects Meta-Analysis";
	public static final String PROPERTY_INCLUDED_STUDIES_COUNT = "studiesIncluded";
	public static final String PROPERTY_CORRECTED = "isCorrected";
	private boolean d_isCorrected = false;

	public RandomEffectsMetaAnalysis(String name, OutcomeMeasure om,
			TreatmentDefinition baseline, TreatmentDefinition subject,
			List<StudyArmsEntry> studyArms, boolean corr) {
		super(ANALYSIS_TYPE, name, getIndication(studyArms), om,
				getStudies(studyArms), Arrays.asList(baseline, subject),
				getArmMap(baseline, subject, studyArms));
		d_isCorrected = corr;
	}
	
	private static Map<Study, Map<TreatmentDefinition, Arm>> getArmMap(
			TreatmentDefinition baseline, TreatmentDefinition subject,
			List<StudyArmsEntry> studyArms) {
		Map<Study, Map<TreatmentDefinition, Arm>> armMap = new HashMap<Study, Map<TreatmentDefinition, Arm>>();
		for (StudyArmsEntry sae : studyArms) {
			Map<TreatmentDefinition, Arm> alternativeMap = new HashMap<TreatmentDefinition, Arm>();
			alternativeMap.put(baseline, sae.getBase());
			alternativeMap.put(subject, sae.getSubject());
			armMap.put(sae.getStudy(), alternativeMap);
		}
		return armMap;
	}

	private static List<Study> getStudies(List<StudyArmsEntry> studyArms) {
		return StudyArmsEntry.getStudyList(studyArms);
	}

	private static Indication getIndication(List<StudyArmsEntry> studyArms) {
		return getStudies(studyArms).get(0).getIndication();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
	}	
	
	@Override
	public TreatmentDefinition getFirstAlternative() {
		return d_alternatives.get(0);
	}
	
	@Override
	public TreatmentDefinition getSecondAlternative() {
		return d_alternatives.get(1);
	}
	
	public List<StudyArmsEntry> getStudyArms() {
		List<StudyArmsEntry> studyArms = new ArrayList<StudyArmsEntry>();
		for (Study s : getIncludedStudies()) {
			studyArms.add(new StudyArmsEntry(s, getArm(s, getFirstAlternative()), getArm(s, getSecondAlternative())));
		}
		return studyArms;
	}
	
	List<RelativeEffect<? extends Measurement>> getRelativeEffects(Class<? extends RelativeEffect<?>> type) {
		List<RelativeEffect<? extends Measurement>> relEffects = new ArrayList<RelativeEffect<? extends Measurement>>();
		for (StudyArmsEntry entry : getStudyArms()) {
			relEffects.add((BasicRelativeEffect<? extends Measurement>) RelativeEffectFactory.buildRelativeEffect(entry, d_outcome, type, d_isCorrected));
		}
		return relEffects;
	}
	
	List<RelativeEffect<? extends Measurement>> getFilteredRelativeEffects(Class<? extends RelativeEffect<?>> type) {
		final List<RelativeEffect<? extends Measurement>> relativeEffects = getRelativeEffects(type);
		CollectionUtils.filter(relativeEffects, new Predicate<RelativeEffect<? extends Measurement>>() {
			public boolean evaluate(RelativeEffect<? extends Measurement> re) {
				return re.isDefined();
			}
		});
		return relativeEffects;
	}
		
	public RandomEffectMetaAnalysisRelativeEffect<Measurement> getRelativeEffect(Class<? extends RelativeEffect<?>> type) {
		final List<RelativeEffect<? extends Measurement>> relativeEffects = getFilteredRelativeEffects(type);
		if (relativeEffects.isEmpty()) {
			return null;
		}
		return new RandomEffectsRelativeEffect(relativeEffects);
	}
	
	public boolean getIsCorrected() {
		return d_isCorrected;
	}

	public void setIsCorrected(boolean iscorrected) {
		boolean oldVal = d_isCorrected;
		d_isCorrected = iscorrected;
		firePropertyChange(PROPERTY_CORRECTED, oldVal, d_isCorrected);
	}
	
	@Override
	public boolean deepEquals(Entity other) {
		if (!super.deepEquals(other)) {
			return false;
		}
		RandomEffectsMetaAnalysis o = (RandomEffectsMetaAnalysis) other;
		for (StudyArmsEntry s : o.getStudyArms()) {
			if ( !EntityUtil.deepEqual(s.getBase(), getArm(s.getStudy(), o.getFirstAlternative())) ||
				 !EntityUtil.deepEqual(s.getSubject(), getArm(s.getStudy(), o.getSecondAlternative()))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public MultivariateNormalSummary getRelativeEffectsSummary() {
		Class<? extends RelativeEffect<?>> type = (d_outcome.getVariableType() instanceof RateVariableType) ? 
				BasicOddsRatio.class : BasicMeanDifference.class;
		RelativeEffect<Measurement> effect = getRelativeEffect(type);
		GaussianBase distribution = (GaussianBase) effect.getDistribution();
		return new SimpleMultivariateNormalSummary(
				new double[]{ distribution.getMu() },
				new double[][] { { distribution.getSigma() * distribution.getSigma() } });
	}
	
}

