package org.drugis.common;

import java.io.Serializable;

public class Gauss implements Serializable {
	private static final long serialVersionUID = -564129541401686189L;

	private double d_mean;
	private double d_std;
	
	public Gauss(double mean, double std) {
		d_mean = mean;
		d_std = std;
	}
	
	@Override
	public String toString() {
		return d_mean + " ± " + d_std;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Gauss) {
			Gauss other = (Gauss) o;
			return toString().equals(other.toString());
		}
		
		return false;
	}

}
