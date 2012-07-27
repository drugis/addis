package org.drugis.addis.presentation;

import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ObservableList;

public class DosedDrugTreatmentPresentationTest {

	private DosedDrugTreatment d_bean;
	private DosedDrugTreatmentPresentation d_pm;

	@Before
	public void setUp() {
		d_bean = new DosedDrugTreatment("HD/LD", ExampleData.buildDrugCandesartan(), ExampleData.MILLIGRAMS_A_DAY);
		d_pm = new DosedDrugTreatmentPresentation(d_bean, null);
	}

	@Test
	public void testIncludedStudies() {
		final Study studyBennie = ExampleData.buildStudyBennie();		//dose 20
		final Study studyChouinard = ExampleData.buildStudyChouinard();	//dose 27.5
		final Study studyFava2002 = ExampleData.buildStudyFava2002();		//dose 30
		final Category catNodeFoo = new Category("foo");
		final Category catNodeBar = new Category("bar");

		// NOTE: studies Bennie and Chouinard have already been added by default, so just add Fava2002 and its dependencies here
		d_domain.getAdverseEvents().add(ExampleData.buildAdverseEventSexualDysfunction());
		d_domain.getStudies().add(studyFava2002);

		final RangeNode prototype = new RangeNode(AbstractDose.class, AbstractDose.PROPERTY_DOSE_TYPE);
		d_pm.setKnownDoses(prototype);

		List<RangeNode> splits = d_pm.splitKnowDoseRanges(21, false);
		d_pm.setKnownDoses(splits.get(0), catNodeFoo);

		final ObservableList<Study> fooStudies = d_pm.getCategorizedStudyList(catNodeFoo).getIncludedStudies();
		final ObservableList<Study> barStudies = d_pm.getCategorizedStudyList(catNodeBar).getIncludedStudies();

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
}
