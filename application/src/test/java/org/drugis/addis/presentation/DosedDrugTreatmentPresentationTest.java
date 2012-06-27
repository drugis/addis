package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.entities.treatment.CategoryNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.entities.treatment.TypeNode;
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
	
	@Test(expected=IllegalArgumentException.class)
	public void testCannotSetChildOfExcludeNode() {
		CategoryNode catNode1 = new CategoryNode("foo");
		d_pm.setChildNode(d_pm.getBean().getRootNode(), FlexibleDose.class, catNode1);
	}

	@Test
	public void testSetCategoryTypeNode() {
		TypeNode rootNode = TypeNode.createDefaultTypeNode();
		d_pm.getBean().setRootNode(rootNode);
		CategoryNode catNode1 = new CategoryNode("foo");
		CategoryNode catNode2 = new CategoryNode("bar");
		d_pm.setChildNode(d_pm.getBean().getRootNode(), FixedDose.class, catNode1);
		d_pm.setChildNode(d_pm.getBean().getRootNode(), UnknownDose.class, catNode2);
		assertEquals(catNode1, d_pm.getSelectedCategory(FixedDose.class).getValue());
		assertEquals(catNode2, d_pm.getSelectedCategory(UnknownDose.class).getValue());
	}
	
	@Test
	public void testSetCategoryRangeNode() {
		RangeNode rootNode = new RangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		d_pm.getBean().setRootNode(rootNode);
		CategoryNode catNode1 = new CategoryNode("foo");
		d_pm.setChildNode(d_pm.getBean().getRootNode(), FixedDose.class, FixedDose.PROPERTY_QUANTITY, catNode1);
		assertEquals(catNode1, d_pm.getSelectedCategory(FixedDose.class, FixedDose.PROPERTY_QUANTITY).getValue());
	}
	
	@Test
	public void testSetCategoryOnRangeNodeWithIndex() { 
		RangeNode rootNode = new RangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		d_pm.getBean().setRootNode(rootNode);
		CategoryNode catNode1 = new CategoryNode("foo");
		CategoryNode catNode2 = new CategoryNode("bar");
		int node2 = rootNode.addCutOff(50, false);
		d_pm.setChildNode(d_pm.getBean().getRootNode(), FixedDose.class, FixedDose.PROPERTY_QUANTITY, 0, catNode1);
		d_pm.setChildNode(d_pm.getBean().getRootNode(), FixedDose.class, FixedDose.PROPERTY_QUANTITY, node2, catNode2);
		assertEquals(catNode1, d_pm.getSelectedCategory(FixedDose.class, FixedDose.PROPERTY_QUANTITY, 0).getValue());
		assertEquals(catNode2, d_pm.getSelectedCategory(FixedDose.class, FixedDose.PROPERTY_QUANTITY, node2).getValue());

	}
}
