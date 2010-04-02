package org.drugis.addis.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.drugis.addis.entities.*;

import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.XMLReferenceResolver;
import javolution.xml.stream.XMLStreamException;

@SuppressWarnings("serial")
public class AddisBinding extends XMLBinding {

	public AddisBinding() {
		setAliases();
	}

	public static Entity readModel(InputStream is) throws XMLStreamException {
		XMLObjectReader reader = new XMLObjectReader().setInput(is).setBinding(new AddisBinding());
		reader.setReferenceResolver(new XMLReferenceResolver());
		return reader.read();
	}
	// SMAAModel -> Entity
	public static void writeModel(Entity model, OutputStream os) throws XMLStreamException {
		XMLObjectWriter writer = new XMLObjectWriter().setOutput(os).setBinding(new AddisBinding());
		writer.setReferenceResolver(new XMLReferenceResolver());		
		writer.setIndentation("\t");
//		The top level cannot be aliased, thats why its being renamed here (in JSMAA)
//		if (model instanceof SMAATRIModel) {
//			writer.write((SMAATRIModel) model, "SMAA-TRI-model", SMAATRIModel.class);
//		} else {
			writer.write(model, "Addis-model", Entity.class);
//		}
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
}
