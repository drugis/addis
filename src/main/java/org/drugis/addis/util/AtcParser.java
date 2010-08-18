package org.drugis.addis.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AtcParser {
		
	public static class AtcDescription {
		private String d_code;
		private String d_desc;
		
		public AtcDescription () {
			this.d_code = null;
			this.d_desc = null;
		}
		
		public AtcDescription (String code, String desc) {
			this.d_code = code;
			this.d_desc = desc;
		}
		
		public String getCode() {
			return d_code;
		}

		public String getDescription() {
			return d_desc;
		}		
	}
	
	private static final Pattern s_codePattern = Pattern.compile("<a href=\"[^\"]*/\\?code=([A-Z0-9]*)(&[^\"]*)?\">([\\w\\s,-/(/)]*)</a>", Pattern.CASE_INSENSITIVE);

	public List<AtcDescription> findDrugDetails(String inputLine) {
		Matcher matchCode = s_codePattern.matcher(inputLine);
		List<AtcDescription> detailsList = new ArrayList<AtcDescription>();
		while(matchCode.find()) {
			if (!matchCode.group(3).equals("Show text from Guidelines")) {
				detailsList.add(new AtcDescription((matchCode.group(1)), matchCode.group(3)));
			}
		}
		return detailsList;
	}
	
	public List<AtcDescription> parse(InputStream is) throws IOException {	
		String inputLine;
		List<AtcDescription> finalList = new ArrayList<AtcDescription>();
		BufferedReader readCode = new BufferedReader(new InputStreamReader(is));
		while ((inputLine = readCode.readLine()) != null) {
			if(!findDrugDetails(inputLine).isEmpty()){
				finalList.addAll(findDrugDetails(inputLine));
			}
		}
		return finalList;
	}

	public AtcDescription getAtcCode(String drugName) throws MalformedURLException, IOException {
		InputStream d_urlStream = new URL("http://www.whocc.no/atc_ddd_index/?name="+drugName).openConnection().getInputStream();
		BufferedReader readCode = new BufferedReader(new InputStreamReader(d_urlStream));
		String inputLine;
		AtcDescription result = new AtcDescription();
		while ((inputLine = readCode.readLine()) != null && result.getCode() == null) {			
			if(!new AtcParser().findDrugDetails(inputLine).isEmpty()) {
				result = new AtcParser().findDrugDetails(inputLine).get(0);
			}
		}
		return result;
	}
	
	public List<AtcDescription> getAtcDetails(String atcCode) throws IOException {
		InputStream d_urlStream = new URL("http://www.whocc.no/atc_ddd_index/?code="+atcCode).openConnection().getInputStream();
		return new AtcParser().parse(d_urlStream);
	}
}
