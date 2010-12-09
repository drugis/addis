package org.drugis.addis.presentation.mcmc;

import org.drugis.addis.presentation.ValueHolder;
import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MCMCResultsEvent;
import org.drugis.mtc.MCMCResultsListener;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class MCMCResultsAvailableModel extends AbstractValueModel implements ValueHolder<Boolean>, MCMCResultsListener {

	private boolean d_val;

	public MCMCResultsAvailableModel(MCMCResults results) {
		d_val = results.getNumberOfSamples() > 0;
		results.addResultsListener(this);
	}

	public Boolean getValue() {
		return d_val;
	}

	public void setValue(Object newValue) {
		throw new IllegalAccessError("MCMCResultsAvailableModel is read-only");
	}

	public void resultsEvent(MCMCResultsEvent event) {
		boolean oldval = d_val;
		d_val = event.getSource().getNumberOfSamples() > 0;
		fireValueChange(oldval, d_val);
	}
}
