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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

import java.awt.Graphics2D;

import org.drugis.addis.entities.relativeeffect.ConfidenceInterval;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;

public class RelativeEffectBar implements Paintable {
	private int d_confStart;
	private int d_confEnd;
	private int d_diamondCenter;
	private int d_diamondSize;
	private int d_yCentre;
	private final boolean d_drawDiamond;

	public RelativeEffectBar(BinnedScale scale, int yCentre, RelativeEffect<?> effect, int diamondW, boolean drawDiamond) {
		this(scale, yCentre, effect.getConfidenceInterval(), diamondW, drawDiamond);
	}
	
	public RelativeEffectBar(BinnedScale scale, int yCentre, ConfidenceInterval ci, int diamondW, boolean drawDiamond) {
		d_yCentre = yCentre;
		d_drawDiamond = drawDiamond;
		
		// Calculate parameters of confidence interval line.
		d_confStart = scale.getBin(ci.getLowerBound()).bin;
		d_confEnd = scale.getBin(ci.getUpperBound()).bin;
		
		// Calculate parameters of mean-diamond.
		d_diamondCenter = scale.getBin(ci.getPointEstimate()).bin;
		d_diamondSize = diamondW; 
	}
	
	public void paint(Graphics2D g2d) {
		// Draw the confidence interval line.
		g2d.drawLine(d_confStart, d_yCentre, d_confEnd, d_yCentre);
		
		// Draw the mean-diamond
		if (d_drawDiamond) {
			g2d.drawLine(d_diamondCenter + d_diamondSize, d_yCentre, d_diamondCenter, d_yCentre + d_diamondSize);
			g2d.drawLine(d_diamondCenter, d_yCentre + d_diamondSize, d_diamondCenter - d_diamondSize, d_yCentre);
			g2d.drawLine(d_diamondCenter - d_diamondSize, d_yCentre, d_diamondCenter, d_yCentre - d_diamondSize);
			g2d.drawLine(d_diamondCenter, d_yCentre - d_diamondSize, d_diamondCenter + d_diamondSize, d_yCentre);
		} else {
			int x = d_diamondCenter - (d_diamondSize - 1)/2;
			int y = d_yCentre - (d_diamondSize - 1)/2;
			g2d.fillRect(x, y, d_diamondSize, d_diamondSize);
		}
	}

	public boolean isDrawDiamond() {
		return d_drawDiamond;
	}
}
