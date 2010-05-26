/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

package org.drugis.addis.treeplot;

import java.awt.Graphics2D;

import org.drugis.addis.entities.relativeeffect.RelativeEffect;

public class RelativeEffectBar implements TreePlot {
	private int d_confStart;
	private int d_confEnd;
	private int d_diamondCenter;
	private int d_diamondSize;
	private int d_yCentre;

	public RelativeEffectBar(BinnedScale scale, int yCentre, RelativeEffect<?> effect, int diamondW) {
		d_yCentre = yCentre;
		
		// Calculate parameters of confidence interval line.
		d_confStart = scale.getBin(effect.getConfidenceInterval().getLowerBound()).bin;
		d_confEnd = scale.getBin(effect.getConfidenceInterval().getUpperBound()).bin;
		
		// Calculate parameters of mean-diamond.
		d_diamondCenter = scale.getBin(effect.getConfidenceInterval().getPointEstimate()).bin;
		d_diamondSize = diamondW; 
	}
	
	public void paint(Graphics2D g2d) {
		// Draw the confidence interval line.
		g2d.drawLine( d_confStart, d_yCentre, d_confEnd, d_yCentre);
		
		// Draw the mean-diamond
		if (d_diamondSize == 0) {
			int size = 8;
			g2d.drawLine(d_diamondCenter + size, d_yCentre, d_diamondCenter, d_yCentre + size);
			g2d.drawLine(d_diamondCenter, d_yCentre + size, d_diamondCenter - size, d_yCentre);
			g2d.drawLine(d_diamondCenter - size, d_yCentre, d_diamondCenter, d_yCentre - size);
			g2d.drawLine(d_diamondCenter, d_yCentre - size, d_diamondCenter + size, d_yCentre);
		} else {
			int x = d_diamondCenter - (d_diamondSize - 1)/2;
			int y = d_yCentre - (d_diamondSize - 1)/2;
			g2d.fillRect(x, y, d_diamondSize, d_diamondSize);
		}
	}
}
