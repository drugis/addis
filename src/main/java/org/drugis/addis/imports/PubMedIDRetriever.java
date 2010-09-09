/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

package org.drugis.addis.imports;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class PubMedIDRetriever {
	private static final int READ_TIMEOUT = 3000;
	private static final int CONNECTION_TIMEOUT = 3000;
	private static final String PUBMED_API = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/";

	public PubMedIdList importPubMedID(String StudyID) {
		// First returned document is a key into the results.
		InputStream inOne = openUrl(PUBMED_API + "esearch.fcgi?db=pubmed&retmax=0&usehistory=y&term="+StudyID+"[Secondary%20Source%20ID]");
		String resultsUrl = getResultsUrl(inOne);
		
		// Second returned document contains results.
		InputStream inTwo = openUrl(PUBMED_API + resultsUrl);
		Document docTwo = parse(inTwo);
		return getIdList(docTwo);
	}
	
	private Document parse (InputStream is) {
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setValidating(false);
			domFactory.setNamespaceAware(false);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			
			Document ret = builder.parse(is);
	
			return ret;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing PubMed response", e);
		}
    }

	private String getResultsUrl(InputStream inOne) {
		Document docOne = parse(inOne);
		String queryKey = getTagValue(docOne, "QueryKey");
		String webEnv = getTagValue(docOne, "WebEnv");
		String resultsUrl = "esummary.fcgi?db=pubmed&retmode=xml&query_key="+queryKey+"&WebEnv="+webEnv+"&retstart=0";
		return resultsUrl;
	}

	private String getTagValue(Document docOne, String tagName) {
		NodeList QK = docOne.getElementsByTagName(tagName);
		return QK.item(0).getFirstChild().getNodeValue();
	}

	private PubMedIdList getIdList(Document docTwo) {	
		NodeList PID = docTwo.getElementsByTagName("Id");
		
		PubMedIdList PubMedID = new PubMedIdList();
		for (int i = 0; i < PID.getLength(); i++) {
			PubMedID.add(new PubMedId(PID.item(i).getFirstChild().getNodeValue()));
		}
		
		return PubMedID;
	}

	private InputStream openUrl(String url) {
		try { 
			URLConnection urlConn = new URL(url).openConnection();
			urlConn.setConnectTimeout(CONNECTION_TIMEOUT);
			urlConn.setReadTimeout(READ_TIMEOUT);
			InputStream is = urlConn.getInputStream();
			return is;
		} catch (Exception e) {
			throw new RuntimeException("Could not open PubMed connection", e);
		}
	}

}