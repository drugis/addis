package org.drugis.addis.entities.metaanalysis;

import java.util.List;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;

public interface MetaAnalysis extends Entity, Comparable<MetaAnalysis> {
	public static final String PROPERTY_NAME = "name";	
	public String getName();
	public void setName(String name);
	
	public static final String PROPERTY_TYPE = "type";
	public String getType();
	
	public static final String PROPERTY_INDICATION = "indication";
	public Indication getIndication();

	public static final String PROPERTY_OUTCOME_MEASURE = "outcomeMeasure";
	public OutcomeMeasure getOutcomeMeasure();
	
	public static final String PROPERTY_SAMPLE_SIZE = "sampleSize";
	public int getSampleSize();

	public static final String PROPERTY_INCLUDED_DRUGS = "includedDrugs";
	public List<Drug> getIncludedDrugs();
	
	public static final String PROPERTY_INCLUDED_STUDIES = "includedStudies";
	public List<Study> getIncludedStudies();
}
