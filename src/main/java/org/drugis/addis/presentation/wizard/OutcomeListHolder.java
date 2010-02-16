/**
 * 
 */
package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.TypedHolder;

@SuppressWarnings("serial")
public class OutcomeListHolder extends AbstractListHolder<OutcomeMeasure> implements PropertyChangeListener {
	private TypedHolder<Indication> d_indication;
	private Domain d_domain;

	public OutcomeListHolder(TypedHolder<Indication> indication, Domain domain) {
		this.d_indication = indication;
		this.d_domain = domain;
		d_indication.addValueChangeListener(this);
	}
	
	@Override
	public List<OutcomeMeasure> getValue() {	
		return getEndpointSet();
	}
	
	private List<OutcomeMeasure> getEndpointSet() {
		TreeSet<OutcomeMeasure> endpoints = new TreeSet<OutcomeMeasure>();
		if (this.d_indication.getValue() != null) {
			for (Study s : d_domain.getStudies(this.d_indication.getValue()).getValue()) {
				endpoints.addAll(s.getOutcomeMeasures());
			}			
		}	
		return new ArrayList<OutcomeMeasure>(endpoints);
	}		

	public void propertyChange(PropertyChangeEvent event) {
		fireValueChange(null, getValue());
	}
}