package org.drugis.addis.gui.builder;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.wizard.StudyActivitiesTableModel;
import org.drugis.addis.presentation.StudyPresentation;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyDesignView implements ViewBuilder {

	private class StudyActivityRenderer extends JLabel implements TableCellRenderer {
		private static final long serialVersionUID = -3963454510182436593L;
		
		private int d_maxHeight;
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			TableColumnModel colModel = table.getColumnModel();
			setSize(colModel.getColumn(column).getWidth(), 0);
			d_maxHeight = Math.max((int) getPreferredSize().getHeight(), d_maxHeight);
			if (value instanceof StudyActivity) {
				StudyActivity sa = (StudyActivity) value;
				if (sa.getActivity() instanceof TreatmentActivity) {
					TreatmentActivity ta = (TreatmentActivity) sa.getActivity();
					setText("<html>" + sa.getName() + "<br/>" + ta.getDrug().getName() + " (" + ta.getDose().toString() + ")</html>");
				} else {
					setText("<html>" + sa.getActivity().toString() + "</html>");
				}
			} else {
				setText(value == null ? "" : value.toString());
			}
			return this;
		}
		
	}
	
	private TableModel d_tableModel;

	public StudyDesignView(StudyPresentation spm) {

		d_tableModel = new StudyActivitiesTableModel(spm.getBean());
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"fill:0:grow", 
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		final EnhancedTable armsEpochsTable = new EnhancedTable(d_tableModel, 100000);
		
		armsEpochsTable.setRowHeight(calculateHeight());

		armsEpochsTable.getTableHeader().setReorderingAllowed(false);
		armsEpochsTable.getTableHeader().setResizingAllowed(false);
		armsEpochsTable.setDefaultRenderer(StudyActivity.class, new StudyActivityRenderer());
		armsEpochsTable.autoSizeColumns();
		armsEpochsTable.setPreferredScrollableViewportSize(armsEpochsTable.getPreferredSize());
		
		JScrollPane tableScrollPane = new JScrollPane(armsEpochsTable);
		builder.add(tableScrollPane, cc.xy(1,1));
		return builder.getPanel();
	}

	private int calculateHeight() {
		JLabel jLabel = new JLabel("<html>Text<br>Text</html>");
		return (int) jLabel.getPreferredSize().getHeight();
	}

}
