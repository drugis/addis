package nl.rug.escher.addis.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.DomainListener;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.Study;

public class DomainTreeModel implements TreeModel {
	public static final int ENDPOINTS = 0;
	public static final int STUDIES = 1;
	private String d_root = "Database";
	private String d_endpointsNode = "Endpoints";
	private String d_studiesNode = "Studies";
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
		} else if (d_root == parent && childIndex == STUDIES) {
			return d_studiesNode;
		} else if (isEndpointRequest(parent, childIndex)) {
			return new ArrayList<Endpoint>(d_domain.getEndpoints()).get(childIndex); //FIXME
		} else if (isStudyRequest(parent, childIndex)) {
			return new ArrayList<Study>(d_domain.getStudies()).get(childIndex); //FIXME
		}
		return null;
	}

	private boolean isStudyRequest(Object parent, int childIndex) {
		return d_studiesNode == parent && childIndex >= 0 && childIndex < d_domain.getStudies().size();
	}

	private boolean isEndpointRequest(Object parent, int childIndex) {
		return d_endpointsNode == parent && childIndex >= 0 && childIndex < d_domain.getEndpoints().size();
	}

	public int getChildCount(Object parent) {
		if (d_root == parent) {
			return 2;
		} else if (d_endpointsNode == parent) {
			return d_domain.getEndpoints().size();
		} else if (d_studiesNode == parent) {
			return d_domain.getStudies().size();
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent == d_root && child == d_endpointsNode) {
			return 0;
		}
		if (parent == d_root && child == d_studiesNode) {
			return 1;
		}
		if (parent == d_endpointsNode) {
			return new ArrayList<Endpoint>(d_domain.getEndpoints()).indexOf(child); //FIXME
		}
		if (parent == d_studiesNode) {
			return new ArrayList<Study>(d_domain.getStudies()).indexOf(child); //FIXME
		}
		return -1;
	}

	public Object getRoot() {
		return d_root;
	}

	public boolean isLeaf(Object node) {
		return d_domain.getEndpoints().contains(node) || d_domain.getStudies().contains(node);
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

	public Object getStudiesNode() {
		return getChild(getRoot(), DomainTreeModel.STUDIES);
	}

	public Object getEndpointsNode() {
		return getChild(getRoot(), DomainTreeModel.ENDPOINTS);
	}
}
