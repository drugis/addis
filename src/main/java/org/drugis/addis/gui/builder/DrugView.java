package org.drugis.addis.gui.builder;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.presentation.DrugPresentationModel;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DrugView implements ViewBuilder{
	private DrugPresentationModel d_model;
	private JFrame d_parent;

	public DrugView(DrugPresentationModel model, JFrame parent) {
		d_model = model;
		d_parent = parent;
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
			
		JComponent studiesComp = null;
		if(d_model.getIncludedStudies().isEmpty()) {
			studiesComp = new JLabel("No studies found.");
		} else {
			StudyTablePanelView d_studyView = new StudyTablePanelView(d_model, d_parent);
			studiesComp = d_studyView.buildPanel();
		}
		builder.add(studiesComp, cc.xyw(1, 9, 3));
				
		return builder.getPanel();	
	}
}