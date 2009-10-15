package org.drugis.common.gui;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class OneWayObjectFormat extends Format {
	private static final long serialVersionUID = 6273074987898968131L;

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo,
			FieldPosition pos) {
		return toAppendTo.append(obj);
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		return null; // NI
	}

}
