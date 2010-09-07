package org.drugis.common.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;


public class ImageExporter {

	private static void writePNG(String filename, JComponent p, int width, int height) {
		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage bufferedImage = config.createCompatibleImage(width, height, Transparency.OPAQUE);

		Graphics2D toWrite = bufferedImage.createGraphics();
		toWrite.setBackground(Color.WHITE);
		toWrite.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
		toWrite.setColor(Color.BLACK);
		p.paint(toWrite);
		
		writePNG(filename, bufferedImage);
	}

	private static void writePNG(String filename, BufferedImage b) {
		try{ImageIO.write(b,"png",new File(filename));}catch (Exception e) {e.printStackTrace();}
	}
	
	private static void writeSVG(String filename, JComponent p, int width, int height){
		// Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        // Setting image dimensions
        svgGenerator.setSVGCanvasSize(new Dimension(width,height));
        // setting background to white :_)
        svgGenerator.setBackground(Color.WHITE);
        svgGenerator.clearRect(0, 0, width, height);
        svgGenerator.setColor(Color.BLACK);
        // Ask the test to render into the SVG Graphics2D implementation.
        p.paint(svgGenerator);
        // Finally, stream out SVG to the standard output using UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
        FileWriter out;    
        try{out = new FileWriter(filename); svgGenerator.stream(out, useCSS);}
        catch (Exception e) {e.printStackTrace();}	
	}
	
	public static void writeImage(Component frame, final JComponent p, final int width, final int height) {
		String [] extensions = {"png", "svg"};
		String [] descriptions = {"PNG files", "SVG files"};
		new FileSaveDialog(frame, extensions, descriptions) {
			@Override
			public void doAction(String path, String extension) {
				if (extension.equals("png"))
					writePNG(path, p, width, height);
				else if (extension.equals("svg"))
					writeSVG(path, p, width, height);
				else
					throw new IllegalArgumentException("Unknown extension " + extension);
			}
		};
	}
}
