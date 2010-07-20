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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

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
	
	
	public static void writePNG(Component frame, JComponent p, int width, int height) {
		final JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.addChoosableFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "PNG image";
			}
			
			@Override
			 public boolean accept(File f) {
		        if (f.isDirectory()) {
		            return true;
		        }
		        
		        String extension = getExtension(f);
		        if (extension != null) {
		            if (extension.equals("png")) {
		            	return true;
		        	} else {
		                return false;
		            }
		        }
		        return false;
			}
		});
		
		int returnVal = fileChooser.showSaveDialog(frame);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				String path = fixExtension(fileChooser.getSelectedFile().getAbsolutePath());
				writePNG(path, p, width, height);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(frame,
						"Couldn't save file "
								+ fileChooser.getSelectedFile()
										.getAbsolutePath() + " .");
				e1.printStackTrace();
			}
		}
	}
	
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    public static String fixExtension(String absPath) {
    	return absPath.toLowerCase().contains(".png") ? absPath : absPath+".png";
    }

}
