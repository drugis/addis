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

package org.drugis.addis.gui;

import javax.swing.JDialog;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.value.ValueModel;


public interface CategoryKnowledge {
	
	/**
	 * Get the type name (singular) of entities in this category with a capitalized first letter.
	 */
	public String getSingular();
	
	/**
	 * Get the type name (singular) of entities in this category with a capitalized first letter.
	 */
	public String getSingularCapitalized();
	
	/**
	 * Get the type name (plural) of entities in this category.
	 */
	public String getPlural();

	/**
	 * Get the filename for the entity icon.
	 */
	public String getIconName();
	
	/**
	 * Get the filename for the entity "new" icon.
	 */
	public String getNewIconName();

	/**
	 * Get the entity mnemonic (keyboard shortcut).
	 */
	public char getMnemonic();
	
	/**
	 * Builds the creation dialog for this category.
	 */
	public JDialog getAddDialog(AddisWindow mainWindow, Domain domain, ValueModel selectionModel);
	
	/**
	 * Whether the category should get a toolbar button.
	 */
	public boolean isToolbarCategory();
	
	/**
	 * What to show for the category
	 */
	public ViewBuilder getCategoryViewBuilder(AddisWindow main, Domain domain);
	
	/**
	 * What to show for a specific entity 
	 */
	public ViewBuilder getEntityViewBuilder(AddisWindow main, Domain domain, Entity entity);
}
