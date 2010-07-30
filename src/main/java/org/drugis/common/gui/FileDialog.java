package org.drugis.common.gui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public abstract class FileDialog {
	
	private class CustomFileFilter extends FileFilter
	{
		private final String d_description;
		private final String d_extension;

		public CustomFileFilter(String extension, String description) {
			d_extension = extension;
			d_description = description;
			
		}
		@Override
		public String getDescription() {
			return d_description;
		}
			
		private String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
	            ext = s.substring(i+1).toLowerCase();
	        }
	        return ext;
	    }
	    
		
		@Override
		 public boolean accept(File f) {
	        if (f.isDirectory()) {
	            return true;
	        }
	        
	        String extension = getExtension(f);
	        if (extension != null) {
	            if (extension.equals(d_extension)) {
	            	return true;
	        	} else {
	                return false;
	            }
	        }
	        return false;
		}
	}

	static File d_currentDirectory = null;
	protected JFileChooser d_fileChooser;
    
    public static String fixExtension(String absPath, String ext) {
    	return absPath.toLowerCase().contains("."+ext) ? absPath : absPath+"."+ext;
    }

	
	public FileDialog(Component frame, String extension, final String description) {
		
		d_fileChooser = new JFileChooser();
		d_fileChooser.addChoosableFileFilter(new CustomFileFilter(extension, description));
		
		if (d_currentDirectory != null)
			d_fileChooser.setCurrentDirectory(d_currentDirectory);
			
	
}
	
	protected void handleFileDialogResult(Component frame, String extension,
			int returnVal, String message) {
		d_currentDirectory = d_fileChooser.getCurrentDirectory();
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				String path = fixExtension(d_fileChooser.getSelectedFile().getAbsolutePath(),extension);
				doAction(path);
			} catch (Exception e1) {
				
				JOptionPane.showMessageDialog(frame,
								message
								+ d_fileChooser.getSelectedFile()
										.getAbsolutePath() + " .");
				e1.printStackTrace();
			}
		}
	}

	public abstract void doAction(String path);
	
}
