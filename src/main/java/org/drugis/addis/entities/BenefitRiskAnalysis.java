package org.drugis.addis.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.entities.metaanalysis.MetaAnalysis;

public class BenefitRiskAnalysis extends AbstractEntity implements Comparable<BenefitRiskAnalysis> {

	private String d_id;
	private Indication d_indication;
	private List<OutcomeMeasure> d_outcomeMeasures;
	private List<MetaAnalysis> d_metaAnalyses;
	private List<Drug> d_drugs;
	
	public BenefitRiskAnalysis(String id, Indication indication, List<OutcomeMeasure> outcomeMeasures,
			List<MetaAnalysis> metaAnalysis, List<Drug> drugs) {
		super();
		d_indication = indication;
		d_outcomeMeasures = outcomeMeasures;
		d_metaAnalyses = metaAnalysis;
		d_drugs = drugs;
		setId(id);
	}

	public Indication getIndication() {
		return d_indication;
	}

	public void setIndication(Indication indication) {
		d_indication = indication;
	}

	public List<OutcomeMeasure> getOutcomeMeasures() {
		return d_outcomeMeasures;
	}

	public void setOutcomeMeasures(List<OutcomeMeasure> outcomeMeasures) {
		d_outcomeMeasures = outcomeMeasures;
	}

	public List<MetaAnalysis> getMetaAnalysis() {
		return d_metaAnalyses;
	}

	public void setMetaAnalysis(List<MetaAnalysis> metaAnalysis) {
		d_metaAnalyses = metaAnalysis;
	}

	public List<Drug> getDrugs() {
		return d_drugs;
	}

	public void setDrugs(List<Drug> drugs) {
		d_drugs = drugs;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		HashSet<Entity> dependencies = new HashSet<Entity>();
		dependencies.add(d_indication);
		dependencies.addAll(d_outcomeMeasures);
		dependencies.addAll(d_drugs);
		dependencies.addAll(d_metaAnalyses);
		return dependencies;
	}

	public void setId(String id) {
		d_id = id;
	}

	public String getId() {
		return d_id;
	}

	public boolean equals(Object other){
		if (other == null)
			return false;
		if (!(other instanceof BenefitRiskAnalysis))
			return false;
		return this.getId().equals( ((BenefitRiskAnalysis)other).getId() );
	}

	public int compareTo(BenefitRiskAnalysis other) {
		if (other == null) {
			return 1;
		}
		return getId().compareTo(other.getId());
	}
}
