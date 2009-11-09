package org.drugis.addis.plot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.plot.BinnedScale.Bin;
import org.drugis.common.Interval;
import org.junit.Test;

public class ScaleTest {

	@Test
	public void testIdentityThrows() {
		IdentityScale id = new IdentityScale();
		assertEquals(1.1D, id.getNormalized(1.1), 0.0001);
	}
	
	@Test
	public void testLinearScale() {
		LinearScale ls = new LinearScale(new Interval<Double>(10.0,20.0));
		assertEquals(0.5, ls.getNormalized(15.0), 0.0001);
	}
	
	@Test
	public void testLogScale() {
		LogScale los = new LogScale(new Interval<Double>(0.1,10.0));
		assertEquals(0.5, los.getNormalized(Math.exp( (Math.log(10.0) - Math.log(0.1)) / 2 + Math.log(0.1) )), 0.0001);
	}
	
	@Test
	public void testLogScale10base() {
		LogScale los = new LogScale(new Interval<Double>(0.1,1000.0));
		assertEquals(0.75, los.getNormalizedLog10(100), 0.0001);
	}
	
	@Test
	public void testBinnedScale() {
		BinnedScale bs = new BinnedScale(new IdentityScale(), 1, 201);
		Bin b = bs.getBin(0.75);
		assertTrue(!b.outOfBoundsMax);
		assertTrue(!b.outOfBoundsMin);
		assertEquals((int) 151, (int) b.bin);
	}
	
	@Test
	public void testBinnedScaleOutofBounds() {
		BinnedScale bs = new BinnedScale(new IdentityScale(), 1, 201);
		assertTrue(bs.getBin(1.1).outOfBoundsMax);
		assertTrue(bs.getBin(-0.1).outOfBoundsMin);
	}
}
