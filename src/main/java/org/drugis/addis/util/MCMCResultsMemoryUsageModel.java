package org.drugis.addis.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.drugis.addis.presentation.ValueHolder;
import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MCMCResultsEvent;
import org.drugis.mtc.MCMCResultsListener;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class MCMCResultsMemoryUsageModel extends AbstractValueModel implements ValueHolder<String> {

	private MCMCResults d_results;

	public MCMCResultsMemoryUsageModel(MCMCResults results) {
		d_results = results;
		d_results.addResultsListener(new MCMCResultsListener() {
			public void resultsEvent(MCMCResultsEvent event) {
				fireValueChange(null, getValue());
			}
		});
	}

	public String getValue() {
		NumberFormat format = new DecimalFormat("0.0");

		if(d_results.getNumberOfSamples() > 0) {
			if(getKiloByteSize() < 100) {
				return format.format(getKiloByteSize()) + " KB";
			}
			return format.format(getKiloByteSize() / 1000.0) + " MB";
		}
		return "0.0 KB";
	}

	private double getKiloByteSize() {
		return getByteSize() / 1000.0;
	}
	
	private long getByteSize() {
		return (long) d_results.getNumberOfSamples() * d_results.getNumberOfChains() * d_results.getParameters().length * Double.SIZE / 8;
	}

	public void setValue(Object newValue) {
		throw new IllegalAccessError("MCMCResultsMemoryUsageModel is read-only");
	}

	public void clear() {
		d_results.clear();
		fireValueChange(null, getValue());
	}

}
