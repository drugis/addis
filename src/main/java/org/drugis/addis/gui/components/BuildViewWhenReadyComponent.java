/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

package org.drugis.addis.gui.components;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.FileNames;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.ViewBuilder;

@SuppressWarnings("serial")
public class BuildViewWhenReadyComponent extends JPanel {
	private final ViewBuilder d_builder;
	private final ValueHolder<Boolean> d_readyModel;
	private final String d_message;

	/**
	 * Only show (build) the component once the value model has true. 
	 * Until then, show a waiting spinner and message.
	 * @param builder
	 * @param readyModel
	 * @param message
	 */
	public BuildViewWhenReadyComponent(ViewBuilder builder, 
			ValueHolder<Boolean> readyModel, String message) {
		d_builder = builder;
		d_readyModel = readyModel;
		d_readyModel.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				buildView();
			}
		});
		d_message = message;
		buildView();
	}

	private synchronized void buildView() {
		if (d_readyModel.getValue() == false) {
			buildWaitingView();
		} else {
			buildDoneView();
		}
	}
	
	protected void buildDoneView() {
		setVisible(false);
		removeAll();
		add(d_builder.buildPanel());
		setVisible(true);
	}

	private void buildWaitingView() {
		setVisible(false);
		removeAll();
		JLabel spinner = new JLabel(ImageLoader.getIcon(FileNames.ICON_LOADING_LARGE));
		JLabel label = new JLabel(d_message);
		JPanel nested = new JPanel(new BorderLayout());
		nested.add(spinner, BorderLayout.CENTER);
		nested.add(label, BorderLayout.SOUTH);
		add(nested);
		setVisible(true);
	}

	public static void main(String [] args) throws InterruptedException {
		ImageLoader.setImagePath("/org/drugis/addis/gfx/");
		JFrame frame = new JFrame();
		JPanel panel = new JPanel(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ViewBuilder builder = new ViewBuilder() {
			public JComponent buildPanel() {
				return new JLabel("Ok, time for coffee!");
			}
		};
		
		ValueHolder<Boolean> readyModel = new ModifiableHolder<Boolean>(false);
		
		panel.add(new JLabel("It's supposed to be doing that"), BorderLayout.NORTH);
		panel.add(new BuildViewWhenReadyComponent(builder, readyModel, "Please wait while I sleep"), BorderLayout.CENTER);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		
		Random gen = new Random();
		for (int i = 0; i < 100; ++i) {
			Thread.sleep(gen.nextInt(500));
			readyModel.setValue(gen.nextBoolean()); 
		}
		
		System.exit(0);
	}
}
