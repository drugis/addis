/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.EntitiesTablePanel;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class EntitiesNodeView<T extends Entity> implements ViewBuilder {
	
	private List<String> d_formatter;
	private List<PresentationModel<T>> d_dpms;
	private Main d_main;
	private String d_title;

	public EntitiesNodeView(List<String> formatter, List<PresentationModel<T>> dpms, Main main, String title) {
		d_formatter = formatter;
		d_dpms = dpms;
		d_main = main;
		d_title = title;
	}

	public JComponent buildPanel() {		
		FormLayout layout = new FormLayout(
				"pref",
				"p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);

		CellConstraints cc =  new CellConstraints();
		
		if (d_title != null)
			builder.addSeparator(d_title, cc.xy(1, 1));
		
		TablePanel tablePanel = new EntitiesTablePanel<T>(d_formatter, d_dpms, d_main);
		int height = tablePanel.getPreferredSize().height;
		int width = Math.min(715, tablePanel.getPreferredSize().width);
		tablePanel.getTable().setPreferredScrollableViewportSize(new Dimension(width, height));
		builder.add(tablePanel,cc.xy(1, 3));

		return builder.getPanel();
	}

}
