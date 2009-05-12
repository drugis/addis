package nl.rug.escher.common.gui;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class LayoutUtil {

	public static void addColumn(FormLayout layout) {
		layout.appendColumn(ColumnSpec.decode("3dlu"));
		layout.appendColumn(ColumnSpec.decode("pref"));
	}

	public static void addRow(FormLayout layout) {
		layout.appendRow(RowSpec.decode("3dlu"));
		layout.appendRow(RowSpec.decode("p"));
	}

}
