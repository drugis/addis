package org.drugis.addis.gui;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class LinkLabel extends JLabel{

	private String d_url;

	public LinkLabel (String text, String url) {
		super(generateLabel(text, url));
		d_url = url;
		this.addMouseListener(new ClickListener());
		this.setToolTipText("Go to " + url);
	}

	private static String generateLabel(String text, String url) {
		return "<html><a href=\""+url+"\">"+text+"</a></html>";
	}
	
	private class ClickListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent ev) {
			BrowserLaunch.openURL(d_url);
		}
		
		@Override
		public void mouseEntered(MouseEvent ev) {
			Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
			setCursor(handCursor);
		}
	}
	
}
