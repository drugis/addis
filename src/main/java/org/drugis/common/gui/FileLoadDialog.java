package org.drugis.common.gui;

import java.awt.Component;

public abstract class FileLoadDialog extends FileDialog {

	private int d_returnValue;

	public FileLoadDialog(Component frame, String extension, String description) {
		super(frame, extension, description);

		String message = "Couldn't open file ";

		d_returnValue = d_fileChooser.showOpenDialog(frame);
		handleFileDialogResult(frame, d_returnValue, message);
	}
	
	public int getReturnValue() {
		return d_returnValue;
	}
}