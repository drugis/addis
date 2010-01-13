package org.drugis.addis.imports;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;



public class ClinicaltrialsImporter {

	public static void getClinicaltrialsData(Study study, File file){
		try {
			JAXBContext jc = JAXBContext.newInstance("org.drugis.addis.imports"); //
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			//unmarshaller.setValidating(false);
			ClinicalStudy studyImport = (ClinicalStudy) unmarshaller.unmarshal(file);// the .xml file to be read
			
			getClinicalTrialsData(study,studyImport);
			
			//System.out.println(studyImport.detailedDescription.getTextblock());
		} 
		catch (JAXBException e){
			System.out.println("Error in parsing xml file (ClinicaltrialsImporter.java))");
			e.printStackTrace();
		} 
	}
	
	private static void getClinicalTrialsData(Study study, ClinicalStudy studyImport) {
		// ID
		study.setId(studyImport.getIdInfo().getOrgStudyId());
		
		// Randomization
		if (designContains(studyImport, "non-randomized")) 
			study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.NONRANDOMIZED);
		else
			study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.RANDOMIZED);
		
		
		// Blinding
		if (designContains(studyImport, "single blind"))    
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.SINGLE_BLIND);
		else if (designContains(studyImport, "open label"))
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.OPEN);
		else if (designContains(studyImport, "triple blind"))
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.TRIPLE_BLIND);
		else 
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.DOUBLE_BLIND);
		
		// Objective
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, studyImport.getBriefSummary().getTextblock());
		
		// Indication // FIXME: Print label instead of setting it.
		study.setCharacteristic(BasicStudyCharacteristic.INDICATION, new Indication(0l, studyImport.getCondition().get(0)) ) ;

		// Dates
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
		try {
			study.setCharacteristic(BasicStudyCharacteristic.STUDY_START, sdf.parse(studyImport.startDate));
			study.setCharacteristic(BasicStudyCharacteristic.STUDY_END, sdf.parse(studyImport.endDate));
		} catch (ParseException e) {
			System.out.println("exception thrown at date in ClinicaltrialsImporter.java");
			e.printStackTrace();
		}

		// Status
		if (studyImport.getOverallStatus().toLowerCase().contains("Recruiting"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.RECRUITING);
		else if (studyImport.getOverallStatus().contains("Enrolling"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.RECRUITING);
		else if (studyImport.getOverallStatus().contains("Active"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.ONGOING);
		else
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.FINISHED);
		
		
		// Inclusion + Exclusion criteria
		final String EXCLUSION_CRITERIA = "exclusion criteria:";
		final String INCLUSION_CRITERIA = "inclusion criteria:";
		String criteria = studyImport.getEligibility().getCriteria().getTextblock();
		int inclusionStart 	= criteria.toLowerCase().indexOf(INCLUSION_CRITERIA) + INCLUSION_CRITERIA.length();
		int inclusionEnd 	= criteria.toLowerCase().indexOf(EXCLUSION_CRITERIA);
		int exclusionStart 	= criteria.toLowerCase().indexOf(EXCLUSION_CRITERIA) + EXCLUSION_CRITERIA.length();;
		
		study.setCharacteristic(BasicStudyCharacteristic.INCLUSION,criteria.substring(inclusionStart, inclusionEnd));
		study.setCharacteristic(BasicStudyCharacteristic.EXCLUSION,criteria.substring(exclusionStart));
	
	}

	private static boolean designContains(ClinicalStudy studyImport, String contains) {
		return studyImport.getStudyDesign().toLowerCase().contains(contains) || studyImport.getStudyDesign().toLowerCase().contains(contains.replace(' ', '-'));
	}
}
