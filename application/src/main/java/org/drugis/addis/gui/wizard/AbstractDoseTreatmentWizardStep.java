package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.common.validation.BooleanAndModel;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ValueModel;

public abstract class AbstractDoseTreatmentWizardStep extends PanelWizardStep {
	private static final long serialVersionUID = 5608736267613312255L;
	protected JPanel d_dialogPanel = new JPanel();
	protected static final int PANEL_WIDTH = 600;
	protected List<ValueModel> d_validators = new ArrayList<ValueModel>();
	private JPanel d_dialogCache = null;
	protected final Domain d_domain;
	protected final AddisWindow d_mainWindow;
	protected final DosedDrugTreatmentPresentation d_pm;

	public AbstractDoseTreatmentWizardStep(DosedDrugTreatmentPresentation presentationModel, 
			Domain domain, 
			AddisWindow mainWindow) {
		this(presentationModel, domain, mainWindow, null, null, null);
	}

	public AbstractDoseTreatmentWizardStep(DosedDrugTreatmentPresentation presentationModel, 
			Domain domain, 
			AddisWindow mainWindow, 
			String name, 
			String summary) {
		this(presentationModel, domain, mainWindow, name, summary, null);
	}

	public AbstractDoseTreatmentWizardStep(DosedDrugTreatmentPresentation presentationModel, 
			Domain domain, 
			AddisWindow mainWindow,
			String name, 
			String summary,
			Icon icon) {
		super(name, summary, icon);
		d_pm = presentationModel;
		d_domain = domain;
		d_mainWindow = mainWindow;
	}

	@Override
	public void prepare() {
		this.setVisible(false);
		initialize();
	 	buildWizardStep();
	 	BooleanAndModel valid = new BooleanAndModel(d_validators);  
	 	PropertyConnector.connectAndUpdate(valid, this, "complete");
	 	this.setVisible(true);
	 	repaint();
	}
	
	protected void initialize() {}
	
	private void buildWizardStep() {
		if(d_dialogCache == null) { 
			d_dialogCache = buildPanel();
		}
		d_dialogPanel.setLayout(new BorderLayout());
		d_dialogPanel.setPreferredSize(new Dimension(PANEL_WIDTH, 500));
		d_dialogPanel.add(d_dialogCache);
		add(d_dialogPanel, BorderLayout.CENTER);	
	}
	
	protected void rebuildPanel() {
		d_dialogPanel.setVisible(false);
		d_dialogPanel.removeAll();
		d_dialogCache = buildPanel();
		d_dialogPanel.add(d_dialogCache);
		d_dialogPanel.setVisible(true);
	}
	
	protected abstract JPanel buildPanel();
	
}