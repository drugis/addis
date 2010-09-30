/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

package org.drugis.addis.gui.builder;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.RelativeEffectTableDialog;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.presentation.MeanDifferenceTableModel;
import org.drugis.addis.presentation.OddsRatioTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.RelativeEffectTableModel;
import org.drugis.addis.presentation.RiskDifferenceTableModel;
import org.drugis.addis.presentation.RiskRatioTableModel;
import org.drugis.addis.presentation.StandardisedMeanDifferenceTableModel;
import org.drugis.addis.presentation.StudyPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.java.components.TableSorter;

public class StudyOutcomeMeasuresView implements ViewBuilder {
	
	private StudyPresentation d_model;
	private PresentationModelFactory d_pmf;
	private JFrame d_mainWindow;
	private boolean d_isEndpoints;

	public StudyOutcomeMeasuresView(StudyPresentation model, Main main, boolean endpoints) {
		this(model, main, main.getPresentationModelFactory(), endpoints);
	}
	
	public StudyOutcomeMeasuresView(StudyPresentation model, JFrame parent, 
			PresentationModelFactory pmf, boolean endpoints) {
		d_model = model;
		d_pmf = pmf;
		d_mainWindow = parent;
		d_isEndpoints = endpoints;		
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, left:pref, 3dlu, pref:grow", 
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
	
		List<OutcomeMeasure> outcomeMeasures = d_isEndpoints ? d_model.getEndpoints() : d_model.getAdverseEvents();
		if (outcomeMeasures.isEmpty()) {
			builder.addLabel("No " + (d_isEndpoints ? "Endpoints" : "Adverse Events"), cc.xy(1, 1));
		} else {
			int row = 1;
			for (OutcomeMeasure om : outcomeMeasures) {
				JComponent outcomeMeasureLabelWithIcon = GUIFactory.createOutcomeMeasureLabelWithIcon(om);
				
				outcomeMeasureLabelWithIcon.setToolTipText(GUIHelper.createToolTip(
						d_model.getBean().getNote(om)));
				builder.add(
						outcomeMeasureLabelWithIcon,
						cc.xy(1, row));
				
				JPanel panel = new JPanel(new FlowLayout());
				if (om.getType().equals(Variable.Type.RATE)) {
					panel.add(createOddsRatioButton(om));
					panel.add(createRiskRatioButton(om));
					panel.add(createRiskDifferenceButton(om));
				} else if (om.getType().equals(Variable.Type.CONTINUOUS)) {
					panel.add(createWMDButton(om));
					panel.add(createSMDButton(om));
				}
				builder.add(panel, cc.xy(3, row));
				row += 2;

				LayoutUtil.addRow(layout);
			}
		
			EnhancedTable measurementTable = null;
			if (d_isEndpoints) {
				measurementTable = new EnhancedTable(d_model.getEndpointTableModel());
			} else {
				measurementTable = new EnhancedTable(d_model.getAdverseEventTableModel());
			}
			measurementTable.setSortingStatus(0, TableSorter.ASCENDING);

			builder.add(AuxComponentFactory.createUnscrollableTablePanel(measurementTable),
					cc.xyw(1, row, 5));
		}
		
		return builder.getPanel();
	}

	private JButton createOddsRatioButton(OutcomeMeasure om) {
		final RelativeEffectTableModel tableModel = new OddsRatioTableModel(d_model.getBean(), om, d_pmf);
		return createRatioButton(tableModel);
	}

	private JButton createRiskRatioButton(OutcomeMeasure om) {
		final RelativeEffectTableModel tableModel = new RiskRatioTableModel(d_model.getBean(), om, d_pmf);
		return createRatioButton(tableModel);
	}
	
	private JButton createRiskDifferenceButton(OutcomeMeasure om) {
		final RelativeEffectTableModel tableModel = new RiskDifferenceTableModel(d_model.getBean(), om, d_pmf);
		return createRatioButton(tableModel);
	}
	
	private JButton createWMDButton(OutcomeMeasure om) {
		final RelativeEffectTableModel tableModel = new MeanDifferenceTableModel(d_model.getBean(), om, d_pmf);
		return createRatioButton(tableModel);
	}
	
	private JButton createSMDButton(OutcomeMeasure om) {
		final RelativeEffectTableModel tableModel = new StandardisedMeanDifferenceTableModel(d_model.getBean(), om, d_pmf);
		return createRatioButton(tableModel);
	}
	
	private JButton createRatioButton(final RelativeEffectTableModel tableModel) {
		JButton button = new JButton(tableModel.getTitle());
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RelativeEffectTableDialog dlg = new RelativeEffectTableDialog(d_mainWindow, tableModel);
				GUIHelper.centerWindow(dlg, d_mainWindow);
				dlg.setVisible(true);
			}
		});
		return button;
	}
}
