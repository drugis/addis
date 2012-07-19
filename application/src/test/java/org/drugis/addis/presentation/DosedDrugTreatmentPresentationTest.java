package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.treatment.CategoryNode;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseRangeNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.entities.treatment.TypeNode;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DosedDrugTreatmentPresentationTest {

	private DosedDrugTreatment d_bean;
	private DosedDrugTreatmentPresentation d_pm;

	@Before
	public void setUp() {
		d_bean = new DosedDrugTreatment("HD/LD", ExampleData.buildDrugCandesartan());
		d_pm = new DosedDrugTreatmentPresentation(d_bean);
	}
	
	@Test
	public void testInitialisation() {
		assertEquals(ExampleData.buildDrugCandesartan(), d_pm.getDrug());
		assertEquals("HD/LD", d_pm.getName().getValue());
		assertEquals(Collections.emptyList(), d_pm.getCategories());
	}
	
	@Test
	public void testMessWithCategories() {
		CategoryNode catNode1 = new CategoryNode("foo");
		CategoryNode catNode2 = new CategoryNode("bar");
		d_pm.getCategories().add(catNode1);
		d_pm.getCategories().add(catNode2);
		assertEquals(Arrays.asList(catNode1, catNode2), d_pm.getCategories());
		d_pm.getCategories().remove(catNode1);
		assertEquals(Arrays.asList(catNode2), d_pm.getCategories());
	}
	
	@Test
	public void testDoseUnit()  {
		assertEquals(ExampleData.MILLIGRAMS_A_DAY, d_pm.getDoseUnitPresentation().getBean());
		d_pm.setDoseUnit(ExampleData.KILOGRAMS_PER_HOUR);
		assertEquals(ExampleData.KILOGRAMS_PER_HOUR, d_pm.getDoseUnitPresentation().getBean());
	}

	@Test
	public void testChainSetNodes() { 
		TypeNode fixedDose = new TypeNode(FixedDose.class);
		RangeNode range = new RangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		CategoryNode lowDoseNode = new CategoryNode("Low dose");
		CategoryNode highDoseNode = new CategoryNode("High dose");

		d_pm.setSelected(d_pm.getBean().getRootNode(), fixedDose);
		d_pm.setSelected(fixedDose, range);
		
		List<RangeNode> ranges = d_pm.splitRange(range, 20, true);
		d_pm.setSelected(ranges.get(0), lowDoseNode);
		d_pm.setSelected(ranges.get(1), highDoseNode);

		FixedDose lowDose = new FixedDose(10.0, ExampleData.MILLIGRAMS_A_DAY);
		FixedDose highDose = new FixedDose(30.0, ExampleData.MILLIGRAMS_A_DAY);
		
		assertEquals(lowDoseNode.getName(), d_pm.getBean().getCategory(lowDose).getName());
		assertEquals(highDoseNode.getName(), d_pm.getBean().getCategory(highDose).getName());

	}
	
	@Test
	public void testGetType() {
		TypeNode fixedDose = new TypeNode(FixedDose.class);
		TypeNode flexibleDose = new TypeNode(FlexibleDose.class);

		RangeNode range1 = new RangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		RangeNode range2 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE);

		d_pm.setSelected(d_pm.getBean().getRootNode(), fixedDose);
		d_pm.setSelected(d_pm.getBean().getRootNode(), flexibleDose);

		assertEquals(fixedDose, d_pm.getType(FixedDose.class));
		assertEquals(flexibleDose, d_pm.getType(FlexibleDose.class));

		d_pm.setSelected(fixedDose, range1);
		d_pm.setSelected(flexibleDose, range2);

		assertEquals(fixedDose, d_pm.getType(FixedDose.class));
		assertEquals(flexibleDose, d_pm.getType(FlexibleDose.class));

		assertEquals(d_pm.getType(FlexibleDose.class), 
				d_pm.getType(FlexibleDose.class));
	}

	@Test
	public void testGetChildren() {
		TypeNode fixedDose = new TypeNode(FixedDose.class);
		TypeNode flexibleDose = new TypeNode(FlexibleDose.class);

		RangeNode range1 = new DoseRangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, ExampleData.MILLIGRAMS_A_DAY);
		RangeNode range2 = new DoseRangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, ExampleData.MILLIGRAMS_A_DAY);

		d_pm.setSelected(d_pm.getBean().getRootNode(), fixedDose);
		d_pm.setSelected(d_pm.getBean().getRootNode(), flexibleDose);
		d_pm.setSelected(fixedDose, range1);
		d_pm.setSelected(flexibleDose, range2);
		
		Collection<DecisionTreeNode> sortedSet = new TreeSet<DecisionTreeNode>();
		sortedSet.addAll(Arrays.asList(range1));
		JUnitUtil.assertAllAndOnly(sortedSet, d_pm.getChildNodes(fixedDose));
	}
	
	@Test
	public void testSetKnownDoses() {
		FixedDose fixedDose = new FixedDose(10, ExampleData.KILOGRAMS_PER_HOUR);
		FlexibleDose flexibleDose = new FlexibleDose(new Interval<Double>(10.0, 20.0), ExampleData.KILOGRAMS_PER_HOUR);
		
		CategoryNode prototype1 = new CategoryNode("Some dose");
		d_pm.setKnownDoses(prototype1);

		DecisionTreeNode fixedNode1 = d_pm.getBean().getCategory(fixedDose);
		DecisionTreeNode flexibleNode1 = d_pm.getBean().getCategory(flexibleDose);
		
		assertEquals(prototype1.toString(), flexibleNode1.toString());
		assertEquals(prototype1.toString(), fixedNode1.toString());

		RangeNode prototype2 = new DoseRangeNode(AbstractDose.class, AbstractDose.PROPERTY_DOSE_TYPE, ExampleData.MILLIGRAMS_A_DAY);
		d_pm.setKnownDoses(prototype2);
		
		DecisionTreeNode fixedNode2 = d_pm.getBean().getCategory(fixedDose);
		DecisionTreeNode flexibleNode2 = d_pm.getBean().getCategory(flexibleDose);
		
		assertTrue(flexibleNode2.similar(prototype2));
		assertTrue(fixedNode2.similar(prototype2));
	}
	
	@Test
	public void testSplitKnownDoses() { 
		FixedDose fixedDose1 = new FixedDose(10, ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexibleDose1 = new FlexibleDose(new Interval<Double>(10.0, 20.0), ExampleData.MILLIGRAMS_A_DAY);
		
		FixedDose fixedDose2 = new FixedDose(40, ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexibleDose2 = new FlexibleDose(new Interval<Double>(40.0, 50.0), ExampleData.MILLIGRAMS_A_DAY);
		
		RangeNode prototype = new DoseRangeNode(AbstractDose.class, AbstractDose.PROPERTY_DOSE_TYPE, ExampleData.MILLIGRAMS_A_DAY);
		d_pm.setKnownDoses(prototype);
		
		DecisionTreeNode fixedNode1 = d_pm.getBean().getCategory(fixedDose1);
		DecisionTreeNode flexibleNode1 = d_pm.getBean().getCategory(flexibleDose1);
		
		DecisionTreeNode fixedNode2 = d_pm.getBean().getCategory(fixedDose2);
		DecisionTreeNode flexibleNode2 = d_pm.getBean().getCategory(flexibleDose2);
		
		assertTrue(prototype.similar(flexibleNode1));
		assertTrue(prototype.similar(fixedNode1));	
		
		List<RangeNode> splits = d_pm.splitKnowDoseRanges(30, false);
		
		fixedNode1 = d_pm.getBean().getCategory(fixedDose1);
		flexibleNode1 = d_pm.getBean().getCategory(flexibleDose1);
		fixedNode2 = d_pm.getBean().getCategory(fixedDose2);
		flexibleNode2 = d_pm.getBean().getCategory(flexibleDose2);
		
		// left hand of the split
		assertTrue(splits.get(0).similar(flexibleNode1));
		assertTrue(splits.get(0).similar(fixedNode1));	
		
		// right hand of the split
		assertTrue(splits.get(1).similar(flexibleNode2));
		assertTrue(splits.get(1).similar(fixedNode2));	
	}
	
	@Test
	public void testSplitKnownDosesTreeStructure() { 
		FixedDose fixedDose1 = new FixedDose(10, ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexibleDose1 = new FlexibleDose(new Interval<Double>(10.0, 20.0), ExampleData.MILLIGRAMS_A_DAY);
		FixedDose fixedDose2 = new FixedDose(40, ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexibleDose2 = new FlexibleDose(new Interval<Double>(40.0, 40.0), ExampleData.MILLIGRAMS_A_DAY);
		
		RangeNode prototype = new RangeNode(AbstractDose.class, AbstractDose.PROPERTY_DOSE_TYPE);
		d_pm.setKnownDoses(prototype);
		
		DecisionTreeNode flexType = d_pm.getType(FlexibleDose.class);
		DecisionTreeNode fixedType = d_pm.getType(FixedDose.class);

		assertTrue(containsNodeString(flexibleDose1, flexType));
		assertTrue(containsNodeString(fixedDose1, fixedType));

		List<RangeNode> splits = d_pm.splitKnowDoseRanges(30, true);
		
		assertTrue(containsNodeString(flexibleDose1, flexType));
		assertTrue(containsNodeString(fixedDose1, fixedType));
		
		assertTrue(containsNodeString(flexibleDose2, flexType));
		assertTrue(containsNodeString(fixedDose2, fixedType));
	}

	private boolean containsNodeString(AbstractDose dose, DecisionTreeNode parent) {
		DecisionTreeNode doseCategory = d_pm.getBean().getCategory(dose);
		System.out.println("doseCat: " + doseCategory);
		for(DecisionTreeNode node : d_pm.getChildNodes(parent)) { 
			System.out.println("  comparing to: " + node);
			if(node.similar(doseCategory)) {
				return true;
			}
		}
		return false;
	}
}
