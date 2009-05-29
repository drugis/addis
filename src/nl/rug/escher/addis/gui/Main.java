package nl.rug.escher.addis.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.DomainImpl;
import nl.rug.escher.addis.entities.DomainListener;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.common.gui.GUIHelper;
import nl.rug.escher.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;

import fi.smaa.common.ImageLoader;

@SuppressWarnings("serial")
public class Main extends JFrame {
	private JComponent d_leftPanel;
	private JScrollPane d_rightPanel;
	
	private ViewBuilder d_rightPanelBuilder;
	
	private Domain d_domain;
	
	private ImageLoader imageLoader = new ImageLoader("/resources/gfx/");

	public Main() {
		super("Escher ADDIS");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);	
		setPreferredSize(new Dimension(800, 500));
		GUIHelper.initializeLookAndFeel();
		
		d_domain = new DomainImpl();
		d_domain.addListener(new MainListener());
		
		MainData.initDefaultData(d_domain);
	}

	void showStudyAddEndpointDialog(Study study) {
		StudyAddEndpointDialog dialog = new StudyAddEndpointDialog(this, d_domain, study);
		dialog.setVisible(true);
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createAddMenu());		
		setJMenuBar(menuBar);
	}

	private JMenu createAddMenu() {
		JMenu addMenu = new JMenu("Add");
		addMenu.setMnemonic('a');
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
		AddEndpointDialog dialog = new AddEndpointDialog(this, d_domain);
		dialog.setVisible(true);
	}
	
	private void showAddStudyDialog() {
		AddStudyDialog dialog = new AddStudyDialog(this, d_domain);
		dialog.setVisible(true);
	}
	
	private void showAddDrugDialog() {
		AddDrugDialog dialog = new AddDrugDialog(this, d_domain);
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
				exit();
			}
		});
		return exitItem;
	}
	
	private void exit() {
		System.exit(0);
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
		DomainTreeModel model = new DomainTreeModel(d_domain);
		JTree tree = new JTree(model);
		tree.setCellRenderer(new DomainTreeCellRenderer(imageLoader));
		tree.setRootVisible(false);
		tree.expandPath(new TreePath(new Object[]{model.getRoot(), model.getEndpointsNode()}));
		tree.expandPath(new TreePath(new Object[]{model.getRoot(), model.getStudiesNode()}));
		
		tree.addTreeSelectionListener(createSelectionListener());
		
		d_leftPanel = tree;
	}
	
	private TreeSelectionListener createSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent event) {
				Object node = ((JTree)event.getSource()).getLastSelectedPathComponent();
				
				if (node instanceof Study) {
					studySelected((Study)node);
				} else if (node instanceof Endpoint) {
					endpointSelected((Endpoint)node);
				}
			}
		};
	}
	
	public void endpointSelected(Endpoint e, Study selectedStudy) {
		EndpointStudiesView view = new EndpointStudiesView(e, d_domain, this);
		view.setSelectedStudy(selectedStudy);
		d_rightPanelBuilder = view;
		d_rightPanel.setViewportView(view.buildPanel());
	}

	public void endpointSelected(Endpoint node) {
		endpointSelected(node, null);
	}
	
	private void studySelected(Study node) {
		StudyView view = new StudyView(new PresentationModel<Study>(node), d_domain, this);
		d_rightPanelBuilder = view;
		d_rightPanel.setViewportView(view.buildPanel());
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
