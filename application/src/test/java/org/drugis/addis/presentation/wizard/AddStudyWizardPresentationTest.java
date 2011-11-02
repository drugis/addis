/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen,
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi,
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal,
 * Daniel Reid, Florin Schimbinschi.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.presentation.wizard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.MeasurementKey;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.drugis.addis.entities.WhenTaken;
import org.drugis.addis.entities.StudyActivity.UsedBy;
import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.util.EntityUtil;
import org.junit.Before;
import org.junit.Test;
import org.pietschy.wizard.InvalidStateException;

import com.jgoodies.binding.list.ObservableList;

public class AddStudyWizardPresentationTest {

	private Domain d_domain;
	private AddStudyWizardPresentation d_wizardImported;
	private AddStudyWizardPresentation d_wizard;

	@Before
	public void setUp(){
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_wizard = new AddStudyWizardPresentation(d_domain, new PresentationModelFactory(d_domain), null);
	}

	private void importStudy() throws MalformedURLException, IOException {
		d_wizardImported = new AddStudyWizardPresentation(d_domain, new PresentationModelFactory(d_domain), null);
		d_wizardImported.getIdModel().setValue("NCT00644527");
		d_wizardImported.importCT();
	}

	@Test
	public void testSetIdGetSourceModel() throws MalformedURLException, IOException{
		importStudy();
		assertEquals(Source.CLINICALTRIALS, d_wizardImported.getSourceModel().getValue());
	}

	@Test
	public void testGetTitleModel() throws MalformedURLException, IOException {
		importStudy();
		assertEquals("Receptive Music Therapy for the Treatment of Depression", d_wizardImported.getTitleModel().getValue());
	}

	@Test
	public void testGetTitleNoteModel() throws MalformedURLException, IOException {
		importStudy();
		String titleNote = (String) d_wizardImported.getCharacteristicNoteModel(BasicStudyCharacteristic.TITLE).getValue();
		assertTrue(titleNote.contains("Rezeptive Musiktherapie"));
	}

	@Test
	public void testGetIndicationListModel() {
		assertTrue(d_wizard.getIndicationsModel().containsAll(d_domain.getIndications()));
	}

	@Test
	public void testGetIndicationNoteModel() throws MalformedURLException, IOException {
		importStudy();
		assertEquals("Depression",d_wizardImported.getIndicationNoteModel().getValue());
	}

	@Test
	public void testClearStudies() throws MalformedURLException, IOException {
		importStudy();
		d_wizardImported.resetStudy();
		assertEquals(Source.MANUAL, d_wizardImported.getSourceModel().getValue());
		assertEquals("", d_wizardImported.getTitleModel().getValue());
		assertEquals(null, d_wizardImported.getIndicationNoteModel().getValue());
		assertEquals(1,d_wizardImported.getEndpointSelectModel().getSlots().size());
		assertEquals(2,d_wizardImported.getArms().size());
	}

	@Test
	public void testGetCharacteristicNoteModel() throws MalformedURLException, IOException {
		importStudy();
		assertEquals("March 2008",d_wizardImported.getCharacteristicNoteModel(BasicStudyCharacteristic.STUDY_START).getValue());
	}

	@Test
	public void testgetNumberEndpoints() {
		int numEndpoints = d_wizard.getEndpointSelectModel().getSlots().size();
		d_wizard.getEndpointSelectModel().addSlot();
		d_wizard.getEndpointSelectModel().addSlot();
		assertEquals(numEndpoints + 2, d_wizard.getEndpointSelectModel().getSlots().size());
	}

	@Test
	public void testCheckID() {
		d_wizard.getNewStudyPM().getBean().setName("This is not in the domain");
		assertEquals(true, d_wizard.isIdAvailable());
		d_wizard.getIdModel().setValue(d_domain.getStudies().get(0).getName());
		assertEquals(false,d_wizard.isIdAvailable());
	}

	@Test
	public void testSaveStudy() {
		d_wizard.getNewStudyPM().getBean().setName("This is not in the domain");
		d_wizard.getEndpointSelectModel().getSlot(0).setValue(d_domain.getEndpoints().get(0));
		d_wizard.getIndicationModel().setValue(d_domain.getIndications().get(0));
		d_wizard.saveStudy();
		assertTrue(d_domain.getStudies().contains(d_wizard.getStudy()));
	}

	@Test(expected=IllegalStateException.class)
	public void testSaveNoEndpoint() {
		d_wizard.getIndicationModel().setValue(d_domain.getIndications().get(0));
		assertEquals(1, d_wizard.getEndpointSelectModel().getSlots().size());
		//d_wizard.removeEndpoint(0);
		d_wizard.getEndpointSelectModel().removeSlot(0);
		d_wizard.saveStudy();
	}

	@Test(expected=IllegalStateException.class)
	public void testSaveIllegalID() throws InvalidStateException {
		d_wizard.getIdModel().setValue(d_domain.getStudies().get(0).getName());
		d_wizard.getEndpointSelectModel().getSlot(0).setValue(d_domain.getEndpoints().get(0));
		d_wizard.getIndicationModel().setValue(d_domain.getIndications().get(0));
		d_wizard.saveStudy();
	}

	@Test
	public void testDeleteOrphanUsedBys() throws MalformedURLException, IOException {
		importStudy();
		Study study = d_wizard.getStudy();
		ObservableList<Arm> arms = study.getArms();
		ObservableList<Epoch> epochs = study.getEpochs();
		ObservableList<StudyActivity> studyActivities = study.getStudyActivities();
		studyActivities.add(new StudyActivity("test", PredefinedActivity.RANDOMIZATION));
		study.setStudyActivityAt(arms.get(0), epochs.get(0), studyActivities.get(0));
		study.setStudyActivityAt(arms.get(1), epochs.get(0), studyActivities.get(0));
		Set<UsedBy> usedBy = study.getStudyActivities().get(0).getUsedBy();
		d_wizard.deleteOrphanUsedBys();
		assertEquals(usedBy, study.getStudyActivities().get(0).getUsedBy());
		arms.remove(1);
		assertFalse(usedBy.equals(study.getStudyActivities().get(0).getUsedBy()));
	}

	@Test
	public void testRenameArmsShouldRebuildMeasurementMap() {
		AddStudyWizardPresentation aswp =
			new AddStudyWizardPresentation(d_domain, new PresentationModelFactory(d_domain), null, ExampleData.buildStudyChouinard());
		List<BasicMeasurement> expected = new ArrayList<BasicMeasurement>();
		List<BasicMeasurement> actual = new ArrayList<BasicMeasurement>();
		Arm arm0 = aswp.getAddArmsModel().getList().get(0);
		Arm arm1 = aswp.getAddArmsModel().getList().get(1);
		for (OutcomeMeasure om : aswp.getOldStudy().getOutcomeMeasures()) {
			expected.add(aswp.getNewStudyPM().getBean().getMeasurement(om, arm0));
			expected.add(aswp.getNewStudyPM().getBean().getMeasurement(om, arm1));
		}
		arm0.setName("newname0");
		arm1.setName("newname1");
		for (OutcomeMeasure om : aswp.getOldStudy().getOutcomeMeasures()) {
			actual.add(aswp.getNewStudyPM().getBean().getMeasurement(om, arm0));
			actual.add(aswp.getNewStudyPM().getBean().getMeasurement(om, arm1));
		}
		assertEquals(expected, actual);
	}

	@Test
	public void testRenameEpochsShouldRebuildMeasurementMap() {
		Study study = ExampleData.buildStudyChouinard().clone();
		// Add second measurement moment to each SOM
		Random rnd = new Random();
		for (OutcomeMeasure om : study.getOutcomeMeasures()) {
			Epoch epoch = study.getEpochs().get(0);
			WhenTaken wt = new WhenTaken(EntityUtil.createDuration("P0D"), RelativeTo.BEFORE_EPOCH_END, epoch);
			study.setMeasurement(new MeasurementKey(om, study.getArms().get(0), wt), om.buildMeasurement(rnd.nextInt()));
			study.setMeasurement(new MeasurementKey(om, study.getArms().get(1), wt), om.buildMeasurement(rnd.nextInt()));
			for(StudyOutcomeMeasure<? extends OutcomeMeasure> som: study.getStudyOutcomeMeasures(om.getClass())) {
				if (som.getValue().equals(om)) {
					som.getWhenTaken().add(wt);
				}
			}
		}
		AddStudyWizardPresentation aswp =
			new AddStudyWizardPresentation(d_domain, new PresentationModelFactory(d_domain), null, study);
		List<BasicMeasurement> expected = new ArrayList<BasicMeasurement>();
		List<BasicMeasurement> actual = new ArrayList<BasicMeasurement>();
		Arm arm0 = aswp.getAddArmsModel().getList().get(0);
		Arm arm1 = aswp.getAddArmsModel().getList().get(1);
		Epoch ep0 = aswp.getAddEpochsModel().getList().get(0);
		Epoch ep1 = aswp.getAddEpochsModel().getList().get(1);
		WhenTaken wt0 = new WhenTaken(EntityUtil.createDuration("P0D"), RelativeTo.BEFORE_EPOCH_END, aswp.getAddEpochsModel().getList().get(0));
		WhenTaken wt1 = new WhenTaken(EntityUtil.createDuration("P0D"), RelativeTo.BEFORE_EPOCH_END, aswp.getAddEpochsModel().getList().get(1));
		for (OutcomeMeasure om : aswp.getOldStudy().getOutcomeMeasures()) {
			expected.add(aswp.getNewStudyPM().getBean().getMeasurement(om, arm0, wt0));
			expected.add(aswp.getNewStudyPM().getBean().getMeasurement(om, arm0, wt1));
			expected.add(aswp.getNewStudyPM().getBean().getMeasurement(om, arm1, wt0));
			expected.add(aswp.getNewStudyPM().getBean().getMeasurement(om, arm1, wt1));
		}
		ep1.setName("NEWNAME2");
		ep0.setName("NEWNAME1");
		for (OutcomeMeasure om : aswp.getOldStudy().getOutcomeMeasures()) {
			actual.add(aswp.getNewStudyPM().getBean().getMeasurement(om, arm0, wt0));
			actual.add(aswp.getNewStudyPM().getBean().getMeasurement(om, arm0, wt1));
			actual.add(aswp.getNewStudyPM().getBean().getMeasurement(om, arm1, wt0));
			actual.add(aswp.getNewStudyPM().getBean().getMeasurement(om, arm1, wt1));
		}
		assertEquals(expected, actual);
	}

	/**
	 * Test that the measurement keys get re-indexed when the WhenTakens change, so that they swap in the natural ordering.
	 * If re-indexing doesn't happen, the keys may become lost in the map, making their values inaccessible (though still present).
	 * The Measurement map is a TreeMap, so keys get lost only if the WhenTakens change their relative position.
	 */
	@Test
	public void testEditWhenTakensShouldRebuildMeasurementMap() {
		Study study = ExampleData.buildStudyChouinard().clone();
		// Add second measurement moment to each SOM
		Random rnd = new Random();
		for (OutcomeMeasure om : study.getOutcomeMeasures()) {
			Epoch epoch = study.getEpochs().get(0);
			WhenTaken wt = new WhenTaken(EntityUtil.createDuration("P0D"), RelativeTo.BEFORE_EPOCH_END, epoch);
			study.setMeasurement(new MeasurementKey(om, study.getArms().get(0), wt), om.buildMeasurement(rnd.nextInt()));
			study.setMeasurement(new MeasurementKey(om, study.getArms().get(1), wt), om.buildMeasurement(rnd.nextInt()));
			study.findStudyOutcomeMeasure(om).getWhenTaken().add(wt);
		}
		AddStudyWizardPresentation aswp = new AddStudyWizardPresentation(d_domain, new PresentationModelFactory(d_domain), null, study);

		Arm arm0 = aswp.getAddArmsModel().getList().get(0);
		Arm arm1 = aswp.getAddArmsModel().getList().get(1);
		WhenTaken wt0 = new WhenTaken(EntityUtil.createDuration("P0D"), RelativeTo.BEFORE_EPOCH_END, aswp.getAddEpochsModel().getList().get(0));
		WhenTaken wt1 = new WhenTaken(EntityUtil.createDuration("P0D"), RelativeTo.BEFORE_EPOCH_END, aswp.getAddEpochsModel().getList().get(1));

		List<BasicMeasurement> expected = new ArrayList<BasicMeasurement>();
		for (OutcomeMeasure om : aswp.getOldStudy().getOutcomeMeasures()) {
			expected.add(aswp.getOldStudy().getMeasurement(om, arm0, wt0));
			expected.add(aswp.getOldStudy().getMeasurement(om, arm0, wt1));
			expected.add(aswp.getOldStudy().getMeasurement(om, arm1, wt0));
			expected.add(aswp.getOldStudy().getMeasurement(om, arm1, wt1));
		}

		Study newStudy = aswp.getNewStudyPM().getBean();

		// change whentakens in study
		for (OutcomeMeasure om : aswp.getOldStudy().getOutcomeMeasures()) {
			WhenTaken whenTaken0 = newStudy.findStudyOutcomeMeasure(om).getWhenTaken().get(0);
			WhenTaken whenTaken1 = newStudy.findStudyOutcomeMeasure(om).getWhenTaken().get(1);
			int before = whenTaken0.compareTo(whenTaken1);
			whenTaken1.setRelativeTo(RelativeTo.FROM_EPOCH_START);
			whenTaken1.setEpoch(newStudy.getEpochs().get(1));
			int after = whenTaken0.compareTo(whenTaken1);

			// The WhenTakens have to swap in their natural ordering for the test to work (Study uses a TreeMap).
			assertFalse(Math.signum(before) == Math.signum(after));
		}
		wt0.setEpoch(newStudy.getEpochs().get(1));
		wt0.setRelativeTo(RelativeTo.FROM_EPOCH_START);

		List<BasicMeasurement> actual = new ArrayList<BasicMeasurement>();
		for (OutcomeMeasure om : aswp.getOldStudy().getOutcomeMeasures()) {
			actual.add(newStudy.getMeasurement(om, arm0, wt0));
			actual.add(newStudy.getMeasurement(om, arm0, wt1));
			actual.add(newStudy.getMeasurement(om, arm1, wt0));
			actual.add(newStudy.getMeasurement(om, arm1, wt1));
		}
		assertEquals(expected, actual);
	}
}
