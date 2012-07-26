package org.drugis.addis.entities.treatment;

import org.drugis.common.gui.GUIHelper;

public class TypeEdge implements DecisionTreeEdge {
	private final Class<?> d_type;

	public TypeEdge(final Class<?> type) {
		d_type = type;
	}

	@Override
	public boolean decide(final Object object) {
		return object == d_type;
	}

	@Override
	public String toString() {
		return GUIHelper.humanize(d_type.getSimpleName().replace("Dose", ""));
	}
}
