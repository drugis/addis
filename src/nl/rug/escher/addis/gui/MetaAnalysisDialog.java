package nl.rug.escher.addis.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.jgoodies.forms.builder.ButtonBarBuilder2;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.MetaAnalysis;
import nl.rug.escher.addis.entities.MetaStudy;
import nl.rug.escher.common.gui.ViewBuilder;

@SuppressWarnings("serial")
public class MetaAnalysisDialog extends JDialog {
	
	private MetaAnalysis d_analysis;
	private Domain d_domain;

	public MetaAnalysisDialog(JFrame parent, Domain domain, MetaAnalysis analysis) {
		super(parent, "Meta-Analysis");
		d_domain = domain;
		d_analysis = analysis;
		
		initComponents();
		pack();
	}

	private void initComponents() {
		ViewBuilder view = new MetaAnalysisView(d_analysis);
		add(view.buildPanel(), BorderLayout.CENTER);
		JButton closeButton = new JButton("Close");
		closeButton.setMnemonic('c');
		closeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		JButton saveButton = new JButton("Save as new study");
		saveButton.setMnemonic('s');
		saveButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				saveAsStudy();
			}			
		});
		
		ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
		bbuilder.addButton(closeButton);
		bbuilder.addGlue();
		bbuilder.addButton(saveButton);
		
		add(bbuilder.getPanel(), BorderLayout.SOUTH);
	}

	protected void saveAsStudy() {
		String res = JOptionPane.showInputDialog(this, "Input name for new study", 
				"Save meta-analysis as study", JOptionPane.QUESTION_MESSAGE);
		if (res != null) {
			d_domain.addStudy(new MetaStudy(res, d_analysis));
		}
	}

}
