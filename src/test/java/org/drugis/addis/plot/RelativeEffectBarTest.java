package org.drugis.addis.plot;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.MeanDifference;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.Endpoint.Type;
import org.junit.Test;

public class RelativeEffectBarTest {
	
	@Test
	public void testNormalPlot() {
		// Create relative effect.
		Endpoint e = new Endpoint("e", Type.CONTINUOUS);
		PatientGroup p1 = new BasicPatientGroup(null, null, 100);
		PatientGroup p2 = new BasicPatientGroup(null, null, 100);
		RelativeEffect<ContinuousMeasurement> effect = new MeanDifference(new BasicContinuousMeasurement(e, 0.25, 1.26 / Math.sqrt(2), p1), 
											new BasicContinuousMeasurement(e, 0.5, 1.26 / Math.sqrt(2), p2));
	
		// Make some BinnedScale that maps [0, 1] -> [0, 200]
		BinnedScale bsl = new BinnedScale(new IdentityScale(), 0, 200);
	
		// set confidence interval line in mock.
		Integer lowerX = bsl.getBin(effect.getConfidenceInterval().getLowerBound()).bin;
		Integer upperX = bsl.getBin(effect.getConfidenceInterval().getUpperBound()).bin;
		
		Collection<Shape> shapeSet = new ArrayList<Shape>();
		shapeSet.add(new Line(lowerX , 11, upperX , 11));
		
		// set Mean box in mock.
		shapeSet.add(new FilledRectangle( bsl.getBin(effect.getRelativeEffect()).bin - 2, 11 - 2, 5, 5) );
	
		MockGraphics2D g2d = new MockGraphics2D(shapeSet);
		RelativeEffectBar plot = new RelativeEffectBar(bsl, 11, effect, 5);
		plot.paint(g2d);
		
		g2d.verify();
	}
	
	@Test
	public void testCombinedPlot() {
		Endpoint e = new Endpoint("e", Type.CONTINUOUS);
		PatientGroup p1 = new BasicPatientGroup(null, null, 100);
		PatientGroup p2 = new BasicPatientGroup(null, null, 100);
		RelativeEffect<ContinuousMeasurement> effect = new MeanDifference(new BasicContinuousMeasurement(e, 0.25, 1.26 / Math.sqrt(2), p1), 
											new BasicContinuousMeasurement(e, 0.5, 1.26 / Math.sqrt(2), p2));
	
		// Make some BinnedScale that maps [0, 1] -> [0, 200]
		BinnedScale bsl = new BinnedScale(new IdentityScale(), 0, 200);
	
		// set confidence interval line in mock.
		Integer lowerX = bsl.getBin(effect.getConfidenceInterval().getLowerBound()).bin;
		Integer upperX = bsl.getBin(effect.getConfidenceInterval().getUpperBound()).bin;
		
		Collection<Shape> shapeSet = new ArrayList<Shape>();
		shapeSet.add(new Line(lowerX , 11, upperX , 11));
		
		// set Mean box in mock.
		int center = bsl.getBin(effect.getRelativeEffect()).bin;
		shapeSet.add(new Line(center + 8, 11, center, 19));
		shapeSet.add(new Line(center, 19, center - 8, 11));
		shapeSet.add(new Line(center - 8, 11, center, 3));
		shapeSet.add(new Line(center, 3, center + 8, 11));
	
		MockGraphics2D g2d = new MockGraphics2D(shapeSet);
		RelativeEffectBar plot = new RelativeEffectBar(bsl, 11, effect, 0);
		plot.paint(g2d);
		
		g2d.verify();
		
	}
}
