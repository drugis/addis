package org.drugis.addis.entities;

public enum PredefinedActivity implements Activity {
	RANDOMIZATION("Randomization"),
	SCREENING("Screening"),
	WASH_OUT("Wash out"),
	FOLLOW_UP("Follow up");

	private final String d_description;

	PredefinedActivity(String description) {
		d_description = description;
	}
	
	public String getDescription() {
		return d_description;
	}
}
