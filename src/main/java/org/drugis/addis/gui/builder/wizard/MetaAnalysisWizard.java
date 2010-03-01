package org.drugis.addis.gui.builder.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.gui.builder.RandomEffectsMetaAnalysisView;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.RandomEffectsMetaAnalysisPresentation;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.wizard.MetaAnalysisWizardPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MetaAnalysisWizard implements ViewBuilder {

	private MetaAnalysisWizardPresentation d_pm;
	private Main d_frame;
	
	public MetaAnalysisWizard(Main parent, MetaAnalysisWizardPresentation pm) {
		d_frame = parent;
		d_pm = pm;
	}
	
	public Wizard buildPanel() {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIndicationWizardStep(d_pm));
		wizardModel.add(new SelectEndpointWizardStep(d_pm));
		wizardModel.add(new SelectDrugsWizardStep());
		SelectStudiesWizardStep selectStudiesStep = new SelectStudiesWizardStep(d_pm, d_frame);
		wizardModel.add(selectStudiesStep);
		Bindings.bind(selectStudiesStep, "complete", d_pm.getMetaAnalysisCompleteModel());
		wizardModel.add(new SelectArmsWizardStep());
		wizardModel.add(new OverviewWizardStep());
		Wizard wizard = new Wizard(wizardModel);
		wizard.setDefaultExitMode(Wizard.EXIT_ON_FINISH);
		wizard.setPreferredSize(new Dimension(950, 650));
		return wizard;
	}
	
	
	@SuppressWarnings("serial")
	public class OverviewWizardStep extends PanelWizardStep {
		
		public OverviewWizardStep() {
			super("Overview","Overview of selected Meta-analysis.");
		}
		
		public void prepare() {
			removeAll();
			
			ViewBuilder mav = new RandomEffectsMetaAnalysisView(d_pm.getMetaAnalysisModel(), d_frame, true);
			add(mav.buildPanel());
			setComplete(true);
		}

		public void applyState()
		throws InvalidStateException {
			saveAsAnalysis();
		}
		
		private void saveAsAnalysis() throws InvalidStateException {
			String res = JOptionPane.showInputDialog(this, "Input name for new analysis", 
					"Save meta-analysis", JOptionPane.QUESTION_MESSAGE);
			if (res != null) {
				try {
					RandomEffectsMetaAnalysisPresentation study = d_pm.saveMetaAnalysis(res);	
					d_frame.leftTreeFocus(study.getBean());
				} catch (EntityIdExistsException e) {
					JOptionPane.showMessageDialog(this, "There already exists a meta-analysis with the given name, input another name",
							"Unable to save meta-analysis", JOptionPane.ERROR_MESSAGE);
					saveAsAnalysis();
				}
			} else {
				throw new InvalidStateException();
			}
		}
	}
	
	
	@SuppressWarnings("serial")
	public class SelectArmsWizardStep extends PanelWizardStep {
		
		private PanelBuilder d_builder;
		private FormLayout d_layout;

		public SelectArmsWizardStep (){
			super ("Select Arms","Select the specific arms to be used for the meta-analysis");
			
			d_layout = new FormLayout("3dlu, left:pref, 3dlu, pref:grow:fill", "p");	
			
			d_builder = new PanelBuilder(d_layout);
			d_builder.setDefaultDialogBorder();
		}

		@Override
		public void prepare() {
			
			remove(d_builder.getPanel());
			
			CellConstraints cc = new CellConstraints();
			
			d_builder = new PanelBuilder(d_layout);
			d_builder.setDefaultDialogBorder();
			
			int row = 1;
			for (Study curStudy : d_pm.getStudyListModel().getSelectedStudiesModel().getValue()) {
				d_builder.addSeparator(curStudy.toString(), cc.xyw(1, row, 4));
				LayoutUtil.addRow(d_layout);
				row += 2;
				
				for (Drug drug: d_pm.getSelectedDrugsModel().getValue()) {
					if (curStudy.getDrugs().contains(drug)) {
						row = createArmSelect(row, curStudy, drug, cc);
					}
				}
			}
			
			add(d_builder.getPanel());
			setComplete(true);
		}

		private int createArmSelect(int row, Study curStudy, Drug drug, CellConstraints cc) {
			d_builder.addLabel(drug.toString(), cc.xy(2, row));
			
			ListHolder<Arm> arms = d_pm.getArmsPerStudyPerDrug(curStudy, drug);

			JComboBox drugBox  = AuxComponentFactory.createBoundComboBox(arms,
					d_pm.getSelectedArmModel(curStudy, drug));
			if (arms.getValue().size() == 1)
				drugBox.setEnabled(false);

			d_builder.add(drugBox, cc.xy(4, row));
			LayoutUtil.addRow(d_layout);
			
			return row + 2;
		}
		
	}
	
	@SuppressWarnings("serial")
	public class SelectDrugsWizardStep extends PanelWizardStep {

		public SelectDrugsWizardStep() {
			super("Select Drugs","Select the drugs to be used for meta analysis.");
					
			setLayout(new BorderLayout());
			    
			FormLayout layout = new FormLayout(
					"center:pref:grow",
					"p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			builder.add(buildSelectDrugsPanel(), cc.xy(1, 1));			
			builder.add(BasicComponentFactory.createLabel(d_pm.getStudiesMeasuringLabelModel()),
					cc.xy(1, 3));
			builder.setBorder(BorderFactory.createEmptyBorder());
			builder.add(buildStudiesGraph(), cc.xy(1, 5));
			
			JScrollPane sp = new JScrollPane(builder.getPanel());
			add(sp);
			sp.getVerticalScrollBar().setUnitIncrement(16);			
			
			Bindings.bind(this, "complete", d_pm.getMetaAnalysisCompleteModel());
		}
		
		private Component buildStudiesGraph() {
			StudyGraphModel pm = d_pm.getStudyGraphModel();
			StudyGraph panel = new StudyGraph(pm);
			return panel;
		}

		private JPanel buildSelectDrugsPanel() {
			FormLayout layout = new FormLayout(
					"center:pref, 3dlu, center:pref, 3dlu, center:pref",
					"p, 3dlu, p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			
			CellConstraints cc = new CellConstraints();
			builder.addLabel("First Drug",cc.xy(1, 1));
			builder.addLabel("Second Drug",cc.xy(5, 1));
						
			JComboBox firstDrugBox = AuxComponentFactory.createBoundComboBox(d_pm.getDrugListModel(), d_pm.getFirstDrugModel());
			JComboBox secondDrugBox = AuxComponentFactory.createBoundComboBox(d_pm.getDrugListModel(), d_pm.getSecondDrugModel());
			
			builder.add(firstDrugBox,cc.xy(1, 3));
			builder.add(secondDrugBox,cc.xy(5, 3));
			builder.addLabel("VS",cc.xy(3, 3));
			JPanel panel = builder.getPanel();			
			
			return panel;
		}
	}	
}
