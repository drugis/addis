package org.drugis.addis.imports;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;



public class ClinicaltrialsImporter {
	
	public static void getClinicaltrialsData(Study study, String url) throws IOException, MalformedURLException{
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
			JAXBContext jc = JAXBContext.newInstance("org.drugis.addis.imports"); //
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			ClinicalStudy studyImport = (ClinicalStudy) unmarshaller.unmarshal(file);// the .xml file to be read
			getClinicalTrialsData(study,studyImport);
		} 
		catch (JAXBException e){
			System.out.println("Error in parsing xml file (ClinicaltrialsImporter.java))");
			e.printStackTrace();
		} 
	}
	
	private static void getClinicalTrialsData(Study study, ClinicalStudy studyImport) {
		// ID
		study.setId(studyImport.getIdInfo().getNctId());
		
		// Randomization
		if (designContains(studyImport, "non-randomized")) 
			study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.NONRANDOMIZED);
		else if (designContains(studyImport, "randomized"))
			study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.RANDOMIZED);
		
		
		// Blinding
		if (designContains(studyImport, "open label"))
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.OPEN);
		else if (designContains(studyImport, "single blind"))    
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.SINGLE_BLIND);
		else if (designContains(studyImport, "double blind"))
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.DOUBLE_BLIND);
		else if (designContains(studyImport, "triple blind"))
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.TRIPLE_BLIND);
		
		
		// Objective
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, studyImport.getBriefSummary().getTextblock());
		
		// Indication // FIXME: Print label instead of setting it.
		study.setIndication(new Indication(0l, studyImport.getCondition().get(0)) ) ;

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

		// Status
		if (studyImport.getOverallStatus().toLowerCase().contains("recruiting"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.RECRUITING);
		else if (studyImport.getOverallStatus().contains("Enrolling"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.RECRUITING);
		else if (studyImport.getOverallStatus().contains("Active"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.ONGOING);
		else if (studyImport.getOverallStatus().contains("Completed"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.FINISHED);
		else if (studyImport.getOverallStatus().contains("Available"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.FINISHED);
		
		
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
			study.setCharacteristic(BasicStudyCharacteristic.INCLUSION,criteria.substring(inclusionStart, inclusionEnd));

		if(criteria.toLowerCase().indexOf(EXCLUSION_CRITERIA) != -1)
			study.setCharacteristic(BasicStudyCharacteristic.EXCLUSION,criteria.substring(exclusionStart));

	}

	private static boolean designContains(ClinicalStudy studyImport, String contains) {
		return studyImport.getStudyDesign().toLowerCase().contains(contains) || studyImport.getStudyDesign().toLowerCase().contains(contains.replace(' ', '-'));
	}
}
