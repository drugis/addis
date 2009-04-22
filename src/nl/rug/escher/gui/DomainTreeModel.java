package nl.rug.escher.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.DomainListener;
import nl.rug.escher.entities.Endpoint;

public class DomainTreeModel implements TreeModel {
	public static final int ENDPOINTS = 0;
	private String d_root = "Database";
	private String d_endpointsNode = "Endpoints";
	private Domain d_domain;
	
	private List<TreeModelListener> d_listeners;
	
	
	private class DomainListenerImpl implements DomainListener {
		public void endpointsChanged() {
			fireTreeStructureChanged();
		}
	}
	
	public DomainTreeModel(Domain domain) {
		d_domain = domain;
		d_domain.addListener(new DomainListenerImpl());
		
		d_listeners = new ArrayList<TreeModelListener>();
	}

	public Object getChild(Object parent, int childIndex) {
		if (d_root == parent && childIndex == ENDPOINTS) {
			return d_endpointsNode;
		} else if (isEndpointRequest(parent, childIndex)) {
			return d_domain.getEndpoints().get(childIndex);
		}
		return null;
	}

	private boolean isEndpointRequest(Object parent, int childIndex) {
		return d_endpointsNode == parent && childIndex >= 0 && childIndex < d_domain.getEndpoints().size();
	}

	public int getChildCount(Object parent) {
		if (d_root == parent) {
			return 1;
		} else if (d_endpointsNode == parent) {
			return d_domain.getEndpoints().size();
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent == d_root && child == d_endpointsNode) {
			return 0;
		}
		if (parent == d_endpointsNode) {
			if (child instanceof Endpoint) {
				return d_domain.getEndpoints().indexOf(child);
			}
		}
		return -1;
	}

	public Object getRoot() {
		return d_root;
	}

	public boolean isLeaf(Object node) {
		return d_domain.getEndpoints().contains(node);
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
		// TODO Auto-generated method stub

	}
}
