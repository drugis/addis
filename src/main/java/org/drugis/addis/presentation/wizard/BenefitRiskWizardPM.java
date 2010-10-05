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
		private final Drug d_alternative;

		public AlternativeEnabledModel(Drug alternative) {
			d_alternative = alternative;
			setValue(alternativeShouldBeEnabled(d_alternative));
		}

		public void propertyChange(PropertyChangeEvent evt) {
			boolean alternativeShouldBeEnabled = alternativeShouldBeEnabled(d_alternative);
			if(!alternativeShouldBeEnabled) getAlternativeSelectedModel(d_alternative).setValue(false);
			setValue(alternativeShouldBeEnabled);
		}
	}

	@SuppressWarnings("serial")
	private class ArmEnabledModel extends ModifiableHolder<Boolean> implements PropertyChangeListener {
		private final Arm d_arm;

		public ArmEnabledModel(Arm a) {
			d_arm = a;
			setValue(armShouldBeEnabled(a));
		}

		public void propertyChange(PropertyChangeEvent evt) {
			setValue(armShouldBeEnabled(d_arm));
		}
	}

	
	@SuppressWarnings("serial")
	private class ArmSelectedHolder extends ModifiableHolder<Boolean> {
		public ArmSelectedHolder(Arm a) {
			super(false);
		}
		
		@Override
		public void setValue(Object selected) {
			super.setValue(selected);
			updateArmsEnabled();
		}
	}

	
	@SuppressWarnings("serial")
	private class AlternativeSelectedHolder extends ModifiableHolder<Boolean> {
		public AlternativeSelectedHolder(Drug d) {
			super(false);
		}
		
		@Override
		public void setValue(Object selected) {
			super.setValue(selected);
			updateAlternativesEnabled();
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
	private HashMap<Arm, ModifiableHolder<Boolean>> d_armEnabledMap;
	private CompleteHolder d_completeHolder;
	private ModifiableHolder<Study> d_studyHolder;
	private ModifiableHolder<BRAType> d_evidenceTypeHolder;
	private ModifiableHolder<AnalysisType> d_analysisTypeHolder;
	private StudiesWithIndicationHolder d_studiesWithIndicationHolder;
	private HashMap<OutcomeMeasure, ModifiableHolder<Boolean>> d_outcomeEnabledMap;
	private MetaAnalysesSelectedHolder d_metaAnalysesSelectedHolder;
//	private AlternativeSelectedHolder d_alternativeSelectedHolder;
	
	
	public BenefitRiskWizardPM(Domain d) {
		super(d);
		d_outcomeSelectedMap = new HashMap<OutcomeMeasure, ModifiableHolder<Boolean>>();
		d_metaAnalysisSelectedMap = new HashMap<OutcomeMeasure, ModifiableHolder<MetaAnalysis>>();
		d_alternativeEnabledMap = new HashMap<Drug, ModifiableHolder<Boolean>>();
		d_alternativeSelectedMap = new HashMap<Drug, ModifiableHolder<Boolean>>();
		d_armEnabledMap = new HashMap<Arm, ModifiableHolder<Boolean>>();
		d_armSelectedMap = new HashMap<Arm, ModifiableHolder<Boolean>>();
		d_completeHolder = new CompleteHolder();
		d_studyHolder = new ModifiableHolder<Study>();
		d_evidenceTypeHolder = new ModifiableHolder<BRAType>(BRAType.Synthesis);
		d_analysisTypeHolder = new ModifiableHolder<AnalysisType>(AnalysisType.SMAA);
		d_outcomeEnabledMap = new HashMap<OutcomeMeasure, ModifiableHolder<Boolean>>();
		
		PropertyChangeListener clearValuesListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				d_outcomeSelectedMap.clear();
				d_alternativeEnabledMap.clear();
				d_alternativeSelectedMap.clear();
				d_armSelectedMap.clear();
				d_armEnabledMap.clear();
				d_metaAnalysesSelectedHolder.fireValueChange();
				d_metaAnalysisSelectedMap.clear();
				d_outcomeEnabledMap.clear();
				d_completeHolder.propertyChange(null);
			}
		};
		d_indicationHolder.addValueChangeListener(clearValuesListener);
		d_evidenceTypeHolder.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				d_studyHolder.setValue(null);
			}
		});
		d_studyHolder.addValueChangeListener(clearValuesListener);
		d_analysisTypeHolder.addValueChangeListener(clearValuesListener);
		
		d_studiesWithIndicationHolder = new StudiesWithIndicationHolder(d_indicationHolder, d_domain);
		
		d_metaAnalysesSelectedHolder = new MetaAnalysesSelectedHolder();
		d_metaAnalysesSelectedHolder.addValueChangeListener(d_completeHolder);
	}
	
	public void updateArmsEnabled() {
		for(Arm a: d_armSelectedMap.keySet()) {
			getArmEnabledModel(a).setValue(armShouldBeEnabled(a));
		}
	}

	public Object armShouldBeEnabled(Arm a) {
		if(d_analysisTypeHolder.getValue() == AnalysisType.SMAA) return true;
		else if(d_analysisTypeHolder.getValue() == AnalysisType.LyndOBrien)
		{
			return getArmSelectedModel(a).getValue() || (nArmsSelected() < 2);
		}
		return false;
	}

	private int nArmsSelected() {
		int n = 0;
		for(Arm a: d_armSelectedMap.keySet()) {
			if(getArmSelectedModel(a).getValue() == true) {
				++n;
			}
		}
		return n;
	}

	public void updateAlternativesEnabled() {
		for(Drug d: d_alternativeSelectedMap.keySet()) {
			getAlternativeEnabledModel(d).setValue(alternativeShouldBeEnabled(d));
		}
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

	public ValueHolder<Boolean> getAlternativeSelectedModel(Drug d) {
		ModifiableHolder<Boolean> val = d_alternativeSelectedMap.get(d);
		if (val == null) {
			val = new AlternativeSelectedHolder(d);
			val.addPropertyChangeListener(d_completeHolder);
			d_alternativeSelectedMap.put(d, val);
		}
		return val;
	}
	
	
	public ValueHolder<MetaAnalysis> getMetaAnalysesSelectedModel(OutcomeMeasure om) {
		ModifiableHolder<MetaAnalysis> val = d_metaAnalysisSelectedMap.get(om);
		if (val == null) {
			val = new ModifiableHolder<MetaAnalysis>();
			val.addPropertyChangeListener(d_metaAnalysesSelectedHolder);
			d_metaAnalysisSelectedMap.put(om, val);
			d_metaAnalysesSelectedHolder.fireValueChange(null, d_metaAnalysesSelectedHolder.getValue());
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
		getAlternativeSelectedModel(d).addValueChangeListener(model);
		return model;
	}

	private ModifiableHolder<Boolean> createArmEnabledModel(Arm a) {
		ArmEnabledModel model = new ArmEnabledModel(a);
		getMetaAnalysesSelectedHolder().addValueChangeListener(model);
		getArmSelectedModel(a).addValueChangeListener(model);
		return model;
	}

	private boolean alternativeShouldBeEnabled(Drug d) {
		if(d_analysisTypeHolder.getValue() == AnalysisType.SMAA)
			return getAlternativeIncludedInAllSelectedAnalyses(d);
		else if (d_analysisTypeHolder.getValue() == AnalysisType.LyndOBrien) {
			if(!getAlternativeIncludedInAllSelectedAnalyses(d)) {
				return false;
			} 
			return (getAlternativeSelectedModel(d).getValue() == true) || nSelectedAlternatives() < 2;
		}
		return false;
	}
	
	private boolean getAlternativeIncludedInAllSelectedAnalyses(Drug d) {
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

	public ValueHolder<Boolean> getOutcomeEnabledModel(OutcomeMeasure out) {
		ModifiableHolder<Boolean> val = d_outcomeEnabledMap.get(out);
		if (val == null) {
			val = new ModifiableHolder<Boolean>(getCriterionShouldBeEnabled(out));
			d_outcomeEnabledMap.put(out, val);
		}
		return val;
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

	public ValueHolder<Boolean> getArmSelectedModel(Arm a) {
		ModifiableHolder<Boolean> val = d_armSelectedMap.get(a);
		if (val == null) {
			val = new ArmSelectedHolder(a);
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
		ArrayList<Arm> alternatives = getSelectedEntities(d_armSelectedMap);
		ArrayList<OutcomeMeasure> studyAnalyses = getSelectedEntities(d_outcomeSelectedMap);
		
		StudyBenefitRiskAnalysis sbr = new StudyBenefitRiskAnalysis(id, d_indicationHolder.getValue(), d_studyHolder.getValue(), 
				studyAnalyses, alternatives, d_analysisTypeHolder.getValue());
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
				d_analysisTypeHolder.getValue()
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
	
	
	public ValueModel getEvidenceTypeHolder() {
		return d_evidenceTypeHolder;
	}
	
	public ValueModel getAnalysisTypeHolder() {
		return d_analysisTypeHolder;
	}

	public ValueHolder<Boolean> getArmEnabledModel(Arm a) {
		ModifiableHolder<Boolean> val = d_armEnabledMap.get(a);
		if (val == null) {
			val = createArmEnabledModel(a); 
			d_armEnabledMap.put(a, val);
		}
		return val;
	}

}
