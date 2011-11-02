/**
 * 
 */
package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import org.drugis.common.EqualsUtil;

public class MeasurementKey extends AbstractEntity implements Entity, Comparable<MeasurementKey> {

	final Variable d_variable;
	final Arm d_arm;
	private final WhenTaken d_wt;

	public MeasurementKey(Variable v, Arm a, WhenTaken wt) {
		if (v == null) {
			throw new NullPointerException("Variable may not be null");
		}
		if (v instanceof OutcomeMeasure && a == null) {
			throw new NullPointerException(
					"Arm may not be null for Endpoints/ADEs");
		}
		if (wt == null) {
			throw new NullPointerException("Moment of measurement may not be null");
		}
		d_variable = v;
		d_arm = a;
		d_wt = wt;
	}

	public MeasurementKey(StudyOutcomeMeasure<? extends Variable> som, Arm a, WhenTaken wt) {
		this(som.getValue(), a, wt);
	}

	public Variable getVariable() {
		return d_variable;
	}

	public Arm getArm() {
		return d_arm;
	}

	public WhenTaken getWhenTaken() {
		return d_wt;
	}

	@Override
	public String toString() {
		return "<" + d_variable + ", " + d_arm + " at " + d_wt + ">";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MeasurementKey) {
			MeasurementKey other = (MeasurementKey) o;
			return d_variable.equals(other.d_variable)
					&& EqualsUtil.equal(d_arm, other.d_arm)
					&& EqualsUtil.equal(d_wt, other.d_wt);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int code = 1;
		code = code * 31 + d_variable.hashCode();
		code = code * 31 + (d_arm == null ? 0 : d_arm.hashCode());
		code = code * 31 + (d_wt == null ? 0 : d_wt.hashCode());
		return code;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}

	@Override
	public int compareTo(MeasurementKey o) {
		if (d_variable.compareTo(o.d_variable) == 0) {
			if (d_arm != null) {
				if (d_arm.compareTo(o.d_arm) == 0) {
					return d_wt.compareTo(o.d_wt);
				}
				return d_arm.compareTo(o.d_arm);
			} else if (o.d_arm == null) {
				return d_wt.compareTo(o.d_wt);
			} else {
				return -1;
			}
		}
		return d_variable.compareTo(o.d_variable);
	}
}