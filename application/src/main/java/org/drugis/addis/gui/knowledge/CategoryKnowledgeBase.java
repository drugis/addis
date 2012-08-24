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

package org.drugis.addis.gui.knowledge;

import java.util.Arrays;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.CategoryKnowledge;
import org.drugis.addis.gui.builder.TitledPanelBuilder;
import org.drugis.addis.gui.components.EntityTablePanel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.list.ObservableList;

public abstract class CategoryKnowledgeBase implements CategoryKnowledge {
	private final String d_singular;
	private final String d_plural;
	private final String d_iconName;
	private final Class<? extends Entity> d_entityClass;
	
	public CategoryKnowledgeBase(String singular, String iconName, Class<? extends Entity> entityClass) {
		this(singular, capitalize(singular) + "s", iconName, entityClass);
	}
	
	public CategoryKnowledgeBase(String singular, String plural, String iconName,
			Class<? extends Entity> entityClass) {
		d_singular = singular;
		d_plural = plural;
		d_iconName = iconName;
		d_entityClass = entityClass;
	}

	public String getPlural() {
		return d_plural;
	}

	public String getSingular() {
		return d_singular;
	}
	
	public String getSingularCapitalized() {
		return capitalize(d_singular);
	}
	
	private static String capitalize(String in) {
		return Character.toUpperCase(in.charAt(0)) + in.substring(1);
	}
	
	public String getIconName() {
		return d_iconName;
	}
	
	public String getNewIconName() {
		return d_iconName;
	}
	
	public char getMnemonic() {
		return getSingularCapitalized().toLowerCase().charAt(0);
	}
	
	public boolean isToolbarCategory() {
		return false;
	}
	
	private ViewBuilder buildEntityTable(Class<? extends Entity> entityType,
			ObservableList<? extends Entity> observableList, String[] formatter, String title, PresentationModelFactory pmf, AddisWindow main) {
		return new TitledPanelBuilder(new EntityTablePanel(entityType, observableList, Arrays.asList(formatter), main, pmf), getPlural());
	}
	
	public ViewBuilder getCategoryViewBuilder(AddisWindow main, Domain domain) {
		return buildEntityTable(getEntityClass(),
				domain.getCategoryContents(domain.getCategory(getEntityClass())), getShownProperties(), getPlural(), main.getPresentationModelFactory(), main);
	}
	
	protected Class<? extends Entity> getEntityClass() {
		return d_entityClass;
	}

	protected String[] getShownProperties() {
		return new String[] {};
	}
	
}
