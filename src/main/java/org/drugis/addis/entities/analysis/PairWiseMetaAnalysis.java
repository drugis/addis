package org.drugis.addis.entities.analysis;

import org.drugis.addis.entities.Drug;

public interface PairWiseMetaAnalysis extends MetaAnalysis {

	public static final String PROPERTY_FIRST_DRUG = "firstDrug";
	public static final String PROPERTY_SECOND_DRUG = "secondDrug";

	public Drug getFirstDrug();

	public Drug getSecondDrug();

}