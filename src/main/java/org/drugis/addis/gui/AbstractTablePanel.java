package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.drugis.addis.gui.components.EnhancedTableHeader;
import org.drugis.addis.presentation.TableModelWithDescription;

@SuppressWarnings("serial")
public class AbstractTablePanel extends JPanel {

	protected TableModel d_tableModel;
	//protected JPanel d_rootPanel;
	protected JTable d_table;
	private JScrollPane d_scroll;
	private int d_maxWidth;

	public AbstractTablePanel(TableModel tableModel) {
		super();
		d_tableModel = tableModel;
		d_table = new JTable(d_tableModel);
		d_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//d_rootPanel = new JPanel(new BorderLayout());
		//d_rootPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setRenderer();
		initComps();
	}
	
	public void setRenderer() {
		d_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
		EnhancedTableHeader.autoSizeColumns(d_table);
	}
	
	public void setMaxWidth(int width) {
		d_maxWidth = width;
	}
	
	protected void initComps() {
		JPanel d_rootPanel = new JPanel();
		d_rootPanel.setLayout(new BorderLayout());
		
		if (d_tableModel instanceof TableModelWithDescription)
			d_rootPanel.add(new JLabel(((TableModelWithDescription) d_tableModel).getDescription()), BorderLayout.NORTH);
		
		EnhancedTableHeader.autoSizeColumns(d_table);
	
		d_scroll = new JScrollPane(d_table);
		d_scroll.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(7, 7, 7, 7),
						BorderFactory.createMatteBorder(1, 1, 0, 0, Color.gray)));
		d_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		d_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		d_table.setPreferredScrollableViewportSize(new Dimension(500, d_table.getPreferredSize().height ));
		
		d_table.setBackground(Color.WHITE);
		
		d_rootPanel.add(d_scroll, BorderLayout.CENTER);			
		this.add(d_rootPanel, BorderLayout.NORTH); 
		//this.add(d_scroll, BorderLayout.NORTH);
	}

	class DefaultTableCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object val, boolean isSelected, boolean hasFocus, int row, int col) {
		
			JLabel label = new JLabel(val.toString());
			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			return label;
		}
	}
		
	public void doLayout() {
		super.doLayout();
		EnhancedTableHeader.autoSizeColumns(d_table, 350);
		d_table.setPreferredScrollableViewportSize(new Dimension(d_table.getPreferredSize().width, d_table.getPreferredSize().height ));
		if (d_maxWidth != 0)
			d_scroll.setPreferredSize(new Dimension(Math.min(d_table.getPreferredScrollableViewportSize().width +22, d_maxWidth), d_tableModel.getRowCount() * 24));
	}
}