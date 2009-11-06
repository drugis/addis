package org.drugis.addis.plot;

import java.awt.geom.Line2D;

public class Line extends Line2D.Double {
	private static final long serialVersionUID = -6107006633910258400L;

	public Line(double d, double e, double f, double g) {
		super(d, e, f, g);
	}

	public boolean equals(Object o) {
		if (o instanceof Line2D) {
			return ((Line2D) o).getP1().equals(getP1()) && ((Line2D) o).getP2().equals(getP2());
		}
		return false;
	}
	
	public String toString() {
		return "Line(x1 = " + getX1() + ", y1 = " + getY1() +
			", x2 = " + getX2() + ", y2 = " + getY2() + ")";
	}
}
