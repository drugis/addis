package org.drugis.addis.entities;

import javax.xml.datatype.Duration;

import com.jgoodies.binding.beans.Observable;

public interface TypeWithDuration extends Observable{
	
	public Duration getDuration();
	public void setDuration(Duration duration);
	
}
