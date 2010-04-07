package org.drugis.addis.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javolution.xml.XMLBinding;
import javolution.xml.XMLFormat;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.XMLReferenceResolver;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.CategoricalPopulationCharacteristic;
import org.drugis.addis.entities.CharacteristicsMap;
import org.drugis.addis.entities.ContinuousPopulationCharacteristic;
import org.drugis.addis.entities.DomainData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;

@SuppressWarnings("serial")
public class AddisBinding extends XMLBinding {

	public AddisBinding() {
		setAliases();
	}

	public static Object readModel(InputStream is) throws XMLStreamException {
		XMLObjectReader reader = new XMLObjectReader().setInput(is).setBinding(new AddisBinding());
		reader.setReferenceResolver(new XMLReferenceResolver());
		return reader.read();
	}

	public static void writeModel(Object model, OutputStream os) throws XMLStreamException {
		XMLObjectWriter writer = new XMLObjectWriter().setOutput(os).setBinding(new AddisBinding());
		writer.setReferenceResolver(new XMLReferenceResolver());		
		writer.setIndentation("\t");
//		The top level cannot be aliased, thats why its being renamed here (in JSMAA)
		if (model instanceof DomainData) {
			writer.write((DomainData) model, "ADDIS-Domain", DomainData.class);
		} else {
			writer.write(model, "Addis-model", Object.class);
		}
		writer.close();
	}
	
	private void setAliases() {
//		This part avoids the class hierarchy names in the xml code (indication instead of org.drugis.addis.entities.indication)
		setAlias(Study.class,"study");
		setAlias(Indication.class, "indication");
		setAlias(Endpoint.class, "endpoint");
		setAlias(AdverseEvent.class, "adverse event");
		setAlias(Drug.class, "drug");
		setAlias(PopulationCharacteristic.class, "population characteristic");
		
		setAlias(CategoricalPopulationCharacteristic.class, "categoricalcharacteristic");
		setAlias(ContinuousPopulationCharacteristic.class, "continuouscharacteristic");
		setAlias(org.drugis.addis.entities.Variable.Type.class, "type");
		setAlias(org.drugis.addis.entities.Study.MeasurementKey.class, "measurementkey");

		setAlias(FixedDose.class, "fixedDose");
		setAlias(FlexibleDose.class, "flexibleDose");
		setAlias(SIUnit.class, "SIUnit");
		setAlias(Arm.class, "arm");
		setAlias(Indication.class, "indication");
		setAlias(Variable.class, "variable");
		setAlias(Date.class, "date");
		
		
		setAlias(BasicStudyCharacteristic.class, "basicCharacteristic");
		setAlias(BasicStudyCharacteristic.Status.class, "status");
		setAlias(BasicStudyCharacteristic.Allocation.class, "allocation");
		setAlias(BasicStudyCharacteristic.Blinding.class, "blinding");
		setAlias(CharacteristicsMap.class, "characteristicMap");
		
		setAlias(BasicContinuousMeasurement.class, "continuousMeasurement");
		setAlias(BasicRateMeasurement.class, "rateMeasurement");
		setAlias(OutcomeMeasure.Direction.class, "direction");

		setAlias(Integer.class, "number");
		setAlias(String.class, "string");
		setAlias(DomainData.class, "addis-data");
		/*
		setAlias(GaussianMeasurement.class, "gaussian");
		setAlias(LogNormalMeasurement.class, "lognormal");
		setAlias(ExactMeasurement.class, "exact");
		setAlias(CriterionMeasurementPair.class, "criterionMeasurement");
		setAlias(CriterionAlternativeMeasurement.class, "criterionAlternativeMeasurement");
		setAlias(ScaleCriterion.class, "cardinalCriterion");
		setAlias(OrdinalCriterion.class, "ordinalCriterion");
		setAlias(OutrankingCriterion.class, "outrankingCriterion");
		setAlias(Alternative.class, "alternative");
		setAlias(CardinalPreferenceInformation.class, "cardinalPreferences");
		setAlias(OrdinalPreferenceInformation.class, "ordinalPreferences");
		setAlias(Interval.class, "interval");
		setAlias(SMAAModel.class, "SMAA-2-model");
		setAlias(SMAATRIModel.class, "SMAA-TRI-model");*/
	}
	
	// Override XMLFormatter for Date.class objects
	XMLFormat<Date> dateXML = new XMLFormat<Date>(null) {
		SimpleDateFormat sdf = new SimpleDateFormat("DD MMM yyyy");
		
		@Override
		public Date newInstance(Class<Date> cls, InputElement ie) throws XMLStreamException {
			try {
				return sdf.parse(ie.getAttribute("date").toString());
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public void read(javolution.xml.XMLFormat.InputElement xml, Date date) throws XMLStreamException {
		}

		@Override
		public void write(Date date, javolution.xml.XMLFormat.OutputElement xml) throws XMLStreamException {
			if (date != null)
				xml.setAttribute("date", sdf.format(date));
		}	
	}; // Unbound.
	
    @SuppressWarnings("unchecked")
	public XMLFormat getFormat(Class cls) throws XMLStreamException {
        if (Date.class.isAssignableFrom(cls)) {
            return dateXML; // Overrides default XML format.
        } else {
            return super.getFormat(cls);
        }
    }

}
