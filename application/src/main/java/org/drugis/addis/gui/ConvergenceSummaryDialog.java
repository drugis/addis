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
import java.text.DecimalFormat;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.drugis.addis.entities.mtcwrapper.MCMCModelWrapper;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.ConvergenceDiagnosticTableModel;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.mtc.MCMCSettings;
import org.drugis.mtc.Parameter;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueModel;
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
	private final MCMCModelWrapper d_wrapper;
	private final ValueHolder<Boolean> d_modelConstructed;
	private ConvergenceDiagnosticTableModel d_tableModel;
	private MCMCSettings d_settings ;

	private JPanel d_settingsPanel;
	
	public ConvergenceSummaryDialog(final JFrame main, final MCMCModelWrapper wrapper, final ValueHolder<Boolean> modelConstructed, String name) {
		d_mainWindow = main;
		d_wrapper = wrapper;
		d_modelConstructed = modelConstructed;
		d_settings = d_wrapper.getSettings();
		setPreferredSize(new Dimension(d_mainWindow.getWidth() / 6 * 4, d_mainWindow.getHeight() / 8 * 5));
		setMinimumSize(new Dimension(d_mainWindow.getWidth() / 6 * 4, d_mainWindow.getHeight() / 8 * 5));
		setLocationRelativeTo(d_mainWindow);
		setLocationByPlatform(true);
		setTitle(name);
		pack();
		d_tableModel = convergenceTable(wrapper, modelConstructed);
		final JPanel panel = createPanel();
		add(panel);
		

	}
	
	private JPanel createPanel() { 
		final FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:0:grow",
				"pref, 3dlu, pref");
		final PanelBuilder builder = new PanelBuilder(layout, new JPanel());
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		builder.add(buildConvergenceTable(d_wrapper, d_modelConstructed), cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		builder.add(AuxComponentFactory.createHtmlField(CONVERGENCE_TEXT), cc.xy(3, 1));
		d_settingsPanel = buildMCMCSettingsPanel();
		builder.add(d_settingsPanel, cc.xyw(1, 3, 3));
		
		final JPanel panel = builder.getPanel();

		d_tableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				panel.validate();
			}
		});
		return panel;
	}
	
	private JPanel buildMCMCSettingsPanel() {
		final FormLayout layout = new FormLayout(
				"pref, 7dlu, fill:0:grow",
				"pref");
		int rows = 1;
		final PanelBuilder builder = new PanelBuilder(layout, new JPanel());
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

		PresentationModel<MCMCSettings> pm = new PresentationModel<MCMCSettings>(d_settings);
		rows = buildSettingsRow(layout, rows, builder, cc, "Number of chains", pm.getModel(MCMCSettings.PROPERTY_NUMBER_OF_CHAINS));
		rows = buildSettingsRow(layout, rows, builder, cc, "Tuning iterations",  pm.getModel(MCMCSettings.PROPERTY_TUNING_ITERATIONS));
		rows = buildSettingsRow(layout, rows, builder, cc, "Simulation iterations", pm.getModel(MCMCSettings.PROPERTY_SIMULATION_ITERATIONS));
		rows = buildSettingsRow(layout, rows, builder, cc, "Thinning interval", pm.getModel(MCMCSettings.PROPERTY_THINNING_INTERVAL));
		rows = buildSettingsRow(layout, rows, builder, cc, "Inference samples", pm.getModel(MCMCSettings.PROPERTY_INFERENCE_SAMPLES));
		rows = buildSettingsRow(layout, rows, builder, cc, "Variance scaling factor",  pm.getModel(MCMCSettings.PROPERTY_VARIANCE_SCALING_FACTOR));
		
		return builder.getPanel();
	}
	private int buildSettingsRow(final FormLayout layout, int rows, final PanelBuilder builder, CellConstraints cc,
			String label, ValueModel model) {
		rows = LayoutUtil.addRow(layout, rows);
		builder.add(new JLabel(label), cc.xy(1, rows));
		builder.add(new JLabel(":"), cc.xy(2, rows));
		builder.add(BasicComponentFactory.createLabel(model, new DecimalFormat()), cc.xy(3, rows));
		return rows;
	}

	private TablePanel buildConvergenceTable(final MCMCModelWrapper wrapper, ValueHolder<Boolean> modelConstructed) {
		ConvergenceDiagnosticTableModel tableModel = convergenceTable(wrapper, modelConstructed);
		EnhancedTable convergenceTable = EnhancedTable.createBare(tableModel);

		TablePanel pane = new TablePanel(convergenceTable);
		convergenceTable.autoSizeColumns();

		convergenceTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					JTable table = (JTable)e.getComponent();
					int row = table.convertRowIndexToModel(table.rowAtPoint(e.getPoint()));
					Parameter[] parameters = wrapper.getParameters();
					if (row <= parameters.length) {
						Parameter p = parameters[row];
						showConvergencePlots(wrapper, p);
					}
				}
			}
		});
		return pane;
	}

	private ConvergenceDiagnosticTableModel convergenceTable(final MCMCModelWrapper wrapper, ValueHolder<Boolean> modelConstructed) {
		return (d_tableModel == null) ? new ConvergenceDiagnosticTableModel(wrapper, modelConstructed) : d_tableModel;
	}

	private void showConvergencePlots(MCMCModelWrapper wrapper, Parameter p) {
		if (!wrapper.isSaved() && wrapper.getModel().getResults().getNumberOfSamples() > 0) {
			JDialog dialog = new ConvergencePlotsDialog(d_mainWindow, wrapper.getModel(), p);
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
