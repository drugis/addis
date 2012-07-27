package org.drugis.addis.presentation;

import static org.junit.Assert.*;

import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.RangeEdge;
import org.drugis.addis.entities.treatment.TypeEdge;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ObservableList;

import edu.uci.ics.jung.graph.util.Pair;

public class DosedDrugTreatmentPresentationTest {

	private DosedDrugTreatment d_bean;
	private DosedDrugTreatmentPresentation d_pm;
	private Domain d_domain;
	private DecisionTree d_tree;

	@Before
	public void setUp() {
		d_bean = new DosedDrugTreatment("HD/LD", ExampleData.buildDrugCandesartan(), DoseUnit.MILLIGRAMS_A_DAY);
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pm = new DosedDrugTreatmentPresentation(d_bean, d_domain);
		d_tree = d_bean.getDecisionTree();

	}

	@Test
	public void testIncludedStudies() {
		final Study studyBennie = ExampleData.buildStudyBennie();		//dose 20
		final Study studyChouinard = ExampleData.buildStudyChouinard();	//dose 27.5
		final Study studyFava2002 = ExampleData.buildStudyFava2002();	//dose 30
		final Category foo = new Category("foo");
		final Category bar = new Category("bar");

		// NOTE: studies Bennie and Chouinard have already been added by default, so just add Fava2002 and its dependencies here
		d_domain.getAdverseEvents().add(ExampleData.buildAdverseEventSexualDysfunction());
		d_domain.getStudies().add(studyFava2002);
		
		TypeEdge edge = (TypeEdge) d_tree.getOutEdges(d_bean.getRootNode()).toArray()[1];
		d_tree.addChild(edge, d_bean.getRootNode(), new ChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY));

//		final RangeNode prototype = new RangeNode(AbstractDose.class, AbstractDose.PROPERTY_DOSE_TYPE);
//		d_pm.setKnownDoses(prototype);
//
//		List<RangeNode> splits = d_pm.splitKnowDoseRanges(21, false);
//		d_pm.setKnownDoses(splits.get(0), catNodeFoo);
		Pair<RangeEdge> splits = splitRangeOnChild(bar, 21, false, 0);

		final ObservableList<Study> fooStudies = d_pm.getCategorizedStudyList(foo).getIncludedStudies();
		final ObservableList<Study> barStudies = d_pm.getCategorizedStudyList(bar).getIncludedStudies();

		assertTrue(fooStudies.contains(studyBennie));
		assertFalse(fooStudies.contains(studyChouinard));
		assertFalse(fooStudies.contains(studyFava2002));
		assertTrue(barStudies.isEmpty());
	}

	private Pair<RangeEdge> splitRangeOnChild(final Category bar, int value, boolean lowerRangeOpen, int i) {
		ChoiceNode parent = (ChoiceNode) d_tree.getChildren(d_bean.getRootNode()).toArray()[i];
		RangeEdge rangeEdge = RangeEdge.createDefault();
		d_tree.addChild(rangeEdge, parent, new LeafNode(bar));

		return d_bean.splitRange(rangeEdge, value, lowerRangeOpen);
	}
}
