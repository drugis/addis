package org.drugis.addis.presentation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.RangeEdge;
import org.drugis.addis.presentation.wizard.DosedDrugTreatmentWizardPresentation;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ObservableList;

import edu.uci.ics.jung.graph.util.Pair;

public class DosedDrugTreatmentPresentationTest {

	private DosedDrugTreatment d_bean;
	private DosedDrugTreatmentPresentation d_pm;
	private Domain d_domain;
	private DosedDrugTreatmentWizardPresentation d_wpm;

	@Before
	public void setUp() {
		d_bean = new DosedDrugTreatment("HD/LD", ExampleData.buildDrugFluoxetine(), DoseUnit.MILLIGRAMS_A_DAY);
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pm = new DosedDrugTreatmentPresentation(d_bean, d_domain);
		d_wpm = new DosedDrugTreatmentWizardPresentation(d_bean, d_domain);
	}

	@Test
	public void testIncludedStudies() {
		final Study studyBennie = ExampleData.buildStudyBennie();		//dose 20
		final Study studyChouinard = ExampleData.buildStudyChouinard();	//dose 27.5
		final Study studyFava2002 = ExampleData.buildStudyFava2002();	//dose 30
		final Category foo = new Category("foo");
		final Category bar = new Category("bar");
		final Category baz = new Category("baz");

		// NOTE: studies Bennie and Chouinard have already been added by default, so just add Fava2002 and its dependencies here
		d_domain.getAdverseEvents().add(ExampleData.buildAdverseEventSexualDysfunction());
		d_domain.getStudies().add(studyFava2002);

		d_wpm.getModelForFixedDose().setValue(d_wpm.getFixedRangeNode());
		d_wpm.addDefaultRangeEdge(d_wpm.getFixedRangeNode());
		Pair<RangeEdge> splits = d_wpm.splitRange((RangeEdge) d_wpm.getOutEdges(d_wpm.getFixedRangeNode()).get(0), 21.0, false);
		d_wpm.getModelForEdge(splits.getFirst()).setValue(new LeafNode(foo));
		d_wpm.getModelForEdge(splits.getSecond()).setValue(new LeafNode(bar));

		final ObservableList<Study> fooStudies = d_pm.getCategorizedStudyList(foo).getIncludedStudies();
		final ObservableList<Study> barStudies = d_pm.getCategorizedStudyList(bar).getIncludedStudies();
		final ObservableList<Study> bazStudies = d_pm.getCategorizedStudyList(baz).getIncludedStudies();

		assertTrue(fooStudies.contains(studyBennie));
		assertFalse(fooStudies.contains(studyChouinard));
		assertFalse(fooStudies.contains(studyFava2002));
		
		assertTrue(barStudies.contains(studyChouinard));
		assertTrue(barStudies.contains(studyFava2002));
		assertTrue(bazStudies.isEmpty());
	}
}
