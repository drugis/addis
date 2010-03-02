package org.drugis.addis.gui.builder.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.gui.builder.RandomEffectsMetaAnalysisView;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.wizard.MetaAnalysisWizardPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class MetaAnalysisWizard extends Wizard {
	
	public MetaAnalysisWizard(Main parent, MetaAnalysisWizardPresentation pm) {
		super(buildModel(pm, parent));
		setDefaultExitMode(Wizard.EXIT_ON_FINISH);
		setPreferredSize(new Dimension(950, 650));
	}
	
	private static WizardModel buildModel(MetaAnalysisWizardPresentation pm, Main frame) {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIndicationWizardStep(pm));
		wizardModel.add(new SelectEndpointWizardStep(pm));
		wizardModel.add(new SelectDrugsWizardStep(pm, frame));
		SelectStudiesWizardStep selectStudiesStep = new SelectStudiesWizardStep(pm, frame);
		wizardModel.add(selectStudiesStep);
		Bindings.bind(selectStudiesStep, "complete", pm.getMetaAnalysisCompleteModel());
		wizardModel.add(new SelectArmsWizardStep(pm));
		wizardModel.add(new OverviewWizardStep(pm, frame));
		return wizardModel;
	}

	public static class OverviewWizardStep extends PanelWizardStep {
		
		private final MetaAnalysisWizardPresentation d_pm;
		private final Main d_frame;

		public OverviewWizardStep(MetaAnalysisWizardPresentation pm, Main frame) {
			super("Overview","Overview of selected Meta-analysis.");
			d_pm = pm;
			d_frame = frame;
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
			String res = JOptionPane.showInputDialog(this.getTopLevelAncestor(),
					"Input name for new analysis", 
					"Save meta-analysis", JOptionPane.QUESTION_MESSAGE);
			if (res != null) {
				try {
					d_frame.leftTreeFocus(d_pm.saveMetaAnalysis(res));
				} catch (EntityIdExistsException e) {
					JOptionPane.showMessageDialog(this.getTopLevelAncestor(), 
							"There already exists a meta-analysis with the given name, input another name",
							"Unable to save meta-analysis", JOptionPane.ERROR_MESSAGE);
					saveAsAnalysis();
				}
			} else {
				throw new InvalidStateException();
			}
		}
	}

	public static class SelectDrugsWizardStep extends PanelWizardStep {
		MetaAnalysisWizardPresentation d_pm;
		Main d_frame;

		public SelectDrugsWizardStep(MetaAnalysisWizardPresentation pm, Main frame) {
			super("Select Drugs","Select the drugs to be used for meta analysis.");
			
			d_pm = pm;
			d_frame = frame;
					
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
