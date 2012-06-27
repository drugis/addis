package org.drugis.addis.util;

import java.text.DecimalFormat;

/**
 * NOTE: this class abuses {@link #setPositivePrefix(String)} and {@link #setPositiveSuffix(String)}
 * from {@link DecimalFormat}, so this class is only usable for positive numbers.
 */
@SuppressWarnings("serial")
public class AffixableFormat extends DecimalFormat {

	public void setPrefix(String prefixText) {
		setPositivePrefix(prefixText);
	}
	
	public void setSuffix(String suffixText) {
		setPositiveSuffix(suffixText);
	}
}