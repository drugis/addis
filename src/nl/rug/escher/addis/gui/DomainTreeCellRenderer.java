package nl.rug.escher.addis.gui;

import java.awt.Component;
import java.io.FileNotFoundException;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

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
			} else {
				setToolTipText(null); //no tool tip
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return this;
	}
}
