package nl.rug.escher.gui;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatter;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ValueModel;

import nl.rug.escher.entities.ContinuousMeasurement;
import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.RateMeasurement;

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

	public static JComponent[] getComponents(Measurement m) {
		if (m instanceof ContinuousMeasurement) {
			PresentationModel<ContinuousMeasurement> model = 
				new PresentationModel<ContinuousMeasurement>((ContinuousMeasurement)m);
			return new JComponent[] {
				MeasurementInputHelper.buildFormatted(model.getModel(ContinuousMeasurement.PROPERTY_MEAN)),
				MeasurementInputHelper.buildFormatted(model.getModel(ContinuousMeasurement.PROPERTY_STDDEV))
			};
		} else if (m instanceof RateMeasurement) {
			PresentationModel<RateMeasurement> model = 
				new PresentationModel<RateMeasurement>((RateMeasurement)m);
			return new JComponent[] {
				MeasurementInputHelper.buildFormatted(model.getModel(RateMeasurement.PROPERTY_RATE))
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
