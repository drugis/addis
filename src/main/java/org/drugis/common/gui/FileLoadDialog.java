package org.drugis.common.gui;

import java.awt.Component;

public abstract class FileLoadDialog extends FileDialog {

	public FileLoadDialog(Component frame, String extension, String description) {
		super(frame, extension, description);


		String message = "Couldn't open file ";

		int returnValue = d_fileChooser.showOpenDialog(frame);
		handleFileDialogResult(frame, returnValue, message);
	}
}