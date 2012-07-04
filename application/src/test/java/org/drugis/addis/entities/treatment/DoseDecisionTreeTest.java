package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.common.Interval;
import org.junit.Test;

public class DoseDecisionTreeTest {
	@Test
	public void testSplit() {
		DosedDrugTreatment ddt = new DosedDrugTreatment("Test", ExampleData.buildDrugCandesartan());

		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 0, true, 40, false);

		FlexibleDose flexDose1 = new FlexibleDose(new Interval<Double>(10.0, 15.0), ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexDose2 = new FlexibleDose(new Interval<Double>(25.0, 30.0), ExampleData.MILLIGRAMS_A_DAY);
		ddt.getDecisionTree().setChild(ddt.getRootNode(), node1);
		
		List<RangeNode> ranges = ddt.getDecisionTree().splitChildRange(ddt.getRootNode(), 20, true);

		RangeNode left = ranges.get(0);
		assertTrue(left.decide(flexDose1));
		assertFalse(left.decide(flexDose2));

		RangeNode right = ranges.get(1);
		assertFalse(right.decide(flexDose1));
		assertTrue(right.decide(flexDose2));
	}
}
