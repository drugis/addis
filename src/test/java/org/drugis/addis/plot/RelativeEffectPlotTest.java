package org.drugis.addis.plot;

import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;

public class RelativeEffectPlotTest {
	@Ignore
	@Test
	public void testNormalPlot() {
		MockGraphics2D g2d = new MockGraphics2D(Collections.singleton(new Line(0, 11, 100, 11)));
		// (Also add the mean-box)
		// Make some BinnedScale that maps [0, 1] -> [0, 200]
		// RelativeEffect with Interval [0, 0.5]
		// RelativeEffectPlot plot = ...
		// plot.paint(g2d)
		g2d.verify();
	}
}
