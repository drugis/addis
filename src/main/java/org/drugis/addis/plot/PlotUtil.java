package org.drugis.addis.plot;

import java.awt.Graphics2D;

public class PlotUtil {

	public static void drawWeightBox(Graphics2D g2d, int ycentre, int xcentre, int width) {
		assert(width % 2 == 1);
		g2d.fillRect(xcentre - width/2,ycentre - width/2, width, width);		
	}
	
	public static void drawInterval(Graphics2D g2d, int ycentre, int lower, int upper) {
		g2d.drawLine(lower, ycentre, upper, ycentre);
	}
	
}
