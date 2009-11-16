package org.drugis.addis.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.RelativeEffect.AxisType;
import org.drugis.addis.presentation.ForestPlotPresentation;

public class ForestPlot implements Plot {
	enum Align {
		LEFT,
		CENTER,
		RIGHT
	};
	
	public static final int ROWHEIGHT = 21;
	public static final int ROWVCENTER = ROWHEIGHT / 2 + 1;
	public static final int ROWPAD = 10;
	public static final int FULLROW = ROWHEIGHT + ROWPAD;
	public static final int BARWIDTH = 301;
	public static final int STUDYWIDTH = 201;
	public static final int CIWIDTH = 201;
	public static final int FULLWIDTH = BARWIDTH + STUDYWIDTH + CIWIDTH;
	public static final int TICKLENGTH = 4;
	public static final int HORPAD = 20;
	
	private List<RelativeEffectBar> d_bars;
	private ForestPlotPresentation d_pm;
	
	public ForestPlot (ForestPlotPresentation pm) {
		d_pm = pm;
		d_bars = new ArrayList<RelativeEffectBar>();
		
		int yPos = ROWVCENTER;
		
		for (int i=0; i < d_pm.getNumRelativeEffects(); ++i) {		
			d_bars.add(new RelativeEffectBar(d_pm.getScale(), yPos, (RelativeEffect<?>) d_pm.getRelativeEffectAt(i), d_pm.getDiamondSize(i)));
			yPos += FULLROW;
		}
	}
	
	public void paint(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		//BACKGROUND COLORING:
		Color c = g2d.getColor();
		g2d.setColor(Color.white);
		g2d.fillRect(0, ROWHEIGHT, FULLWIDTH, FULLROW * (d_bars.size() + 4));
		g2d.setColor(c);
		
		//HEADER ROW:
		drawVCentrString(g2d, "Study", 0, 1, Align.LEFT);
		drawVCentrString(g2d, "Relative Effect (95% CI)", 0, FULLWIDTH, Align.RIGHT);
		
		g2d.drawRect(1, ROWHEIGHT, FULLWIDTH, 1);
				
		//STUDY COLUMN & CI COLUMN:
		int yPos = 2 * FULLROW - ROWVCENTER;
		for (int i = 0; i < d_pm.getNumRelativeEffects(); ++i) {
			drawVCentrString(g2d, d_pm.getStudyLabelAt(i), i + 1, 1, Align.LEFT);
			drawVCentrString(g2d, d_pm.getCIlabelAt(i), i + 1, FULLWIDTH, Align.RIGHT);
			yPos += FULLROW;
		}
		
		g2d.translate(STUDYWIDTH, FULLROW);
		for (int i=0; i < d_bars.size(); ++i) {
			d_bars.get(i).paint(g2d);
		}
		paintAxis(g2d);
	}
	
	public void paintAxis(Graphics2D g2d) {
		//Horizontal axis:
		g2d.drawLine(1, FULLROW * d_bars.size(), BARWIDTH, FULLROW * d_bars.size());
		//Vertical axis:
		int originX = d_pm.getScale().getBin(d_pm.getScaleType() == AxisType.LOGARITHMIC ? 1D : 0D).bin;
		g2d.drawLine(originX, 1, originX, FULLROW * d_bars.size());
		
		//Tickmarks:
		int index = 0;
		for (Integer i : d_pm.getTicks()) {
			g2d.drawLine(i, FULLROW * d_bars.size(), i, FULLROW * d_bars.size() + TICKLENGTH);
			drawVCentrString(g2d, d_pm.getTickVals().get(index).toString(), d_bars.size(), i, Align.CENTER);
			++index;
		}
		
		drawVCentrString(g2d, d_pm.getRelativeEffectAt(0).getName(), d_bars.size() + 1, originX, Align.CENTER);
		drawVCentrString(g2d, "Favours " + d_pm.getBaselineDrugLabel(), d_bars.size() + 2, originX - HORPAD, Align.RIGHT);
		drawVCentrString(g2d, "Favours " + d_pm.getSubjectDrugLabel(), d_bars.size() + 2, originX + HORPAD, Align.LEFT);
	}
	
	public Dimension getPlotSize() {
		return new Dimension(FULLWIDTH, FULLROW * (d_bars.size() + 4));
	}
	
	
	private void drawVCentrString(Graphics2D g2d, String text, int rownr, int xpos, Align a) {
		//rownr for the header == 0
		Rectangle2D textBounds = g2d.getFontMetrics().getStringBounds(text, g2d);
		int y = (int) ((FULLROW * rownr) + ROWVCENTER + (textBounds.getHeight() / 2.0));
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
		g2d.drawString(text, x, y);
	}

}
