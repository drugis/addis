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
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SwingSVGPrettyPrint;
import org.drugis.common.gui.FileSaveDialog;
import org.jfree.chart.JFreeChart;
import org.jgraph.JGraph;
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
	
	public interface DrawCommand<T, C extends Graphics2D> {
		public void draw(T toDraw, C canvas, Dimension dim);
	}
	
	protected static <T> void writePNG(String path, DrawCommand<T, Graphics2D> drawer, T toDraw, Dimension dim) {
		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage bufferedImage = config.createCompatibleImage(dim.width, dim.height, Transparency.OPAQUE);

		Graphics2D canvas = bufferedImage.createGraphics();
		canvas.setBackground(Color.WHITE);
		canvas.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
		canvas.setColor(Color.BLACK);
		drawer.draw(toDraw, canvas, dim);
		
		writePNG(path, bufferedImage);
	}
	
	public static <T> void writeSVG(String path, DrawCommand<T, SVGGraphics2D> drawer, T toDraw, Dimension dim) {
        // Get a DOMImplementation and create an XML document
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();
        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		// draw the chart in the SVG generator
		svgGenerator.setSVGCanvasSize(dim);
		ImageWriterRegistry.getInstance().register(new ImageIOPNGImageWriter());
		drawer.draw(toDraw, svgGenerator, dim);
		
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
	
	public static void writeSVG(String path, JComponent p, int width, int height) {
		writeSVG(path, new DrawCommand<JComponent, SVGGraphics2D>() {
			public void draw(JComponent toDraw, SVGGraphics2D canvas, Dimension dim) {
				SwingSVGPrettyPrint.print(toDraw, canvas);
			}
		}, p, new Dimension(width, height));
	}

	protected static void writePNG(String path, JComponent p, int width, int height) {
		writePNG(path,  new DrawCommand<JComponent, Graphics2D>() {
			public void draw(JComponent toDraw, Graphics2D canvas, Dimension dim) {
				toDraw.paint(canvas);
			}
		}, p, new Dimension(width, height));
	}


	public static void writeImage(Component frame, final JGraph p, final int width, final int height) {
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
	
	protected static void writeSVG(String path, JGraph graph, int width, int height) {
		writeSVG(path, new DrawCommand<JGraph, SVGGraphics2D>() {
			public void draw(JGraph toDraw, SVGGraphics2D canvas, Dimension dim) {
		        toDraw.paint(canvas);
			}
		}, graph, new Dimension(width, height));
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
		writeSVG(path, new DrawCommand<JFreeChart, SVGGraphics2D>() {
			public void draw(JFreeChart toDraw, SVGGraphics2D canvas, Dimension dim) {
				toDraw.draw(canvas, new Rectangle(dim));
			}
		}, chart, new Dimension(width, height));
	}

	protected static void writePNG(String path, JFreeChart chart, int width, int height) {
		writePNG(path,  new DrawCommand<JFreeChart, Graphics2D>() {
			public void draw(JFreeChart toDraw, Graphics2D canvas, Dimension dim) {
				toDraw.draw(canvas, new Rectangle(dim.width, dim.height));
			}
		}, chart, new Dimension(width, height));
	}
	
	private static void writePNG(String filename, BufferedImage b) {
		try {
			ImageIO.write(b, "png", new File(filename));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
