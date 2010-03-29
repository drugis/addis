package org.drugis.addis.entities;


public class ContinuousPopulationCharacteristic extends AbstractVariable implements PopulationCharacteristic {
	private static final long serialVersionUID = -1047329092617146770L;

	public ContinuousPopulationCharacteristic() {
		super("", Type.CONTINUOUS);
	}
	
	public ContinuousPopulationCharacteristic(String name) {
		super(name, Type.CONTINUOUS);
	}
}
