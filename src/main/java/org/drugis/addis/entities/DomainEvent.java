package org.drugis.addis.entities;


public class DomainEvent {

	private Type d_type;
	
	public enum Type {
		STUDIES,
		DRUGS,
		ENDPOINTS,
		INDICATIONS,
		ANALYSES,
		VARIABLES
	}

	public DomainEvent(Type type) {
		d_type = type;
	} 
	
	public Type getType() {
		return d_type;
	}	
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof DomainEvent)) {
			return false;
		}
		DomainEvent dom = (DomainEvent) other;
		return dom.getType().equals(getType());
	}
}
