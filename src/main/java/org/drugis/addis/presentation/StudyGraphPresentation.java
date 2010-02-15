package org.drugis.addis.presentation;

import java.util.List;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;

public class StudyGraphPresentation {
	private ValueHolder<Indication> d_indication;
	private ValueHolder<Endpoint> d_endpoint;
	private Domain d_domain;
	private ListHolder<Drug> d_drugs;

	public StudyGraphPresentation(ValueHolder<Indication> indication, ValueHolder<Endpoint> endpoint, 
			ListHolder<Drug> drugs, Domain domain) {
		d_indication = indication;
		d_endpoint = endpoint;
		d_drugs = drugs;
		d_domain = domain;
	}
	
	/**
	 * Return the list of drugs that are included in at least one of the studies having the correct indication
	 * and endpoint.
	 */
	public List<Drug> getDrugs() {
		return d_drugs.getValue();
	}
	
	/**
	 * Return the studies with the correct indication and endpoint that compare the given drugs.
	 */
	public List<Study> getStudies(Drug a, Drug b) {
		List<Study> studies = getStudies(a);
		studies.retainAll(d_domain.getStudies(b).getValue());
		return studies;
	}
	
	/**
	 * Return the studies with the correct indication and endpoint that include the given drug.
	 */
	public List<Study> getStudies(Drug a) {
		List<Study> studies = d_domain.getStudies(a).getValue();
		studies.retainAll(d_domain.getStudies(getIndication()).getValue());
		studies.retainAll(d_domain.getStudies(getEndpoint()).getValue());
		return studies;
	}

	private OutcomeMeasure getEndpoint() {
		return d_endpoint.getValue();
	}

	private Indication getIndication() {
		return d_indication.getValue();
	}
}
