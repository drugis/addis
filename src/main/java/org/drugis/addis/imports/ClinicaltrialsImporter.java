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

package org.drugis.addis.imports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.ObjectWithNotes;
import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.BasicStudyCharacteristic.Allocation;
import org.drugis.addis.entities.BasicStudyCharacteristic.Blinding;
import org.drugis.addis.entities.Study.StudyOutcomeMeasure;



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
			
			JAXBContext jc = JAXBContext.newInstance("org.drugis.addis.imports"); //
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			ClinicalStudy studyImport = (ClinicalStudy) unmarshaller.unmarshal(isr);
			getClinicalTrialsData(study,studyImport);
			isr.close();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static void getClinicaltrialsData(Study study, File file){
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
			getClinicalTrialsData(study,studyImport);
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
		study.setStudyId(studyImport.getIdInfo().getNctId());
		study.getStudyIdWithNotes().getNotes().add(new Note(Source.CLINICALTRIALS, studyImport.getIdInfo().getNctId()));
		
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
				objectWithNote(studyImport.getBriefSummary().getTextblock().trim(), studyImport.getBriefSummary().getTextblock().trim()));

		// Indication note
		study.getIndicationWithNotes().getNotes().add(new Note(Source.CLINICALTRIALS, createIndicationNote(studyImport)));

		// Start and end date
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.STUDY_START,
				objectWithNote(guessDate(studyImport.startDate), studyImport.startDate));
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.STUDY_END,
				objectWithNote(guessDate(studyImport.endDate), studyImport.endDate));
		
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.STATUS, 
				objectWithNote(guessStatus(studyImport), studyImport.getOverallStatus().trim()));
		
		String criteria = studyImport.getEligibility().getCriteria().getTextblock();
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.INCLUSION,
				objectWithNote(guessInclusionCriteria(criteria), criteria.trim()));		
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.EXCLUSION,
				objectWithNote(guessExclusion(criteria), criteria.trim()));
		
		// References
		for (Reference ref : studyImport.getReference()) {
			if (ref.getPMID() != null) {
				((PubMedIdList)study.getCharacteristic(BasicStudyCharacteristic.PUBMED)).add(new PubMedId(ref.getPMID()));
			}
		}
		
		addStudyArms(study, studyImport);

		addStudyEndpoints(study, studyImport);
		
		// Import date & Source.
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.CREATION_DATE, 
				objectWithNote(new Date(), studyImport.getRequiredHeader().getDownloadDate().trim()));
		study.setCharacteristicWithNotes(BasicStudyCharacteristic.SOURCE, 
				objectWithNote(Source.CLINICALTRIALS, studyImport.getRequiredHeader().getUrl().trim()));
	}

	private static void addStudyEndpoints(Study study, ClinicalStudy studyImport) {
		// Outcome Measures
		for (PrimaryOutcome endp : studyImport.getPrimaryOutcome()) {
			StudyOutcomeMeasure<Endpoint> om = new StudyOutcomeMeasure<Endpoint>(null);
			om.getNotes().add(new Note(Source.CLINICALTRIALS, endp.getMeasure()));
			study.getStudyEndpoints().add(om);
		}
		
		for (SecondaryOutcome endp : studyImport.getSecondaryOutcome()) {
			StudyOutcomeMeasure<Endpoint> om = new StudyOutcomeMeasure<Endpoint>(null);
			om.getNotes().add(new Note(Source.CLINICALTRIALS, endp.getMeasure()));
			study.getStudyEndpoints().add(om);
		}
	}

	private static void addStudyArms(Study study, ClinicalStudy studyImport) {
		// Add note to the study-arms.
		Map<String,Arm> armLabels = new HashMap<String,Arm>();
		for(ArmGroup ag : studyImport.getArmGroup()){
			Arm arm = new Arm(ag.getArmGroupLabel(), 0);
			study.addArm(arm);
			String noteStr = "Arm Type: " + ag.getArmGroupType()+"\nArm Description: "+ag.getDescription();
			arm.getNotes().add(new Note(Source.CLINICALTRIALS, noteStr.trim()));
			armLabels.put(ag.getArmGroupLabel(), arm);
		}
		
		// Add note to the drugs within the study-arm.
		for(Intervention i : studyImport.getIntervention()){
			String noteStr = "\n\nIntervention Name: "+i.getInterventionName()+"\nIntervention Type: "+i.getInterventionType()+"\nIntervention Description: "+i.getDescription();
			boolean notAssigned = true;
			for (String label : i.getArmGroupLabel()) {
				Arm arm = armLabels.get(label);
				if (arm != null) {
					notAssigned = false;
					Note note = arm.getNotes().get(0);
					note.setText(note.getText() + noteStr);
				}
			}
			/* Add the intervention note to all arms if it can't be mapped to any single arm */
			if (notAssigned) {
				for (Arm arm : study.getArms()) {
					Note note = arm.getNotes().get(0);
					note.setText(note.getText() + noteStr);
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

	private static Date guessDate(String startDate2) {
		Date startDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
		try {
			if (startDate2 != null)
					startDate = sdf.parse(startDate2);
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
		String noteStr = "";
		for (Location l : studyImport.getLocation()) {
			noteStr += l.getFacility().getName()+"\n";
		}
		return noteStr;
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
