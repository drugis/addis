/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.apache.commons.lang.StringUtils;
import org.drugis.addis.FileNames;
import org.drugis.addis.entities.mtcwrapper.MCMCModelWrapper;
import org.drugis.addis.gui.components.progressgraph.GraphBar;
import org.drugis.addis.gui.components.progressgraph.GraphConnector;
import org.drugis.addis.gui.components.progressgraph.GraphLine;
import org.drugis.addis.gui.components.progressgraph.GraphProgressNode;
import org.drugis.addis.gui.components.progressgraph.GraphSimpleNode;
import org.drugis.addis.gui.components.progressgraph.GraphSimpleNode.GraphSimpleNodeType;
import org.drugis.common.gui.task.TaskProgressBar;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.common.threading.status.ActivityTaskInPhase;
import org.drugis.common.threading.status.TaskStartableModel;
import org.drugis.common.threading.status.TaskTerminatedModel;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.common.validation.BooleanNotModel;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MCMCModel.ExtendSimulation;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.Sizes;

public class AnalysisComponentFactory {
	
	public static JPanel createSimulationControls(
			final MCMCPresentation model, 
			final JFrame parent,
			boolean withSeparator,
			JButton ... buttons) {

		final FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:0:grow, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		CellConstraints cc = new CellConstraints();
		PanelBuilder panelBuilder = new PanelBuilder(layout);
		int panelRow = 1;
		if (withSeparator) {
			panelBuilder.addSeparator(model.toString(), cc.xyw(1, panelRow, 5));
			panelRow += 2;
		}
		
		createProgressBarRow(model, parent, cc, panelBuilder, panelRow, true, buttons);
		panelRow += 2;
		if(!model.hasSavedResults() && hasConvergence(model)) { 
			panelBuilder.add(questionPanel(model), cc.xyw(1, panelRow, 3));
		}
		
		panelBuilder.add(createProgressPanel(model), cc.xyw(1, panelRow + 2, 5));
		
		return panelBuilder.getPanel();
	}

	private static JPanel questionPanel(final MCMCPresentation model) {
		final FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		final JButton extendSimulationButton = createExtendSimulationButton(model);
		final JButton stopButton = createStopButton(model.getModel().getActivityTask(), model);
		
		ValueModel inAssessConvergence = new ActivityTaskInPhase(model.getModel().getActivityTask(), MCMCModel.ASSESS_CONVERGENCE_PHASE);
		ValueModel notTaskFinished = new BooleanNotModel(new TaskTerminatedModel(model.getModel().getActivityTask()));
		
		ValueModel shouldAssessConvergence = new BooleanAndModel(inAssessConvergence, notTaskFinished);
		
		Bindings.bind(extendSimulationButton, "enabled", shouldAssessConvergence);
		Bindings.bind(stopButton, "enabled", shouldAssessConvergence);

		
		JPanel questionPanel = new JPanel(flowLayout);
		questionPanel.add(new JLabel("Has the simulation converged?"));
		questionPanel.add(stopButton);
		questionPanel.add(extendSimulationButton);
		
		return questionPanel;
	}

	private static JPanel createProgressPanel(final MCMCPresentation model) {
		final int cellHeight = (int)new JProgressBar().getPreferredSize().getHeight() + 4;
		final Dimension gridCellSize = new Dimension(95, cellHeight);
		final int circleDiameter = 20;
		final int edgeLength = 35;
		final int arrowSize = 10;
		final int barWidth = 9;
		final int numberOfChains = model.getWrapper().getSettings().getNumberOfChains();
		final int numMainRows = (numberOfChains - 1) * 2 + 1;
		final int numTotalRows = numMainRows + 2;
		final int numCols = 17;
		
		final FormLayout layout = new FormLayout(
				createFormSpec("pref", numCols),
				"p, " + createFormSpec("3dlu, p", numTotalRows - 1));
		CellConstraints cc = new CellConstraints();
		JPanel progressPanel = new JPanel(layout);

		for(int i = 0; i < numberOfChains; ++i) {
			int rowIdx = (2 * i) + 1;
			progressPanel.add(new GraphLine(new Dimension(edgeLength, arrowSize), 2, SwingConstants.EAST), cc.xy(6, rowIdx));
			progressPanel.add(new GraphProgressNode(gridCellSize, model.getProgressModel(), "burn-in " + i), cc.xy(7, rowIdx));

			progressPanel.add(new GraphLine(new Dimension(edgeLength * 2, arrowSize), 2, SwingConstants.EAST), cc.xy(9, rowIdx));
			progressPanel.add(new GraphProgressNode(gridCellSize, model.getProgressModel(), "simulation " + i ), cc.xy(10, rowIdx));
			progressPanel.add(new GraphLine(new Dimension(edgeLength, arrowSize), 2, SwingConstants.EAST), cc.xy(11, rowIdx));	
		}
		
		/** Placement needed for the calculated preferred size */
		progressPanel.add(new GraphSimpleNode(new Dimension(circleDiameter,circleDiameter), GraphSimpleNodeType.START), centerCell(cc, numMainRows, 1));
		progressPanel.add(new GraphLine(new Dimension(edgeLength, arrowSize), 2, SwingConstants.EAST), centerCell(cc, numMainRows, 2));
		progressPanel.add(new GraphProgressNode(gridCellSize, model.getProgressModel(), "build model", false), centerCell(cc, numMainRows, 3));
		progressPanel.add(new GraphLine(new Dimension(edgeLength, arrowSize), 2, SwingConstants.EAST), centerCell(cc, numMainRows, 4));
		//NOTE: it is a mystery why numMainRows - 1 is the correct count instead of just numMainRows
		progressPanel.add(new GraphBar(new Dimension(barWidth, (int)progressPanel.getPreferredSize().getHeight())), centerCell(cc, numMainRows - 1, 5));
		progressPanel.add(new GraphBar(new Dimension(barWidth, (int)progressPanel.getPreferredSize().getHeight())), centerCell(cc, numMainRows - 1, 12));
		
		progressPanel.add(new GraphLine(new Dimension(edgeLength, arrowSize), 2, SwingConstants.EAST), centerCell(cc, numMainRows, 13));
		progressPanel.add(new GraphProgressNode(gridCellSize, model.getProgressModel(), "assess convergence", false), centerCell(cc, numMainRows, 14));
		progressPanel.add(new GraphLine(new Dimension(arrowSize, 50), 2, SwingConstants.SOUTH), cc.xywh(14, numMainRows / 2 + 2, 1,  numMainRows / 2 + 1, CellConstraints.CENTER, CellConstraints.BOTTOM));
		progressPanel.add(new GraphSimpleNode(new Dimension(circleDiameter,circleDiameter), GraphSimpleNodeType.DECISION), cc.xywh(14, numMainRows + 2, 1, 1, CellConstraints.CENTER, CellConstraints.CENTER));
		progressPanel.add(new GraphLine(new Dimension(edgeLength + 4, arrowSize), 2, SwingConstants.EAST), cc.xyw(14, numMainRows + 2, 2, CellConstraints.RIGHT, CellConstraints.DEFAULT));
		progressPanel.add(new GraphSimpleNode(new Dimension(circleDiameter,circleDiameter), GraphSimpleNodeType.END), cc.xy(16, numMainRows + 2));
		progressPanel.add(new GraphLine(new Dimension(edgeLength * 6 + 2, arrowSize), 2, SwingConstants.WEST), cc.xyw(10, numMainRows + 2, 14 - 7, CellConstraints.LEFT, CellConstraints.DEFAULT));
		progressPanel.add(new GraphBar(new Dimension(edgeLength * 2, barWidth)), cc.xy(9, numMainRows + 2));

		int totalHeight = (int)progressPanel.getPreferredSize().getHeight();
		progressPanel.add(new GraphConnector(new Dimension(edgeLength * 2, totalHeight), cellHeight + Sizes.DLUY3.getPixelSize(progressPanel), totalHeight - 30, numberOfChains), cc.xywh(9, 1, 1, numTotalRows));
		return progressPanel;
	}

	private static String createFormSpec(String rowSpec, final int numRows) {
		String[] rowArray = new String[numRows];
		Arrays.fill(rowArray, rowSpec);
		String completeRowSpec = StringUtils.join(rowArray, ",");
		return completeRowSpec;
	}

	private static CellConstraints centerCell(CellConstraints cc, int rowSpan, int col) {
		return cc.xywh(col, 1, 1, rowSpan, CellConstraints.CENTER, CellConstraints.CENTER);
	}
	
	private static void createProgressBarRow(final MCMCPresentation model,
			JFrame main, CellConstraints cc,
			PanelBuilder panelBuilder, int panelRow, boolean hasConvergence, JButton[] buttons) {
		JButton startButton = createStartButton(model);
		
		JPanel bb = new JPanel();
		bb.add(startButton);	
		for(JButton b : buttons) { 
			bb.add(b);
		}
		panelBuilder.add(bb, cc.xy(1, panelRow));

		if(hasConvergence) { 
			panelBuilder.add(new TaskProgressBar(model.getProgressModel()), cc.xy(3, panelRow));
			panelBuilder.add(createShowConvergenceButton(main, model), cc.xy(5, panelRow));
		} else {
			panelBuilder.add(new TaskProgressBar(model.getProgressModel()), cc.xyw(3, panelRow, 3));
		}
	}

	public static JButton createStartButton(final MCMCPresentation model) {
		final JButton button = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_RUN));
		button.setToolTipText("Run simulation");
		
		if (model.getModel() == null) {
			button.setEnabled(false);
			return button;
		}
		
		ValueModel buttonEnabledModel = new TaskStartableModel(model.getModel().getActivityTask());
		Bindings.bind(button, "enabled", buttonEnabledModel);

		final ActivityTask task = model.getModel().getActivityTask();
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ThreadHandler.getInstance().scheduleTask(task);
			}
		});
		
		return button;
	}
	

	
	public static JButton createStopButton(final Task task, final MCMCPresentation presentation) {
		final JButton button = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_TICK));
		button.setText("Yes, finish");
		button.setToolTipText("Finish the simulation");
		
		final MCMCModelWrapper wrapper = presentation.getWrapper();
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(task.isStarted()) { 
					wrapper.getModel().setExtendSimulation(ExtendSimulation.FINISH);
				}
			}
		});
		return button;
	}	

	public static JButton createExtendSimulationButton(final MCMCPresentation presentation) {
		JButton button = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_RESTART));
		button.setText("No, extend");
		button.setToolTipText("Extend the simulation");
		
		final MCMCModelWrapper wrapper = presentation.getWrapper();
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wrapper.getModel().setExtendSimulation(ExtendSimulation.EXTEND);
			}
		});
		return button;
	}

	public static JButton createShowConvergenceButton(final JFrame main, final MCMCPresentation presentation) {
		JButton button = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_CURVE_CHART));
		button.setText("Show convergence");
		final MCMCModelWrapper wrapper = presentation.getWrapper();
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog convergence = new ConvergenceSummaryDialog(main, wrapper, presentation.isModelConstructed(), presentation.toString());
				convergence.setVisible(true);
			}
		});
		return button;
	}

	private static boolean hasConvergence(MCMCPresentation model) {
		return true;
	}
}
