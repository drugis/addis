package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.math.DoubleRange;
import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.entities.treatment.CategoryNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.entities.treatment.TypeNode;
import org.drugis.addis.util.BoundedInterval;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;
import org.mvel2.ast.AssertNode;

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
	public void testSetCategoryTypeNode() {
		CategoryNode catNode1 = new CategoryNode("foo");
		CategoryNode catNode2 = new CategoryNode("bar");
		
		d_pm.setSelected(new TypeNode(FixedDose.class), catNode1);
		d_pm.setSelected(new TypeNode(UnknownDose.class), catNode2);
		assertEquals(catNode1, d_pm.getSelectedCategory(new TypeNode(FixedDose.class)).getValue());
		assertEquals(catNode2, d_pm.getSelectedCategory(new TypeNode(UnknownDose.class)).getValue());
	}
	
	@Test
	public void testSetCategoryRangeNode() {
		CategoryNode catNode1 = new CategoryNode("foo");
		RangeNode node = new RangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		d_pm.setSelected(node, catNode1);
		assertEquals(catNode1, d_pm.getSelectedCategory(node).getValue());
	}
	
	@Test
	public void overwriteCategory() { 
		CategoryNode catNode1 = new CategoryNode("foo");
		CategoryNode catNode2 = new CategoryNode("bar");
		CategoryNode catNode3 = new CategoryNode("qux");
		
		RangeNode range1 = new RangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, new BoundedInterval(new DoubleRange(0.0, 15.0), false, true));
		RangeNode range2 = new RangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, new BoundedInterval(new DoubleRange(0.0, 20.0), false, true));

		d_pm.setSelected(range1, catNode1);
		d_pm.setSelected(range2, catNode2);
		assertEquals(catNode1, d_pm.getSelectedCategory(range1).getValue());
		assertEquals(catNode2, d_pm.getSelectedCategory(range2).getValue());

		d_pm.setSelected(range2, catNode3);
		assertEquals(catNode1, d_pm.getSelectedCategory(range1).getValue());
		assertEquals(catNode3, d_pm.getSelectedCategory(range2).getValue());

	}
	
	@Test
	public void testSetCategoryOnRangeNode() { 
		CategoryNode catNode1 = new CategoryNode("foo");
		CategoryNode catNode2 = new CategoryNode("bar");
		
		RangeNode node1 = new RangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		d_pm.setSelected(node1, catNode1);
		
		List<RangeNode> ranges = d_pm.splitRange(node1, 50, false);
		d_pm.setSelected(ranges.get(0), catNode1);
		d_pm.setSelected(ranges.get(1), catNode2);

		assertEquals(catNode1, d_pm.getSelectedCategory(ranges.get(0)).getValue());
		assertEquals(catNode2, d_pm.getSelectedCategory(ranges.get(1)).getValue());
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
		
		assertEquals(lowDoseNode, d_pm.getBean().getNode(lowDose));
		assertEquals(highDoseNode, d_pm.getBean().getNode(highDose));

	}
	
	@Test
	public void testGetParentNode() {
		TypeNode fixedDose = new TypeNode(FixedDose.class);
		TypeNode flexibleDose = new TypeNode(FlexibleDose.class);

		RangeNode range1 = new RangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		RangeNode range2 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE);

		d_pm.setSelected(d_pm.getBean().getRootNode(), fixedDose);
		d_pm.setSelected(d_pm.getBean().getRootNode(), flexibleDose);

		assertEquals(d_pm.getBean().getRootNode(), d_pm.getParentNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY));
		assertEquals(d_pm.getBean().getRootNode(), d_pm.getParentNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE));

		d_pm.setSelected(fixedDose, range1);
		d_pm.setSelected(flexibleDose, range2);

		assertEquals(fixedDose, d_pm.getParentNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY));
		assertEquals(flexibleDose, d_pm.getParentNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE));
		JUnitUtil.assertNotEquals(d_pm.getParentNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE), d_pm.getParentNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE));

	}
	
}
