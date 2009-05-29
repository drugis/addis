package nl.rug.escher.addis.entities;

import java.io.Serializable;

public enum SIUnit implements Serializable {
	MILLIGRAMS_A_DAY("Milligrams a Day", "mg/day");
	
	private static final long serialVersionUID = 1L;
	private String d_name;
	private String d_symbol;
	private SIUnit(String name, String symbol) {
		d_name = name;
		d_symbol = symbol;
	}
	
	public String getName() {
		return d_name;
	}
	
	public String getSymbol() {
		return d_symbol;
	}
	
	public String toString() {
		return d_symbol;
	}
}
