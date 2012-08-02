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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.TreatmentCategorySet;
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
import org.drugis.addis.util.EntityUtil;
import org.drugis.mtc.summary.MultivariateNormalSummary;
import org.drugis.mtc.summary.SimpleMultivariateNormalSummary;

public class RandomEffectsMetaAnalysis extends AbstractMetaAnalysis implements PairWiseMetaAnalysis {

	private static final String ANALYSIS_TYPE = "DerSimonian-Laird Random Effects Meta-Analysis";
	public static final String PROPERTY_INCLUDED_STUDIES_COUNT = "studiesIncluded";
	public static final String PROPERTY_CORRECTED = "isCorrected";
	private boolean d_isCorrected = false;

	public RandomEffectsMetaAnalysis(String name, OutcomeMeasure om,
			TreatmentCategorySet baseline, TreatmentCategorySet subject,
			List<StudyArmsEntry> studyArms, boolean corr) {
		super(ANALYSIS_TYPE, name, getIndication(studyArms), om,
				getStudies(studyArms), Arrays.asList(baseline, subject),
				getArmMap(studyArms));
		
		for (StudyArmsEntry sae : studyArms){ // FIXME: drug tests should use category matching.
			if(!sae.getStudy().getDrugs(sae.getBase()).equals(getFirstAlternative())){
				throw new IllegalArgumentException("Left drug not consistent over all studies");
			}
			if(!sae.getStudy().getDrugs(sae.getSubject()).equals(getSecondAlternative())){
				throw new IllegalArgumentException("Right drug not consistent over all studies");
			}
		}
		d_isCorrected = corr;
	}
	
	private static Map<Study, Map<TreatmentCategorySet, Arm>> getArmMap(List<StudyArmsEntry> studyArms) {
		Map<Study, Map<TreatmentCategorySet, Arm>> armMap = new HashMap<Study, Map<TreatmentCategorySet, Arm>>();
		for (StudyArmsEntry sae : studyArms) {
			Map<TreatmentCategorySet, Arm> alternativeMap = new HashMap<TreatmentCategorySet, Arm>();
			alternativeMap.put(sae.getStudy().getDrugs(sae.getBase()), sae.getBase());
			alternativeMap.put(sae.getStudy().getDrugs(sae.getSubject()), sae.getSubject());
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
	public TreatmentCategorySet getFirstAlternative() {
		return d_alternatives.get(0);
	}
	
	@Override
	public TreatmentCategorySet getSecondAlternative() {
		return d_alternatives.get(1);
	}
	
	public List<StudyArmsEntry> getStudyArms() {
		List<StudyArmsEntry> studyArms = new ArrayList<StudyArmsEntry>();
		for (Study s : getIncludedStudies()) {
			studyArms.add(new StudyArmsEntry(s, getArm(s, getFirstAlternative()), getArm(s, getSecondAlternative())));
		}
		return studyArms;
	}
	
	List<BasicRelativeEffect<? extends Measurement>> getFilteredRelativeEffects(Class<? extends RelativeEffect<?>> type) {
		List<BasicRelativeEffect<? extends Measurement>> relEffects = new ArrayList<BasicRelativeEffect<? extends Measurement>>();
		for (StudyArmsEntry entry : getStudyArms()) {
			RelativeEffect<? extends Measurement> re = RelativeEffectFactory.buildRelativeEffect(entry, d_outcome, type, d_isCorrected);
			if (re.isDefined()) {
				relEffects.add((BasicRelativeEffect<? extends Measurement>) re);
			}
		}
		return relEffects;
	}
		
	public RandomEffectMetaAnalysisRelativeEffect<Measurement> getRelativeEffect(Class<? extends RelativeEffect<?>> type) {
		return new RandomEffectsRelativeEffect(getFilteredRelativeEffects(type));
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

