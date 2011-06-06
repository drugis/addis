package org.drugis.addis.util;

import static org.junit.Assert.assertNotNull;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.Study;
import org.junit.Test;

public class D80TableGeneratorTest {
	
	public static void main(String[] args){
		JFrame window = new JFrame();
		
		JLabel pane = new JLabel();
		pane.setText(D80TableGenerator.getHtml(getExample()));
		window.add(pane);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}
	
	@Test
	public void testGetHtml() { // FIXME
		assertNotNull(D80TableGenerator.getHtml(getExample()));
	}

	private static Study getExample() {
		// TODO: get rid of example study
		Study example = ExampleData.buildStudyDeWilde();
		example.getEpochs().add(new Epoch("Randomization", null));
		
		Epoch mainPhase = (Epoch)example.findTreatmentEpoch();
		try {
			mainPhase.setDuration(DatatypeFactory.newInstance().newDuration("PT5H"));
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		return example;
	}
}
