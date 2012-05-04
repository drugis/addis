/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.plot;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.forestplot.BinnedScale;
import org.drugis.addis.forestplot.IdentityScale;
import org.drugis.addis.forestplot.RelativeEffectBar;
import org.junit.Test;

public class RelativeEffectBarTest {
	
	@Test
	public void testNormalPlot() {
		// Create relative effect.
		Arm p1 = new Arm("p1", 100);
		Arm p2 = new Arm("p2", 100);
		RelativeEffect<ContinuousMeasurement> effect = new BasicMeanDifference(new BasicContinuousMeasurement(0.25, 1.26 / Math.sqrt(2), p1.getSize()), 
											new BasicContinuousMeasurement(0.5, 1.26 / Math.sqrt(2), p2.getSize()));
	
		// Make some BinnedScale that maps [0, 1] -> [0, 200]
		BinnedScale bsl = new BinnedScale(new IdentityScale(), 0, 200);
	
		// set confidence interval line in mock.
		Integer lowerX = bsl.getBin(effect.getConfidenceInterval().getLowerBound()).bin;
		Integer upperX = bsl.getBin(effect.getConfidenceInterval().getUpperBound()).bin;
		
		Collection<Shape> shapeSet = new ArrayList<Shape>();
		shapeSet.add(new Line(lowerX , 11, upperX , 11));
		
		// set Mean box in mock.
		shapeSet.add(new FilledRectangle( bsl.getBin(effect.getConfidenceInterval().getPointEstimate()).bin - 2, 11 - 2, 5, 5) );
	
		MockGraphics2D g2d = new MockGraphics2D(shapeSet);
		RelativeEffectBar plot = new RelativeEffectBar(bsl, 11, effect, 5, false);
		plot.paint(g2d);
		
		g2d.verify();
	}
	
	@Test
	public void testCombinedPlot() {
		Arm p1 = new Arm("p1", 100);
		Arm p2 = new Arm("p2", 100);
		RelativeEffect<ContinuousMeasurement> effect = new BasicMeanDifference(new BasicContinuousMeasurement(0.25, 1.26 / Math.sqrt(2), p1.getSize()), 
											new BasicContinuousMeasurement(0.5, 1.26 / Math.sqrt(2), p2.getSize()));
	
		// Make some BinnedScale that maps [0, 1] -> [0, 200]
		BinnedScale bsl = new BinnedScale(new IdentityScale(), 0, 200);
	
		// set confidence interval line in mock.
		Integer lowerX = bsl.getBin(effect.getConfidenceInterval().getLowerBound()).bin;
		Integer upperX = bsl.getBin(effect.getConfidenceInterval().getUpperBound()).bin;
		
		Collection<Shape> shapeSet = new ArrayList<Shape>();
		shapeSet.add(new Line(lowerX , 11, upperX , 11));
		
		// set Mean box in mock.
		int center = bsl.getBin(effect.getConfidenceInterval().getPointEstimate()).bin;
		shapeSet.add(new Line(center + 8, 11, center, 19));
		shapeSet.add(new Line(center, 19, center - 8, 11));
		shapeSet.add(new Line(center - 8, 11, center, 3));
		shapeSet.add(new Line(center, 3, center + 8, 11));
	
		MockGraphics2D g2d = new MockGraphics2D(shapeSet);
		RelativeEffectBar plot = new RelativeEffectBar(bsl, 11, effect, 8, true);
		plot.paint(g2d);
		
		g2d.verify();
		
	}
}
