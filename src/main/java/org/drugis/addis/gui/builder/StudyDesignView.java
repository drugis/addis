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
import org.drugis.addis.gui.components.EnhancedTableHeader;
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
		
		// We can't use the EnhancedTable because it doesn't play nicely with the cell renderer.
		JTable armsEpochsTable = new JTable(d_tableModel);
		
		// Set our own row height and cell renderer
		armsEpochsTable.setRowHeight(calculateHeight());
		armsEpochsTable.setDefaultRenderer(StudyActivity.class, new StudyActivityRenderer());
		
		// use our own column resizer
		armsEpochsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		EnhancedTableHeader tableHeader = new EnhancedTableHeader(armsEpochsTable.getColumnModel(), armsEpochsTable);
		tableHeader.setMaxColWidth(1000);
		armsEpochsTable.setTableHeader(tableHeader);
		tableHeader.autoSizeColumns();
		
		// disable reordering and resizing of columns
		tableHeader.setReorderingAllowed(false);
		tableHeader.setResizingAllowed(false);

		// create a scrollpane that only scrolls horizontally
		JScrollPane tableScrollPane = new JScrollPane(armsEpochsTable);
		tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		// fit the viewport to the contents
		armsEpochsTable.setPreferredScrollableViewportSize(armsEpochsTable.getPreferredSize());
		
		builder.add(tableScrollPane, cc.xy(1,1));
		return builder.getPanel();
	}

	private int calculateHeight() {
		JLabel jLabel = new JLabel("<html>Text<br>Text</html>");
		return (int) jLabel.getPreferredSize().getHeight();
	}

}
