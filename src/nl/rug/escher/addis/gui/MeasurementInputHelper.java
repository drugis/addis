package nl.rug.escher.addis.gui;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatter;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ValueModel;

import nl.rug.escher.addis.entities.BasicContinuousMeasurement;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.BasicMeasurement;
import nl.rug.escher.addis.entities.BasicRateMeasurement;

public class MeasurementInputHelper {

	public static int numComponents(Endpoint e) {
		switch (e.getType()) {
		case CONTINUOUS:
			return 2;
		case RATE:
			return 1;
		default:
			throw new IllegalStateException("Unhandled enum value");
		}
	}

	public static String[] getHeaders(Endpoint e) {
		switch (e.getType()) {
		case CONTINUOUS:
			return new String[] {"Mean", "StdDev"};
		case RATE:
			return new String[] {"Rate"};
		default:
			throw new IllegalStateException("Unhandled enum value");
		}
	}

	public static JComponent[] getComponents(BasicMeasurement m) {
		if (m instanceof BasicContinuousMeasurement) {
			PresentationModel<BasicContinuousMeasurement> model = 
				new PresentationModel<BasicContinuousMeasurement>((BasicContinuousMeasurement)m);
			return new JComponent[] {
				MeasurementInputHelper.buildFormatted(model.getModel(BasicContinuousMeasurement.PROPERTY_MEAN)),
				MeasurementInputHelper.buildFormatted(model.getModel(BasicContinuousMeasurement.PROPERTY_STDDEV))
			};
		} else if (m instanceof BasicRateMeasurement) {
			PresentationModel<BasicRateMeasurement> model = 
				new PresentationModel<BasicRateMeasurement>((BasicRateMeasurement)m);
			return new JComponent[] {
				MeasurementInputHelper.buildFormatted(model.getModel(BasicRateMeasurement.PROPERTY_RATE))
			};
			
		}
		throw new IllegalStateException("Unhandled Measurement sub-type");
	}

	static JFormattedTextField buildFormatted(ValueModel model) {
		JFormattedTextField field = new JFormattedTextField(new DefaultFormatter());
		PropertyConnector.connectAndUpdate(model, field, "value");
		return field;
	}

}
