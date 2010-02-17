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
public class SelectEndpointWizardStep extends PanelWizardStep {
	public SelectEndpointWizardStep(AbstractMetaAnalysisWizardPM<?> pm) {
		super("Select Endpoint","Select an Endpoint that you want to use for this meta analysis.");
		JComboBox endPointBox = AuxComponentFactory.createBoundComboBox(pm.getOutcomeMeasureListModel(), pm.getEndpointModel());
		add(endPointBox);
		pm.getEndpointModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				setComplete(evt.getNewValue() != null);
			}
		});
	}
}