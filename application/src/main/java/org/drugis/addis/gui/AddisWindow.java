/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.drugis.addis.AppInfo;
import org.drugis.addis.FileNames;
import org.drugis.addis.entities.DependentEntitiesException;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.EntityCategory;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.components.AddisScrollPane;
import org.drugis.addis.gui.components.AddisTabbedPane;
import org.drugis.addis.gui.wizard.AddStudyWizard;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.wizard.AddStudyWizardPresentation;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.FileSaveDialog;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public class AddisWindow extends JFrame {
	private JComponent d_leftPanel;
	private JPanel d_rightPanel;
	private ViewBuilder d_rightPanelBuilder;
	private DomainTreeModel d_domainTreeModel;
	private JTree d_leftPanelTree;
	
	private JMenuItem d_editMenuDeleteItem;
	private JMenuItem d_editMenuEditItem;
	private PresentationModelFactory d_pmf;

	final public static String DEFAULT_TITLE = AppInfo.getAppName() + " v" + AppInfo.getAppVersion();
	JMenuItem d_saveMenuItem;
	private final Domain d_domain;
	private Main d_main;

	public AddisWindow(final Main main, Domain domain) {
		super(DEFAULT_TITLE);
		d_domain = domain;
		d_pmf = new PresentationModelFactory(d_domain);
		d_main = main;
		d_main.getDomainChangedModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				reloadRightPanel();
				updateTitle();
			}
		});

		setIconImage(ImageLoader.getImage(FileNames.ICON_ADDIS_APP));
		
		setPreferredSize(fitDimensionToScreen(960, 800));
		setMinimumSize(new Dimension(750, 550)); // fit the screen for 800x600 resolution

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				main.quitApplication();
			}
		});
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		initComponents();
		Main.bindPrintScreen(super.getContentPane());
		updateTitle();
		d_leftPanelTree.getSelectionModel().setSelectionPath(d_domainTreeModel.getPathTo(d_domain.getCategory(Study.class)));
	}
	
	public static Dimension fitDimensionToScreen(int width, int height) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new Dimension(
				Math.min(width, screenSize.width - 20),
				Math.min(height, screenSize.height - 50));
	}
	
	public Domain getDomain() {
		return d_domain;
	}

	public void initComponents() {
		initMenu();
		initPanel();
		initToolbar();
		initStatusBar();
	}

	private void initToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setLayout(new BorderLayout());

		ButtonBarBuilder2 builder = new ButtonBarBuilder2();
		for (EntityCategory cat : getDomain().getCategories()) {
			CategoryKnowledge knowledge = CategoryKnowledgeFactory.getCategoryKnowledge(cat);
			if (knowledge.isToolbarCategory()) {
				builder.addButton(createToolbarButton(knowledge));
			}
		}

		toolbar.add(builder.getPanel(), BorderLayout.CENTER);
		toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(toolbar, BorderLayout.NORTH);
	}

	private void initStatusBar() {
		StatusBar statusBar = new StatusBar();
		statusBar.setFloatable(false);
		add(statusBar, BorderLayout.SOUTH);
	}

	private JButton createToolbarButton(final CategoryKnowledge knowledge) {
		String title = "New " + knowledge.getSingularCapitalized();
		JButton topAddStudyButton = new JButton(title,
				ImageLoader.getIcon(knowledge.getNewIconName()));
		topAddStudyButton.setToolTipText(title);
		topAddStudyButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				showAddDialog(knowledge, null);
			}
		});
		return topAddStudyButton;
	}

	private void initPanel() {
		JSplitPane pane = new JSplitPane();
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setEnabled(true);
		pane.setOneTouchExpandable(true);

		initLeftPanel();
		pane.setLeftComponent(d_leftPanel);
		d_leftPanel.setMinimumSize(new Dimension(200, d_leftPanel.getMinimumSize().height));
		
		initRightPanel();
		pane.setRightComponent(d_rightPanel);
		d_rightPanel.setMinimumSize(new Dimension(770, d_rightPanel.getMinimumSize().height));

		add(pane);
	}

	public JPanel getRightPanel() {
		return d_rightPanel;
	}

	private void initLeftPanel() {
		d_domainTreeModel = new DomainTreeModel(getDomain());
		d_leftPanelTree = new JTree(d_domainTreeModel);
		d_leftPanelTree.setCellRenderer(new DomainTreeCellRenderer(getDomain()));
		d_leftPanelTree.setRootVisible(false);
		expandLeftPanelTree();

		d_leftPanelTree.addTreeSelectionListener(new DomainTreeSelectionListener());
		d_domainTreeModel.addTreeModelListener(new TreeModelListener() {
			public void treeNodesChanged(TreeModelEvent arg0) {}
			public void treeNodesInserted(TreeModelEvent arg0) {}
			public void treeNodesRemoved(TreeModelEvent arg0) {}
			public void treeStructureChanged(TreeModelEvent arg0) {
				expandLeftPanelTree();
			}
		});

		d_leftPanel = new JScrollPane(d_leftPanelTree);
	}

	private void expandLeftPanelTree() {
		for (EntityCategory cat : getDomain().getCategories()) {
			d_leftPanelTree.expandPath(new TreePath(new Object[] {d_domainTreeModel.getRoot(), cat}));
		}
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
		for (EntityCategory cat : getDomain().getCategories()) {
			addMenu.add(createAddMenuItem(CategoryKnowledgeFactory.getCategoryKnowledge(cat)));
		}
		return addMenu;
	}

	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('f');

		fileMenu.add(createNewItem());
		fileMenu.add(createLoadItem());
		fileMenu.add(createSaveItem());
		fileMenu.add(createSaveAsItem());
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
		
		d_editMenuEditItem = createEditItem();
		d_editMenuEditItem.setEnabled(false);
		editMenu.add(d_editMenuEditItem);

		if(AppInfo.getAppVersion().equals(AppInfo.APPVERSIONFALLBACK)) {
			JMenuItem menuItem = new JMenuItem("Generate error");
			editMenu.add(menuItem);
			
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					throw new RuntimeException("Test exception");
				}
			});
		}
		return editMenu;
	}

	private JMenuItem createDeleteItem() {
		JMenuItem item = new JMenuItem("Delete", ImageLoader.getIcon(FileNames.ICON_DELETE));
		item.setMnemonic('d');
		item.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				deleteMenuAction();
			}
		});

		return item;
	}
	
	private JMenuItem createEditItem() {
		JMenuItem item = new JMenuItem("Edit", ImageLoader.getIcon(FileNames.ICON_EDIT));
		item.setMnemonic('e');
		item.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				editMenuAction();
			}
		});

		return item;
	}

	protected void deleteMenuAction() {
		Entity selected = getSelectedEntity();
		if (selected != null) {
			deleteEntity(selected, true);
		}
	}

	private Entity getSelectedEntity() {
		Object obj = d_leftPanelTree.getSelectionPath().getLastPathComponent();
		if (obj instanceof Entity) return (Entity) obj;
		return null;
	}
	
	protected void editMenuAction() {
		Entity selected = getSelectedEntity();
		if (selected == null) {
			
		} else if (selected instanceof Study) {
			Study study = (Study) selected;
			if (getDomain().hasDependents(study)) {
				JOptionPane.showMessageDialog(this,
						"The study \"" + study + "\" is used by analyses. You can't edit it.",
						"Unable to edit",
						JOptionPane.ERROR_MESSAGE);
			} else {
				showEditStudyWizard(study);
			}
		} else if (selected instanceof Indication) {
			Indication indication = (Indication) selected;
			showEditIndicationWizard(indication);
		}
	}

	private void showEditIndicationWizard(Indication indication) {
		// TODO Auto-generated method stub
		
	}

	private JMenuItem createLoadItem() {
		JMenuItem openItem = new JMenuItem("Load", ImageLoader
				.getIcon(FileNames.ICON_OPENFILE));
		openItem.setMnemonic('l');
		openItem.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if(d_main.isDataChanged()) {
					if(saveChangesDialog()) {
						d_main.fileLoadActions();
					}
				} else {
					d_main.fileLoadActions();
				}
			}
		});
		return openItem;
	}
	

	protected boolean saveChangesDialog(){
		boolean yesNoClicked = false;
		int action = JOptionPane.showConfirmDialog(AddisWindow.this, "Do you want to save changes?", 
				"File contents changed", JOptionPane.YES_NO_CANCEL_OPTION);
		switch(action) {
			case JOptionPane.YES_OPTION : {
				try {
					if (d_main.getCurFilename() == null) {				
						MainFileSaveDialog dialog = new MainFileSaveDialog(AddisWindow.this, "xml", "XML files");
						dialog.saveActions();
						yesNoClicked = true;
					} else {
						d_main.saveDomainToFile(d_main.getCurFilename());
						yesNoClicked = true;
					}										
				} catch (Exception e) {
					JOptionPane.showMessageDialog(AddisWindow.this, "Error saving domain", "Error saving domain", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			} break;
			case JOptionPane.NO_OPTION : {
				yesNoClicked = true;
			} break;
		}
		return yesNoClicked;
	}
	
	private final class MainFileSaveDialog extends FileSaveDialog {
		private MainFileSaveDialog(Component frame, String extension,
				String description) {
			super(frame, extension, description);
		}

		@Override
		public void doAction(String path, String extension) {
			d_main.saveDomainToFile(path);
		}
	}
	
	private JMenuItem createSaveItem() {
		d_saveMenuItem = new JMenuItem("Save", ImageLoader.getIcon(FileNames.ICON_SAVEFILE));
		d_saveMenuItem.setMnemonic('s');
		Bindings.bind(d_saveMenuItem, "enabled", d_main.getDomainChangedModel());
		
		// Attach to ctrl-s
		d_saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		
		d_saveMenuItem.addActionListener(new AbstractAction() {
			
			public void actionPerformed(ActionEvent e) {
				if (d_main.getCurFilename() == null) {				
					MainFileSaveDialog dialog = new MainFileSaveDialog(AddisWindow.this, "addis", "ADDIS data files");
					dialog.saveActions();
				} else {
					d_main.saveDomainToFile(d_main.getCurFilename());
				}
			}
		});
		
		return d_saveMenuItem;
	}
	
	private JMenuItem createSaveAsItem() {
		JMenuItem saveItem = new JMenuItem("Save As", ImageLoader.getIcon(FileNames.ICON_SAVEFILE));
		
		// attach to ctrl-shift-s
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		//align "Ctrl+Shift+S" text in the menu to the right
		saveItem.setAlignmentX(LEFT_ALIGNMENT);
		
		saveItem.setMnemonic('a');

		saveItem.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				MainFileSaveDialog dialog = new MainFileSaveDialog(AddisWindow.this, "addis", "ADDIS data files");
				dialog.saveActions();
			}
		});
		return saveItem;
	}
	

	private JMenuItem createExitItem() {
		JMenuItem exitItem = new JMenuItem("Exit", ImageLoader.getIcon(FileNames.ICON_STOP));
		exitItem.setMnemonic('e');
		exitItem.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				d_main.quitApplication();
			}
		});
		return exitItem;
	}

	private class DomainTreeSelectionListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent event) {
			Object node = ((JTree) event.getSource()).getLastSelectedPathComponent();
			if (node == null || !(node instanceof Entity)) {
				nonEntitySelected();
			}

			if (node == null) {
				noneSelected();
			} else if (node instanceof Entity) {
				entitySelected((Entity) node);
			} else if (node instanceof EntityCategory) {
				categorySelected((EntityCategory) node);
			} else {
				noneSelected();
			}
		}
	};
	
	private void categorySelected(EntityCategory node) {
		CategoryKnowledge knowledge = CategoryKnowledgeFactory.getCategoryKnowledge(node);
		setRightPanelView(knowledge.getCategoryViewBuilder(this, getDomain()));
	}

	private void nonEntitySelected() {
		d_editMenuDeleteItem.setEnabled(false);
		d_editMenuEditItem.setEnabled(false);
	}

	private void entitySelected(Entity entity) {
		ViewBuilder view = getEntityKnowledge(entity).getEntityViewBuilder(this, getDomain(), entity);
		setRightPanelView(view);
		d_editMenuDeleteItem.setEnabled(true);
		d_editMenuEditItem.setEnabled(entity instanceof Study);
	}

	private void initRightPanel() {
		d_rightPanel = new JPanel(new BorderLayout());
		d_rightPanel.setOpaque(true);
	}
	

	public void setRightPanelView(ViewBuilder view) {
		d_rightPanelBuilder = view;
		setRightPanelContents(view.buildPanel());
	}
	

	public void repaintRightPanel() {
		d_rightPanel.setVisible(false);
		d_rightPanel.setVisible(true);
		d_rightPanel.revalidate();
	}

	public void reloadRightPanel() {
		if (d_rightPanelBuilder != null) {
			d_rightPanel.setVisible(false);
			setRightPanelContents(d_rightPanelBuilder.buildPanel());
			d_rightPanel.setVisible(true);	
		}
	}

	private void setRightPanelContents(JComponent component) {
		d_rightPanel.setVisible(false);
		d_rightPanel.removeAll();
		d_rightPanel.add(encapsulate(component), BorderLayout.CENTER);
		d_rightPanel.setVisible(true);
	}

	
	private Component encapsulate(JComponent component) {
		if (!(component instanceof AddisTabbedPane)) {
			return new AddisScrollPane(component);
		}
		return component;
	}

	public void leftTreeFocus(Object node) {
		TreePath path = d_domainTreeModel.getPathTo(node);
		if (path != null) {
			d_leftPanelTree.setSelectionPath(path);
		}
	}
	
	private void noneSelected() {
		setRightPanelView(new ViewBuilder() {
			public JComponent buildPanel() {
				return new JPanel();
			}
		});
	}
	
	private CategoryKnowledge getEntityKnowledge(Entity entity) {
		CategoryKnowledge knowledge = CategoryKnowledgeFactory.getCategoryKnowledge(
				getDomain().getCategory(entity));
		return knowledge;
	}


	public void deleteEntity(Entity selected, boolean confirmation) {
		String selectedType = getEntityKnowledge(selected).getSingularCapitalized();

		if (confirmation) {
			int conf = JOptionPane.showConfirmDialog(this,
					"Do you really want to delete " + selectedType + " " + selected
					+ " ?", "Confirm deletion", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, ImageLoader
					.getIcon(FileNames.ICON_DELETE));
			if (conf != JOptionPane.YES_OPTION) {
				return;
			}
		}
		try {
			getDomain().deleteEntity(selected);
		} catch (DependentEntitiesException e) {
			String str = new String(selected + " is used by: ");
			for (Entity en : e.getDependents()) {
				str += "\n\t" + en;
			}
			str += "\n - delete these first.";
			JTextArea text = new JTextArea(str);
			text.setWrapStyleWord(true);
			text.setLineWrap(true);
			text.setMargin(new Insets(5, 5, 5, 5));
			JScrollPane sp = new JScrollPane(text);
			sp.setPreferredSize(new Dimension(300, 200));
			JOptionPane.showMessageDialog(this, sp, "Error deleting "
					+ selected, JOptionPane.ERROR_MESSAGE);
		}
	}

	private JMenuItem createAddMenuItem(final CategoryKnowledge knowledge) {
		JMenuItem item = new JMenuItem(knowledge.getSingularCapitalized(), ImageLoader.getIcon(knowledge.getNewIconName()));
		item.setMnemonic(knowledge.getMnemonic());
		item.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				showAddDialog(knowledge, null);
			}
		});
		return item;
	}

	public void showAddDialog(CategoryKnowledge knowledge, ValueModel selectionModel) {
		JDialog dialog = knowledge.getAddDialog(this, getDomain(), selectionModel);
		GUIHelper.centerWindow(dialog, this);
		dialog.setVisible(true);
	}

	private void showEditStudyWizard(Study study) {
		JDialog dialog = new JDialog((Frame) this, "Edit Study", true);
		AddStudyWizardPresentation pm = new AddStudyWizardPresentation(getDomain(), d_pmf, this, study);
		
		leftTreeFocus(d_domainTreeModel.getRoot());
		AddStudyWizard wizard = new AddStudyWizard(pm, this, dialog);
		dialog.getContentPane().add(wizard);
		dialog.pack();
		WizardFrameCloser.bind(wizard, dialog);
		Main.bindPrintScreen(wizard);
		dialog.setVisible(true);
	}

	private JMenuItem createNewItem() {
		JMenuItem newItem = new JMenuItem("New", ImageLoader
				.getIcon(FileNames.ICON_FILE_NEW));
		newItem.setMnemonic('n');
		newItem.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				if(d_main.isDataChanged()) {
					if(saveChangesDialog()){
						d_main.newFileActions();
					}
				} else {
					d_main.newFileActions();
				}
			}
		});
		return newItem;
	}
	
	
	public void updateTitle() {
		setTitle(DEFAULT_TITLE + " - " + d_main.getDisplayName() + (d_main.getDomainChangedModel().getValue() ? "*" : ""));
	}

	public PresentationModelFactory getPresentationModelFactory() {
		return d_pmf;
	}
}
