/**
 * 
 */
package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;

@SuppressWarnings("serial")
public class DomainStudyListHolder extends AbstractListHolder<Study> {
	private final ValueHolder<OutcomeMeasure> d_outcome;
	private final ValueHolder<Indication> d_indication;
	private final Domain d_domain;

	public DomainStudyListHolder(Domain domain,
			ValueHolder<Indication> indication, 
			ValueHolder<OutcomeMeasure> outcome) {
		d_domain = domain;
		d_indication = indication;
		d_outcome = outcome;
		
		d_domain.addListener(new DomainListener() {
			public void domainChanged(DomainEvent evt) {
				if (evt.getType().equals(DomainEvent.Type.STUDIES)) {
					fireValueChange(null, getValue());
				}
			}
		});
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fireValueChange(null, getValue());
			}
		};
		d_indication.addValueChangeListener(listener);
		d_outcome.addValueChangeListener(listener);
	}

	@Override
	public List<Study> getValue() {
		if (d_indication.getValue() == null || d_outcome.getValue() == null)
			return Collections.emptyList();
		List<Study> studies = d_domain.getStudies(d_indication.getValue()).getValue();
		studies.retainAll(d_domain.getStudies(d_outcome.getValue()).getValue());
		return studies;
	}
}