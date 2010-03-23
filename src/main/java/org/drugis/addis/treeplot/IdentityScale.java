package org.drugis.addis.treeplot;

public class IdentityScale implements Scale {

	public double getMax() {
		return 1.0;
	}

	public double getMin() {
		return 0.0;
	}

	public double getNormalized(double x) {
		return x;
	}

}
