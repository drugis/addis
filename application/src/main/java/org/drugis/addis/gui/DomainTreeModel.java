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

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.EntityCategory;

import com.jgoodies.binding.list.ObservableList;


public class DomainTreeModel implements TreeModel {
	private String d_root = "Database";
	private Domain d_domain;
	
	private List<TreeModelListener> d_listeners;
		
	private class CategoryListener implements ListDataListener {
		private final EntityCategory d_category;

		public CategoryListener(EntityCategory c) {
			d_category = c;
		}
		
		public void contentsChanged(ListDataEvent e) {
			throw new IllegalStateException("Unexpected behavior from Category " + d_category + ": list elements being overwritten.");
		}

		public void intervalAdded(ListDataEvent e) {
			if (e.getIndex0() != e.getIndex1()) {
				throw new IllegalStateException("Unexpected behavior from Category " + d_category + ": list insertions not one-by-one.");
			}
			fireTreeNodeInserted(d_category, e.getIndex0());	
		}

		public void intervalRemoved(ListDataEvent e) {
			if (e.getIndex0() != e.getIndex1()) {
				throw new IllegalStateException("Unexpected behavior from Category " + d_category + ": list removals not one-by-one.");
			}
			fireTreeNodeRemoved(d_category, e.getIndex0());	
		}
	}
	
	public DomainTreeModel(Domain domain) {
		d_domain = domain;
		
		for (EntityCategory c : d_domain.getCategories()) {
			d_domain.getCategoryContents(c).addListDataListener(new CategoryListener(c));
		}
		
		d_listeners = new ArrayList<TreeModelListener>();
	}

	public Object getChild(Object parent, int childIndex) {
		if (d_root.equals(parent) && childIndex >= 0 && childIndex < d_domain.getCategories().size()) {
			return d_domain.getCategories().get(childIndex);
		} else {
			for (EntityCategory cat : d_domain.getCategories()) {
				if (isCategoryRequest(cat, parent, childIndex)) {
					return d_domain.getCategoryContents(cat).get(childIndex);
				}
			}
		}
		return null;
	}
	
	private boolean isCategoryRequest(EntityCategory categoryNode, Object parent, int childIndex) {
		return categoryNode == parent && childIndex >= 0 && childIndex < d_domain.getCategoryContents(categoryNode).size();
	}

	private EntityCategory getCategoryNode(Object node) {
		int typeIdx = d_domain.getCategories().indexOf(node);
		if (typeIdx >= 0) {
			return d_domain.getCategories().get(typeIdx);
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if (d_root.equals(parent)) {
			return d_domain.getCategories().size();
		} else {
			ObservableList<? extends Entity> contents = d_domain.getCategoryContents(getCategoryNode(parent));
			if (contents != null) {
				return contents.size();
			}
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent.equals(d_root)) {
			return d_domain.getCategories().indexOf(child);
		} else {
			ObservableList<? extends Entity> contents = d_domain.getCategoryContents(getCategoryNode(parent));
			if (contents != null) {
				return contents.indexOf(child);
			}
		}
		return -1;
	}

	public Object getRoot() {
		return d_root;
	}
	
	public boolean isLeaf(Object node) {
		if (node instanceof Entity) {
			EntityCategory category = d_domain.getCategory(((Entity) node));
			if (category != null) {
				return d_domain.getCategoryContents(category).contains(node);
			}
		}
		return false;
	}
	
	public TreePath getPathTo(Object node) {
		if (d_root.equals(node)) {
			return new TreePath(new Object[] { d_root });
		} else if (d_domain.getCategories().contains(node)) {
			return new TreePath(new Object[] { d_root, node });
		} else if (isLeaf(node)) {
			return new TreePath(new Object[] { d_root, d_domain.getCategory(((Entity)node)), node }); 
		}
		return null;
	}

	public void addTreeModelListener(TreeModelListener listener) {
		if (!d_listeners.contains(listener)) {
			d_listeners.add(listener);
		}
	}
	
	public void removeTreeModelListener(TreeModelListener listener) {
		d_listeners.remove(listener);
	}

	private void fireTreeNodeInserted(EntityCategory category, int idx) {
		for (TreeModelListener l : d_listeners) {
			l.treeNodesInserted(new TreeModelEvent(this, new Object[]{d_root, category}, new int[]{idx}, 
					new Object[] {d_domain.getCategoryContents(category).get(idx)}));
		}
	}

	private void fireTreeNodeRemoved(EntityCategory category, int idx) {
		for (TreeModelListener l : d_listeners) {
			l.treeNodesRemoved(new TreeModelEvent(this, new Object[]{d_root, category}, new int[]{idx}, null));
		}
	}



	public void valueForPathChanged(TreePath path, Object node) {
	}
}
