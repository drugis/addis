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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.drugis.addis.AppInfo;
import org.drugis.addis.entities.DependentEntitiesException;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.MutableStudy;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.builder.DrugView;
import org.drugis.addis.gui.builder.EndpointView;
import org.drugis.addis.gui.builder.IndicationView;
import org.drugis.addis.gui.builder.MetaStudyView;
import org.drugis.addis.gui.builder.StudyView;
import org.drugis.addis.presentation.DrugPresentationModel;
import org.drugis.addis.presentation.IndicationPresentation;
import org.drugis.addis.presentation.MetaStudyPresentationModel;
import org.drugis.addis.presentation.PresentationModelManager;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public class Main extends JFrame {
	private JComponent d_leftPanel;
	private JScrollPane d_rightPanel;
	
	private ViewBuilder d_rightPanelBuilder;
	
	private DomainManager d_domain;
	
	private ImageLoader d_imageLoader = new ImageLoader("/org/drugis/addis/gfx/");
	private DomainTreeModel d_domainTreeModel;
	private JTree d_leftPanelTree;
	private JMenuItem d_editMenuDeleteItem;
	
	private PresentationModelManager d_pmManager;
	
	public PresentationModelManager getPresentationModelManager() {
		return d_pmManager;
	}

	public Main() {
		super(AppInfo.getAppName() + " v" + AppInfo.getAppVersion());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				quitApplication();
			}
		});

		setPreferredSize(new Dimension(900, 700));
		GUIHelper.initializeLookAndFeel();
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
		
		initializeDomain();
		d_pmManager = new PresentationModelManager(d_domain.getDomain());
		
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

	public void showStudyAddEndpointDialog(MutableStudy study) {
		StudyAddEndpointDialog dialog = new StudyAddEndpointDialog(this, getDomain(), study);
		GUIHelper.centerWindow(dialog, this);		
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
		GUIHelper.centerWindow(dlg, this);
		dlg.setVisible(true);
	}	

	private JMenu createAddMenu() {
		JMenu addMenu = new JMenu("Add");
		addMenu.setMnemonic('a');
		addMenu.add(createAddIndicationMenuItem());
		addMenu.add(createAddDrugMenuItem());		
		addMenu.add(createAddEndpointMenuItem());
		addMenu.add(createAddStudyMenuItem());		
		return addMenu;
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
		d_editMenuDeleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
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
		} else if (selected instanceof MetaStudy) {
			selectedType = "meta-analysis ";
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
	
	private JMenuItem createAddIndicationMenuItem() {
		JMenuItem item = new JMenuItem("Indication", getIcon(FileNames.ICON_INDICATION));
		item.setMnemonic('i');
		item.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				showAddIndicationDialog();
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
	
	public void showAddIndicationDialog() {
		AddIndicationDialog dialog = new AddIndicationDialog(this, getDomain());
		GUIHelper.centerWindow(dialog, this);
		dialog.setVisible(true);
	}
	
	public void showAddEndpointDialog() {
		AddEndpointDialog dialog = new AddEndpointDialog(this, getDomain());
		GUIHelper.centerWindow(dialog, this);		
		dialog.setVisible(true);
	}
	
	private void showAddStudyDialog() {
		AddStudyDialog dialog = new AddStudyDialog(this, getDomain());
		GUIHelper.centerWindow(dialog, this);		
		dialog.setVisible(true);
	}
	
	public void showAddDrugDialog() {
		AddDrugDialog dialog = new AddDrugDialog(this, getDomain());
		GUIHelper.centerWindow(dialog, this);		
		dialog.setVisible(true);
	}
	
	public Icon getIcon(String name) {
		try {
			return d_imageLoader.getIcon(name);
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
		initToolbar();
	}
	
	private void initToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);

		JButton topAddStudyButton = new JButton("Add study", getIcon(FileNames.ICON_STUDY));
		topAddStudyButton.setToolTipText("Add study");
		topAddStudyButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				showAddStudyDialog();
			}
		});
		
		JButton topAddMetaStudyButton = new JButton("Create meta-analysis", getIcon(FileNames.ICON_METASTUDY));
		topAddMetaStudyButton.setToolTipText("Create meta-analysis");
		topAddMetaStudyButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Not yet implemented");
			}
		});	
		
		ButtonBarBuilder2 builder = new ButtonBarBuilder2();
		builder.addButton(topAddStudyButton);
		builder.addButton(topAddMetaStudyButton);
		toolbar.add(builder.getPanel());
		toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(toolbar, BorderLayout.NORTH);
	}

	private void initPanel() {
		JSplitPane pane = new JSplitPane();
		pane.setBorder(BorderFactory.createEmptyBorder());
		
		initLeftPanel();
		pane.setLeftComponent(d_leftPanel);
		
		initRightPanel();
		pane.setRightComponent(d_rightPanel);
		
		add(pane);
	}

	private void initLeftPanel() {
		d_domainTreeModel = new DomainTreeModel(getDomain());
		d_leftPanelTree = new JTree(d_domainTreeModel);
		d_leftPanelTree.setCellRenderer(new DomainTreeCellRenderer(d_imageLoader));
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
		d_leftPanelTree.expandPath(new TreePath(new Object[]{d_domainTreeModel.getRoot(), d_domainTreeModel.getIndicationsNode()}));
		d_leftPanelTree.expandPath(new TreePath(new Object[]{d_domainTreeModel.getRoot(), d_domainTreeModel.getEndpointsNode()}));
		d_leftPanelTree.expandPath(new TreePath(new Object[]{d_domainTreeModel.getRoot(), d_domainTreeModel.getStudiesNode()}));
		d_leftPanelTree.expandPath(new TreePath(new Object[]{d_domainTreeModel.getRoot(), d_domainTreeModel.getDrugsNode()}));
		d_leftPanelTree.expandPath(new TreePath(new Object[]{d_domainTreeModel.getRoot(), d_domainTreeModel.getAnalysesNode()}));		
	}
	
	private TreeSelectionListener createSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent event) {
				Object node = ((JTree)event.getSource()).getLastSelectedPathComponent();
				if (node instanceof MetaStudy) {
					metaStudySelected((MetaStudy)node);
				} else if (node instanceof Study) {
					studySelected((Study)node);
				} else if (node instanceof Endpoint) {
					endpointSelected((Endpoint)node);
				} else if (node instanceof Drug) {
					drugSelected((Drug) node);
				} else if (node instanceof Indication) {
					indicationSelected((Indication) node);
				} else {
					noneSelected();
				}
			}
		};
	}
	
	private void noneSelected() {
		setRightPanelView(new ViewBuilder() {
			public JComponent buildPanel() {
				return new JPanel();
			}});
	}
	
	private void drugSelected(Drug drug) {
		DrugView view = new DrugView((DrugPresentationModel) d_pmManager.getModel(drug));
		setRightPanelView(view);
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
	
	private void endpointSelected(Endpoint e, Study selectedStudy) {
		EndpointView view = new EndpointView(e, getDomain(), this);
		view.setSelectedStudy(selectedStudy);
		setRightPanelView(view);
	}
	
	private void endpointSelected(Endpoint node) {
		endpointSelected(node, null);
		d_editMenuDeleteItem.setEnabled(true);		
	}
	
	private void indicationSelected(Indication i) {
		IndicationView view = new IndicationView((IndicationPresentation) d_pmManager.getModel(i));
		setRightPanelView(view);
	}
	
	private void studySelected(Study node) {
		StudyView view = new StudyView(d_pmManager.getModel(node), getDomain(), this, d_imageLoader);
		setRightPanelView(view);		
	}
	
	private void metaStudySelected(MetaStudy node) {
		MetaStudyView view = new MetaStudyView( (MetaStudyPresentationModel) d_pmManager.getModel(node), this, d_imageLoader);
		setRightPanelView(view);		
	}

	private void setRightPanelView(ViewBuilder view) {
		d_rightPanelBuilder = view;
		d_rightPanel.setViewportView(view.buildPanel());
		d_editMenuDeleteItem.setEnabled(true);
	}
	
	private void initRightPanel() {
		JPanel panel = new JPanel();
		d_rightPanel = new JScrollPane(panel);
		d_rightPanel.getVerticalScrollBar().setUnitIncrement(16);
	}

	public static void main(String[] args) {
		Main frame = new Main();			
		frame.initComponents();
		frame.pack();
		GUIHelper.centerWindow(frame);		
		frame.setVisible(true);		
	}
	
	private void dataModelChanged() {
		if (d_rightPanelBuilder != null) {
			setRightPanelContents(d_rightPanelBuilder.buildPanel());
		}
	}

	private void setRightPanelContents(JComponent component) {
		d_rightPanel.setViewportView(component);
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

		public void indicationsChanged() {
			dataModelChanged();
		}
	}

	public void leftTreeFocusOnStudy(Study d_study) {
		d_leftPanelTree.setSelectionPath(new TreePath(
				new Object[] {d_domainTreeModel.getRoot(), 
						d_domainTreeModel.getStudiesNode(), d_study }));		
	}

	public void leftTreeFocusOnMetaStudy(MetaStudy d_study) {
		d_leftPanelTree.setSelectionPath(new TreePath(
				new Object[] {d_domainTreeModel.getRoot(), 
						d_domainTreeModel.getAnalysesNode(), d_study }));		
	}
	
	public void leftTreeFocusOnEndpoint(Endpoint ep) {
		d_leftPanelTree.setSelectionPath(new TreePath(
				new Object[] {d_domainTreeModel.getRoot(), 
						d_domainTreeModel.getEndpointsNode(), ep }));		
	}	
	
	public void leftTreeFocusOnDrug(Drug d) {
		d_leftPanelTree.setSelectionPath(new TreePath(
				new Object[] {d_domainTreeModel.getRoot(), 
						d_domainTreeModel.getDrugsNode(), d }));		
	}

	public void leftTreeFocusOnIndication(Indication indication) {
		d_leftPanelTree.setSelectionPath(new TreePath(
				new Object[] {d_domainTreeModel.getRoot(),
						d_domainTreeModel.getIndicationsNode(), indication
				} ));
		
	}

	public ImageLoader getImageLoader() {
		return d_imageLoader;
	}		
}
