package org.drugis.addis.gui.builder;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.util.D80TableGenerator;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class D80ReportView extends JDialog {
	private static final long serialVersionUID = 8901532700897942147L;
	private JLabel d_htmlPane;

	public D80ReportView(Frame owner) {
		super(owner, "D80 Report Dialog", false);
		setMinimumSize(new Dimension(owner.getWidth()/4*3, owner.getHeight()/4*3));
		//setResizable(false);
		setLocationRelativeTo(owner);
		
		buildPanel();
	}

	private void buildPanel() {
		FormLayout layout = new FormLayout("fill:0:grow", "p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		
		// Editor pane
		
		//String html = D80TableGenerator.getHtml();
		
		String html = "<html><table border='1'><tr><td>test</td></tr></table></html>";
		
		d_htmlPane = new JLabel(html);	
		builder.add(d_htmlPane, cc.xy(1, 1));
		
		
		// Buttons
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(new JButton("Export table as html"));
		buttonsPanel.add(new JButton("Copy table to clip board"));
		builder.add(buttonsPanel, cc.xy(1, 2));
		
		JScrollPane scrollPane = new JScrollPane(builder.getPanel());
		scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
		add(scrollPane);
	}
}
