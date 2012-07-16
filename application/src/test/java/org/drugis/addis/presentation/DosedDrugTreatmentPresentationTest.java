package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.treatment.CategoryNode;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseRangeNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.entities.treatment.TypeNode;
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
		
		assertEquals(lowDoseNode.getName(), d_pm.getBean().getNode(lowDose).getName());
		assertEquals(highDoseNode.getName(), d_pm.getBean().getNode(highDose).getName());

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
	
}
