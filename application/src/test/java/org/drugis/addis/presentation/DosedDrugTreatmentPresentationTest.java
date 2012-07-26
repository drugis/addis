package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
<<<<<<< HEAD
=======
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
>>>>>>> 42460a7... Built most of the treatment overview panel (individual selected arms in studies are not yet visible). Story #536.

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.drugis.addis.ExampleData;
<<<<<<< HEAD
=======
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
>>>>>>> 42460a7... Built most of the treatment overview panel (individual selected arms in studies are not yet visible). Story #536.
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DosedDrugTreatmentPresentationTest {

	private DosedDrugTreatment d_bean;
	private DosedDrugTreatmentPresentation d_pm;

	@Before
	public void setUp() {
		d_bean = new DosedDrugTreatment("HD/LD", ExampleData.buildDrugCandesartan(), ExampleData.MILLIGRAMS_A_DAY);
		d_pm = new DosedDrugTreatmentPresentation(d_bean);
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
	public void testDoseUnit()  {
		assertEquals(ExampleData.MILLIGRAMS_A_DAY, d_pm.getDoseUnitPresentation().getBean());
		d_pm.setDoseUnit(ExampleData.KILOGRAMS_PER_HOUR);
		assertEquals(ExampleData.KILOGRAMS_PER_HOUR, d_pm.getDoseUnitPresentation().getBean());
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
<<<<<<< HEAD
=======
	
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
		
		DoseDecisionTree tree = d_pm.getBean().getDecisionTree();
		
		Collection<DecisionTreeNode> fixChildren = tree.getChildren(d_pm.getType(FixedDose.class));
		assertEquals(fixChildren.size(), 1);
		DecisionTreeNode fixChild = (DecisionTreeNode) fixChildren.toArray()[0];
		assertTrue(fixChild instanceof DoseRangeNode);
		assertTrue(((RangeNode)fixChild).getPropertyName().equals(FixedDose.PROPERTY_QUANTITY));
		
		Collection<DecisionTreeNode> flexChildren1 = tree.getChildren(d_pm.getType(FlexibleDose.class));
		assertEquals(flexChildren1.size(), 1);
		DecisionTreeNode flexChild1 = (DecisionTreeNode) flexChildren1.toArray()[0];
		assertTrue(flexChild1 instanceof DoseRangeNode);
		assertTrue(((RangeNode)flexChild1).getPropertyName().equals(FlexibleDose.PROPERTY_MIN_DOSE));

		Collection<DecisionTreeNode> flexChildren2 = tree.getChildren(flexChild1);
		assertEquals(flexChildren2.size(), 1);
		DecisionTreeNode flexChild2 = (DecisionTreeNode) flexChildren2.toArray()[0];
		assertTrue(flexChild2 instanceof DoseRangeNode);
		assertTrue(((RangeNode)flexChild2).getPropertyName().equals(FlexibleDose.PROPERTY_MAX_DOSE));
	}
	
	@Test
	public void testSplitKnownDoses() { 
		FixedDose fixedDose1 = new FixedDose(10, ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexibleDose1 = new FlexibleDose(new Interval<Double>(10.0, 20.0), ExampleData.MILLIGRAMS_A_DAY);
		
		FixedDose fixedDose2 = new FixedDose(40, ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexibleDose2 = new FlexibleDose(new Interval<Double>(40.0, 50.0), ExampleData.MILLIGRAMS_A_DAY);
		
		RangeNode prototype = new DoseRangeNode(AbstractDose.class, AbstractDose.PROPERTY_DOSE_TYPE, ExampleData.MILLIGRAMS_A_DAY);
		d_pm.setKnownDoses(prototype);
		
		List<RangeNode> splits = d_pm.splitKnowDoseRanges(30, false);
		
		d_pm.setKnownDoses(splits.get(0), new CategoryNode("foo"));
		d_pm.setKnownDoses(splits.get(1), new CategoryNode("bar"));
		
		// left hand of the split
		assertEquals(d_pm.getCategory(flexibleDose1), "foo");
		assertEquals(d_pm.getCategory(fixedDose1), "foo");

		// right hand of the split
		assertEquals(d_pm.getCategory(flexibleDose2), "bar");
		assertEquals(d_pm.getCategory(fixedDose2), "bar");

	}
	
	@Test
	public void testSetKnownDosesTree() { 
		FixedDose fixedDose1 = new FixedDose(10, ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexibleDose1 = new FlexibleDose(new Interval<Double>(10.0, 20.0), ExampleData.MILLIGRAMS_A_DAY);
		FixedDose fixedDose2 = new FixedDose(40, ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexibleDose2 = new FlexibleDose(new Interval<Double>(40.0, 40.0), ExampleData.MILLIGRAMS_A_DAY);
		
		RangeNode prototype = new RangeNode(AbstractDose.class, AbstractDose.PROPERTY_DOSE_TYPE);
		d_pm.setKnownDoses(prototype);
		List<RangeNode> splits = d_pm.splitKnowDoseRanges(30, true);
		
		CategoryNode cat1 = new CategoryNode("foo");
		CategoryNode cat2 = new CategoryNode("bar");

		d_pm.setKnownDoses(splits.get(0), cat1);
		d_pm.setKnownDoses(splits.get(1), cat2);
		
		assertEquals(cat1.toString(), d_pm.getCategory(fixedDose1));
		assertEquals(cat1.toString(), d_pm.getCategory(flexibleDose1));
		
		assertEquals(cat2.toString(), d_pm.getCategory(fixedDose2));
		assertEquals(cat2.toString(), d_pm.getCategory(flexibleDose2));
		
		ExcludeNode exclude = new ExcludeNode();
		d_pm.setKnownDoses(splits.get(1), exclude);
		assertEquals(exclude.getName(), d_pm.getCategory(fixedDose2));
		assertEquals(exclude.getName(), d_pm.getCategory(flexibleDose2));
	}
	
	@Test 
	public void testMultipleSplits() { 
		FixedDose fixedDose1 = new FixedDose(1.5, ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexibleDose1 = new FlexibleDose(new Interval<Double>(1.0, 1.5), ExampleData.MILLIGRAMS_A_DAY);
		FixedDose fixedDose2 = new FixedDose(3.5, ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexibleDose2 = new FlexibleDose(new Interval<Double>(3.0, 3.5), ExampleData.MILLIGRAMS_A_DAY);
		
		RangeNode prototype = new RangeNode(AbstractDose.class, AbstractDose.PROPERTY_DOSE_TYPE);
		d_pm.setKnownDoses(prototype);
		
		d_pm.splitKnowDoseRanges(50, false);
		d_pm.splitKnowDoseRanges(40, false);
		d_pm.splitKnowDoseRanges(30, false);
		d_pm.splitKnowDoseRanges(20, false);
		d_pm.splitKnowDoseRanges(10, false);
		d_pm.splitKnowDoseRanges(5, false);
		List<RangeNode> splits = d_pm.splitKnowDoseRanges(2.5, false);
		
		d_pm.setKnownDoses(splits.get(0), new CategoryNode("foo"));
		d_pm.setKnownDoses(splits.get(1), new CategoryNode("bar"));
		// left hand of the split
		assertEquals(d_pm.getCategory(flexibleDose1), "foo");
		assertEquals(d_pm.getCategory(fixedDose1), "foo");

		// right hand of the split
		assertEquals(d_pm.getCategory(flexibleDose2), "bar");
		assertEquals(d_pm.getCategory(fixedDose2), "bar");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSplitSameValue() { 
		d_pm.splitKnowDoseRanges(50, false);
		d_pm.splitKnowDoseRanges(50, false);
	}
	
	@Test
	public void testIncludedStudies() {
		Study studyBennie = ExampleData.buildStudyBennie();		//dose 20
		Study studyChouinard = ExampleData.buildStudyChouinard();	//dose 27.5
		Study studyFava2002 = ExampleData.buildStudyFava2002();		//dose 30
		CategoryNode catNodeFoo = new CategoryNode("foo");
		CategoryNode catNodeBar = new CategoryNode("bar");
		
		//NOTE: studies Bennie and Chouinard have already been added by default, so just add Fava2002 and its dependencies here
		d_domain.getAdverseEvents().add(ExampleData.buildAdverseEventSexualDysfunction());
		d_domain.getStudies().add(studyFava2002);

		RangeNode prototype = new RangeNode(AbstractDose.class, AbstractDose.PROPERTY_DOSE_TYPE);
		d_pm.setKnownDoses(prototype);
		
		List<RangeNode> splits = d_pm.splitKnowDoseRanges(21, false);
		d_pm.setKnownDoses(splits.get(0), catNodeFoo);
		
		ObservableList<Study> fooStudies = d_pm.getCategorizedStudyList(catNodeFoo).getIncludedStudies();
		ObservableList<Study> barStudies = d_pm.getCategorizedStudyList(catNodeBar).getIncludedStudies();
		
		assertTrue(fooStudies.contains(studyBennie));
		assertFalse(fooStudies.contains(studyChouinard));
		assertFalse(fooStudies.contains(studyFava2002));
		assertTrue(barStudies.isEmpty());
		
		splits = d_pm.splitKnowDoseRanges(29, false);
		d_pm.setKnownDoses(splits.get(1), catNodeBar);
		
		assertTrue(fooStudies.contains(studyBennie));
		assertFalse(fooStudies.contains(studyChouinard));
		assertFalse(barStudies.contains(studyChouinard));
		assertTrue(barStudies.contains(studyFava2002));
	}
>>>>>>> 42460a7... Built most of the treatment overview panel (individual selected arms in studies are not yet visible). Story #536.
}
