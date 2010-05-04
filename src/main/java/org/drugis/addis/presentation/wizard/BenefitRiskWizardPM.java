package org.drugis.addis.presentation.wizard;

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
	
	public BenefitRiskWizardPM(Domain d) {
		super(d);
		d_outcomeSelectedMap = new HashMap<OutcomeMeasure,ModifiableHolder<Boolean>>();
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
	
	
}
