package org.drugis.common.gui;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class LayoutUtil {
	public static void addColumn(FormLayout layout) {
		addColumn(layout, "left:pref");
	}

	public static void addColumn(FormLayout layout, String spec) {
		layout.appendColumn(ColumnSpec.decode("3dlu"));
		layout.appendColumn(ColumnSpec.decode(spec));
	}

	public static void addRow(FormLayout layout) {
		layout.appendRow(RowSpec.decode("3dlu"));
		layout.appendRow(RowSpec.decode("p"));
	}
}
