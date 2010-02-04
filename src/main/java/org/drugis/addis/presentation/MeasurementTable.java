package org.drugis.addis.presentation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.GUIHelper;

import com.jgoodies.binding.PresentationModel;

public class MeasurementTable extends JTable{
	private static final long serialVersionUID = -5815104084298298455L;
	
	private Window d_frame;
	
	public MeasurementTable(TableModel tableModel, Window parent) {
		super(tableModel);
		//this.setDefaultEditor(Object.class, new MeasurementsTableCellEditor());
		d_frame = parent;
		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
		TableColumn column = null;
		for (int i = 0; i < getModel().getColumnCount(); i++) {
		    column = getColumnModel().getColumn(i);
		        column.setPreferredWidth(50);
		}
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = ((JTable)e.getComponent()).rowAtPoint(e.getPoint());
				int col = ((JTable)e.getComponent()).columnAtPoint(e.getPoint());
				
				if (col > 0) {
					JWindow measurement = new EnterMeasurementWindow(d_frame,row,col);
					measurement.requestFocusInWindow();
				}
				
				//measurement.requestFocus();
			}
		});
	}
	
	public static class NothingFocussedListener implements FocusListener {
		List<Component> d_components = new ArrayList<Component>();
		JWindow d_window;
		
		public NothingFocussedListener(JWindow window) {
			d_window = window;
		}
		
		public void focusGained(FocusEvent arg0) {
			// TODO Auto-generated method stub
			System.out.println("gained focus");
			d_window.setVisible(true);
		}

		public void focusLost(FocusEvent arg0) {
			System.out.println("disposing JWindow beging");
			for(Component c : d_components)
				if(c.isFocusOwner())
					return;
			d_window.setVisible(false);
			
			System.out.println("disposing JWindow end");
		}
		
		public void addComponent(Component c){
			c.addFocusListener(this);
			d_components.add(c);
		}

	}
	
	@SuppressWarnings("serial")
	public class EnterMeasurementWindow extends JWindow{
		

		private NothingFocussedListener d_nothingFocussedListener;
		
		public EnterMeasurementWindow(Window parent, int row, int col) {
			super(parent);
			
			d_nothingFocussedListener = new NothingFocussedListener(this);
			
			// Retrieve value-model
			PresentationModel<Measurement> cellModel = ((PresentationModel<Measurement>)getModel().getValueAt(row, col));
			
			// Create Panel with input components.
			JPanel importPanel = new JPanel();
			if(cellModel.getBean() instanceof ContinuousMeasurement)
				makeContinuousInputfield(importPanel, cellModel);
			else if (cellModel.getBean() instanceof RateMeasurement)
				makeRateInputfield(importPanel, cellModel);
			/*
			// Calculate the location of the window, and move it there.
			Rectangle cellLocation = getCellRect(row, col, false);
			Point l = getComponentAt(col, row).getLocationOnScreen();
			l.translate(cellLocation.x, cellLocation.y);
			System.out.println("Location: "+l);
			setLocation(l);*/
			GUIHelper.centerWindow(this,parent);
			
			// Add the panel to the window, and make the window visible.
			getContentPane().add(importPanel, BorderLayout.CENTER);
			setVisible(true);
			pack();
			
			requestFocus();
		}
		
		private void makeRateInputfield(JPanel importPanel, PresentationModel<?> cellModel) {
			importPanel.setLayout(new FlowLayout());
			importPanel.add(new JLabel("Rate: "));
			JTextField d_rateField = AuxComponentFactory.createNonNegativeIntegerTextField(cellModel.getModel(RateMeasurement.PROPERTY_RATE));
			
			d_nothingFocussedListener.addComponent(d_rateField);
			importPanel.add(d_rateField);
			importPanel.add(new JLabel("Size: "));
			JTextField sizeField = AuxComponentFactory.createNonNegativeIntegerTextField(cellModel.getModel(RateMeasurement.PROPERTY_SAMPLESIZE));
			d_nothingFocussedListener.addComponent(sizeField);

			importPanel.add(sizeField);
		}

		private void makeContinuousInputfield(JPanel importPanel, PresentationModel<?> cellModel) {
			importPanel.setLayout(new FlowLayout());
			importPanel.add(new JLabel("Mean: "));
			JTextField meanField = AuxComponentFactory.createDoubleTextField(cellModel.getModel(ContinuousMeasurement.PROPERTY_MEAN));
			d_nothingFocussedListener.addComponent(meanField);
			importPanel.add(meanField);
			importPanel.add(new JLabel("Std dev: "));
			JTextField stddevField = AuxComponentFactory.createDoubleTextField(cellModel.getModel(ContinuousMeasurement.PROPERTY_STDDEV));
			d_nothingFocussedListener.addComponent(stddevField);
			importPanel.add(stddevField);
			importPanel.add(new JLabel("Size: "));
			JTextField sizeField = AuxComponentFactory.createNonNegativeIntegerTextField(cellModel.getModel(ContinuousMeasurement.PROPERTY_SAMPLESIZE));
			d_nothingFocussedListener.addComponent(sizeField);
			importPanel.add(sizeField);
		}

		
		
	}
}
