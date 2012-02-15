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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.relativeeffect.BasicRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RandomEffectMetaAnalysisRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RandomEffectsRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffectFactory;
import org.drugis.addis.util.EntityUtil;

public class RandomEffectsMetaAnalysis extends AbstractMetaAnalysis implements PairWiseMetaAnalysis {

	private static final String ANALYSIS_TYPE = "DerSimonian-Laird Random Effects Meta-Analysis";
	public static final String PROPERTY_INCLUDED_STUDIES_COUNT = "studiesIncluded";
	public static final String PROPERTY_CORRECTED = "isCorrected";
	private boolean d_isCorrected = false;
	
	/**
	 * @throws IllegalArgumentException if all studies don't measure the same indication OR
	 * if the list of studies is empty
	 */
	public RandomEffectsMetaAnalysis(String name, OutcomeMeasure om, List<Study> studies,
			DrugSet drug1, DrugSet drug2) 
	throws IllegalArgumentException {
		super(ANALYSIS_TYPE,
				name, studies.get(0).getIndication(), om, studies, 
				drugSetList(drug1, drug2), getArmMap(studies, drug1, drug2));
		checkREDataConsistency(studies, drug1, drug2);
	}

	private void checkREDataConsistency(List<? extends Study> studies, DrugSet drug1, DrugSet drug2) {
		if (studies.size() == 0)
			throw new IllegalArgumentException("No studies in MetaAnalysis");
		for (Study s : studies)
			if (!(s.getDrugs().contains(drug1) && s.getDrugs().contains(drug2)))
				throw new IllegalArgumentException("Not all studies contain the drugs under comparison");
	}

	public RandomEffectsMetaAnalysis(String name, OutcomeMeasure om, List<StudyArmsEntry> studyArms, Boolean corr)
	throws IllegalArgumentException {
		super(ANALYSIS_TYPE,
				name, getIndication(studyArms), om, getStudies(studyArms), getDrugs(studyArms), getArmMap(studyArms));
		
		for (StudyArmsEntry sae : studyArms){
			if(!sae.getStudy().getDrugs(sae.getBase()).equals(getFirstDrug())){
				throw new IllegalArgumentException("Left drug not consistent over all studies");
			}
			if(!sae.getStudy().getDrugs(sae.getSubject()).equals(getSecondDrug())){
				throw new IllegalArgumentException("Right drug not consistent over all studies");
			}
		}
		d_isCorrected = corr;
	}
	
	public RandomEffectsMetaAnalysis(String name, OutcomeMeasure om, List<StudyArmsEntry> studyArms) {
		this(name, om, studyArms, false);
	}
	
	private static Map<Study, Map<DrugSet, Arm>> getArmMap(
			List<? extends Study> studies, DrugSet drug1, DrugSet drug2) {
		List<StudyArmsEntry> studyArms = new ArrayList<StudyArmsEntry>();

		for (Study s : studies) {
			Arm arm1 = RelativeEffectFactory.findFirstArm(s, drug1);
			Arm arm2 = RelativeEffectFactory.findFirstArm(s, drug2);
			studyArms.add(new StudyArmsEntry(s, arm1, arm2));
		}
		
		return getArmMap(studyArms);
	}
	
	private static Map<Study, Map<DrugSet, Arm>> getArmMap(List<StudyArmsEntry> studyArms) {
		Map<Study, Map<DrugSet, Arm>> armMap = new HashMap<Study, Map<DrugSet, Arm>>();
		for (StudyArmsEntry sae : studyArms) {
			Map<DrugSet, Arm> drugMap = new HashMap<DrugSet, Arm>();
			drugMap.put(sae.getStudy().getDrugs(sae.getBase()), sae.getBase());
			drugMap.put(sae.getStudy().getDrugs(sae.getSubject()), sae.getSubject());
			armMap.put(sae.getStudy(), drugMap);
		}
		return armMap;
	}

	private static List<DrugSet> getDrugs(List<StudyArmsEntry> studyArms) {
		DrugSet d1 = getFirstDrug(studyArms);
		DrugSet d2 = getSecondDrug(studyArms);
		return drugSetList(d1, d2);
	}

	private static List<DrugSet> drugSetList(DrugSet d1, DrugSet d2) {
		List<DrugSet> list = new ArrayList<DrugSet>();
		list.add(d1);
		list.add(d2);
		return list;
	}

	private static DrugSet getSecondDrug(List<StudyArmsEntry> studyArms) {
		StudyArmsEntry studyArmsEntry = studyArms.get(0);
		return studyArmsEntry.getStudy().getDrugs(studyArmsEntry.getSubject());
	}

	private static DrugSet getFirstDrug(List<StudyArmsEntry> studyArms) {
		StudyArmsEntry studyArmsEntry = studyArms.get(0);
		return studyArmsEntry.getStudy().getDrugs(studyArmsEntry.getBase());
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
	
	/* (non-Javadoc)
	 * @see org.drugis.addis.entities.analysis.PairWiseMetaAnalysis#getFirstDrug()
	 */
	public DrugSet getFirstDrug() {
		return d_drugs.get(0);
	}
	
	/* (non-Javadoc)
	 * @see org.drugis.addis.entities.analysis.PairWiseMetaAnalysis#getSecondDrug()
	 */
	public DrugSet getSecondDrug() {
		return d_drugs.get(1);
	}
	
	public List<StudyArmsEntry> getStudyArms() {
		return getStudyArms(false);
	}
	
	private List<StudyArmsEntry> getStudyArms(boolean drugsSwapped) {
		List<StudyArmsEntry> studyArms = new ArrayList<StudyArmsEntry>();
		for (Study s : getIncludedStudies()) {
			if (!drugsSwapped)
				studyArms.add(new StudyArmsEntry(s, getArm(s, getFirstDrug()), getArm(s, getSecondDrug())));
			else
				studyArms.add(new StudyArmsEntry(s, getArm(s, getSecondDrug()), getArm(s, getFirstDrug())));
		}
		return studyArms;
	}

	public RandomEffectMetaAnalysisRelativeEffect<Measurement> getRelativeEffect(DrugSet d1, DrugSet d2, Class<? extends RelativeEffect<?>> type) {
		if (!d_drugs.containsAll(drugSetList(d1, d2)))
			throw new IllegalArgumentException(d_name + " compares drugs " + d_drugs + " but " + drugSetList(d1, d2) + " were asked");
		
		
		List<BasicRelativeEffect<? extends Measurement>> relEffects = getFilteredRelativeEffects(d1, d2, type);
		
		return new RandomEffectsRelativeEffect(relEffects);
	}

	List<BasicRelativeEffect<? extends Measurement>> getFilteredRelativeEffects(DrugSet d1, DrugSet d2, Class<? extends RelativeEffect<?>> type) {
		boolean drugsSwapped = !d1.equals(getFirstDrug());
		List<BasicRelativeEffect<? extends Measurement>> relEffects = new ArrayList<BasicRelativeEffect<? extends Measurement>>();
		
		for (int i=0; i<d_studies.size(); ++i ){ 
			RelativeEffect<? extends Measurement> re;
			re = RelativeEffectFactory.buildRelativeEffect(getStudyArms(drugsSwapped).get(i), d_outcome, type, d_isCorrected);
			if (re.isDefined())
				relEffects.add((BasicRelativeEffect<? extends Measurement>) re);
		}
		return relEffects;
	}
		
	public RandomEffectMetaAnalysisRelativeEffect<Measurement> getRelativeEffect(Class<? extends RelativeEffect<?>> type) {
		return getRelativeEffect(getFirstDrug(), getSecondDrug(), type);
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
			if ( !EntityUtil.deepEqual(s.getBase(), getArm(s.getStudy(), o.getFirstDrug())) ||
				 !EntityUtil.deepEqual(s.getSubject(), getArm(s.getStudy(), o.getSecondDrug()))) {
				return false;
			}
		}
		return true;
	}
	
}

