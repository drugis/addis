/**
 * 
 */
package org.drugis.addis.entities;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.common.EqualsUtil;
import org.drugis.common.beans.ContentAwareListModel;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class StudyOutcomeMeasure<T extends Variable> extends ObjectWithNotes<T> {
	public static final String PROPERTY_IS_PRIMARY = "isPrimary";
	public static final String PROPERTY_WHEN_TAKEN_EDITED = "whenTakenEdited";

	private Boolean d_isPrimary = false;
	private ObservableList<WhenTaken> d_whenTaken = new ArrayListModel<WhenTaken>();
	final private ContentAwareListModel<WhenTaken> d_whenTakenMonitor = new ContentAwareListModel<WhenTaken>(d_whenTaken);

	public StudyOutcomeMeasure(T obj) {
		super(obj);
		d_whenTakenMonitor.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
			}
			public void intervalAdded(ListDataEvent e) {
			}
			public void contentsChanged(ListDataEvent e) {
				firePropertyChange(PROPERTY_WHEN_TAKEN_EDITED, false, true);
			}
		});
	}

	public StudyOutcomeMeasure(T obj, WhenTaken whenTaken) {
		this(obj);
		if (whenTaken != null) {
			d_whenTaken.add(whenTaken);
		}
	}

	@Override
	public StudyOutcomeMeasure<T> clone() {
		StudyOutcomeMeasure<T> clone = new StudyOutcomeMeasure<T>(getValue());
		clone.setIsPrimary(getIsPrimary());
		clone.getNotes().addAll(getNotes());
		for (WhenTaken wt : getWhenTaken()) {
			clone.getWhenTaken().add(wt.clone());
		}
		return clone;
	}

	public Boolean getIsPrimary() {
		return d_isPrimary;
	}
	
	public void setIsPrimary(Boolean isPrimary) {
		Boolean oldValue = new Boolean(d_isPrimary);
		d_isPrimary = isPrimary;
		firePropertyChange(PROPERTY_IS_PRIMARY, oldValue, d_isPrimary);
	}

	public ObservableList<WhenTaken> getWhenTaken() {
		return d_whenTaken;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof StudyOutcomeMeasure<?>) {
			StudyOutcomeMeasure<?> other = (StudyOutcomeMeasure<?>) o;
			return  EqualsUtil.equal(getValue(), other.getValue());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return d_isPrimary ? "primary measure: " : "secondary measure: " + getValue().getName() + " " + d_whenTaken + " " + getNotes();
	}
}