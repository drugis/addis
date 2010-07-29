package org.drugis.common.gui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public abstract class FileSaveDialog {

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


    
    public static String fixExtension(String absPath, String ext) {
    	return absPath.toLowerCase().contains("."+ext) ? absPath : absPath+"."+ext;
    }

	
	public FileSaveDialog(Component frame, String extension, final String description) {
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new CustomFileFilter(extension, description));
		
		int returnVal = fileChooser.showSaveDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				String path = fixExtension(fileChooser.getSelectedFile().getAbsolutePath(),extension);
				save(path);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(frame,
						"Couldn't save file "
								+ fileChooser.getSelectedFile()
										.getAbsolutePath() + " .");
				e1.printStackTrace();
			}
		}
}

	public abstract void save(String path);
	
}
