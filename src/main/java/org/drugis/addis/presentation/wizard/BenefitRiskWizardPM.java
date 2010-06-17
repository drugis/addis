package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.UnmodifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.pietschy.wizard.InvalidStateException;

public class BenefitRiskWizardPM extends AbstractWizardWithSelectableIndicationPM {

	@SuppressWarnings("serial")
	private class MetaAnalysesSelectedHolder extends ModifiableHolder<MetaAnalysis> {
		@Override
		public void setValue(Object ma) {
			super.setValue(ma);
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
		}
	}
	
	@SuppressWarnings("serial")
	private class CompleteHolder extends ModifiableHolder<Boolean> implements PropertyChangeListener {
		public CompleteHolder() {
			super(false);
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			setValue(
					(getSelectedCriteria().size() >= 2) && 
					(getSelectedAlternatives().size() >= 2) && 
					selectedOutcomesHaveAnalysis());
		}
	}
	
	private Map<OutcomeMeasure,ModifiableHolder<Boolean>> d_outcomeSelectedMap;
	private Map<OutcomeMeasure,ModifiableHolder<MetaAnalysis>> d_metaAnalysisSelectedMap;
	private HashMap<Drug, ModifiableHolder<Boolean>> d_alternativeEnabledMap;
	private HashMap<Drug, ModifiableHolder<Boolean>> d_alternativeSelectedMap;
	private CompleteHolder d_completeHolder;
	
	public BenefitRiskWizardPM(Domain d) {
		super(d);
		d_outcomeSelectedMap = new HashMap<OutcomeMeasure, ModifiableHolder<Boolean>>();
		d_metaAnalysisSelectedMap = new HashMap<OutcomeMeasure, ModifiableHolder<MetaAnalysis>>();
		d_alternativeEnabledMap = new HashMap<Drug, ModifiableHolder<Boolean>>();
		d_alternativeSelectedMap = new HashMap<Drug, ModifiableHolder<Boolean>>();
		d_completeHolder = new CompleteHolder();
		
		d_indicationHolder.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				d_outcomeSelectedMap.clear();
				d_alternativeSelectedMap.clear();
				d_alternativeEnabledMap.clear();
				d_metaAnalysisSelectedMap.clear();
				d_completeHolder.propertyChange(null);
			}
		});
	}
	
	public boolean selectedOutcomesHaveAnalysis() {
		for (OutcomeMeasure om : getSelectedEntities(d_outcomeSelectedMap)) {
			if (getMetaAnalysesSelectedModel(om).getValue() == null) {
				return false;
			}
		}
		return true;
	}

	public ListHolder<OutcomeMeasure> getOutcomesListModel() {
		return new OutcomeListHolder(d_indicationHolder, d_domain);
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
			val = new MetaAnalysesSelectedHolder();
			val.addPropertyChangeListener(d_completeHolder);
			d_metaAnalysisSelectedMap.put(om, val);
		}
		
		return val;
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
			val = new ModifiableHolder<Boolean>(getAlternativeIncludedInAllSelectedAnalyses(d));
			d_alternativeEnabledMap.put(d, val);
		}
		
		return val;
	}

	private void updateAlternativesEnabled() {
		for (Drug d : getAlternativesListModel().getValue()) {
			boolean enabled = getAlternativeIncludedInAllSelectedAnalyses(d);
			getAlternativeEnabledModel(d).setValue(enabled);
			getAlternativeSelectedModel(d).setValue(enabled);
		}
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

	public ValueHolder<Boolean> getCompleteModel() {
		return d_completeHolder;
	}
	
	public BenefitRiskAnalysis saveAnalysis(String id) throws InvalidStateException, EntityIdExistsException {
		if(!getCompleteModel().getValue())
			throw new InvalidStateException("cannot commit, Benefit Risk Analysis not ready. Select at least two criteria, and two alternatives");
		
		BenefitRiskAnalysis brAnalysis = createBRAnalysis(id);
		
		if(d_domain.getBenefitRiskAnalyses().contains(brAnalysis))
			throw new EntityIdExistsException("Benefit Risk Analysis with this ID already exists in domain");
		
		d_domain.addBenefitRiskAnalysis(brAnalysis);
		return brAnalysis;
	}

	private BenefitRiskAnalysis createBRAnalysis(String id) {
		ArrayList<OutcomeMeasure> outcomes = getSelectedEntities(d_outcomeSelectedMap);
		ArrayList<Drug> alternatives = getSelectedEntities(d_alternativeSelectedMap);
		ArrayList<MetaAnalysis> metaAnalyses = new ArrayList<MetaAnalysis>();
		
		for(ModifiableHolder<MetaAnalysis> ma : d_metaAnalysisSelectedMap.values()){
			if(ma.getValue() !=null )
				metaAnalyses.add(ma.getValue());
		}
			
		Drug baseline = alternatives.get(0);
		alternatives.remove(0);
		BenefitRiskAnalysis brAnalysis = new BenefitRiskAnalysis(
				id,
				d_indicationHolder.getValue(), 
				outcomes,
				metaAnalyses, 
				baseline, 
				alternatives
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
}
