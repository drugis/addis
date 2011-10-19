package org.drugis.addis.entities;

public enum ScaleModifier {
	MEGA("mega", "M", 1000000),
	KILO("kilo", "k", 1000),
	UNIT("","",1),
	MILLI("milli", "m", 0.001),
	MICRO("micro", "\u03BC", 0.000001);
	
	private final String d_prefix;
	private final String d_symbol;
	private final double d_factor;

	private ScaleModifier(String prefix, String symbol, double factor) {
		d_prefix = prefix;
		d_symbol = symbol;
		d_factor = factor;
	}

	public String getPrefix() {
		return d_prefix;
	}

	public String getSymbol() {
		return d_symbol;
	}

	public double getFactor() {
		return d_factor;
	}
}
