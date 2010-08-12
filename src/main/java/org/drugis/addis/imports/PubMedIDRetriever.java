package org.drugis.addis.imports;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class PubMedIDRetriever {

	public Document parse (InputStream is) {
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
	
	public String importPubMedID(String StudyID) throws Exception {
		String PubMedID = "";
		String urlOne = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax=0&usehistory=y&term="+StudyID+"[Secondary%20Source%20ID]";
		String queryKey = "";
		String webEnv = "";
		
		URL XmlUrlOne = null;
		XmlUrlOne = new URL(urlOne);
					
		InputStream inOne = XmlUrlOne.openStream();
		Document docOne = parse(inOne);

		// second url
		
		NodeList QK = docOne.getElementsByTagName("QueryKey");
		queryKey = QK.item(0).getFirstChild().getNodeValue();
		NodeList WE = docOne.getElementsByTagName("WebEnv");
		webEnv = WE.item(0).getFirstChild().getNodeValue();
		
		String urlTwo = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&retmode=xml&query_key="+queryKey+"&WebEnv="+webEnv+"&retstart=0&retmax=10%22";
		URL XmlUrlTwo = null;
		XmlUrlTwo = new URL(urlTwo);
		
		
		InputStream inTwo = XmlUrlTwo.openStream();
		Document docTwo = parse(inTwo);
		
		docTwo.getDocumentElement().normalize();
		
		NodeList PID = docTwo.getElementsByTagName("Id");
		
		PubMedID = PID.item(0).getFirstChild().getNodeValue();
		
		return PubMedID;
	}

}
