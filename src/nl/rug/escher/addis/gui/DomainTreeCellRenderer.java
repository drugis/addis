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

package nl.rug.escher.addis.gui;

import java.awt.Component;
import java.io.FileNotFoundException;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import nl.rug.escher.addis.entities.CombinedStudy;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.addis.entities.MetaStudy;
import fi.smaa.common.ImageLoader;

@SuppressWarnings("serial")
public class DomainTreeCellRenderer extends DefaultTreeCellRenderer {

	private ImageLoader loader;
	
	public DomainTreeCellRenderer(ImageLoader loader) {
		this.loader = loader;
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value,	boolean sel,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		try {
			if (value instanceof Endpoint) {
				setIcon(loader.getIcon(FileNames.ICON_ENDPOINT));
				setToolTipText("Endpoint");
			} else if (value instanceof MetaStudy) {
				setIcon(loader.getIcon(FileNames.ICON_METASTUDY));
				setToolTipText("Study from meta-analysis");
			} else if (value instanceof BasicStudy) {
				setIcon(loader.getIcon(FileNames.ICON_STUDY));
				setToolTipText("Study");				
			}else if (value instanceof CombinedStudy) {
				setIcon(loader.getIcon(FileNames.ICON_COMBINEDSTUDY));
				setToolTipText("Combined study");				
			} else if (value instanceof Drug) {
				setIcon(loader.getIcon(FileNames.ICON_DRUG));
				setToolTipText("Drug");				
			} else {
				setToolTipText(null); //no tool tip
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return this;
	}
}
