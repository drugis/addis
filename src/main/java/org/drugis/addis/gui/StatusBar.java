package org.drugis.addis.gui;

import java.util.Formatter;

import javax.swing.JLabel;
import javax.swing.JToolBar;

import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.ThreadHandlerPresentation;
import org.drugis.common.gui.OneWayObjectFormat;

import com.jgoodies.binding.adapter.BasicComponentFactory;

import sun.util.calendar.BaseCalendar;

public class StatusBar extends JToolBar {
	private ThreadHandlerPresentation d_ThreadHandlerPresentation = new ThreadHandlerPresentation();
	
	public StatusBar(){
		super();
		add(new JLabel("  Running threads:"));
		add(BasicComponentFactory.createLabel(d_ThreadHandlerPresentation.getRunningThreads(), new OneWayObjectFormat()));
		add(new JLabel("    Threads in queue:"));
		add(BasicComponentFactory.createLabel(d_ThreadHandlerPresentation.getThreadsInQueue(), new OneWayObjectFormat()));
	}
	

}
