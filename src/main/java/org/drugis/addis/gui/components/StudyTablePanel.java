package org.drugis.addis.gui.components;

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
import org.drugis.addis.presentation.StudyCharTableModel;
import org.drugis.addis.presentation.StudyListPresentationModel;
import org.drugis.common.gui.GUIHelper;

public class StudyTablePanel {

	@SuppressWarnings("serial")
	public
	static JPanel createStudyTablePanel(final StudyListPresentationModel metamodel, final Main mainWindow) {
		JPanel panel = new JPanel(new BorderLayout());
		
		StudyCharTableModel model = new StudyCharTableModel(metamodel);
		final JTable table = new StudyTable(model);
		JScrollPane pane = new JScrollPane(table);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JButton customizeButton = new JButton("Customize Shown Characteristics");
		customizeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				JDialog dialog = new CharacteristicSelectDialog(mainWindow, metamodel);
				GUIHelper.centerWindow(dialog, mainWindow);
				dialog.setVisible(true);
			}
		});
		
		panel.add(pane, BorderLayout.CENTER);
		JPanel cbp = new JPanel();
		cbp.add(customizeButton);
		panel.add(cbp, BorderLayout.SOUTH);
		return panel;
	}

}
