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
