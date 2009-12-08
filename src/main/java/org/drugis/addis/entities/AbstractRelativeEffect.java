package org.drugis.addis.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class AbstractRelativeEffect<T extends Measurement> extends AbstractEntity implements RelativeEffect<T>{

	private static final long serialVersionUID = 1863294156273299358L;
	protected T d_subject;
	protected T d_baseline; 

	protected AbstractRelativeEffect(T subject, T baseline) {
		if (!subject.getEndpoint().equals(baseline.getEndpoint())) {
			throw new IllegalArgumentException();
		}
		
		d_subject = subject;
		d_baseline = baseline;
		connectListeners();
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		connectListeners();
	}	
	
	private void connectListeners() {
		MemberListener listener = new MemberListener();
		getSubject().getPatientGroup().addPropertyChangeListener(listener);
		getSubject().addPropertyChangeListener(listener);
		getBaseline().getPatientGroup().addPropertyChangeListener(listener);
		getBaseline().addPropertyChangeListener(listener);
	}

	public Integer getSampleSize() {
		return getSubject().getPatientGroup().getSize() + getBaseline().getPatientGroup().getSize();
	}
	
	public T getSubject() {
		return d_subject;
	}

	public T getBaseline() {
		return d_baseline;
	}

	private class MemberListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange(PROPERTY_SAMPLESIZE, null, getSampleSize());
		}		
	}
}