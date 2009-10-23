/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.Study;
import org.drugis.common.ImageLoader;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;

public class GUIFactory {
	public static JButton createPlusButton(ImageLoader loader, String toolTipText) {
		JButton button = new JButton(toolTipText);
		try {
			Icon icon = loader.getIcon(FileNames.ICON_PLUS);
			button = new JButton(icon);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		button.setToolTipText(toolTipText);
		return button;
	}

	public static JComponent createEndpointLabelWithIcon(ImageLoader loader, Study s, Endpoint e) {
		String fname = FileNames.ICON_STUDY;
		if (s instanceof MetaStudy) {
			MetaStudy ms = (MetaStudy) s;
			if (ms.getAnalysis().getEndpoint().equals(e)) {
				fname = FileNames.ICON_METASTUDY;
			}
		}
		JLabel textLabel = null;
		try {
			Icon icon = loader.getIcon(fname);
			textLabel = new JLabel(e.getName(), icon, JLabel.CENTER);			
		} catch (FileNotFoundException ex) {
			textLabel = new JLabel(e.getName());			
			ex.printStackTrace();
		}
		Bindings.bind(textLabel, "text", 
				new PresentationModel<Endpoint>(e).getModel(Endpoint.PROPERTY_NAME));
		return textLabel;
	}

	public static JComboBox createDrugSelector(PresentationModel<BasicPatientGroup> model, Domain domain) {
		SelectionInList<Drug> drugSelectionInList =
			new SelectionInList<Drug>(
					new ArrayList<Drug>(domain.getDrugs()),
					model.getModel(BasicPatientGroup.PROPERTY_DRUG));
		return BasicComponentFactory.createComboBox(drugSelectionInList);
	}	
}
