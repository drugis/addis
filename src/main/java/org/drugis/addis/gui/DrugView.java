package org.drugis.addis.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.presentation.DrugPresentationModel;
import org.drugis.addis.presentation.StudyCharTableModel;
import org.drugis.addis.presentation.StudyListPresentationModel;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DrugView implements ViewBuilder{
	private DrugPresentationModel d_model;

	public DrugView(DrugPresentationModel model) {
		d_model = model;
	}
	
	public JComponent buildPanel() {

		
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, left:pref:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Drug", cc.xyw(1, 1, 3));
		builder.addLabel("Name:", cc.xy(1, 3));
		JLabel nameComp =
			BasicComponentFactory.createLabel(d_model.getModel(Drug.PROPERTY_NAME));
		builder.add(nameComp, cc.xy(3,3));
		builder.addLabel("ATC Code:", cc.xy(1, 5));
		JLabel atcCodeComp =
			BasicComponentFactory.createLabel(d_model.getModel(Drug.PROPERTY_ATCCODE));
		builder.add(atcCodeComp, cc.xy(3, 5));
		
		builder.addSeparator("Studies measuring this drug", cc.xyw(1, 7, 3));
		
		StudyListPresentationModel studyListModel = d_model.getStudyListModel();
			
		JComponent studiesComp = null;
		if(studyListModel.getIncludedStudies().isEmpty()) {
			studiesComp = new JLabel("No studies found.");
		} else {
			StudyCharTableModel model = new StudyCharTableModel(studyListModel);
			final JTable table = new JTable(model);
			table.setPreferredScrollableViewportSize(table.getPreferredSize());
			table.setBackground(Color.WHITE);
			JScrollPane pane = new JScrollPane(table);
			pane.setBorder(BorderFactory.createEmptyBorder());
			pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			studiesComp = pane;
		}
		builder.add(studiesComp, cc.xyw(1, 9, 3));
				
		return builder.getPanel();	
	}
}