/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

package org.drugis.addis.imports;
import static org.apache.commons.collections15.CollectionUtils.find;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.PredicateUtils;
import org.apache.commons.lang.StringUtils;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.BasicStudyCharacteristic.Allocation;
import org.drugis.addis.entities.BasicStudyCharacteristic.Blinding;
import org.drugis.addis.entities.CategoricalVariableType;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.ObjectWithNotes;
import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.WhenTaken;
import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.util.EntityUtil;

public class ClinicaltrialsImporter {

	private static final String INCLUSION_CRITERIA = "inclusion criteria";
	private static final String EXCLUSION_CRITERIA = "exclusion criteria";

	public static Study getClinicaltrialsData(String url, boolean importResults) throws MalformedURLException, IOException {
		Study study = new Study("", new Indication(0l, ""));
		getClinicaltrialsData(study ,url, importResults);
		return study;
	}

	static void getClinicaltrialsData(Study study, String url, boolean importResults) throws IOException {
		URLConnection conn = new URL(url).openConnection();
		getClinicaltrialsData(study, conn.getInputStream(), importResults);
	}

	static void getClinicaltrialsData(Study study, InputStream is, boolean importResults) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance("org.drugis.addis.imports");
			ClinicalStudy studyImport = (ClinicalStudy) jc.createUnmarshaller().unmarshal(is);
			new ClinicaltrialsImporter(study, studyImport, importResults).importStudy();
		} catch (JAXBException e) {
			System.err.println("Error in parsing xml file (ClinicaltrialsImporter.java))");
			throw new RuntimeException(e);
		}
	}

	private Study d_study;
	private ClinicalStudy d_studyImport;
	private boolean d_importResults;
	private Epoch d_mainEpoch;

	public ClinicaltrialsImporter(Study study, ClinicalStudy studyImport, boolean importResults) {
		d_study = study;
		d_studyImport = studyImport;
		d_importResults = importResults;
	}

	public void importStudy() {
		importStudyCharacteristics();

		importReferences();

		// Add default epochs + the study arms
		d_mainEpoch = new Epoch("Main phase", null);
		d_study.getEpochs().add(d_mainEpoch);
		importArms();
		if (d_study.getCharacteristic(BasicStudyCharacteristic.ALLOCATION).equals(Allocation.RANDOMIZED)) {
			addRandomizationEpochAndActivity();
		}

		importStudyOutcomeMeasures();

		if (shouldImportResults()) {
			importAdverseEvents();
		}

		// Import date & Source.
		d_study.setCharacteristicWithNotes(BasicStudyCharacteristic.CREATION_DATE,
				objectWithNote(new Date(), d_studyImport.getRequiredHeader().getDownloadDate().trim()));
		d_study.setCharacteristicWithNotes(BasicStudyCharacteristic.SOURCE,
				objectWithNote(Source.CLINICALTRIALS, d_studyImport.getRequiredHeader().getUrl().trim()));
	}

	/**
	 * @return true if results import has been requested AND results are available.
	 */
	private boolean shouldImportResults() {
		return d_importResults && d_studyImport.getClinicalResults() != null;
	}

	private void importStudyCharacteristics() {
		// ID  (& ID note =study url)
		d_study.setName(d_studyImport.getIdInfo().getNctId());
		d_study.getNotes().add(new Note(Source.CLINICALTRIALS, d_studyImport.getIdInfo().getNctId()));
	
		// Title
		d_study.setCharacteristicWithNotes(BasicStudyCharacteristic.TITLE,
				objectWithNote(d_studyImport.getBriefTitle().trim(), createTitleNote(d_studyImport)));
	
		// Study Centers
		d_study.setCharacteristicWithNotes(BasicStudyCharacteristic.CENTERS,
				objectWithNote(d_studyImport.getLocation().size(), createCentersNote(d_studyImport)));
	
		d_study.setCharacteristicWithNotes(BasicStudyCharacteristic.ALLOCATION,
				objectWithNote(guessAllocation(d_studyImport), d_studyImport.getStudyDesign().trim()));
	
		d_study.setCharacteristicWithNotes(BasicStudyCharacteristic.BLINDING,
				objectWithNote(guessBlinding(d_studyImport), d_studyImport.getStudyDesign().trim()));
	
		// Objective
		d_study.setCharacteristicWithNotes(BasicStudyCharacteristic.OBJECTIVE,
				objectWithNote(deindent(d_studyImport.getBriefSummary().getTextblock()),
						deindent(d_studyImport.getBriefSummary().getTextblock())));
	
		// Indication note
		d_study.getIndicationWithNotes().getNotes().add(new Note(Source.CLINICALTRIALS, createIndicationNote(d_studyImport)));
	
		// Start and end date
		d_study.setCharacteristicWithNotes(BasicStudyCharacteristic.STUDY_START,
				objectWithNote(guessDate(d_studyImport.getStartDate()), d_studyImport.getStartDate() != null ? d_studyImport.getStartDate().getContent() : ""));
		d_study.setCharacteristicWithNotes(BasicStudyCharacteristic.STUDY_END,
				objectWithNote(guessDate(d_studyImport.getCompletionDate()), d_studyImport.getCompletionDate() != null ? d_studyImport.getCompletionDate().getContent() : ""));
		d_study.setCharacteristicWithNotes(BasicStudyCharacteristic.STATUS,
				objectWithNote(guessStatus(d_studyImport), d_studyImport.getOverallStatus().trim()));
	
		String criteria = deindent(d_studyImport.getEligibility().getCriteria().getTextblock());
		d_study.setCharacteristicWithNotes(BasicStudyCharacteristic.INCLUSION,
				objectWithNote(guessInclusionCriteria(criteria), criteria));
		d_study.setCharacteristicWithNotes(BasicStudyCharacteristic.EXCLUSION,
				objectWithNote(guessExclusion(criteria), criteria));
	}

	private void importReferences() {
		PubMedIdList list = (PubMedIdList) d_study.getCharacteristic(BasicStudyCharacteristic.PUBMED);
	
		for (ReferenceStruct ref : d_studyImport.getReference()) {
			if (ref.getPMID() != null) {
				list.add(new PubMedId(ref.getPMID()));
			}
		}
	
		if (d_importResults && d_studyImport.getResultsReference() != null) {
			for (ReferenceStruct ref : d_studyImport.getResultsReference()) {
				if (ref.getPMID() != null) {
					list.add(new PubMedId(ref.getPMID()));
				}
			}
		}
	}

	private WhenTaken createDefaultWhenTaken(StudyOutcomeMeasure<? extends Variable> som) {
		WhenTaken wt = new WhenTaken(EntityUtil.createDuration("P0D"), RelativeTo.BEFORE_EPOCH_END, d_mainEpoch);
		wt.commit();
		som.getWhenTaken().add(wt);
		return wt;
	}

	private void importArms() {
		// Add note to the study-arms.
		Map<String,Arm> armLabels = new HashMap<String,Arm>();
		for(ArmGroupStruct ag : d_studyImport.getArmGroup()){
			Arm arm = new Arm(ag.getArmGroupLabel(), 0);
			d_study.getArms().add(arm);
			StringBuilder noteBuilder = new StringBuilder();
			noteBuilder.append(formatIfAny("Arm Name", ag.getArmGroupLabel(), ""));
			noteBuilder.append(formatIfAny("Arm Type", ag.getArmGroupType(), "\n"));
			noteBuilder.append(formatIfAny("Arm Description", ag.getDescription(), "\n"));
	
			arm.getNotes().add(new Note(Source.CLINICALTRIALS, noteBuilder.toString()));
			armLabels.put(ag.getArmGroupLabel(), arm);
		}
	
		// Add interventions to the study-arms.
		List<String> interventionNames = new ArrayList<String>();
		for(InterventionStruct i : d_studyImport.getIntervention()) {
			interventionNames.add(i.getInterventionName());
		}
		uniqueify(interventionNames);
		for (int i = 0; i < d_studyImport.getIntervention().size(); i++) {
			InterventionStruct intervention = d_studyImport.getIntervention().get(i);
			StringBuilder noteBuilder = new StringBuilder();
			noteBuilder.append(formatIfAny("Intervention Name", intervention.getInterventionName(), "\n"));
			noteBuilder.append(formatIfAny("Intervention Type", intervention.getInterventionType(), "\n"));
			noteBuilder.append(formatIfAny("Intervention Description", intervention.getDescription(), "\n"));
	
			boolean notAssigned = true;
			for (String label : intervention.getArmGroupLabel()) {
				StudyActivity act = new StudyActivity(interventionNames.get(i), new TreatmentActivity(new DrugTreatment(null, null)));
				d_study.getStudyActivities().add(act);
				act.getNotes().add(new Note(Source.CLINICALTRIALS, intervention.getDescription()));
				Arm arm = armLabels.get(label);
				if (arm != null) {
					notAssigned = false;
					Note note = arm.getNotes().get(0);
					note.setText(note.getText() + "\n" + noteBuilder.toString());
					d_study.setStudyActivityAt(arm, d_mainEpoch, act);
				}
			}
			// Add the intervention note to all arms if it can't be mapped to any single arm
			if (notAssigned) {
				for (Arm arm : d_study.getArms()) {
					Note note = arm.getNotes().get(0);
					note.setText(note.getText() + "\n" + noteBuilder.toString());
				}
			}
		}
	}

	private void addRandomizationEpochAndActivity() {
		Epoch randomizationEpoch = new Epoch("Randomization", null);
		d_study.getEpochs().add(0, randomizationEpoch);
		StudyActivity randomizationActivity = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
		d_study.getStudyActivities().add(randomizationActivity);
		for (Arm a: d_study.getArms()) {
			d_study.setStudyActivityAt(a, randomizationEpoch, randomizationActivity);
		}
	}

	private void importStudyOutcomeMeasures() {
		for (ProtocolOutcomeStruct outcome : d_studyImport.getPrimaryOutcome()) {
			importStudyOutcomeMeasure(outcome, true);
		}
		for (ProtocolOutcomeStruct outcome : d_studyImport.getSecondaryOutcome()) {
			importStudyOutcomeMeasure(outcome, false);
		}
	}

	private void importStudyOutcomeMeasure(ProtocolOutcomeStruct outcome, boolean isPrimary) {
		StudyOutcomeMeasure<Endpoint> som = new StudyOutcomeMeasure<Endpoint>(Endpoint.class);
		som.setIsPrimary(isPrimary);
	
		StringBuilder noteBuilder = new StringBuilder(outcome.getMeasure());
		noteBuilder.append(formatIfAny("Time frame", outcome.getTimeFrame(), "\n"));
		noteBuilder.append(formatIfAny("Safety issue", outcome.getSafetyIssue(), "\n"));
		som.getNotes().add(new Note(Source.CLINICALTRIALS, noteBuilder.toString()));
	
		WhenTaken wt = createDefaultWhenTaken(som);
		d_study.getEndpoints().add(som);
		if (shouldImportResults()) {
			importMeasurements(outcome, som, wt);
		}
	}

	private void importMeasurements(final ProtocolOutcomeStruct outcome, StudyOutcomeMeasure<Endpoint> som, WhenTaken wt) {
		List<ResultsOutcomeStruct> outcomes = d_studyImport.getClinicalResults().getOutcomeList().outcome;
		ResultsOutcomeStruct outcome1 = find(outcomes, new Predicate<ResultsOutcomeStruct>() {
			public boolean evaluate(ResultsOutcomeStruct object) {
				return object.getTitle().equals(outcome.getMeasure());
			}
		});
		for (GroupStruct xmlArm : outcome1.getGroupList().group) {
			// Fails for multiple epochs since we treat them as categorical variables (seems to be the default)
			MeasureStruct total = outcome1.measureList.measure.get(0);
			MeasureStruct second = outcome1.measureList.measure.get(1);
			List<MeasureCategoryStruct> categories = total.getCategoryList().getCategory();
			if (categories.size() == 1) {
				if (total.getParam().equals("Number") && second.getParam().equals("Number")) {
					addBasicRateMeasurement(d_study, som, wt, xmlArm, outcome1);
				}
				if (total.getParam().equals("Number") && !second.getParam().equals("Number")) {
					addContinuousRateMeasurement(d_study, som, wt, xmlArm, outcome1);
				}
			} else if (categories.size() > 1) {	 // Categorical variable
				addFrequencyMeasurement(d_study, som, wt, xmlArm, outcome1, categories);
			}
		}
	}

	private void importAdverseEvents() {
		ReportedEventsStruct reportedEvents = d_studyImport.clinicalResults.reportedEvents;
		for (EventCategoryStruct sae : reportedEvents.seriousEvents.categoryList.category) {
			importEvents(reportedEvents, sae);
		}

		for (EventCategoryStruct ae : reportedEvents.otherEvents.categoryList.category) {
			importEvents(reportedEvents, ae);
		}
	}

	private void importEvents(ReportedEventsStruct reportedEvents, EventCategoryStruct categories) {
		for (EventStruct event : categories.getEventList().event) {
			StudyOutcomeMeasure<AdverseEvent> som = new StudyOutcomeMeasure<AdverseEvent>(AdverseEvent.class);
			String noteStr = event.getSubTitle().value + " (" + categories.title + ")";
			noteStr = addIfAny(noteStr, "Description", event.description);
			noteStr = addIfAny(noteStr, "Assessment", event.assessment);

			WhenTaken wt = createDefaultWhenTaken(som);
			d_study.getAdverseEvents().add(som);

			som.getNotes().add(new Note(Source.CLINICALTRIALS, noteStr));
			for (final EventCountsStruct counts : event.getCounts()) {
				GroupStruct xmlArm = find(reportedEvents.groupList.group, new Predicate<GroupStruct>() {
					public boolean evaluate(GroupStruct object) {
						return object.getGroupId().equalsIgnoreCase(counts.groupId);
				}});
				Arm arm = findArmWithName(d_study, xmlArm.getTitle());
				BasicRateMeasurement m =
						new BasicRateMeasurement(Integer.parseInt(counts.subjectsAffected), Integer.parseInt(counts.subjectsAtRisk));
				som.getValue().setVariableType(new RateVariableType());
				d_study.setMeasurement(som, arm, wt, m);
			}
		}
	}

	private static ObjectWithNotes<Object> objectWithNote(Object val, String note) {
		ObjectWithNotes<Object> obj = new ObjectWithNotes<Object>(val);
		obj.getNotes().add(new Note(Source.CLINICALTRIALS, note != null ? note : "N/A"));
		return obj;
	}

	/**
	 * Remove indentation and text wrapping from a text field.
	 * All leading and trailing whitespace is removed from each line of input.
	 * Single newlines are removed, double newlines are retained.
	 */
	private static String deindent(String textblock) {
		BufferedReader bufferedReader = new BufferedReader(new StringReader(textblock.trim()));
		StringBuilder builder = new StringBuilder();
		try {
			for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
				line = line.trim();
				if (!line.isEmpty()) {
					builder.append(line);
				} else {
					builder.append("\n\n");
				}
			}
			bufferedReader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return builder.toString();
	}

	private static void addFrequencyMeasurement(
			Study study,
			StudyOutcomeMeasure<Endpoint> som,
			WhenTaken wt,
			GroupStruct xmlArm,
			ResultsOutcomeStruct outcome, List<MeasureCategoryStruct> categories) {
		final Arm arm = findArmWithName(study, xmlArm.getTitle());
		List<String> categoryNames = new LinkedList<String>();
		Map<String, Integer> frequencies = new HashMap<String, Integer>();
		for (int i = 0; i < categories.size(); i++) {
			MeasureCategoryStruct category = categories.get(i);
			String name = category.subTitle;
			categoryNames.add(name);
			int frequency = Integer.parseInt(getMeasurementForArm(outcome, xmlArm.groupId, 1, i).getValueAttribute());
			frequencies.put(name, frequency);
		}
		som.getValue().setVariableType(new CategoricalVariableType(categoryNames));
		study.setMeasurement(som, arm, wt, new FrequencyMeasurement(categoryNames, frequencies));
	}

	private static void addBasicRateMeasurement(
			final Study study,
			final StudyOutcomeMeasure<? extends Variable> som,
			final WhenTaken wt,
			GroupStruct xmlArm,
			ResultsOutcomeStruct outcome) {
		final Arm arm = findArmWithName(study, xmlArm.getTitle());
		final String xmlArmId = xmlArm.groupId;
		BasicMeasurement meas = createBasicRateMeasurement(
				getMeasurementForArm(outcome, xmlArmId, 0, 0), // Total number of participants
				getMeasurementForArm(outcome, xmlArmId, 1, 0), // Number of responders
				outcome.measureList.measure.get(1));
		som.getValue().setVariableType(new RateVariableType());
		study.setMeasurement(som, arm, wt, meas);
	}

	private static void addContinuousRateMeasurement(
			final Study study,
			final StudyOutcomeMeasure<? extends Variable> som,
			final WhenTaken wt,
			GroupStruct xmlArm,
			ResultsOutcomeStruct outcome) {
		final Arm arm = findArmWithName(study, xmlArm.getTitle());
		final String xmlArmId = xmlArm.groupId;
		MeasureStruct rateMeasure = outcome.measureList.measure.get(1);

		if(!(rateMeasure.param.equalsIgnoreCase("Mean") || rateMeasure.param.equalsIgnoreCase("Least Squares Mean"))) {
			System.err.println(("Cannot import mean, not of type Mean or Least Squares Mean, but " + rateMeasure.param));
			return;
		}
		BasicMeasurement meas = createBasicContinuousMeasurement(
				getMeasurementForArm(outcome, xmlArmId, 0, 0),
				getMeasurementForArm(outcome, xmlArmId, 1, 0),
				rateMeasure);
		som.getValue().setVariableType(new ContinuousVariableType());
		study.setMeasurement(som, arm, wt, meas);
	}

	private static BasicMeasurement createBasicContinuousMeasurement(
			MeasurementStruct totalStruct,
			MeasurementStruct rateStruct,
			MeasureStruct rateMeasure) {
		int total = Integer.parseInt(totalStruct.valueAttribute);
		double mean = Double.parseDouble(rateStruct.valueAttribute);
		double stdDev = Double.parseDouble(rateStruct.spread);
		if (rateMeasure.dispersion.equals("Standard Error")) {
			stdDev = stdDev * Math.sqrt(total);
		} else if (!rateMeasure.dispersion.equals("Standard Deviation")) {
			System.err.println("Cannot convert dispersion in " + rateMeasure.title + " of type" + rateMeasure.dispersion);
			return null;
		}
		return new BasicContinuousMeasurement(mean, stdDev, total);
	}

	private static BasicMeasurement createBasicRateMeasurement(
			MeasurementStruct totalStruct,
			MeasurementStruct rateStruct,
			MeasureStruct rateMeasure) {
		boolean isPercentage = StringUtils.containsIgnoreCase(rateMeasure.units, "Percentage");
		double total = Double.parseDouble(totalStruct.valueAttribute);
		double rate =  Double.parseDouble(rateStruct.valueAttribute);
		return new BasicRateMeasurement((int)Math.round((isPercentage ? ((rate / 100) * total) : rate)), (int)Math.round(total));
	}

	private static MeasurementStruct getMeasurementForArm(ResultsOutcomeStruct outcome, final String xmlArmId, int index, int categoryIdx) {
		MeasureStruct measure = outcome.measureList.measure.get(index);
		List<MeasurementStruct> measureMeasurements = measure.getCategoryList().getCategory().get(categoryIdx).measurementList.measurement;
		return findMeasurement(xmlArmId, measureMeasurements);
	}

	private static MeasurementStruct findMeasurement(final String xmlArmId, List<MeasurementStruct> totalMeasurements) {
		return find(totalMeasurements, new Predicate<MeasurementStruct>() {
			public boolean evaluate(MeasurementStruct object) {
				return object.getGroupId().equalsIgnoreCase(xmlArmId);
			}
		});
	}

	private static Arm findArmWithName(final Study study, final String armName) {
		return find(study.getArms(), new Predicate<Arm>() {
			public boolean evaluate(Arm object) {
				return object.getName().equalsIgnoreCase(armName);
			}
		});
	}

	private static String formatIfAny(String fieldName, String value, String separator) {
		if (value != null && !value.equals("")) {
			return separator + fieldName + ": " + value;
		}
		return "";
	}

	private static String addIfAny(String noteStr, String fieldName, String value) {
		return noteStr + formatIfAny(fieldName, value, "\n\n");
	}

	private static void uniqueify(List<String> names) {
		for (int i = 0; i < names.size() - 1; ++i) {
			List<String> sublist = names.subList(i + 1, names.size());
			String name = names.get(i);
			if (sublist.contains(name)) {
				names.set(i, name + " " + i);
				for (int idx = sublist.indexOf(name); idx > -1; idx = sublist.indexOf(name)) {
					sublist.set(idx, name + " " + (i + 1 + idx));
				}
			}
		}
	}

	private static String guessExclusion(String criteria) {
		int exclusionStart 	= criteria.toLowerCase().indexOf(EXCLUSION_CRITERIA) + EXCLUSION_CRITERIA.length()+1;
		String exclusion = null;
		if(criteria.toLowerCase().indexOf(EXCLUSION_CRITERIA) != -1)
			exclusion = criteria.substring(exclusionStart).trim();
		return exclusion;
	}

	private static String guessInclusionCriteria(String criteria) {
		int inclusionStart 	= criteria.toLowerCase().indexOf(INCLUSION_CRITERIA) + INCLUSION_CRITERIA.length()+1;
		int inclusionEnd 	= criteria.toLowerCase().indexOf(EXCLUSION_CRITERIA);

		if(inclusionEnd == -1)
			inclusionEnd = criteria.length()-1;

		String inclusion = null;
		if(criteria.toLowerCase().indexOf(INCLUSION_CRITERIA) != -1)
			inclusion = criteria.substring(inclusionStart, inclusionEnd).trim();
		return inclusion;
	}

	private static BasicStudyCharacteristic.Status guessStatus(
			ClinicalStudy studyImport) {
		BasicStudyCharacteristic.Status status = BasicStudyCharacteristic.Status.UNKNOWN;
		if (studyImport.getOverallStatus().toLowerCase().contains("recruiting"))
			status = BasicStudyCharacteristic.Status.RECRUITING;
		else if (studyImport.getOverallStatus().contains("Enrolling"))
			status = BasicStudyCharacteristic.Status.RECRUITING;
		else if (studyImport.getOverallStatus().contains("Active"))
			status = BasicStudyCharacteristic.Status.ACTIVE;
		else if (studyImport.getOverallStatus().contains("Completed"))
			status = BasicStudyCharacteristic.Status.COMPLETED;
		else if (studyImport.getOverallStatus().contains("Available"))
			status = BasicStudyCharacteristic.Status.COMPLETED;
		return status;
	}

	private static Date guessDate(DateStruct startDate2) {
		Date startDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
		try {
			if (startDate2 != null)
					startDate = sdf.parse(startDate2.getContent());
		} catch (ParseException e) {
			System.err.println("ClinicalTrialsImporter:: Couldn't parse date. Left empty.");
		}
		return startDate;
	}

	private static String createIndicationNote(ClinicalStudy studyImport) {
		String out = "";
		for(String s : studyImport.getCondition()){
			out = out+s+"\n";
		}
		out = out.trim();
		return out;
	}

	private static BasicStudyCharacteristic.Blinding guessBlinding(
			ClinicalStudy studyImport) {
		BasicStudyCharacteristic.Blinding blinding = Blinding.UNKNOWN;
		if (designContains(studyImport, "open label"))
			blinding = BasicStudyCharacteristic.Blinding.OPEN;
		else if (designContains(studyImport, "single blind"))
			blinding = BasicStudyCharacteristic.Blinding.SINGLE_BLIND;
		else if (designContains(studyImport, "double blind"))
			blinding = BasicStudyCharacteristic.Blinding.DOUBLE_BLIND;
		else if (designContains(studyImport, "triple blind"))
			blinding = BasicStudyCharacteristic.Blinding.TRIPLE_BLIND;
		return blinding;
	}

	private static BasicStudyCharacteristic.Allocation guessAllocation(
			ClinicalStudy studyImport) {
		Allocation allocation = Allocation.UNKNOWN;
		if (designContains(studyImport, "non-randomized"))
			allocation = Allocation.NONRANDOMIZED;
		else if (designContains(studyImport, "randomized"))
			allocation = Allocation.RANDOMIZED;
		return allocation;
	}

	private static String createCentersNote(ClinicalStudy studyImport) {
		StringBuilder noteBuilder = new StringBuilder();
		for (LocationStruct l : studyImport.getLocation()) {
			FacilityStruct f = l.getFacility();
			AddressStruct a = f.getAddress();
			List<String> fields = new ArrayList<String>(Arrays.asList(new String[] {
					f.getName(), a.getZip(), a.getCity(), a.getState(), a.getCountry()
				}));
			CollectionUtils.filter(fields, PredicateUtils.notNullPredicate());
			noteBuilder.append(StringUtils.join(fields, ", "));
			noteBuilder.append('\n');
		}
		return noteBuilder.toString();
	}

	private static String createTitleNote(ClinicalStudy studyImport) {
		StringBuilder titleNote = new StringBuilder();
		titleNote.append("Brief title: ");
		if (studyImport.getBriefTitle() != null) {
			titleNote.append(studyImport.getBriefTitle().trim());
		} else {
			titleNote.append("N/A");
		}
		titleNote.append("\n\n");
		titleNote.append("Official title: ");
		if (studyImport.getOfficialTitle() != null) {
			titleNote.append(studyImport.getOfficialTitle().trim());
		} else {
			titleNote.append("N/A");
		}
		return titleNote.toString();
	}

	private static boolean designContains(ClinicalStudy studyImport, String contains) {
		return studyImport.getStudyDesign().toLowerCase().contains(contains) || studyImport.getStudyDesign().toLowerCase().contains(contains.replace(' ', '-'));
	}
}
