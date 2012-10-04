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

package org.drugis.addis.gui.renderer;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.EntityCategory;
import org.drugis.addis.gui.CategoryKnowledge;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.Main;

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
			setIcon(Main.IMAGELOADER.getIcon(knowledge.getIconName()));
			setToolTipText(knowledge.getSingularCapitalized());
		} else {
			setToolTipText(null); //no tool tip
		}
		return this;
	}
}
