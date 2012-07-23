package org.drugis.addis.gui.wizard;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.common.validation.BooleanAndModel;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ValueModel;

public abstract class AbstractDoseTreatmentWizardStep extends PanelWizardStep {
	private static final long serialVersionUID = 5608736267613312255L;
	protected static final int PANEL_WIDTH = 600;
	protected List<ValueModel> d_validators = new ArrayList<ValueModel>();
	private JPanel d_dialogCache = null;
	protected final Domain d_domain;
	protected final AddisWindow d_mainWindow;
	protected final DosedDrugTreatmentPresentation d_pm;
	protected JDialog d_dialog;

	public AbstractDoseTreatmentWizardStep(DosedDrugTreatmentPresentation presentationModel, JDialog dialog) {
		this(presentationModel, null, null, null, dialog);
	}

	public AbstractDoseTreatmentWizardStep(DosedDrugTreatmentPresentation presentationModel, 
			String name, 
			String summary, JDialog dialog) {
		this(presentationModel, name, summary, null, dialog);
	}

	public AbstractDoseTreatmentWizardStep(DosedDrugTreatmentPresentation presentationModel, 
			String name, 
			String summary,
			Icon icon, JDialog dialog) {
		super(name, summary, icon);
		d_pm = presentationModel;
		d_dialog = dialog;
		d_mainWindow = Main.getMainWindow();
		d_domain = d_mainWindow.getDomain();
	}

	@Override
	public void prepare() {
		this.setVisible(false);
		initialize();
	 	buildWizardStep();
	 	BooleanAndModel valid = new BooleanAndModel(d_validators);  
	 	PropertyConnector.connectAndUpdate(valid, this, "complete");
	 	this.setVisible(true);
	}
	
	protected void initialize() {}
	
	private void buildWizardStep() {
		if(d_dialogCache == null) { 
			d_dialogCache = buildPanel();
		}
		removeAll();
		add(d_dialogCache);
	}
	
	protected void rebuildPanel() {
		setVisible(false);
		removeAll();
		d_dialogCache = buildPanel();
		add(d_dialogCache);
		setVisible(true);
	}
	
	protected abstract JPanel buildPanel();
}