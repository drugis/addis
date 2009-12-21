package org.drugis.addis.gui;

import java.awt.Dimension;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.gui.builder.StudyAddPopulationCharacteristicView;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.presentation.StudyAddPopulationCharacteristicPresentation;
import org.drugis.addis.presentation.StudyPresentationModel;
import org.drugis.common.gui.OkCancelDialog;

@SuppressWarnings("serial")
public class StudyAddPopulationCharacteristicDialog extends OkCancelDialog {
	private StudyAddPopulationCharacteristicPresentation d_pm;
	private StudyAddPopulationCharacteristicView d_view;
	private Main d_main;
	private StudyPresentationModel d_studyModel;
	private NotEmptyValidator d_validator;	
	

	public StudyAddPopulationCharacteristicDialog(Main mainWindow,
			Domain domain, StudyPresentationModel model) {
		super(mainWindow, "Add Population Characteristic");
		this.setModal(true);
		
		d_validator = new NotEmptyValidator(d_okButton);
		
		d_pm = new StudyAddPopulationCharacteristicPresentation(model, domain);
		d_view = new StudyAddPopulationCharacteristicView(d_pm, d_validator);
		d_main = mainWindow;
		d_studyModel = model;
		
		setPreferredSize(new Dimension(400, 200));
		
		getUserPanel().removeAll();
		getUserPanel().add(d_view.buildPanel());
		pack();
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void commit() {
		d_pm.addToStudy();
		setVisible(false);
		d_main.leftTreeFocusOnStudy(d_studyModel.getBean());
	}
}
