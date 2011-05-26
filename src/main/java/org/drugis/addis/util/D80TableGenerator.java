package org.drugis.addis.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class D80TableGenerator {

	public static String getHtml() {
		String html = "";
		try {
			InputStreamReader fr = new InputStreamReader(D80TableGenerator.class.getResourceAsStream("TemplateD80Report.html"));
			
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while((line = br.readLine()) != null ) {
				html += line;
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("not found");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return html;
	}

}
