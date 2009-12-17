package org.drugis.addis.entities;

public interface Population {

	public VariableMap getPopulationCharacteristics();

	/**
	 * Gets the characteristic.
	 * 
	 * @param c
	 * @return A characteristic, or null if its not set.
	 */
	public Measurement getPopulationCharacteristic(Variable v);

}