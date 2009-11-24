package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.StudyTable;
import org.drugis.addis.presentation.MetaAnalysisWizardPresentation;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.RandomEffectsMetaAnalysisPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MetaAnalysisWizard implements ViewBuilder {

	private MetaAnalysisWizardPresentation d_pm;
	private Main d_frame;
	private PresentationModelFactory d_pmm;
	
	public MetaAnalysisWizard(Main parent, MetaAnalysisWizardPresentation pm, PresentationModelFactory pmm) {
		d_frame = parent;
		d_pm = pm;
		d_pmm = pmm;
	}
	
	public Wizard buildPanel() {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIndicationWizardStep());
		wizardModel.add(new SelectEndpointWizardStep());
		wizardModel.add(new SelectDrugsWizardStep());
		wizardModel.add(new SelectStudiesWizardStep());
		wizardModel.add(new OverviewWizardStep());
		Wizard wizard = new Wizard(wizardModel);
		wizard.setDefaultExitMode(Wizard.EXIT_ON_FINISH);
		wizard.setPreferredSize(new Dimension(1000, 500));
		return wizard;
	}
	
	
	@SuppressWarnings("serial")
	public class OverviewWizardStep extends PanelWizardStep {
		private RandomEffectsMetaAnalysis d_ma;
		
		public OverviewWizardStep() {
			super("Overview","Overview of selected Meta-analysis.");
		}
		
		public void prepare() {
			removeAll();
			
			if (!endpointIsRate()) {
				add(new JLabel("Meta-Analyze Not Implemented for non-rate measurements"));
			} else {
				d_ma = d_pm.createMetaAnalysis();
				ViewBuilder mav = new RandomEffectsMetaAnalysisView((RandomEffectsMetaAnalysisPresentation) d_pmm.getModel(d_ma), d_frame, true);
				add(mav.buildPanel());
				setComplete(true);
			}
		}

		private boolean endpointIsRate() {
			return ((Endpoint)d_pm.getEndpointModel().getValue()).getType().equals(Endpoint.Type.RATE);
		}
		
		public void applyState()
		throws InvalidStateException {
			saveAsStudy();
		}
		
		private void saveAsStudy() throws InvalidStateException {
			String res = JOptionPane.showInputDialog(this, "Input name for new analysis", 
					"Save meta-analysis", JOptionPane.QUESTION_MESSAGE);
			if (res != null) {
				RandomEffectsMetaAnalysis study = d_pm.saveMetaAnalysis(res, d_ma);	
				d_frame.leftTreeFocusOnMetaStudy(study);
			} else {
				throw new InvalidStateException();
			}
		}
	}
	
	@SuppressWarnings("serial")
	public class SelectStudiesWizardStep extends PanelWizardStep {

		public SelectStudiesWizardStep() {
			super("Select Studies","Select the studies to be used in the meta analysis");
	
	}
		
		public void prepare() {
			setComplete(true);
			
			removeAll();
			FormLayout layout = new FormLayout(
					"left:pref, 3dlu, left:pref",
					"p, 3dlu, p");

			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc =  new CellConstraints();
			builder.addSeparator("Studies", cc.xyw(1, 1, 3));

			int row = 3;
			for (Study s : d_pm.getStudySet()) {
				layout.appendRow(RowSpec.decode("3dlu"));
				layout.appendRow(RowSpec.decode("p"));

				JCheckBox box = BasicComponentFactory.createCheckBox(d_pm.getSelectedStudyBooleanModel(s), s.getId());
				box.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setComplete(!d_pm.getSelectedStudySet().isEmpty());						
					}
				});
				

				builder.add(box, cc.xy(1, row));

				row += 2;
			}
			
			JScrollPane sp = new JScrollPane(builder.getPanel());
			add(sp);
		}
	}
	
	
	@SuppressWarnings("serial")
	public class SelectDrugsWizardStep extends PanelWizardStep {

		private StudyTable d_table;
		
		public SelectDrugsWizardStep() {
			super("Select two drugs","Select two drugs to be used for meta analysis.");
					
			setLayout(new BorderLayout());
			JComponent studiesComp;
	
		    d_table = new StudyTable(d_pm.getStudyTableModel());
			    
		    JScrollPane sPane = new JScrollPane(d_table);
		    sPane.setPreferredSize(new Dimension(800,200));
			    
			studiesComp = sPane;

			FormLayout layout = new FormLayout(
					"center:pref",
					"p, 3dlu, p, 3dlu, fill:p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			builder.add(buildSelectDrugsPanel(), cc.xy(1, 1));			
			builder.add(BasicComponentFactory.createLabel(d_pm.getStudiesMeasuringLabelModel()),
					cc.xy(1, 3));
			builder.add(studiesComp, cc.xy(1, 5));
			builder.setBorder(BorderFactory.createEmptyBorder());
			JScrollPane sp = new JScrollPane(builder.getPanel());
		
			add(sp);
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
						
			JComboBox firstDrugBox = createDrugSelectionBox(d_pm.getFirstDrugModel());
			JComboBox secondDrugBox = createDrugSelectionBox(d_pm.getSecondDrugModel());
			
			builder.add(firstDrugBox,cc.xy(1, 3));
			builder.add(secondDrugBox,cc.xy(5, 3));
			builder.addLabel("VS",cc.xy(3, 3));
			JPanel panel = builder.getPanel();
			return panel;
		}
		
		
		private JComboBox createDrugSelectionBox(ValueModel firstDrugModel) {
			JComboBox endPointBox = AuxComponentFactory.createBoundComboBox(d_pm.getDrugListModel(), firstDrugModel);
			endPointBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					getDrugSelectionComplete();					
				}
			});
			return endPointBox;
		}

		private void getDrugSelectionComplete() {
			setComplete( (d_pm.getFirstDrugModel().getValue() != null)
						&& (d_pm.getSecondDrugModel().getValue() != null) );
		}
		
	}
	
	@SuppressWarnings("serial")
	public class SelectEndpointWizardStep extends PanelWizardStep {
		public SelectEndpointWizardStep() {
			super("Select Endpoint","Select an Endpoint that you want to use for this meta analysis.");
			JComboBox endPointBox = AuxComponentFactory.createBoundComboBox(d_pm.getEndpointListModel(), d_pm.getEndpointModel());
			add(endPointBox);
			endPointBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					setComplete(d_pm.getEndpointModel().getValue() != null);					
				}
			});			
		}
	}
	
	@SuppressWarnings("serial")
	public class SelectIndicationWizardStep extends PanelWizardStep {
		 public SelectIndicationWizardStep() {
			 super("Select Indication","Select an Indication that you want to use for this meta analysis.");
			JComboBox indBox = AuxComponentFactory.createBoundComboBox(d_pm.getIndicationListModel(), d_pm.getIndicationModel());
			add(indBox);
			indBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					setComplete(d_pm.getIndicationModel().getValue() != null);					
				}
			});
		 }
	}	
}
