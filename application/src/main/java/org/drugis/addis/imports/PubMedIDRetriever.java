/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PubMedIDRetriever {
	public static final int READ_TIMEOUT = 3000;
	public static final int CONNECTION_TIMEOUT = 3000;
	public static final String PUBMED_API = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
	
	public static class ParseException extends IOException {
		private static final long serialVersionUID = -3902366298759803187L;
		private final Throwable d_cause;

		public ParseException(String message, Throwable cause) {
			super(message);
			d_cause = cause;
		}
		
		@Override
		public Throwable getCause() {
			return d_cause;
		}
	}

	public PubMedIdList importPubMedID(String StudyID) throws IOException {
		// First returned document is a key into the results.
		InputStream inOne = openUrl(PUBMED_API + "esearch.fcgi?db=pubmed&retmax=0&usehistory=y&term="+StudyID+"[Secondary%20Source%20ID]");
		String resultsUrl = getResultsUrl(inOne);
		
		// Second returned document contains results.
		InputStream inTwo = openUrl(PUBMED_API + resultsUrl);
		Document docTwo = parse(inTwo);
		return getIdList(docTwo);
	}
	
	public static Document parse(InputStream is) throws IOException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setValidating(false);
		domFactory.setNamespaceAware(false);
		domFactory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = null;
		try {
			builder = domFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		
		Document ret;
		try {
			ret = builder.parse(is);
		} catch (SAXException e) {
			throw new ParseException("Error parsing PubMed response", e);
		}

		return ret;
    }

	private String getResultsUrl(InputStream inOne) throws IOException {
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

	public static InputStream openUrl(String url) throws IOException {
		URLConnection urlConn = new URL(url).openConnection();
		urlConn.setConnectTimeout(CONNECTION_TIMEOUT);
		urlConn.setReadTimeout(READ_TIMEOUT);
		InputStream is = urlConn.getInputStream();
		return is;
	}

}