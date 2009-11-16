package org.drugis.common.gui;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

@SuppressWarnings("serial")
public class DayDateFormat extends Format {
	SimpleDateFormat d_format;
	public DayDateFormat() {
		d_format = new SimpleDateFormat("dd MMM yyyy");
		d_format.setCalendar(new GregorianCalendar());
	}
	
	@Override
	public StringBuffer format(Object date, StringBuffer toAppendTo, FieldPosition pos) {
		if (date == null) {
			return toAppendTo.append("");
		} 
		return toAppendTo.append(d_format.format(date));
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		throw new RuntimeException("not implemented");
	}
}
