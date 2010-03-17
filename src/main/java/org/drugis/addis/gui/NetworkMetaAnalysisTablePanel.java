package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.presentation.LabeledPresentationModel;
import org.drugis.addis.presentation.NetworkTableModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;



@SuppressWarnings("serial")
public class NetworkMetaAnalysisTablePanel extends AbstractTablePanel{
	
	public NetworkMetaAnalysisTablePanel(JFrame parent, NetworkTableModel networkAnalysisTableModel) {
		super(networkAnalysisTableModel);
	}

	@Override
	public void setRenderer() {
		d_table.setDefaultRenderer(Object.class, new NetworkTableCellRenderer());
	}
	
	@Override
	protected void initComps() {
		JLabel description = new JLabel(((NetworkTableModel) d_tableModel).getDescription());
		d_rootPanel.add(description, BorderLayout.NORTH);
		d_table.setTableHeader(null);
		super.initComps();
	}
	
	class NetworkTableCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object val, boolean isSelected, boolean hasFocus, int row, int col) {
			
			JLabel label = BasicComponentFactory.createLabel(((LabeledPresentationModel)val).getLabelModel());
			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	
			if (((PresentationModel<?>)val).getBean() instanceof Drug) {
				label.setBackground(Color.lightGray);
			} 
			label.setOpaque(true);
			
			if (((NetworkTableModel) d_tableModel).getDescriptionAt(row, col) != null) {
				label.setToolTipText(((NetworkTableModel) d_tableModel).getDescriptionAt(row, col));
			}
			
			TableColumn colo = table.getColumnModel().getColumn(col);
			if ((label.getPreferredSize().width+5) > colo.getWidth()) {
				table.setVisible(false);
				colo.setMinWidth(label.getPreferredSize().width + 5);
				table.setVisible(true);
			}
			
			return label;
		}
	}
}
