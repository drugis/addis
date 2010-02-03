package org.drugis.addis.presentation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.FocusTransferrer;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.StudyTable;
import org.drugis.common.gui.AuxComponentFactory;

import com.jgoodies.binding.PresentationModel;

public class MeasurementTable extends JTable{
	private static final long serialVersionUID = -5815104084298298455L;
	
	private class MeasurementTableModel extends AbstractTableModel {		
		private static final long serialVersionUID = 5331596469882184969L;
	
		private Study d_study;
		private PresentationModelFactory d_pmf;
		
	
		public MeasurementTableModel(Study study, PresentationModelFactory pmf) {
			d_study = study;
			d_pmf = pmf;
		}
	
		public int getColumnCount() {
			return d_study.getArms().size();
		}
	
		public int getRowCount() {
			return d_study.getOutcomeMeasures().size();
		}
		
		  public boolean isCellEditable(int row, int col)
	       { return true; }

	
		public Object getValueAt(int rowIndex, int columnIndex) {
			OutcomeMeasure om = new ArrayList<OutcomeMeasure>(d_study.getOutcomeMeasures()).get(rowIndex);
			Arm arm = d_study.getArms().get(columnIndex);
			return d_pmf.getLabeledModel(d_study.getMeasurement(om, arm));
		}
	}
	
	
	private Window d_frame;
	protected JTable d_table;
	
	public MeasurementTable(Study study, PresentationModelFactory pmf, Window parent) {
		this.setModel(new MeasurementTableModel(study,pmf));
		//this.setDefaultEditor(Object.class, new MeasurementsTableCellEditor());
		d_frame = parent;
		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
		TableColumn column = null;
		for (int i = 0; i < getModel().getColumnCount(); i++) {
		    column = getColumnModel().getColumn(i);
		        column.setPreferredWidth(50);
		}
		d_table = this;

		d_table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = ((JTable)e.getComponent()).rowAtPoint(e.getPoint());
				int col = ((JTable)e.getComponent()).columnAtPoint(e.getPoint());
				
				JWindow measurement = new EnterMeasurementWindow(d_frame,row,col);
				measurement.requestFocusInWindow();
				//measurement.requestFocus();
			}
		});
	}
	
	
	@SuppressWarnings("serial")
	public class EnterMeasurementWindow extends JWindow{
		
		private class NothingFocussedListener implements FocusListener {
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

		private NothingFocussedListener d_nothingFocussedListener;
		
		@SuppressWarnings("unchecked")
		public EnterMeasurementWindow(Window parent, int row, int col) {
			super(parent);
			
			d_nothingFocussedListener = new NothingFocussedListener(this);
			
			// Retrieve value-model
			PresentationModel<Measurement> cellModel = ((PresentationModel<Measurement>)d_table.getModel().getValueAt(row, col));
			
			// Create Panel with input components.
			JPanel importPanel = new JPanel();
			if(cellModel.getBean() instanceof ContinuousMeasurement)
				makeContinuousInputfield(importPanel, cellModel);
			else if (cellModel.getBean() instanceof RateMeasurement)
				makeRateInputfield(importPanel, cellModel);

			// Calculate the location of the window, and move it there.
			Rectangle cellLocation = d_table.getCellRect(row, col, false);
			Point l = d_table.getComponentAt(col, row).getLocationOnScreen();
			l.translate(cellLocation.x, cellLocation.y);
			System.out.println("Location: "+l);
			setLocation(l);
			
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
