package org.drugis.addis.plot;

import java.awt.Color;
import java.awt.Rectangle;

public class FilledRectangle extends Rectangle {
	private static final long serialVersionUID = 2895176337497405740L;
	
	private Color d_color;
	
	FilledRectangle(int x, int y, int width, int height, Color color) {
		super(x,y,width,height);
		d_color = color;
	}
	
	FilledRectangle(int x, int y, int width, int height) {
		super(x,y,width,height);
		d_color = Color.BLACK;
	}
	
	FilledRectangle(Rectangle r) {
		this(r.x, r.y, r.width, r.height);
	}
	
	FilledRectangle(Rectangle r, Color c) {
		this(r.x, r.y, r.width, r.height, c);
	}

	public boolean equals(Object o) {
		if (o instanceof FilledRectangle) {
			FilledRectangle other = (FilledRectangle) o;
			return super.equals(other) && other.d_color.equals(d_color);
		}			
		return false;
	}
	
	public String toString() {
		return "FilledRectangle(x = " + x + ", y = " + y +
			", width = " + width + ", height = " + height +
			", color = " + d_color + ")";
	}
}
