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

package org.drugis.addis.plot;

import java.util.Collections;

import org.drugis.addis.treeplot.PlotUtil;
import org.junit.Test;

public class PlotUtilTest {
	
	@Test
	public void testDrawWeightBox() {
		// We're drawing on a 201x21 grid upperLeft -> lowerRight : (0, 0) -> (200, 20)
		MockGraphics2D g2d = new MockGraphics2D(Collections.singleton(new FilledRectangle(48, 8, 5, 5)));
		PlotUtil.drawWeightBox(g2d, 10, 50, 5);
		g2d.verify();
	}
	
	@Test
	public void testDrawWeightBoxMaxSize() {
		MockGraphics2D g2d = new MockGraphics2D(Collections.singleton(new FilledRectangle(40, 0, 21, 21)));
		PlotUtil.drawWeightBox(g2d, 10, 50, 21);
		g2d.verify();
	}
	
	@Test
	public void testDrawInterval() {
		MockGraphics2D g2d = new MockGraphics2D(Collections.singleton(new Line(20, 10, 75, 10)));
		PlotUtil.drawInterval(g2d, 10, 20, 75);
		g2d.verify();
	}
}
