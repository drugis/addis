/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Source;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.builder.StudyView;
import org.drugis.addis.gui.components.ComboBoxPopupOnFocusListener;
import org.drugis.addis.gui.components.MeasurementTable;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.imports.PubMedIDRetriever;
import org.drugis.addis.presentation.DosePresentation;
import org.drugis.addis.presentation.wizard.AddStudyWizardPresentation;
import org.drugis.addis.presentation.wizard.CompleteListener;
import org.drugis.addis.presentation.wizard.AddStudyWizardPresentation.OutcomeMeasurementsModel;
import org.drugis.addis.util.PubMedListFormat;
import org.drugis.addis.util.RunnableReadyModel;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.AbstractWizardModel;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardAdapter;
import org.pietschy.wizard.WizardEvent;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.util.DefaultUnitConverter;
import com.toedter.calendar.JDateChooser;

@SuppressWarnings("serial")
public class AddStudyWizard extends Wizard {
	private static final String EXAMPLE_NCT_ID = "NCT00296517";
	public static final String DEFAULT_NOTETITLE = "Source Text (ClinicalTrials.gov):";

	public AddStudyWizard(final AddStudyWizardPresentation pm, final AddisWindow mainWindow, JDialog dialog) {
		super(buildModel(pm, mainWindow, dialog));
		
		setDefaultExitMode(Wizard.EXIT_ON_FINISH);
		addWizardListener(new WizardAdapter() {
			@Override
			public void wizardClosed(WizardEvent e) {
				mainWindow.leftTreeFocus(pm.saveStudy());
			}
		});
		setOverviewVisible(false);
		
		dialog.setPreferredSize(new Dimension(mainWindow.getWidth() / 5 * 4, mainWindow.getHeight() / 5 * 4));
		dialog.setMinimumSize(new Dimension(mainWindow.getMinimumSize().width - 100, mainWindow.getMinimumSize().height - 100));
	}
	
	private static AbstractWizardModel buildModel(final AddStudyWizardPresentation pm, AddisWindow mainWindow, JDialog dialog) {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new EnterIdTitleWizardStep(pm, dialog));
		wizardModel.add(new SelectIndicationWizardStep(pm, mainWindow));
		wizardModel.add(new EnterCharacteristicsWizardStep(pm));
		wizardModel.add(new SelectEndpointWizardStep(pm));
		wizardModel.add(new SetArmsWizardStep(pm, mainWindow));
		wizardModel.add(new SetEndpointMeasurementsWizardStep(pm, dialog));
		wizardModel.add(new SelectAdverseEventWizardStep(pm));
		wizardModel.add(new SetAdverseEventMeasurementsWizardStep(pm, dialog));
		wizardModel.add(new SelectPopulationCharsWizardStep(pm));
		wizardModel.add(new SetPopulationCharMeasurementsWizardStep(pm, dialog));
		wizardModel.add(new ReviewStudyStep(pm, mainWindow));
		
		wizardModel.setLastVisible(false);
		// The measurements + variable lists are saved on viewing the measurement tables
		// unless this is changed, skipping steps should be disabled.
		
		return wizardModel;
	}
	
	public static class ReviewStudyStep extends PanelWizardStep {
		 private final AddStudyWizardPresentation d_pm;
		private final AddisWindow d_mainwindow;

		public ReviewStudyStep(AddStudyWizardPresentation pm, AddisWindow mainWindow) {
			 super("Review study", "Please review the study to be created. " +
					 "You can go back through the wizard to correct any mistakes, " +
					 "but after the study has been added it cannot be changed.");
			d_pm = pm;
			d_mainwindow = mainWindow;
			 setLayout(new BorderLayout());
			 setComplete(true);
		 }
		 
		 @Override
		 public void prepare() {
			 StudyView view = new StudyView(d_pm.getNewStudyPM(), d_pm.getDomain(), 
					 d_mainwindow, d_mainwindow.getPresentationModelFactory());
			 removeAll();
			 add(view.buildPanel(), BorderLayout.CENTER);
			 this.setVisible(true);			 
		 }
	 }
	
	public static class SetMeasurementsWizardStep extends PanelWizardStep {
		private JScrollPane d_scrollPane;
		private OutcomeMeasurementsModel d_model;
		private JDialog d_dialog;
		private MeasurementTable d_table;
		private AddStudyWizardPresentation d_pm;
		
		public SetMeasurementsWizardStep(AddStudyWizardPresentation pm, String title, String description,
				OutcomeMeasurementsModel model, JDialog dialog) {
			super(title, description);
			d_model = model;
			d_dialog = dialog;
			d_pm = pm;
			if (d_pm.isEditing())
				setComplete(true);
		} 
		
		@Override
		public void prepare() {
			this.setVisible(false);
			if (d_scrollPane != null) {
				 remove(d_scrollPane);
			}
			 
			buildWizardStep();
			this.setVisible(true);
			repaint();
			setComplete(true);
		}
		
		private void buildWizardStep() {
			this.setLayout(new BorderLayout());
			TableModel tableModel = d_model.getMeasurementTableModel();
			d_table = new MeasurementTable(tableModel, d_dialog);
			d_scrollPane = new JScrollPane(d_table);
			d_scrollPane.getVerticalScrollBar().setUnitIncrement(16);
			
			add(d_scrollPane, BorderLayout.CENTER);
		}
	}
	
	public static class SetEndpointMeasurementsWizardStep extends SetMeasurementsWizardStep {
		public SetEndpointMeasurementsWizardStep(AddStudyWizardPresentation pm, JDialog dialog) {
			super(pm, "Set Measurements", "Please enter the measurements for all arm-endpoint combinations.",
					pm.getEndpointsModel(), dialog);
		}
	}
	
	public static class SetAdverseEventMeasurementsWizardStep extends SetMeasurementsWizardStep {
		public SetAdverseEventMeasurementsWizardStep(AddStudyWizardPresentation pm, JDialog dialog){
			super(pm, "Input adverse event data", "Please enter the measurements for all arm-event combinations.",
					pm.getAdverseEventsModel(), dialog);
		}

	}
	
	public static class SetPopulationCharMeasurementsWizardStep extends SetMeasurementsWizardStep {
		public SetPopulationCharMeasurementsWizardStep(AddStudyWizardPresentation pm, JDialog dialog){
			super(pm, "Input population data", "Please enter the measurements for all population baseline characteristics.",
					pm.getPopulationCharsModel(), dialog);
		}

	}
	
	public static class SetArmsWizardStep extends PanelWizardStep {
		private class NewDrugButtonListener implements ActionListener{
			int d_index;

			public NewDrugButtonListener(int index) {
				d_index = index;
			}
			
			public void actionPerformed(ActionEvent e) {
				d_mainWindow.showAddDialog(CategoryKnowledgeFactory.getCategoryKnowledge(Drug.class), d_pm.getArmModel(d_index).getModel(Arm.PROPERTY_DRUG));
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
		private AddStudyWizardPresentation d_pm;
		private AddisWindow d_mainWindow;
		
		public SetArmsWizardStep(AddStudyWizardPresentation pm, AddisWindow mainWindow) {
			super("Select Arms", "Please input the appropriate arms. " +
					"The drug field of every arm must be filled in order to continue. At least one arm must be included.");
			d_pm = pm;
			d_mainWindow = mainWindow;
			if (d_pm.isEditing())
				setComplete(true);
		}
		
		 @Override
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
			d_builder.add(new JLabel("Drug:"),cc.xy(3, 1));
			d_builder.add(new JLabel("Dose:"),cc.xy(7, 1));
			d_builder.add(new JLabel("Size:"),cc.xy(13, 1));
			
			
			int row = buildArmsPart(1, d_builder, cc, 3, layout);
			
			if(d_pm.getNumberArms() == 0 ) {
				d_pm.addArmModels(1);
				row += 2;
				prepare();
			}
			
			// add 'Add Arm' button 
			JButton btn = new JButton("Add");
			d_builder.add(btn, cc.xy(1, row+=2));
			btn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					d_pm.addArmModels(1);
					prepare();
				}
			});
			d_builder.addSeparator("", cc.xyw(3, row, 11));
			
			JPanel panel = d_builder.getPanel();
			this.setLayout(new BorderLayout());
			d_scrollPane = new JScrollPane(panel);
			d_scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
			add(d_scrollPane, BorderLayout.CENTER);
		}

		private int buildArmsPart(int fullwidth, PanelBuilder builder,	CellConstraints cc, int row, FormLayout layout) {
			// For all the arms found in the imported study
			for(int curArmNumber = 0; curArmNumber < d_pm.getNumberArms(); ++curArmNumber){
				LayoutUtil.addRow(layout);
				row+=2;
				
				// add 'remove arm button' 
				JButton btn = new JButton("Remove");
				builder.add(btn, cc.xy(1, row));
				btn.addActionListener(new RemoveArmListener(curArmNumber));
				
				// Set the drug of this arm
				JComboBox drugsBox = AuxComponentFactory.createBoundComboBox(d_pm.getDrugsModel(), d_pm.getArmModel(curArmNumber).getModel(Arm.PROPERTY_DRUG));
				d_validator.add(drugsBox);
				builder.add(drugsBox, cc.xy(3, row));
				
				// add 'add drug button' 
				btn = GUIFactory.createPlusButton("Create drug");
				builder.add(btn, cc.xy(5, row));
				btn.addActionListener(new NewDrugButtonListener(curArmNumber));
				
				// add min dose
				DosePresentation doseModel = d_pm.getArmModel(curArmNumber).getDoseModel();
				JTextField minDoseField =  new JFormattedTextField(new DefaultFormatter());
				PropertyConnector.connectAndUpdate(doseModel.getMinModel(), minDoseField, "value");
				minDoseField.setColumns(4);
				d_validator.add(minDoseField);
				builder.add(minDoseField, cc.xy(7, row));
				
				// add max dose
				JTextField maxDoseField = new JFormattedTextField(new DefaultFormatter());
				PropertyConnector.connectAndUpdate(doseModel.getMaxModel(), maxDoseField, "value");
				maxDoseField.setColumns(4);
				d_validator.add(maxDoseField);
				builder.add(maxDoseField, cc.xy(9, row));
				
				// add dose unit
				JComboBox doseUnitBox = AuxComponentFactory.createBoundComboBox(SIUnit.values(), doseModel.getUnitModel());
				d_validator.add(doseUnitBox);
				builder.add(doseUnitBox, cc.xy(11, row));
				
				// add group size
				JTextField sizeField =  new JFormattedTextField(new DefaultFormatter());
				PropertyConnector.connectAndUpdate(d_pm.getArmModel(curArmNumber).getModel(Arm.PROPERTY_SIZE), sizeField, "value");
				sizeField.setColumns(4);
				d_validator.add(sizeField);
				builder.add(sizeField, cc.xy(13, row));
				
				// Show the notes from the imported study for the drug
				row = AuxComponentFactory.addNoteField(builder, cc, row, 3, 11, layout, d_pm.getArmNoteModel(curArmNumber));
			}
			return row;
		}
		
		
	}
	
	public static class SelectEndpointWizardStep extends SelectFromFiniteListWizardStep<Endpoint> {
		public SelectEndpointWizardStep(AddStudyWizardPresentation pm) {
			super(pm.getEndpointSelectModel());
		}		
	}
	
	public static class SelectPopulationCharsWizardStep extends SelectFromFiniteListWizardStep<PopulationCharacteristic> {
		public SelectPopulationCharsWizardStep(AddStudyWizardPresentation pm) {
			super(pm.getPopulationCharSelectModel());
		}
		
	}
	
	public static class SelectAdverseEventWizardStep extends SelectFromFiniteListWizardStep<AdverseEvent> {
		public SelectAdverseEventWizardStep(AddStudyWizardPresentation pm) {
			super(pm.getAdverseEventSelectModel());
		}		
	}
	
	public static class EnterCharacteristicsWizardStep extends PanelWizardStep{
		JPanel d_me = this;
		private PanelBuilder d_builder;
		private JScrollPane d_scrollPane;
		private NotEmptyValidator d_validator;

		private Set<BasicStudyCharacteristic> excludedChars = new HashSet<BasicStudyCharacteristic>();	
		private AddStudyWizardPresentation d_pm;
		
		public EnterCharacteristicsWizardStep(AddStudyWizardPresentation pm) {
			super("Enter additional information", "Enter additional information for this study. " +
					"Fields may be left empty if unknown.");
			
			excludedChars.add(BasicStudyCharacteristic.TITLE);
			excludedChars.add(BasicStudyCharacteristic.CREATION_DATE);
			excludedChars.add(BasicStudyCharacteristic.SOURCE);
			d_pm = pm;
			if (d_pm.isEditing())
				setComplete(true);
		}
		
		@Override
		public void prepare() {
			d_validator = new NotEmptyValidator();
			d_validator.addValueChangeListener(new CompleteListener(this));
			
			if (d_scrollPane != null)
				 remove(d_scrollPane);
			 
			setComplete(true); // Don't require fields to be filled
			 
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
			d_scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
			add(d_scrollPane, BorderLayout.CENTER);
		}
		
		private int buildCharacteristicsPart(int fullWidth, PanelBuilder builder, CellConstraints cc, int row, FormLayout layout) {
			for (BasicStudyCharacteristic c : BasicStudyCharacteristic.values()) {
				if (!excludedChars.contains(c)) {
					// add characteristic field
					builder.addLabel(c.getDescription() + ":", cc.xy(1, row));
					builder.add(createCharacteristicComponent(c), cc.xyw(3, row,fullWidth));

					// add note field
					row = AuxComponentFactory.addNoteField(builder, cc, row, 3, 1, layout, d_pm.getCharacteristicNoteModel(c));

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
				} else if (PubMedIdList.class.isAssignableFrom(c.getValueType())) {
					ValueModel model = d_pm.getCharacteristicModel(c);
					component = createPubMedIDComponent(model);
				} else {
					if (c.getValueType().isEnum()) {
						try {
							component = createOptionsComboBox(c, c.getValueType().getEnumConstants());
						} catch (Exception e) {
							component = new JLabel("ILLEGAL CHARACTERISTIC ENUM TYPE");
						}
					} else {
						throw new RuntimeException("unknown characteristic type");
					}
				}
			} 
			
			return component;
		}
		
		private class PubMedIdsRetriever implements Runnable {
			private final JButton d_importButton;

			public PubMedIdsRetriever(JButton importButton) {
				d_importButton = importButton;
			}
			
			public void run() {				
					String studyID = d_pm.getIdModel().getValue().toString().trim();
					try {
						d_importButton.setEnabled(false);
						
						PubMedIdList importPubMedID = new PubMedIDRetriever().importPubMedID(studyID.replace(" ", "%20"));
						
						if (!importPubMedID.isEmpty()) {
							d_pm.getCharacteristicModel(BasicStudyCharacteristic.PUBMED).setValue(importPubMedID);
						} else {
							JOptionPane.showMessageDialog(d_me, "The Study ID ("+studyID+")\nhas no PubMed ID associated", "Warning", JOptionPane.WARNING_MESSAGE);
						}
						d_importButton.setEnabled(true);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(d_me, "Couldn't retrieve PubMed ID ...", e.getMessage(), JOptionPane.ERROR_MESSAGE);
					}
			}
		}
		
		private JComponent createPubMedIDComponent(ValueModel model) {
			JTextField inputField = BasicComponentFactory.createFormattedTextField(model, new PubMedListFormat());							
			inputField.setColumns(30);
			inputField.setToolTipText("You can enter multiple PubMed IDs delimited by comma");
			
			final JButton d_importButton = GUIFactory.createIconButton(FileNames.ICON_SEARCH, "Search PubMed ID based on the trial ID");
			d_importButton.setDisabledIcon(ImageLoader.getIcon(FileNames.ICON_LOADING));
			d_importButton.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					PubMedIdsRetriever pubMedRetriever = new PubMedIdsRetriever(d_importButton);
					RunnableReadyModel readyModel = new RunnableReadyModel(pubMedRetriever);
					new Thread(readyModel).start();
				}
			});

			PanelBuilder builder = new PanelBuilder(new FormLayout("pref:grow, 3dlu, pref", "p"));
			CellConstraints cc = new CellConstraints();

			builder.add(inputField, cc.xy(1, 1));
			builder.add(d_importButton, cc.xy(3, 1));

			return builder.getPanel();
		}
		
		private <E> JComponent createOptionsComboBox(BasicStudyCharacteristic c, E[] options) {
			ValueModel selectionHolder = d_pm.getCharacteristicModel(c);
			JComboBox component = AuxComponentFactory.createBoundComboBox(options, selectionHolder);
			ComboBoxPopupOnFocusListener.add(component);
			return component;
		}
	}
	
	public static class SelectIndicationWizardStep extends PanelWizardStep {
		private PanelBuilder d_builder;
		private NotEmptyValidator d_validator;
		private JScrollPane d_scrollPane;
		private AddStudyWizardPresentation d_pm;
		private AddisWindow d_mainWindow;

		public SelectIndicationWizardStep (AddStudyWizardPresentation pm, AddisWindow mainWindow) {
			super("Select Indication", "Select the indication for this study. " +
					"An indication must be selected to continue.");
			d_pm = pm;
			d_mainWindow = mainWindow;
			if (d_pm.isEditing())
				setComplete(true);
		}
		
		 @Override
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
					"right:pref, 3dlu, pref:grow:fill, 3dlu, left:pref",
					"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
					);	
			d_builder = new PanelBuilder(layout);
			d_builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			d_builder.addLabel("Indication",cc.xy(1, 3));	
			
			JComboBox indBox = AuxComponentFactory.createBoundComboBox(d_pm.getIndicationListModel(), d_pm.getIndicationModel());
			d_builder.add(indBox, cc.xyw(3, 3, 2));
			d_validator.add(indBox);
			
			// add 'add indication' button
			JButton btn = GUIFactory.createPlusButton("add new indication");
			d_builder.add(btn, cc.xy(5, 3));
			btn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					d_mainWindow.showAddDialog(CategoryKnowledgeFactory.getCategoryKnowledge(Indication.class),
							d_pm.getIndicationModel());
				}
			});
			
			// add note
			AuxComponentFactory.addNoteField(d_builder, cc, 3, 3, 1, layout, d_pm.getIndicationNoteModel());

			this.setLayout(new BorderLayout());
			d_scrollPane = new JScrollPane(d_builder.getPanel());
			d_scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
			add(d_scrollPane, BorderLayout.CENTER);
		}
	}
	
	public static class EnterIdTitleWizardStep extends PanelWizardStep {
		JPanel d_me = this;
		private JTextField d_idField;
		private JComponent d_titleField;
		private PanelBuilder d_builder;
		private JButton d_importButton;
		private NotEmptyValidator d_validator;
		private JScrollPane d_scrollPane;
		private AddStudyWizardPresentation d_pm;
		private JDialog d_dialog;

		public EnterIdTitleWizardStep(AddStudyWizardPresentation pm, JDialog dialog) {
			super("Select ID and Title","Set the ID and title of the study. Studies can also be extracted from Clinicaltrials.gov using the NCT-id.");
			d_pm = pm;
			d_dialog = dialog;
		}

		@Override
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
						"right:pref, 3dlu, pref:grow:fill, 3dlu, left:pref",
						"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
						);	
				d_builder = new PanelBuilder(layout);
				d_builder.setDefaultDialogBorder();
				CellConstraints cc = new CellConstraints();
				
				// add source fields
				d_builder.addLabel("Source:",cc.xy(1, 1));	
				JComponent sourceSelecter = AuxComponentFactory.createBoundComboBox(Source.values(), d_pm.getSourceModel());
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
						if (!d_pm.isIdAvailable()){
							JOptionPane.showMessageDialog(d_dialog, "There is already a study called \"" + 
									d_pm.getIdModel().getValue() + "\".\nPlease save under a different title.",
									"Error: study already exists", JOptionPane.ERROR_MESSAGE);
							setComplete(false);
						}
					}
					
					public void focusGained(FocusEvent e) {
					}
				});
				
				// add import button
				d_importButton = GUIFactory.createIconButton(FileNames.ICON_IMPORT,
						"Enter NCT id to retrieve study data from ClinicalTrials.gov");
				d_importButton.setDisabledIcon(ImageLoader.getIcon(FileNames.ICON_LOADING));
				d_importButton.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						CTRetriever ctRetriever = new CTRetriever();
						RunnableReadyModel readyModel = new RunnableReadyModel(ctRetriever);
						new Thread(readyModel).start();
					}});
				d_builder.add(d_importButton, cc.xy(5, 3));	
				
				// add note to ID field
				AuxComponentFactory.addNoteField(d_builder, cc, 3, 3, 1, layout, d_pm.getIdNoteModel());

				// add title label
				d_builder.addLabel("Title:",cc.xy(1, 7));
				d_titleField = AuxComponentFactory.createTextArea(d_pm.getTitleModel(), true);
				d_validator.add(d_titleField);
				d_builder.add(d_titleField, cc.xy(3, 7));		
				
				// add title note
				AuxComponentFactory.addNoteField(d_builder, cc, 7, 3, 1, layout, d_pm.getCharacteristicNoteModel(BasicStudyCharacteristic.TITLE));
				
				// add clear button
				JButton clearButton = new JButton("Clear input");
				clearButton.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						d_pm.clearStudies();
						prepare();	
					}
				});
				d_builder.add(clearButton, cc.xy(3, 11));
				d_builder.add(buildTip(), cc.xy(3, 13));
				
				this.setLayout(new BorderLayout());
				d_scrollPane = new JScrollPane(d_builder.getPanel());
				d_scrollPane.getVerticalScrollBar().setUnitIncrement(16);
				
				add(d_scrollPane, BorderLayout.CENTER);
		 }
		 
		public class CTRetriever implements Runnable {
			public void run() {
				try {
					d_importButton.setEnabled(false);				
					d_pm.importCT();
					d_importButton.setEnabled(true);
				} catch (FileNotFoundException e) { // file not found is expected when user enters "strange" IDs
					JOptionPane.showMessageDialog(d_me, "Couldn't find NCT ID: "+ d_pm.getIdModel().getValue(), "Not Found" , JOptionPane.WARNING_MESSAGE);
				} catch (IOException e) { // IOExceptions are expected when there is a network error -- so report them
					e.printStackTrace();
					JOptionPane.showMessageDialog(d_me, "Unable to read from ClinicalTrials.gov: " + e.getMessage(), "Error reading", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e) { // otherwise throw onwards.
					throw new RuntimeException("Unexpected error trying to import study from ClinicalTrials.gov", e);
				}
				prepare();
			}
		}

		private class ImportButtonEnableListener implements CaretListener{
			public void caretUpdate(CaretEvent arg0) {
				d_importButton.setEnabled(!d_idField.getText().equals(""));
			}
		 }
	}	
	
	private static JComponent buildTip() {
		JTextPane area = new JTextPane();
		StyledDocument doc = area.getStyledDocument();
		addStylesToDoc(doc);

		area.setBackground(new Color(255, 180, 180));

		try {
			doc.insertString(0, "x", doc.getStyle("tip"));
			doc.insertString(doc.getLength(), " Tip: \n", doc.getStyle("bold"));
			doc.insertString(doc.getLength(),
					"You can import studies from ClinicalTrials.gov by entering their NCT-ID, " +
					"and then pressing the import button next to the ID field. " +
					"For example, try " + EXAMPLE_NCT_ID + ".\n\n" +
					"Unfortunately, due to limitations of ClinicalTrials.gov, it is currently not possible to import adverse events or study results.",
					doc.getStyle("regular"));
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		area.setEditable(false);
		
		JScrollPane pane = new JScrollPane(area);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.setPreferredSize(textPaneDimension(area, 270, 70));
		
		pane.setWheelScrollingEnabled(true);
		pane.getVerticalScrollBar().setValue(0);
		
		return pane;
	}
	
	public static Dimension defaultTextPaneDimension(JTextPane area) {
		return textPaneDimension(area, 230, 50);
	}

	private static Dimension textPaneDimension(JTextPane area, int dluX,
			int dluY) {
		return new Dimension(
				DefaultUnitConverter.getInstance().dialogUnitXAsPixel(dluX, area), 
				DefaultUnitConverter.getInstance().dialogUnitYAsPixel(dluY, area));
	}
	
	public static void addStylesToDoc(StyledDocument doc) {
       //Initialize some styles.
       Style def = StyleContext.getDefaultStyleContext().
                       getStyle(StyleContext.DEFAULT_STYLE);

       Style regular = doc.addStyle("regular", def);

       Style bold = doc.addStyle("bold", regular);
       StyleConstants.setBold(bold, true);
       
       // The image must first be wrapped in a style
       Style style = doc.addStyle("tip", null);
       StyleConstants.setIcon(style, ImageLoader.getIcon(FileNames.ICON_TIP)); 
	}

}
