package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.drugis.mtc.summary.Summary;

@SuppressWarnings("serial")
public class AllSummariesDefinedModel extends UnmodifiableHolder<Boolean> implements PropertyChangeListener {

	public AllSummariesDefinedModel(List<? extends Summary> summaries) {
		super(evaluate(summaries));
		
		for (Summary s : summaries) {
			fireValueChange(false, s.PROPERTY_DEFINED);
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
		// TODO Auto-generated method stub
		
	}

}
