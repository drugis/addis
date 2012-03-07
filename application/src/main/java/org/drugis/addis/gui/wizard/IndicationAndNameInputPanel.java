package org.drugis.addis.gui.wizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.presentation.wizard.AnalysisWizardPresentation;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class IndicationAndNameInputPanel {


	private static boolean stepComplete(AnalysisWizardPresentation pm) {
		return pm.getIndicationModel().getValue() != null && pm.getNameValidModel().getValue();
	}

	private static void updateBorder(final JTextField nameInput, final Border border, AnalysisWizardPresentation pm) {
		if (!pm.getNameValidModel().getValue()){
			nameInput.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
			if (pm.getNameModel().getValue() == null || pm.getNameModel().getValue().isEmpty()) {
				nameInput.setToolTipText("An analysis must have a name, please provide one.");
			} else {
				nameInput.setToolTipText("An analysis with that name already exists, please change it.");
			}
			nameInput.dispatchEvent(new KeyEvent(nameInput, KeyEvent.KEY_PRESSED, 0, KeyEvent.CTRL_MASK, KeyEvent.VK_F1, KeyEvent.CHAR_UNDEFINED));
		} else {
			nameInput.setToolTipText("");
			nameInput.setBorder(border);
		}
	}
	
	public static Component create(final PanelWizardStep panelWizardStep, final AnalysisWizardPresentation pm) {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref:grow",
				"p, 3dlu, p"
			);	
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
	
		
		final JTextField nameInput = BasicComponentFactory.createTextField(pm.getNameModel(), false);
		nameInput.setColumns(20);
		final Border border = nameInput.getBorder();
		builder.add(new JLabel("Name : "), cc.xy(1, 1));
		builder.add(nameInput, cc.xy(3, 1));
		pm.getNameValidModel().addValueChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateBorder(nameInput, border, pm);
				panelWizardStep.setComplete(stepComplete(pm));
			}
		});
	
		JComboBox indBox = AuxComponentFactory.createBoundComboBox(pm.getIndicationsModel(), pm.getIndicationModel(), true);
		builder.add(new JLabel("Indication : "), cc.xy(1, 3));
		builder.add(indBox, cc.xy(3, 3));
		pm.getIndicationModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				panelWizardStep.setComplete(stepComplete(pm));
			}
		});
		JPanel panel = builder.getPanel();
		return panel;
	}

}
