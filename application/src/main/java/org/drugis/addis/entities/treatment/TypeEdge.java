package org.drugis.addis.entities.treatment;

public class TypeEdge implements DecisionTreeEdge {
	private final Class<?> d_type;

	public TypeEdge(Class<?> type) {
		d_type = type;	
	}

	@Override
	public boolean decide(Object object) {
		return object == d_type;
	}
}
