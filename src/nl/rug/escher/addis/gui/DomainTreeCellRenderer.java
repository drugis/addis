/*
	This file is part of JSMAA.
	(c) Tommi Tervonen, 2009	

    JSMAA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JSMAA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JSMAA.  If not, see <http://www.gnu.org/licenses/>.
*/

package nl.rug.escher.addis.gui;

import java.awt.Component;
import java.io.FileNotFoundException;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.BasicStudy;
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
			} else if (value instanceof BasicStudy) {
				setIcon(loader.getIcon(FileNames.ICON_STUDY));
				setToolTipText("Study");				
			} else {
				setToolTipText(null); //no tool tip
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return this;
	}
}
