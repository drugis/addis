package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.gui.components.StudyTable;
import org.drugis.addis.presentation.CharacteristicVisibleMap;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.MetaAnalysisWizardPresentation;
import org.drugis.addis.presentation.StudyCharTableModel;
import org.drugis.addis.presentation.StudyListPresentationModel;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MetaAnalysisWizard implements ViewBuilder {

	private MetaAnalysisWizardPresentation d_pm;
	
	public MetaAnalysisWizard(MetaAnalysisWizardPresentation pm) {
		d_pm = pm;
	}
	
	public Wizard buildPanel() {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIndicationWizardStep());
		wizardModel.add(new SelectEndpointWizardStep());
		wizardModel.add(new SelectDrugsWizardStep());
		Wizard wizard = new Wizard(wizardModel);
		wizard.setPreferredSize(new Dimension(800, 600));
		return wizard;
	}
	
	@SuppressWarnings("serial")
	public class SelectStudiesWizardStep extends PanelWizardStep {
		public SelectStudiesWizardStep() {
			super("Select Studies","Select an Endpoint that you want to use for this meta analysis.");
	
		}
	}
	
	@SuppressWarnings("serial")
	public class SelectDrugsWizardStep extends PanelWizardStep {

		private StudyTable d_table;
		private StudyListPresentationModel d_model;

		public SelectDrugsWizardStep() {
			super("Select two drugs","Select two drugs to be used for meta analysis.");
					
			setLayout(new BorderLayout());
			JComponent studiesComp;
		//	if(d_pm.getStudySet().isEmpty()) {
			//	studiesComp = new JLabel("No studies found.");
		//	} else {
			    d_model = new StudyListModel();
			    d_table = new StudyTable(new StudyCharTableModel(d_model));
			    
			    //JPanel pane = new JPanel();
			    //pane.setLayout(new BorderLayout());
			    //pane.add(table);
			    JScrollPane sPane = new JScrollPane(d_table);
			    sPane.setBorder(BorderFactory.createEmptyBorder());
			    //table.setPreferredScrollableViewportSize(new Dimension(400, 300));
			    
			    JScrollPane sPane2 = new JScrollPane(sPane);
			    sPane2.setPreferredSize(new Dimension(600,100));
			    
				studiesComp = sPane2;
		//	}

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
		
		
		private class StudyListModel implements StudyListPresentationModel {
			private CharacteristicVisibleMap d_characteristicVisibleMap = new CharacteristicVisibleMap();
			
			public AbstractValueModel getCharacteristicVisibleModel(
					StudyCharacteristic c) {
				return d_characteristicVisibleMap.get(c);
			}

			public ListHolder<Study> getIncludedStudies() {
				return d_pm.getStudyListModel();
			}
		}

		private JComboBox createDrugSelectionBox(ValueModel firstDrugModel) {
			JComboBox endPointBox = AuxComponentFactory.createBoundComboBox(d_pm.getDrugListModel(), firstDrugModel);
			endPointBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					// FIXME
					setComplete(true);
					//setComplete(d_pm.getEndpointModel().getValue() != null);					
				}
			});
			return endPointBox;
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
					// FIXME
					setComplete(true);
					//setComplete(d_pm.getEndpointModel().getValue() != null);					
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
