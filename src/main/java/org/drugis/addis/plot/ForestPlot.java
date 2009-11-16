package org.drugis.addis.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.presentation.ForestPlotPresentation;

public class ForestPlot implements Plot {
	enum Align {
		LEFT,
		CENTER,
		RIGHT
	};
	
	public static final int ROWHEIGHT = 21;
	public static final int ROWPAD = 10;
	private static final int FULLROW = ROWHEIGHT + ROWPAD;
	public static final int BARWIDTH = 301;
	public static final int STUDYWIDTH = 201;
	public static final int CIWIDTH = 201;
	private static final int TOTALWIDTH = BARWIDTH + STUDYWIDTH + CIWIDTH;
		
	private List<RelativeEffectBar> d_bars;
	private ForestPlotPresentation d_pm;
	
	public ForestPlot (ForestPlotPresentation pm) {
		d_pm = pm;
		d_bars = new ArrayList<RelativeEffectBar>();
		
		int yPos = ROWHEIGHT / 2 + ROWPAD;
		
		for (int i=0; i < d_pm.getNumRelativeEffects(); ++i) {		
			d_bars.add(new RelativeEffectBar(d_pm.getScale(), yPos, (RelativeEffect<?>) d_pm.getRelativeEffectAt(i)));
			yPos += FULLROW;
		}
	}
	
	public void paint(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		//BACKGROUND COLORING:
		Color c = g2d.getColor();
		g2d.setColor(Color.lightGray);
		g2d.fillRect(0, FULLROW, STUDYWIDTH - ROWPAD, FULLROW * (d_bars.size()));
		g2d.fillRect(TOTALWIDTH - (CIWIDTH - ROWPAD) , FULLROW, CIWIDTH - ROWPAD, FULLROW * (d_bars.size()));
		g2d.setColor(c);
		
		//HEADER ROW:
		drawVCentrString(g2d, "Study", 0, 1, Align.LEFT);
		drawVCentrString(g2d, "Relative Effect (95% CI)", 0, TOTALWIDTH, Align.RIGHT);
		
		g2d.drawLine(1, ROWHEIGHT, TOTALWIDTH, ROWHEIGHT);
		g2d.drawLine(1, ROWHEIGHT + 1, TOTALWIDTH, ROWHEIGHT + 1);
		
		//STUDY COLUMN & CI COLUMN:
		int yPos = 2 * FULLROW - ROWHEIGHT / 2;
		for (int i = 0; i < d_pm.getNumRelativeEffects(); ++i) {
			g2d.drawString(d_pm.getStudyLabelAt(i), 1, yPos);
						
			String CI = d_pm.getCIlabelAt(i);
			Rectangle2D boundsCIR = g2d.getFontMetrics().getStringBounds(CI, g2d);
			g2d.drawString(CI, (int) (TOTALWIDTH - boundsCIR.getWidth()), yPos);
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
		g2d.drawLine(d_pm.getScale().getBin(0.0).bin, 1, d_pm.getScale().getBin(0.0).bin, FULLROW * d_bars.size());
		
		//Tickmarks:
		int index = 0;
		for (Integer i : d_pm.getTicks()) {
			g2d.drawLine(i, FULLROW * d_bars.size(), i, FULLROW * d_bars.size() + 4);
			g2d.drawString(d_pm.getTickVals().get(index).toString(), i, FULLROW * d_bars.size() + 4 + ROWPAD);
			++index;
		}
		
		String bottomText = d_pm.getRelativeEffectAt(0).getName();
		
		Rectangle2D stringBounds = g2d.getFontMetrics().getStringBounds(bottomText, g2d);
		
		g2d.drawString(bottomText, (int) (d_pm.getScale().getBin(0).bin - stringBounds.getWidth() / 2), (int) (FULLROW * (d_bars.size() + 1)));
	}
	
	public Dimension getPlotSize() {
		return new Dimension(TOTALWIDTH, FULLROW * (d_bars.size() + 2));
	}
	
	
	private void drawVCentrString(Graphics2D g2d, String text, int rownr, int xpos, Align a) {
		//rownr for the header == 0
		Rectangle2D textBounds = g2d.getFontMetrics().getStringBounds(text, g2d);
		int y = (int) ((ROWHEIGHT / 2) * (rownr + 1) + textBounds.getHeight() / 2);
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
