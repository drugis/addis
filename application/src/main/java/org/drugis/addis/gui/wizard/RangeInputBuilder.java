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

package org.drugis.addis.gui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.ListModel;

import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.RangeEdge;
import org.drugis.addis.gui.renderer.CategoryComboboxRenderer;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RangeInputBuilder {
	private final JDialog d_dialog;
	private final RangeInputPresentation d_pm;

	public RangeInputBuilder(final JDialog dialog, final RangeInputPresentation rangeInputPresentation) {
		d_dialog = dialog;
		d_pm = rangeInputPresentation;
	}

	public int addFamilyToPanel(final PanelBuilder builder, int row) {
		final FormLayout layout = builder.getLayout();

		final ObservableList<DecisionTreeEdge> ranges = d_pm.getRanges();
		for (final DecisionTreeEdge edge : ranges) {
			row = rangeRow(layout, builder, row, (RangeEdge) edge);
		}
		return row;
	}

	private int rangeRow(final FormLayout layout,
			final PanelBuilder builder,
			int row,
			final RangeEdge range) {
		final CellConstraints cc = new CellConstraints();
		row = LayoutUtil.addRow(layout, row);

		final JButton splitBtn = new JButton("Split Range");
		splitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final DoseRangeCutOffDialog dialog = new DoseRangeCutOffDialog(d_dialog, d_pm.getParentPresentation(), d_pm.getParent(), range);
				dialog.setVisible(true);
			}
		});

		builder.add(splitBtn, cc.xy(1, row));
		final String variableName = GUIHelper.humanize(d_pm.getParent().getPropertyName());
		builder.add(new JLabel(RangeEdge.format(variableName, range)), cc.xy(3, row));

		final JComboBox comboBox = BasicComponentFactory.createComboBox(
				new SelectionInList<DecisionTreeNode>(
						(ListModel)d_pm.getParentPresentation().getOptionsForEdge(range),
						d_pm.getParentPresentation().getModelForEdge(range)), new CategoryComboboxRenderer(d_pm.hasPrevious()));

		builder.add(comboBox, cc.xy(5, row));
		return row;
	}
}