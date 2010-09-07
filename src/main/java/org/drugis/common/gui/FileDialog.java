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
	    
		public String getPresentExtension() {
			return d_extension;
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
    	if (ext == null || ext.equals("")) {
    		return absPath;
    	}
    	if (absPath.toLowerCase().substring(absPath.lastIndexOf('.') + 1, absPath.length()).equals(ext)) {
    		return absPath;
    	}
    	return absPath + "." + ext;
    }

    public FileDialog(Component frame, String extension, String description){
    	this(frame, new String [] {extension}, new String [] {description});
    }
	
	public FileDialog(Component frame, String [] extension, String [] description) {
		
		d_fileChooser = new JFileChooser();
		CustomFileFilter defaultFilter = null;
		for(int i=0; i< extension.length; i++) {
			CustomFileFilter filter = new CustomFileFilter(extension[i], description[i]);
			d_fileChooser.addChoosableFileFilter(filter);
			if (i == 0) {
				defaultFilter = filter;
			}
		}
		d_fileChooser.setFileFilter(defaultFilter);
		if (d_currentDirectory != null)
			d_fileChooser.setCurrentDirectory(d_currentDirectory);
	}
	
	protected void handleFileDialogResult(Component frame, int returnVal, String message) {
		d_currentDirectory = d_fileChooser.getCurrentDirectory();
		String extension = getExtension();
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String path = fixExtension(d_fileChooser.getSelectedFile().getAbsolutePath(),extension);
			try {
				doAction(path, extension);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(frame, message + "\n" +
						d_fileChooser.getSelectedFile().getAbsolutePath());
				e1.printStackTrace();
			}
		}
	}

	private String getExtension() {
		if(d_fileChooser.getFileFilter() instanceof CustomFileFilter) {
			return ((CustomFileFilter) d_fileChooser.getFileFilter()).getPresentExtension();
		} else {
			return "";
		}
	}

	public abstract void doAction(String path, String extension);
	
}
