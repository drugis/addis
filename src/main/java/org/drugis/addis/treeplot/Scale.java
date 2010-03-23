package org.drugis.addis.treeplot;

public interface Scale {
	
	public double getNormalized(double x);

	public double getMax(); 
	public double getMin();
}
