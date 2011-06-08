/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import org.apache.commons.lang.StringUtils;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Study.StudyOutcomeMeasure;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.NoteViewButton;
import org.drugis.addis.gui.RelativeEffectTableDialog;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.MeasurementCellRenderer;
import org.drugis.addis.presentation.MeanDifferenceTableModel;
import org.drugis.addis.presentation.OddsRatioTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.RelativeEffectTableModel;
import org.drugis.addis.presentation.RiskDifferenceTableModel;
import org.drugis.addis.presentation.RiskRatioTableModel;
import org.drugis.addis.presentation.StandardisedMeanDifferenceTableModel;
import org.drugis.addis.presentation.StudyPresentation;
import org.drugis.addis.presentation.wizard.MissingMeasurementPresentation;
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
	private Class<? extends Variable> d_type;

	public StudyOutcomeMeasuresView(StudyPresentation model, AddisWindow main, Class<? extends Variable> type) {
		this(model, main, main.getPresentationModelFactory(), type);
	}
	
	public StudyOutcomeMeasuresView(StudyPresentation model, JFrame parent, PresentationModelFactory pmf, Class<? extends Variable> type) {
		d_model = model;
		d_pmf = pmf;
		d_mainWindow = parent;
		d_type = type;		
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, left:0:grow, 3dlu, left:pref, 3dlu, left:pref", 
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
	
		List<? extends StudyOutcomeMeasure<?>> outcomeMeasures = null;
		if (d_type == Endpoint.class) {
			outcomeMeasures = d_model.getBean().getStudyEndpoints();
		} else if (d_type == AdverseEvent.class) {
			 outcomeMeasures = d_model.getBean().getStudyAdverseEvents();
		} else if (d_type == PopulationCharacteristic.class) {
			outcomeMeasures = d_model.getBean().getStudyPopulationCharacteristics();
		}
		if (outcomeMeasures.isEmpty()) {
			builder.addLabel("No " + d_type.getSimpleName(), cc.xy(1, 1));
		} else {
			int row = 1;
			for (StudyOutcomeMeasure<?> som : outcomeMeasures) {
				Variable var = (Variable) som.getValue();
				NoteViewButton omNotes = new NoteViewButton(d_mainWindow, var.getName(), som.getNotes());
				builder.add(omNotes, cc.xy(1, row));
				
				builder.add( GUIFactory.createOutcomeMeasureLabelWithIcon(var), cc.xy(3, row));
				
				JPanel panel = new JPanel(new FlowLayout());
				if (var instanceof OutcomeMeasure) {
					OutcomeMeasure om = (OutcomeMeasure) var;
					if (om.getType().equals(Variable.Type.RATE)) {
						panel.add(createOddsRatioButton(om));
						panel.add(createRiskRatioButton(om));
						panel.add(createRiskDifferenceButton(om));
					} else if (om.getType().equals(Variable.Type.CONTINUOUS)) {
						panel.add(createWMDButton(om));
						panel.add(createSMDButton(om));
					}
				}
				builder.add(panel, cc.xy(5, row));
				row += 2;

				LayoutUtil.addRow(layout);
			}
		
			EnhancedTable measurementTable = null;
			if (d_type == Endpoint.class) {
				measurementTable = EnhancedTable.createWithSorterAndAutoSize(d_model.getEndpointTableModel());
			} else if (d_type == AdverseEvent.class) {
				measurementTable = EnhancedTable.createWithSorterAndAutoSize(d_model.getAdverseEventTableModel());
			} else if (d_type == PopulationCharacteristic.class) {
				measurementTable = EnhancedTable.createWithSorterAndAutoSize(d_model.getPopulationCharTableModel());
			}
			measurementTable.setSortingStatus(0, TableSorter.ASCENDING);
			measurementTable.setDefaultRenderer(MissingMeasurementPresentation.class, new MeasurementCellRenderer());

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
		JButton button = new JButton(StringUtils.remove(tableModel.getTitle(), " Table"));
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
