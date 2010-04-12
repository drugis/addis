package org.drugis.addis.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.gui.components.EnhancedTableHeader;
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
			} else {
				label.setBackground(Color.WHITE);
			}
			label.setOpaque(true);
			
			if (((NetworkTableModel) d_tableModel).getDescriptionAt(row, col) != null) {
				label.setToolTipText(((NetworkTableModel) d_tableModel).getDescriptionAt(row, col));
			}
			
//			 Set correct size
			TableColumn column = table.getColumnModel().getColumn(col);
			column.setMinWidth((int) label.getPreferredSize().getWidth() + 10);
			d_table.setPreferredScrollableViewportSize(new Dimension(d_table.getPreferredSize().width, d_table.getPreferredSize().height ));
			
			return label;
		}
	}
	
	public void doLayout() {
		super.doLayout();
		EnhancedTableHeader.autoSizeColumns(d_table);
		d_table.setPreferredScrollableViewportSize(new Dimension(d_table.getPreferredSize().width, d_table.getPreferredSize().height ));
	}
}
