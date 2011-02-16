package org.drugis.addis.util;

import static org.drugis.addis.entities.AssertEntityEquals.assertDomainEquals;
import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.CategoricalPopulationCharacteristic;
import org.drugis.addis.entities.CharacteristicsMap;
import org.drugis.addis.entities.ContinuousPopulationCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.BasicStudyCharacteristic.Allocation;
import org.drugis.addis.entities.BasicStudyCharacteristic.Blinding;
import org.drugis.addis.entities.BasicStudyCharacteristic.Status;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Study.MeasurementKey;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.entities.data.Arms;
import org.drugis.addis.entities.data.CategoricalVariable;
import org.drugis.addis.entities.data.Category;
import org.drugis.addis.entities.data.Characteristics;
import org.drugis.addis.entities.data.ContinuousMeasurement;
import org.drugis.addis.entities.data.ContinuousVariable;
import org.drugis.addis.entities.data.DateWithNotes;
import org.drugis.addis.entities.data.IdReference;
import org.drugis.addis.entities.data.Measurements;
import org.drugis.addis.entities.data.NameReference;
import org.drugis.addis.entities.data.NameReferenceWithNotes;
import org.drugis.addis.entities.data.Notes;
import org.drugis.addis.entities.data.OutcomeMeasure;
import org.drugis.addis.entities.data.RateMeasurement;
import org.drugis.addis.entities.data.RateVariable;
import org.drugis.addis.entities.data.References;
import org.drugis.addis.entities.data.StringIdReference;
import org.drugis.addis.entities.data.StringWithNotes;
import org.drugis.addis.entities.data.StudyOutcomeMeasure;
import org.drugis.addis.entities.data.StudyOutcomeMeasures;
import org.drugis.addis.util.JAXBConvertor.ConversionException;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class JAXBConvertorTest {

	private JAXBContext d_jaxb;
	//private Marshaller d_marshaller;
	private Unmarshaller d_unmarshaller;
	
	@Before
	public void setup() throws JAXBException{
		d_jaxb = JAXBContext.newInstance("org.drugis.addis.entities.data" );
		d_unmarshaller = d_jaxb.createUnmarshaller();
//		d_marshaller = d_jaxb.createMarshaller();
//		d_unmarshaller.setEventHandler(new AddisDataValidationEventHandler());
	}
	
	@Test
	public void testConvertContinuousEndpoint() throws ConversionException {
		String name = "Onset of erection";
		String desc = "Time to onset of erection of >= 60 % rigidity";
		String unit = "Minutes";
		Direction dir = Direction.LOWER_IS_BETTER;
		
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		ContinuousVariable value = new ContinuousVariable();
		value.setDirection(dir);
		value.setUnitOfMeasurement(unit);
		m.setContinuous(value);
		
		Endpoint e = new Endpoint(name, Type.CONTINUOUS, dir);
		e.setDescription(desc);
		e.setUnitOfMeasurement(unit);
		
		assertEntityEquals(e, JAXBConvertor.convertEndpoint(m));
	}
	
	@Test
	public void testConvertRateEndpoint() throws ConversionException {
		String name = "Efficacy";
		String desc = "Erection of >= 60% rigidity within 1 hr of medication";
		Direction dir = Direction.HIGHER_IS_BETTER;
		
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		RateVariable value = new RateVariable();
		value.setDirection(dir);
		m.setRate(value);
		
		Endpoint e = new Endpoint(name, Type.RATE, dir);
		e.setDescription(desc);
		
		assertEntityEquals(e, JAXBConvertor.convertEndpoint(m));
		
		value.setDirection(Direction.LOWER_IS_BETTER);
		e.setDirection(org.drugis.addis.entities.OutcomeMeasure.Direction.LOWER_IS_BETTER);
		
		assertEntityEquals(e, JAXBConvertor.convertEndpoint(m));
	}
	
	@Test(expected=ConversionException.class)
	public void testConvertCategoricalEndpointThrows() throws ConversionException {
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName("Gender");
		m.setDescription("Which gender you turn out to be after taking medication");
		CategoricalVariable value = new CategoricalVariable();
		m.setCategorical(value);
		
		JAXBConvertor.convertEndpoint(m);
	}

	@Test
	public void testConvertContinuousAdverseEvent() throws ConversionException {
		String name = "Onset of erection";
		String desc = "Time to onset of erection of >= 60 % rigidity";
		String unit = "Minutes";
		
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		ContinuousVariable value = new ContinuousVariable();
		value.setDirection(Direction.LOWER_IS_BETTER);
		value.setUnitOfMeasurement(unit);
		m.setContinuous(value);
		
		AdverseEvent e = new AdverseEvent(name, Type.CONTINUOUS);
		e.setDescription(desc);
		e.setUnitOfMeasurement(unit);
		
		assertEntityEquals(e, JAXBConvertor.convertAdverseEvent(m));
	}
	
	@Test
	public void testConvertRateAdverseEvent() throws ConversionException {
		String name = "Seizure";
		String desc = "Its bad hmmkay";
		
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		RateVariable value = new RateVariable();
		value.setDirection(Direction.LOWER_IS_BETTER);
		m.setRate(value);
		
		AdverseEvent e = new AdverseEvent(name, Type.RATE);
		e.setDescription(desc);
		
		assertEntityEquals(e, JAXBConvertor.convertAdverseEvent(m));
		
		value.setDirection(Direction.HIGHER_IS_BETTER);
		e.setDirection(org.drugis.addis.entities.OutcomeMeasure.Direction.HIGHER_IS_BETTER);
		
		assertEntityEquals(e, JAXBConvertor.convertAdverseEvent(m));
	}
	
	@Test(expected=ConversionException.class)
	public void testConvertCategoricalAdverseEventThrows() throws ConversionException {
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName("Efficacy");
		m.setDescription("Erection of >= 60% rigidity within 1 hr of medication");
		CategoricalVariable value = new CategoricalVariable();
		m.setCategorical(value);
		
		JAXBConvertor.convertAdverseEvent(m);
	}

	@Test
	public void testConvertContinuousPopChar() throws ConversionException {
		String name = "Age";
		String desc = "Age (years since birth)";
		String unit = "Years";
		
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		ContinuousVariable value = new ContinuousVariable();
		value.setUnitOfMeasurement(unit);
		m.setContinuous(value);
		
		ContinuousPopulationCharacteristic p = new ContinuousPopulationCharacteristic(name);
		p.setUnitOfMeasurement(unit);
		p.setDescription(desc);
		
		assertEntityEquals(p, JAXBConvertor.convertPopulationCharacteristic(m));
	}
	
	@Test(expected=ConversionException.class)
	public void testConvertRatePopCharThrows() throws ConversionException {
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName("Seizure");
		m.setDescription("Its bad hmmkay");
		RateVariable value = new RateVariable();
		value.setDirection(Direction.LOWER_IS_BETTER);
		m.setRate(value);
		
		JAXBConvertor.convertPopulationCharacteristic(m);
	}
	
	@Test
	public void testConvertCategoricalPopChar() throws ConversionException {
		String name = "Smoking habits";
		String desc = "Classification of smoking habits";
		String[] categories = new String[] {"Non-smoker", "Smoker", "Ex-smoker"};
		
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		CategoricalVariable var = new CategoricalVariable();
		for (String s : categories) {
			var.getCategory().add(s);
		}
		m.setCategorical(var);
		
		CategoricalPopulationCharacteristic catChar = new CategoricalPopulationCharacteristic(name, categories);
		catChar.setDescription(desc);
		
		assertEntityEquals(catChar, JAXBConvertor.convertPopulationCharacteristic(m));
	}

	@Test
	public void testConvertIndication() {
		String name = "Erectile Dysfunction";
		long code = 12;
		
		org.drugis.addis.entities.data.Indication i1 = new org.drugis.addis.entities.data.Indication(); 
		i1.setCode(code);
		i1.setName(name);
	
		Indication i2 = new Indication(code, name);
		
		assertEntityEquals(i2, JAXBConvertor.convertIndication(i1));
	}
	
	@Test
	public void testConvertDrug() {
		String name = "Sildenafil";
		String code = "G04BE03";
		
		org.drugis.addis.entities.data.Drug d1 = new org.drugis.addis.entities.data.Drug(); 
		d1.setAtcCode(code);
		d1.setName(name);
	
		Drug d2 = new Drug(name, code);
		
		assertEntityEquals(d2, JAXBConvertor.convertDrug(d1));
	}
	
	@Test
	public void testConvertArm() throws ConversionException {
		int size = 99;
		String name = "Sildenafil";
		String code = "G04BE03";
		double quantity = 12.5;
		double maxQuantity = 34.5;
		
		Domain domain = new DomainImpl();
		Drug drug = new Drug(name, code);
		domain.addDrug(drug);
		
		org.drugis.addis.entities.data.Arm arm1 = new org.drugis.addis.entities.data.Arm();
		arm1.setId(1);
		arm1.setSize(size);
		arm1.setNotes(new Notes());
		org.drugis.addis.entities.data.FixedDose fixDose = new org.drugis.addis.entities.data.FixedDose();
		fixDose.setQuantity(quantity);
		fixDose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		arm1.setFixedDose(fixDose);
		NameReference nameRef = new NameReference();
		nameRef.setName(name);
		arm1.setDrug(nameRef);
		
		Arm arm2 = buildFixedDoseArm(size, drug, quantity);
		
		assertEntityEquals(arm2, JAXBConvertor.convertArm(arm1, domain));
		
		arm1.setFixedDose(null);
		org.drugis.addis.entities.data.FlexibleDose flexDose = new org.drugis.addis.entities.data.FlexibleDose();
		flexDose.setMinDose(quantity);
		flexDose.setMaxDose(maxQuantity);
		flexDose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		arm1.setFlexibleDose(flexDose);
		
		Arm arm3 = buildFlexibleDoseArm(size, drug, quantity, maxQuantity);
		
		assertEntityEquals(arm3, JAXBConvertor.convertArm(arm1, domain));
	}

	private Arm buildFixedDoseArm(int size, Drug drug, double quantity) {
		FixedDose fixDose2 = new FixedDose(quantity, SIUnit.MILLIGRAMS_A_DAY);
		Arm arm2 = new Arm(drug, fixDose2, size);
		return arm2;
	}

	private Arm buildFlexibleDoseArm(int size, Drug drug,
			double minQuantity, double maxQuantity) {
		FlexibleDose flexDose2 = new FlexibleDose(new Interval<Double> (minQuantity, maxQuantity), SIUnit.MILLIGRAMS_A_DAY);
		Arm arm3 = new Arm(drug, flexDose2, size);
		return arm3;
	}
	
	@Test
	public void testConvertArms() throws ConversionException {
		int size1 = 99;
		int size2 = 101;
		String name = "Sildenafil";
		String code = "G04BE03";
		double quantity = 10.0;
		double minQuantity = 12.5;
		double maxQuantity = 34.5;
		
		Domain domain = new DomainImpl();
		Drug drug = new Drug(name, code);
		domain.addDrug(drug);
		
		org.drugis.addis.entities.data.Arm arm1 = buildFixedDoseArmData(1,
				size1, name, quantity);
		
		org.drugis.addis.entities.data.Arm arm2 = buildFlexibleDoseArmData(
				2, size2, name, minQuantity, maxQuantity);
		
		Arms arms = new Arms();
		arms.getArm().add(arm1);
		arms.getArm().add(arm2);
		
		LinkedHashMap<Integer, Arm> convertedArms = JAXBConvertor.convertStudyArms(arms, domain);
		Set<Integer> keys = new HashSet<Integer>();
		keys.add(1);
		keys.add(2);
		assertEquals(keys , convertedArms.keySet());
		assertEquals(size1, (int)convertedArms.get(1).getSize());
		assertEquals(size2, (int)convertedArms.get(2).getSize());
		assertEquals(FixedDose.class, convertedArms.get(1).getDose().getClass());
		assertEquals(FlexibleDose.class, convertedArms.get(2).getDose().getClass());
	}

	private org.drugis.addis.entities.data.Arm buildFlexibleDoseArmData(
			Integer id, int size2, String name, double minQuantity, double maxQuantity) {
		org.drugis.addis.entities.data.Arm arm2 = new org.drugis.addis.entities.data.Arm();
		arm2.setId(id);
		arm2.setSize(size2);
		arm2.setNotes(new Notes());
		org.drugis.addis.entities.data.FlexibleDose flexDose = new org.drugis.addis.entities.data.FlexibleDose();
		flexDose.setMinDose(minQuantity);
		flexDose.setMaxDose(maxQuantity);
		flexDose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		arm2.setFlexibleDose(flexDose);
		arm2.setDrug(nameReference(name));
		return arm2;
	}

	private org.drugis.addis.entities.data.Arm buildFixedDoseArmData(
			Integer id, int size1, String name, double quantity) {
		org.drugis.addis.entities.data.Arm arm1 = new org.drugis.addis.entities.data.Arm();
		arm1.setId(id);
		arm1.setSize(size1);
		arm1.setNotes(new Notes());
		org.drugis.addis.entities.data.FixedDose fixDose = new org.drugis.addis.entities.data.FixedDose();
		fixDose.setQuantity(quantity);
		fixDose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		arm1.setFixedDose(fixDose);
		arm1.setDrug(nameReference(name));
		return arm1;
	}
	
	@Test
	public void testConvertStudyChars() {
		Allocation alloc = Allocation.RANDOMIZED;
		Blinding blind = Blinding.UNKNOWN;
		String title = "MyStudy";
		int centers = 5;
		String objective = "The loftiest of goals";
		String incl = "Obesity";
		String excl = "Diabetes";
		Status status = Status.ENROLLING;
		Source source = Source.MANUAL;
		GregorianCalendar studyStart = new GregorianCalendar(2008, 8, 12);
		GregorianCalendar studyEnd = new GregorianCalendar(2010, 1, 18);
		GregorianCalendar created = new GregorianCalendar(2011, 2, 15);
		PubMedIdList pmids = new PubMedIdList();
		pmids.add(new PubMedId("1"));
		pmids.add(new PubMedId("12345"));
		List<Integer> pmints = new ArrayList<Integer>();
		for (PubMedId id : pmids) {
			pmints.add(Integer.parseInt(id.toString()));
		}
		
		org.drugis.addis.entities.data.Characteristics chars1 = new org.drugis.addis.entities.data.Characteristics();
		chars1.setTitle(stringWithNotes(title));
		chars1.setAllocation(allocationWithNotes(alloc));
		chars1.setBlinding(blindingWithNotes(blind));
		chars1.setCenters(intWithNotes(centers));
		chars1.setObjective(stringWithNotes(objective));
		chars1.setStudyStart(dateWithNotes(studyStart));
		chars1.setStudyEnd(dateWithNotes(studyEnd));
		chars1.setStatus(statusWithNotes(status));
		chars1.setInclusion(stringWithNotes(incl));
		chars1.setExclusion(stringWithNotes(excl));
		References refs = new References();
		refs.getPubMedId().addAll(pmints);
		chars1.setReferences(refs);
		chars1.setSource(sourceWithNotes(source));
		chars1.setCreationDate(dateWithNotes(created));

		
		CharacteristicsMap chars2 = new CharacteristicsMap();
		chars2.put(BasicStudyCharacteristic.TITLE, title);
		chars2.put(BasicStudyCharacteristic.ALLOCATION, alloc);
		chars2.put(BasicStudyCharacteristic.BLINDING, blind);
		chars2.put(BasicStudyCharacteristic.CENTERS, centers);
		chars2.put(BasicStudyCharacteristic.OBJECTIVE, objective);
		chars2.put(BasicStudyCharacteristic.STUDY_START, studyStart.getTime());
		chars2.put(BasicStudyCharacteristic.STUDY_END, studyEnd.getTime());
		chars2.put(BasicStudyCharacteristic.INCLUSION, incl);
		chars2.put(BasicStudyCharacteristic.EXCLUSION, excl);
		chars2.put(BasicStudyCharacteristic.PUBMED, pmids); // References
		chars2.put(BasicStudyCharacteristic.STATUS, status);
		chars2.put(BasicStudyCharacteristic.SOURCE, source);
		chars2.put(BasicStudyCharacteristic.CREATION_DATE, created.getTime());
		
		assertEntityEquals(chars2, JAXBConvertor.convertStudyCharacteristics(chars1));

	}

	private DateWithNotes dateWithNotes(GregorianCalendar cal) {
		org.drugis.addis.entities.data.DateWithNotes date = new org.drugis.addis.entities.data.DateWithNotes();
		date.setValue(new XMLGregorianCalendarImpl(cal));
		date.setNotes(new Notes());
		return date;
	}

	private org.drugis.addis.entities.data.Source sourceWithNotes(Source nested) {
		org.drugis.addis.entities.data.Source source = new org.drugis.addis.entities.data.Source();
		source.setValue(nested);
		source.setNotes(new Notes());
		return source;
	}

	private org.drugis.addis.entities.data.Status statusWithNotes(Status nested) {
		org.drugis.addis.entities.data.Status status = new org.drugis.addis.entities.data.Status();
		status.setValue(nested);
		status.setNotes(new Notes());
		return status;
	}

	private org.drugis.addis.entities.data.IntegerWithNotes intWithNotes(int centers) {
		org.drugis.addis.entities.data.IntegerWithNotes integer = new org.drugis.addis.entities.data.IntegerWithNotes();
		integer.setValue(centers);
		integer.setNotes(new Notes());
		return integer;
	}

	private org.drugis.addis.entities.data.Blinding blindingWithNotes(Blinding nested) {
		org.drugis.addis.entities.data.Blinding blinding = new org.drugis.addis.entities.data.Blinding();
		blinding.setValue(nested);
		blinding.setNotes(new Notes());
		return blinding;
	}

	private org.drugis.addis.entities.data.Allocation allocationWithNotes(Allocation nested) {
		org.drugis.addis.entities.data.Allocation allocation = new org.drugis.addis.entities.data.Allocation();
		allocation.setValue(nested);
		allocation.setNotes(new Notes());
		return allocation;
	}

	com.sun.xml.bind.v2.runtime.JAXBContextImpl context;
	
	private StringWithNotes stringWithNotes(String string) {
		StringWithNotes strNot = new StringWithNotes();
		strNot.setValue(string);
		strNot.setNotes(new Notes());
		return strNot;
	}
	
	@Test
	public void testConvertStudyOutcomeMeasure() throws ConversionException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		
		Endpoint ep = ExampleData.buildEndpointHamd();
		StudyOutcomeMeasure om = new StudyOutcomeMeasure();
		om.setEndpoint(nameReference(ep.getName()));
		assertEntityEquals(ep, JAXBConvertor.convertStudyOutcomeMeasure(om, domain));
		
		AdverseEvent ade = ExampleData.buildAdverseEventDiarrhea();
		domain.addAdverseEvent(ade);
		om.setEndpoint(null);
		om.setAdverseEvent(nameReference(ade.getName()));
		assertEntityEquals(ade, JAXBConvertor.convertStudyOutcomeMeasure(om, domain));
		
		PopulationCharacteristic pc = ExampleData.buildGenderVariable();
		domain.addPopulationCharacteristic(pc);
		om.setAdverseEvent(null);
		om.setPopulationCharacteristic(nameReference(pc.getName()));
		assertEntityEquals(pc, JAXBConvertor.convertStudyOutcomeMeasure(om, domain));
	}
	
	@Test(expected=ConversionException.class)
	public void testConvertStudyOutcomeMeasureThrows() throws ConversionException {
		Domain domain = new DomainImpl();
		StudyOutcomeMeasure om = new StudyOutcomeMeasure();
		JAXBConvertor.convertStudyOutcomeMeasure(om, domain);
	}
	
	@Test
	public void testConvertStudyOutcomeMeasures() throws ConversionException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);		
		Endpoint ep = ExampleData.buildEndpointHamd();
		AdverseEvent ade = ExampleData.buildAdverseEventDiarrhea();
		domain.addAdverseEvent(ade);
		LinkedHashMap<String, Variable> vars = new LinkedHashMap<String, Variable>();
		vars.put("X", ep);
		vars.put("Y", ade);

		StudyOutcomeMeasure epRef = new StudyOutcomeMeasure();
		epRef.setId("X");
		epRef.setEndpoint(nameReference(ep.getName()));
		StudyOutcomeMeasure adeRef = new StudyOutcomeMeasure();
		adeRef.setId("Y");
		adeRef.setAdverseEvent(nameReference(ade.getName()));
		StudyOutcomeMeasures oms = new StudyOutcomeMeasures();
		oms.getStudyOutcomeMeasure().add(epRef);
		oms.getStudyOutcomeMeasure().add(adeRef);
		
		assertEquals(vars, JAXBConvertor.convertStudyOutcomeMeasures(oms, domain));
	}
	
	@Test
	public void testConvertMeasurement() throws ConversionException {
		org.drugis.addis.entities.data.RateMeasurement rm = new org.drugis.addis.entities.data.RateMeasurement();
		int c = 12;
		int s = 42;
		rm.setRate(c);
		rm.setSampleSize(s);
		org.drugis.addis.entities.data.Measurement meas = new org.drugis.addis.entities.data.Measurement();
		meas.setRateMeasurement(rm);
		assertEntityEquals(new BasicRateMeasurement(c, s), JAXBConvertor.convertMeasurement(meas));
		
		org.drugis.addis.entities.data.ContinuousMeasurement cm = new org.drugis.addis.entities.data.ContinuousMeasurement();
		double m = 3.14;
		double e = 2.71;
		cm.setMean(m);
		cm.setStdDev(e);
		cm.setSampleSize(s);
		meas = new org.drugis.addis.entities.data.Measurement();
		meas.setContinuousMeasurement(cm);
		assertEntityEquals(new BasicContinuousMeasurement(m, e, s), JAXBConvertor.convertMeasurement(meas));
		
		org.drugis.addis.entities.data.CategoricalMeasurement fm = new org.drugis.addis.entities.data.CategoricalMeasurement();
		Category c1 = new Category();
		c1.setName("Cats");
		c1.setRate(18);
		Category c2 = new Category();
		c2.setName("Dogs");
		c2.setRate(2145);
		fm.getCategory().add(c1);
		fm.getCategory().add(c2);
		meas = new org.drugis.addis.entities.data.Measurement();
		meas.setCategoricalMeasurement(fm);
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("Dogs", 2145);
		map.put("Cats", 18);
		FrequencyMeasurement expected = new FrequencyMeasurement(new String[] {"Cats", "Dogs"}, map);
		assertEntityEquals(expected, JAXBConvertor.convertMeasurement(meas));
	}
	
	@Test
	public void testConvertMeasurements() throws ConversionException {
		Map<Integer, Arm> arms = new HashMap<Integer, Arm>();
		Arm arm5 = new Arm(new Drug("Opium", "OPIUM4TW"), new FixedDose(100.0, SIUnit.MILLIGRAMS_A_DAY), 42);
		arms.put(5, arm5);
		Arm arm8 = new Arm(new Drug("LSD", "UFO"), new FixedDose(100.0, SIUnit.MILLIGRAMS_A_DAY), 42);
		arms.put(8, arm8);
		Map<String, Variable> oms = new HashMap<String, Variable>();
		String pcName = "popChar-hair";
		ContinuousPopulationCharacteristic pc = new ContinuousPopulationCharacteristic("Hair Length");
		oms.put(pcName, pc);
		String epName = "endpoint-tripping";
		Endpoint ep = new Endpoint("Tripping achieved", Type.RATE, Direction.HIGHER_IS_BETTER);
		oms.put(epName, ep);
		String aeName = "ade-nojob";
		AdverseEvent ae = new AdverseEvent("Job loss", Type.RATE);
		oms.put(aeName, ae);
		
		org.drugis.addis.entities.data.RateMeasurement rm1 = new org.drugis.addis.entities.data.RateMeasurement();
		rm1.setRate(10);
		rm1.setSampleSize(100);
		BasicRateMeasurement crm1 = new BasicRateMeasurement(10, 100);
		
		org.drugis.addis.entities.data.RateMeasurement rm2 = new org.drugis.addis.entities.data.RateMeasurement();
		rm2.setRate(20);
		rm2.setSampleSize(100);
		BasicRateMeasurement crm2 = new BasicRateMeasurement(20, 100);
		
		org.drugis.addis.entities.data.ContinuousMeasurement cm1 = new org.drugis.addis.entities.data.ContinuousMeasurement();
		cm1.setMean(1.5);
		cm1.setStdDev(1.0);
		cm1.setSampleSize(100);
		BasicContinuousMeasurement ccm1 = new BasicContinuousMeasurement(1.5, 1.0, 100);
		
		Measurements measurements = new Measurements();
		List<org.drugis.addis.entities.data.Measurement> list = measurements.getMeasurement();
		org.drugis.addis.entities.data.Measurement m1 = new org.drugis.addis.entities.data.Measurement();
		m1.setArm(idReference(5));
		m1.setStudyOutcomeMeasure(stringIdReference(epName));
		m1.setRateMeasurement(rm1);
		org.drugis.addis.entities.data.Measurement m2 = new org.drugis.addis.entities.data.Measurement();
		m2.setArm(idReference(8));
		m2.setStudyOutcomeMeasure(stringIdReference(epName));
		m2.setRateMeasurement(rm2);
		org.drugis.addis.entities.data.Measurement m3 = new org.drugis.addis.entities.data.Measurement();
		m3.setArm(idReference(5));
		m3.setStudyOutcomeMeasure(stringIdReference(aeName));
		m3.setRateMeasurement(rm2);
		org.drugis.addis.entities.data.Measurement m4 = new org.drugis.addis.entities.data.Measurement();
		m4.setArm(idReference(8));
		m4.setStudyOutcomeMeasure(stringIdReference(aeName));
		m4.setRateMeasurement(rm1);
		org.drugis.addis.entities.data.Measurement m5 = new org.drugis.addis.entities.data.Measurement();
		m5.setArm(idReference(5));
		m5.setStudyOutcomeMeasure(stringIdReference(pcName));
		m5.setContinuousMeasurement(cm1);
		org.drugis.addis.entities.data.Measurement m6 = new org.drugis.addis.entities.data.Measurement();
		m6.setArm(idReference(8));
		m6.setStudyOutcomeMeasure(stringIdReference(pcName));
		m6.setContinuousMeasurement(cm1);
		org.drugis.addis.entities.data.Measurement m7 = new org.drugis.addis.entities.data.Measurement();
		m7.setArm(null);
		m7.setStudyOutcomeMeasure(stringIdReference(pcName));
		m7.setContinuousMeasurement(cm1);
		list.add(m1);		
		list.add(m2);
		list.add(m3);
		list.add(m4);
		list.add(m5);
		list.add(m6);
		list.add(m7);
		
		Map<MeasurementKey, Measurement> expected = new HashMap<MeasurementKey, Measurement>();
		expected.put(new MeasurementKey(ep, arm5), crm1);
		expected.put(new MeasurementKey(ep, arm8), crm2);
		expected.put(new MeasurementKey(ae, arm5), crm2);
		expected.put(new MeasurementKey(ae, arm8), crm1);
		expected.put(new MeasurementKey(pc, arm5), ccm1);
		expected.put(new MeasurementKey(pc, arm8), ccm1);
		expected.put(new MeasurementKey(pc, null), ccm1);
		assertEquals(expected, JAXBConvertor.convertMeasurements(measurements, arms, oms));
	}

	private StringIdReference stringIdReference(String id) {
		StringIdReference ref = new StringIdReference();
		ref.setId(id);
		return ref;
	}

	private IdReference idReference(int id) {
		IdReference ref = new IdReference();
		ref.setId(id);
		return ref;
	}

	public NameReference nameReference(String name) {
		NameReference ref = new NameReference();
		ref.setName(name);
		return ref;
	}

	@Test
	public void testConvertStudy() throws ConversionException {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		domain.addEndpoint(ExampleData.buildEndpointCgi());
		domain.addAdverseEvent(ExampleData.buildAdverseEventConvulsion());
		
		String name = "My fancy study";
		org.drugis.addis.entities.data.Study study = new org.drugis.addis.entities.data.Study();
		study.setName(name);
		NameReferenceWithNotes indicationRef = new NameReferenceWithNotes();
		indicationRef.setName(ExampleData.buildIndicationDepression().getName());
		indicationRef.setNotes(new Notes());
		study.setIndication(indicationRef);
		
		// Endpoints
		StudyOutcomeMeasures studyOutcomeMeasures = new StudyOutcomeMeasures();
		study.setStudyOutcomeMeasures(studyOutcomeMeasures);
		StudyOutcomeMeasure ep1 = new StudyOutcomeMeasure();
		String ep1ref = "endpoint-" + ExampleData.buildEndpointHamd().getName();
		ep1.setId(ep1ref);
		ep1.setEndpoint(nameReference(ExampleData.buildEndpointHamd().getName()));
		studyOutcomeMeasures.getStudyOutcomeMeasure().add(ep1);
		StudyOutcomeMeasure ep2 = new StudyOutcomeMeasure();
		String ep2ref = "endpoint-" + ExampleData.buildEndpointCgi().getName();
		ep2.setId(ep2ref);
		ep2.setEndpoint(nameReference(ExampleData.buildEndpointCgi().getName()));
		studyOutcomeMeasures.getStudyOutcomeMeasure().add(ep2);
		
		// Adverse events
		StudyOutcomeMeasure ae1 = new StudyOutcomeMeasure();
		String ae1ref = "adverseEvent-" + ExampleData.buildAdverseEventConvulsion().getName();
		ae1.setId(ae1ref);
		ae1.setAdverseEvent(nameReference(ExampleData.buildAdverseEventConvulsion().getName()));
		studyOutcomeMeasures.getStudyOutcomeMeasure().add(ae1);
		
		// Population chars
		StudyOutcomeMeasure pc1 = new StudyOutcomeMeasure();
		String pc1ref = "popChar-" + ExampleData.buildAgeVariable().getName();
		pc1.setId(pc1ref);
		pc1.setPopulationCharacteristic(nameReference(ExampleData.buildAgeVariable().getName()));
		studyOutcomeMeasures.getStudyOutcomeMeasure().add(pc1);
		
		// Arms
		Arms arms = new Arms();
		study.setArms(arms);
		arms.getArm().add(buildFixedDoseArmData(1, 100, ExampleData.buildDrugFluoxetine().getName(), 12.5));
		arms.getArm().add(buildFixedDoseArmData(2, 102, ExampleData.buildDrugParoxetine().getName(), 12.5));

		// Study characteristics
		Characteristics chars = new Characteristics();
		study.setCharacteristics(chars);
		chars.setTitle(stringWithNotes("WHOO"));
		chars.setCenters(intWithNotes(3));
		chars.setAllocation(allocationWithNotes(Allocation.RANDOMIZED));
		chars.setReferences(new References());
		
		// Measurements
		Measurements measurements = new Measurements();
		List<org.drugis.addis.entities.data.Measurement> list = measurements.getMeasurement();
		study.setMeasurements(measurements);
		org.drugis.addis.entities.data.Measurement m1 = new org.drugis.addis.entities.data.Measurement();
		m1.setArm(idReference(2));
		m1.setStudyOutcomeMeasure(stringIdReference(ep1ref));
		RateMeasurement rm1 = new RateMeasurement();
		rm1.setRate(10);
		rm1.setSampleSize(110);
		m1.setRateMeasurement(rm1);
		list.add(m1);
		org.drugis.addis.entities.data.Measurement m2 = new org.drugis.addis.entities.data.Measurement();
		m2.setArm(null);
		m2.setStudyOutcomeMeasure(stringIdReference(pc1ref));
		ContinuousMeasurement cm1 = new ContinuousMeasurement();
		cm1.setMean(0.2);
		cm1.setStdDev(0.01);
		cm1.setSampleSize(110);
		m2.setContinuousMeasurement(cm1);
		list.add(m2);
		//----------------------------------------
		Study study2 = new Study();
		study2.setStudyId(name);
		study2.setIndication(ExampleData.buildIndicationDepression());
		study2.addEndpoint(ExampleData.buildEndpointHamd());
		study2.addEndpoint(ExampleData.buildEndpointCgi());
		study2.addAdverseEvent(ExampleData.buildAdverseEventConvulsion());
		study2.addOutcomeMeasure(ExampleData.buildAgeVariable());
		Arm arm1 = buildFixedDoseArm(100, ExampleData.buildDrugFluoxetine(), 12.5);
		study2.addArm(arm1);
		Arm arm2 = buildFixedDoseArm(102, ExampleData.buildDrugParoxetine(), 12.5);
		study2.addArm(arm2);
		study2.setCharacteristic(BasicStudyCharacteristic.TITLE, "WHOO");
		study2.setCharacteristic(BasicStudyCharacteristic.CENTERS, 3);
		study2.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, Allocation.RANDOMIZED);
		study2.setCharacteristic(BasicStudyCharacteristic.PUBMED, new PubMedIdList());
		study2.setMeasurement(ExampleData.buildEndpointHamd(), arm2, JAXBConvertor.convertMeasurement(m1));
		study2.setMeasurement(ExampleData.buildAgeVariable(), JAXBConvertor.convertMeasurement(m2));
		
		assertEntityEquals(study2, JAXBConvertor.convertStudy(study, domain));
	}
	
	@Test
	public void testConvertPairWiseMetaAnalysis() {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		domain.addEndpoint(ExampleData.buildEndpointCgi());
		domain.addAdverseEvent(ExampleData.buildAdverseEventConvulsion());
		domain.addIndication(ExampleData.buildIndicationDepression());
	
		String name = "Fluox-Venla Diarrhea";	
		org.drugis.addis.entities.data.PairwiseMetaAnalysis pwma = new org.drugis.addis.entities.data.PairwiseMetaAnalysis();
		pwma.setName(name);		
		pwma.setIndication(nameReference(ExampleData.buildIndicationDepression().getName()));
		pwma.setEndpoint(nameReference(ExampleData.buildEndpointHamd().getName()));
		pwma.setAdverseEvent(nameReference(ExampleData.buildAdverseEventDiarrhea().getName()));
		
		String study_name = "My fancy study";
		org.drugis.addis.entities.data.Study study = new org.drugis.addis.entities.data.Study();
		study.setName(study_name);
		NameReferenceWithNotes indicationRef = new NameReferenceWithNotes();
		indicationRef.setName(ExampleData.buildIndicationDepression().getName());
		indicationRef.setNotes(new Notes());
		study.setIndication(indicationRef);
		// arms
		Arms arms = new Arms();
		study.setArms(arms);
		arms.getArm().add(buildFixedDoseArmData(1, 100, ExampleData.buildDrugFluoxetine().getName(), 12.5));
		arms.getArm().add(buildFixedDoseArmData(2, 102, ExampleData.buildDrugParoxetine().getName(), 12.5));
		// om
		StudyOutcomeMeasures studyOutcomeMeasures = new StudyOutcomeMeasures();
		study.setStudyOutcomeMeasures(studyOutcomeMeasures);
		StudyOutcomeMeasure ep1 = new StudyOutcomeMeasure();
		String ep1ref = "endpoint-" + ExampleData.buildEndpointHamd().getName();
		ep1.setId(ep1ref);
		ep1.setEndpoint(nameReference(ExampleData.buildEndpointHamd().getName()));
		studyOutcomeMeasures.getStudyOutcomeMeasure().add(ep1);
				
		//-----------------------------------
		Study study2 = new Study();
		study2.setStudyId(name);
		
		List<StudyArmsEntry> armsList = new ArrayList<StudyArmsEntry>();
		Arm arm_base = buildFixedDoseArm(100, ExampleData.buildDrugFluoxetine(), 12.5);
		Arm arm_subject = buildFixedDoseArm(102, ExampleData.buildDrugParoxetine(), 12.5);
		study2.addArm(arm_subject);
		study2.addArm(arm_base);
		armsList.add(new StudyArmsEntry(study2, arm_base, arm_subject));
		
		study2.addEndpoint(ExampleData.buildEndpointHamd());
		study2.addOutcomeMeasure(ExampleData.buildEndpointHamd());
		study2.setIndication(new Indication(1L, ExampleData.buildIndicationDepression().getName()));
		
		RandomEffectsMetaAnalysis pwma2 = new RandomEffectsMetaAnalysis(name, ExampleData.buildEndpointHamd(), armsList);
		
		assertEntityEquals(pwma2, JAXBConvertor.convertPairWiseMetaAnalysis(pwma, domain));
		
	}
	
	@Test
	@Ignore
	public void testConvertNetworkMetaAnalysis() {
		fail();
	}
	
	@Test
	@Ignore
	public void testConvertMetaAnalyses() {
		fail();
	}
	
	@Test
	@Ignore
	public void testConvertMetaBenefitRiskAnalysis() {
		fail();
	}
	
	@Test
	@Ignore
	public void testConvertStudyBenefitRiskAnalysis() {
		fail();
	}
	
	@Test
	@Ignore
	public void testConvertBenefitRiskAnalyses() {
		fail();
	}
	
	@Test
	@Ignore
	public void testConvertStudyWithNotes() {
		fail("Note conversion not implemented");
	}
	
	@Test
	@Ignore
	// ACCEPTANCE TEST -- should be replaced by something nicer so we can remove the Javalution support.
	public void testAddisDataToDomainData() throws JAXBException, XMLStreamException, ConversionException {
		InputStream xmlStream = getClass().getResourceAsStream("defaultData.xml");
		assertNotNull(xmlStream);
		InputStream transformedXmlStream = getClass().getResourceAsStream("defaulttransformed.xml");
		assertNotNull(transformedXmlStream);
		DomainData importedDomainData = (DomainData)XMLHelper.fromXml(xmlStream);
		Domain importedDomain = new DomainImpl(importedDomainData);
		
		AddisData data = (AddisData) d_unmarshaller.unmarshal(transformedXmlStream);
		Domain domainData = JAXBConvertor.addisDataToDomain(data);
		assertDomainEquals(importedDomain, domainData);
	}
}
