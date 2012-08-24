/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.gui.components;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JWindow;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
abstract public class JTableWithPopupEditor extends JTable {
	protected Window d_parent;
	private JWindow d_window;
	private JLabel d_hidden;
	private int d_row = 0;
	private int d_col = 0;

	public JTableWithPopupEditor(TableModel model, Window frame) {
		super(model);
		d_parent = frame;

		setCellSelectionEnabled(true);

		if (d_parent == null) {
			return;
		}

		// Mouse listener to start editing cell
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = ((JTable)e.getComponent()).rowAtPoint(e.getPoint());
				int col = ((JTable)e.getComponent()).columnAtPoint(e.getPoint());
				startCellEditor(row, col);
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
					int col = getSelectedColumn();
					int row = getSelectedRow();
					if (row >= 0 && col >= 0) {
						e.consume();
						startCellEditor(row, col);
					}
				}
			}
		});

		// Make sure the window closes when our hidden field gains focus
		d_hidden = new JLabel();
		d_hidden.setFocusable(true);
		FocusAdapter destroyInputListener = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent ev) {
				destroyInputWindow();
			}
		};
		d_hidden.addFocusListener(destroyInputListener);

		d_parent.addFocusListener(destroyInputListener);
		addFocusListener(destroyInputListener);

		addAncestorListener(new AncestorListener() {
			public void ancestorRemoved(AncestorEvent event) {
				destroyInputWindow();
			}

			public void ancestorMoved(AncestorEvent event) {
				positionWindow();
			}

			public void ancestorAdded(AncestorEvent e) {
			}
		});
		addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if(e.getChanged() instanceof JScrollPane) {
					registerDestroyInputMouseListener(e.getChanged());
				}
			}
		});
	}

	private void positionWindow() {
		if (d_window != null) {
			setWindowLocation();
		}
	}

	abstract protected JPanel createEditorPanel(int row, int col);

	private JWindow startCellEditor(int row, int col) {
		destroyInputWindow();

		JPanel panel = createEditorPanel(row, col);
		if (panel == null) {
			return null;
		}

		for (Component c : panel.getComponents()) {
			c.addKeyListener(new KeyAdapter() {
				@Override public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						destroyInputWindow();
					}
				}
			});
			Set<AWTKeyStroke> keys = new HashSet<AWTKeyStroke>(c.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
			keys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
			c.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, keys);
		}


		panel.add(d_hidden);

		// create the window
		final JWindow window = new JWindow(d_parent);
		d_window = window;

		window.getContentPane().add(panel, BorderLayout.CENTER);
		window.pack();

		d_col = col;
		d_row = row;
		setWindowLocation();

		// Make sure the window closes when we click in the frame containing the table
		getTopLevelAncestor().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				destroyInputWindow();
			}
		});

		window.setVisible(true);

		// Make sure the first component has focus
		window.requestFocusInWindow();
		window.getFocusTraversalPolicy().getFirstComponent(window).requestFocus();

		return window;
	}

	private void setWindowLocation() {
		Rectangle cellLocation = getCellRect(d_row, d_col, false);
		Point l = getComponentAt(d_col, d_row).getLocationOnScreen();
		l.translate(cellLocation.x + cellLocation.width / 2, cellLocation.y + cellLocation.height / 2);
		d_window.setLocation(l);
	}

	public void destroyInputWindow() {
		if (d_window != null) {
			d_window.dispose();
			d_window = null;
		}
	}

	private void registerDestroyInputMouseListener(Component component) {
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				destroyInputWindow();
			}
		});
	}

}
