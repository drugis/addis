/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
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
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.ObjectWithNotes;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.entities.TypeWithNotes;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.ErrorDialog;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.builder.StudyView;
import org.drugis.addis.gui.components.ComboBoxPopupOnFocusListener;
import org.drugis.addis.gui.components.MeasurementTable;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.gui.components.NotesView;
import org.drugis.addis.imports.PubMedIDRetriever;
import org.drugis.addis.presentation.DosePresentation;
import org.drugis.addis.presentation.NotesModel;
import org.drugis.addis.presentation.wizard.AddArmsPresentation;
import org.drugis.addis.presentation.wizard.AddEpochsPresentation;
import org.drugis.addis.presentation.wizard.AddStudyWizardPresentation;
import org.drugis.addis.presentation.wizard.AddStudyWizardPresentation.OutcomeMeasurementsModel;
import org.drugis.addis.util.PubMedListFormat;
import org.drugis.addis.util.RunnableReadyModel;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.AbstractWizardModel;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardEvent;
import org.pietschy.wizard.WizardListener;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.PresentationModel;
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
	public static final String INSERT_EXAMPLE_ID = "control I";

	public AddStudyWizard(final AddStudyWizardPresentation pm, final AddisWindow mainWindow, JDialog dialog) {
		super(buildModel(pm, mainWindow, dialog));
		
		setDefaultExitMode(Wizard.EXIT_ON_FINISH);
		addWizardListener(new WizardListener() {
			public void wizardClosed(WizardEvent e) {
				mainWindow.leftTreeFocus(pm.saveStudy());
			}
			
			public void wizardCancelled(WizardEvent e) {
				mainWindow.leftTreeFocus(pm.getOldStudy());
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
		
		wizardModel.add(new AddArmsWizardStep(pm.getAddArmsModel()));
		wizardModel.add(new AddEpochsWizardStep(pm.getAddEpochsModel()));
		wizardModel.add(new AssignActivitiesWizardStep(pm));
		
		wizardModel.add(new SelectEndpointWizardStep(pm));
//		wizardModel.add(new SetArmsWizardStep(pm, mainWindow));
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
	
	// FIXME: should probably be in presentation
	private static ObjectWithNotes<?> getCharWithNotes(Study newStudy, BasicStudyCharacteristic schar) {
		ObjectWithNotes<?> charWithNotes = newStudy.getCharacteristicWithNotes(schar);
		if (charWithNotes == null) {
			newStudy.setCharacteristic(schar, null);
			charWithNotes = newStudy.getCharacteristicWithNotes(schar);
		}
		return charWithNotes;
	}
	
	private static NotesView buildNotesEditor(TypeWithNotes obj) {
		return buildNotesEditor(obj.getNotes());
	}

	static NotesView buildNotesEditor(List<Note> notes) {
		return new NotesView(new NotesModel(notes), true);
	}
	
	// -- Wizard Steps
	
	public static class AddArmsWizardStep extends AddListItemsWizardStep<Arm> {
		public AddArmsWizardStep(AddArmsPresentation pm) {
			super("Add arms", "Enter the arms for this study.", pm);
		}
		
		@Override
		protected void addAdditionalFields(PanelBuilder builder,
				CellConstraints cc, int rows, int idx) {
			builder.addLabel("Size: ", cc.xy(7, rows));
			JTextField sizeField = BasicComponentFactory.createFormattedTextField(
					new PresentationModel<Arm>(d_pm.getList().get(idx)).getModel(Arm.PROPERTY_SIZE), new DefaultFormatter()); 
			sizeField.setColumns(4);
			builder.add(sizeField, cc.xy(9, rows));
		}

	}
	
	public static class AddEpochsWizardStep extends AddListItemsWizardStep<Epoch> {
		public AddEpochsWizardStep(AddEpochsPresentation pm) {
			super("Add epochs", "Enter the epochs for this study.", pm);
		}
		
		@Override
		protected void addAdditionalFields(PanelBuilder builder, CellConstraints cc, int rows, int idx) {
		}
	}
	
	public static class AssignActivitiesWizardStep extends PanelWizardStep{
		JPanel d_me = this;
		private PanelBuilder d_builder;
		private JScrollPane d_scrollPane;
		
		private AddStudyWizardPresentation d_pm;
		private NotEmptyValidator d_validator;
		public JTable armsEpochsTable;
		
		public AssignActivitiesWizardStep(AddStudyWizardPresentation pm) {
			super("Assign activities", "Select teh activities that ye wants to add.");
			
			d_pm = pm;
			if (d_pm.isEditing())
				setComplete(true);
		}
		
		@Override
		public void prepare() {
			 this.setVisible(false);
			 d_validator = new NotEmptyValidator();
			 PropertyConnector.connectAndUpdate(d_validator, this, "complete");
			 
			 if (d_scrollPane != null)
				 remove(d_scrollPane);
			 
			 buildWizardStep();
			 this.setVisible(true);
			 repaint();
		 }
		
		private void buildWizardStep() {
			FormLayout layout = new FormLayout(
					"fill:pref, 7dlu, fill:pref:grow",
					"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
					);
			d_builder = new PanelBuilder(layout);
			d_builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			// add labels
			d_builder.addLabel("Activities: ", cc.xy(1, 1));
			d_builder.addLabel("Arms and Epochs: ", cc.xy(3, 1));
			
			// add activities combo-box
			String[] activities = { "Screening", "Randomization", "Fluoxetine", "Paroxetine", "Placebo" };

			JList activityList = new JList(activities);
			activityList.setDragEnabled(true);
			activityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			activityList.setLayoutOrientation(JList.VERTICAL);

			JScrollPane activityScrollPane = new JScrollPane(activityList);
			//activityScrollPane.setPreferredSize(new Dimension(100, 250));
			d_builder.add(activityScrollPane , cc.xy(1, 3));
			
			createTable(cc);
			
			createButtons(cc);
						
			this.setLayout(new BorderLayout());
			d_scrollPane = new JScrollPane(d_builder.getPanel());
			d_scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
			add(d_scrollPane, BorderLayout.CENTER);
		}

		private void createTable(CellConstraints cc) {
			// add arms & epochs table
			final DefaultTableModel tableModel = new DefaultTableModel();
			tableModel.addColumn("Arm");
			tableModel.addColumn("Item 1");
			tableModel.addColumn("Item 2");
			
			// add arm names
			List<Arm> list = d_pm.getNewStudyPM().getBean().getArms();
			Iterator<Arm> itr = list.iterator();
			while (itr.hasNext()) {
				Arm arm = itr.next();
				tableModel.addRow(new Object[]{arm.getName()});
			}
			
			final JTable armsEpochsTable = new JTable(tableModel);
			JScrollPane tableScrollPane = new JScrollPane(armsEpochsTable);
			//armsEpochsTable.setFillsViewportHeight(true);
			armsEpochsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			armsEpochsTable.setDropMode(DropMode.ON_OR_INSERT_ROWS);
			armsEpochsTable.setTransferHandler(new TransferHandler(){
				
				public boolean canImport(TransferSupport support) {
	                if (!support.isDrop()) {
	                    return false;
	                }

	                // import Strings
	                if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
	                    return false;
	                }

	                return true;
	            }

	            public boolean importData(TransferSupport support) {
	                // if we can't handle the import, say so
	                if (!canImport(support)) {
	                    return false;
	                }

	                // fetch the drop location
	                JTable.DropLocation dl = (JTable.DropLocation)support.getDropLocation();

	                int row = dl.getRow();
	                int column = dl.getColumn();

	                // fetch the data and bail if this fails
	                String data;
	                try {
	                    data = (String)support.getTransferable().getTransferData(DataFlavor.stringFlavor);
	                } catch (UnsupportedFlavorException e) {
	                    return false;
	                } catch (IOException e) {
	                    return false;
	                }

	                if (column > 0) {
	                	tableModel.setValueAt(data, row, column);
	                } else {
	                	return false;
	                }
	                
	                Rectangle rect = armsEpochsTable.getCellRect(row, 0, false);
	                if (rect != null) {
	                	armsEpochsTable.scrollRectToVisible(rect);
	                }

	                return true;
	            }
			});
			
			
			d_builder.add(tableScrollPane, cc.xy(3, 3));
		}

		private void createButtons(CellConstraints cc) {
			// add new, edit and remove buttons
			JButton newActBtn = new JButton("New Activity");
			d_builder.add(newActBtn, cc.xy(1, 5));
			newActBtn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					//TODO: write code here
				}
			});
			
			JButton editActBtn = new JButton("Edit Activity");
			d_builder.add(editActBtn, cc.xy(1, 7));
			editActBtn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					//TODO: write code here
				}
			});
			
			JButton remActBtn = new JButton("Remove Activity");
			d_builder.add(remActBtn, cc.xy(1, 9));
			remActBtn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					//TODO: write code here
				}
			});
		}
		
		class SourceTransferHandler extends TransferHandler {

        }
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
				d_mainWindow.showAddDialog(CategoryKnowledgeFactory.getCategoryKnowledge(Drug.class), d_pm.getTreatmentActivityModel(d_index).getModel(TreatmentActivity.PROPERTY_DRUG));
			}
		}
		private class RemoveArmListener extends AbstractAction {
			int d_index;
			
			public RemoveArmListener(int index) {
				d_index = index;
			}
			
			public void actionPerformed(ActionEvent e) {
				d_pm.getArms().remove(d_index);
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
			 PropertyConnector.connectAndUpdate(d_validator, this, "complete");
			 
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
						
			// add 'Add Arm' button 
			JButton btn = new JButton("Add");
			d_builder.add(btn, cc.xy(1, row+=2));
			btn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					d_pm.addArms(1);
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
			for(int curArmNumber = 0; curArmNumber < d_pm.getArms().size(); ++curArmNumber){
				LayoutUtil.addRow(layout);
				row+=2;
				
				// add 'remove arm button' 
				JButton btn = new JButton("Remove");
				builder.add(btn, cc.xy(1, row));
				btn.addActionListener(new RemoveArmListener(curArmNumber));
				
				// Set the drug of this arm
				JComboBox drugsBox = AuxComponentFactory.createBoundComboBox(d_pm.getDrugsModel(), d_pm.getTreatmentActivityModel(curArmNumber).getModel(TreatmentActivity.PROPERTY_DRUG));
				d_validator.add(drugsBox);
				builder.add(drugsBox, cc.xy(3, row));
				
				// add 'add drug button' 
				btn = GUIFactory.createPlusButton("Create drug");
				builder.add(btn, cc.xy(5, row));
				btn.addActionListener(new NewDrugButtonListener(curArmNumber));
				
				// add min dose
				DosePresentation doseModel = d_pm.getTreatmentActivityModel(curArmNumber).getDoseModel();
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
				LayoutUtil.addRow(layout);
				row += 2;
				builder.add(buildNotesEditor(d_pm.getNewStudyPM().getBean().getArms().get(curArmNumber)), cc.xyw(3, row, 11));
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

		private Set<BasicStudyCharacteristic> excludedChars = new HashSet<BasicStudyCharacteristic>();	
		private AddStudyWizardPresentation d_pm;
		
		public EnterCharacteristicsWizardStep(AddStudyWizardPresentation pm) {
			super("Enter additional information", "Enter additional information for this study. " +
					"Fields may be left empty if unknown.");
			
			excludedChars.add(BasicStudyCharacteristic.TITLE);
			excludedChars.add(BasicStudyCharacteristic.CREATION_DATE);
			excludedChars.add(BasicStudyCharacteristic.SOURCE);
			d_pm = pm;
			setComplete(true);
		}
		
		@Override
		public void prepare() {
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
					LayoutUtil.addRow(layout);
					row += 2;
					builder.add(buildNotesEditor(getCharWithNotes(d_pm.getNewStudyPM().getBean(), c)), cc.xy(3, row));

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
				} catch (IOException e) {
					ErrorDialog.showDialog(e, "Couldn't retrieve PubMed ID", "Error reading from PubMed: " + e.toString(), false);
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
			 PropertyConnector.connectAndUpdate(d_validator, this, "complete");
			 
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
			
			// (new) '+' button
			JButton btn = GUIFactory.createPlusButton("Create Indication");
			d_builder.add(btn, cc.xy(5, 3));
			btn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					d_mainWindow.showAddDialog(CategoryKnowledgeFactory.getCategoryKnowledge(Indication.class),d_pm.getIndicationModel());
					prepare();
				}
			});
			
			// add note
			d_builder.add(buildNotesEditor(d_pm.getNewStudyPM().getBean().getIndicationWithNotes()), cc.xy(3, 5));

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
		
		public class IdStepValidator extends NotEmptyValidator {
			 public IdStepValidator(ValueModel idModel) {
				 idModel.addValueChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						update();
					}
				});
			}

			@Override
			public Boolean getValue() {
				return super.getValue() && d_pm.isIdAvailable();
			}
		}
		
		public EnterIdTitleWizardStep(AddStudyWizardPresentation pm, JDialog dialog) {
			super("Select ID and Title","Set the ID and title of the study. Studies can also be extracted from Clinicaltrials.gov using the NCT-id.");
			d_pm = pm;
		}

		@Override
		public void prepare() {
			 this.setVisible(false);
			 d_validator = new IdStepValidator(d_pm.getIdModel());
			 PropertyConnector.connectAndUpdate(d_validator, this, "complete");
			 
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
				bindDefaultId(d_idField);
				d_builder.add(d_idField, cc.xy(3, 3));
				d_idField.addCaretListener(new ImportButtonEnableListener());
				final Border border = d_idField.getBorder();
				d_idField.addCaretListener(new CaretListener() {
					
					public void caretUpdate(CaretEvent e) {
						if (!d_pm.isIdAvailable()){
							d_idField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
							d_idField.setToolTipText("Study ID already exists, please change it.");
							d_idField.dispatchEvent(new KeyEvent(d_idField, KeyEvent.KEY_PRESSED, 0, KeyEvent.CTRL_MASK, KeyEvent.VK_F1, KeyEvent.CHAR_UNDEFINED));
						} else {
							d_idField.setToolTipText("");
							d_idField.setBorder(border);
						}
					}
				});
				
				d_validator.add(d_idField);
				
				// add import button
				d_importButton = GUIFactory.createIconButton(FileNames.ICON_IMPORT,
						"Enter NCT id to retrieve study data from ClinicalTrials.gov");
				d_importButton.setEnabled(isIdValid());
				d_importButton.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						CTRetriever ctRetriever = new CTRetriever();
						RunnableReadyModel readyModel = new RunnableReadyModel(ctRetriever);
						new Thread(readyModel).start();
					}});
				d_builder.add(d_importButton, cc.xy(5, 3));	
				
				// add note to ID field
				Study newStudy = d_pm.getNewStudyPM().getBean();
				d_builder.add(buildNotesEditor(newStudy.getNameWithNotes()), cc.xy(3, 5));

				// add title label
				d_builder.addLabel("Title:",cc.xy(1, 7));
				d_titleField = AuxComponentFactory.createTextArea(d_pm.getTitleModel(), true);
				d_validator.add((JTextArea)((JScrollPane)d_titleField).getViewport().getView());
				d_builder.add(d_titleField, cc.xy(3, 7));		
				
				d_builder.add(buildNotesEditor((ObjectWithNotes<?>) getCharWithNotes(newStudy, BasicStudyCharacteristic.TITLE)), cc.xy(3, 9));
				
				// add clear button
				JButton clearButton = new JButton("Clear input");
				clearButton.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						d_pm.resetStudy();
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
		
		public class StartLoadingAnimation implements Runnable {
			public void run() {
				d_importButton.setDisabledIcon(ImageLoader.getIcon(FileNames.ICON_LOADING));
				d_importButton.setEnabled(false);
			}
		}
		public class StopLoadingAnimation implements Runnable {
			public void run() {
				d_importButton.setDisabledIcon(ImageLoader.getIcon(FileNames.ICON_IMPORT));
				d_importButton.setEnabled(true);
			}		 
		}
		public class CTRetriever implements Runnable {
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new StartLoadingAnimation());
					d_pm.importCT();
					SwingUtilities.invokeAndWait(new StopLoadingAnimation());
				} catch (FileNotFoundException e) { // file not found is expected when user enters "strange" IDs
					JOptionPane.showMessageDialog(d_me, "Couldn't find NCT ID: "+ d_pm.getIdModel().getValue(), "Not Found" , JOptionPane.WARNING_MESSAGE);
				} catch (IOException e) { // IOExceptions are expected when there is a network error -- so report them
					ErrorDialog.showDialog(e, "Couldn't retrieve study", "Error reading from ClinicalTrials.gov: " + e.toString(), false);
				} catch (Exception e) { // otherwise throw onwards.
					throw new RuntimeException("Unexpected error trying to import study from ClinicalTrials.gov", e);
				}
				prepare();
			}
		}

		private class ImportButtonEnableListener implements CaretListener {
			public void caretUpdate(CaretEvent arg0) {
				d_importButton.setEnabled(isIdValid());
			}
		 }

		private boolean isIdValid() {
			return d_idField.getText().toUpperCase().trim().matches("^NCT[0-9]+$");
		}
		
		public static void bindDefaultId(final JTextField idField) {
			idField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(INSERT_EXAMPLE_ID), "insertSample");
			idField.getActionMap().put("insertSample", 
					new AbstractAction("insertSample") { 
						public void actionPerformed(ActionEvent evt) {
							idField.setText(EXAMPLE_NCT_ID);
						} 
					} 
			);
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
