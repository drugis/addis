/**
 * 
 */
package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drugis.addis.presentation.ModifiableHolder;

@SuppressWarnings("unchecked")	
public class SetEmptyListener implements PropertyChangeListener {
	private List<ModifiableHolder> holders;
	
	public SetEmptyListener(ModifiableHolder h) {
		holders = new ArrayList<ModifiableHolder>();
		holders.add(h);
	}
	
	public SetEmptyListener(ModifiableHolder[] holders) {
		this.holders = Arrays.asList(holders);
	}
	
	public void propertyChange(PropertyChangeEvent arg0) {
		for (ModifiableHolder h : holders) {
			h.setValue(null);
		}
	}
}