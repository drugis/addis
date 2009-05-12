package nl.rug.escher.common.gui;

import javax.swing.JComboBox;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.AbstractValueModel;

public class AuxComponentFactory {

	public static <T> JComboBox createBoundComboBox(T[] values, AbstractValueModel model) {
		SelectionInList<T> typeSelectionInList =
			new SelectionInList<T>(values, model);
		JComboBox type = BasicComponentFactory.createComboBox(typeSelectionInList);
		return type;
	}

}
