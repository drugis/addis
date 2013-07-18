/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.drugis.addis.AppInfo;
import org.drugis.addis.presentation.ThreadHandlerPresentation;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.gui.LinkLabel;
import org.drugis.common.gui.OneWayObjectFormat;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public class StatusBar extends JToolBar {
	private ThreadHandlerPresentation d_ThreadHandlerPresentation = new ThreadHandlerPresentation();
	private JLabel d_verionLabel;

	public StatusBar(){
		super();
		setLayout(new BorderLayout());

		ButtonBarBuilder2 builder = new ButtonBarBuilder2();
		builder.addButton(createCounter("Running", "jobs", d_ThreadHandlerPresentation.getRunningThreads()));
		builder.addButton(createCounter("for", "user tasks", d_ThreadHandlerPresentation.getThreadsInQueue()));
		builder.addGlue();

		final ValueHolder<String> latestVersion = AppInfo.getLatestVersion();
		updateVersionLabel(latestVersion);
		latestVersion.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateVersionLabel(latestVersion);
			}
		});
		builder.addButton(d_verionLabel);
		builder.addRelatedGap();
		builder.addButton(GUIFactory.buildSiteLink());

		add(builder.getPanel(), BorderLayout.CENTER);
	}

	private void updateVersionLabel(ValueHolder<String> latestVersion) {
		d_verionLabel = new JLabel();
		if (AppInfo.compareVersion(latestVersion.getValue(), AppInfo.getAppVersion())) {
			d_verionLabel = new LinkLabel(
					"<font color=\"red\">new version available</font>",
					"http://drugis.org/files/addis-" + latestVersion.getValue() + ".zip");
			d_verionLabel.setForeground(Color.RED);

		}
	}

	private JPanel createCounter(String pre, String post, ValueModel threads) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.add(new JLabel(pre));
		panel.add(BasicComponentFactory.createLabel(threads, new OneWayObjectFormat()));
		panel.add(new JLabel(post));
		return panel;
	}


}
