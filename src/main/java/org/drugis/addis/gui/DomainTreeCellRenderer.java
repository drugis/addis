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

package org.drugis.addis.gui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.EntityCategory;
import org.drugis.common.ImageLoader;

@SuppressWarnings("serial")
public class DomainTreeCellRenderer extends DefaultTreeCellRenderer {
	private final Domain d_domain;

	public DomainTreeCellRenderer(Domain domain) {
		d_domain = domain;
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,	boolean sel,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (value instanceof EntityCategory) {
			CategoryKnowledge knowledge = CategoryKnowledgeFactory.getCategoryKnowledge((EntityCategory)value);
			super.getTreeCellRendererComponent(tree, knowledge.getPlural(), sel, expanded, leaf, row, hasFocus);
		} else {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		}
		if (value instanceof Entity) {
			CategoryKnowledge knowledge =
				CategoryKnowledgeFactory.getCategoryKnowledge(d_domain.getCategory((Entity)value));
			setIcon(ImageLoader.getIcon(knowledge.getIconName()));
			setToolTipText(knowledge.getSingular());
		} else {
			setToolTipText(null); //no tool tip
		}
		return this;
	}
}
