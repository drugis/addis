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

package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.UnmodifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.util.comparator.CriteriaComparator;
import org.pietschy.wizard.InvalidStateException;

import com.jgoodies.binding.value.ValueModel;

public class BenefitRiskWizardPM extends AbstractWizardWithSelectableIndicationPM {

	@SuppressWarnings("serial")
	private class MetaAnalysesSelectedHolder extends AbstractListHolder<MetaAnalysis> implements PropertyChangeListener {
		@Override
		public List<MetaAnalysis> getValue() {
			List<MetaAnalysis> list = new ArrayList<MetaAnalysis>();
			for (ModifiableHolder<MetaAnalysis> holder : getSelectedMetaAnalysisHolders()) {
				if (holder.getValue() != null) {
					list.add(holder.getValue());
				}
			}
			return list;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			fireValueChange();
		}

		public void fireValueChange() {
			fireValueChange(null, getValue());			
		}
	}

	@SuppressWarnings("serial")
	private class AlternativeEnabledModel extends ModifiableHolder<Boolean> implements PropertyChangeListener {
		private final Entity d_alternative;

		public AlternativeEnabledModel(Entity e) {
			d_alternative = e;
			setValue(alternativeShouldBeEnabled(d_alternative));
		}

		public void propertyChange(PropertyChangeEvent evt) {
			boolean enabled = alternativeShouldBeEnabled(d_alternative);
			ValueHolder<Boolean> selectedModel = getAlternativeSelectedModel(d_alternative);
			if(!enabled && selectedModel != null) {
				selectedModel.setValue(false);
			}
			setValue(enabled);
		}
	}

	@SuppressWarnings("serial")
	private class OutcomeSelectedHolder extends ModifiableHolder<Boolean> {
		OutcomeMeasure d_om;

		public OutcomeSelectedHolder(OutcomeMeasure om) {
			super(false);
			d_om = om;
		}

		@Override
		public void setValue(Object selected) {
			super.setValue(selected);
			if (getEvidenceTypeHolder().getValue().equals(BRAType.Synthesis)) { // update selected meta-analysis
				if (selected.equals(false)) {
					getMetaAnalysesSelectedModel(d_om).setValue(null);
				} else if (getMetaAnalyses(d_om).size() == 1) {
					getMetaAnalysesSelectedModel(d_om).setValue(getMetaAnalyses(d_om).get(0));
				}
			}
			updateCriteriaEnabled();
		}
	}

	@SuppressWarnings("serial")
	private class CompleteHolder extends ModifiableHolder<Boolean> implements PropertyChangeListener {
		public CompleteHolder() {
			super(false);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			setValue(isComplete());
		}

		private boolean isComplete() {
			return getEvidenceTypeHolder().getValue().equals(BRAType.Synthesis) ? synthesisComplete() : singleStudyComplete();
		}

		private boolean synthesisComplete() {
			return (getSelectedEntities(d_outcomeSelectedMap).size() >= 2) && 
				(getSelectedEntities(d_alternativeSelectedMap).size() >= 2) && 
				selectedOutcomesHaveAnalysis();
		}

		private boolean singleStudyComplete() {
			return (getSelectedEntities(d_alternativeSelectedMap).size() >= 2) && 
				(getSelectedEntities(d_outcomeSelectedMap).size() >= 2);
		}
	}

	public enum BRAType {
		SingleStudy,
		Synthesis
	}

	private Map<OutcomeMeasure,ModifiableHolder<Boolean>> d_outcomeSelectedMap;
	private Map<OutcomeMeasure,ModifiableHolder<MetaAnalysis>> d_metaAnalysisSelectedMap;
	private HashMap<Entity, ModifiableHolder<Boolean>> d_alternativeEnabledMap;
	private HashMap<Entity, ModifiableHolder<Boolean>> d_alternativeSelectedMap;
	private CompleteHolder d_completeHolder;
	private ModifiableHolder<Study> d_studyHolder;
	private ModifiableHolder<BRAType> d_evidenceTypeHolder;
	private ModifiableHolder<AnalysisType> d_analysisTypeHolder;
	private StudiesWithIndicationHolder d_studiesWithIndicationHolder;
	private HashMap<OutcomeMeasure, ModifiableHolder<Boolean>> d_outcomeEnabledMap;
	private MetaAnalysesSelectedHolder d_metaAnalysesSelectedHolder;

	public BenefitRiskWizardPM(Domain d) {
		super(d);
		d_outcomeSelectedMap = new HashMap<OutcomeMeasure, ModifiableHolder<Boolean>>();
		d_metaAnalysisSelectedMap = new HashMap<OutcomeMeasure, ModifiableHolder<MetaAnalysis>>();
		d_alternativeEnabledMap = new HashMap<Entity, ModifiableHolder<Boolean>>();
		d_alternativeSelectedMap = new HashMap<Entity, ModifiableHolder<Boolean>>();
		d_completeHolder = new CompleteHolder();
		d_studyHolder = new ModifiableHolder<Study>();
		d_evidenceTypeHolder = new ModifiableHolder<BRAType>(BRAType.Synthesis);
		d_analysisTypeHolder = new ModifiableHolder<AnalysisType>(AnalysisType.SMAA);
		d_outcomeEnabledMap = new HashMap<OutcomeMeasure, ModifiableHolder<Boolean>>();

		PropertyChangeListener resetValuesListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getSource() != d_studyHolder) { // prevent infinite loop
					d_studyHolder.setValue(null);
				}
				clearValues();
				initializeValues();
			}
		};
		d_indicationHolder.addValueChangeListener(resetValuesListener);
		d_evidenceTypeHolder.addValueChangeListener(resetValuesListener);
		d_studyHolder.addValueChangeListener(resetValuesListener);
		d_analysisTypeHolder.addValueChangeListener(resetValuesListener);

		d_studiesWithIndicationHolder = new StudiesWithIndicationHolder(d_indicationHolder, d_domain);

		d_metaAnalysesSelectedHolder = new MetaAnalysesSelectedHolder();
		d_metaAnalysesSelectedHolder.addValueChangeListener(d_completeHolder);
	}

	private void initializeValues() {
		if (!readyToInit()) {
			return;
		}
		switch (d_evidenceTypeHolder.getValue()) {
		case SingleStudy:
			initializeSingleStudy();
			break;
		case Synthesis:
			initializeSynthesis();
			break;
		}
	}

	private void initOutcomeMeasures() {
		// create outcome selected models
		for (OutcomeMeasure om : getOutcomesListModel().getValue()) {
			ModifiableHolder<Boolean> val = new OutcomeSelectedHolder(om);
			val.addPropertyChangeListener(d_completeHolder);
			d_outcomeSelectedMap.put(om, val);
		}
		// create outcome enabled models
		for (OutcomeMeasure om : getOutcomesListModel().getValue()) {
			ModifiableHolder<Boolean> val = new ModifiableHolder<Boolean>(getCriterionShouldBeEnabled(om));
			d_outcomeEnabledMap.put(om, val);
		}
	}

	private void initializeSynthesis() {
		initOutcomeMeasures();

		// create analyses models
		for (OutcomeMeasure om : getOutcomesListModel().getValue()) {
			ModifiableHolder<MetaAnalysis> val = new ModifiableHolder<MetaAnalysis>();
			val.addPropertyChangeListener(d_metaAnalysesSelectedHolder);
			d_metaAnalysisSelectedMap.put(om, val);
		}
		d_metaAnalysesSelectedHolder.fireValueChange(null, d_metaAnalysesSelectedHolder.getValue());

		Set<? extends Entity> alternatives = getAlternativesListModel().getValue();
		initAlternatives(alternatives);
	}

	private void initializeSingleStudy() {
		initOutcomeMeasures();
		
		Set<? extends Entity> alternatives = new HashSet<Arm>(d_studyHolder.getValue().getArms()); 
		initAlternatives(alternatives);
	}
	
	private void initAlternatives(Set<? extends Entity> alternatives) {
		// create alternative selected models
		for (Entity d : alternatives) {
			ModifiableHolder<Boolean> val = new ModifiableHolder<Boolean>(false);
			d_alternativeSelectedMap.put(d, val);
			val.addPropertyChangeListener(d_completeHolder);
		}
		// create alternative enabled models (they use the selected models -- don't merge the loops!)
		for (Entity d : alternatives) {
			d_alternativeEnabledMap.put(d, createAlternativeEnabledModel(d));
		}
	}


	private boolean readyToInit() {
		return d_indicationHolder.getValue() != null && (d_evidenceTypeHolder.getValue().equals(BRAType.Synthesis) || d_studyHolder.getValue() != null);
	}

	private boolean selectedOutcomesHaveAnalysis() {
		for (OutcomeMeasure om : getSelectedEntities(d_outcomeSelectedMap)) {
			if (getMetaAnalysesSelectedModel(om).getValue() == null) {
				return false;
			}
		}
		return true;
	}

	private int nSelectedOutcomes() {
		int n = 0;
		for (OutcomeMeasure om : getSelectedEntities(d_outcomeSelectedMap)) {
			if (getOutcomeSelectedModel(om).getValue() == true) {
				++n;
			}
		}
		return n;
	}

	private int nSelectedAlternatives() {
		int n = 0;
		for (Entity d : getSelectedEntities(d_alternativeSelectedMap)) {
			if (getAlternativeSelectedModel(d).getValue() == true) {
				++n;
			}
		}
		return n;
	}

	public ListHolder<OutcomeMeasure> getOutcomesListModel() {
		return new OutcomeListHolder(d_indicationHolder, d_domain);
	}

	public ValueHolder<Study> getStudyModel() {
		return d_studyHolder;
	}


	public ArrayList<MetaAnalysis> getMetaAnalyses(OutcomeMeasure out) {
		ArrayList<MetaAnalysis> analyses = new ArrayList<MetaAnalysis>();
		for(MetaAnalysis ma : d_domain.getMetaAnalyses()){
			if(ma.getOutcomeMeasure().equals(out) )
				analyses.add(ma);
		}
		return analyses;
	}

	public ValueHolder<Boolean> getOutcomeSelectedModel(OutcomeMeasure om) {
		return d_outcomeSelectedMap.get(om);
	}

	public ValueHolder<Boolean> getAlternativeSelectedModel(Entity e) {
		return d_alternativeSelectedMap.get(e);
	}


	public ValueHolder<MetaAnalysis> getMetaAnalysesSelectedModel(OutcomeMeasure om) {
		return d_metaAnalysisSelectedMap.get(om);
	}

	private MetaAnalysesSelectedHolder getMetaAnalysesSelectedHolder() {
		return d_metaAnalysesSelectedHolder;
	}

	public ValueHolder<Set<Drug>> getAlternativesListModel() {
		Set<Drug> drugSet = new TreeSet<Drug>();
		for(MetaAnalysis ma : d_domain.getMetaAnalyses()){
			if(ma.getIndication() == getIndicationModel().getValue() )
				drugSet.addAll(ma.getIncludedDrugs());
		}
		return new UnmodifiableHolder<Set<Drug>>(drugSet);
	}

	public ValueHolder<Boolean> getAlternativeEnabledModel(Entity e) {
		return d_alternativeEnabledMap.get(e);
	}

	private ModifiableHolder<Boolean> createAlternativeEnabledModel(Entity e) {
		AlternativeEnabledModel model = new AlternativeEnabledModel(e);

		if(getEvidenceTypeHolder().getValue() == BRAType.Synthesis) {
			// To allow only measured alternatives to be selected
			getMetaAnalysesSelectedHolder().addValueChangeListener(model);

			// To limit the number of selected alternatives to 2 (in L&O analysis)
			for (Drug d : getAlternativesListModel().getValue()) {
				getAlternativeSelectedModel(d).addValueChangeListener(model);
			}
		} else {
			// To allow only measured alternatives to be selected
			for (OutcomeMeasure om : getOutcomesListModel().getValue()) {
				getOutcomeSelectedModel(om).addValueChangeListener(model);
			}

			// To limit the number of selected alternatives to 2 (in L&O analysis)
			for (Arm arm : getStudyModel().getValue().getArms()) {
				getAlternativeSelectedModel(arm).addValueChangeListener(model);
			}
		}

		return model;
	}

	private boolean alternativeShouldBeEnabled(Entity e) {
		if(getEvidenceTypeHolder().getValue() == BRAType.Synthesis) {
			// e is a drug in synthesis; 
			// safeguard added for spurious checks caused by listeners
			if(! (e instanceof Drug))
				return false;
			if(d_analysisTypeHolder.getValue() == AnalysisType.SMAA)
				return getAlternativeIncludedInAllSelectedAnalyses((Drug) e);
			else if (d_analysisTypeHolder.getValue() == AnalysisType.LyndOBrien) {
				if(!getAlternativeIncludedInAllSelectedAnalyses((Drug) e)) {
					return false;
				} 
				return (getAlternativeSelectedModel(e).getValue() == true) || nSelectedAlternatives() < 2;
			}
			return false;
		} else if(getEvidenceTypeHolder().getValue() == BRAType.SingleStudy) {
			// e is an arm in single-study
			// safeguard added for spurious checks caused by listeners
			if(! (e instanceof Arm))
				return false;
			for(Entry<OutcomeMeasure, ModifiableHolder<Boolean>> entry: d_outcomeSelectedMap.entrySet()) {
				BasicMeasurement measurement = d_studyHolder.getValue().getMeasurement(entry.getKey(), (Arm) e);
				if (entry.getValue().getValue() && (measurement == null || !measurement.isComplete())) {
					return false;
				}
			}
			if(d_analysisTypeHolder.getValue() == AnalysisType.LyndOBrien) { 
				return (getAlternativeSelectedModel((Arm) e).getValue() == true) || nSelectedAlternatives() < 2;
			} else {
				return true;
			}
		}
		return false;
	}

	private boolean getAlternativeIncludedInAllSelectedAnalyses(Drug e) {
		boolean atLeastOneMASelected = false;
		for(ValueHolder<MetaAnalysis> ma : getSelectedMetaAnalysisHolders()){
			if(ma.getValue() == null)
				continue;
			else
				atLeastOneMASelected = true;

			if (!(ma.getValue().getIncludedDrugs().contains(e)))
				return false;
		}
		return atLeastOneMASelected;
	}

	public ValueHolder<Boolean> getOutcomeEnabledModel(OutcomeMeasure out) {
		return d_outcomeEnabledMap.get(out);
	}

	private boolean getCriterionShouldBeEnabled(OutcomeMeasure out) {
		if(getOutcomeSelectedModel(out).getValue() == true) return true;
		else return (d_analysisTypeHolder.getValue() == AnalysisType.SMAA) || (nSelectedOutcomes() < 2);
	}

	private void updateCriteriaEnabled() {
		for (OutcomeMeasure om : getOutcomesListModel().getValue()) {
			getOutcomeEnabledModel(om).setValue(getCriterionShouldBeEnabled(om));
		}
	}

	Collection<ModifiableHolder<MetaAnalysis>> getSelectedMetaAnalysisHolders() {
		return d_metaAnalysisSelectedMap.values();
	}

	public ValueHolder<Boolean> getCompleteModel() {
		return d_completeHolder;
	}

	public BenefitRiskAnalysis<?> saveAnalysis(String id) throws InvalidStateException, EntityIdExistsException {

		BenefitRiskAnalysis<?> brAnalysis = null;

		if(getEvidenceTypeHolder().getValue() == BRAType.Synthesis) {
			if(!getCompleteModel().getValue())
				throw new InvalidStateException("cannot commit, Benefit Risk Analysis not ready. Select at least two criteria, and two alternatives.");
			brAnalysis = createMetaBRAnalysis(id);
		} else if(getEvidenceTypeHolder().getValue() == BRAType.SingleStudy) {
			if(!getCompleteModel().getValue())
				throw new InvalidStateException("cannot commit, Benefit Risk Analysis not ready. Select at least two outcome measures, and two arms.");
			brAnalysis = createStudyBRAnalysis(id);
		}
		if(d_domain.getBenefitRiskAnalyses().contains(brAnalysis))
			throw new EntityIdExistsException("Benefit Risk Analysis with this ID already exists in domain");

		d_domain.addBenefitRiskAnalysis(brAnalysis);
		return brAnalysis;
	}

	private StudyBenefitRiskAnalysis createStudyBRAnalysis(String id) {
		List<Arm> alternatives = convertList(getSelectedEntities(d_alternativeSelectedMap), Arm.class);

		List<OutcomeMeasure> studyAnalyses = getSelectedEntities(d_outcomeSelectedMap);
		sortCriteria(studyAnalyses);
		StudyBenefitRiskAnalysis sbr = new StudyBenefitRiskAnalysis(id, d_indicationHolder.getValue(), d_studyHolder.getValue(), 
				studyAnalyses, alternatives, d_analysisTypeHolder.getValue());
		return sbr;
	}

	private void sortCriteria(List<OutcomeMeasure> studyAnalyses) {
		Collections.sort(studyAnalyses, new CriteriaComparator());
	}

	private MetaBenefitRiskAnalysis createMetaBRAnalysis(String id) {
		List<Drug> alternatives = convertList(getSelectedEntities(d_alternativeSelectedMap), Drug.class);
		List<MetaAnalysis> metaAnalyses = new ArrayList<MetaAnalysis>();

		List<OutcomeMeasure> keySetArray = new ArrayList<OutcomeMeasure>(d_metaAnalysisSelectedMap.keySet());
		sortCriteria(keySetArray);

		for(OutcomeMeasure om : keySetArray){
			MetaAnalysis value = d_metaAnalysisSelectedMap.get(om).getValue();
			if (value != null) 
				metaAnalyses.add(value);
		}

		Drug baseline = alternatives.get(0);
		alternatives.remove(0);
		MetaBenefitRiskAnalysis brAnalysis = new MetaBenefitRiskAnalysis(
				id,
				d_indicationHolder.getValue(), 
				metaAnalyses,
				baseline, 
				alternatives,
				d_analysisTypeHolder.getValue()
		);
		return brAnalysis;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> convertList(List<?> list, Class<T> cls) {
		List<T> rval = new ArrayList<T>();
		for (Object o : list) {
			rval.add((T)o);
		}
		return rval;
	}

	private <T> List<T> getSelectedEntities(Map<T, ModifiableHolder<Boolean>> selectedMap) {
		List<T> list = new ArrayList<T>();
		for(T entity : selectedMap.keySet()){
			if(selectedMap.get(entity).getValue().equals(true))
				list.add(entity);
		}
		return list;
	}

	List<OutcomeMeasure> getSelectedCriteria() {
		return getSelectedEntities(d_outcomeSelectedMap);
	}

	List<Entity> getSelectedAlternatives() {
		return getSelectedEntities(d_alternativeSelectedMap);
	}

	@SuppressWarnings("serial")
	public static class StudiesWithIndicationHolder extends AbstractListHolder<Study> implements PropertyChangeListener {
		private final ValueHolder<Indication> d_indicationHolder;
		private final Domain d_domain;

		public StudiesWithIndicationHolder(ValueHolder<Indication> indicationHolder, Domain domain) {
			d_indicationHolder = indicationHolder;
			d_domain = domain;
			d_indicationHolder.addValueChangeListener(this);
		}

		@Override
		public List<Study> getValue() {
			if(d_indicationHolder.getValue() == null) {
				return new ArrayList<Study>();
			} else {
				return new ArrayList<Study>(d_domain.getStudies(d_indicationHolder.getValue()).getValue());
			}
		}

		public void propertyChange(PropertyChangeEvent evt) {
			fireValueChange(null, getValue());
		}

	}

	public ListHolder<Study> getStudiesWithIndication() {
		return d_studiesWithIndicationHolder;
	}	

	public ValueModel getEvidenceTypeHolder() {
		return d_evidenceTypeHolder;
	}

	public ValueModel getAnalysisTypeHolder() {
		return d_analysisTypeHolder;
	}

	private void clearValues() {
		d_outcomeSelectedMap.clear();
		d_alternativeEnabledMap.clear();
		d_alternativeSelectedMap.clear();
		d_metaAnalysesSelectedHolder.fireValueChange();
		d_metaAnalysisSelectedMap.clear();
		d_outcomeEnabledMap.clear();
		d_completeHolder.propertyChange(null);
	}

}
