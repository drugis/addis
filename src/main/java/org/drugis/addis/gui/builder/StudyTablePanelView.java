package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.drugis.addis.gui.CharacteristicSelectDialog;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.StudyTable;
import org.drugis.addis.presentation.StudyCharTableModel;
import org.drugis.addis.presentation.StudyListPresentationModel;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.ButtonBarBuilder2;

public class StudyTablePanelView implements ViewBuilder {
	
	private Main mainWindow;
	private StudyListPresentationModel metamodel;

	public StudyTablePanelView(StudyListPresentationModel metamodel, final Main mainWindow) {
		this.mainWindow = mainWindow;
		this.metamodel = metamodel;
	}

	public JPanel buildPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		
		StudyCharTableModel model = new StudyCharTableModel(metamodel);
		final JTable table = new StudyTable(model);
		JScrollPane pane = new JScrollPane(table);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		ButtonBarBuilder2 bb = new ButtonBarBuilder2();
		bb.addButton(buildCustomizeButton());
		bb.addGlue();
		
		panel.add(pane, BorderLayout.NORTH);
		JPanel cbp = new JPanel();
		cbp.add(bb.getPanel());
		panel.add(cbp, BorderLayout.WEST);
		return panel;
	}

	@SuppressWarnings("serial")
	private JButton buildCustomizeButton() {
		JButton customizeButton = new JButton("Customize Shown Characteristics");
		customizeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				JDialog dialog = new CharacteristicSelectDialog(mainWindow, metamodel);
				GUIHelper.centerWindow(dialog, mainWindow);
				dialog.setVisible(true);
			}
		});
		return customizeButton;
	}

}
