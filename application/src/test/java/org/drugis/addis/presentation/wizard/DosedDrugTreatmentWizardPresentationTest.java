package org.drugis.addis.presentation.wizard;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DosedDrugTreatmentWizardPresentationTest {

	private DosedDrugTreatment d_bean;
	private DosedDrugTreatmentWizardPresentation d_pm;

	@Before
	public void setUp() {
		d_bean = new DosedDrugTreatment("HD/LD", ExampleData.buildDrugCandesartan(), ExampleData.MILLIGRAMS_A_DAY);
		d_pm = new DosedDrugTreatmentWizardPresentation(d_bean, null);
	}

	@Test
	public void testInitialization() {
		assertEquals(ExampleData.buildDrugCandesartan(), d_pm.getDrug().getValue());
		assertEquals("HD/LD", d_pm.getName().getValue());
		assertEquals(Collections.emptyList(), d_pm.getCategories());
	}

	@Test
	public void testMessWithCategories() {
		final Category catNode1 = new Category("foo");
		final Category catNode2 = new Category("bar");
		d_pm.getCategories().add(catNode1);
		d_pm.getCategories().add(catNode2);
		assertEquals(Arrays.asList(catNode1, catNode2), d_pm.getCategories());
		d_pm.getCategories().remove(catNode1);
		assertEquals(Arrays.asList(catNode2), d_pm.getCategories());
	}

	@Test
	public void testChainSetNodes() {
		final TypeNode fixedDose = new TypeNode(FixedDose.class);
		final RangeNode range = new RangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		final CategoryNode lowDoseNode = new LeafNode(new Category("Low dose"));
		final CategoryNode highDoseNode = new LeafNode(new Category("High dose"));

		d_pm.setSelected(d_pm.getBean().getRootNode(), fixedDose);
		d_pm.setSelected(fixedDose, range);

		final List<RangeNode> ranges = d_pm.splitRange(range, 20, true);
		d_pm.setSelected(ranges.get(0), lowDoseNode);
		d_pm.setSelected(ranges.get(1), highDoseNode);

		final FixedDose lowDose = new FixedDose(10.0, ExampleData.MILLIGRAMS_A_DAY);
		final FixedDose highDose = new FixedDose(30.0, ExampleData.MILLIGRAMS_A_DAY);

		assertEquals(lowDoseNode.getName(), d_pm.getBean().getCategory(lowDose).getName());
		assertEquals(highDoseNode.getName(), d_pm.getBean().getCategory(highDose).getName());

	}

	@Test
	public void testGetType() {
		final TypeNode fixedDose = new TypeNode(FixedDose.class);
		final TypeNode flexibleDose = new TypeNode(FlexibleDose.class);

		final RangeNode range1 = new RangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		final RangeNode range2 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE);

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
		final TypeNode fixedDose = new TypeNode(FixedDose.class);
		final TypeNode flexibleDose = new TypeNode(FlexibleDose.class);

		final RangeNode range1 = new DoseRangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, ExampleData.MILLIGRAMS_A_DAY);
		final RangeNode range2 = new DoseRangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, ExampleData.MILLIGRAMS_A_DAY);

		d_pm.setSelected(d_pm.getBean().getRootNode(), fixedDose);
		d_pm.setSelected(d_pm.getBean().getRootNode(), flexibleDose);
		d_pm.setSelected(fixedDose, range1);
		d_pm.setSelected(flexibleDose, range2);

		final Collection<DecisionTreeNode> sortedSet = new TreeSet<DecisionTreeNode>();
		sortedSet.addAll(Arrays.asList(range1));
		JUnitUtil.assertAllAndOnly(sortedSet, d_pm.getChildNodes(fixedDose));
	}
}
