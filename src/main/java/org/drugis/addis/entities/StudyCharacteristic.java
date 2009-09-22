package org.drugis.addis.entities;

public enum StudyCharacteristic {
		INDICATION("Intended Indication"),
		DUMMY("For Testing Purposes");
		
		private String d_description;
		
		StudyCharacteristic(String description) { 
			d_description = description;
		}
		
		public String getDescription() {
			return d_description;
		}
}
