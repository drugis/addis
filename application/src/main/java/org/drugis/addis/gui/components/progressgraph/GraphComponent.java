/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.gui.components.progressgraph;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;

public abstract class GraphComponent extends JComponent {
	private static final long serialVersionUID = -2929257889835014286L;
	
	protected static final int DEFAULT_LINE_WIDTH = 2;
	protected static final Color DEFAULT_COLOR = Color.BLACK;
	
	protected int d_lineWidth;
	protected Color d_color;

	public GraphComponent(Dimension gridCellSize) {
		this(gridCellSize, DEFAULT_LINE_WIDTH, DEFAULT_COLOR);
	}
	
	public GraphComponent(Dimension gridCellSize, int lineWidth, Color color) {
		super();
		
		d_color = color;
		d_lineWidth = lineWidth;
		
		setPreferredSize(gridCellSize);
		setMinimumSize(gridCellSize);
		setMaximumSize(gridCellSize);
		revalidate();
	}
}