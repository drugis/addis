package org.drugis.addis.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.ext.awt.image.codec.imageio.ImageIOPNGImageWriter;
import org.apache.batik.ext.awt.image.codec.png.PNGImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SwingSVGPrettyPrint;
import org.drugis.common.gui.FileSaveDialog;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class ImageExporterExperimental {
	
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
	
	public static void writeSVG(String path, JComponent p, int width,
			int height) {
        // Get a DOMImplementation and create an XML document
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();
        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        Dimension dim = new Dimension(width, height);
		// draw the chart in the SVG generator
		svgGenerator.setSVGCanvasSize(dim);
		ImageWriterRegistry.getInstance().register(new ImageIOPNGImageWriter());
		SwingSVGPrettyPrint.print(p, svgGenerator);
		
        // Write svg file
        OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(path);
	        Writer out = new OutputStreamWriter(outputStream, "UTF-8");
	        svgGenerator.stream(out, true /* use css */);						
	        outputStream.flush();
	        outputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected static void writePNG(String path, JComponent p, int width,
			int height) {
		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage bufferedImage = config.createCompatibleImage(width, height, Transparency.OPAQUE);

		Graphics2D toWrite = bufferedImage.createGraphics();
		toWrite.setBackground(Color.WHITE);
		toWrite.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
		toWrite.setColor(Color.BLACK);
		p.paint(toWrite);
		
		writePNG(path, bufferedImage);
	}

	public static void writeImage(Component frame, final JFreeChart p, final int width, final int height) {
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

	protected static void writeSVG(String path, JFreeChart chart, int width, int height) {
        // Get a DOMImplementation and create an XML document
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);

        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        Dimension dim = new Dimension(width, height);
		// draw the chart in the SVG generator
		svgGenerator.setSVGCanvasSize(dim);
        chart.draw(svgGenerator, new Rectangle(dim));

        // Write svg file
        OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(path);
	        Writer out = new OutputStreamWriter(outputStream, "UTF-8");
	        svgGenerator.stream(out, true /* use css */);						
	        outputStream.flush();
	        outputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected static void writePNG(String path, JFreeChart chart, int width,
			int height) {
		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage bufferedImage = config.createCompatibleImage(width, height, Transparency.OPAQUE);

		Graphics2D toWrite = bufferedImage.createGraphics();
		toWrite.setBackground(Color.WHITE);
		toWrite.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
		toWrite.setColor(Color.BLACK);
		chart.draw(toWrite, new Rectangle(width, height));
		
		writePNG(path, bufferedImage);
	}
	
	private static void writePNG(String filename, BufferedImage b) {
		try{ImageIO.write(b,"png",new File(filename));}catch (Exception e) {e.printStackTrace();}
	}
}
