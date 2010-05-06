package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.drugis.addis.entities.BenefitRiskAnalysis;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
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
		
		public void setValue(Object selected) {
			super.setValue(selected);
			if (selected.equals(false))
				getMetaAnalysesSelectedModel(d_om).setValue(null);
		}
	}
	
	@SuppressWarnings("serial")
	private class CompleteHolder extends ModifiableHolder<Boolean> implements PropertyChangeListener {
		public CompleteHolder() {
			super(false);
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			int maCounter = 0;
			for (ModifiableHolder<MetaAnalysis> mah : d_metaAnalysisSelectedMap.values())
				if (mah.getValue() != null)
					++maCounter;
			
			int altCounter = 0;
			for (ModifiableHolder<Boolean> altEnabled : d_alternativeSelectedMap.values())
				if (altEnabled.getValue())
					++altCounter;
			
			setValue((maCounter >= 2) && (altCounter >= 2));
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
			val = new ModifiableHolder<Boolean>(getAlternativeEnabled(d));
			d_alternativeEnabledMap.put(d, val);
		}
		
		return val;
	}

	private void updateAlternativesEnabled() {
		for (Entry<Drug,ModifiableHolder<Boolean>> entry : d_alternativeEnabledMap.entrySet()) {
			boolean enabled = getAlternativeEnabled(entry.getKey());
			entry.getValue().setValue(enabled);
			getAlternativeSelectedModel(entry.getKey()).setValue(enabled);
		}
	}
	
	private Boolean getAlternativeEnabled(Drug d) {
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
			
		BenefitRiskAnalysis brAnalysis = new BenefitRiskAnalysis(
				id,
				d_indicationHolder.getValue(), 
				outcomes,
				metaAnalyses, 
				alternatives
			);
		return brAnalysis;
	}

	private <T> ArrayList<T> getSelectedEntities(Map<T, ModifiableHolder<Boolean>> outcomeSelectedMap) {
		ArrayList<T> list = new ArrayList<T>();
		for(T entity : outcomeSelectedMap.keySet()){
			if(outcomeSelectedMap.get(entity).getValue().equals(true))
				list.add(entity);
		}
		return list;
	}
}
