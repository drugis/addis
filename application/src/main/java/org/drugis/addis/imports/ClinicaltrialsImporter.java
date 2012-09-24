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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import javax.xml.bind.Unmarshaller;

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

	public static Study getClinicaltrialsData(String url) throws MalformedURLException, IOException {
		Study study = new Study("", new Indication(0l, ""));
		getClinicaltrialsData(study ,url);
		return study;
	}

	public static void getClinicaltrialsData(Study study, String url) throws IOException {
		URL updateWebService;

		try {
			updateWebService = new URL(url);
			URLConnection conn = updateWebService.openConnection();
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());

			JAXBContext jc = JAXBContext.newInstance("org.drugis.addis.imports");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			ClinicalStudy studyImport = (ClinicalStudy) unmarshaller.unmarshal(isr);
			getClinicalTrialsData(study, studyImport);
			isr.close();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static void getClinicaltrialsData(Study study, File file) {
		try {
			getClinicaltrialsData(study, new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static void getClinicaltrialsData(Study study, InputStream is) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance("org.drugis.addis.imports");
			ClinicalStudy studyImport = (ClinicalStudy) jc.createUnmarshaller().unmarshal(is);
			getClinicalTrialsData(study, studyImport);
		} catch (JAXBException e) {
			System.err.println("Error in parsing xml file (ClinicaltrialsImporter.java))");
			throw new RuntimeException(e);
		}

	}

	private static ObjectWithNotes<Object> objectWithNote(Object val, String note) {
		ObjectWithNotes<Object> obj = new ObjectWithNotes<Object>(val);
		obj.getNotes().add(new Note(Source.CLINICALTRIALS, note != null ? note : "N/A"));
		return obj;
	}

	private static void getClinicalTrialsData(Study study, ClinicalStudy studyImport) {
		// ID  (& ID note =study url)
		study.setName(studyImport.getIdInfo().getNctId());
		study.getNotes().add(new Note(Source.CLINICALTRIALS, studyImport.getIdInfo().getNctId()));

		// Title
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.TITLE,
				objectWithNote(studyImport.getBriefTitle().trim(), createTitleNote(studyImport)));

		// Study Centers
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.CENTERS,
				objectWithNote(studyImport.getLocation().size(), createCentersNote(studyImport)));

		study.setCharacteristicWithNotes(BasicStudyCharacteristic.ALLOCATION,
				objectWithNote(guessAllocation(studyImport), studyImport.getStudyDesign().trim()));

		study.setCharacteristicWithNotes(BasicStudyCharacteristic.BLINDING,
				objectWithNote(guessBlinding(studyImport), studyImport.getStudyDesign().trim()));

		// Objective
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.OBJECTIVE,
				objectWithNote(deindent(studyImport.getBriefSummary().getTextblock()),
						deindent(studyImport.getBriefSummary().getTextblock())));

		// Indication note
		study.getIndicationWithNotes().getNotes().add(new Note(Source.CLINICALTRIALS, createIndicationNote(studyImport)));

		// Start and end date
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.STUDY_START,
				objectWithNote(guessDate(studyImport.getStartDate()), studyImport.getStartDate() != null ? studyImport.getStartDate().getContent() : ""));
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.STUDY_END,
				objectWithNote(guessDate(studyImport.getCompletionDate()), studyImport.getCompletionDate() != null ? studyImport.getCompletionDate().getContent() : ""));
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.STATUS,
				objectWithNote(guessStatus(studyImport), studyImport.getOverallStatus().trim()));

		String criteria = deindent(studyImport.getEligibility().getCriteria().getTextblock());
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.INCLUSION,
				objectWithNote(guessInclusionCriteria(criteria), criteria));
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.EXCLUSION,
				objectWithNote(guessExclusion(criteria), criteria));

		// References
		for (ReferenceStruct ref : studyImport.getReference()) {
			if (ref.getPMID() != null) {
				((PubMedIdList)study.getCharacteristic(BasicStudyCharacteristic.PUBMED)).add(new PubMedId(ref.getPMID()));
			}
		}

		// Arms and measurements

		Epoch mainphaseEpoch = new Epoch("Main phase", null);
		study.getEpochs().add(mainphaseEpoch);
		addStudyArms(study, studyImport, mainphaseEpoch);

		addStudyOutcomeMeasures(study, studyImport);

		if (study.getCharacteristic(BasicStudyCharacteristic.ALLOCATION).equals(Allocation.RANDOMIZED)) {
			Epoch randomizationEpoch = new Epoch("Randomization", null);
			study.getEpochs().add(0, randomizationEpoch);
			StudyActivity randomizationActivity = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
			study.getStudyActivities().add(randomizationActivity);
			for (Arm a: study.getArms()) {
				study.setStudyActivityAt(a, randomizationEpoch, randomizationActivity);
			}
		}

		// Adverse events
		if (studyImport.getClinicalResults() != null) {
			addAdverseEvents(study, studyImport);
		}

		// Import date & Source.
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.CREATION_DATE,
				objectWithNote(new Date(), studyImport.getRequiredHeader().getDownloadDate().trim()));
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.SOURCE,
				objectWithNote(Source.CLINICALTRIALS, studyImport.getRequiredHeader().getUrl().trim()));
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

	private static void addAdverseEvents(Study study, ClinicalStudy studyImport) {
		ReportedEventsStruct reportedEvents = studyImport.clinicalResults.reportedEvents;
		for (EventCategoryStruct sae : reportedEvents.seriousEvents.categoryList.category) {
			addEvents(study, reportedEvents, sae);
		}

		for (EventCategoryStruct ae : reportedEvents.otherEvents.categoryList.category) {
			addEvents(study, reportedEvents, ae);
		}

	}

	private static void addEvents(Study study, ReportedEventsStruct reportedEvents, EventCategoryStruct events) {
		for (EventStruct event : events.getEventList().event) {
			StudyOutcomeMeasure<AdverseEvent> ae = new StudyOutcomeMeasure<AdverseEvent>(AdverseEvent.class);
			String noteStr = event.getSubTitle().value + " (" + events.title + ")";
			noteStr = addIfAny(noteStr, "Description", event.description);
			noteStr = addIfAny(noteStr, "Assessment", event.assessment);

			WhenTaken wt = setDefaultWhenTaken(study, ae);
			study.getAdverseEvents().add(ae);

			ae.getNotes().add(new Note(Source.CLINICALTRIALS, noteStr));
			for (final EventCountsStruct counts : event.getCounts()) {
				GroupStruct xmlArm = find(reportedEvents.groupList.group, new Predicate<GroupStruct>() {
					public boolean evaluate(GroupStruct object) {
						return object.getGroupId().equalsIgnoreCase(counts.groupId);
				}});
				Arm arm = findArmWithName(study, xmlArm.getTitle());
				BasicRateMeasurement m =
						new BasicRateMeasurement(Integer.parseInt(counts.subjectsAffected), Integer.parseInt(counts.subjectsAtRisk));
				ae.getValue().setVariableType(new RateVariableType());
				study.setMeasurement(ae, arm, wt, m);
			}
		}
	}

	private static WhenTaken setDefaultWhenTaken(Study study, StudyOutcomeMeasure<? extends Variable> ae) {
		WhenTaken wt = new WhenTaken(EntityUtil.createDuration("P0D"), RelativeTo.BEFORE_EPOCH_END, study.getEpochs().get(0));
		wt.commit();
		ae.getWhenTaken().add(wt);
		return wt;
	}

	private static void addStudyOutcomeMeasures(Study study, ClinicalStudy studyImport) {
		for (ProtocolOutcomeStruct outcome : studyImport.getPrimaryOutcome()) {
			addStudyOutcomeMeasure(study, studyImport, outcome, true);
		}
		for (ProtocolOutcomeStruct outcome : studyImport.getSecondaryOutcome()) {
			addStudyOutcomeMeasure(study, studyImport, outcome, false);
		}
	}

	private static void addStudyOutcomeMeasure(Study study, ClinicalStudy studyImport, ProtocolOutcomeStruct outcome, boolean isPrimary) {
		StudyOutcomeMeasure<Endpoint> som = new StudyOutcomeMeasure<Endpoint>(Endpoint.class);
		som.setIsPrimary(isPrimary);

		StringBuilder noteBuilder = new StringBuilder(outcome.getMeasure());
		noteBuilder.append(formatIfAny("Time frame", outcome.getTimeFrame(), "\n"));
		noteBuilder.append(formatIfAny("Safety issue", outcome.getSafetyIssue(), "\n"));
		som.getNotes().add(new Note(Source.CLINICALTRIALS, noteBuilder.toString()));

		WhenTaken wt = setDefaultWhenTaken(study, som);
		study.getEndpoints().add(som);
		if (studyImport.getClinicalResults() != null) {
			addMeasurements(som, outcome, wt, study, studyImport);
		}
	}

	private static void addMeasurements(
			final StudyOutcomeMeasure<Endpoint> om,
			final ProtocolOutcomeStruct outcomeStruct,
			final WhenTaken wt,
			final Study study,
			final ClinicalStudy studyImport) {
		List<ResultsOutcomeStruct> outcomes = studyImport.getClinicalResults().getOutcomeList().outcome;
		ResultsOutcomeStruct outcome = find(outcomes, new Predicate<ResultsOutcomeStruct>() {
			public boolean evaluate(ResultsOutcomeStruct object) {
				return object.getTitle().equals(outcomeStruct.getMeasure());
			}
		});
		for (GroupStruct xmlArm : outcome.getGroupList().group) {
			// Fails for multiple epochs since we treat them as categorical variables (seems to be the default)
			MeasureStruct total = outcome.measureList.measure.get(0);
			MeasureStruct second = outcome.measureList.measure.get(1);
			List<MeasureCategoryStruct> categories = total.getCategoryList().getCategory();
			if (categories.size() == 1) {
				if (total.getParam().equals("Number") && second.getParam().equals("Number")) {
					addBasicRateMeasurement(study, om, wt, xmlArm, outcome);
				}
				if (total.getParam().equals("Number") && !second.getParam().equals("Number")) {
					addContinuousRateMeasurement(study, om, wt, xmlArm, outcome);
				}
			} else if (categories.size() > 1) {	 // Categorical variable
				addFrequencyMeasurement(study, om, wt, xmlArm, outcome, categories);
			}
		}
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

	private static void addStudyArms(Study study, ClinicalStudy studyImport, Epoch mainphaseEpoch) {
		// Add note to the study-arms.
		Map<String,Arm> armLabels = new HashMap<String,Arm>();
		for(ArmGroupStruct ag : studyImport.getArmGroup()){
			Arm arm = new Arm(ag.getArmGroupLabel(), 0);
			study.getArms().add(arm);
			StringBuilder noteBuilder = new StringBuilder();
			noteBuilder.append(formatIfAny("Arm Name", ag.getArmGroupLabel(), ""));
			noteBuilder.append(formatIfAny("Arm Type", ag.getArmGroupType(), "\n"));
			noteBuilder.append(formatIfAny("Arm Description", ag.getDescription(), "\n"));

			arm.getNotes().add(new Note(Source.CLINICALTRIALS, noteBuilder.toString()));
			armLabels.put(ag.getArmGroupLabel(), arm);
		}

		// Add interventions to the study-arms.
		List<String> interventionNames = new ArrayList<String>();
		for(InterventionStruct i : studyImport.getIntervention()) {
			interventionNames.add(i.getInterventionName());
		}
		uniqueify(interventionNames);
		for (int i = 0; i < studyImport.getIntervention().size(); i++) {
			InterventionStruct intervention = studyImport.getIntervention().get(i);
			StringBuilder noteBuilder = new StringBuilder();
			noteBuilder.append(formatIfAny("Intervention Name", intervention.getInterventionName(), "\n"));
			noteBuilder.append(formatIfAny("Intervention Type", intervention.getInterventionType(), "\n"));
			noteBuilder.append(formatIfAny("Intervention Description", intervention.getDescription(), "\n"));

			boolean notAssigned = true;
			for (String label : intervention.getArmGroupLabel()) {
				StudyActivity act = new StudyActivity(interventionNames.get(i), new TreatmentActivity(new DrugTreatment(null, null)));
				study.getStudyActivities().add(act);
				act.getNotes().add(new Note(Source.CLINICALTRIALS, intervention.getDescription()));
				Arm arm = armLabels.get(label);
				if (arm != null) {
					notAssigned = false;
					Note note = arm.getNotes().get(0);
					note.setText(note.getText() + "\n" + noteBuilder.toString());
					study.setStudyActivityAt(arm, mainphaseEpoch, act);
				}
			}
			// Add the intervention note to all arms if it can't be mapped to any single arm
			if (notAssigned) {
				for (Arm arm : study.getArms()) {
					Note note = arm.getNotes().get(0);
					note.setText(note.getText() + "\n" + noteBuilder.toString());
				}
			}
		}
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
