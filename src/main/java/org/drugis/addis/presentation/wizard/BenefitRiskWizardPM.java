package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;

public class BenefitRiskWizardPM extends AbstractWizardWithSelectableIndicationPM {

	private Map<OutcomeMeasure,ModifiableHolder<Boolean>> d_outcomeSelectedMap;
	private Map<OutcomeMeasure,ModifiableHolder<MetaAnalysis>> d_metaAnalysisSelectedMap;
	
	public BenefitRiskWizardPM(Domain d) {
		super(d);
		d_outcomeSelectedMap = new HashMap<OutcomeMeasure,ModifiableHolder<Boolean>>();
		d_metaAnalysisSelectedMap = new HashMap<OutcomeMeasure,ModifiableHolder<MetaAnalysis>>();
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
			val = new ModifiableHolder<Boolean>(new Boolean(false));
			d_outcomeSelectedMap.put(om, val);
			
		}
		
		return val;
	}

	public ValueHolder<MetaAnalysis> getMetaAnalysesSelectedModel(OutcomeMeasure om) {
		ModifiableHolder<MetaAnalysis> val = d_metaAnalysisSelectedMap.get(om);
		if (val == null) {
			val = new ModifiableHolder<MetaAnalysis>();
			d_metaAnalysisSelectedMap.put(om, val);
		}
		
		return val;
	}
	
	
}
