package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.drugis.addis.AppInfo;
import org.drugis.addis.gui.components.LinkLabel;
import org.drugis.addis.presentation.ThreadHandlerPresentation;
import org.drugis.common.gui.OneWayObjectFormat;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public class StatusBar extends JToolBar {
	private ThreadHandlerPresentation d_ThreadHandlerPresentation = new ThreadHandlerPresentation();
	
	public StatusBar(){
		super();
		setLayout(new BorderLayout());

		ButtonBarBuilder2 builder = new ButtonBarBuilder2();
		builder.addButton(createCounter("Analyses running: ", d_ThreadHandlerPresentation.getRunningThreads()));
		builder.addButton(createCounter("Analyses waiting: ", d_ThreadHandlerPresentation.getThreadsInQueue()));
		builder.addGlue();
		
		String latestVersion = AppInfo.getLatestVersion();
		if (latestVersion != null) {
			LinkLabel linkLabel = new LinkLabel(
					"<font color=\"red\">new version available</font>",
					"http://drugis.org/files/addis-" + latestVersion + ".zip");
			linkLabel.setForeground(Color.RED);
			builder.addButton(linkLabel);
			builder.addRelatedGap();
		}
		builder.addButton(GUIFactory.buildSiteLink());

		add(builder.getPanel(), BorderLayout.CENTER);
	}

	private JPanel createCounter(String string, ValueModel threads) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.add(new JLabel(string));
		panel.add(BasicComponentFactory.createLabel(threads, new OneWayObjectFormat()));
		return panel;
	}
	

}
