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

package org.drugis.addis.gui.builder;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.Document;

import org.drugis.addis.entities.Indication;
import org.drugis.addis.gui.util.NonEmptyValueModel;
import org.drugis.common.gui.ViewBuilder;
import org.drugis.common.validation.BooleanAndModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddIndicationView implements ViewBuilder {
	private JFormattedTextField d_code;
	private JTextField d_name;
	private PresentationModel<Indication> d_model;
	private BooleanAndModel d_validator = new BooleanAndModel();
	
	public AddIndicationView(PresentationModel<Indication> model, JButton okButton) {
		Bindings.bind(okButton, "enabled", d_validator);
		d_model = model;
	}
	
	public void initComponents() {
		d_code = new JFormattedTextField(new DefaultFormatter());
		d_code.setColumns(18);
		PropertyConnector.connectAndUpdate(d_model.getModel(Indication.PROPERTY_CODE), d_code, "value");
		d_name = BasicComponentFactory.createTextField(d_model.getModel(Indication.PROPERTY_NAME), false);
		
		d_validator.add(new DocumentNotEmptyModel(d_code.getDocument()));
		d_validator.add(new NonEmptyValueModel(d_model.getModel(Indication.PROPERTY_NAME)));

	}

	public JComponent buildPanel() {
		initComponents();
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Indication", cc.xyw(1, 1, 3));
		builder.addLabel("SNOMED Concept ID:", cc.xy(1, 3));
		builder.add(d_code, cc.xy(3,3));
		builder.addLabel("Fully Specified Name:", cc.xy(1, 5));
		builder.add(d_name, cc.xy(3,5));
		
		return builder.getPanel();	
	}

	
	private static class DocumentNotEmptyModel extends AbstractValueModel {
		private static final long serialVersionUID = 843575902496056597L;
		private Document d_document;

		public DocumentNotEmptyModel(Document document) {
			d_document = document;
			document.addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent ev) {
					fireValueChange(null, getValue());			
				}

				public void insertUpdate(DocumentEvent ev) {
					fireValueChange(null, getValue());
				}

				public void removeUpdate(DocumentEvent ev) {
					fireValueChange(null, getValue());
				}
			});
		}
		
		@Override
		public Object getValue() {
				return (d_document.getLength() != 0);
		}

		@Override
		public void setValue(Object newValue) {
			throw new RuntimeException("Modification not allowed");
		}
	}
}
