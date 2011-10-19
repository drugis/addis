package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import org.drugis.common.EqualsUtil;

public class Unit extends AbstractNamedEntity<Unit> {
	public static final String PROPERTY_SYMBOL = "symbol";
	private String d_symbol;
	
	public Unit(String name, String symbol) {
		super(name);
		d_symbol = symbol;
	}
	
	public String getSymbol() {
		return d_symbol;
	}
	
	public void setSymbol(String symbol) {
		String oldValue = d_symbol;
		d_symbol = symbol;
		firePropertyChange(PROPERTY_SYMBOL, oldValue, d_symbol);
	}
	
	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	@Override
	public boolean deepEquals(Entity other) {
		if (!super.deepEquals(other)) {
			return false;
		}
		Unit o = (Unit) other;
		return EqualsUtil.equal(d_symbol, o.d_symbol);
	}
	
	@Override
	public String toString() {
		return getLabel();
	}
	
}
