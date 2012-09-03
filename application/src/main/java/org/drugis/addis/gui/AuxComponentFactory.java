/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.NumberFormatter;
import javax.swing.text.StyledDocument;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.gui.components.ListPanel;
import org.drugis.addis.gui.wizard.AddStudyWizard;
import org.drugis.addis.presentation.StudyCharacteristicHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.BrowserLaunch;
import org.drugis.common.gui.DayDateFormat;
import org.drugis.common.gui.LinkLabel;
import org.drugis.common.gui.OneWayObjectFormat;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.formatter.EmptyNumberFormatter;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.util.DefaultUnitConverter;

public class AuxComponentFactory {

	private static final String USER_NOTE = "User Note:";
	private static final String NCT_NOTE = "Source Text (ClinicalTrials.gov):";
	public static final Color COLOR_NOTE = new Color(255, 255, 180);
	public static final Color COLOR_NOTE_EDIT = new Color(255, 255, 210);
	public static final Color COLOR_ERROR = new Color(255, 215, 215);

	public static <T> JComboBox createBoundComboBox(T[] values, ValueModel model) {
		return createBoundComboBox(values, model, false);
	}
	
	public static <T> JComboBox createBoundComboBox(T[] values, ValueModel model, boolean isEntity) {
		return createBoundComboBox(new ArrayListModel<T>(Arrays.asList(values)), model, isEntity);
	}
	
	public static <T> JComboBox createBoundComboBox(ListModel list, ValueModel model, boolean isEntity) {
		SelectionInList<T> selectionInList = new SelectionInList<T>(list, model);
		JComboBox comboBox = BasicComponentFactory.createComboBox(selectionInList);
		
		if (isEntity) {
			final ListCellRenderer renderer = comboBox.getRenderer();
			comboBox.setRenderer(new ListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					return renderer.getListCellRendererComponent(list, getDescription(value), index, isSelected, cellHasFocus);
				}

				private String getDescription(Object value) {
					return value == null ? "" : ((Entity)value).getLabel();
				}
			});
		}
		
		return comboBox;
	}	

	public static JScrollPane createTextArea(ValueModel model, boolean editable) {
		return createTextArea(model, editable, true);
	}
	
	public static JScrollPane createTextArea(ValueModel model, boolean editable, boolean commitOnFocusLost) {
		JTextArea area = BasicComponentFactory.createTextArea(model, commitOnFocusLost);
		dontStealTabKey(area);
		area.setEditable(editable);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		if (!editable) {
			area.setUI(new javax.swing.plaf.basic.BasicTextAreaUI());
		}
		
		JScrollPane pane = new JScrollPane(area);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.setPreferredSize(new Dimension(
				DefaultUnitConverter.getInstance().dialogUnitXAsPixel(200, area), 
				DefaultUnitConverter.getInstance().dialogUnitYAsPixel(50, area)));
		return pane;
	}
	
	public static void dontStealTabKey(final JTextArea area) {
		area.setFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				new JLabel().getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		area.setFocusTraversalKeys(
				KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				new JLabel().getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
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
			component.setLayout(new BoxLayout(component, BoxLayout.Y_AXIS));
			for (PubMedId pmid : (PubMedIdList) model.getValue()) {
				component.add(new LinkLabel(pmid.getId(), "http://www.ncbi.nlm.nih.gov/pubmed/" + pmid.getId()));	
			}
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

	public static JComponent createUnscrollableTablePanel(JTable table) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(table, BorderLayout.CENTER);
		panel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		
		table.setBackground(Color.WHITE);
		table.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
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

	public static JScrollPane createInScrollPane(PanelBuilder builder, Dimension prefSize) {
		JScrollPane scroll = new JScrollPane(builder.getPanel());
		scroll.setPreferredSize(prefSize);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		return scroll;
	}

	public static JComponent createTextPane(String html, boolean scrollable) {
		JTextPane area = new JTextPane();
		area.setContentType("text/html");
		area.setText(html);
		area.setCaretPosition(0);
		area.setEditable(false);
		
		if (!scrollable) {
			area.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), 
					BorderFactory.createEmptyBorder(4,4,4,4)));
			return area;
		}
		
		return putTextPaneInScrollPane(area);
	}

	private static JComponent putTextPaneInScrollPane(JTextPane area) {
		JScrollPane pane = new JScrollPane(area);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.setPreferredSize(defaultTextPaneDimension(area));
		
		pane.setWheelScrollingEnabled(true);
		pane.getVerticalScrollBar().setValue(0);
		
		return pane;
	}
	
	public static JComponent createNoteView(Note note, boolean scrollable) {
		JTextPane area = new JTextPane();
		
		StyledDocument doc = area.getStyledDocument();
		AddStudyWizard.addStylesToDoc(doc);
		
		area.setBackground(COLOR_NOTE);
		
		try {
			switch (note.getSource()) {
			case CLINICALTRIALS:
				doc.insertString(doc.getLength(), NCT_NOTE + "\n", doc.getStyle("bold"));
				break;
			case MANUAL:
				doc.insertString(doc.getLength(), USER_NOTE + "\n", doc.getStyle("bold"));
				break;
			}
			doc.insertString(doc.getLength(), (String)note.getText(), doc.getStyle("regular"));
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}

		area.setEditable(false);
		if (!scrollable) {
			area.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), 
					BorderFactory.createEmptyBorder(4,4,4,4)));
			return area;
		}
		
		return putTextPaneInScrollPane(area);
	}
	
	/**
	 * Create a styled HTML text pane with a certain (HTML) body text. Use &lt;p&gt;'s to structure.
	 */
	public static JComponent createHtmlField(String bodyText) {	
		JLabel label = new JLabel("<html><div style='margin:0; padding: 10px;'>" + bodyText + "</div></html>", SwingConstants.CENTER);
		label.setOpaque(true);
		label.setBackground(COLOR_NOTE);
		label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		return label;
	}
	
	public static JLabel createAutoWrapLabel(ValueModel value) {
		return BasicComponentFactory.createLabel(new HTMLWrappingModel(value));
	}

	public static Dimension defaultTextPaneDimension(JTextPane area) {
		return AuxComponentFactory.textPaneDimension(area, 230, 50);
	}

	public static Dimension textPaneDimension(JTextPane area, int dluX, int dluY) {
		return new Dimension(
				DefaultUnitConverter.getInstance().dialogUnitXAsPixel(dluX, area), 
				DefaultUnitConverter.getInstance().dialogUnitYAsPixel(dluY, area));
	}

	public static JTextPane createTextPaneWithHyperlinks(String str) {
		return createTextPaneWithHyperlinks(str, COLOR_NOTE, true);
	}
	
	public static JTextPane createTextPaneWithHyperlinks(String str, Color bg, boolean opaque) {
		JTextPane pane = new JTextPane();
		pane.setBackground(bg);
		pane.setContentType("text/html");
		pane.setText(str);
		pane.setEditable(false);
		pane.setOpaque(opaque);
		pane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
					BrowserLaunch.openURL(e.getURL().toExternalForm());
				}
			}
		});
		return pane;
	}
}