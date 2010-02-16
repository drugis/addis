/**
 * 
 */
package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drugis.addis.presentation.TypedHolder;

@SuppressWarnings("unchecked")	
public class SetEmptyListener implements PropertyChangeListener {
	private List<TypedHolder> holders;
	
	public SetEmptyListener(TypedHolder h) {
		holders = new ArrayList<TypedHolder>();
		holders.add(h);
	}
	
	public SetEmptyListener(TypedHolder[] holders) {
		this.holders = Arrays.asList(holders);
	}
	
	public void propertyChange(PropertyChangeEvent arg0) {
		for (TypedHolder h : holders) {
			h.setValue(null);
		}
	}
}