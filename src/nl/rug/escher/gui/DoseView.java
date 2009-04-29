package nl.rug.escher.gui;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.text.DefaultFormatter;

import nl.rug.escher.entities.Dose;
import nl.rug.escher.entities.SIUnit;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.SelectionInList;

public class DoseView implements ViewBuilder {
	PresentationModel<Dose> d_model;
	private JFormattedTextField d_quantity;
	private JComboBox d_unit;
	
	public DoseView(PresentationModel<Dose> dose) {
		d_model = dose;
	}
	
	public void initComponents() {
		d_quantity = new JFormattedTextField(new DefaultFormatter());
		PropertyConnector.connectAndUpdate(d_model.getModel(Dose.PROPERTY_QUANTITY), d_quantity, "value");
		d_quantity.setColumns(8);
		
		SelectionInList<SIUnit> unitSelectionInList = new SelectionInList<SIUnit>(
				SIUnit.values(),
				d_model.getModel(Dose.PROPERTY_UNIT));
		d_unit = BasicComponentFactory.createComboBox(unitSelectionInList);
	}

	public JComponent buildPanel() {
		initComponents();
		JPanel panel = new JPanel();
		panel.add(d_quantity);
		panel.add(d_unit);
		return panel;
	}
}
