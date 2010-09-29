/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
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
import org.pietschy.wizard.InvalidStateException;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class BenefitRiskWizardPM extends AbstractWizardWithSelectableIndicationPM {

	@SuppressWarnings("serial")
	private class MetaAnalysesSelectedHolder extends ModifiableHolder<MetaAnalysis> {
		@Override
		public void setValue(Object ma) {
			super.setValue(ma);
		}
	}
	
	private class AlternativeEnabledModel extends ModifiableHolder<Boolean> implements PropertyChangeListener {
		private final Drug d_alternative;

		public AlternativeEnabledModel(Drug alternative) {
			d_alternative = alternative;
			setValue(alternativeShouldBeEnabled(d_alternative));
		}

		public void propertyChange(PropertyChangeEvent evt) {
			setValue(alternativeShouldBeEnabled(d_alternative));			
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
			if (selected.equals(false)) {
				getMetaAnalysesSelectedModel(d_om).setValue(null);
			} else if (getMetaAnalyses(d_om).size() == 1) {
				getMetaAnalysesSelectedModel(d_om).setValue(getMetaAnalyses(d_om).get(0));
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
			boolean synthesisComplete = (getSelectedCriteria().size() >= 2) && (getSelectedAlternatives().size() >= 2) && 
				selectedOutcomesHaveAnalysis();
			boolean singleStudyComplete = (getSelectedEntities(d_armSelectedMap).size() >= 2) && 
				(getSelectedEntities(d_outcomeSelectedMap).size() >= 2);
			setValue(synthesisComplete || singleStudyComplete);
		}
	}
	
	public enum BRAType {
		SingleStudy,
		Synthesis
	}
	
	private Map<OutcomeMeasure,ModifiableHolder<Boolean>> d_outcomeSelectedMap;
	private Map<OutcomeMeasure,ModifiableHolder<MetaAnalysis>> d_metaAnalysisSelectedMap;
	private HashMap<Drug, ModifiableHolder<Boolean>> d_alternativeEnabledMap;
	private HashMap<Drug, ModifiableHolder<Boolean>> d_alternativeSelectedMap;
	private HashMap<Arm, ModifiableHolder<Boolean>> d_armSelectedMap;
	private CompleteHolder d_completeHolder;
	private ModifiableHolder<Study> d_studyHolder;
	private ModifiableHolder<BRAType> d_studyType;
	private ModifiableHolder<AnalysisType> d_analysisType;
	private StudiesWithIndicationHolder d_studiesWithIndicationHolder;
	private HashMap<OutcomeMeasure, ModifiableHolder<Boolean>> d_criteriaEnabledMap;
	private MetaAnalysesSelectedHolder d_metaAnalysesSelectedHolder;
	
	public BenefitRiskWizardPM(Domain d) {
		super(d);
		d_outcomeSelectedMap = new HashMap<OutcomeMeasure, ModifiableHolder<Boolean>>();
		d_metaAnalysisSelectedMap = new HashMap<OutcomeMeasure, ModifiableHolder<MetaAnalysis>>();
		d_alternativeEnabledMap = new HashMap<Drug, ModifiableHolder<Boolean>>();
		d_alternativeSelectedMap = new HashMap<Drug, ModifiableHolder<Boolean>>();
		d_completeHolder = new CompleteHolder();
		d_studyHolder = new ModifiableHolder<Study>();
		d_studyType = new ModifiableHolder<BRAType>(BRAType.Synthesis);
		d_armSelectedMap = new HashMap<Arm, ModifiableHolder<Boolean>>();
		d_analysisType = new ModifiableHolder<AnalysisType>(AnalysisType.SMAA);
		d_criteriaEnabledMap = new HashMap<OutcomeMeasure, ModifiableHolder<Boolean>>();
		
		d_indicationHolder.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				d_outcomeSelectedMap.clear();
				d_alternativeSelectedMap.clear();
				d_armSelectedMap.clear();
				d_alternativeEnabledMap.clear();
				d_metaAnalysisSelectedMap.clear();
				d_completeHolder.propertyChange(null);
			}
		});
		d_studyHolder.addValueChangeListener(new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				d_outcomeSelectedMap.clear();
				d_alternativeSelectedMap.clear();
				d_armSelectedMap.clear();
				d_completeHolder.propertyChange(null);
			}
		});
		
		d_analysisType.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				d_outcomeSelectedMap.clear();
				d_alternativeSelectedMap.clear();
				d_metaAnalysisSelectedMap.clear();
				d_armSelectedMap.clear();
				d_alternativeEnabledMap.clear();
				d_completeHolder.propertyChange(null);
			}
		});
		
		d_studiesWithIndicationHolder = new StudiesWithIndicationHolder(d_indicationHolder, d_domain);
		
		d_metaAnalysesSelectedHolder = new MetaAnalysesSelectedHolder();
	}
	
	public boolean selectedOutcomesHaveAnalysis() {
		for (OutcomeMeasure om : getSelectedEntities(d_outcomeSelectedMap)) {
			if (getMetaAnalysesSelectedModel(om).getValue() == null) {
				return false;
			}
		}
		return true;
	}
	
	public int nSelectedOutcomes() {
		int n = 0;
		for (OutcomeMeasure om : getSelectedEntities(d_outcomeSelectedMap)) {
			if (getOutcomeSelectedModel(om).getValue() == true) {
				++n;
			}
		}
		return n;
	}

	public int nSelectedAlternatives() {
		int n = 0;
		for (Drug d : getSelectedEntities(d_alternativeSelectedMap)) {
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
		ModifiableHolder<Boolean> val = d_outcomeSelectedMap.get(om);
		if (val == null) {
			val = new OutcomeSelectedHolder(om);
			val.addPropertyChangeListener(d_completeHolder);
			d_outcomeSelectedMap.put(om, val);
		}
		
		return val;
	}

	public ValueHolder<MetaAnalysis> getMetaAnalysesSelectedModel(OutcomeMeasure om) {
		ModifiableHolder<MetaAnalysis> val = d_metaAnalysisSelectedMap.get(om);
		if (val == null) {
			val = getMetaAnalysesSelectedHolder();
			val.addPropertyChangeListener(d_completeHolder);
			d_metaAnalysisSelectedMap.put(om, val);
		}
		
		return val;
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

	public ValueHolder<Boolean> getAlternativeEnabledModel(Drug d) {
		ModifiableHolder<Boolean> val = d_alternativeEnabledMap.get(d);
		if (val == null) {
			val = createAlternativeEnabledModel(d); 
			
			d_alternativeEnabledMap.put(d, val);
		}
		
		return val;
	}
	
	private ModifiableHolder<Boolean> createAlternativeEnabledModel(Drug d) {
		AlternativeEnabledModel model = new AlternativeEnabledModel(d);
		getMetaAnalysesSelectedHolder().addValueChangeListener(model);
		getSelectedAlternativesHolder().addValueChangeListener(model);
		return null;
	}

	private AbstractValueModel getSelectedAlternativesHolder() {
		// TODO Auto-generated method stub
		return null;
	}

	private void updateAlternativesEnabled() {
		for (Drug d : getAlternativesListModel().getValue()) {
			boolean enabled = alternativeShouldBeEnabled(d);
			getAlternativeEnabledModel(d).setValue(enabled);
			getAlternativeSelectedModel(d).setValue(enabled);
		}
	}
	
	private boolean alternativeShouldBeEnabled(Drug d) {
		if(d_analysisType.getValue() == AnalysisType.SMAA)
			return getAlternativeIncludedInAllSelectedAnalyses(d);
		else if (d_analysisType.getValue() == AnalysisType.LyndOBrien) {
			return (getAlternativeSelectedModel(d).getValue() == true) ||
			getAlternativeIncludedInAllSelectedAnalyses(d) &&
			nSelectedAlternatives() < 2;
		}
		return false;
	}
	
	private Boolean getAlternativeIncludedInAllSelectedAnalyses(Drug d) {
		boolean atLeastOneMASelected = false;
		for(ValueHolder<MetaAnalysis> ma : getSelectedMetaAnalysisHolders()){
			if(ma.getValue() == null)
				continue;
			else
				atLeastOneMASelected = true;

			if (!(ma.getValue().getIncludedDrugs().contains(d)))
				return false;
		}
		return atLeastOneMASelected;
	}

	public ValueHolder<Boolean> getCriteriaEnabledModel(OutcomeMeasure out) {
		ModifiableHolder<Boolean> val = d_criteriaEnabledMap.get(out);
		if (val == null) {
			val = new ModifiableHolder<Boolean>(getCriterionShouldBeEnabled(out));
			d_criteriaEnabledMap.put(out, val);
		}
		return val;
	}

	private Boolean getCriterionShouldBeEnabled(OutcomeMeasure out) {
		if(getOutcomeSelectedModel(out).getValue() == true) return true;
		else return (d_analysisType.getValue() == AnalysisType.SMAA) || (nSelectedOutcomes() < 2);
	}

	private void updateCriteriaEnabled() {
		for (OutcomeMeasure om : getOutcomesListModel().getValue()) {
			boolean enabled = getCriterionShouldBeEnabled(om);
			getCriteriaEnabledModel(om).setValue(enabled);
		}
	}


	Collection<ModifiableHolder<MetaAnalysis>> getSelectedMetaAnalysisHolders() {
		return d_metaAnalysisSelectedMap.values();
	}

	public ValueHolder<Boolean> getAlternativeSelectedModel(Drug d) {
		ModifiableHolder<Boolean> val = d_alternativeSelectedMap.get(d);
		if (val == null) {
			val = new ModifiableHolder<Boolean>(false);
			val.addPropertyChangeListener(d_completeHolder);
			d_alternativeSelectedMap.put(d, val);
		}
		
		return val;
	}
	
	public ValueHolder<Boolean> getArmSelectedModel(Arm a) {
		ModifiableHolder<Boolean> val = d_armSelectedMap.get(a);
		if (val == null) {
			val = new ModifiableHolder<Boolean>(false);
			val.addPropertyChangeListener(d_completeHolder);
			d_armSelectedMap.put(a, val);
		}
		
		return val;
	}
	
	public ValueHolder<Boolean> getCompleteModel() {
		return d_completeHolder;
	}
	
	public BenefitRiskAnalysis<?> saveAnalysis(String id) throws InvalidStateException, EntityIdExistsException {
		
		BenefitRiskAnalysis<?> brAnalysis = null;

		if(getStudyType().getValue() == BRAType.Synthesis) {
			if(!getCompleteModel().getValue())
				throw new InvalidStateException("cannot commit, Benefit Risk Analysis not ready. Select at least two criteria, and two alternatives.");
			brAnalysis = createMetaBRAnalysis(id);
 		} else if(getStudyType().getValue() == BRAType.SingleStudy) {
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
		ArrayList<Arm> alternatives = getSelectedEntities(d_armSelectedMap);
		ArrayList<OutcomeMeasure> studyAnalyses = getSelectedEntities(d_outcomeSelectedMap);
		
		StudyBenefitRiskAnalysis sbr = new StudyBenefitRiskAnalysis(id, d_indicationHolder.getValue(), d_studyHolder.getValue(), 
				studyAnalyses, alternatives, AnalysisType.SMAA);
		return sbr;
	}

	private MetaBenefitRiskAnalysis createMetaBRAnalysis(String id) {
		ArrayList<Drug> alternatives = getSelectedEntities(d_alternativeSelectedMap);
		ArrayList<MetaAnalysis> metaAnalyses = new ArrayList<MetaAnalysis>();
		
		for(ModifiableHolder<MetaAnalysis> ma : d_metaAnalysisSelectedMap.values()){
			if(ma.getValue() !=null )
				metaAnalyses.add(ma.getValue());
		}
			
		Drug baseline = alternatives.get(0);
		alternatives.remove(0);
		MetaBenefitRiskAnalysis brAnalysis = new MetaBenefitRiskAnalysis(
				id,
				d_indicationHolder.getValue(), 
				metaAnalyses,
				baseline, 
				alternatives,
				AnalysisType.SMAA
			);
		return brAnalysis;
	}

	private <T> ArrayList<T> getSelectedEntities(Map<T, ModifiableHolder<Boolean>> selectedMap) {
		ArrayList<T> list = new ArrayList<T>();
		for(T entity : selectedMap.keySet()){
			if(selectedMap.get(entity).getValue().equals(true))
				list.add(entity);
		}
		return list;
	}

	ArrayList<OutcomeMeasure> getSelectedCriteria() {
		return getSelectedEntities(d_outcomeSelectedMap);
	}

	ArrayList<Drug> getSelectedAlternatives() {
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
	
	
	public ValueModel getStudyType() {
		return d_studyType;
	}
	
	public ValueModel getAnalysisType() {
		return d_analysisType;
	}

}
