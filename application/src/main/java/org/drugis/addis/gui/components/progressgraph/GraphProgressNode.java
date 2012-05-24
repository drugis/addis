package org.drugis.addis.gui.components.progressgraph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.SwingConstants;

import org.drugis.common.gui.task.TaskProgressBar;
import org.drugis.common.gui.task.TaskProgressModel;

public class GraphProgressNode extends GraphComponent implements SwingConstants {
	private static final long serialVersionUID = 7151331776919970759L;

	private static final double DEFAULT_ROUNDING_ARCH = 5;

	private String d_labelText;
	private final TaskProgressModel d_progressModel;

	private final boolean d_hasProgress;
	
	public GraphProgressNode(Dimension gridCellSize, TaskProgressModel progressModel, String labelText) {
		this(gridCellSize, DEFAULT_LINE_WIDTH, DEFAULT_COLOR, progressModel, labelText);
	}
	
	public GraphProgressNode(Dimension gridCellSize, int lineWidth, Color color, TaskProgressModel progressModel, String labelText) {
		this(gridCellSize, lineWidth, color, progressModel, labelText, true);
	}
	
	
	public GraphProgressNode(Dimension gridCellSize, TaskProgressModel progressModel, String labelText, boolean hasProgress) {
		this(gridCellSize, DEFAULT_LINE_WIDTH, DEFAULT_COLOR, progressModel, labelText, hasProgress);
	}
	
	
	public GraphProgressNode(Dimension gridCellSize, int lineWidth, Color color, TaskProgressModel progressModel, String labelText, boolean hasProgress) {
		super(gridCellSize, lineWidth, color);
		
		d_labelText = labelText;
		d_progressModel = progressModel;
		d_hasProgress = hasProgress;
		if(hasProgress) { 
			TaskProgressBar tpb = new TaskProgressBar(d_progressModel);			
			setLayout(new BorderLayout(0, 0));
			add(tpb, BorderLayout.NORTH);
			tpb.setVisible(true);
			revalidate();
		}	
	}


	@Override
	protected void paintComponent(Graphics g) {
		if(!d_hasProgress) {
			Graphics2D g2 = (Graphics2D)g;  
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Dimension s = getPreferredSize();
			g2.setStroke(new BasicStroke((float)d_lineWidth));
			RoundRectangle2D.Double s2 = new RoundRectangle2D.Double(d_lineWidth / 2.0, d_lineWidth / 2.0, s.getWidth() - d_lineWidth, s.getHeight() - d_lineWidth, DEFAULT_ROUNDING_ARCH, DEFAULT_ROUNDING_ARCH);
			g2.setPaint(Color.YELLOW);
			g2.fill(s2);
			g2.setPaint(DEFAULT_COLOR);
			g2.draw(s2);

			Rectangle2D textBounds = g2.getFontMetrics().getStringBounds(d_labelText, g);
			g2.drawString(d_labelText, (float)(s.width / 2 - textBounds.getCenterX()), (float)(s.height / 2 - textBounds.getCenterY()));
		}
	}
}

