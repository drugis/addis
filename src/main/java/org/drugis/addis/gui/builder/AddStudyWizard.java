package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultFormatter;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.ComboBoxPopupOnFocusListener;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.presentation.AddStudyWizardPresentation;
import org.drugis.addis.presentation.DosePresentationModel;
import org.drugis.addis.presentation.MeasurementTable;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;

public class AddStudyWizard implements ViewBuilder{
	
	public static final String DEFAULT_NOTETITLE = "CT.gov description:";
	AddStudyWizardPresentation d_pm;
	Main d_main;
	private JDialog d_dialog;
	
	public AddStudyWizard(AddStudyWizardPresentation pm, Main main, JDialog frame) {
		d_pm = pm;
		d_main = main;
		d_dialog = frame;
	}
	
	public Wizard buildPanel() {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new EnterIdTitleWizardStep());
		wizardModel.add(new SelectIndicationWizardStep());
		wizardModel.add(new EnterCharacteristicsWizardStep());
		wizardModel.add(new SelectEndpointWizardStep());
		wizardModel.add(new SetArmsWizardStep());
		wizardModel.add(new SetMeasurementsWizardStep());
		Wizard wizard = new Wizard(wizardModel);
		wizard.setDefaultExitMode(Wizard.EXIT_ON_FINISH);
		wizard.setPreferredSize(new Dimension(950, 950));
		return wizard;
	}
	
	@SuppressWarnings("serial")
	public class SetMeasurementsWizardStep extends PanelWizardStep {
		private JScrollPane d_scrollPane;
		
		public SetMeasurementsWizardStep(){
			super("Set Measurements","Please enter the measurements for all arm-endpoint combinations.");			
		} 
		
		public void prepare() {
			this.setVisible(false);
			if (d_scrollPane != null)
				 remove(d_scrollPane);
			 
			 buildWizardStep();
			 this.setVisible(true);
			 repaint();
			 setComplete(true);
		}
		
		public void applyState()
		throws InvalidStateException {
			d_pm.saveStudy();
		}
		
		private void buildWizardStep() {
			this.setLayout(new BorderLayout());
			TableModel tableModel = d_pm.getMeasurementTableModel();
			JTable table = new MeasurementTable(tableModel,(Window) d_dialog);
			d_scrollPane = new JScrollPane(table);
			add(d_scrollPane, BorderLayout.CENTER);
		}
	}
	
	@SuppressWarnings("serial")
	public class SetArmsWizardStep extends PanelWizardStep {
		private class NewDrugButtonListener implements ActionListener{
			int d_index;

			public NewDrugButtonListener(int index) {
				d_index = index;
			}
			
			public void actionPerformed(ActionEvent e) {
				d_main.showAddDrugDialog(d_pm.getArmModel(d_index).getModel(Arm.PROPERTY_DRUG));
			}
		}
		private class RemoveArmListener extends AbstractAction {
			int d_index;
			
			public RemoveArmListener(int index) {
				d_index = index;
			}
			
			public void actionPerformed(ActionEvent e) {
				d_pm.removeArm(d_index);
				prepare();
			}	
		}
		
		private PanelBuilder d_builder;
		private NotEmptyValidator d_validator;
		private JScrollPane d_scrollPane;
		
		public SetArmsWizardStep(){
			super("Select Arms","Please select the appropriate arms");			
		}
		
		 public void prepare() {
			 this.setVisible(false);
			 d_validator = new NotEmptyValidator();
			 d_validator.addValueChangeListener(new CompleteListener(this));
			 
			 if (d_scrollPane != null)
				 remove(d_scrollPane);
			 
			 buildWizardStep();
			 this.setVisible(true);
			 repaint();
		 }
		 
		private void buildWizardStep() {// 300px
			FormLayout layout = new FormLayout(
					"fill:pref, 3dlu, 300px, 3dlu,  right:pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref",
					"p, 3dlu, p, 3dlu, p"
					);	
			d_builder = new PanelBuilder(layout);
			d_builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			// Labels
			d_builder.add(new JLabel("Drugs:"),cc.xy(3, 1));
			d_builder.add(new JLabel("Dose:"),cc.xy(7, 1));
			d_builder.add(new JLabel("Size:"),cc.xy(13, 1));
			
			int row = buildArmsPart(1, d_builder, cc, 3, layout);
			
			// add 'Add Arm' button 
			JButton btn = new JButton("Add Arm");
			d_builder.add(btn, cc.xy(1, row+=2));
			btn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					d_pm.addArmModels(1);
					prepare();
				}
			});
			
			JPanel panel = d_builder.getPanel();
			this.setLayout(new BorderLayout());
			d_scrollPane = new JScrollPane(panel);
		
			add(d_scrollPane, BorderLayout.CENTER);
		}

		private int buildArmsPart(int fullwidth, PanelBuilder builder,	CellConstraints cc, int row, FormLayout layout) {
			// For all the arms found in the imported study
			for(int curArmNumber = 0; curArmNumber < d_pm.getNumberArms(); ++curArmNumber){
				LayoutUtil.addRow(layout);
				row+=2;
				
				// add 'remove arm button' 
				JButton btn = new JButton("Remove Arm");
				builder.add(btn, cc.xy(1, row));
				btn.addActionListener(new RemoveArmListener(curArmNumber));
				
				// Set the drug of this arm
				JComboBox drugsBox = AuxComponentFactory.createBoundComboBox(d_pm.getDrugsModel(), d_pm.getArmModel(curArmNumber).getModel(Arm.PROPERTY_DRUG));
				d_validator.add(drugsBox);
				builder.add(drugsBox, cc.xy(3, row));
				
				// add 'add drug button' 
				btn = GUIFactory.createPlusButton("add new drug");
				builder.add(btn, cc.xy(5, row));
				btn.addActionListener(new NewDrugButtonListener(curArmNumber));
				
				// add min dose
				DosePresentationModel doseModel = d_pm.getArmModel(curArmNumber).getDoseModel();
				JTextField minDoseField =  new JFormattedTextField(new DefaultFormatter());
				PropertyConnector.connectAndUpdate(doseModel.getMinModel(), minDoseField, "value");
				minDoseField.setColumns(2);
				d_validator.add(minDoseField);
				builder.add(minDoseField, cc.xy(7, row));
				
				// add max dose
				JTextField maxDoseField = new JFormattedTextField(new DefaultFormatter());
				PropertyConnector.connectAndUpdate(doseModel.getMaxModel(), maxDoseField, "value");
				maxDoseField.setColumns(2);
				d_validator.add(maxDoseField);
				builder.add(maxDoseField, cc.xy(9, row));
				
				// add dose unit
				JComboBox doseUnitBox = AuxComponentFactory.createBoundComboBox(SIUnit.values(), doseModel.getUnitModel());
				d_validator.add(doseUnitBox);
				builder.add(doseUnitBox, cc.xy(11, row));
				
				// add group size
				JTextField sizeField =  new JFormattedTextField(new DefaultFormatter());
				PropertyConnector.connectAndUpdate(d_pm.getArmModel(curArmNumber).getModel(Arm.PROPERTY_SIZE), sizeField, "value");
				sizeField.setColumns(2);
				d_validator.add(sizeField);
				builder.add(sizeField, cc.xy(13, row));
				
				// Show the notes from the imported study for the drug
				row = addNoteField(builder, cc, row, 3, 11, layout, d_pm.getArmNoteModel(curArmNumber), DEFAULT_NOTETITLE);
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
		
		private class NewEndpointButtonListener implements ActionListener{
			int d_index;

			public NewEndpointButtonListener(int index) {
				d_index = index;
			}
			
			public void actionPerformed(ActionEvent e) {
				d_main.showAddEndpointDialog(d_pm.getEndpointModel(d_index));
			}
		}
		
		private PanelBuilder d_builder;
		private NotEmptyValidator d_validator;
		private JScrollPane d_scrollPane;
		
		public SelectEndpointWizardStep(){
			super("Select Endpoints","Please select the appropriate endpoints");
			this.setLayout(new BorderLayout());
		}
		
		 public void prepare() {
			 this.setVisible(false);
 			 d_validator = new NotEmptyValidator();
			 d_validator.addValueChangeListener(new CompleteListener(this));
			 
			 if (d_scrollPane != null)
				 remove(d_scrollPane);
			 
			 buildWizardStep();
			 this.setVisible(true);
			 repaint();
		 }
		 
		private void buildWizardStep() {
			FormLayout layout = new FormLayout(
					"center:pref, 3dlu, right:pref, 3dlu, fill:pref:grow, 3dlu, left:pref",
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
		
			JPanel panel = d_builder.getPanel();
			d_scrollPane = new JScrollPane(panel);
		
			add(d_scrollPane, BorderLayout.CENTER);
		}

		private int buildEndpointsPart(int fullWidth, PanelBuilder builder, CellConstraints cc, int row, FormLayout layout) {
			
			// For all the endpoints found in the imported study
			for(int i = 0; i < d_pm.getNumberEndpoints(); ++i){
				LayoutUtil.addRow(layout);
				row+=2;
				
				// add 'remove endpoint' button
				JButton btn = new JButton("Remove Endpoint");
				builder.add(btn, cc.xy(1, row));
				btn.addActionListener(new RemoveEndpointListener(i));
				
				// add label
				builder.addLabel("endpoint: ", cc.xy(3, row));
				
				// Set the endoints from a list of options
				JComboBox endpoints = AuxComponentFactory.createBoundComboBox(d_pm.getEndpointListModel(), d_pm.getEndpointModel(i));
				d_validator.add(endpoints);
				builder.add(endpoints, cc.xy(5, row));
				
				// add 'add endpoint button' 
				btn = GUIFactory.createPlusButton("add new endpoint");
				builder.add(btn, cc.xy(7, row));
				btn.addActionListener(new NewEndpointButtonListener(i));
				
				
				// Show the notes from the imported study
				row = addNoteField(builder, cc, row, 5, 1, layout, d_pm.getEndpointNoteModel(i), DEFAULT_NOTETITLE);
			}
			return row;	
		}
	}
	
	
	@SuppressWarnings("serial")
	public class EnterCharacteristicsWizardStep extends PanelWizardStep{
		
		private PanelBuilder d_builder;
		private NotEmptyValidator d_validator;
		private JScrollPane d_scrollPane;

		private Set<BasicStudyCharacteristic> excludedChars = new HashSet<BasicStudyCharacteristic>();		
		
		public EnterCharacteristicsWizardStep () {
			super("Enter additional information", "Enter additional information for this study");
			
			excludedChars.add(BasicStudyCharacteristic.TITLE);
			excludedChars.add(BasicStudyCharacteristic.CREATION_DATE);
			excludedChars.add(BasicStudyCharacteristic.SOURCE);
		}
		
		public void prepare() {
			 d_validator = new NotEmptyValidator();
			 d_validator.addValueChangeListener(new CompleteListener(this));
			 
			 if (d_scrollPane != null)
				 remove(d_scrollPane);
			 
			 buildWizardStep();
			 repaint(); 
		}

		private void buildWizardStep() {
			FormLayout layout = new FormLayout(
					"right:pref, 3dlu, fill:pref:grow, 3dlu, pref",
					"p, 3dlu, p"
					);	
			d_builder = new PanelBuilder(layout);
			d_builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			buildCharacteristicsPart(3, d_builder, cc, 1, layout);
			
			JPanel panel = d_builder.getPanel();
			this.setLayout(new BorderLayout());
			d_scrollPane = new JScrollPane(panel);
		
			add(d_scrollPane, BorderLayout.CENTER);
		}
		
		private int buildCharacteristicsPart(int fullWidth, PanelBuilder builder, CellConstraints cc, int row, FormLayout layout) {
			
			for (BasicStudyCharacteristic c : BasicStudyCharacteristic.values()) {
				if (!excludedChars.contains(c)) {
					// add characteristic field
					builder.addLabel(c.getDescription() + ":", cc.xy(1, row/*, "right, c"*/));
					builder.add(createCharacteristicComponent(c), cc.xyw(3, row,fullWidth));
					
					// add note field
					row = addNoteField(builder, cc, row, 3, 1, layout, d_pm.getCharacteristicNoteModel(c), DEFAULT_NOTETITLE);

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
					component = AuxComponentFactory.createTextArea(model, true);
				} else if (c.getValueType().equals(Integer.class)) {
					component = AuxComponentFactory.createNonNegativeIntegerTextField(d_pm.getCharacteristicModel(c));
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
		private NotEmptyValidator d_validator;
		private JScrollPane d_scrollPane;

		public SelectIndicationWizardStep () {
			super("Select Indications", "Select the indications for this study");
		}
		
		 public void prepare() {
			 this.setVisible(false);
			 d_validator = new NotEmptyValidator();
			 d_validator.addValueChangeListener(new CompleteListener(this));
			 
			 if (d_scrollPane != null)
				 remove(d_scrollPane);
			 buildWizardStep();
			 this.setVisible(true);
			 repaint();
		 }
		 
		 public void buildWizardStep(){
			FormLayout layout = new FormLayout(
					"right:pref, 3dlu, left:pref:grow, 3dlu, left:pref",
					"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
					);	
			d_builder = new PanelBuilder(layout);
			d_builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			d_builder.addLabel("Indication",cc.xy(1, 3));	
			
			// add set indication box
			JComboBox indBox = AuxComponentFactory.createBoundComboBox(d_pm.getIndicationListModel(), d_pm.getIndicationModel());
			d_builder.add(indBox, cc.xyw(3, 3, 2));
			d_validator.add(indBox);
			
			// add 'add indication' button
			JButton btn = GUIFactory.createPlusButton("add new indication");
			d_builder.add(btn, cc.xy(5, 3));
			btn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					d_main.showAddIndicationDialog(d_pm.getIndicationModel());
				}
			});
			
			// add note
			addNoteField(d_builder, cc, 3, 3, 1, layout, d_pm.getIndicationNoteModel(), DEFAULT_NOTETITLE);

			this.setLayout(new BorderLayout());
			d_scrollPane = new JScrollPane(d_builder.getPanel());
		
			add(d_scrollPane, BorderLayout.CENTER);
		}
	}
	
	@SuppressWarnings("serial")
	private class EnterIdTitleWizardStep extends PanelWizardStep {
		JPanel d_me = this;
		private JTextField d_idField;
		private JComponent d_titleField;
		private PanelBuilder d_builder;
		private JButton d_importButton;
		private NotEmptyValidator d_validator;
		private JScrollPane d_scrollPane;
		
		 public EnterIdTitleWizardStep() {
			super("Select ID and Title","Set the ID and title of the study. Studies can also be extracted from Clinicaltrials.gov using the NCT-id.");
		 }

		 public void prepare() {
			 this.setVisible(false);
			 d_validator = new NotEmptyValidator();
			 d_validator.addValueChangeListener(new CompleteListener(this));
			 
			 if (d_scrollPane != null)
				 remove(d_scrollPane);
			 
			 buildWizardStep();
			 this.setVisible(true);
			 repaint();
		 }
		 
		 private void buildWizardStep() {
			 FormLayout layout = new FormLayout(
						"right:pref, 3dlu, left:pref:grow, 3dlu, left:pref",
						"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
						);	
				d_builder = new PanelBuilder(layout);
				d_builder.setDefaultDialogBorder();
				CellConstraints cc = new CellConstraints();
				
				// add source fields
				d_builder.addLabel("Source:",cc.xy(1, 1));	
				JComponent sourceSelecter = AuxComponentFactory.createBoundComboBox(BasicStudyCharacteristic.Source.values(), d_pm.getSourceModel());
				sourceSelecter.setEnabled(false);
				d_builder.add(sourceSelecter, cc.xyw(3, 1, 2));
				
				// add ID fields
				d_builder.addLabel("ID:",cc.xy(1, 3));
				d_idField = BasicComponentFactory.createTextField(d_pm.getIdModel(), false);
				d_idField.setColumns(30);
				d_validator.add(d_idField);
				d_builder.add(d_idField, cc.xy(3, 3));
				d_idField.addCaretListener(new ImportButtonEnableListener());
				d_idField.addFocusListener(new FocusListener() {
					
					public void focusLost(FocusEvent e) {
						if (!d_pm.checkID()){
							JOptionPane.showMessageDialog(d_main, "WARNING: There is already a study with this ID in the domain."+ d_pm.getIdModel().getValue());
							setComplete(false);
						}
					}
					
					public void focusGained(FocusEvent e) {
					}
				});
				
				// add import button
				d_importButton = GUIFactory.createPlusButton("enter NCT id to retrieve study data from ClinicalTrials.gov");
				d_importButton.setEnabled(false);
				d_importButton.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						try {
							d_pm.importCT();
						} catch (MalformedURLException e) {
							JOptionPane.showMessageDialog(d_me, "Invalid NCT ID: "+ d_pm.getIdModel().getValue());
						} catch (IOException e) {
							JOptionPane.showMessageDialog(d_me, "Couldn't find ID: " + d_pm.getIdModel().getValue() + " on ClinicalTrials.gov");
						}
						prepare();

					}});
				d_builder.add(d_importButton, cc.xy(5, 3));	
				
				// add note to ID field
				addNoteField(d_builder, cc, 3, 3, 1, layout, d_pm.getIdNoteModel(), DEFAULT_NOTETITLE);

				// add title label
				d_builder.addLabel("Title:",cc.xy(1, 7));
				d_titleField = AuxComponentFactory.createTextArea(d_pm.getTitleModel(), true);
				d_validator.add(d_titleField);
				d_builder.add(d_titleField, cc.xy(3, 7));		
				
				// add title note
				addNoteField(d_builder, cc, 7, 3, 1, layout, d_pm.getCharacteristicNoteModel(BasicStudyCharacteristic.TITLE), "CT.gov description");
				
				// add clear button
				JButton clearButton = new JButton("clear");
				clearButton.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						d_pm.clearStudies();
						prepare();	
					}
				});
				d_builder.add(clearButton, cc.xy(5, 11));				
				
				this.setLayout(new BorderLayout());
				d_scrollPane = new JScrollPane(d_builder.getPanel());
				add(d_scrollPane, BorderLayout.CENTER);
		 }
		 
		 private class ImportButtonEnableListener implements CaretListener{
			public void caretUpdate(CaretEvent arg0) {
				d_importButton.setEnabled(!d_idField.getText().equals(""));
			}
		 }
	}	
	
	private int addNoteField(PanelBuilder builder, CellConstraints cc,	int row, int col, int width, FormLayout layout, ValueModel model, String title) {
		if(model != null && model.getValue() != null && model.getValue() != ""){
			LayoutUtil.addRow(layout);
			row+=2;
			JScrollPane notePane = AuxComponentFactory.createTextArea(model, false);
			
			notePane.setWheelScrollingEnabled(true);
			builder.add(notePane, cc.xyw(col, row, width));
			builder.add(new JLabel(title),cc.xy(col-2, row));
		}
		return row;
	}
	
	class CompleteListener implements PropertyChangeListener {
		 PanelWizardStep d_curStep;
		
		 public CompleteListener(PanelWizardStep currentStep){
			 d_curStep = currentStep;
		 }
		
		 public void propertyChange(PropertyChangeEvent evt) {
		 	 d_curStep.setComplete((Boolean) evt.getNewValue());
		 }
	 }
}
