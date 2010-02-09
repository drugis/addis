package org.drugis.addis.gui.components;

import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.gui.MeasurementInputHelper;

@SuppressWarnings("serial")
public class MeasurementTable extends JTableWithPopupEditor {
	public MeasurementTable(TableModel tableModel, Window parent) {
		super(tableModel, parent);

		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
		TableColumn column = null;
		for (int i = 0; i < getModel().getColumnCount(); i++) {
		    column = getColumnModel().getColumn(i);
		        column.setPreferredWidth(50);
		}
	}

	@Override
	protected JPanel createEditorPanel(int row, int col) {
		if (col < 1) {
			return null;
		}
		
		return createPanel((BasicMeasurement)getModel().getValueAt(row, col));
	}

	private JPanel createPanel(BasicMeasurement m) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		String[] h = MeasurementInputHelper.getHeaders(m);
		JComponent[] c = MeasurementInputHelper.getComponents(m);
		for (int i = 0; i < h.length; ++i) {
			panel.add(new JLabel(h[i]));
			panel.add(c[i]);
		}

		return panel;
	}
}