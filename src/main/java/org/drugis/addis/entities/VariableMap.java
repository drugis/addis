package org.drugis.addis.entities;

import java.util.Set;

public class VariableMap extends MapBean<Variable, Measurement> {
	private static final long serialVersionUID = 1752332147189407475L;

	@Override
	public Set<Variable> getDependencies() {
		return keySet();
	}

}
