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

package org.drugis.addis.gui.renderer;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.apache.commons.lang.StringUtils;
import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.presentation.wizard.TreatmentCategorizationWizardPresentation.CategorySpecifiers;

public class CategoryComboboxRenderer implements ListCellRenderer {
	private boolean d_alternate;
	private ListCellRenderer d_defaultRenderer;

	public CategoryComboboxRenderer(boolean hasPrevious) {
		d_defaultRenderer = new JComboBox().getRenderer();
		d_alternate = hasPrevious; 
	}
	
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		// Pre-processing 
		boolean postprocess = false;
		if(value instanceof ChoiceNode) {
			postprocess = true;
			String property = StringUtils.lowerCase(value.toString());
			if(d_alternate) {
				value = "Consider " + property;
			} else { 
				value = "Consider " + property + " first";
			}
		}
		
		Component c = d_defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		//Post-processing 
		if(	postprocess
			|| value instanceof CategorySpecifiers 
			|| (value instanceof LeafNode && value.toString().equals(LeafNode.NAME_EXCLUDE))) {
			c.setFont(c.getFont().deriveFont(Font.BOLD));
		}
		return c;
	} 
	
}