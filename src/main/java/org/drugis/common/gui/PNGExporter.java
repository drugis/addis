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
import javax.swing.JComponent;

public class PNGExporter {

	public static void writePNG(String filename, JComponent p, int width, int height) {
		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage bufferedImage = config.createCompatibleImage(width, height, Transparency.OPAQUE);

		Graphics2D toWrite = bufferedImage.createGraphics();
		toWrite.setBackground(Color.WHITE);
		toWrite.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
		toWrite.setColor(Color.BLACK);
		p.paint(toWrite);
		
		writePNG(filename, bufferedImage);
	}


	public static void writePNG(String filename, BufferedImage b) {
		try{ImageIO.write(b,"png",new File(filename));}catch (Exception e) {e.printStackTrace();}
	}
	
	
	public static void writePNG(Component frame, final JComponent p, final int width, final int height) {
		new FileSaveDialog(frame, "png", "PNG files") {
			@Override
			public void save(String path) {
				writePNG(path, p, width, height);
			}
		};
	}
}
