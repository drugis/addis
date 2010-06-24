/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.drugis.addis.AppInfo;
import org.drugis.addis.FileNames;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.DependentEntitiesException;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.DomainManager;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.EntityCategory;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.gui.builder.EntitiesNodeView;
import org.drugis.addis.gui.builder.StudiesNodeView;
import org.drugis.addis.gui.builder.ViewFactory;
import org.drugis.addis.gui.builder.wizard.AddStudyWizard;
import org.drugis.addis.gui.components.LinkLabel;
import org.drugis.addis.gui.components.StudiesTablePanel;
import org.drugis.addis.presentation.DefaultStudyListPresentationModel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.wizard.AddStudyWizardPresentation;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public class Main extends JFrame {
	private static final String DOMAIN_DEFAULT_FILENAME = "domain-"
			+ AppInfo.getAppVersion() + ".xml";
	private JComponent d_leftPanel;
	private JScrollPane d_rightPanel;
	private ViewBuilder d_rightPanelBuilder;

	private DomainManager d_domainMgr;
	private DomainTreeModel d_domainTreeModel;
	private JTree d_leftPanelTree;
	
	private JMenuItem d_editMenuDeleteItem;
	private JMenuItem d_editMenuEditItem;

	private PresentationModelFactory d_pmManager;

	public PresentationModelFactory getPresentationModelFactory() {
		return d_pmManager;
	}

	public Main() {
		super(AppInfo.getAppName() + " v" + AppInfo.getAppVersion());
		ImageLoader.setImagePath("/org/drugis/addis/gfx/");

		setPreferredSize(new Dimension(1020, 764));
		setMinimumSize(new Dimension(1020, 764));

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				quitApplication();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				setRightPanelViewSize();
			}
		});
		
		GUIHelper.initializeLookAndFeel();
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
		ToolTipManager.sharedInstance().setInitialDelay(0);

		GUIHelper.configureJFreeChartLookAndFeel();

		initializeDomain();
		d_pmManager = new PresentationModelFactory(getDomain());

	}

	protected void quitApplication() {
		try {
			saveDomainToXMLFile(DOMAIN_DEFAULT_FILENAME);
			System.exit(0);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error saving domain",
					"Error saving domain", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void saveDomainToXMLFile(String fileName) throws IOException {
		File f = new File(fileName);
		if (f.exists()) {
			f.delete();
		}

		FileOutputStream fos = new FileOutputStream(f);
		d_domainMgr.saveXMLDomain(fos);
	}

	private void initializeDomain() {
		d_domainMgr = new DomainManager();

		try {
			loadDomainFromXMLFile(DOMAIN_DEFAULT_FILENAME);
		} catch (Exception e) {
			try {
				loadDomainFromXMLResource("defaultData.xml");
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(this,
						"Error loading default data: " + e2.toString(),
						"Error loading domain", JOptionPane.ERROR_MESSAGE);
			}
		}

		getDomain().addListener(new MainListener());
	}

	public Domain getDomain() {
		return d_domainMgr.getDomain();
	}

	private void loadDomainFromXMLFile(String fileName) throws IOException,
			ClassNotFoundException {
		File f = new File(fileName);
		if (f.exists() && f.isFile()) {
			FileInputStream fis = new FileInputStream(f);
			d_domainMgr.loadXMLDomain(fis);
		} else {
			throw new FileNotFoundException(fileName + " not found");
		}
	}

	private void loadDomainFromXMLResource(String fileName) throws IOException,
			ClassNotFoundException {
		InputStream fis = Main.class.getResourceAsStream("/org/drugis/addis/"
				+ fileName);
		d_domainMgr.loadXMLDomain(fis);
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
		fileMenu.add(createImportXMLItem());
		fileMenu.add(createExportXMLItem());
		fileMenu.add(createExitItem());

		return fileMenu;
	}

	private JMenu createEditMenu() {
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic('e');
		
		d_editMenuDeleteItem = createDeleteItem();
		d_editMenuDeleteItem.setEnabled(false);
		d_editMenuDeleteItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_DELETE, 0));
		editMenu.add(d_editMenuDeleteItem);
		
		d_editMenuEditItem = createEditItem();
		d_editMenuEditItem.setEnabled(false);
		//editMenu.add(d_editMenuEditItem);
		
		return editMenu;
	}

	private JMenuItem createDeleteItem() {
		JMenuItem item = new JMenuItem("Delete", ImageLoader
				.getIcon(FileNames.ICON_DELETE));
		item.setMnemonic('d');
		item.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				deleteMenuAction();
			}
		});

		return item;
	}
	
	private JMenuItem createEditItem() {
		JMenuItem item = new JMenuItem("Edit", ImageLoader
				.getIcon(FileNames.ICON_EDIT));
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
			deleteEntity(selected);
		}
	}

	private Entity getSelectedEntity() {
		Object obj = d_leftPanelTree.getSelectionPath().getLastPathComponent();
		if (obj instanceof Entity) return (Entity) obj;
		return null;
	}
	
	protected void editMenuAction() {
		Entity selected = getSelectedEntity();
		if (selected != null && selected instanceof Study) {
			Study study = (Study) selected;
			if (getDomain().hasDependents(study)) {
				JOptionPane.showMessageDialog(Main.this,
						"The study \"" + study + "\" is used by analyses. You can't edit it.",
						"Unable to edit",
						JOptionPane.ERROR_MESSAGE);
			} else {
				showEditStudyWizard(study);
			}
		}
	}

	public void deleteEntity(Entity selected) {
		EntityCategory category = getDomain().getCategory(selected);
		String selectedType = CategoryKnowledgeFactory.getCategoryKnowledge(category).getSingular();

		int conf = JOptionPane.showConfirmDialog(this,
				"Do you really want to delete " + selectedType + " " + selected
						+ " ?", "Confirm deletion", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, ImageLoader
						.getIcon(FileNames.ICON_DELETE));
		if (conf != JOptionPane.YES_OPTION) {
			return;
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
		JMenuItem item = new JMenuItem(knowledge.getSingular(), ImageLoader.getIcon(knowledge.getNewIconName()));
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
		JDialog dialog = new JDialog((Frame) this, "Add Study", true);
		AddStudyWizardPresentation pm = new AddStudyWizardPresentation(getDomain(),
				getPresentationModelFactory(), this);
		pm.setNewStudy(study);
		deleteEntity(study);
		AddStudyWizard wizardBuilder = new AddStudyWizard(pm, this, dialog);
		Wizard wizard = wizardBuilder.buildPanel();
		dialog.getContentPane().add(wizard);
		dialog.pack();
		WizardFrameCloser.bind(wizard, dialog);
		dialog.setVisible(true);
	}

	private JMenuItem createNewItem() {
		JMenuItem newItem = new JMenuItem("New", ImageLoader
				.getIcon(FileNames.ICON_NEWFILE));
		newItem.setMnemonic('n');
		newItem.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				getDomain().clearDomain();
			}
		});
		return newItem;
	}

	private JMenuItem createImportXMLItem() {
		JMenuItem openItem = new JMenuItem("Load XML", ImageLoader
				.getIcon(FileNames.ICON_OPENFILE));
		openItem.setMnemonic('l');
		openItem.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				final JFileChooser fileChooser = new JFileChooser();
				int returnVal = fileChooser.showOpenDialog(Main.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						loadDomainFromXMLFile(fileChooser.getSelectedFile()
								.getAbsolutePath());
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(Main.this,
								"Couldn't open file "
										+ fileChooser.getSelectedFile()
												.getAbsolutePath() + " .");
					}
				}
			}
		});
		return openItem;
	}

	private JMenuItem createExportXMLItem() {
		JMenuItem saveItem = new JMenuItem("Save XML", ImageLoader
				.getIcon(FileNames.ICON_SAVEFILE));
		saveItem.setMnemonic('s');
		saveItem.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				final JFileChooser fileChooser = new JFileChooser();
				int returnVal = fileChooser.showSaveDialog(Main.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						saveDomainToXMLFile(fileChooser.getSelectedFile()
								.getAbsolutePath());
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(Main.this,
								"Couldn't save file "
										+ fileChooser.getSelectedFile()
												.getAbsolutePath() + " .");
						e1.printStackTrace();
					}
				}
			}
		});
		return saveItem;
	}

	private JMenuItem createExitItem() {
		JMenuItem exitItem = new JMenuItem("Exit", ImageLoader
				.getIcon(FileNames.ICON_STOP));
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
		toolbar.setLayout(new BorderLayout());

		ButtonBarBuilder2 builder = new ButtonBarBuilder2();
		for (EntityCategory cat : getDomain().getCategories()) {
			CategoryKnowledge knowledge = CategoryKnowledgeFactory.getCategoryKnowledge(cat);
			if (knowledge.isToolbarCategory()) {
				builder.addButton(createToolbarButton(knowledge));
			}
		}
		builder.addGlue();

		String latestVersion = AppInfo.getLatestVersion();
		if (latestVersion != null) {
			LinkLabel linkLabel = new LinkLabel(
					"<font color=\"red\">new version available</font>",
					"http://drugis.org/files/addis-" + latestVersion + ".zip");
			linkLabel.setForeground(Color.RED);
			builder.addButton(linkLabel);
			builder.addRelatedGap();
		}
		builder.addButton(GUIFactory.buildSiteLink());

		toolbar.add(builder.getPanel(), BorderLayout.CENTER);
		toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(toolbar, BorderLayout.NORTH);
	}

	private JButton createToolbarButton(final CategoryKnowledge knowledge) {
		String title = "Create " + knowledge.getSingular();
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

	public JScrollPane getRightPanel() {
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
		for (EntityCategory cat : getDomain().getCategories()) {
			d_leftPanelTree.expandPath(new TreePath(new Object[] {
					d_domainTreeModel.getRoot(), cat}));
		}
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
		if (node.getEntityClass().equals(Study.class)) {
			DefaultStudyListPresentationModel studyListPM = new DefaultStudyListPresentationModel(
					getDomain().getStudiesHolder());
			StudiesNodeView view = new StudiesNodeView(new StudiesTablePanel(
					studyListPM, this));
			setRightPanelView(view);
		} else if (node.getEntityClass().equals(Drug.class)) {
			String[] properties = { "name", "atcCode" };
			buildEntityTable(getDomain().getDrugs(), properties, knowledge.getPlural());
		} else if (node.getEntityClass().equals(Indication.class)) {
			String[] properties = { "name", "code" };
			buildEntityTable(getDomain().getIndications(), properties,
					knowledge.getPlural());
		} else if (node.getEntityClass().equals(Endpoint.class)) {
			String[] properties = { "name", "description", "unitOfMeasurement",
					"type", "direction" };
			buildEntityTable(getDomain().getEndpoints(), properties, knowledge.getPlural());
		} else if (node.getEntityClass().equals(AdverseEvent.class)) {
			String[] properties = { "name", "description", "unitOfMeasurement",
					"type", "direction" };
			buildEntityTable(getDomain().getAdverseEvents(), properties,
					knowledge.getPlural());
		} else if (node.getEntityClass().equals(PopulationCharacteristic.class)) {
			String[] properties = { "name", "description", "unitOfMeasurement",
					"type" };
			buildEntityTable(getDomain().getPopulationCharacteristics(), properties,
					knowledge.getPlural());
		} else if (node.getEntityClass().equals(MetaAnalysis.class)) {
			String[] properties = { "name", "type", "indication", "outcomeMeasure",
					"includedDrugs", "studiesIncluded", "sampleSize" };
			buildEntityTable(getDomain().getMetaAnalyses(), properties,
					knowledge.getPlural());
		} else if (node.getEntityClass().equals(BenefitRiskAnalysis.class)) {
			String[] properties = { "name", "indication", "outcomeMeasures",
					"metaAnalyses", "baseline", "drugs" };
			buildEntityTable(getDomain().getBenefitRiskAnalyses(), properties,
					knowledge.getPlural());
		}
	}

	private void nonEntitySelected() {
		d_editMenuDeleteItem.setEnabled(false);
		d_editMenuEditItem.setEnabled(false);
	}

	private void entitySelected(Entity node) {
		ViewBuilder view = ViewFactory.createView(node, d_pmManager, this);
		setRightPanelView(view);
		d_editMenuDeleteItem.setEnabled(true);
		d_editMenuEditItem.setEnabled(node instanceof Study);
	}

	private void noneSelected() {
		setRightPanelView(new ViewBuilder() {
			public JComponent buildPanel() {
				return new JPanel();
			}
		});
	}

	private <T extends Entity> void buildEntityTable(SortedSet<T> allX,
			String[] formatter, String title) {
		List<PresentationModel<T>> dpms = new ArrayList<PresentationModel<T>>();
		for (T i : allX) {
			dpms.add(d_pmManager.getModel(i));
		}
		EntitiesNodeView<T> view = new EntitiesNodeView<T>(Arrays
				.asList(formatter), dpms, this, title);
		setRightPanelView(view);
	}

	private void setRightPanelView(ViewBuilder view) {
		d_rightPanelBuilder = view;
		setRightPanelContents(view.buildPanel());
	}

	private void initRightPanel() {
		JPanel panel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		d_rightPanel = scrollPane;
	}

	public static void main(String[] args) {
		ThreadGroup threadGroup = new ThreadGroup("ExceptionGroup") {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.toString(),
						"Unexpected Error", JOptionPane.ERROR_MESSAGE);
			}
		};
		Thread mainThread = new Thread(threadGroup, "Main thread") {
			@Override
			public void run() {
				Main frame = new Main();
				frame.initComponents();
				frame.pack();
				frame.setVisible(true);

			}
		};
		mainThread.start();
	}

	private void dataModelChanged() {
		reloadRightPanel();
	}

	public void repaintRightPanel() {
		d_rightPanel.setVisible(false);
		d_rightPanel.setVisible(true);
		d_rightPanel.revalidate();
	}

	public void reloadRightPanel() {
		if (d_rightPanelBuilder != null) {
			setRightPanelContents(d_rightPanelBuilder.buildPanel());
		}
	}

	private void setRightPanelContents(JComponent component) {
		d_rightPanel.setViewportView(component);
		setRightPanelViewSize();
	}

	private void setRightPanelViewSize() {
		JComponent view = (JComponent) d_rightPanel.getViewport().getView();
		Dimension dimension = new Dimension();
		int prefWidth = getSize().width - d_leftPanel.getPreferredSize().width
				- 40;
		dimension.width = Math.max(prefWidth, view.getMinimumSize().width);
		dimension.height = view.getPreferredSize().height;
		view.setPreferredSize(dimension);
	}

	private class MainListener implements DomainListener {
		public void domainChanged(DomainEvent evt) {
			dataModelChanged();
		}
	}

	public void leftTreeFocus(Object node) {
		TreePath path = d_domainTreeModel.getPathTo(node);
		if (path != null) {
			d_leftPanelTree.setSelectionPath(path);
		}
	}
}
