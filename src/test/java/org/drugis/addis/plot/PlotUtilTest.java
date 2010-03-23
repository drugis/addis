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
