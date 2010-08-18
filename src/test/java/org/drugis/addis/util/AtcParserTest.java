package org.drugis.addis.util;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.util.AtcParser.AtcDescription;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AtcParserTest {
	InputStream d_drugStream;
	InputStream d_codeStream;
	InputStream d_urlStream;
	
	@Before
	public void setUp() {
		d_codeStream = AtcParserTest.class.getResourceAsStream("ATCResultCode.html");
		d_drugStream = AtcParserTest.class.getResourceAsStream("ATCResultDrug.html");
	}
	
	@After
	public void tearDown() {
		try {
			d_codeStream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			d_drugStream.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	@Test 
	public void testFilesExist() throws IOException{
		BufferedReader readCode = new BufferedReader(new InputStreamReader(d_codeStream));
		BufferedReader readDrug = new BufferedReader(new InputStreamReader(d_drugStream));
		readDrug.readLine();
		readCode.readLine();
	}
	
	@Test
	public void testURLs() throws MalformedURLException, IOException {
		d_urlStream = new URL("http://www.whocc.no/atc_ddd_index/?code=").openConnection().getInputStream();
		BufferedReader readCode = new BufferedReader(new InputStreamReader(d_urlStream));
		readCode.readLine();
	}
	
	@Test
	public void testMatchCodeFail() {
		String drugTest = "This will have to return null";
		assertEquals(Collections.emptyList(), new AtcParser().findDrugDetails(drugTest));
	}
	
	@Test
	public void testMatchCodeSuccesful() {
		String drugTest = "<a href=\"http://www.whocc.no/atc_ddd_index/?code=N06AB03\">fluoxetine</a>";
		assertEquals(1, new AtcParser().findDrugDetails(drugTest).size());
		assertEquals("N06AB03", new AtcParser().findDrugDetails(drugTest).get(0).getCode());
		assertEquals("fluoxetine", new AtcParser().findDrugDetails(drugTest).get(0).getDescription());
	}
	
	@Test 
	public void testCodeParse() throws IOException {
		BufferedReader readCode = new BufferedReader(new InputStreamReader(d_drugStream));
		AtcParser parser = new AtcParser();
		String inputLine;
		List<AtcDescription> tmp = Collections.emptyList();
		while ((inputLine = readCode.readLine()) != null && tmp.isEmpty()) {
			tmp = parser.findDrugDetails(inputLine);
		}
		assertEquals("N06AB03", tmp.get(0).getCode());
		assertEquals("fluoxetine", tmp.get(0).getDescription());
	}
	
	@Test 
	public void testCodeParseFromUrl() throws IOException {
		assertEquals("N06AB03", new AtcParser().getAtcCode("fluoxetine").getCode());
		assertEquals("fluoxetine", new AtcParser().getAtcCode("fluoxetine").getDescription());
		
	}
	
	@Test 
	public void testDrugFirstDetailParse() throws IOException {
		BufferedReader readCode = new BufferedReader(new InputStreamReader(d_codeStream));
		AtcParser parser = new AtcParser();
		String inputLine;
		while ((inputLine = readCode.readLine()) != null && parser.findDrugDetails(inputLine).isEmpty()) {
		}
		List<AtcDescription> finalList = parser.findDrugDetails(inputLine);
		assertEquals(1, finalList.size());
		assertEquals("N", finalList.get(0).getCode());
		assertEquals("NERVOUS SYSTEM", finalList.get(0).getDescription());
	}
	
	@Test 
	public void testDrugAllDetailsParse() throws IOException {
		AtcParser parser = new AtcParser();
		List<AtcDescription> finalList = parser.parse(d_codeStream);
		
		assertEquals(6, finalList.size());
		assertEquals("N", finalList.get(0).getCode());
		assertEquals("NERVOUS SYSTEM", finalList.get(0).getDescription());
		assertEquals("N07", finalList.get(1).getCode());
		assertEquals("OTHER NERVOUS SYSTEM DRUGS", finalList.get(1).getDescription());
		assertEquals("N07B", finalList.get(2).getCode());
		assertEquals("DRUGS USED IN ADDICTIVE DISORDERS", finalList.get(2).getDescription());
		assertEquals("N07BA", finalList.get(3).getCode());
		assertEquals("Drugs used in nicotine dependence", finalList.get(3).getDescription());
		assertEquals("N07BA01", finalList.get(4).getCode());
		assertEquals("nicotine", finalList.get(4).getDescription());
		assertEquals("N07BA03", finalList.get(5).getCode());
		assertEquals("varenicline", finalList.get(5).getDescription());
	}
	
	@Test 
	public void testDrugAllDetailsParseFromUrl() throws IOException {
		d_urlStream = new URL("http://www.whocc.no/atc_ddd_index/?code=N07BA").openConnection().getInputStream();
		AtcParser parser = new AtcParser();
		List<AtcDescription> finalList = parser.parse(d_urlStream);
		
		assertEquals(6, finalList.size());
		assertEquals("N", finalList.get(0).getCode());
		assertEquals("NERVOUS SYSTEM", finalList.get(0).getDescription());
		assertEquals("N07", finalList.get(1).getCode());
		assertEquals("OTHER NERVOUS SYSTEM DRUGS", finalList.get(1).getDescription());
		assertEquals("N07B", finalList.get(2).getCode());
		assertEquals("DRUGS USED IN ADDICTIVE DISORDERS", finalList.get(2).getDescription());
		assertEquals("N07BA", finalList.get(3).getCode());
		assertEquals("Drugs used in nicotine dependence", finalList.get(3).getDescription());
		assertEquals("N07BA01", finalList.get(4).getCode());
		assertEquals("nicotine", finalList.get(4).getDescription());
		assertEquals("N07BA03", finalList.get(5).getCode());
		assertEquals("varenicline", finalList.get(5).getDescription());
	}
	
}
