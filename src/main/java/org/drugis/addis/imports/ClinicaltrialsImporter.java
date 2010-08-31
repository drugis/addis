/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;



public class ClinicaltrialsImporter {
	
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
	
	private static void getClinicalTrialsData(Study study, ClinicalStudy studyImport) {
		// ID  (& ID note =study url)
		study.setStudyId(studyImport.getIdInfo().getNctId());
		study.putNote(Study.PROPERTY_ID, new Note(Source.CLINICALTRIALS, studyImport.getIdInfo().getNctId().trim()));
		
		// Title
		study.setCharacteristic(BasicStudyCharacteristic.TITLE, studyImport.getBriefTitle().trim());
		study.putNote(BasicStudyCharacteristic.TITLE, new Note(Source.CLINICALTRIALS, createTitleNote(studyImport)));
		
		// Study Centers
		study.setCharacteristic(BasicStudyCharacteristic.CENTERS, studyImport.getLocation().size());
		String noteStr = "";
		for (Location l : studyImport.getLocation()) {
			noteStr += l.getFacility().getName()+"\n";
		}
		study.putNote(BasicStudyCharacteristic.CENTERS, new Note(Source.CLINICALTRIALS));
		
		// Randomization
		if (designContains(studyImport, "non-randomized")) 
			study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.NONRANDOMIZED);
		else if (designContains(studyImport, "randomized"))
			study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.RANDOMIZED);
		study.putNote(BasicStudyCharacteristic.ALLOCATION, new Note(Source.CLINICALTRIALS, studyImport.getStudyDesign().trim()));
		
		// Blinding
		if (designContains(studyImport, "open label"))
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.OPEN);
		else if (designContains(studyImport, "single blind"))    
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.SINGLE_BLIND);
		else if (designContains(studyImport, "double blind"))
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.DOUBLE_BLIND);
		else if (designContains(studyImport, "triple blind"))
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.TRIPLE_BLIND);
		study.putNote(BasicStudyCharacteristic.BLINDING, new Note(Source.CLINICALTRIALS, studyImport.getStudyDesign().trim()));
		
		// Objective
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, studyImport.getBriefSummary().getTextblock().trim());
		study.putNote(BasicStudyCharacteristic.OBJECTIVE, new Note(Source.CLINICALTRIALS, studyImport.getBriefSummary().getTextblock().trim()));
		
		study.setIndication(new Indication(0l, studyImport.getCondition().get(0)) ) ;
		
		String out = "";
		for(String s : studyImport.getCondition()){
			out = out+s+"\n";
		}
		study.putNote(Study.PROPERTY_INDICATION, new Note(Source.CLINICALTRIALS, out.trim()));

		// Dates
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
		try {
			if (studyImport.startDate != null)
					study.setCharacteristic(BasicStudyCharacteristic.STUDY_START, sdf.parse(studyImport.startDate));
			if (studyImport.endDate != null)
					study.setCharacteristic(BasicStudyCharacteristic.STUDY_END, sdf.parse(studyImport.endDate));
		} catch (ParseException e) {
			System.err.println("ClinicalTrialsImporter:: Couldn't parse date. Left empty.");
		}
		study.putNote((Object)BasicStudyCharacteristic.STUDY_START, new Note(Source.CLINICALTRIALS, studyImport.startDate));
		study.putNote((Object)BasicStudyCharacteristic.STUDY_END,   new Note(Source.CLINICALTRIALS, studyImport.endDate));
		
		// Import date & Source.
		study.setCharacteristic(BasicStudyCharacteristic.CREATION_DATE,new Date());
		study.setCharacteristic(BasicStudyCharacteristic.SOURCE, Source.CLINICALTRIALS);
		study.putNote((Object)BasicStudyCharacteristic.CREATION_DATE, new Note(Source.CLINICALTRIALS, studyImport.getRequiredHeader().getDownloadDate().trim()));
		study.putNote((Object)BasicStudyCharacteristic.SOURCE, new Note(Source.CLINICALTRIALS, studyImport.getRequiredHeader().getUrl().trim()));

		// Status
		if (studyImport.getOverallStatus().toLowerCase().contains("recruiting"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.RECRUITING);
		else if (studyImport.getOverallStatus().contains("Enrolling"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.RECRUITING);
		else if (studyImport.getOverallStatus().contains("Active"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.ACTIVE);
		else if (studyImport.getOverallStatus().contains("Completed"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.COMPLETED);
		else if (studyImport.getOverallStatus().contains("Available"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.COMPLETED);
		study.putNote((Object)BasicStudyCharacteristic.STATUS, new Note(Source.CLINICALTRIALS, studyImport.getOverallStatus().trim()));
		
		
		// Inclusion + Exclusion criteria
		final String EXCLUSION_CRITERIA = "exclusion criteria";
		final String INCLUSION_CRITERIA = "inclusion criteria";
		String criteria = studyImport.getEligibility().getCriteria().getTextblock();
		int inclusionStart 	= criteria.toLowerCase().indexOf(INCLUSION_CRITERIA) + INCLUSION_CRITERIA.length()+1;
		int inclusionEnd 	= criteria.toLowerCase().indexOf(EXCLUSION_CRITERIA);
		int exclusionStart 	= criteria.toLowerCase().indexOf(EXCLUSION_CRITERIA) + EXCLUSION_CRITERIA.length()+1;
		
		if(inclusionEnd == -1)
			inclusionEnd = criteria.length()-1; 
		
		if(criteria.toLowerCase().indexOf(INCLUSION_CRITERIA) != -1)
			study.setCharacteristic(BasicStudyCharacteristic.INCLUSION,criteria.substring(inclusionStart, inclusionEnd).trim());

		if(criteria.toLowerCase().indexOf(EXCLUSION_CRITERIA) != -1)
			study.setCharacteristic(BasicStudyCharacteristic.EXCLUSION,criteria.substring(exclusionStart).trim());
		study.putNote((Object)BasicStudyCharacteristic.INCLUSION, new Note(Source.CLINICALTRIALS, criteria.trim()));
		study.putNote((Object)BasicStudyCharacteristic.EXCLUSION, new Note(Source.CLINICALTRIALS, criteria.trim()));
		
		// Add note to the study-arms.
		Map<String,Arm> armLabels = new HashMap<String,Arm>();
		for(ArmGroup ag : studyImport.getArmGroup()){
			Arm arm = new Arm(new Drug("",""), new FixedDose(0,SIUnit.MILLIGRAMS_A_DAY),0);
			study.addArm(arm);
			noteStr = "Arm Type: " + ag.getArmGroupType()+"\nArm Description: "+ag.getDescription();
			study.putNote(arm, new Note(Source.CLINICALTRIALS, noteStr.trim()));
			armLabels.put(ag.getArmGroupLabel(),arm);
		}
		
		// Add note to the drugs within the study-arm.
		for(Intervention i : studyImport.getIntervention()){
			noteStr = "\n\nIntervention Name: "+i.getInterventionName()+"\nIntervention Type: "+i.getInterventionType()+"\nIntervention Description: "+i.getDescription();
			boolean notAssigned = true;
			for (String label : i.getArmGroupLabel()) {
				Arm arm = armLabels.get(label);
				if (arm != null) {
					notAssigned = false;
					Note note = study.getNote(arm);
					note.setText(note.getText() + noteStr);
				}
			}
			/* Add the intervention note to all arms if it can't be mapped to any single arm */
			if (notAssigned) {
				for (Arm arm : study.getArms()) {
					Note note = study.getNote(arm);
					note.setText(note.getText() + noteStr);
				}
			}
		}

		// Outcome Measures
		for (PrimaryOutcome endp : studyImport.getPrimaryOutcome()) {
			Endpoint e = new Endpoint(endp.getMeasure(), Variable.Type.RATE);
			study.addEndpoint(e);
			study.putNote(e, new Note(Source.CLINICALTRIALS, endp.getMeasure()));
		}
		
		for (SecondaryOutcome endp : studyImport.getSecondaryOutcome()) {
			Endpoint e = new Endpoint(endp.getMeasure(), Variable.Type.RATE);
			study.addEndpoint(e);
			study.putNote(e, new Note(Source.CLINICALTRIALS, endp.getMeasure()));
		}	
	}

	private static String createTitleNote(ClinicalStudy studyImport) {
		return "Brief title: " + studyImport.getBriefTitle().trim() + "\n\nOfficial title: " + studyImport.getOfficialTitle().trim();
	}

	private static boolean designContains(ClinicalStudy studyImport, String contains) {
		return studyImport.getStudyDesign().toLowerCase().contains(contains) || studyImport.getStudyDesign().toLowerCase().contains(contains.replace(' ', '-'));
	}
}
