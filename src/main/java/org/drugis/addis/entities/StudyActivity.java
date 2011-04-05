package org.drugis.addis.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class StudyActivity extends AbstractEntity implements TypeWithNotes {
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_ACTIVITY = "activity";
	public static final String PROPERTY_USED_BY = "usedBy";

	public static class UsedBy {
		private final Epoch d_epoch;
		private final Arm d_arm;
		public UsedBy(Epoch e, Arm a) {
			d_epoch = e;
			d_arm = a;
		}
		public Epoch getEpoch() {
			return d_epoch;
		}
		public Arm getArm() {
			return d_arm;
		}
	}

	private String d_name;
	private Activity d_activity;
	private List<UsedBy> d_usedBy = new ArrayList<UsedBy>();
	
	public StudyActivity(String name, Activity activity) {
		d_name = name;
		d_activity = activity;
	}


	@Override
	public Set<? extends Entity> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}


	public void setName(String name) {
		String oldValue = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldValue, d_name);
	}

	public String getName() {
		return d_name;
	}

	public void setActivity(Activity activity) {
		Activity oldValue = d_activity;
		d_activity = activity;
		firePropertyChange(PROPERTY_ACTIVITY, oldValue, d_activity);
	}

	public Activity getActivity() {
		return d_activity;
	}


	public void setUsedBy(List<UsedBy> usedBy) {
		d_usedBy = usedBy;
	}


	public List<UsedBy> getUsedBy() {
		return d_usedBy;
	}


	public List<Note> getNotes() {
		return Collections.emptyList();
	}
}
