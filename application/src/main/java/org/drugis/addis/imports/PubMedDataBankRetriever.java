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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.PubMedId;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class PubMedDataBankRetriever {
	public static class DataBankId {
		public String d_dataBankName;
		public String d_id;
		
		public DataBankId(String dataBankName, String id) {
			d_dataBankName = dataBankName;
			d_id = id;
		}
		
		@Override
		public String toString() {
			return d_dataBankName + ":" + d_id;
		}
	}
	
	public List<DataBankId> getDataBankList(PubMedId id) throws IOException {
		InputStream is = PubMedIDRetriever.openUrl(PubMedIDRetriever.PUBMED_API + 
				"efetch.fcgi?db=pubmed&retmode=xml&id=" + id.getId());
		Document doc = PubMedIDRetriever.parse(is);
		return getDataBankList(doc);
	}

	public static void copyStream(InputStream input, OutputStream output)
    throws IOException {
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	
	private List<DataBankId> getDataBankList(Document doc) {
		List<DataBankId> list = new ArrayList<DataBankId>();
		NodeList elementsByTagName = doc.getElementsByTagName("DataBankList");
		if (elementsByTagName.getLength() > 0) {
			NodeList dataBankList = elementsByTagName.item(0).getChildNodes();
			for (int i = 0; i < dataBankList.getLength(); ++i) {
				if (!dataBankList.item(i).getLastChild().getNodeName().equals("AccessionNumberList")) {
					throw new RuntimeException("Error parsing PubMed response");
				}
				if (!dataBankList.item(i).getFirstChild().getNodeName().equals("DataBankName")) {
					throw new RuntimeException("Error parsing PubMed response");
				}
				String dataBankName = dataBankList.item(i).getFirstChild().getFirstChild().getNodeValue();
				NodeList idList = dataBankList.item(i).getLastChild().getChildNodes();
				for(int j = 0; j < idList.getLength(); ++j) {
					String id = idList.item(0).getFirstChild().getNodeValue();
					list.add(new DataBankId(dataBankName, id));
				}
			}
		}
		return list;
	}

	public static void main(String[] args) throws IOException {
		String[] pmids = {
				"15562200", "17130196", "17277036", "15504997",
				"17130197", "15855572", "17373638", "12882864",
				"19019476", "17300592", "18284434", "17485570",
				"19221978", "17559733", "17593236", "15855571",
				"19118913", "15161785", "19317822", "19097665",
				"18803987", "18931095", "17933414", "17223217",
				"15929678", "17001471", "18194595", "16219012",
				"17157112", "18355325", "17156104", "18201203",
				"18495285", "17372153", "17404349", "19289857"
				};
		
		PubMedDataBankRetriever retriever = new PubMedDataBankRetriever();
		for (String id : pmids) {
			PubMedId pmid = new PubMedId(id);
			System.out.println(pmid + " -> " + retriever.getDataBankList(pmid));
		}
	}
}
