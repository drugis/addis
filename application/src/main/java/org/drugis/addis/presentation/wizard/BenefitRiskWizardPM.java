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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.UnmodifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.util.comparator.CriteriaComparator;
import org.pietschy.wizard.InvalidStateException;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

// FIXME: the type of Alternative can not be known at construction time.
// FIXME: refactor this class to fix this.
public class BenefitRiskWizardPM<Alternative extends Comparable<Alternative>> extends AbstractWizardWithSelectableIndicationPM {

	@SuppressWarnings("serial")
	private class MetaAnalysesSelectedHolder extends ModifiableHolder<List<MetaAnalysis>> implements PropertyChangeListener {
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
		private final Alternative d_alternative;

		public AlternativeEnabledModel(Alternative e) {
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
			return getSelectedAlternatives().contains(d_baselineModel.getValue()) && 
			(getEvidenceTypeHolder().getValue().equals(BRAType.Synthesis) ? synthesisComplete() : singleStudyComplete());
		}

		private boolean synthesisComplete() {
			return (getSelectedEntities(d_criteriaSelectedMap).size() >= 2) && 
				(d_selectedAlternatives.getSelectedOptions().size() >= 2) && 
				selectedOutcomesHaveAnalysis();
		}

		private boolean singleStudyComplete() {
			return (d_selectedAlternatives.getSelectedOptions().size() >= 2) && 
				(getSelectedEntities(d_criteriaSelectedMap).size() >= 2);
		}
	}

	public enum BRAType {
		SingleStudy,
		Synthesis
	}

	private Map<OutcomeMeasure,ModifiableHolder<Boolean>> d_criteriaSelectedMap;
	private Map<OutcomeMeasure,ModifiableHolder<MetaAnalysis>> d_metaAnalysisSelectedMap;
	private HashMap<Alternative, ModifiableHolder<Boolean>> d_alternativeEnabledMap;
	private SelectableOptionsModel<Alternative> d_selectedAlternatives;
	private CompleteHolder d_completeHolder;
	private ModifiableHolder<BRAType> d_evidenceTypeHolder;
	private ModifiableHolder<AnalysisType> d_analysisTypeHolder;
	private HashMap<OutcomeMeasure, ModifiableHolder<Boolean>> d_outcomeEnabledMap;
	private MetaAnalysesSelectedHolder d_metaAnalysesSelectedHolder;
	private ObservableList<OutcomeMeasure> d_outcomes = new ArrayListModel<OutcomeMeasure>();
	private ModifiableHolder<Alternative> d_baselineModel;
	
	private final StudyCriteriaAndAlternativesPresentation d_studyCritAlt;
	private final MetaCriteriaAndAlternativesPresentation d_metaCritAlt;


	public BenefitRiskWizardPM(Domain d) {
		super(d);
		d_criteriaSelectedMap = new HashMap<OutcomeMeasure, ModifiableHolder<Boolean>>();
		d_metaAnalysisSelectedMap = new HashMap<OutcomeMeasure, ModifiableHolder<MetaAnalysis>>();
		d_alternativeEnabledMap = new HashMap<Alternative, ModifiableHolder<Boolean>>();
		d_selectedAlternatives = new SelectableOptionsModel<Alternative>();
		d_completeHolder = new CompleteHolder();
		d_evidenceTypeHolder = new ModifiableHolder<BRAType>(BRAType.Synthesis);
		d_analysisTypeHolder = new ModifiableHolder<AnalysisType>(AnalysisType.SMAA);
		d_outcomeEnabledMap = new HashMap<OutcomeMeasure, ModifiableHolder<Boolean>>();
		d_baselineModel = new ModifiableHolder<Alternative>();
		
		updateOutcomes();
		d_indicationHolder.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateOutcomes();
			}
		});

		PropertyChangeListener resetValuesListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (d_evidenceTypeHolder.getValue().equals(BRAType.Synthesis)) {
					clearValues();
				}
				initializeValues();
			}
		};

		d_indicationHolder.addValueChangeListener(resetValuesListener);

		d_evidenceTypeHolder.addValueChangeListener(resetValuesListener);
		d_analysisTypeHolder.addValueChangeListener(resetValuesListener);

		d_metaAnalysesSelectedHolder = new MetaAnalysesSelectedHolder();
		d_metaAnalysesSelectedHolder.addValueChangeListener(d_completeHolder);
		d_baselineModel.addValueChangeListener(d_completeHolder);
		
		d_studyCritAlt = new StudyCriteriaAndAlternativesPresentation(d_indicationHolder, d_analysisTypeHolder, d_domain.getStudies());
		d_metaCritAlt = new MetaCriteriaAndAlternativesPresentation(d_indicationHolder, d_analysisTypeHolder, d_domain.getMetaAnalyses());
	}

	private void initializeValues() {
		if (!readyToInit()) {
			return;
		}
		switch (d_evidenceTypeHolder.getValue()) {
		case SingleStudy:
			break;
		case Synthesis:
			initializeSynthesis();
			break;
		}
	}

	private void initOutcomeMeasures() {
		// create outcome selected models
		for (OutcomeMeasure om : getCriteriaListModel()) {
			ModifiableHolder<Boolean> val = new OutcomeSelectedHolder(om);
			val.addPropertyChangeListener(d_completeHolder);
			d_criteriaSelectedMap.put(om, val);
		}
		// create outcome enabled models
		for (OutcomeMeasure om : getCriteriaListModel()) {
			ModifiableHolder<Boolean> val = new ModifiableHolder<Boolean>(getCriterionShouldBeEnabled(om));
			d_outcomeEnabledMap.put(om, val);
		}
	}


	private void updateOutcomes() {
		SortedSet<OutcomeMeasure> outcomeSet = new TreeSet<OutcomeMeasure>();
		if (d_indicationHolder.getValue() != null) {
			for (Study s : d_domain.getStudies(this.d_indicationHolder.getValue())) {
				outcomeSet.addAll(Study.extractVariables(s.getEndpoints()));
				outcomeSet.addAll(Study.extractVariables(s.getAdverseEvents()));
			}			
		}	
		d_outcomes.clear();
		d_outcomes.addAll(outcomeSet);
	}
	
	private void initializeSynthesis() {
		initOutcomeMeasures();

		// create analyses models
		for (OutcomeMeasure om : getCriteriaListModel()) {
			ModifiableHolder<MetaAnalysis> val = new ModifiableHolder<MetaAnalysis>();
			val.addPropertyChangeListener(d_metaAnalysesSelectedHolder);
			d_metaAnalysisSelectedMap.put(om, val);
		}
		d_metaAnalysesSelectedHolder.fireValueChange(null, d_metaAnalysesSelectedHolder.getValue());
		
		Set<Alternative> alternatives = getAlternativesListModel().getValue();
		initAlternatives(alternatives);
	}
	
	private void initAlternatives(Set<Alternative> alternatives) {
		// create alternative selected models
		for (Alternative d : alternatives) {
			ModifiableHolder<Boolean> val = d_selectedAlternatives.addOption(d, false);
			val.addPropertyChangeListener(d_completeHolder);
		}
		// create alternative enabled models (they use the selected models -- don't merge the loops!)
		for (Alternative d : alternatives) {
			d_alternativeEnabledMap.put(d, createAlternativeEnabledModel(d));
		}
	}


	private boolean readyToInit() {
		return d_indicationHolder.getValue() != null && (d_evidenceTypeHolder.getValue().equals(BRAType.Synthesis));
	}

	private boolean selectedOutcomesHaveAnalysis() {
		for (OutcomeMeasure om : getSelectedEntities(d_criteriaSelectedMap)) {
			if (getMetaAnalysesSelectedModel(om).getValue() == null) {
				return false;
			}
		}
		return true;
	}

	private int nSelectedOutcomes() {
		int n = 0;
		for (OutcomeMeasure om : getSelectedEntities(d_criteriaSelectedMap)) {
			if (getCriterionSelectedModel(om).getValue() == true) {
				++n;
			}
		}
		return n;
	}

	private int nSelectedAlternatives() {
		int n = 0;
		for (Alternative d : d_selectedAlternatives.getSelectedOptions()) {
			if (getAlternativeSelectedModel(d).getValue() == true) {
				++n;
			}
		}
		return n;
	}

	public ObservableList<OutcomeMeasure> getCriteriaListModel() {
		return d_outcomes;
	}

	public ValueHolder<Study> getStudyModel() {
		return getStudyBRPresentation().getStudyModel();
	}


	public ArrayList<MetaAnalysis> getMetaAnalyses(OutcomeMeasure out) {
		ArrayList<MetaAnalysis> analyses = new ArrayList<MetaAnalysis>();
		for(MetaAnalysis ma : d_domain.getMetaAnalyses()){
			if(ma.getOutcomeMeasure().equals(out) )
				analyses.add(ma);
		}
		return analyses;
	}

	public ValueHolder<Boolean> getCriterionSelectedModel(OutcomeMeasure om) {
		return d_criteriaSelectedMap.get(om);
	}

	public ValueHolder<Boolean> getAlternativeSelectedModel(Alternative alternative) {
		return d_selectedAlternatives.getSelectedModel(alternative);
	}

	public ValueHolder<MetaAnalysis> getMetaAnalysesSelectedModel(OutcomeMeasure om) {
		return d_metaAnalysisSelectedMap.get(om);
	}

	private MetaAnalysesSelectedHolder getMetaAnalysesSelectedHolder() {
		return d_metaAnalysesSelectedHolder;
	}

	public UnmodifiableHolder<Set<Alternative>> getAlternativesListModel() {
		Set<DrugSet> drugSet = new TreeSet<DrugSet>();
		for(MetaAnalysis ma : d_domain.getMetaAnalyses()){
			if(ma.getIndication() == getIndicationModel().getValue())
				drugSet.addAll(ma.getIncludedDrugs());
		}
		
		return new UnmodifiableHolder<Set<Alternative>>((Set<Alternative>)drugSet);
	}

	public ValueHolder<Boolean> getAlternativeEnabledModel(Entity e) {
		return d_alternativeEnabledMap.get(e);
	}

	@SuppressWarnings("unchecked")
	private ModifiableHolder<Boolean> createAlternativeEnabledModel(Alternative e) {
		AlternativeEnabledModel model = new AlternativeEnabledModel(e);

		if(getEvidenceTypeHolder().getValue() == BRAType.Synthesis) {
			// To allow only measured alternatives to be selected
			getMetaAnalysesSelectedHolder().addValueChangeListener(model);

			// To limit the number of selected alternatives to 2 (in L&O analysis)
			for (Alternative d : getAlternativesListModel().getValue()) {
				getAlternativeSelectedModel(d).addValueChangeListener(model);
			}
		} else {
			// To allow only measured alternatives to be selected
			for (OutcomeMeasure om : getCriteriaListModel()) {
				getCriterionSelectedModel(om).addValueChangeListener(model);
			}

			// To limit the number of selected alternatives to 2 (in L&O analysis)
			for (Arm arm : getStudyModel().getValue().getArms()) {
				getAlternativeSelectedModel((Alternative)arm).addValueChangeListener(model);
			}
		}

		return model;
	}

	private boolean alternativeShouldBeEnabled(Alternative alternative) {
		if(getEvidenceTypeHolder().getValue() == BRAType.Synthesis) {
			// e is a drug in synthesis; 
			// safeguard added for spurious checks caused by listeners
			if(!(alternative instanceof DrugSet))
				return false;
			if(d_analysisTypeHolder.getValue() == AnalysisType.SMAA)
				return getAlternativeIncludedInAllSelectedAnalyses((DrugSet) alternative);
			else if (d_analysisTypeHolder.getValue() == AnalysisType.LyndOBrien) {
				if(!getAlternativeIncludedInAllSelectedAnalyses((DrugSet) alternative)) {
					return false;
				} 
				return (getAlternativeSelectedModel(alternative).getValue() == true) || nSelectedAlternatives() < 2;
			}
			return false;
		}
		return false;
	}

	private boolean getAlternativeIncludedInAllSelectedAnalyses(DrugSet e) {
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

	public ValueHolder<Boolean> getCriterionEnabledModel(OutcomeMeasure out) {
		return d_outcomeEnabledMap.get(out);
	}

	private boolean getCriterionShouldBeEnabled(OutcomeMeasure out) {
		if(getCriterionSelectedModel(out).getValue() == true) return true;
		else return (d_analysisTypeHolder.getValue() == AnalysisType.SMAA) || (nSelectedOutcomes() < 2);
	}

	private void updateCriteriaEnabled() {
		for (OutcomeMeasure om : getCriteriaListModel()) {
			getCriterionEnabledModel(om).setValue(getCriterionShouldBeEnabled(om));
		}
	}

	Collection<ModifiableHolder<MetaAnalysis>> getSelectedMetaAnalysisHolders() {
		return d_metaAnalysisSelectedMap.values();
	}

	public ValueHolder<Boolean> getCompleteModel() {
		return d_completeHolder;
	}

	public BenefitRiskAnalysis<?> saveAnalysis(String id) throws InvalidStateException, EntityIdExistsException {
		if(getEvidenceTypeHolder().getValue() == BRAType.SingleStudy) {
			return getStudyBRPresentation().saveAnalysis(d_domain, id);
		}
		
		
		BenefitRiskAnalysis<?> brAnalysis = null;

		if(getEvidenceTypeHolder().getValue() == BRAType.Synthesis) {
			if(!getCompleteModel().getValue())
				throw new InvalidStateException("cannot commit, Benefit Risk Analysis not ready. Select at least two criteria, and two alternatives.");
			brAnalysis = createMetaBRAnalysis(id);
		}
		if(d_domain.getBenefitRiskAnalyses().contains(brAnalysis))
			throw new EntityIdExistsException("Benefit Risk Analysis with this ID already exists in domain");

		d_domain.getBenefitRiskAnalyses().add(brAnalysis);
		return brAnalysis;
	}

	private void sortCriteria(List<OutcomeMeasure> studyAnalyses) {
		Collections.sort(studyAnalyses, new CriteriaComparator());
	}

	private MetaBenefitRiskAnalysis createMetaBRAnalysis(String id) {
		List<DrugSet> alternatives = convertList(d_selectedAlternatives.getSelectedOptions(), DrugSet.class);
		List<MetaAnalysis> metaAnalyses = new ArrayList<MetaAnalysis>();

		List<OutcomeMeasure> keySetArray = new ArrayList<OutcomeMeasure>(d_metaAnalysisSelectedMap.keySet());
		sortCriteria(keySetArray);

		for(OutcomeMeasure om : keySetArray){
			MetaAnalysis value = d_metaAnalysisSelectedMap.get(om).getValue();
			if (value != null) 
				metaAnalyses.add(value);
		}

		DrugSet baseline = (DrugSet) d_baselineModel.getValue();
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
		return getSelectedEntities(d_criteriaSelectedMap);
	}

	public ObservableList<Alternative> getSelectedAlternatives() {
		return d_selectedAlternatives.getSelectedOptions();
	}

	public ValueModel getEvidenceTypeHolder() {
		return d_evidenceTypeHolder;
	}

	public ValueModel getAnalysisTypeHolder() {
		return d_analysisTypeHolder;
	}

	private void clearValues() {
		d_criteriaSelectedMap.clear();
		d_alternativeEnabledMap.clear();
		d_selectedAlternatives.clear();
		d_metaAnalysesSelectedHolder.fireValueChange();
		d_metaAnalysisSelectedMap.clear();
		d_outcomeEnabledMap.clear();
		d_completeHolder.propertyChange(null);
		d_baselineModel.setValue(null);
	}

	public ValueModel getBaselineModel() {
		return d_baselineModel;
	}

	public StudyCriteriaAndAlternativesPresentation getStudyBRPresentation() {
		return d_studyCritAlt;
	}

	public  MetaCriteriaAndAlternativesPresentation getMetaBRPresentation() {
		return d_metaCritAlt;
	}

}
