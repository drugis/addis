/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
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
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.commons.math3.util.Pair;
import org.drugis.addis.FileNames;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.ObjectWithNotes;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.drugis.addis.entities.TypeWithNotes;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.ErrorDialog;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.StudyView;
import org.drugis.addis.gui.components.ComboBoxPopupOnFocusListener;
import org.drugis.addis.gui.components.MeasurementTable;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.gui.components.NotesView;
import org.drugis.addis.imports.PubMedIDRetriever;
import org.drugis.addis.presentation.AbstractListValidator;
import org.drugis.addis.presentation.DurationPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.StudyActivitiesTableModel;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.wizard.AddArmsPresentation;
import org.drugis.addis.presentation.wizard.AddEpochsPresentation;
import org.drugis.addis.presentation.wizard.AddStudyWizardPresentation;
import org.drugis.addis.presentation.wizard.AddStudyWizardPresentation.OutcomeMeasurementsModel;
import org.drugis.addis.presentation.wizard.StudyActivityPresentation;
import org.drugis.addis.util.PubMedListFormat;
import org.drugis.addis.util.RunnableReadyModel;
import org.drugis.common.beans.ContentAwareListModel;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.common.validation.BooleanNotModel;
import org.pietschy.wizard.AbstractWizardModel;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardEvent;
import org.pietschy.wizard.WizardListener;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
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
	}
	
	private static AbstractWizardModel buildModel(final AddStudyWizardPresentation pm, AddisWindow mainWindow, JDialog dialog) {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new EnterIdTitleWizardStep(dialog, pm));
		wizardModel.add(new SelectIndicationWizardStep(pm, mainWindow));
		wizardModel.add(new EnterCharacteristicsWizardStep(pm));
		
		wizardModel.add(new AddArmsWizardStep(dialog, pm.getAddArmsModel()));
		wizardModel.add(new AddEpochsWizardStep(dialog, pm.getAddEpochsModel()));
		wizardModel.add(new AssignActivitiesWizardStep(dialog, pm, mainWindow));
		
		wizardModel.add(new SelectEndpointWizardStep(dialog, pm));
		wizardModel.add(new SetEndpointMeasurementsWizardStep(dialog, pm));
		wizardModel.add(new SelectAdverseEventWizardStep(dialog, pm));
		wizardModel.add(new SetAdverseEventMeasurementsWizardStep(dialog, pm));
		wizardModel.add(new SelectPopulationCharsWizardStep(dialog, pm));
		wizardModel.add(new SetPopulationCharMeasurementsWizardStep(dialog, pm));
		wizardModel.add(new ReviewStudyStep(dialog, pm, mainWindow));
		
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
	
	static NotesView buildNotesEditor(TypeWithNotes obj) {
		return buildNotesEditor(obj.getNotes());
	}

	static NotesView buildNotesEditor(ObservableList<Note> notes) {
		return new NotesView(notes, true);
	}
	
	// -- Wizard Steps
	
	public static class AddArmsWizardStep extends AddListItemsWizardStep<Arm> {
		public AddArmsWizardStep(JDialog dialog, AddArmsPresentation pm) {
			super("Add arms", "Enter the arms for this study.", pm, dialog);
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
		public AddEpochsWizardStep(JDialog dialog, AddEpochsPresentation pm) {
			super("Add epochs", "Enter the epochs for this study.", pm, dialog);
			rebuild();
		}	
		
		@Override
		protected void addAdditionalFields(PanelBuilder builder, CellConstraints cc, int rows, int idx) {
			DurationPresentation<Epoch> durationModel = ((AddEpochsPresentation)d_pm).getDurationModel(idx);
			ValueModel definedModel = new PropertyAdapter<DurationPresentation<Epoch>>(
					durationModel, DurationPresentation.PROPERTY_DEFINED, true);
			
			JPanel panel = new JPanel();
			panel.add(new JLabel("Duration: "));

			// defined/undefined radio buttons
			panel.add(BasicComponentFactory.createRadioButton(definedModel, true, "Known"));
			panel.add(BasicComponentFactory.createRadioButton(definedModel, false, "Unknown"));

			// duration quantity input
			final JTextField quantityField = BasicComponentFactory.createFormattedTextField(
					new PropertyAdapter<DurationPresentation<Epoch>>(durationModel, DurationPresentation.PROPERTY_DURATION_QUANTITY, true),
					new DefaultFormatter());
			quantityField.setColumns(4);
			Bindings.bind(quantityField, "enabled", definedModel);
			panel.add(quantityField);

			// duration units input
			final JComboBox unitsField = AuxComponentFactory.createBoundComboBox(
					DurationPresentation.DateUnits.values(), 
					new PropertyAdapter<DurationPresentation<Epoch>>(durationModel, DurationPresentation.PROPERTY_DURATION_UNITS, true));
			Bindings.bind(unitsField, "enabled", definedModel);
			panel.add(unitsField);
			
			builder.add(panel, cc.xy(7, rows));
		}
	}
	
	public static class AssignActivitiesWizardStep extends PanelWizardStep {
		
		public final static class ActivitiesCompleteValidator extends AbstractListValidator<StudyActivity> {
			public ActivitiesCompleteValidator(ObservableList<StudyActivity> list) {
				super(new ContentAwareListModel<StudyActivity>(list));
			}

			@Override
			public boolean validate() {
				for (StudyActivity act : d_list) {
					if (!act.isComplete()) {
						return false;
					}
				}
				return true;
			}
		}
		
		public final static class AllActivitiesUsedValidator extends AbstractListValidator<StudyActivity> {
			public AllActivitiesUsedValidator(ObservableList<StudyActivity> list) {
				super(new ContentAwareListModel<StudyActivity>(list));
			}

			@Override
			public boolean validate() {
				for (StudyActivity act : d_list) {
					if (act.getUsedBy().isEmpty()) {
						return false;
					}
				}
				return true;
			}
		}
		
		public final static class TableFilledValidator extends AbstractValueModel implements ValueHolder<Boolean> {
			private final StudyActivitiesTableModel d_tableModel;
			private boolean d_value;

			public TableFilledValidator(StudyActivitiesTableModel table) {
				d_tableModel = table;
				d_tableModel.addTableModelListener(new TableModelListener() {
					
					@Override
					public void tableChanged(TableModelEvent e) {
						update();
					}
				});
				update();
			}
			
			private void update() {
				boolean oldValue = d_value;
				d_value = validate();
				fireValueChange(oldValue, d_value);
			}
			
			private boolean validate() {
				for(int row = 0; row < d_tableModel.getRowCount(); ++row) { 
					for(int col = 0; col < d_tableModel.getColumnCount(); ++col) { 
						if(d_tableModel.getValueAt(row, col) == null) {
							return false;
						}
					}
				}
				return true;
			}
			
			@Override
			public Boolean getValue() {
				return d_value;
			}

			@Override
			public void setValue(Object newValue) {
				throw new IllegalAccessError("TableFilledFilledValidator is read-only");
			}
			
		}
		
		private PanelBuilder d_builder;
		private JScrollPane d_scrollPane;
		
		private AddStudyWizardPresentation d_pm;
		private List<Pair<String, ValueModel>> d_validators = new ArrayList<Pair<String, ValueModel>>();
		public JTable armsEpochsTable;
		private final JDialog d_parent;
		private AddisWindow d_mainWindow;
		
		private static DataFlavor s_studyActivityFlavor = createFlavor();
		private JList d_activityList;
		private StudyActivitiesTableModel d_tableModel;
		private BooleanAndModel d_readyValidator;
		
		private static DataFlavor createFlavor() {
			try {
				return new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" +
						StudyActivity.class.getCanonicalName());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		
		public AssignActivitiesWizardStep(JDialog parent, AddStudyWizardPresentation pm, AddisWindow mainWindow) {
			super("Assign activities", "Drag activities to their proper combination of (arm, epoch). Incomplete activities are shown in red.");
			d_parent = parent;
			d_mainWindow = mainWindow;
			d_pm = pm;
		}
		
		private void addValidator(String warning, ValueModel validator) {
			d_validators.add(new Pair<String, ValueModel>(warning, validator));
		}
		
		@Override
		public void prepare() {
			this.setVisible(false);
			
			d_tableModel = new StudyActivitiesTableModel(d_pm.getNewStudyPM().getBean());

			addValidator("Some activities have missing data", new ActivitiesCompleteValidator(d_pm.getNewStudyPM().getBean().getStudyActivities()));
			addValidator("Not all cells in the table are filled in", new TableFilledValidator(d_tableModel));
			addValidator("Not all the activities are used", new AllActivitiesUsedValidator(d_pm.getNewStudyPM().getBean().getStudyActivities()));
			 
			List<ValueModel> validators = new ArrayList<ValueModel>();
			for(Pair<String, ValueModel> validator : d_validators) {
				validators.add(validator.getValue());
			}
			d_readyValidator = new BooleanAndModel(validators);
			PropertyConnector.connectAndUpdate(d_readyValidator, this, "complete");

			 
			if (d_scrollPane != null)
				remove(d_scrollPane);
			
			buildWizardStep();
			this.setVisible(true);
			repaint();
		 }
		
		private void buildWizardStep() {
			FormLayout layout = new FormLayout(
					"fill:pref, 7dlu, fill:pref:grow",
					"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
					);
			d_builder = new PanelBuilder(layout);
			d_builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			// add labels
			d_builder.addLabel("Activities: ", cc.xy(1, 1));
			d_builder.addLabel("Arms and Epochs: ", cc.xy(3, 1));

			Study study = d_pm.getNewStudyPM().getBean();

			ContentAwareListModel<StudyActivity> dataModel = new ContentAwareListModel<StudyActivity>(study.getStudyActivities());
			dataModel.addListDataListener(new ListDataListener() {
				public void intervalRemoved(ListDataEvent e) {
					d_tableModel.fireTableDataChanged();
					revalidate();
				}
				
				public void intervalAdded(ListDataEvent e) {
					d_tableModel.fireTableDataChanged();
					revalidate();
				}
				
				public void contentsChanged(ListDataEvent e) {
					d_tableModel.fireTableDataChanged();
					revalidate();
				}
			});
			
			d_activityList = new JList(dataModel);
			d_activityList.setDragEnabled(true);
			d_activityList.setTransferHandler(new TransferHandler() {
				@Override
				public int getSourceActions(JComponent c) {
					return COPY;
				}
				
				@Override
				protected Transferable createTransferable(final JComponent c) {
					return new Transferable() {
						private Object d_value = ((JList)c).getSelectedValue();
						
						public boolean isDataFlavorSupported(DataFlavor flavor) {
							return flavor.equals(s_studyActivityFlavor);
						}
						
						public DataFlavor[] getTransferDataFlavors() {
							return new DataFlavor[] { s_studyActivityFlavor };
						}
						
						public Object getTransferData(DataFlavor flavor)
								throws UnsupportedFlavorException, IOException {
							if (!isDataFlavorSupported(flavor)) {
								throw new UnsupportedFlavorException(flavor);
							}
							return d_value;
						}
					}; 
				}
			});
			d_activityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			d_activityList.setLayoutOrientation(JList.VERTICAL);
			d_activityList.setCellRenderer(new DefaultListCellRenderer() {
				public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					StudyActivity sa = (StudyActivity)value;
					JComponent listCellRendererComponent = (JComponent) super.getListCellRendererComponent(list, sa.getName(), 
									index, isSelected, cellHasFocus);
					if(!sa.isComplete()) {
						listCellRendererComponent.setBorder(BorderFactory.createLineBorder(Color.RED));
						listCellRendererComponent.setForeground(Color.RED);
					}
					return listCellRendererComponent;
				}
			});

			JScrollPane activityScrollPane = new JScrollPane(d_activityList);
			activityScrollPane.setPreferredSize(new Dimension(200, 300));
			d_builder.add(activityScrollPane, cc.xy(1, 3));
			
			createArmsAndEpochsTable(cc);
			
			createButtons(cc, d_activityList);
						
			this.setLayout(new BorderLayout());
			d_scrollPane = new JScrollPane(d_builder.getPanel());
			d_scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
			add(d_scrollPane, BorderLayout.CENTER);
			
			JPanel validationPanel = new JPanel();
			validationPanel.setLayout(new BoxLayout(validationPanel, BoxLayout.Y_AXIS));
			for (Pair<String, ValueModel> x : d_validators) {
				validationPanel.add(createValidationLabel(x.getKey(), x.getValue()));
			}
			
			d_builder.add(validationPanel, cc.xy(3, 11));
			
			JComponent note = buildTip("The study activities encode precisely what happened in each arm (patient group), during each epoch (phase of the study). " +
					"To create a valid study design, please add an activity to each cell of the arms and epochs table above." +
					"You should also make sure that each activity is completely specified and used in the study design.");
			d_builder.add(note, cc.xyw(1, 13, 3));
		}

		private JComponent createValidationLabel(String message, ValueModel validModel) {
			JLabel label = new JLabel(message);
			label.setForeground(Color.RED);
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			Bindings.bind(label, "visible", new BooleanNotModel(validModel));
			return label;
		}

		private void createArmsAndEpochsTable(CellConstraints cc) {

			final JTable armsEpochsTable = new JTable(d_tableModel);
			armsEpochsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			for (int i = 0 ; i < armsEpochsTable.getColumnCount(); i++) {
				armsEpochsTable.getColumnModel().getColumn(i).setMinWidth(100);
			}
			armsEpochsTable.getTableHeader().setReorderingAllowed(false);			
			armsEpochsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			armsEpochsTable.setDropMode(DropMode.ON_OR_INSERT_COLS);
			
			TransferHandler transferHandler = new TransferHandler() {
				public boolean canImport(TransferSupport support) {
	                if (!support.isDrop()) {
	                    return false;
	                }
	                if (!support.isDataFlavorSupported(s_studyActivityFlavor)) {
	                    return false;
	                }
	                if (support.getDropLocation() instanceof JTable.DropLocation) { // drop to table cell
						return ((JTable.DropLocation)support.getDropLocation()).getColumn() > 0;
	                }
	                if (support.getDropLocation() instanceof TransferHandler.DropLocation) { // drop to table header
	                	return getHeaderColumnIndex(support.getDropLocation()) > 0;
	                }
	                return false;
	            }

				private int getHeaderColumnIndex(TransferHandler.DropLocation dl) {
					return armsEpochsTable.getTableHeader().getColumnModel().getColumnIndexAtX(dl.getDropPoint().x);
				}

				public boolean importData(TransferSupport support) {				
					if (!canImport(support)) {
					    return false;
					}

					StudyActivity data;
		            try {
		                data = (StudyActivity)support.getTransferable().getTransferData(s_studyActivityFlavor);
		            } catch (UnsupportedFlavorException e) {
		                return false;
		            } catch (IOException e) {
		                return false;
		            }
		            
		            if (support.getDropLocation() instanceof JTable.DropLocation) { // drop to table cell
						JTable.DropLocation dl = (JTable.DropLocation)support.getDropLocation();
		            	d_tableModel.setValueAt(data, dl.getRow(), dl.getColumn());
			            return true;
		            } else { // drop to table header
		            	int columnIndex = getHeaderColumnIndex(support.getDropLocation());
		            	for (int i = 0; i < armsEpochsTable.getRowCount(); i++) {
		            		d_tableModel.setValueAt(data, i, columnIndex);
		            	}
		            	return true;
		            }
		        }
			};
			
			armsEpochsTable.setTransferHandler(transferHandler);
			armsEpochsTable.getTableHeader().setTransferHandler(transferHandler);
			armsEpochsTable.setDefaultRenderer(StudyActivity.class, new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value,
						boolean arg2, boolean arg3, int arg4, int arg5) {
					String strValue = value == null ? "" : ((StudyActivity)value).getName();
					return super.getTableCellRendererComponent(table, strValue, 
							arg2, arg3, arg4, arg5);
				}
			});
			JScrollPane tableScrollPane = new JScrollPane(armsEpochsTable);
			d_builder.add(tableScrollPane, cc.xywh(3, 3, 1, 5));
		}

		private void createButtons(CellConstraints cc, JList activities) {
			final JButton newButton = new JButton("New Activity");
			final JButton editButton = new JButton("Edit Activity");
			final JButton removeButton = new JButton("Remove Activity");

			// make sure edit and remove are only enabled when anything is selected.
			editButton.setEnabled(activities.getSelectedIndex() >= 0);
			removeButton.setEnabled(activities.getSelectedIndex() >= 0);
		
			activities.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					boolean anySelected = ((JList) (e.getSource())).getSelectedIndex() >= 0;
					editButton.setEnabled(anySelected);
					removeButton.setEnabled(anySelected);
				}					
			});
			
			d_builder.add(newButton, cc.xy(1, 5));
			newButton.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					AddStudyActivityDialog addStudyActivityDialog = new AddStudyActivityDialog(d_parent, d_mainWindow, 
							new StudyActivityPresentation(getStudyActivities(), d_pm.getDrugsModel()));
					addStudyActivityDialog.setLocationRelativeTo(d_parent);
					addStudyActivityDialog.setVisible(true);
				}
			});
			

			d_builder.add(editButton, cc.xy(1, 7));
			editButton.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					AddStudyActivityDialog addStudyActivityDialog = new AddStudyActivityDialog(d_parent, d_mainWindow, 
							new StudyActivityPresentation(getStudyActivities(), d_pm.getDrugsModel(), getStudyActivities().get(d_activityList.getSelectedIndex())));
					addStudyActivityDialog.setLocationRelativeTo(d_parent);
					addStudyActivityDialog.setVisible(true);
				}
			});
			
			d_builder.add(removeButton, cc.xy(1, 9));
			removeButton.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					Study study = d_pm.getNewStudyPM().getBean();
					StudyActivity studyActivity = d_pm.getNewStudyPM().getBean().getStudyActivities().get(d_activityList.getSelectedIndex());
					StudyActivity activity = study.findStudyActivity(studyActivity.getName());
					study.getStudyActivities().remove(activity);
				}
			});
		}
		
		private ObservableList<StudyActivity> getStudyActivities() {
			return d_pm.getNewStudyPM().getBean().getStudyActivities();
		}
	}
	
	
	public static class ReviewStudyStep extends PanelWizardStep {
		private final AddStudyWizardPresentation d_pm;
		private final AddisWindow d_mainwindow;
		private final JDialog d_dialog;

		public ReviewStudyStep(JDialog dialog, AddStudyWizardPresentation pm, AddisWindow mainWindow) {
			 super("Review study", "Please review the study to be created. " +
					 "You can go back through the wizard to correct any mistakes, " +
					 "but after the study has been added it cannot be changed.");
			d_pm = pm;
			d_mainwindow = mainWindow;
			d_dialog = dialog;
			 setLayout(new BorderLayout());
			 setComplete(true);
		 }
		 
		 @Override
		 public void prepare() {
			 StudyView view = new StudyView(d_pm.getNewStudyPM(), d_pm.getDomain(), 
					 d_dialog, d_mainwindow.getPresentationModelFactory());
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
		
		public SetMeasurementsWizardStep(AddStudyWizardPresentation pm, String title, String description,
				OutcomeMeasurementsModel model, JDialog dialog) {
			super(title, description);
			d_model = model;
			d_dialog = dialog;
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
		public SetEndpointMeasurementsWizardStep(JDialog dialog, AddStudyWizardPresentation pm) {
			super(pm, "Set Measurements", "Please enter the measurements for all arm-endpoint combinations.",
					pm.getEndpointsModel(), dialog);
		}
	}
	
	public static class SetAdverseEventMeasurementsWizardStep extends SetMeasurementsWizardStep {
		public SetAdverseEventMeasurementsWizardStep(JDialog dialog, AddStudyWizardPresentation pm){
			super(pm, "Input adverse event data", "Please enter the measurements for all arm-event combinations.",
					pm.getAdverseEventsModel(), dialog);
		}

	}
	
	public static class SetPopulationCharMeasurementsWizardStep extends SetMeasurementsWizardStep {
		public SetPopulationCharMeasurementsWizardStep(JDialog dialog, AddStudyWizardPresentation pm){
			super(pm, "Input population data", "Please enter the measurements for all population baseline characteristics.",
					pm.getPopulationCharsModel(), dialog);
		}

	}
	
	public static class SelectEndpointWizardStep extends SelectFromOutcomeMeasureListWizardStep<Endpoint> {
		public SelectEndpointWizardStep(JDialog parent, AddStudyWizardPresentation pm) {
			super(parent, pm.getEndpointSelectModel(), pm.getAddEpochsModel());
		}
		
		@Override
		protected int createAdditionalComponents(ModifiableHolder<Endpoint> slot, PanelBuilder builder, FormLayout layout, int row) {
			row = LayoutUtil.addRow(layout, row);
			StudyOutcomeMeasure<Endpoint> som = (StudyOutcomeMeasure<Endpoint>) slot;
			PropertyAdapter<StudyOutcomeMeasure<Endpoint>> primaryModel = new PropertyAdapter<StudyOutcomeMeasure<Endpoint>>(som, StudyOutcomeMeasure.PROPERTY_IS_PRIMARY, true);
			JCheckBox primaryCB = BasicComponentFactory.createCheckBox(primaryModel, "Primary endpoint");
			builder.add(primaryCB, (new CellConstraints()).xy(5, row));
			return row;
		}
	}
	
	public static class SelectPopulationCharsWizardStep extends SelectFromOutcomeMeasureListWizardStep<PopulationCharacteristic> {
		public SelectPopulationCharsWizardStep(JDialog parent, AddStudyWizardPresentation pm) {
			super(parent, pm.getPopulationCharSelectModel(), pm.getAddEpochsModel());
		}
	}
	
	public static class SelectAdverseEventWizardStep extends SelectFromOutcomeMeasureListWizardStep<AdverseEvent> {
		protected SelectAdverseEventWizardStep(JDialog parent, AddStudyWizardPresentation pm) {
			super(parent, pm.getAdverseEventSelectModel(), pm.getAddEpochsModel());
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
			d_importButton.setDisabledIcon(Main.IMAGELOADER.getIcon(FileNames.ICON_LOADING));
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
			
			JComboBox indBox = AuxComponentFactory.createBoundComboBox(d_pm.getIndicationsModel(), d_pm.getIndicationModel(), true);
			if(d_pm.getIndicationsModel().size() == 1 && indBox.getSelectedIndex() < 0) {
				indBox.setSelectedIndex(0);
			}
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
		
		public EnterIdTitleWizardStep(JDialog dialog, AddStudyWizardPresentation pm) {
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
				d_builder.add(buildNotesEditor(newStudy), cc.xy(3, 5));

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
				String tip = 	"You can import studies from ClinicalTrials.gov by entering their NCT-ID, " +
								"and then pressing the import button next to the ID field. " +
								"For example, try " + EXAMPLE_NCT_ID + ".\n\n" +
								"Unfortunately, due to limitations of ClinicalTrials.gov, it is currently not possible to import adverse events or study results.";
								
				d_builder.add(buildTip(tip), cc.xy(3, 13));
				
				this.setLayout(new BorderLayout());
				d_scrollPane = new JScrollPane(d_builder.getPanel());
				d_scrollPane.getVerticalScrollBar().setUnitIncrement(16);
				
				add(d_scrollPane, BorderLayout.CENTER);
		 }
		
		public class StartLoadingAnimation implements Runnable {
			public void run() {
				d_importButton.setDisabledIcon(Main.IMAGELOADER.getIcon(FileNames.ICON_LOADING));
				d_importButton.setEnabled(false);
			}
		}
		public class StopLoadingAnimation implements Runnable {
			public void run() {
				d_importButton.setDisabledIcon(Main.IMAGELOADER.getIcon(FileNames.ICON_IMPORT));
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
	
	private static JComponent buildTip(String tip) {
		JTextPane area = new JTextPane();
		StyledDocument doc = area.getStyledDocument();
		addStylesToDoc(doc);

		area.setBackground(new Color(255, 180, 180));

		try {
			doc.insertString(0, "x", doc.getStyle("tip"));
			doc.insertString(doc.getLength(), " Tip: \n", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), tip,
					doc.getStyle("regular"));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		area.setEditable(false);
		
		JScrollPane pane = new JScrollPane(area);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.setPreferredSize(AuxComponentFactory.textPaneDimension(area, 270, 70));
		
		pane.setWheelScrollingEnabled(true);
		pane.getVerticalScrollBar().setValue(0);
		
		return pane;
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
       StyleConstants.setIcon(style, Main.IMAGELOADER.getIcon(FileNames.ICON_TIP)); 
	}

}
