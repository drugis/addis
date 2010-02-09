/**
 * 
 */
package org.drugis.addis.entities;

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
}