package org.drugis.common.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.NumberFormatter;
import javax.swing.text.StyledDocument;

import org.drugis.addis.gui.builder.wizard.AddStudyWizard;
import org.drugis.addis.gui.components.MeasurementTable;
import org.drugis.addis.presentation.StudyCharacteristicHolder;
import org.drugis.addis.presentation.ValueHolder;

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
			MeasurementTable measurementTable) {
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

	public static int addNoteField(PanelBuilder builder, CellConstraints cc,	int row, int col, int width, FormLayout layout, ValueModel model) {
		if(model != null && model.getValue() != null && model.getValue() != ""){
			LayoutUtil.addRow(layout);
			row+=2;
			
			JTextPane area = new JTextPane();
			StyledDocument doc = area.getStyledDocument();
			AddStudyWizard.addStylesToDoc(doc);
			
			area.setBackground(new Color(255, 255, 180));
			
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
}
