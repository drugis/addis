/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.gui;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DefaultFormatterFactory;

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.presentation.FrequencyMeasurementPresentation;
import org.drugis.addis.presentation.wizard.MissingMeasurementPresentation;
import org.drugis.addis.util.MissingValueFormat;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ConverterFactory;
import com.jgoodies.binding.value.ValueModel;

public class MeasurementInputHelper {
	public static int numComponents(Measurement m) {
		return getHeaders(m).length;
	}

	public static int numComponents(OutcomeMeasure e) {
		return getHeaders(e).length;
	}

	public static String[] getHeaders(OutcomeMeasure e) {
		return getHeaders(e.buildMeasurement(0));
	}
	
	public static String[] getHeaders(Measurement m) {
		if (m instanceof ContinuousMeasurement) {
			return new String[] {"Mean", "StdDev", "Subjects"};
		} else if (m instanceof RateMeasurement) {
			return new String[] {"Occurence", "Subjects"};
		} else if (m instanceof FrequencyMeasurement) {
			return ((FrequencyMeasurement)m).getCategories();
		}
		throw new IllegalStateException("Unhandled measurement type");
	}

	public static JTextField[] getComponents(MissingMeasurementPresentation mmp) {
		DoubleFormatter doubleFormatter = new DoubleFormatter(); 
		IntegerFormatter intFormatter = new IntegerFormatter();
		BasicMeasurement m = mmp.getMeasurement();
		if (m instanceof BasicContinuousMeasurement) {
			PresentationModel<BasicContinuousMeasurement> model = 
				new PresentationModel<BasicContinuousMeasurement>((BasicContinuousMeasurement)m);
			
			return new JTextField[] {
				MeasurementInputHelper.buildFormatted(model.getModel(BasicContinuousMeasurement.PROPERTY_MEAN), mmp.getMissingModel(), doubleFormatter),
				MeasurementInputHelper.buildFormatted(model.getModel(BasicContinuousMeasurement.PROPERTY_STDDEV), mmp.getMissingModel(),doubleFormatter),
				MeasurementInputHelper.buildFormatted(model.getModel(BasicContinuousMeasurement.PROPERTY_SAMPLESIZE), mmp.getMissingModel(), intFormatter)
			};
		} else if (m instanceof BasicRateMeasurement) {
			PresentationModel<BasicRateMeasurement> model = 
				new PresentationModel<BasicRateMeasurement>((BasicRateMeasurement)m);
			return new JTextField[] {
				MeasurementInputHelper.buildFormatted(model.getModel(BasicRateMeasurement.PROPERTY_RATE), mmp.getMissingModel(), intFormatter),
				MeasurementInputHelper.buildFormatted(model.getModel(BasicRateMeasurement.PROPERTY_SAMPLESIZE), mmp.getMissingModel(), intFormatter)
			};
			
		} else if (m instanceof FrequencyMeasurement) {
			List<JTextField> comps = new ArrayList<JTextField>();
			FrequencyMeasurement fm = (FrequencyMeasurement) m;
			FrequencyMeasurementPresentation model = new FrequencyMeasurementPresentation(fm);
			for (String cat : fm.getCategories()) {
				comps.add(MeasurementInputHelper.buildFormatted(model.getFrequencyModel(cat), mmp.getMissingModel(), intFormatter));
			}
			return comps.toArray(new JTextField[]{});
		}
		throw new IllegalStateException("Unhandled Measurement sub-type");
	}
	
	private static class IntegerFormatter extends AbstractFormatter {
		private static final long serialVersionUID = -3955737227956551845L;
		private final MissingValueFormat d_format;

		public IntegerFormatter(){
			d_format = new MissingValueFormat(NumberFormat.getIntegerInstance());
		}
		@Override
		public Object stringToValue(String text) throws ParseException {
			Object value = d_format.parseObject(text);
			return value == null ? null : ((Number)value).intValue();
		}
		@Override
		public String valueToString(Object value) throws ParseException {
			return d_format.format(value);
		}
	}
	
	private static class DoubleFormatter extends AbstractFormatter {
		private static final long serialVersionUID = -3955737227956551845L;
		private final MissingValueFormat d_format;

		public DoubleFormatter(){
			d_format = new MissingValueFormat(NumberFormat.getNumberInstance());
		}
		@Override
		public Object stringToValue(String text) throws ParseException {
			Object value = d_format.parseObject(text);
			return value == null ? null : ((Number)value).doubleValue();
		}
		@Override
		public String valueToString(Object value) throws ParseException {
			return d_format.format(value);
		}
	}

	public static JFormattedTextField buildFormatted(ValueModel model, ValueModel disabledModel, AbstractFormatter formatter) {
		final JFormattedTextField field = new JFormattedTextField(new DefaultFormatterFactory(formatter, formatter, formatter, formatter));
		PropertyConnector.connectAndUpdate(model, field, "value");
		PropertyConnector.connectAndUpdate(ConverterFactory.createBooleanNegator(disabledModel), field, "enabled");
		field.setColumns(5);
		return field;
	}
}