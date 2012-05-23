package org.drugis.addis.gui.components;

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
	private static final double DEFAULT_PROGRESS_BAR_HEIGHT = 7;
	private static final double PROGRESS_BAR_SPACING = 5;

	private String d_labelText;
	private final TaskProgressModel d_progressModel;
	
	public GraphProgressNode(Dimension gridCellSize, TaskProgressModel progressModel, String labelText) {
		this(gridCellSize, DEFAULT_LINE_WIDTH, DEFAULT_COLOR, progressModel, labelText);
	}
	
	public GraphProgressNode(Dimension gridCellSize, int lineWidth, Color color, TaskProgressModel progressModel, String labelText) {
		super(gridCellSize, lineWidth, color);
		
		d_labelText = labelText;
		d_progressModel = progressModel;
		
		TaskProgressBar tpb = new TaskProgressBar(d_progressModel);
		double pbWidth = getWidth() - PROGRESS_BAR_SPACING;
		tpb.setPreferredSize(new Dimension((int)pbWidth, (int)DEFAULT_PROGRESS_BAR_HEIGHT));
		
		setLayout(new BorderLayout(0, (int)d_lineWidth));
		add(tpb, BorderLayout.NORTH);
		
//		add(tpb);
//		Dimension s = getSize();
		//NOTE: maybe use setBounds as mentioned here? http://stackoverflow.com/questions/2820872/manually-position-jcomponent-inside-jpanel
//		Rectangle2D pbBounds = new Rectangle2D.Double(s.getWidth() / 2 - pbWidth / 2, s.getHeight() - d_lineWidth / 2 - DEFAULT_PROGRESS_BAR_HEIGHT / 2, pbWidth, DEFAULT_PROGRESS_BAR_HEIGHT);
//		tpb.setLocation((int)pbBounds.getMinX(), (int)(pbBounds.getMinY()));
		revalidate();
		tpb.setVisible(true);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;  
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Dimension s = getSize();
		
		g2.setColor(d_color);
		g2.setStroke(new BasicStroke((float)d_lineWidth));
		g2.draw(new RoundRectangle2D.Double(d_lineWidth / 2.0, d_lineWidth / 2.0, s.getWidth() - d_lineWidth, s.getHeight() - d_lineWidth, DEFAULT_ROUNDING_ARCH, DEFAULT_ROUNDING_ARCH));

		Rectangle2D textBounds = g2.getFontMetrics().getStringBounds(d_labelText, g);
		g2.drawString(d_labelText, (float)(s.width / 2 - textBounds.getCenterX()), (float)(s.height / 2 - textBounds.getCenterY()));
	}
}

