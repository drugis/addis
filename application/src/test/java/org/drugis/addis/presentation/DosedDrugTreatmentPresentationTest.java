package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.entities.treatment.CategoryNode;
import org.drugis.addis.entities.treatment.DoseRangeNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.entities.treatment.TypeNode;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation.DecisionTreeCoordinate;
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
	public void testSetCategoryTypeNode() {
		TypeNode rootNode = TypeNode.createDefaultTypeNode();
		d_pm.getBean().setRootNode(rootNode);
		CategoryNode catNode1 = new CategoryNode("foo");
		CategoryNode catNode2 = new CategoryNode("bar");
		d_pm.setChildNode(FixedDose.class, catNode1);
		d_pm.setChildNode(UnknownDose.class, catNode2);
		assertEquals(catNode1, d_pm.getSelectedCategory(FixedDose.class).getValue());
		assertEquals(catNode2, d_pm.getSelectedCategory(UnknownDose.class).getValue());
	}
	
	@Test
	public void testSetCategoryRangeNode() {
		RangeNode rootNode = new RangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		d_pm.getBean().setRootNode(rootNode);
		CategoryNode catNode1 = new CategoryNode("foo");
		d_pm.setChildNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, catNode1);
		assertEquals(catNode1, d_pm.getSelectedCategory(FixedDose.class, FixedDose.PROPERTY_QUANTITY).getValue());
	}
	
	@Test
	public void testSetCategoryOnRangeNodeWithIndex() { 
		RangeNode rootNode = new RangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		d_pm.getBean().setRootNode(rootNode);
		CategoryNode catNode1 = new CategoryNode("foo");
		CategoryNode catNode2 = new CategoryNode("bar");
		
		int node2 = rootNode.addCutOff(50, false);
		d_pm.setChildNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, 0, catNode1);
		d_pm.setChildNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, node2, catNode2);
		assertEquals(catNode1, d_pm.getSelectedCategory(FixedDose.class, FixedDose.PROPERTY_QUANTITY, 0).getValue());
		assertEquals(catNode2, d_pm.getSelectedCategory(FixedDose.class, FixedDose.PROPERTY_QUANTITY, node2).getValue());
	}
	
	@Test
	public void testCoordinateEquals() {
		DecisionTreeCoordinate c0 = new DosedDrugTreatmentPresentation.DecisionTreeCoordinate(FixedDose.class, FixedDose.PROPERTY_QUANTITY, 0);
		DecisionTreeCoordinate c1 = new DosedDrugTreatmentPresentation.DecisionTreeCoordinate(FixedDose.class, FixedDose.PROPERTY_QUANTITY, 0);
		assertEquals(c1.hashCode(), c0.hashCode());
		assertTrue(c0.equals(c1));
		assertTrue(c1.equals(c0));

		DecisionTreeCoordinate c2 = new DosedDrugTreatmentPresentation.DecisionTreeCoordinate(FixedDose.class, null, 0);
		assertEquals(c2.hashCode(), c1.hashCode());
		assertTrue(c2.equals(c1));
		assertTrue(c1.equals(c2));
		
		DecisionTreeCoordinate c3 = new DosedDrugTreatmentPresentation.DecisionTreeCoordinate(FixedDose.class, FixedDose.PROPERTY_QUANTITY, 1);
		DecisionTreeCoordinate c4 = new DosedDrugTreatmentPresentation.DecisionTreeCoordinate(FixedDose.class, FixedDose.PROPERTY_QUANTITY, 2);
		DecisionTreeCoordinate c5 = new DosedDrugTreatmentPresentation.DecisionTreeCoordinate(FixedDose.class, FixedDose.PROPERTY_QUANTITY, null);

		JUnitUtil.assertNotEquals(c3.hashCode(), c4.hashCode());
		assertFalse(c3.equals(c4));
		assertFalse(c4.equals(c3));
		assertTrue(c4.equals(c5));
		assertTrue(c3.equals(c5));

	}
	
	@Test
	public void testChainSetNodes() { 
		CategoryNode catNode1 = new CategoryNode("foo");
		CategoryNode catNode2 = new CategoryNode("bar");
		DosedDrugTreatment bean = new DosedDrugTreatment();
		bean.setRootNode(TypeNode.createDefaultTypeNode());
		DosedDrugTreatmentPresentation dpm = new DosedDrugTreatmentPresentation(bean);
		dpm.setChildNode(FixedDose.class, catNode1);
		dpm.setChildNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, catNode2);
		assertEquals(catNode2, dpm.getSelectedCategory(FixedDose.class, FixedDose.PROPERTY_QUANTITY).getValue());

		CategoryNode catNode3 = new CategoryNode("baz");
		CategoryNode catNode4 = new CategoryNode("fus");

		RangeNode range = new DoseRangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, ExampleData.MILLIGRAMS_A_DAY);
		int right1 = range.addCutOff(12, false);
		int right2 = range.addCutOff(60, false);

		System.out.println("Setting range node");
		dpm.setChildNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, range);
		dpm.setChildNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, right1, catNode3);
		dpm.setChildNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, right2, catNode4);
		
		assertEquals(catNode3, dpm.getSelectedCategory(FixedDose.class, FixedDose.PROPERTY_QUANTITY, right1).getValue());
		assertEquals(catNode4, dpm.getSelectedCategory(FixedDose.class, FixedDose.PROPERTY_QUANTITY, right2).getValue());
		
		FixedDose low = new FixedDose(6, ExampleData.MILLIGRAMS_A_DAY);
		FixedDose high = new FixedDose(15, ExampleData.MILLIGRAMS_A_DAY);

		assertEquals(catNode3, dpm.getBean().getCategoryNode(low));
		assertEquals(catNode4, dpm.getBean().getCategoryNode(high));

	}
}
