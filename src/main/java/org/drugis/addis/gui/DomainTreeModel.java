/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.common.CollectionUtil;


public class DomainTreeModel implements TreeModel {
	public static final int INDICATIONS = 0;
	public static final int DRUGS = 1;	
	public static final int ENDPOINTS = 2;
	public static final int STUDIES = 3;
	private String d_root = "Database";
	private String d_indicationsNode = "Indications";
	private String d_endpointsNode = "Endpoints";
	private String d_studiesNode = "Studies";
	private String d_drugsNode = "Drugs";
	private Domain d_domain;
	
	private List<TreeModelListener> d_listeners;
	
	
	private class DomainListenerImpl implements DomainListener {
		public void endpointsChanged() {
			fireTreeStructureChanged();
		}

		public void studiesChanged() {
			fireTreeStructureChanged();
		}

		public void drugsChanged() {
			fireTreeStructureChanged();
		}

		public void indicationsChanged() {
			fireTreeStructureChanged();
		}
	}
	
	public DomainTreeModel(Domain domain) {
		d_domain = domain;
		d_domain.addListener(new DomainListenerImpl());
		
		d_listeners = new ArrayList<TreeModelListener>();
	}

	public Object getChild(Object parent, int childIndex) {
		if (d_root == parent && childIndex == INDICATIONS) {
			return d_indicationsNode;
		} else if (d_root == parent && childIndex == ENDPOINTS) {
			return d_endpointsNode;
		} else if (d_root == parent && childIndex == STUDIES) {
			return d_studiesNode;
		} else if (d_root == parent && childIndex == DRUGS) {
			return d_drugsNode;
		} else if (isIndicationRequest(parent, childIndex)) {
			return CollectionUtil.getElementAtIndex(d_domain.getIndications(), childIndex);
		} else if (isEndpointRequest(parent, childIndex)) {
			return CollectionUtil.getElementAtIndex(d_domain.getEndpoints(), childIndex);
		} else if (isDrugsRequest(parent, childIndex)) {
			return CollectionUtil.getElementAtIndex(d_domain.getDrugs(), childIndex);
		} else if (isStudyRequest(parent, childIndex)) {
			return CollectionUtil.getElementAtIndex(d_domain.getStudies(), childIndex);
		}
		return null;
	}

	private boolean isIndicationRequest(Object parent, int childIndex) {
		return d_indicationsNode == parent && childIndex >= 0 && childIndex < d_domain.getIndications().size();
	}

	private boolean isStudyRequest(Object parent, int childIndex) {
		return d_studiesNode == parent && childIndex >= 0 && childIndex < d_domain.getStudies().size();
	}
	
	private boolean isDrugsRequest(Object parent, int childIndex) {
		return d_drugsNode == parent && childIndex >= 0 && childIndex < d_domain.getDrugs().size();
	}	

	private boolean isEndpointRequest(Object parent, int childIndex) {
		return d_endpointsNode == parent && childIndex >= 0 && childIndex < d_domain.getEndpoints().size();
	}

	public int getChildCount(Object parent) {
		if (d_root == parent) {
			return 4;
		} else if (d_indicationsNode == parent) {
			return d_domain.getIndications().size();
		} else if (d_endpointsNode == parent) {
			return d_domain.getEndpoints().size();
		} else if (d_studiesNode == parent) {
			return d_domain.getStudies().size();
		} else if (d_drugsNode == parent) {
			return d_domain.getDrugs().size();
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent == d_root && child == d_indicationsNode) {
			return INDICATIONS;
		}
		if (parent == d_root && child == d_endpointsNode) {
			return ENDPOINTS;
		}
		if (parent == d_root && child == d_studiesNode) {
			return STUDIES;
		}
		if (parent == d_root && child == d_drugsNode) {
			return DRUGS;
		}	
		if (parent == d_indicationsNode) {
			return CollectionUtil.getIndexOfElement(d_domain.getIndications(), child);
		}
		if (parent == d_endpointsNode) {
			return CollectionUtil.getIndexOfElement(d_domain.getEndpoints(), child);
		}
		if (parent == d_studiesNode) {
			return CollectionUtil.getIndexOfElement(d_domain.getStudies(), child);
		}
		if (parent == d_drugsNode) {
			return CollectionUtil.getIndexOfElement(d_domain.getDrugs(), child);			
		}
		return -1;
	}

	public Object getRoot() {
		return d_root;
	}

	public boolean isLeaf(Object node) {
		if (node instanceof Indication) {
			return d_domain.getIndications().contains(node);
		}
		if (node instanceof Endpoint) {
			return d_domain.getEndpoints().contains(node);
		}
		if (node instanceof Study) {
			return d_domain.getStudies().contains(node);
		}
		if (node instanceof Drug) {
			return d_domain.getDrugs().contains(node);			
		}
		return false;
	}

	public void addTreeModelListener(TreeModelListener listener) {
		if (!d_listeners.contains(listener)) {
			d_listeners.add(listener);
		}
	}
	
	public void removeTreeModelListener(TreeModelListener listener) {
		d_listeners.remove(listener);
	}

	private void fireTreeStructureChanged() {
		for (TreeModelListener l : d_listeners) {
			l.treeStructureChanged(new TreeModelEvent(this, new Object[]{d_root}));
		}
	}

	public void valueForPathChanged(TreePath path, Object node) {
	}
	
	public Object getIndicationsNode() {
		return getChild(getRoot(), DomainTreeModel.INDICATIONS);
	}

	public Object getStudiesNode() {
		return getChild(getRoot(), DomainTreeModel.STUDIES);
	}

	public Object getEndpointsNode() {
		return getChild(getRoot(), DomainTreeModel.ENDPOINTS);
	}
	
	public Object getDrugsNode() {
		return getChild(getRoot(), DomainTreeModel.DRUGS);
	}
}
