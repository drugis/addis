package org.drugis.addis.gui.util;

import java.awt.Color;

import javax.swing.JFrame;

import org.drugis.common.gui.ImageExporter;
import org.jgraph.JGraph;

public class JGraphUtil {

	public static void writeGraphImage(JFrame frame, JGraph graph) {
		Color oldCol = graph.getBackground();
		graph.setBackground(Color.white);
		graph.setDoubleBuffered(false);
		ImageExporter.writeImage(frame, graph, graph.getPreferredSize().width, graph.getPreferredSize().height);
		graph.setDoubleBuffered(true);
		graph.setBackground(oldCol);
	}

}
