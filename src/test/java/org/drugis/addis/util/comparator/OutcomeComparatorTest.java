package org.drugis.addis.util.comparator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.OutcomeMeasure;
import org.junit.Before;
import org.junit.Test;

public class OutcomeComparatorTest {
	private OutcomeComparator d_comparator;

	@Before
	public void setup(){
		d_comparator = new OutcomeComparator();
	}

	@Test
	public void testCompareOutcomeMeasure(){
		ArrayList<OutcomeMeasure> list = new ArrayList<OutcomeMeasure>();
		list.add(ExampleData.buildAdverseEventConvulsion());
		list.add(ExampleData.buildEndpointHamd());
		list.add(ExampleData.buildEndpointCgi());
		Collections.sort(list, d_comparator);
		assertEquals(ExampleData.buildEndpointCgi(), list.get(0));
		assertEquals(ExampleData.buildEndpointHamd(), list.get(1));
		assertEquals(ExampleData.buildAdverseEventConvulsion(), list.get(2));
	}
}
