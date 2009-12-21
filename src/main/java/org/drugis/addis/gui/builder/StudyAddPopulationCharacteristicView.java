package org.drugis.addis.gui.builder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.gui.MeasurementInputHelper;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.presentation.StudyAddPopulationCharacteristicPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyAddPopulationCharacteristicView implements ViewBuilder {

	private StudyAddPopulationCharacteristicPresentation d_pm;
	private NotEmptyValidator d_validator;

	public StudyAddPopulationCharacteristicView(
			StudyAddPopulationCharacteristicPresentation pm, NotEmptyValidator validator) {
		d_pm = pm;
		d_validator = validator;
	}

	public JComponent buildPanel() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout( 
				"left:pref, 3dlu, left:pref:grow",
				"p, 3dlu, p" 
				);
		
		PanelBuilder builder = new PanelBuilder(layout);
		
		JComboBox chooserBox = AuxComponentFactory.createBoundComboBox(d_pm.getVariableList().toArray(), d_pm.getVariableModel());
		d_validator.add(chooserBox);
		builder.add(chooserBox,
				cc.xy(3, 1));
		
		final JPanel measurementPanel = new JPanel();
		measurementPanel.add(new JLabel("Select a variable to input"));
		builder.add(measurementPanel, cc.xyw(1, 3, 3));
		
		d_pm.getMeasurementModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				measurementPanel.removeAll();
				if (evt.getNewValue() != null) {
					measurementPanel.add(buildMeasurementPanel((Measurement)evt.getNewValue()));
					measurementPanel.revalidate();
				}
			}

		});
		
		return builder.getPanel();
	}
	
	private JComponent buildMeasurementPanel(Measurement m) {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout( 
				"pref",
				"p, 3dlu, p" 
				);
		
		PanelBuilder builder = new PanelBuilder(layout);
		
		for (int i = 1; i < MeasurementInputHelper.numComponents(m); ++i) {
			LayoutUtil.addColumn(layout, "pref:grow");
		}
		
		int col = 1;
		for (String header : MeasurementInputHelper.getHeaders(m)) {
			builder.addLabel(header, cc.xy(col, 1));
			col += 2;
		}
		
		col = 1;
		for (JComponent comp : MeasurementInputHelper.getComponents((BasicMeasurement)m)) {
			d_validator.add(comp);
			builder.add(comp, cc.xy(col, 3));
			col += 2;
		}
		
		return builder.getPanel();
	}
}
