/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
