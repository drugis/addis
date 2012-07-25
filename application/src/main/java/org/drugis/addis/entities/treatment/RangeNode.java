package org.drugis.addis.entities.treatment;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import org.apache.commons.math3.util.Precision;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.util.BoundedInterval;
import org.drugis.common.EqualsUtil;
import org.drugis.common.gui.GUIHelper;

import com.jgoodies.binding.beans.BeanUtils;

public class RangeNode extends DecisionTreeNode implements Comparable<RangeNode> {
	public static final double EPSILON = 1.0E-14;
	public static final String PROPERTY_INTERVAL = "interval";
	
	private final Class<?> d_beanClass;
	protected final String d_propertyName;
	private final BoundedInterval d_interval;
	
	

	public RangeNode(Class<?> beanClass, String propertyName) {
		this(beanClass, propertyName, 0, false, Double.POSITIVE_INFINITY, true);
	}
	
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
			double upperBound, boolean upperBoundIsOpen) {
		this(beanClass, propertyName, new BoundedInterval(lowerBound, lowerBoundIsOpen, upperBound, upperBoundIsOpen));
	}
	
	public RangeNode(Class<?> beanClass, String propertyName, BoundedInterval interval) {
		d_beanClass = beanClass;
		d_propertyName = propertyName;
		d_interval = interval;
	}

	/**
	 * Get the lower bound of the range.
	 * @return The lower bound.
	 */
	public double getRangeLowerBound() {
		return d_interval.getRange().getMinimumDouble();
	}
	
	/**
	 * Get whether the lower bound of the range is open (exclusive).
	 * @return True if the lower bound is open (exclusive), false if it is close (inclusive).
	 */
	public boolean isRangeLowerBoundOpen() {
		return d_interval.isLowerBoundOpen();
	}

	/**
	 * Get the upper bound
	 * @return The upper bound.
	 */
	public double getRangeUpperBound() {
		return d_interval.getRange().getMaximumDouble();
	}
	
	/**
	 * Get whether the upper bound of the range is open (exclusive).
	 * @return True if the upper bound is open (exclusive), false if it is close (inclusive).
	 */
	public boolean isRangeUpperBoundOpen() {
		return d_interval.isUpperBoundOpen();
	}

	/**
	 * Classify the given object according to range-subdivision of the relevant property.
	 * @param object Object to classify.
	 * @return The relevant child node.
	 * @throws IllegalArugmentException if the object is not of the expected class, or if the property is
	 * not numeric, or if the property value is not within the range specified for this node.
	 */
	public boolean decide(Object object) {
		try { 
			if(!getBeanClass().isInstance(object)) {
				throw new IllegalArgumentException("Object not of the valid type " + getBeanClass().getName() + "  was: " + object.getClass().getName());
			}
			PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(getBeanClass(), d_propertyName);
			try { 
				Double doseValue = (Double)BeanUtils.getValue(object, propertyDescriptor);		
				return d_interval.getRange().containsDouble(doseValue);
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("Property was not numeric. but: " + BeanUtils.getValue(object, propertyDescriptor).getClass());
			}  

		} catch (IntrospectionException e) {
			e.printStackTrace();
		} 
		throw new IllegalStateException("Could not decide the fate of " + object.toString());
	}
	
	public boolean isLeaf() {
		return false;
	}

	
	@Override
	public Class<?> getBeanClass() {
		return d_beanClass;
	}

	public BoundedInterval getInterval() {
		return d_interval;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RangeNode) {
			RangeNode other = (RangeNode) obj;
			return EqualsUtil.equal(getInterval(), other.getInterval()) 
					&& EqualsUtil.equal(getBeanClass(), other.getBeanClass())
					&& EqualsUtil.equal(d_propertyName, other.d_propertyName);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getInterval().hashCode() + 41 * d_beanClass.hashCode() + 41 * 41 * d_propertyName.hashCode();
	}
	
	@Override
	public String getPropertyName() {
		return d_propertyName;
	}
	
	public String getLabel() {
		return getLabel(null);
	}
	
	public String getLabel(DoseUnit unit) {
		String rangeText;
		if (!Double.isInfinite(getRangeUpperBound())) {
			rangeText = String.format("%.2f %s %s %s %.2f %s",
					getRangeLowerBound(),
					isRangeLowerBoundOpen() ? "\u003C" : "\u2264",
					GUIHelper.humanize(getPropertyName()),
					isRangeUpperBoundOpen() ? "\u003C" : "\u2264",
					getRangeUpperBound(),
					unit == null ? "" : unit);
		} else {
			rangeText = String.format("%s %s %.2f %s",
					GUIHelper.humanize(getPropertyName()),
					isRangeLowerBoundOpen() ? "\u003E" : "\u2265",
					getRangeLowerBound(),
					unit == null ? "" : unit);
		}		
		return rangeText;
	}

	public String getName() {
		return getLabel(null); //NOTE: the argument is arbitrary
	}
	
	@Override
	public int compareTo(RangeNode o) {
		if(o.equals(this)) {
			return 0;
		}
		boolean lowerDiff = getRangeLowerBound() != o.getRangeLowerBound();
		if(lowerDiff) { 
			return getRangeLowerBound() > o.getRangeLowerBound() ? 1 : -1;
		} else {
			return getRangeUpperBound() > o.getRangeUpperBound() ? 1 : -1;
		}
	}
	
	@Override
	public boolean similar(DecisionTreeNode other) {
		if(other instanceof RangeNode) {
			RangeNode o = (RangeNode) other; 
			return	Precision.equals(o.getRangeLowerBound(), getRangeLowerBound(), RangeNode.EPSILON * 10) && 
					o.isRangeLowerBoundOpen() == isRangeLowerBoundOpen() &&
					Precision.equals(o.getRangeUpperBound(), getRangeUpperBound(), RangeNode.EPSILON * 10) && 
					o.isRangeUpperBoundOpen() == isRangeUpperBoundOpen();
		}
		return false;
	}
}
