/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.gui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.drugis.addis.entities.analysis.models.MTCModelWrapper;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.ConvergenceDiagnosticTableModel;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.mtc.Parameter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class ConvergenceSummaryDialog extends JDialog  {

	private static final String CONVERGENCE_TEXT = "<p>Convergence is assessed using the Brooks-Gelman-Rubin method. " +
			"This method compares within-chain and between-chain variance to calculate the <em>Potential Scale Reduction Factor</em> " +
			"(PSRF). A PSRF close to one indicates approximate convergence has been reached. See S.P. Brooks and A. Gelman (1998), " +
			"<em>General methods for monitoring convergence of iterative simulations</em>, Journal of Computational and Graphical " +
			"Statistics, 7(4): 434-455. <a href=\"http://www.jstor.org/stable/1390675\">JSTOR 1390675</a>." +
			"</p><p>Double click a parameter in the table below to see the convergence plots.</p>";
	
	private static final long serialVersionUID = -220027860371330394L;
	private final JFrame d_mainWindow;

	private final MTCModelWrapper d_model;

	private final ValueHolder<Boolean> d_modelConstructed;

	private ConvergenceDiagnosticTableModel d_tableModel;
	
	public ConvergenceSummaryDialog(final JFrame main, final MTCModelWrapper mtc, final ValueHolder<Boolean> modelConstructed, String name) {
		d_mainWindow = main;
		d_model = mtc;
		d_modelConstructed = modelConstructed;
		this.setPreferredSize(new Dimension(d_mainWindow.getWidth() / 6 * 4, d_mainWindow.getHeight() / 8 * 4));
		this.setMinimumSize(new Dimension(d_mainWindow.getWidth() / 6 * 4, d_mainWindow.getHeight() / 8 * 4));
		this.setLocationRelativeTo(d_mainWindow);
		this.setLocationByPlatform(true);
		this.setTitle(name);
		this.pack();
		d_tableModel = convergenceTable(mtc, modelConstructed);
		final JPanel panel = createPanel();
		this. add(panel);
		

	}
	
	private JPanel createPanel() { 
		final FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:0:grow",
				"pref");
		final PanelBuilder builder = new PanelBuilder(layout, new JPanel());
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		builder.add(buildConvergenceTable(d_model, d_modelConstructed), cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		builder.add(AuxComponentFactory.createHtmlField(CONVERGENCE_TEXT), cc.xy(3, 1));
		
		final JPanel panel = builder.getPanel();

		d_tableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				panel.validate();
			}
		});
		return panel;
	}
	
	private TablePanel buildConvergenceTable(final MTCModelWrapper mtc, ValueHolder<Boolean> modelConstructed) {
		ConvergenceDiagnosticTableModel tableModel = convergenceTable(mtc, modelConstructed);
		EnhancedTable convergenceTable = EnhancedTable.createBare(tableModel);

		TablePanel pane = new TablePanel(convergenceTable);
		convergenceTable.autoSizeColumns();

		convergenceTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!mtc.hasSavedResults() && e.getClickCount() > 1) {
					JTable table = (JTable)e.getComponent();
					int row = table.convertRowIndexToModel(table.rowAtPoint(e.getPoint()));
					Parameter[] parameters = mtc.getResults().getParameters();
					if (row <= parameters.length) {
						Parameter p = parameters[row];
						showConvergencePlots(mtc, p);
					}
				}
			}
		});
		return pane;
	}

	private ConvergenceDiagnosticTableModel convergenceTable(final MTCModelWrapper mtc, ValueHolder<Boolean> modelConstructed) {
		return (d_tableModel == null) ? new ConvergenceDiagnosticTableModel(mtc, modelConstructed) : d_tableModel;
	}

	private void showConvergencePlots(MTCModelWrapper mtc, Parameter p) {
		if(mtc.getResults().getNumberOfSamples() > 0) {
			JDialog dialog = new ConvergencePlotsDialog(d_mainWindow, mtc, p);
			dialog.setPreferredSize(new Dimension(d_mainWindow.getWidth() / 5 * 4, d_mainWindow.getHeight() / 5 * 4));
			dialog.setMinimumSize(new Dimension(d_mainWindow.getMinimumSize().width - 100, d_mainWindow.getMinimumSize().height - 100));
			dialog.setModal(true);
			dialog.setLocationRelativeTo(d_mainWindow);
			dialog.setLocationByPlatform(true);
			dialog.pack();
			dialog.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(d_mainWindow, "Convergence plots cannot be shown because the results of " +
					"this analysis has been discarded to save memory.", "No results available", JOptionPane.WARNING_MESSAGE);
		}
	}
}