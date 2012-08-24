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

package org.drugis.addis.plot;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Collections;

import org.junit.Test;

public class MockGraphics2DTest {
	@Test
	public void testEmptyVerify() {
		MockGraphics2D g2d = new MockGraphics2D(Collections.<Shape>emptyList());
		g2d.verify();
	}
	
	@Test(expected=AssertionError.class)
	public void testEmptyVerifyFail() {
		MockGraphics2D g2d = new MockGraphics2D(Collections.<Shape>singletonList(new Rectangle()));
		g2d.verify();
	}
	
	@Test
	public void testDrawRectangle() {
		MockGraphics2D g2d = new MockGraphics2D(Collections.<Shape>singletonList(new Rectangle(10, 10, 20, 20)));
		g2d.drawRect(10, 10, 20, 20);
		g2d.verify();
	}
	
	@Test
	public void testFillRectangle() {
		MockGraphics2D g2d = new MockGraphics2D(Collections.<Shape>singletonList(new FilledRectangle(10, 10, 20, 20)));
		g2d.fillRect(10, 10, 20, 20);
		g2d.verify();
	}
	
	@Test
	public void testTranslateDrawRect() {
		MockGraphics2D g2d = new MockGraphics2D(Collections.<Shape>singletonList(new Rectangle(10, 10, 20, 20)));
		g2d.translate(10, 10);
		g2d.drawRect(0, 0, 20, 20);
		g2d.verify();
	}
	
	@Test
	public void testTranslateFillRect() {
		MockGraphics2D g2d = new MockGraphics2D(Collections.<Shape>singletonList(new FilledRectangle(10, 10, 20, 20)));
		g2d.translate(10, 10);
		g2d.fillRect(0, 0, 20, 20);
		g2d.verify();
	}
	
	@Test
	public void testDrawLine() {
		MockGraphics2D g2d = new MockGraphics2D(Collections.<Shape>singletonList(new Line(10, 10, 15, 20)));
		g2d.drawLine(10, 10, 15, 20);
		g2d.verify();
	}
	
	@Test
	public void testTranslateDrawLine() {
		MockGraphics2D g2d = new MockGraphics2D(Collections.<Shape>singletonList(new Line(20, 20, 25, 30)));
		g2d.translate(10, 10);
		g2d.drawLine(10, 10, 15, 20);
		g2d.verify();
	}
	
	@Test
	public void testFillRectAlternateColor() {
		MockGraphics2D g2d = new MockGraphics2D(Collections.<Shape>singletonList(new FilledRectangle(10, 10, 20, 20, Color.PINK)));
		g2d.setColor(Color.PINK);
		g2d.fillRect(10, 10, 20, 20);
		g2d.verify();
	}
}