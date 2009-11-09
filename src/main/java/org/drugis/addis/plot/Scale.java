package org.drugis.addis.plot;

public interface Scale {
	
	public double getNormalized(double x);

	public double getMax(); 
	public double getMin();
}
