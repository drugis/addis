/**
 * 
 */
package org.drugis.addis.gui.builder.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.pietschy.wizard.PanelWizardStep;

public class CompleteListener implements PropertyChangeListener {
	 PanelWizardStep d_curStep;
	
	 public CompleteListener(PanelWizardStep currentStep){
		 d_curStep = currentStep;
	 }
	
	 public void propertyChange(PropertyChangeEvent evt) {
	 	 d_curStep.setComplete((Boolean) evt.getNewValue());
	 }
 }