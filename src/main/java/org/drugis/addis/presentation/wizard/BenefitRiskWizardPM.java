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
import java.util.Map.Entry;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.UnmodifiableHolder;
import org.drugis.addis.presentation.ValueHolder;

import com.jgoodies.binding.value.ValueModel;

public class BenefitRiskWizardPM extends AbstractWizardWithSelectableIndicationPM {

	private Map<OutcomeMeasure,ModifiableHolder<Boolean>> d_outcomeSelectedMap;
	private Map<OutcomeMeasure,ModifiableHolder<MetaAnalysis>> d_metaAnalysisSelectedMap;
	private HashMap<Drug, ModifiableHolder<Boolean>> d_alternativeEnabledMap;
	private HashMap<Drug, ModifiableHolder<Boolean>> d_AlternativeSelectedMap;
	
	public BenefitRiskWizardPM(Domain d) {
		super(d);
		d_outcomeSelectedMap = new HashMap<OutcomeMeasure, ModifiableHolder<Boolean>>();
		d_metaAnalysisSelectedMap = new HashMap<OutcomeMeasure, ModifiableHolder<MetaAnalysis>>();
		d_alternativeEnabledMap = new HashMap<Drug, ModifiableHolder<Boolean>>();
		d_AlternativeSelectedMap = new HashMap<Drug, ModifiableHolder<Boolean>>();
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
			val = new ModifiableHolder<Boolean>(false);
			d_outcomeSelectedMap.put(om, val);
		}
		
		return val;
	}

	public ValueHolder<MetaAnalysis> getMetaAnalysesSelectedModel(OutcomeMeasure om) {
		ModifiableHolder<MetaAnalysis> val = d_metaAnalysisSelectedMap.get(om);
		if (val == null) {
			val = new ModifiableHolder<MetaAnalysis>();
			
			/* Update the enabled alternatives when the selected meta-analysis changes. */
			val.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					updateAlternativesEnabled();	
				}
			});
			
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
		ModifiableHolder<Boolean> val = d_AlternativeSelectedMap.get(d);
		if (val == null) {
			val = new ModifiableHolder<Boolean>(true);
			d_AlternativeSelectedMap.put(d, val);
		}
		
		return val;
	}

}
