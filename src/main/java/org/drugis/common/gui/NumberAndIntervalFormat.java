package org.drugis.common.gui;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import org.drugis.common.Interval;

@SuppressWarnings("serial")
public class NumberAndIntervalFormat extends Format {

	private static final DecimalFormat s_fmt = new DecimalFormat("#0.00");;

	@SuppressWarnings("unchecked")
	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo,
			FieldPosition pos) {
		if (obj instanceof Interval) {
			return toAppendTo.append(format((Interval<Double>)obj));
		} else if (obj instanceof Double) {
			return toAppendTo.append(format((Double)obj));
		} else {
			return toAppendTo.append(obj.toString());
		}
	}
	
	public String format(Interval<Double> interval) {
		return "[" + s_fmt.format(interval.getLowerBound()) + " - " + s_fmt.format(interval.getUpperBound()) + "]";
	}
	
	public String format(Double x) {
		return s_fmt.format(x);
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		return null; // NI
	}
}