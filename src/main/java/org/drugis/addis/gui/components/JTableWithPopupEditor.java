package org.drugis.addis.gui.components;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JWindow;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
abstract public class JTableWithPopupEditor extends JTable {
	protected Window d_parent;
	private JWindow d_window;
	private JLabel d_hidden;
	
	public JTableWithPopupEditor(TableModel model, Window frame) {
		super(model);
		d_parent = frame;
		
		// Mouse listener to start editing cell
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = ((JTable)e.getComponent()).rowAtPoint(e.getPoint());
				int col = ((JTable)e.getComponent()).columnAtPoint(e.getPoint());
				startCellEditor(row, col);
			}
		});
		
		// Make sure the window closes when our hidden field gains focus
		d_hidden = new JLabel();
		d_hidden.setFocusable(true);
		d_hidden.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent arg0) {
				destroyWindow();
			}
		});
		
		setCellSelectionEnabled(true);
	}
	
	abstract protected JPanel createEditorPanel(int row, int col);
	
	private JWindow startCellEditor(int row, int col) {
		destroyWindow();
		
		JPanel panel = createEditorPanel(row, col);
		if (panel == null) {
			return null;
		}
		
		panel.add(d_hidden);

		// create the window
		final JWindow window = new JWindow(d_parent);
		d_window = window;
		
		window.getContentPane().add(panel, BorderLayout.CENTER);
		window.pack();
		
		setWindowLocation(col, row);

		// Make sure the window closes when we click in the frame containing the table
		getTopLevelAncestor().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				destroyWindow();
			}
		});

		window.setVisible(true);		

		// Make sure the first component has focus
		window.requestFocusInWindow();
		window.getFocusTraversalPolicy().getFirstComponent(window).requestFocus();
		
		return window;
	}

	private void setWindowLocation(int col, int row) {
		Rectangle cellLocation = getCellRect(row, col, false);
		Point l = getComponentAt(col, row).getLocationOnScreen();
		l.translate(cellLocation.x + cellLocation.width / 2, cellLocation.y + cellLocation.height / 2);
		d_window.setLocation(l);
	}

	private void destroyWindow() {
		if (d_window != null) {
			d_window.setVisible(false);
			d_window = null;
		}
	}
}
