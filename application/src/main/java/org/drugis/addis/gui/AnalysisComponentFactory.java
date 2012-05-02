package org.drugis.addis.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.FileNames;
import org.drugis.common.gui.task.TaskProgressBar;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.MixedTreatmentComparison.ExtendSimulation;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AnalysisComponentFactory {
		
	public static void createSimulationControls(
			final MCMCWrapper model, 
			final PanelBuilder builder, 
			final int row,
			final JFrame parent, boolean withSeparator) {

		final FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:0:grow, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		CellConstraints cc = new CellConstraints();
		PanelBuilder panelBuilder = new PanelBuilder(layout, new JPanel());
		panelBuilder.setDefaultDialogBorder();
		
		int panelRow = 1;
		if (withSeparator) {
			panelBuilder.addSeparator(model.toString(), cc.xyw(1, panelRow, 5));
			panelRow += 2;
		}
		
		createProgressBarRow(model, parent, cc, panelBuilder, panelRow, hasConvergence(model));
		panelRow += 2;
		if(hasConvergence(model)) { 
			panelBuilder.add(questionPanel(model), cc.xyw(1, panelRow, 3));
		}
			
		builder.add(panelBuilder.getPanel(), cc.xyw(1, row, 3));

	}

	private static JPanel questionPanel(final MCMCWrapper model) {
		final FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		final JButton extendSimulationButton = createExtendSimulationButton(model);
		final JButton stopButton = createStopButton(model.getActivityTask(), model);
		attachSimulationListeners(model, stopButton);
		attachSimulationListeners(model, extendSimulationButton);
		JPanel questionPanel = new JPanel(flowLayout);
		questionPanel.add(new JLabel("Has the simulation converged?"));
		questionPanel.add(stopButton);
		questionPanel.add(extendSimulationButton);
		return questionPanel;
	}

	private static void createProgressBarRow(final MCMCWrapper model,
			JFrame main, CellConstraints cc,
			PanelBuilder panelBuilder, int panelRow, boolean hasConvergence) {
		final JButton startButton = createStartButton(model.getActivityTask());
		panelBuilder.add(startButton, cc.xy(1, panelRow));
		if(hasConvergence) { 
			panelBuilder.add(new TaskProgressBar(model.getProgressModel()), cc.xy(3, panelRow));
			panelBuilder.add(createShowConvergenceButton(main, model), cc.xy(5, panelRow));
		} else {
			panelBuilder.add(new TaskProgressBar(model.getProgressModel()), cc.xyw(3, panelRow, 3));
		}
	}

	private static void attachSimulationListeners(final MCMCWrapper model, final JButton button) {
		for (Task t : model.getActivityTask().getModel().getStates()) { 		
			if (t.toString().equals("Assess convergence")) {
				t.addTaskListener(new TaskListener() {
					public void taskEvent(TaskEvent event) {
						if (event.getType() == EventType.TASK_STARTED) {
							button.setEnabled(true);
						}
						if (event.getType() == EventType.TASK_RESTARTED) {
							button.setEnabled(false);
						}
					}
				});
			}
			if (t.equals(model.getActivityTask().getModel().getEndState())) {
				t.addTaskListener(new TaskListener() {
					public void taskEvent(TaskEvent event) {
						if (event.getType() == EventType.TASK_FINISHED) {
							button.setEnabled(false);
						}
					}
				});
			}
		}
	}

	
	public static JButton createStartButton(final Task task) {
		final JButton button = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_RUN));
		button.setToolTipText("Run simulation");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				button.setEnabled(false);
				ThreadHandler.getInstance().scheduleTask(task);
			}
		});
		return button;
	}
	
	public static JButton createStopButton(final Task task, final MCMCWrapper model) {
		final JButton button = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_TICK));
		button.setText("Yes, finish");
		button.setToolTipText("Finish the simulation");
		button.setEnabled(false);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(task.isStarted()) { 
					((MixedTreatmentComparison) getMTCModel(model.getModel())).setExtendSimulation(ExtendSimulation.FINISH);
				}
			}
		});
		return button;
	}	

	public static JButton createExtendSimulationButton(final MCMCWrapper model) {
		JButton button = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_RESTART));
		button.setEnabled(false);
		button.setText("No, extend");
		button.setToolTipText("Extend the simulation");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((MixedTreatmentComparison) getMTCModel(model.getModel())).setExtendSimulation(ExtendSimulation.EXTEND);
			}
		});
		return button;
	}

	public static JButton createShowConvergenceButton(final JFrame main, final MCMCWrapper model) {
		JButton button = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_CURVE_CHART));
		button.setText("Show convergence");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog convergence = new ConvergenceSummaryDialog(main, getMTCModel(model.getModel()), model.isModelConstructed(), model.toString());
				convergence.setVisible(true);
			}
		});
		return button;
	}
	
	private static boolean hasConvergence(MCMCWrapper model) {
		if( model.getModel() instanceof MixedTreatmentComparison) {
			return true;
		} else { 
			return false;
		}
	}
	
	private static MixedTreatmentComparison getMTCModel(MCMCModel model) {
		if( model instanceof MixedTreatmentComparison) {
			return ((MixedTreatmentComparison) model);
		} else { 
			throw new IllegalArgumentException("Cannot get the MixedTreatmentComparison, was " + model.toString());
		}
	}
}
