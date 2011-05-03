package org.drugis.addis.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.util.EntityUtil;
import org.drugis.common.EqualsUtil;

public class StudyActivity extends AbstractNamedEntity<StudyActivity> implements TypeWithNotes {
	public static final String PROPERTY_ACTIVITY = "activity";
	public static final String PROPERTY_USED_BY = "usedBy";

	public static class UsedBy implements Comparable<UsedBy> {
		private final Epoch d_epoch;
		private final Arm d_arm;
		public UsedBy(Arm a, Epoch e) {
			assert(e != null);
			assert(a != null);
			d_epoch = e;
			d_arm = a;
		}
		public Epoch getEpoch() {
			return d_epoch;
		}
		public Arm getArm() {
			return d_arm;
		}
		
		
		@Override
		public boolean equals(Object obj) {
			if (obj != null && obj instanceof UsedBy) {
				UsedBy other = (UsedBy) obj;
				return EqualsUtil.equal(other.getEpoch(), getEpoch()) && EqualsUtil.equal(other.getArm(), getArm());
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return 31 * d_epoch.hashCode() + d_arm.hashCode();
		}
		public int compareTo(UsedBy o) {
			int armsComp = getArm().getName().compareTo(o.getArm().getName());
			if(armsComp != 0) {
				return armsComp;
			} else {
				return getEpoch().getName().compareTo(o.getEpoch().getName());
			}
		}
	}

	private Activity d_activity;
	private Set<UsedBy> d_usedBy = new HashSet<UsedBy>();
	private List<Note> d_notes = new ArrayList<Note>();
	
	public StudyActivity(String name, Activity activity) {
		super(name);
		d_activity = activity;
	}


	@Override
	public Set<? extends Entity> getDependencies() {
		return d_activity.getDependencies();
	}

	public void setActivity(Activity activity) {
		Activity oldValue = d_activity;
		d_activity = activity;
		firePropertyChange(PROPERTY_ACTIVITY, oldValue, d_activity);
	}

	public Activity getActivity() {
		return d_activity;
	}

	/**
	 * Set the set of (arm, epoch) pairs that use this StudyActivity.
	 * @param usedBy Set of (arm, epoch) pairs; defensively copied.
	 */
	public void setUsedBy(Set<UsedBy> usedBy) {
		Set<UsedBy> oldValue = d_usedBy;
		d_usedBy = new HashSet<UsedBy>(usedBy);
		firePropertyChange(PROPERTY_USED_BY, oldValue, d_usedBy);
	}

	/**
	 * Get the set of (arm, epoch) pairs that use this StudyActivity.
	 * @return unmodifiable set.
	 */
	public Set<UsedBy> getUsedBy() {
		return Collections.unmodifiableSet(d_usedBy);
	}

	public List<Note> getNotes() {
		return d_notes ;
	}
	
	public boolean deepEquals(Entity obj) {
		if(!equals(obj)) return false;
		StudyActivity other = (StudyActivity) obj;
		return EntityUtil.deepEqual(other.getActivity(), getActivity()) && other.getUsedBy().equals(getUsedBy()) && 
			EntityUtil.deepEqual(other.getNotes(), getNotes());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StudyActivity) {
			return super.equals(obj);
		}
		return false;
	}
	
	@Override
	protected StudyActivity clone() {
		StudyActivity cloned = new StudyActivity(getName(), d_activity);
		cloned.setUsedBy(new HashSet<UsedBy>(getUsedBy()));
		return cloned;
	}

}
