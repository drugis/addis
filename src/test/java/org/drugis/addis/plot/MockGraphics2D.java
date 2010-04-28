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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.batik.ext.awt.g2d.AbstractGraphics2D;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.drugis.common.JUnitUtil;

/**
 * A mock object for testing drawing code. Takes a list of shapes that the client code should draw.
 * verify() then check whether all (and only) expected shapes have been drawn. 
 */
public class MockGraphics2D extends AbstractGraphics2D {
	private Collection<? extends Shape> d_expected;
	private Collection<Shape> d_actual;
	
	public MockGraphics2D(Collection<? extends Shape> expected) {
		super(true);
		gc = new GraphicContext();
		d_expected = expected;
		d_actual = new ArrayList<Shape>();
	}
	
	public void verify() throws AssertionError {
		JUnitUtil.assertAllAndOnly(d_expected, d_actual);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addRenderingHints(Map arg0) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void draw(Shape s) {
		d_actual.add(getTransformed(s));
	}

	private Shape getTransformed(Shape s) {
		Shape transformed = null;
		if (s instanceof Rectangle) {
			Rectangle r = (Rectangle) s;
			if (gc.getTransform().getType() == AffineTransform.TYPE_TRANSLATION ||
					gc.getTransform().getType() == AffineTransform.TYPE_IDENTITY) {
				r.x += gc.getTransform().getTranslateX();
				r.y += gc.getTransform().getTranslateY();
				transformed = r;
			} else {
				throw new RuntimeException("Unsupported Transformation");
			}
		} else if (s instanceof Line2D) {
			Line2D l = (Line2D) s;
			if (gc.getTransform().getType() == AffineTransform.TYPE_TRANSLATION ||
					gc.getTransform().getType() == AffineTransform.TYPE_IDENTITY) {
				transformed = new Line(
						l.getX1() + gc.getTransform().getTranslateX(),
						l.getY1() + gc.getTransform().getTranslateY(),
						l.getX2() + gc.getTransform().getTranslateX(),
						l.getY2() + gc.getTransform().getTranslateY()
						);
			} else {
				throw new RuntimeException("Unsupported Transformation");
			}
		} else {
			throw new RuntimeException("Unsupported Shape");
		}
		return transformed;
	}

	@Override
	public void drawRenderableImage(RenderableImage arg0, AffineTransform arg1) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void drawRenderedImage(RenderedImage arg0, AffineTransform arg1) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void drawString(String arg0, float arg1, float arg2) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void drawString(AttributedCharacterIterator arg0, float arg1,
			float arg2) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void fill(Shape s) {
		Shape t = getTransformed(s);
		if (t instanceof Rectangle) {
			d_actual.add(new FilledRectangle((Rectangle)t, gc.getColor()));
		} else {
			throw new RuntimeException("Unsupported Shape");
		}
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		throw new RuntimeException("Not Implemented");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setRenderingHints(Map arg0) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void copyArea(int arg0, int arg1, int arg2, int arg3, int arg4,
			int arg5) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public Graphics create() {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void dispose() {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, ImageObserver arg3) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3,
			int arg4, ImageObserver arg5) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public FontMetrics getFontMetrics(Font arg0) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void setXORMode(Color arg0) {
		throw new RuntimeException("Not Implemented");
	}
}
