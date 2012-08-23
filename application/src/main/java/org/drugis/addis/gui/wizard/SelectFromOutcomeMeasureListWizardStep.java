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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultFormatter;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.drugis.addis.entities.TypeWithNotes;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.WhenTaken;
import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.NotesView;
import org.drugis.addis.presentation.DurationPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.SelectFromFiniteListPresentation;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.WhenTakenPresentation;
import org.drugis.addis.presentation.wizard.AddEpochsPresentation;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OkCancelDialog;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class SelectFromOutcomeMeasureListWizardStep<T extends Variable> extends PanelWizardStep {
	private SelectFromFiniteListPresentation<T> d_pm;
	
	private class RemoveSlotListener extends AbstractAction {
		int d_index;
		
		public RemoveSlotListener(int index) {
			d_index = index;
		}
		
		public void actionPerformed(ActionEvent e) {
			d_pm.removeSlot(d_index);
			prepare();
		}	
	} 
		
	private class newOptionButtonListener extends AbstractAction {
		int d_index;

		public newOptionButtonListener(int index) {
			d_index = index;
		}
		
		public void actionPerformed(ActionEvent e) {
			d_pm.showAddOptionDialog(d_index);
		}
	}
		
	private PanelBuilder d_builder;
	private JScrollPane d_scrollPane;
	private final AddEpochsPresentation d_epm;
	private final JDialog d_parent;
		
	public SelectFromOutcomeMeasureListWizardStep(JDialog parent, SelectFromFiniteListPresentation<T> pm, AddEpochsPresentation epm) {
		super(pm.getTitle(), pm.getDescription());
		d_parent = parent;
		d_epm = epm;
		setLayout(new BorderLayout());
		d_pm = pm;
		setComplete((Boolean)d_pm.getInputCompleteModel().getValue());
		
		PropertyConnector.connectAndUpdate(d_pm.getInputCompleteModel(), this, "complete");
	}
		
	 @Override
	public void prepare() {
		 this.setVisible(false);
		 
		 if (d_scrollPane != null)
			 remove(d_scrollPane);
		 
		 buildWizardStep();
		 this.setVisible(true);
		 repaint();
	 }
		 
	private void buildWizardStep() {
		FormLayout layout = new FormLayout(
				"max(30dlu;pref), 3dlu, right:pref, 3dlu, fill:pref:grow, 3dlu, left:pref",
				"p, 3dlu, p"
				);	
		d_builder = new PanelBuilder(layout);
		d_builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		d_builder.addLabel(d_pm.getTypeName() + "s: ", cc.xy(5, 1));
		
		int row = buildSlotsPart(7, cc, 1, layout);
		
		// Add slot button
		row += 2;
		JButton btn = new JButton("Add");
		d_builder.add(btn, cc.xy(1, row));
		Bindings.bind(btn, "enabled", d_pm.getAddSlotsEnabledModel());
		btn.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				d_pm.addSlot();
				prepare();
			}
		});
		d_builder.addSeparator("", cc.xy(5, row));
	
		JPanel panel = d_builder.getPanel();
		d_scrollPane = new JScrollPane(panel);
		d_scrollPane.getVerticalScrollBar().setUnitIncrement(16);
	
		add(d_scrollPane, BorderLayout.CENTER);
	}

	private int buildSlotsPart(int fullWidth, CellConstraints cc, int row, FormLayout layout) {
		for(int i = 0; i < d_pm.countSlots(); ++i) {
			row = LayoutUtil.addRow(layout, row);
			
			// add 'remove' button
			JButton btn = new JButton("Remove");
			d_builder.add(btn, cc.xy(1, row));
			btn.addActionListener(new RemoveSlotListener(i));
						
			// dropdown
			ModifiableHolder<T> slot = d_pm.getSlot(i);
			d_builder.add(AuxComponentFactory.createBoundComboBox(d_pm.getOptions(), slot, true), cc.xy(5, row));
			
			// (new) + button
			if (d_pm.hasAddOptionDialog()) {
				JButton addOptionButton = GUIFactory.createPlusButton("Create " + d_pm.getTypeName());
				addOptionButton.addActionListener(new newOptionButtonListener(i));
				d_builder.add(addOptionButton, cc.xy(7, row));
			}

			row = createAdditionalComponents(slot, d_builder, layout, row);
			
			row = createMeasurementMomentsRow(slot, d_builder, layout, row);
			
			if (slot instanceof TypeWithNotes) {
				row = LayoutUtil.addRow(layout, row);
				d_builder.add(new NotesView(((TypeWithNotes)slot).getNotes(), true), cc.xy(5, row));
			}
			
			row = LayoutUtil.addRow(layout, row);
		}
		return row;	
	}

	private int createMeasurementMomentsRow(ModifiableHolder<T> slot, final PanelBuilder builder, FormLayout layout, int row) {
		CellConstraints cc = new CellConstraints();
		row = LayoutUtil.addRow(layout, row);
		builder.add(new MeasurementMomentsPanel<T>(d_parent, (StudyOutcomeMeasure<T>) slot, d_epm), cc.xyw(3, row, 5));
		return row;
	}
	
	private static class MeasurementMomentsPanel<T extends Variable> extends JPanel {
		private final JDialog d_parent;
		private final StudyOutcomeMeasure<T> d_slot;
		private final AddEpochsPresentation d_epm;

		public MeasurementMomentsPanel(JDialog parent, StudyOutcomeMeasure<T> slot, AddEpochsPresentation epm) {
			d_parent = parent;
			d_slot = slot;
			d_epm = epm;
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(buildPanel());
		}

		private void rebuild() {
			setVisible(false);
			removeAll();
			add(buildPanel());
			setVisible(true);
		}
	
		private JComponent buildPanel() {
			FormLayout momentLayout = new FormLayout(
					"right:pref, 3dlu, left:pref, 3dlu, left:pref, 3dlu, left:pref",
					"p, 3dlu, p"
					);			
			final PanelBuilder momentPanelBuilder = new PanelBuilder(momentLayout);
			CellConstraints cc = new CellConstraints();
			int row = 1;
			final StudyOutcomeMeasure<T> som = ((StudyOutcomeMeasure<T>)d_slot);
			
			for (final WhenTaken wt : som.getWhenTaken()) {
				row = LayoutUtil.addRow(momentLayout, row);
				momentPanelBuilder.addLabel("Measurement moment: ", cc.xy(1, row));
				momentPanelBuilder.addLabel(wt.toString(), cc.xy(3, row));
				JButton editButton = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_EDIT));
				editButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						AddWhenTakenDialog<T> dialog = new AddWhenTakenDialog<T>(d_parent, wt, (StudyOutcomeMeasure<T>) d_slot, d_epm);
						GUIHelper.centerWindow(dialog, d_parent);
						dialog.setVisible(true);
						rebuild();
					}
				});
				momentPanelBuilder.add(editButton, cc.xy(5, row));
				
				JButton deleteButton = new JButton(Main.IMAGELOADER.getIcon(org.drugis.mtc.gui.FileNames.ICON_DELETE));
				deleteButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						d_slot.getWhenTaken().remove(wt);
						rebuild();
					}
				});
				momentPanelBuilder.add(deleteButton, cc.xy(7, row));
			}
			
			row = LayoutUtil.addRow(momentLayout, row);
			JButton addWhenTakenButton = new JButton("Add measurement moment");
			addWhenTakenButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AddWhenTakenDialog<T> dialog = new AddWhenTakenDialog<T>(d_parent, (StudyOutcomeMeasure<T>) d_slot, d_epm);
					GUIHelper.centerWindow(dialog, d_parent);
					dialog.setVisible(true);
					rebuild();
				}
			});
			momentPanelBuilder.add(addWhenTakenButton, cc.xy(1, row));
			return momentPanelBuilder.getPanel();
		}
	}
	
	private static class AddWhenTakenDialog<T extends Variable> extends OkCancelDialog {

		private final StudyOutcomeMeasure<T> d_som;
		private final WhenTaken d_old;
		private final WhenTaken d_wt;
		private final ValueHolder<Boolean> d_validModel = new ModifiableHolder<Boolean>();
		private final AddEpochsPresentation d_epm;

		public AddWhenTakenDialog(JDialog parent, WhenTaken wt, StudyOutcomeMeasure<T> som, AddEpochsPresentation epm) {
			super(parent, true);
			d_old = wt;
			d_som = som;
			d_epm = epm;
			
			if (d_old == null) {
				d_wt = new WhenTaken(EntityUtil.createDuration("P0D"), RelativeTo.BEFORE_EPOCH_END, d_epm.getStudy().findTreatmentEpoch());
			} else {
				d_wt = wt.clone();
			}
			
			d_wt.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					d_validModel.setValue(isUnique());
				}
			});
			d_validModel.setValue(isUnique());
			
			Bindings.bind(d_okButton, "enabled", d_validModel);

			getUserPanel().add(new MeasurementMomentsEditorPanel<T>(d_wt, epm));
			
			getUserPanel().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "submit");
			getUserPanel().getActionMap().put("submit", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					commit();
				}
			});
			
			pack();
		}
		
		public AddWhenTakenDialog(JDialog parent, StudyOutcomeMeasure<T> som, AddEpochsPresentation epm) {
			this(parent, null, som, epm);
		}

		private boolean isUnique() {
			if (!d_som.getWhenTaken().contains(d_wt)) {
				return true;
			} else {
				return d_wt.equals(d_old);
			}
		}
		
		@Override
		protected void cancel() {
			setVisible(false);
			dispose();
		}

		@Override
		protected void commit() {
			if (isUnique()) {
				d_wt.commit();
				if (d_old == null) {
					d_som.getWhenTaken().add(d_wt);
				} else {
					d_epm.getStudy().replaceWhenTaken(d_som, d_old, d_wt);
				}
				setVisible(false);
				dispose();
			}
		}
		
	}
	
	private static class MeasurementMomentsEditorPanel<T extends Variable> extends JPanel {
		private final AddEpochsPresentation d_epm;
		private final WhenTaken d_wt;

		public MeasurementMomentsEditorPanel(WhenTaken wt, AddEpochsPresentation epm) {
			d_wt = wt;
			d_epm = epm;
			add(buildPanel());
		}
	
		private JComponent buildPanel() {
			FormLayout momentLayout = new FormLayout(
					"max(30dlu;pref), 3dlu, right:pref, 3dlu, fill:pref:grow, 3dlu, left:pref, 3dlu, left:pref",
					"p"
					);			
			final PanelBuilder momentPanelBuilder = new PanelBuilder(momentLayout);
			CellConstraints cc = new CellConstraints();
			int row2 = 1;
			
			WhenTakenPresentation wtp = new WhenTakenPresentation(d_wt, d_epm.getList());
			row2 = LayoutUtil.addRow(momentLayout, row2);

			momentPanelBuilder.add(new JLabel("Measurement moment: "), cc.xy(1, row2));

			DurationPresentation<WhenTaken> durationModel = wtp.getOffsetPresentation();

			// duration quantity input
			final JTextField quantityField = BasicComponentFactory.createFormattedTextField(
					new PropertyAdapter<DurationPresentation<WhenTaken>>(durationModel, DurationPresentation.PROPERTY_DURATION_QUANTITY, true),
					new DefaultFormatter());
			quantityField.setColumns(4);
			momentPanelBuilder.add(quantityField, cc.xy(3, row2));

			// duration units input
			final JComboBox unitsField = AuxComponentFactory.createBoundComboBox(
					DurationPresentation.DateUnits.values(), 
					new PropertyAdapter<DurationPresentation<WhenTaken>>(durationModel, DurationPresentation.PROPERTY_DURATION_UNITS, true));
			momentPanelBuilder.add(unitsField, cc.xy(5, row2));
			
			final JComboBox relativeTofield = AuxComponentFactory.createBoundComboBox(
					RelativeTo.values(), 
					new PropertyAdapter<WhenTaken>(d_wt, WhenTaken.PROPERTY_RELATIVE_TO, true));
			momentPanelBuilder.add(relativeTofield, cc.xy(7, row2));
			
			final JComboBox epochField = AuxComponentFactory.createBoundComboBox(d_epm.getList(),
					new PropertyAdapter<WhenTaken>(d_wt, WhenTaken.PROPERTY_EPOCH), true);
			momentPanelBuilder.add(epochField, cc.xy(9, row2));

			return momentPanelBuilder.getPanel();
		}
	}
	
	protected int createAdditionalComponents(ModifiableHolder<T> slot, PanelBuilder builder, FormLayout layout, int row) {
		return row;
	}
}
