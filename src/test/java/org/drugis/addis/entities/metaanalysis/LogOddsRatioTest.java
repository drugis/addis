package org.drugis.addis.entities.metaanalysis;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.junit.Before;
import org.junit.Test;

public class LogOddsRatioTest {
	/* 
	 * Test data from Figure 2 in "Efficacy and Safety of Second-Generation 
	 * Antidepressants in the Treatment of Major Depressive Disorder" 
	 * by Hansen et al. 2005 and constructed with R:
	 * Random effects ( DerSimonian-Laird ) meta-analysis
		Call: meta.DSL(ntrt = n.trt, nctrl = n.ctrl, ptrt = col.trt, pctrl = col.ctrl, 
    	names = Name, data = testData2, statistic = "OR")
		------------------------------------
                		OR (lower  95% upper)
		Bennie 1995   1.36    0.85       2.17
		Boyer 1998    1.03    0.62       1.71
		Fava 2002     1.65    0.89       3.06
		Newhouse 2000 1.11    0.63       1.95
		Sechter 1999  1.56    0.90       2.70
		------------------------------------
		SummaryOR= 1.3  95% CI ( 1.03,1.65 )
		Test for heterogeneity: X^2( 4 ) = 2.14 ( p-value 0.7099 )
		Estimated random effects variance: 0 
	 * 
	 */
	
	private Drug d_fluox;
	private Drug d_sertra;
	
	private Indication d_ind;
	private Endpoint d_ep;
	
	private Study d_bennie, d_boyer, d_fava, d_newhouse, d_sechter;
	
	private OddsRatio d_ratioBennie, d_ratioBoyer, d_ratioFava, d_ratioNewhouse, d_ratioSechter;

	@Before
	public void setUp() {
		d_ind = new Indication(001L, "Impression");
		d_fluox = new Drug("Fluoxetine","01");
		d_sertra = new Drug("Sertraline","02");
		d_ep = new Endpoint("ep", Endpoint.Type.RATE);
		
		d_bennie = createStudy("Bennie 1995",63,144,73,142);
		d_boyer = createStudy("Boyer 1998", 61,120, 63,122);
		d_fava = createStudy("Fava 2002", 57, 92, 70, 96);
		d_newhouse = createStudy("Newhouse 2000", 84,119, 85,117);
		d_sechter = createStudy("Sechter 1999", 76,120, 86,118);
				
		
		d_ratioBennie = (OddsRatio) RelativeEffectFactory.buildRelativeEffect(d_bennie, d_ep, d_fluox, d_sertra, LogOddsRatio.class);
		d_ratioBoyer = (OddsRatio) RelativeEffectFactory.buildRelativeEffect(d_boyer, d_ep, d_fluox, d_sertra, LogOddsRatio.class);
		d_ratioFava = (OddsRatio) RelativeEffectFactory.buildRelativeEffect(d_fava, d_ep, d_fluox, d_sertra, LogOddsRatio.class);
		d_ratioNewhouse = (OddsRatio) RelativeEffectFactory.buildRelativeEffect(d_newhouse, d_ep, d_fluox, d_sertra, LogOddsRatio.class);
		d_ratioSechter = (OddsRatio) RelativeEffectFactory.buildRelativeEffect(d_sechter, d_ep, d_fluox, d_sertra, LogOddsRatio.class);
	}
	
	@Test
	public void testGetMean() {
		assertEquals(1.36, Math.exp(d_ratioBennie.getRelativeEffect()), 0.01);
		assertEquals(1.03, Math.exp(d_ratioBoyer.getRelativeEffect()), 0.01); 
		assertEquals(1.65, Math.exp(d_ratioFava.getRelativeEffect()), 0.01);
		assertEquals(1.11, Math.exp(d_ratioNewhouse.getRelativeEffect()), 0.01);
		assertEquals(1.56, Math.exp(d_ratioSechter.getRelativeEffect()), 0.01); 
	}
	
	@Test
	public void testGetError() {
		double expected = Math.sqrt(1/63D + 1/73D + 1/(144D-63D) + 1/(142D-73D));
		assertEquals(expected, d_ratioBennie.getError(), 0.001);
	}
			
	private Study createStudy(String studyName, int fluoxResp, int fluoxSize, int sertraResp, int sertraSize)
	{
		BasicStudy s = new BasicStudy(studyName, d_ind);
		s.addEndpoint(d_ep);
		BasicPatientGroup g_fluox = new BasicPatientGroup(s, d_fluox, new Dose(10.0, SIUnit.MILLIGRAMS_A_DAY),fluoxSize);
		BasicPatientGroup g_parox = new BasicPatientGroup(s, d_sertra, new Dose(10.0, SIUnit.MILLIGRAMS_A_DAY),sertraSize);		
		
		s.addPatientGroup(g_parox);
		s.addPatientGroup(g_fluox);
		
		BasicRateMeasurement m_parox = (BasicRateMeasurement) d_ep.buildMeasurement(g_parox);
		BasicRateMeasurement m_fluox = (BasicRateMeasurement) d_ep.buildMeasurement(g_fluox);
		
		m_parox.setRate(sertraResp);
		m_fluox.setRate(fluoxResp);
		
		s.setMeasurement(d_ep, g_parox, m_parox);
		s.setMeasurement(d_ep, g_fluox, m_fluox);		
		
		return s;
	}
}