/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.forestplot;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.presentation.ForestPlotPresentation;

@SuppressWarnings("serial")
public class ForestPlot extends JComponent {
	enum Align {
		LEFT,
		CENTER,
		RIGHT
	};
	
	public static final int PADDING = 4;
	public static final int ROWHEIGHT = 21;
	public static final int ROWVCENTER = ROWHEIGHT / 2 + 1;
	public static final int ROWPAD = 10;
	public static final int FULLROW = ROWHEIGHT + ROWPAD;
	public static final int BARWIDTH = 301;
	public static final int STUDYWIDTH = 192;
	public static final int CIWIDTH = 192;
	public static final int FULLWIDTH = BARWIDTH + STUDYWIDTH + CIWIDTH;
	public static final int TICKLENGTH = 4;
	public static final int HORPAD = 20;
	
	private List<RelativeEffectBar> d_bars;
	private ForestPlotPresentation d_pm;
	private Graphics2D d_g2d;
	
	public ForestPlot(ForestPlotPresentation pres) {
		d_pm = pres;
		d_bars = new ArrayList<RelativeEffectBar>();
		
		int yPos = ROWVCENTER;
		
		for (int i=0; i < d_pm.getNumRelativeEffects(); ++i) {		
			d_bars.add(new RelativeEffectBar(d_pm.getScale(), yPos, (RelativeEffect<?>) d_pm.getRelativeEffectAt(i), 
					d_pm.getDiamondSize(i), d_pm.isPooledRelativeEffect(i)));
			yPos += FULLROW;
		}
	}
	
	@Override
	public Dimension getSize() {
		int y = (int) ((FULLROW * (d_bars.size() + 4)) + ROWVCENTER + d_g2d.getFontMetrics().getHeight());
		return new Dimension(FULLWIDTH + 20, y);
	}
	
	@Override
	public void paint(Graphics g) {
		d_g2d = (Graphics2D) g;
		d_g2d.translate(PADDING, PADDING);
		
		d_g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		//HEADER ROW:
		drawVerticalCenterString(d_g2d, "Study", 1, Align.LEFT, FULLROW * 0);
		drawVerticalCenterString(d_g2d, "Relative Effect (95% CI)", FULLWIDTH, Align.RIGHT, FULLROW * 0);
		
		d_g2d.drawRect(1, ROWHEIGHT, FULLWIDTH, 1);
				
		//STUDY COLUMN & CI COLUMN:
		for (int i = 0; i < d_pm.getNumRelativeEffects(); ++i) {
			drawVerticalCenterString(d_g2d, d_pm.getStudyLabelAt(i), 1, Align.LEFT, FULLROW * (i + 1));
			drawVerticalCenterString(d_g2d, d_pm.getCIlabelAt(i), FULLWIDTH, Align.RIGHT, FULLROW * (i + 1));
		}
		
		d_g2d.translate(STUDYWIDTH, FULLROW);
		for (int i=0; i < d_bars.size(); ++i) {
			if (d_pm.getRelativeEffectAt(i).isDefined())
				d_bars.get(i).paint(d_g2d);
		}
		paintAxis(d_g2d);
	}

	private int getNumRows() {
		return (d_bars.size() + (d_pm.hasPooledRelativeEffect() ? 5 : 4));
	}
	
	public void paintAxis(Graphics2D g2d) {
		//Horizontal axis:
		int y0 = FULLROW * d_bars.size();
		g2d.drawLine(1, y0, BARWIDTH, y0);
		//Vertical axis:
		int originX = d_pm.getScale().getBin(d_pm.getScaleType() == AxisType.LOGARITHMIC ? 1D : 0D).bin;
		g2d.drawLine(originX, 1 - ROWPAD, originX, y0);
		
		//Tickmarks:
		List<Integer> ticks = d_pm.getTicks();
		List<String> tickVals = d_pm.getTickVals();
		drawAxisTicks(g2d, y0, ticks, tickVals);
		
		drawVerticalCenterString(g2d, d_pm.getRelativeEffectAt(0).getName(), originX, Align.CENTER, FULLROW * (d_bars.size() + 1));
		drawVerticalCenterString(g2d, ("Favours " + d_pm.getLowValueFavors()), (originX - HORPAD), Align.RIGHT, FULLROW * (d_bars.size() + 2));
		drawVerticalCenterString(g2d, ("Favours " + d_pm.getHighValueFavors()), (originX + HORPAD), Align.LEFT, FULLROW * (d_bars.size() + 2));
		
		// Draw the Heterogeneity
		if (d_pm.hasPooledRelativeEffect()) {
			drawVerticalCenterString(g2d, ("Heterogeneity = " + d_pm.getHeterogeneity() + " (I\u00B2 = " + d_pm.getHeterogeneityI2() + ")"), (FULLWIDTH / 4), Align.CENTER, FULLROW * (d_bars.size() + 3));
			//draw dashed line from the combined diamond:
			float[] dash = { 1f, 1f, 1f };
			g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, 
	                BasicStroke.JOIN_ROUND, 1.0f, dash, 2f ));
			g2d.drawLine(d_pm.getScale().getBin(d_pm.getRelativeEffectAt(d_pm.getNumRelativeEffects()-1).getConfidenceInterval().getPointEstimate()).bin, 
					(FULLROW * (d_bars.size() - 1)) + 3,
					d_pm.getScale().getBin(d_pm.getRelativeEffectAt(d_pm.getNumRelativeEffects()-1).getConfidenceInterval().getPointEstimate()).bin, 
					ROWVCENTER);
		}
	}

	public static void drawAxisTicks(Graphics g, int y0, List<Integer> ticks, List<String> tickVals) {
		int index = 0;
		for (Integer i : ticks) {
			g.drawLine(i, y0, i, y0 + TICKLENGTH);
			drawVerticalCenterString(g, tickVals.get(index).toString(), i, Align.CENTER, y0);
			++index;
		}
	}
	
	public Dimension getPlotSize() {
		return new Dimension(2 * PADDING + FULLWIDTH, FULLROW * getNumRows() + ROWPAD + 2 * PADDING);
	}
	
	
	private static void drawVerticalCenterString(Graphics g, String text, int xpos, Align a, int y0) {
		Rectangle2D textBounds = g.getFontMetrics().getStringBounds(text, g);
		int y = (int) (y0 + ROWVCENTER + (textBounds.getHeight() / 2.0));
				
		int x = 1;
		
		switch(a) {
			case LEFT:
				x = xpos;
				break;
			case CENTER:
				x = (int) (xpos - textBounds.getWidth() / 2);
				break;
			case RIGHT:
				x = (int) (xpos - textBounds.getWidth());
				break;
		}
		g.drawString(text, x, y);
	}

}
