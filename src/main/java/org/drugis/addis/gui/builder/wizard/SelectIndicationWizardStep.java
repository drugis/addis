/**
 * 
 */
package org.drugis.addis.gui.builder.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;

import org.drugis.addis.presentation.wizard.AbstractMetaAnalysisWizardPM;
import org.drugis.common.gui.AuxComponentFactory;
import org.pietschy.wizard.PanelWizardStep;

@SuppressWarnings("serial")
public class SelectIndicationWizardStep extends PanelWizardStep {
	public SelectIndicationWizardStep(AbstractMetaAnalysisWizardPM<?> pm) {
		super("Select Indication","Select an Indication that you want to use for this meta analysis.");
		JComboBox indBox = AuxComponentFactory.createBoundComboBox(pm.getIndicationListModel(), pm.getIndicationModel());
		add(indBox);
		pm.getIndicationModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				setComplete(evt.getNewValue() != null);
			}
		});
	}
}