package org.drugis.addis.imports;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class PubMedIDRetriever {

	private Document parse (InputStream is) {
        Document ret = null;
        DocumentBuilderFactory domFactory;
        DocumentBuilder builder;

        try {
            domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(false);
            domFactory.setNamespaceAware(false);
            builder = domFactory.newDocumentBuilder();

            ret = builder.parse(is);
        }
        catch (Exception ex) {
            System.err.println("unable to load XML: " + ex);
        }
        return ret;
    }
	
	/*
	public static void main(String [] args) throws Exception{
		System.out.println(new PubMedIDRetriever().importPubMedID("NCT00000400"));
	}
	*/
	
	public List<String> importPubMedID(String StudyID) throws MalformedURLException, IOException{
		List<String> PubMedID = new ArrayList<String>(1);
		String urlOne = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax=0&usehistory=y&term="+StudyID+"[Secondary%20Source%20ID]";
		String queryKey = "";
		String webEnv = "";
		
		// first url
		
		URLConnection XmlUrlOne = new URL(urlOne).openConnection();
		XmlUrlOne.setConnectTimeout(10000);
		XmlUrlOne.setReadTimeout(15000);
		InputStream inOne = XmlUrlOne.getInputStream();
		
		
		Document docOne = parse(inOne);
		docOne.getDocumentElement().normalize();
		
		NodeList QK = docOne.getElementsByTagName("QueryKey");
		queryKey = QK.item(0).getFirstChild().getNodeValue();
		NodeList WE = docOne.getElementsByTagName("WebEnv");
		webEnv = WE.item(0).getFirstChild().getNodeValue();
		
		// second url
		
		String urlTwo = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&retmode=xml&query_key="+queryKey+"&WebEnv="+webEnv+"&retstart=0&retmax=10%22";
		URLConnection XmlUrlTwo = new URL(urlTwo).openConnection();
		XmlUrlTwo.setConnectTimeout(10000);
		XmlUrlTwo.setReadTimeout(15000);
		InputStream inTwo = XmlUrlTwo.getInputStream();
		
		Document docTwo = parse(inTwo);
		docTwo.getDocumentElement().normalize();
		
		NodeList PID = docTwo.getElementsByTagName("Id");
		
		for(int i=0; i<PID.getLength(); i++)
			PubMedID.add(PID.item(i).getFirstChild().getNodeValue());
		
		return PubMedID;
	}

}