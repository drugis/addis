package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.table.DefaultTableModel;

import org.drugis.addis.presentation.MeasurementTable.NothingFocussedListener;

@SuppressWarnings("serial")
public class Test extends JPanel {
	
	private JTable d_table;
	protected JFrame d_frame;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});
		frame.setContentPane(new Test(frame));
		frame.pack();
		frame.setVisible(true);
	}
	
	public Test(JFrame frame) {
		d_frame = frame;
		d_table = new JTable(new DefaultTableModel(3, 5));
		add(d_table);
		d_table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = ((JTable)e.getComponent()).rowAtPoint(e.getPoint());
				int col = ((JTable)e.getComponent()).columnAtPoint(e.getPoint());
				createWindow(d_frame);
			}
		});
	}

	protected JWindow createWindow(JFrame parent) {
		System.out.println("Clicked!");
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(new JLabel("Field1: "));
		JTextField field1 = new JTextField();	
		panel.add(field1);
		panel.add(new JLabel("Field2: "));
		JTextField field2 = new JTextField();	
		field1.setText("AAA");
		field2.setText("AAA");
		panel.add(field2);
		
		// Traversal order with the table as the last element
		final List<JComponent> order = new ArrayList<JComponent>();
		order.add(field1);
		order.add(field2);
		order.add(d_table);
		
		// Traversal policy
		FocusTraversalPolicy policy = new FocusTraversalPolicy() {
			@Override
			public Component getLastComponent(Container arg0) {
				return order.get(order.size() - 1);
			}
			
			@Override
			public Component getFirstComponent(Container arg0) {
				return order.get(0);
			}
			
			@Override
			public Component getDefaultComponent(Container arg0) {
				return order.get(0);
			}
			
			@Override
			public Component getComponentBefore(Container arg0, Component arg1) {
				int idx = order.indexOf(arg1);
				System.out.println("Before Index: " + idx);
				if (idx > 0) return order.get(idx - 1);
				return getLastComponent(arg0);
			}
			
			@Override
			public Component getComponentAfter(Container arg0, Component arg1) {
				int idx = order.indexOf(arg1);
				System.out.println("After Index: " + idx);
				if (idx >= 0 && idx < (order.size() - 1)) return order.get(idx + 1);
				return getLastComponent(arg0);
			}
		};
		
		
		// create the window
		final JWindow window = new JWindow(parent);
		window.setFocusTraversalPolicy(policy); // policy that makes focus leave the panel
		window.getContentPane().add(panel, BorderLayout.CENTER);
		window.setSize(300, 300);
		window.setVisible(true);
		// Make sure the first component has focus
		window.requestFocusInWindow();
		field1.requestFocus();
		
		// Make sure the window closes when none of the fields have focus
		NothingFocussedListener l = new NothingFocussedListener(window);
		l.addComponent(field1);
		l.addComponent(field2);
		
		// Make sure the window closes when we click in the frame containing the table
		d_frame.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
			}
			
			public void mousePressed(MouseEvent e) {
			}
			
			public void mouseExited(MouseEvent e) {
			}
			
			public void mouseEntered(MouseEvent e) {
			}
			
			public void mouseClicked(MouseEvent e) {
				window.setVisible(false);
			}
		});
		
		return window;
	}
}
