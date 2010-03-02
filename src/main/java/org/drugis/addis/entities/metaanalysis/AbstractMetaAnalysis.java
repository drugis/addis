package org.drugis.addis.entities.metaanalysis;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;

public abstract class AbstractMetaAnalysis extends AbstractEntity implements MetaAnalysis {
	private static final long serialVersionUID = 6504073155207712299L;

	public static final String PROPERTY_TYPE = "type";
	public static final String PROPERTY_INDICATION = "indication";
	public static final String PROPERTY_OUTCOME_MEASURE = "outcomeMeasure";
	public static final String PROPERTY_SAMPLE_SIZE = "sampleSize";
	public static final String PROPERTY_INCLUDED_STUDIES_COUNT = "studiesIncluded";
	public static final String PROPERTY_INCLUDED_DRUGS = "includedDrugs";
	
	protected OutcomeMeasure d_outcome;
	protected Indication d_indication;
	protected List<? extends Study> d_studies;
	protected List<Drug> d_drugs;
	protected String d_name;
	protected int d_totalSampleSize;

	public AbstractMetaAnalysis(String name, 
			Indication indication, OutcomeMeasure om,
			List<? extends Study> studies, List<Drug> drugs) 
	throws IllegalArgumentException {
		if (studies.isEmpty()) {
			throw new IllegalArgumentException("studylist empty");
		}
		checkSameIndication(studies, indication);
		
		d_drugs = drugs;
		d_studies = studies;
		d_indication = indication;
		d_outcome = om;
		d_name = name;

		for (Study s : d_studies) {
			d_totalSampleSize += s.getSampleSize();
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public void setName(String name) {
		String oldName = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldName, d_name);
	}

	protected void checkSameIndication(List<? extends Study> studies, Indication indication)
	throws IllegalArgumentException {
		for (int i = 1; i < studies.size(); i++) {
			Indication ind2 = studies.get(i).getIndication();
			if (!ind2.equals(indication)) {
				throw new IllegalArgumentException("different indications in studies");
			}
		}
	}

	public String getName() {
		return d_name;
	}

	public String getType() {
		return "DerSimonian-Laird Random Effects";
	}

	public int getSampleSize() {
		return d_totalSampleSize;
	}

	public List<Study> getStudies() {
		return Collections.unmodifiableList(d_studies);
	}

	public OutcomeMeasure getOutcomeMeasure() {
		return d_outcome;
	}

	public int getStudiesIncluded() {
		return d_studies.size();
	}

	@Override
	public Set<Entity> getDependencies() {
		HashSet<Entity> deps = new HashSet<Entity>();
		deps.addAll(getIncludedDrugs());
		deps.add(getIndication());
		deps.add(getOutcomeMeasure());
		deps.addAll(getStudies());
		return deps;
	}

	public Indication getIndication() {
		return d_indication;
	}

	public int compareTo(MetaAnalysis o) {
		return getName().compareTo(o.getName());
	}
	
	public List<Drug> getIncludedDrugs() {
		return Collections.unmodifiableList(d_drugs);
	}
}