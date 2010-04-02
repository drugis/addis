/**
 * 
 */
package org.drugis.addis.entities;

import org.drugis.addis.util.EnumXMLFormat;

public enum Source {
	MANUAL("Manual Input"), 
	CLINICALTRIALS("ClinicalTrials.gov");
	
	private String d_description;
	Source(String description) {
		d_description = description;
	}
	
	@Override
	public String toString() {
		return d_description;
	}
	
	static EnumXMLFormat<Source> XML = new EnumXMLFormat<Source>(Source.class);
}