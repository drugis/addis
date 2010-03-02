package org.drugis.addis.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;

import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.CharacteristicSelectDialog;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.StudyCharTableModel;
import org.drugis.addis.presentation.StudyListPresentationModel;
import org.drugis.common.gui.GUIHelper;

import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public class StudiesTablePanel extends TablePanel {
	public StudiesTablePanel(StudyListPresentationModel studyListPresentationModel, Main main) {
		super(createTable(studyListPresentationModel, main));
		
		ButtonBarBuilder2 bb = new ButtonBarBuilder2();
		bb.addButton(StudiesTablePanel.buildCustomizeButton(studyListPresentationModel, main));
		bb.addGlue();
		
		add(bb.getPanel(), BorderLayout.WEST);
	}

	public static JTable createTable(final StudyListPresentationModel studyListPM, final Main main) {
		StudyCharTableModel model = new StudyCharTableModel(studyListPM, main.getPresentationModelFactory());
		JTable table = new EnhancedTable(model);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int row = ((EnhancedTable)e.getComponent()).rowAtPoint(e.getPoint());
					Study s = studyListPM.getIncludedStudies().getValue().get(row);
					main.leftTreeFocus(s);
				}
			}
		});
		table.addKeyListener(new EntityTableDeleteListener((Main) main));
		return table;
	}

	public static JButton buildCustomizeButton(final StudyListPresentationModel studyListPM, final Main main) {
		JButton customizeButton = new JButton("Customize Shown Characteristics");
		customizeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				JDialog dialog = new CharacteristicSelectDialog(main, studyListPM);
				GUIHelper.centerWindow(dialog, main);
				dialog.setVisible(true);
			}
		});
		return customizeButton;
	}
}
