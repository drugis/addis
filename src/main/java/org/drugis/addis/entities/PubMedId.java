/**
 * 
 */
package org.drugis.addis.entities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class PubMedId {
	private String d_id;

	private PubMedId() {
	}
	
	public PubMedId(String id) {
		setId(id);
	}

	private String getId() {
		return d_id;
	}
	
	@Override
	public String toString() {
		return getId();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PubMedId) {
			return ((PubMedId)o).getId().equals(this.getId());
		}
		return false;
	}
	
	private static final Pattern s_onlyDigits = Pattern.compile("^[1-9][0-9]*$");
	
	private void setId(String id) {
		if (id == null || id.length() == 0) { // FIXME: more validation should be done
			throw new IllegalArgumentException("PubMedId may not be null or empty.");
		}
		Matcher matcher = s_onlyDigits.matcher(id);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Only digits are valid in a PubMedId, and it may not start with 0.");
		}
		d_id = id;
	}

	protected static final XMLFormat<PubMedId> XML = new XMLFormat<PubMedId>(PubMedId.class) {
		@Override
		public PubMedId newInstance(java.lang.Class<PubMedId> cls, XMLFormat.InputElement ie) throws XMLStreamException {
			return new PubMedId();
		};

		@Override
		public void read(javolution.xml.XMLFormat.InputElement ie,
				PubMedId obj) throws XMLStreamException {
			obj.setId(ie.getAttribute("value").toString());
		}

		@Override
		public void write(PubMedId obj,
				javolution.xml.XMLFormat.OutputElement oe)
				throws XMLStreamException {
			oe.setAttribute("value", obj.getId());				
		}

		@Override
		public boolean isReferenceable() {
			return false;
		}
	};
}