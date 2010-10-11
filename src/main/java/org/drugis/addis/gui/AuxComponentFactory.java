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

package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.NumberFormatter;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang.StringEscapeUtils;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.gui.builder.wizard.AddStudyWizard;
import org.drugis.addis.gui.components.LinkLabel;
import org.drugis.addis.gui.components.ListPanel;
import org.drugis.addis.presentation.StudyCharacteristicHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.HtmlWordWrapper;
import org.drugis.common.gui.DayDateFormat;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OneWayObjectFormat;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.formatter.EmptyNumberFormatter;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.util.DefaultUnitConverter;

public class AuxComponentFactory {

	public static final Color COLOR_NOTE = new Color(255, 255, 180);

	public static <T> JComboBox createBoundComboBox(T[] values, ValueModel model) {
		SelectionInList<T> typeSelectionInList =
			new SelectionInList<T>(values, model);
		JComboBox type = BasicComponentFactory.createComboBox(typeSelectionInList);
		return type;
	}
	
	public static <T> JComboBox createBoundComboBox(ValueModel listHolder, ValueModel model) {
		SelectionInList<T> typeSelectionInList =
			new SelectionInList<T>(listHolder, model);
		JComboBox type = BasicComponentFactory.createComboBox(typeSelectionInList);
		return type;
	}

	public static JScrollPane createTextArea(ValueModel model, boolean editable) {
		JTextArea area = BasicComponentFactory.createTextArea(model);
		area.setEditable(editable);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		if (!editable)
			area.setUI(new javax.swing.plaf.basic.BasicTextAreaUI());
		JScrollPane pane = new JScrollPane(area);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.setPreferredSize(new Dimension(
				DefaultUnitConverter.getInstance().dialogUnitXAsPixel(200, area), 
				DefaultUnitConverter.getInstance().dialogUnitYAsPixel(50, area)));
		return pane;
	}

	public static JComponent createCharacteristicView(StudyCharacteristicHolder model) {
		JComponent component = null;
		Class<?> valueType = model.getCharacteristic().getValueType();
		if (model.getValue() == null) {
			return new JLabel("UNKNOWN");
		}
		if (valueType.equals(String.class)) {
			component = createTextArea(model,false);
		} else if (valueType.equals(Date.class)) {
			component = BasicComponentFactory.createLabel(model, new DayDateFormat());
		} else if (valueType.equals(PubMedIdList.class)) {
			component = new JPanel();
			PubMedIdList pubmedIds = (PubMedIdList)model.getValue();			
			for (int i = 0; i < pubmedIds.size(); i++) {
				component.add(new LinkLabel(pubmedIds.get(i).toString(), "http://www.ncbi.nlm.nih.gov/pubmed/" + pubmedIds.get(i).toString()));	
			}
		} else if (valueType.equals(List.class)) {
			component = new ListPanel(model, "value", Entity.class);
		} else if (valueType.equals(Set.class)) {
			component = new ListPanel(model, "value", Entity.class);
		} else {
			component = BasicComponentFactory.createLabel(model, new OneWayObjectFormat());
		}
		return component;
	}
	
	public static JTextField createNonNegativeIntegerTextField(ValueModel model) {
	    NumberFormatter numberFormatter = new EmptyNumberFormatter(NumberFormat.getIntegerInstance(),0);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setMinimum(0);
		
		JFormattedTextField field = new JFormattedTextField(numberFormatter);
		field.setColumns(3);
		Bindings.bind(field, model);
		return field;
	}

	public static JComponent createUnscrollableTablePanel(
			JTable measurementTable) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(measurementTable, BorderLayout.CENTER);
		panel.add(measurementTable.getTableHeader(), BorderLayout.PAGE_START);
		
		measurementTable.setBackground(Color.WHITE);
		measurementTable.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		return panel;
	}
	
	public static JRadioButton createDynamicEnabledRadioButton(String text, Object choice, ValueModel selectedValueModel, ValueModel enabledModel) {
		JRadioButton button = BasicComponentFactory.createRadioButton(selectedValueModel, choice, text);
		Bindings.bind(button,"enabled", enabledModel);
		
		return button;
	}

	public static JCheckBox createDynamicEnabledBoundCheckbox(String name,
			ValueHolder<Boolean> enabledModel,
			ValueHolder<Boolean> selectedModel) {
		JCheckBox checkBox = BasicComponentFactory.createCheckBox(selectedModel, name);
		Bindings.bind(checkBox,"enabled", enabledModel);	
		return checkBox;
	}

	public static JScrollPane createInScrollPane(PanelBuilder builder, int x, int y) {
		JScrollPane scroll = new JScrollPane(builder.getPanel());
		scroll.setPreferredSize(new Dimension(x, y));
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.setVisible(true);
		return scroll;
	}

	public static int addNoteField(PanelBuilder builder, CellConstraints cc, int row, int col, int width, FormLayout layout, ValueModel model) {
		if(model != null && model.getValue() != null && model.getValue() != ""){
			LayoutUtil.addRow(layout);
			row+=2;
			
			JTextPane area = new JTextPane();
			StyledDocument doc = area.getStyledDocument();
			AddStudyWizard.addStylesToDoc(doc);
			
			area.setBackground(COLOR_NOTE);
			
			try {
				doc.insertString(doc.getLength(), AddStudyWizard.DEFAULT_NOTETITLE + "\n", doc.getStyle("bold"));
				doc.insertString(doc.getLength(), (String)model.getValue(), doc.getStyle("regular"));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
	
			area.setEditable(false);
			
			JScrollPane pane = new JScrollPane(area);
			pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			pane.setPreferredSize(AddStudyWizard.defaultTextPaneDimension(area));
			
			pane.setWheelScrollingEnabled(true);
			pane.getVerticalScrollBar().setValue(0);
			builder.add(pane, cc.xyw(col, row, width));
		}
		return row;
	}
	
	public static JComponent createNoteField(String text) {
		if(text.length() != 0){	
			JComponent htmlPane = HtmlWordWrapper.createHtmlPane(text);
			htmlPane.setBorder(null);
			
			JScrollPane pane = new JScrollPane(htmlPane);
			pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			
			//pane.setPreferredSize(AddStudyWizard.defaultTextPaneDimension((JTextPane)htmlPane));			
			String[] arr = StringEscapeUtils.escapeHtml(text).split(" ");
			pane.setPreferredSize(new Dimension(0, (arr.length < 85 ? arr.length + 18 : 85)) );			
			
			pane.setWheelScrollingEnabled(true);
			
			return pane;
		}
		return null;
	}
}