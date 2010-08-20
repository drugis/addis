package org.drugis.addis.util;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;

@SuppressWarnings("serial")
public class PubMedListFormat extends Format {

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		if (!(obj instanceof List<?>)) {
			throw new IllegalArgumentException();
		}
		List<?> list = (List<?>) obj;
		if(list.size() >= 1) {
			toAppendTo.append(list.get(0));
			for (int i = 1; i < list.size(); i++) {
				toAppendTo.append(", ");
				toAppendTo.append(list.get(i));
			}
		}
		return toAppendTo;
	}

	private static final Pattern s_nonDigit = Pattern.compile("[^0-9]");
	private static final Pattern s_leadingZeros = Pattern.compile("^0*");

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		pos.setIndex(source.length() + 1);
		PubMedIdList list = new PubMedIdList();
		
		StringTokenizer tokenizer = new StringTokenizer(source, ",");
		while (tokenizer.hasMoreTokens()) {
			validatePubMedID(tokenizer.nextToken(), list);	
	    }
		
		return list;
	}

	private void validatePubMedID(String source, PubMedIdList list) {
		Matcher removeNonDigits = s_nonDigit.matcher(source);
		Matcher removeZeros = s_leadingZeros.matcher(removeNonDigits.replaceAll(""));
		String processed = removeZeros.replaceFirst("");
		if(processed.length() != 0) {
			list.add(new PubMedId(processed));
		}
	}

}
