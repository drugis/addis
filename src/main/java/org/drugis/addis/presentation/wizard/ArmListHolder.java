/**
 * 
 */
package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.AbstractListHolder;

@SuppressWarnings("serial")
public class ArmListHolder extends AbstractListHolder<Arm> implements PropertyChangeListener {
	Study d_study;
	Drug d_drug;
	
	public ArmListHolder(Study s, Drug d) {
		d_study = s;
		d_drug = d;
		
		d_study.addPropertyChangeListener(this);
		d_drug.addPropertyChangeListener(this);
	}

	@Override
	public List<Arm> getValue() {
		// get arms per study per drug
		ArrayList<Arm> armList = new ArrayList<Arm>();
		for (Arm curArm : d_study.getArms()) {
			if (curArm.getDrug().equals(d_drug)) {
				armList.add(curArm);
			}
		}
		return armList;
	}
	
	public void propertyChange(PropertyChangeEvent arg0) {
		fireValueChange(null,getValue());			
	}
}