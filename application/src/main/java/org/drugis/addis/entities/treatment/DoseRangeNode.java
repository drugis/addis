package org.drugis.addis.entities.treatment;

import org.drugis.addis.entities.DoseUnit;

public class DoseRangeNode extends RangeNode {

	private final DoseUnit d_doseUnit;

	public DoseRangeNode(Class<?> beanClass, String propertyName,
			double lowerBound, 
			boolean lowerBoundIsOpen, 
			double upperBound,
			boolean upperBoundIsOpen, 
			DoseUnit doseUnit,
			DecisionTreeNode child) {
		super(beanClass, propertyName, lowerBound, lowerBoundIsOpen, upperBound,
				upperBoundIsOpen, child);
		d_doseUnit = doseUnit;
	}
	
	@Override
	public DecisionTreeNode decide(Object object) {
		return null;
	}

}
