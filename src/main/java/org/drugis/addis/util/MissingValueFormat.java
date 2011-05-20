package org.drugis.addis.util;

import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class MissingValueFormat extends Format {
	private static final long serialVersionUID = -7318072422588445440L;
	
	private final NumberFormat d_format;
	
	public MissingValueFormat(NumberFormat format) {
		d_format = format;
	}

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		return toAppendTo.append(obj == null ? "N/A" : d_format.format(obj));
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		int cache = pos.getIndex();
		Number parse = d_format.parse(source, pos);
		if (pos.getIndex() != cache) {
			return parse;
		} else {
			pos.setIndex(source.length());
			return null;
		}
	}
}
