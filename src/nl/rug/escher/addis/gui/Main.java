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

package nl.rug.escher.addis.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import nl.rug.escher.addis.entities.AbstractStudy;
import nl.rug.escher.addis.entities.CombinedStudy;
import nl.rug.escher.addis.entities.DependentEntitiesException;
import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.DomainListener;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.common.gui.GUIHelper;
import nl.rug.escher.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;

import fi.smaa.common.ImageLoader;

@SuppressWarnings("serial")
public class Main extends JFrame {
	public static final String APPNAME = "ADDIS";
	public static final String APPVERSION = "0.1";
	private JComponent d_leftPanel;
	private JScrollPane d_rightPanel;
	
	private ViewBuilder d_rightPanelBuilder;
	
	private DomainManager d_domain;
	
	private ImageLoader imageLoader = new ImageLoader("/resources/gfx/");
	private DomainTreeModel d_domainTreeModel;
	private JTree d_leftPanelTree;
	private JMenuItem d_editMenuDeleteItem;
	private JMenuItem d_addMenuCombinedItem;

	public Main() {
		super(APPNAME + " v" + APPVERSION);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				quitApplication();
			}
		});

		setPreferredSize(new Dimension(800, 500));
		GUIHelper.initializeLookAndFeel();
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);		
		
		initializeDomain();
		
	}

	protected void quitApplication() {
		try {
			saveDomainToFile();
			System.exit(0);			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
					"Error saving domain", "Error saving domain",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void saveDomainToFile() throws IOException {
		File f = new File("domain.dat");
		if (f.exists()) {
			f.delete();
		}
		
		FileOutputStream fos = new FileOutputStream(f);
		d_domain.saveDomain(fos);
	}

	private void initializeDomain() {
		d_domain = new DomainManager();
		
		try {
			loadDomainFromFile();
		} catch (Exception e) {
			MainData.initDefaultData(d_domain.getDomain());
		}
		
		d_domain.getDomain().addListener(new MainListener());
	}
	
	private Domain getDomain() {
		return d_domain.getDomain();
	}

	private void loadDomainFromFile() throws IOException, ClassNotFoundException {
		File f = new File("domain.dat");
		if (f.exists() && f.isFile()) {
			FileInputStream fis = new FileInputStream(f);
			d_domain.loadDomain(fis);
		} else {
			throw new FileNotFoundException("domain.dat not found");
		}
	}

	void showStudyAddEndpointDialog(AbstractStudy study) {
		StudyAddEndpointDialog dialog = new StudyAddEndpointDialog(this, getDomain(), study);
		dialog.setVisible(true);
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
		menuBar.add(createAddMenu());
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(createHelpMenu());
		setJMenuBar(menuBar);
	}

	private JMenu createHelpMenu() {
		JMenu menu = new JMenu("Help");
		menu.setMnemonic('h');
		menu.add(createAboutItem());		
		return menu;
	}

	private JMenuItem createAboutItem() {
		JMenuItem item = new JMenuItem("About");
		item.setMnemonic('a');
		item.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				showAboutDialog();
			}
		});
		return item;
	}

	private void showAboutDialog() {
		final AboutDialog dlg = new AboutDialog(this);
		dlg.setVisible(true);
	}	

	private JMenu createAddMenu() {
		JMenu addMenu = new JMenu("Add");
		addMenu.setMnemonic('a');
		addMenu.add(createAddDrugMenuItem());		
		addMenu.add(createAddEndpointMenuItem());
		addMenu.add(createAddStudyMenuItem());		
		addMenu.addSeparator();
		addMenu.add(createAddCombinedStudyItem());
		return addMenu;
	}

	private JMenuItem createAddCombinedStudyItem() {
		d_addMenuCombinedItem = createAddCombinedItem();
		return d_addMenuCombinedItem;
	}

	private JMenuItem createAddCombinedItem() {
		JMenuItem item = new JMenuItem("Combined study", getIcon(FileNames.ICON_COMBINEDSTUDY));
		item.setMnemonic('c');
		item.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				addCombinedStudy();
			}			
		});
		return item;
	}

	private void addCombinedStudy() {
		if (!checkTreeSelectionStudiesAndMultiple()) {			
			JOptionPane.showMessageDialog(this,
					"To create a combined study, select first more than 1 studies from the tree on the left.",
					"Error creating a combined study",
					JOptionPane.ERROR_MESSAGE);
		} else {
			Set<Study> list = new HashSet<Study>();
			for (TreePath path : d_leftPanelTree.getSelectionPaths()) {
				Study s = (Study) path.getLastPathComponent();
				list.add(s);
			}
			String res = JOptionPane.showInputDialog(this, "Input name for new combined study", 
					"Combined studies", JOptionPane.QUESTION_MESSAGE);
			if (res != null) {
				CombinedStudy c = new CombinedStudy(res, list);				
				d_domain.getDomain().addStudy(c);
				leftTreeFocusStudies();				
			}
		}
	}

	private boolean checkTreeSelectionStudiesAndMultiple() {
		int numSel = d_leftPanelTree.getSelectionRows().length;
		boolean allStudies = true;
		
		for (TreePath o : d_leftPanelTree.getSelectionPaths()) {
			if (!(o.getLastPathComponent() instanceof Study)) {
				allStudies = false;
				break;
			}
		}
		
		if (numSel < 2 || !allStudies) {
			return false;
		}
		return true;
	}

	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('f');
		
		fileMenu.add(createExitItem());
		return fileMenu;
	}
	
	private JMenu createEditMenu() {
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic('e');
		d_editMenuDeleteItem = createDeleteItem();
		d_editMenuDeleteItem.setEnabled(false);		
		editMenu.add(d_editMenuDeleteItem);		
		return editMenu;
	}

	private JMenuItem createDeleteItem() {
		JMenuItem item = new JMenuItem("Delete", getIcon(FileNames.ICON_DELETE));
		item.setMnemonic('d');
		item.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				deleteMenuAction();
			}			
		});

		return item;
	}

	protected void deleteMenuAction() {
		Object selected = d_leftPanelTree.getSelectionPath().getLastPathComponent();
		String selectedType = "";
		if (selected instanceof Drug) {
			selectedType = "drug ";
		} else if (selected instanceof Endpoint) {
			selectedType = "endpoint ";
		} else if (selected instanceof Study) {
			selectedType = "study ";
		}
		
		int conf = JOptionPane.showConfirmDialog(this, 
				"Do you really want to delete " + selectedType + selected + " ?",
				"Confirm deletion",					
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				getIcon(FileNames.ICON_DELETE));
		if (conf != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			if (selected instanceof Drug) {
				d_domain.getDomain().deleteDrug((Drug) selected);
			} else if (selected instanceof Endpoint) {
				d_domain.getDomain().deleteEndpoint((Endpoint) selected);
				leftTreeFocusEndpoints();
			} else if (selected instanceof Study) {
				d_domain.getDomain().deleteStudy((Study) selected);
				leftTreeFocusStudies();
			}
		} catch (DependentEntitiesException e) {
			JOptionPane.showMessageDialog(this,
					selected + " is used by " + e.getDependents()
					+ " - delete these first.",
					"Error deleting " + selected,					
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private JMenuItem createAddEndpointMenuItem() {
		JMenuItem item = new JMenuItem("Endpoint", getIcon(FileNames.ICON_ENDPOINT));
		item.setMnemonic('e');
		item.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				showAddEndpointDialog();
			}
		});
		
		return item;
	}
	
	private JMenuItem createAddStudyMenuItem() {
		JMenuItem item = new JMenuItem("Study", getIcon(FileNames.ICON_STUDY));
		item.setMnemonic('s');
		item.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				showAddStudyDialog();
			}
		});
		
		return item;
	}
	
	private JMenuItem createAddDrugMenuItem() {
		JMenuItem item = new JMenuItem("Drug", getIcon(FileNames.ICON_DRUG));
		item.setMnemonic('d');
		item.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				showAddDrugDialog();
			}

		});
		
		return item;
	}
	
	private void showAddEndpointDialog() {
		AddEndpointDialog dialog = new AddEndpointDialog(this, getDomain());
		dialog.setVisible(true);
	}
	
	private void showAddStudyDialog() {
		AddStudyDialog dialog = new AddStudyDialog(this, getDomain());
		dialog.setVisible(true);
	}
	
	private void showAddDrugDialog() {
		AddDrugDialog dialog = new AddDrugDialog(this, getDomain());
		dialog.setVisible(true);
	}
	
	public Icon getIcon(String name) {
		try {
			return imageLoader.getIcon(name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private JMenuItem createExitItem() {
		JMenuItem exitItem = new JMenuItem("Exit", getIcon(FileNames.ICON_STOP));
		exitItem.setMnemonic('e');		
		exitItem.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				quitApplication();
			}
		});
		return exitItem;
	}
	
	public void initComponents() {
		initMenu();
		initPanel();
	}
	
	private void initPanel() {
		JSplitPane pane = new JSplitPane();
		
		initLeftPanel();
		pane.setLeftComponent(d_leftPanel);
		
		initRightPanel();
		pane.setRightComponent(d_rightPanel);
		
		add(pane);
	}

	private void initLeftPanel() {
		d_domainTreeModel = new DomainTreeModel(getDomain());
		d_leftPanelTree = new JTree(d_domainTreeModel);
		d_leftPanelTree.setCellRenderer(new DomainTreeCellRenderer(imageLoader));
		d_leftPanelTree.setRootVisible(false);
		expandLeftPanelTree();
		
		d_leftPanelTree.addTreeSelectionListener(createSelectionListener());
		d_domainTreeModel.addTreeModelListener(new TreeModelListener() {
			public void treeNodesChanged(TreeModelEvent arg0) {
			}
			public void treeNodesInserted(TreeModelEvent arg0) {
			}
			public void treeNodesRemoved(TreeModelEvent arg0) {
			}
			public void treeStructureChanged(TreeModelEvent arg0) {
				expandLeftPanelTree();
			}			
		});

		d_leftPanel = new JScrollPane(d_leftPanelTree);
	}

	private void expandLeftPanelTree() {
		d_leftPanelTree.expandPath(new TreePath(new Object[]{d_domainTreeModel.getRoot(), d_domainTreeModel.getEndpointsNode()}));
		d_leftPanelTree.expandPath(new TreePath(new Object[]{d_domainTreeModel.getRoot(), d_domainTreeModel.getStudiesNode()}));
		d_leftPanelTree.expandPath(new TreePath(new Object[]{d_domainTreeModel.getRoot(), d_domainTreeModel.getDrugsNode()}));		
	}
	
	private TreeSelectionListener createSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent event) {
				Object node = ((JTree)event.getSource()).getLastSelectedPathComponent();
				if (node instanceof Study) {
					studySelected((Study)node);
				} else if (node instanceof Endpoint) {
					endpointSelected((Endpoint)node);
				} else if (node instanceof Drug) {
					drugSelected((Drug) node);
				} else {
					noneSelected();
				}
			}
		};
	}
	
	protected void noneSelected() {
		d_rightPanel.setViewportView(new JPanel());
		d_editMenuDeleteItem.setEnabled(false);
	}
	
	private void drugSelected(Drug drug) {
		d_editMenuDeleteItem.setEnabled(true);		
	}
	
	private void leftTreeFocusStudies() {
		d_leftPanelTree.setSelectionPath(new TreePath(
				new Object[] {d_domainTreeModel.getRoot(), 
						d_domainTreeModel.getStudiesNode() }));
	}
	
	private void leftTreeFocusEndpoints() {
		d_leftPanelTree.setSelectionPath(new TreePath(
				new Object[] {d_domainTreeModel.getRoot(), 
						d_domainTreeModel.getEndpointsNode() }));
	}	
	
	public void endpointSelected(Endpoint e, Study selectedStudy) {
		EndpointStudiesView view = new EndpointStudiesView(e, getDomain(), this);
		view.setSelectedStudy(selectedStudy);
		d_rightPanelBuilder = view;
		d_rightPanel.setViewportView(view.buildPanel());
		d_editMenuDeleteItem.setEnabled(true);		
	}

	public void endpointSelected(Endpoint node) {
		endpointSelected(node, null);
		d_editMenuDeleteItem.setEnabled(true);		
	}
	
	private void studySelected(Study node) {
		StudyView view = new StudyView(new PresentationModel<Study>(node), getDomain(), this, imageLoader);
		d_rightPanelBuilder = view;
		d_rightPanel.setViewportView(view.buildPanel());
		d_editMenuDeleteItem.setEnabled(true);		
	}
	
	private void initRightPanel() {
		JPanel panel = new JPanel();
		d_rightPanel = new JScrollPane(panel);
	}

	public static void main(String[] args) {
		Main frame = new Main();
		frame.initComponents();
		frame.pack();
		frame.setVisible(true);
	}
	
	private void dataModelChanged() {
		if (d_rightPanelBuilder != null) {
			d_rightPanel.setViewportView(d_rightPanelBuilder.buildPanel());
		}
	}
	
	private class MainListener implements DomainListener {
		public void drugsChanged() {
			dataModelChanged();
		}

		public void endpointsChanged() {
			dataModelChanged();
		}

		public void studiesChanged() {
			dataModelChanged();
		}
	}
}
