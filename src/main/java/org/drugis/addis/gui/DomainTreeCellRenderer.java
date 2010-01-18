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

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.AdverseDrugEvent;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.common.ImageLoader;

@SuppressWarnings("serial")
public class DomainTreeCellRenderer extends DefaultTreeCellRenderer {

	public Component getTreeCellRendererComponent(JTree tree, Object value,	boolean sel,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof Endpoint) {
			setIcon(ImageLoader.getIcon(FileNames.ICON_ENDPOINT));
			setToolTipText("Endpoint");
		}if (value instanceof AdverseDrugEvent) {
			setIcon(ImageLoader.getIcon(FileNames.ICON_ADE));
			setToolTipText("Adverse drug effect");
		} else if (value instanceof RandomEffectsMetaAnalysis) {
			setIcon(ImageLoader.getIcon(FileNames.ICON_METASTUDY));
			setToolTipText("Meta-analysis");
		} else if (value instanceof Study) {
			setIcon(ImageLoader.getIcon(FileNames.ICON_STUDY));
			setToolTipText("Study");				
		} else if (value instanceof Drug) {
			setIcon(ImageLoader.getIcon(FileNames.ICON_DRUG));
			setToolTipText("Drug");	
		} else if (value instanceof Indication) {
			setIcon(ImageLoader.getIcon(FileNames.ICON_INDICATION));
			setToolTipText("Indication");
		} else {
			setToolTipText(null); //no tool tip
		}
		return this;
	}
}
