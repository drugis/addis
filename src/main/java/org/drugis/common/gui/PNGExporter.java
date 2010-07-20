package org.drugis.common.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.drugis.addis.treeplot.Paintable;

public class PNGExporter {

	public static void writePNG(String filename, Paintable p, int width, int height) {
		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage b = config.createCompatibleImage(width, height, Transparency.OPAQUE);

		Graphics2D toWrite = b.createGraphics();
		toWrite.setBackground(Color.WHITE);
		toWrite.clearRect(0, 0, b.getWidth(), b.getHeight());
		toWrite.setColor(Color.BLACK);
		p.paint(toWrite);
		
		try{ImageIO.write(b,"png",new File(filename));}catch (Exception e) {e.printStackTrace();}
	}
	
	public static void writePNG(Component frame, Paintable p, int width, int height) {
		final JFileChooser fileChooser = new JFileChooser();
		int returnVal = fileChooser.showSaveDialog(frame);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				System.out.println("Writing to " + fileChooser.getSelectedFile().getAbsolutePath());
				writePNG(fileChooser.getSelectedFile().getAbsolutePath(), p, width, height);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(frame,
						"Couldn't save file "
								+ fileChooser.getSelectedFile()
										.getAbsolutePath() + " .");
				e1.printStackTrace();
			}
		}
	}
}
