package org.drugis.addis.entities.treatment;

import java.util.ArrayList;


public class RangeNode implements DecisionTreeNode {
	private final Class<?> d_beanClass;
	private final String d_propertyName;
	private final double d_lowerBound;
	private final boolean d_lowerBoundIsOpen;
	private final double d_upperBound;
	private final boolean d_upperBoundIsOpen;
	private ArrayList<DecisionTreeNode> d_children = new ArrayList<DecisionTreeNode>();

	/**
	 * Construct a RangeNode that classifies objects by the given property, which must be numeric.
	 * The specified range is subdivided according to cut-off points (to be specified using {@link #addCutOff(double, boolean)}).
	 * If the to-be-classified object is not of the given type, or the property value is not in the specified range, an exception is raised.
	 * @param beanClass Class of object to be classified.
	 * @param propertyName Property to classify on.
	 * @param lowerBound Lower bound all property values should satisfy.
	 * @param lowerBoundIsOpen True if the lower bound is open (exclusive), false if it is closed (inclusive).
	 * @param upperBound Upper bound all property values should satisfy.
	 * @param upperBoundIsOpen True if the upper bound is open (exclusive), false if it is closed (inclusive).
	 * @param child The initial child node.
	 * @throws IllegalArgumentException If the child is null.
	 */
	public RangeNode(Class<?> beanClass, String propertyName,
			double lowerBound, boolean lowerBoundIsOpen,
			double upperBound, boolean upperBoundIsOpen,
			DecisionTreeNode child) {
		if (child == null) {
			throw new IllegalArgumentException("child may not be null");
		}
		
		d_beanClass = beanClass;
		d_propertyName = propertyName;
		d_lowerBound = lowerBound;
		d_lowerBoundIsOpen = lowerBoundIsOpen;
		d_upperBound = upperBound;
		d_upperBoundIsOpen = upperBoundIsOpen;
		d_children.add(child);
	}
	
	/**
	 * Add a cut-off value. This splits an existing range in two.
	 * The resulting ranges will be initialized with the child node of the original range.
	 * @param value The cut-off value.
	 * @param isOpenAsLowerBound True if the value should be included in the range
	 * where it is a lower bound, and excluded where it is an upper bound.
	 * @return The index of the range where this cut-off is a lower bound.
	 * @throws IllegalArgumentException If the value does not lie within the specified range for this node, 
	 * or if it is equal to an existing cut-off value.
	 */
	public int addCutOff(double value, boolean isOpenAsLowerBound) {
		d_children.add(getChildCount(), getChildNode(getChildCount() - 1));
		return getChildCount() - 1;
	}

	/**
	 * The number of children equals the number of sub-ranges, which is the number of cut-offs + 1.
	 * @return The number of children (sub-ranges).
	 */
	public int getChildCount() {
		return d_children.size();
	}
	
	/**
	 * Set the child node for the index-th range.
	 * @param index Index of the range.
	 * @param node Desired child node.
	 * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #getChildCount()}.
	 */
	public void setChildNode(int index, DecisionTreeNode node) {
		d_children.set(index, node);
	}
	
	/**
	 * Get the child node for the index-th range.
	 * @param index Index of the range.
	 * @return The child at that index.
	 */
	public DecisionTreeNode getChildNode(int index) {
		return d_children.get(index);
	}
	
	/**
	 * Get the lower bound of the index-th range.
	 * @param index Index of the range.
	 * @return The lower bound.
	 */
	public double getRangeLowerBound(int index) {
		return d_lowerBound;
	}
	
	/**
	 * Get whether the lower bound of the index-th range is open (exclusive).
	 * @param index Index of the range.
	 * @return True if the lower bound is open (exclusive), false if it is close (inclusive).
	 */
	public boolean isRangeLowerBoundOpen(int index) {
		return d_lowerBoundIsOpen;
	}

	/**
	 * Get the upper bound of the index-th range.
	 * @param index Index of the range.
	 * @return The upper bound.
	 */
	public double getRangeUpperBound(int index) {
		return d_upperBound;
	}
	
	/**
	 * Get whether the upper bound of the index-th range is open (exclusive).
	 * @param index Index of the range.
	 * @return True if the upper bound is open (exclusive), false if it is close (inclusive).
	 */
	public boolean isRangeUpperBoundOpen(int index) {
		return d_upperBoundIsOpen;
	}

	/**
	 * Classify the given object according to range-subdivision of the relevant property.
	 * @param object Object to classify.
	 * @return The relevant child node.
	 * @throws IllegalArugmentException if the object is not of the expected class, or if the property is
	 * not numeric, or if the property value is not within the range specified for this node.
	 */
	public DecisionTreeNode decide(Object object) {
		
		return null;
	}
	
	public boolean isLeaf() {
		return false;
	}
}
