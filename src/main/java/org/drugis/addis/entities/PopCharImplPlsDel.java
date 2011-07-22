package org.drugis.addis.entities;


public abstract class PopCharImplPlsDel extends AbstractVariable implements PopulationCharacteristic {

	public PopCharImplPlsDel(String name, Type type) {
		super(name, type);
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof PopulationCharacteristic) {
			return super.equals(o);			
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 37;
	}
}