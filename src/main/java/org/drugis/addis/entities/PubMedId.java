/**
 * 
 */
package org.drugis.addis.entities;

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
	
	public String toString() {
		return getId();
	}
	
	public boolean equals(Object o) {
		if (o instanceof PubMedId) {
			return ((PubMedId)o).getId().equals(this.getId());
		}
		return false;
	}
	
	private void setId(String id) {
		if (id == null || id.length() == 0) {
			throw new IllegalArgumentException();
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