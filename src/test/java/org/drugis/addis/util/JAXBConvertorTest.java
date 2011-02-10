package org.drugis.addis.util;

import static org.drugis.addis.entities.AssertEntityEquals.assertDomainEquals;
import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
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
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.BasicStudyCharacteristic.Allocation;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.entities.data.CategoricalVariable;
import org.drugis.addis.entities.data.ContinuousVariable;
import org.drugis.addis.entities.data.NameReference;
import org.drugis.addis.entities.data.Notes;
import org.drugis.addis.entities.data.OutcomeMeasure;
import org.drugis.addis.entities.data.RateVariable;
import org.drugis.addis.entities.data.StringWithNotes;
import org.drugis.addis.util.JAXBConvertor.ConversionException;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class JAXBConvertorTest {

	private JAXBContext d_jaxb;
	private Marshaller d_marshaller;
	private Unmarshaller d_unmarshaller;
	
	@Before
	public void setup() throws JAXBException{
		d_jaxb = JAXBContext.newInstance("org.drugis.addis.entities.data" );
		d_unmarshaller = d_jaxb.createUnmarshaller();
		d_marshaller = d_jaxb.createMarshaller();
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
		
		FixedDose fixDose2 = new FixedDose(quantity, SIUnit.MILLIGRAMS_A_DAY);
		Arm arm2 = new Arm(drug, fixDose2, size);
		
		assertEntityEquals(arm2, JAXBConvertor.convertArm(arm1, domain));
		
		arm1.setFixedDose(null);
		org.drugis.addis.entities.data.FlexibleDose flexDose = new org.drugis.addis.entities.data.FlexibleDose();
		flexDose.setMinDose(quantity);
		flexDose.setMaxDose(maxQuantity);
		flexDose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		arm1.setFlexibleDose(flexDose);
		
		FlexibleDose flexDose2 = new FlexibleDose(new Interval<Double> (quantity, maxQuantity), SIUnit.MILLIGRAMS_A_DAY);
		Arm arm3 = new Arm(drug, flexDose2, size);
		
		assertEntityEquals(arm3, JAXBConvertor.convertArm(arm1, domain));
	}
	
	@Test
	public void testConvertStudyChars() {
		org.drugis.addis.entities.data.Characteristics chars1 = new org.drugis.addis.entities.data.Characteristics();
		chars1.setTitle(stringWithNotes("MyStudy"));
		Allocation alloc = Allocation.RANDOMIZED;
		chars1.setAllocation(allocationWithNotes(alloc));
		
		CharacteristicsMap chars2 = new CharacteristicsMap();
		chars2.put(BasicStudyCharacteristic.ALLOCATION, alloc);
		
		assertEntityEquals(chars2, JAXBConvertor.convertStudyCharacteristics(chars1));
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
	@Ignore
	public void testConvertStudy() {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		
		org.drugis.addis.entities.data.Study study = new org.drugis.addis.entities.data.Study();
		

		Study study2 = new Study();
		
		fail();
	}

	@Test
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
