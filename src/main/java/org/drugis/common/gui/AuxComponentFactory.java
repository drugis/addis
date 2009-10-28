package org.drugis.common.gui;

import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.presentation.CharacteristicHolder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.util.DefaultUnitConverter;

public class AuxComponentFactory {

	public static <T> JComboBox createBoundComboBox(T[] values, ValueModel model) {
		SelectionInList<T> typeSelectionInList =
			new SelectionInList<T>(values, model);
		JComboBox type = BasicComponentFactory.createComboBox(typeSelectionInList);
		return type;
	}
	
	public static <T> JComboBox createBoundComboBox(ValueModel listHolder, ValueModel model) {
		SelectionInList<T> typeSelectionInList =
			new SelectionInList<T>(listHolder, model);
		JComboBox type = BasicComponentFactory.createComboBox(typeSelectionInList);
		return type;
	}

	public static JScrollPane createTextArea(ValueModel model, boolean editable) {
		JTextArea area = BasicComponentFactory.createTextArea(model);
		area.setEditable(editable);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		JScrollPane pane = new JScrollPane(area);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.setPreferredSize(new Dimension(
				DefaultUnitConverter.getInstance().dialogUnitXAsPixel(200, area), 
				DefaultUnitConverter.getInstance().dialogUnitYAsPixel(50, area)));
		return pane;
	}

	public static JComponent createCharacteristicView(CharacteristicHolder model) {
		JComponent component = null;
		if (model.getCharacteristic().getValueType().equals(StudyCharacteristic.ValueType.TEXT)) {
			component = createTextArea(model, false);
		} else {
			component = BasicComponentFactory.createLabel(model, new OneWayObjectFormat());
		}
		return component;
	}	
}
