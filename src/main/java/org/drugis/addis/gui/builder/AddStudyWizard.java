package org.drugis.addis.gui.builder;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultFormatter;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.ComboBoxPopupOnFocusListener;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.presentation.AddStudyWizardPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;

public class AddStudyWizard implements ViewBuilder{
	
	AddStudyWizardPresentation d_pm;
	Main d_main;
	
	public AddStudyWizard(AddStudyWizardPresentation pm, Main main) {
		d_pm = pm;
		d_main = main;
	}
	
	public Wizard buildPanel() {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new EnterIdTitleWizardStep());
		//wizardModel.add(new SelectIndicationWizardStep());
		//wizardModel.add(new EnterCharacteristicsWizardStep());
		//wizardModel.add(new SelectEndpointWizardStep());
		wizardModel.add(new SetArmsWizardStep());
		Wizard wizard = new Wizard(wizardModel);
		wizard.setDefaultExitMode(Wizard.EXIT_ON_FINISH);
		wizard.setPreferredSize(new Dimension(950, 950));
		return wizard;
	}
	
	@SuppressWarnings("serial")
	public class SetArmsWizardStep extends PanelWizardStep {
		
		private PanelBuilder d_builder;
		private NotEmptyValidator d_validator;
		
		public SetArmsWizardStep(){
			super("Select Arms","Please select the appropriate arms");			
		}
		
		 public void prepare() {
			d_validator = new NotEmptyValidator();
			Bindings.bind(this, "complete", d_validator);
			 
			 if (d_builder != null)
				 remove(d_builder.getPanel());
			 
			 buildWizardStep();
			 repaint();
		 }
		 
		private void buildWizardStep() {
			FormLayout layout = new FormLayout(
					"fill:pref, 3dlu, center:pref:grow, 3dlu, pref, 3dlu, pref",
					"p, 3dlu, p"
					);	
			d_builder = new PanelBuilder(layout);
			d_builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			int row = buildArmsPart(1, d_builder, cc, 1, layout);
			
			// add 'Add endpoint button' 
			JButton btn = new JButton("Add Arm");
			d_builder.add(btn, cc.xy(1, row+=2));
			btn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					d_pm.addEndpointModels(1);
					prepare();
				}
			});
			
			add(d_builder.getPanel());
		}

		private int buildArmsPart(int fullwidth, PanelBuilder builder,	CellConstraints cc, int row, FormLayout layout) {
			// For all the arms found in the imported study
			for(int curArmNumber = 0; curArmNumber < d_pm.getArms(); ++curArmNumber){
				LayoutUtil.addRow(layout);
				row+=2;
				
				// add 'remove arm button' 
				JButton btn = new JButton("Remove Arm");
				builder.add(btn, cc.xy(1, row));
				//btn.addActionListener(new RemoveArmListener(i));
				
				// add label
				// builder.addLabel("endpoint: ", cc.xy(3, row));
				
				// Set the drug of this arm
				JComboBox drugsBox = AuxComponentFactory.createBoundComboBox(d_pm.getDrugsModel(), d_pm.getDrugModel(curArmNumber));
				d_validator.add(drugsBox);
				builder.add(drugsBox, cc.xy(5, row));
				
				// add 'add drug button' 
				btn = GUIFactory.createPlusButton("add new drug");
				builder.add(btn, cc.xy(7, row));
				btn.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						d_main.showAddDrugDialog();
					}
				});
				
				// add size of arm
				
				// add dose
				
				// add measurement type
				
				
				
				
				// Show the notes from the imported study for the drug
				//row = addNoteField(builder, cc, 5, row, layout, d_pm.getEndpointNoteModel(i));
				
				// show note for size
				
				// show note for dose
				
				// show note for measurement type
			}
			return row;
		}
		
		
	}
	
	@SuppressWarnings("serial")
	public class SelectEndpointWizardStep extends PanelWizardStep{
		private class RemoveEndpointListener extends AbstractAction {
			int d_index;
			
			public RemoveEndpointListener(int index) {
				d_index = index;
			}
			
			public void actionPerformed(ActionEvent e) {
				d_pm.removeEndpoint(d_index);
				prepare();
			}	
		}
		
		private PanelBuilder d_builder;
		private NotEmptyValidator d_validator;
		
		public SelectEndpointWizardStep(){
			super("Select Endpoints","Please select the appropriate endpoints");
			
			
		}
		
		 public void prepare() {
			d_validator = new NotEmptyValidator();
			Bindings.bind(this, "complete", d_validator);
			 
			 if (d_builder != null)
				 remove(d_builder.getPanel());
			 
			 buildWizardStep();
			 repaint();
		 }
		 
		private void buildWizardStep() {
			FormLayout layout = new FormLayout(
					"fill:pref, 3dlu, center:pref:grow, 3dlu, pref, 3dlu, pref",
					"p, 3dlu, p"
					);	
			d_builder = new PanelBuilder(layout);
			d_builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			int row = buildEndpointsPart(1, d_builder, cc, 1, layout);
			
			// add 'Add endpoint button' 
			JButton btn = new JButton("Add Endpoint");
			d_builder.add(btn, cc.xy(1, row+=2));
			btn.addActionListener(new AbstractAction() {
				
				public void actionPerformed(ActionEvent e) {
					d_pm.addEndpointModels(1);
					prepare();
				}
			});
			
			add(d_builder.getPanel());
		}

		private int buildEndpointsPart(int fullWidth, PanelBuilder builder, CellConstraints cc, int row, FormLayout layout) {
			
			// For all the endpoints found in the imported study
			for(int i = 0; i < d_pm.getNumberEndpoints(); ++i){
				LayoutUtil.addRow(layout);
				row+=2;
				
				// add 'remove endpoint button' 
				JButton btn = new JButton("Remove Endpoint");
				builder.add(btn, cc.xy(1, row));
				btn.addActionListener(new RemoveEndpointListener(i));
				
				// add label
				builder.addLabel("endpoint: ", cc.xy(3, row));
				
				// Set the endoints from a list of options
				JComboBox endpoints = AuxComponentFactory.createBoundComboBox(d_pm.getOutcomeListModel(), d_pm.getEndpointModel(i));
				d_validator.add(endpoints);
				builder.add(endpoints, cc.xy(5, row));
				
				// add 'add endpoint button' 
				btn = GUIFactory.createPlusButton("add new endpoint");
				builder.add(btn, cc.xy(7, row));
				btn.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						d_main.showAddEndpointDialog();
					}
				});
				
				
				// Show the notes from the imported study
				row = addNoteField(builder, cc, 5, row, layout, d_pm.getEndpointNoteModel(i));
			}
			return row;
			
		}


	}
	
	
	@SuppressWarnings("serial")
	public class EnterCharacteristicsWizardStep extends PanelWizardStep{
		
		private PanelBuilder d_builder;
		private NotEmptyValidator d_validator;

		
		private Set<BasicStudyCharacteristic> excludedChars = new HashSet<BasicStudyCharacteristic>();
		
		public EnterCharacteristicsWizardStep () {
			super("Enter additional information", "Enter additional information for this study");
			
			excludedChars.add(BasicStudyCharacteristic.TITLE);
			excludedChars.add(BasicStudyCharacteristic.CREATION_DATE);
			excludedChars.add(BasicStudyCharacteristic.SOURCE);
		}
		
		 public void prepare() {
			d_validator = new NotEmptyValidator();
			Bindings.bind(this, "complete", d_validator);
			 
			 if (d_builder != null)
				 remove(d_builder.getPanel());
			 
			 buildWizardStep();
			 repaint();
		 }

		private void buildWizardStep() {
			FormLayout layout = new FormLayout(
					"fill:pref, 3dlu, center:pref:grow, 3dlu, pref",
					"p, 3dlu, p"
					);	
			d_builder = new PanelBuilder(layout);
			d_builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			buildCharacteristicsPart(3, d_builder, cc, 1, layout);
			add(d_builder.getPanel());
		}
		
		private int buildCharacteristicsPart(int fullWidth, PanelBuilder builder, CellConstraints cc, int row, FormLayout layout) {
			
			for (BasicStudyCharacteristic c : BasicStudyCharacteristic.values()) {
				if (!excludedChars.contains(c)) {
					// add characteristic field
					builder.addLabel(c.getDescription() + ":", cc.xy(1, row, "right, c"));
					builder.add(createCharacteristicComponent(c), cc.xyw(3, row,fullWidth));
					
					// add note field
					row = addNoteField(builder, cc, 3, row, layout, d_pm.getCharacteristicNoteModel(c));

					LayoutUtil.addRow(layout);
					row += 2;

				}
			}
			return row;
		}
		
		private JComponent createCharacteristicComponent(BasicStudyCharacteristic c) {
			JComponent component = null;
			if (c.getValueType() != null) {
				if (c.getValueType().equals(String.class)) {
					ValueModel model = d_pm.getCharacteristicModel(c);
					component = BasicComponentFactory.createTextField(model);
					((JTextField) component).setColumns(30);
				} else if (c.getValueType().equals(Integer.class)) {
					JFormattedTextField f = new JFormattedTextField(new DefaultFormatter() {
						@Override
						public Object stringToValue(String string) throws ParseException {
							int val = 0;
							try {
								val = Integer.parseInt(string);
							} catch (NumberFormatException e) {

							}
							if (val < 1) {
								throw new ParseException("Non-positive values not allowed", 0);
							}
							return val;
						}
					});
					Bindings.bind(f, d_pm.getCharacteristicModelAsString(c), false);
					component = f;
				} else if (c.getValueType().equals(Date.class)) {
					ValueModel mvmodel = d_pm.getCharacteristicModel(c);
					JDateChooser chooser = new JDateChooser();
					PropertyConnector.connectAndUpdate(mvmodel, chooser, "date");
					component = chooser;
				} else {
					if (c.getValueType().isEnum()) {
						try {
							
							component = createOptionsComboBox(c, c.getValueType().getEnumConstants());
						} catch (Exception e) {
							component = new JLabel("ILLEGAL CHARACTERISTIC ENUM TYPE");
						}
					}
				}
			} else {
				throw new RuntimeException("unknown characteristic type");
			}
			d_validator.add(component);
			return component;
		}
		
		private <E> JComponent createOptionsComboBox(BasicStudyCharacteristic c, E[] options) {
			ValueModel selectionHolder = d_pm.getCharacteristicModel(c);
			JComboBox component = AuxComponentFactory.createBoundComboBox(options, selectionHolder);
			ComboBoxPopupOnFocusListener.add(component);
			return component;
		}
		
	}
	
	
	@SuppressWarnings("serial")
	public class SelectIndicationWizardStep extends PanelWizardStep {
		private PanelBuilder d_builder;

		public SelectIndicationWizardStep () {
			super("Select Indications", "Select the indications for this study");
		}
		
		 public void prepare() {
			 if (d_builder != null)
				 remove(d_builder.getPanel());
			 buildWizardStep();
			 //repaint();
		 }
		 
		 public void buildWizardStep(){
			FormLayout layout = new FormLayout(
					"center:pref, 3dlu, center:pref:grow, 3dlu, center:pref",
					"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
					);	
			d_builder = new PanelBuilder(layout);
			d_builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			d_builder.addLabel("Indication",cc.xy(1, 3));	
			
			// add set indication box
			JComboBox indBox = AuxComponentFactory.createBoundComboBox(d_pm.getIndicationListModel(), d_pm.getIndicationModel());
			d_builder.add(indBox, cc.xyw(3, 3, 2));
			indBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					setComplete(d_pm.getIndicationModel().getValue() != null);					
				}
			});		
			
			// add 'add indication' button
			JButton btn = GUIFactory.createPlusButton("add new indication");
			d_builder.add(btn, cc.xy(5, 3));
			btn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					d_main.showAddIndicationDialog();
				}
			});
			
			// add note
			addNoteField(d_builder, cc, 3, 3, layout, d_pm.getIndicationNoteModel());

			add(d_builder.getPanel());
		}
	}
	
	@SuppressWarnings("serial")
	private class EnterIdTitleWizardStep extends PanelWizardStep {
		JPanel d_me = this;
		private JTextField d_idField;
		private JTextField d_titleField;
		private PanelBuilder d_builder;
		private JButton d_importButton;
		
		 public EnterIdTitleWizardStep() {
			super("Select ID and Title","Set the ID and title of the study. Studies can also be extracted from Clinicaltrials.gov using the NCT-id.");
		 }
		 
		 public void prepare() {
			 if (d_builder != null)
				 remove(d_builder.getPanel());
			 buildWizardStep();
			 checkComplete();
			 repaint();
		 }
		 
		 private void buildWizardStep() {
			 FormLayout layout = new FormLayout(
						"center:pref, 3dlu, center:pref, 3dlu, center:pref",
						"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
						);	
				d_builder = new PanelBuilder(layout);
				d_builder.setDefaultDialogBorder();
				CellConstraints cc = new CellConstraints();
				
				// add source fields
				d_builder.addLabel("Source",cc.xy(1, 1));	
				JComponent sourceSelecter = AuxComponentFactory.createBoundComboBox(BasicStudyCharacteristic.Source.values(), d_pm.getSourceModel());
				sourceSelecter.setEnabled(false);
				d_builder.add(sourceSelecter, cc.xyw(3, 1, 2));
				
				// add ID fields
				d_builder.addLabel("ID",cc.xy(1, 3));
				d_idField = BasicComponentFactory.createTextField(d_pm.getIdModel(), false);
				d_idField.setColumns(30);
				d_builder.add(d_idField, cc.xy(3, 3));
				d_idField.addCaretListener(new CompleteListener());
				
				// add import button
				d_importButton = GUIFactory.createPlusButton("enter NCT id to retrieve study data from ClinicalTrials.gov");
				d_importButton.setEnabled(false);
				d_importButton.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						d_pm.importCT(d_me);
						prepare();
					}
				});
				d_builder.add(d_importButton, cc.xy(5, 3));	
				
				// add note to ID field
				addNoteField(d_builder, cc, 3, 3, layout, d_pm.getIdNoteModel());

				// add title label
				d_builder.addLabel("Title",cc.xy(1, 7));
				d_titleField = BasicComponentFactory.createTextField(d_pm.getTitleModel(), false);
				d_titleField.setColumns(30);
				d_builder.add(d_titleField, cc.xy(3, 7));
				d_titleField.addCaretListener(new CompleteListener());
				
				// add title note
				addNoteField(d_builder, cc, 3, 7, layout, d_pm.getTitleNoteModel());
				
				// add clear button
				JButton clearButton = new JButton("clear");
				clearButton.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						d_pm.clearStudies();
						prepare();	
					}
				});
				d_builder.add(clearButton, cc.xy(5, 11));				
				
				// add panel to d_builder
				add(d_builder.getPanel());
		 }
		 
		 private class CompleteListener implements CaretListener{
			public void caretUpdate(CaretEvent arg0) {
				checkComplete();
			}
		 }
		 
		private void checkComplete() {
			setComplete( (!d_idField.getText().equals("")) && 
						 (!d_titleField.getText().equals("")   ));
			d_importButton.setEnabled(!d_idField.getText().equals(""));
		}
	}	
	
	private int addNoteField(PanelBuilder builder, CellConstraints cc,	int col, int row, FormLayout layout, ValueModel model) {
		if(model != null && model.getValue() != null && model.getValue() != ""){
			LayoutUtil.addRow(layout);
			row+=2;
			JScrollPane notePane = AuxComponentFactory.createTextArea(model, false);
			
			notePane.setWheelScrollingEnabled(true);
			builder.add(notePane, cc.xy(col, row));
		}
		return row;
	}
}
