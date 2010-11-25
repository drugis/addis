package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.drugis.mtc.summary.Summary;

@SuppressWarnings("serial")
public class AllSummariesDefinedModel extends UnmodifiableHolder<Boolean> implements PropertyChangeListener {

	private List<? extends Summary> d_summaries;
	boolean d_oldVal;

	public AllSummariesDefinedModel(List<? extends Summary> summaries) {
		super(evaluate(summaries));
		d_summaries = summaries;
		d_oldVal = evaluate(d_summaries);
		for(Summary s: d_summaries) {
			s.addPropertyChangeListener(this);
		}
	}

	private static boolean evaluate(List<? extends Summary> summaries) {
		for(Summary s : summaries) {
			if(!s.getDefined()) {
				return false;
			}
		}
		return true;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		firePropertyChange("value", d_oldVal, evaluate(d_summaries));
		d_oldVal = evaluate(d_summaries);
	}

	@Override
	public Boolean getValue() {
		return evaluate(d_summaries);
	}
	
}
