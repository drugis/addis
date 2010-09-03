package org.drugis.addis.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.drugis.addis.AppInfo;

public final class CopyrightInfo {
	private final int YEAR2009 = 0;
	private final int YEAR2010 = 1;
	private final String[] years = {"2009", "2010"};
	private String [][] d_authors;
	private static String d_aboutText;
	private static String d_headerText;
	private static int d_aboutNewLines;
	
	public CopyrightInfo() {
		d_aboutNewLines = 2;
		d_authors = new String [years.length+1][10];
		d_authors[YEAR2009] = new String[] {"Gert van Valkenhoef" , "Tommi Tervonen"};
		d_authors[YEAR2010] = new String[] {"Gert van Valkenhoef" , "Tommi Tervonen", "Tijs Zwinkels", "Maarten Jacobs",
									"Hanno Koeslag", "Florin Schimbinschi", "Ahmad Kamal", "Daniel Reid"};
		d_aboutText = new String(AppInfo.getAppName() + " is open source and licensed under GPLv3.\n");
		
		d_headerText = "/*\n" +
		" * This file is part of ADDIS (Aggregate Data Drug Information System).\n" +
		" * ADDIS is distributed from http://drugis.org/.\n";

		int c=0;
		for(int i=0; i < years.length; ++i) {
			d_headerText += " * Copyright (C) " + years[i] + " ";			
			d_aboutText += "Copyright \u00A9" + years[i] + "\n";
			
			for(String author : d_authors[i]) {
				c += author.length() + 2;
				d_headerText += author + ", ";
				if(c > 60) {
					d_headerText += "\n * ";
					c = 3;
				}				 
				d_aboutText += author + "\n";
				++d_aboutNewLines;
			}
			
			d_headerText = d_headerText.subSequence(0, d_headerText.lastIndexOf(",") ) + ".";
			d_headerText += "\n";
			d_aboutText += "\n";
			++d_aboutNewLines;
		}
		
		d_headerText += " * This program is free software: you can redistribute it and/or modify\n" +
				" * it under the terms of the GNU General Public License as published by\n" +
				" * the Free Software Foundation, either version 3 of the License, or\n" +
				" * (at your option) any later version.\n" +
				" *\n" +
				" * This program is distributed in the hope that it will be useful,\n" +
				" * but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
				" * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
				" * GNU General Public License for more details.\n" +
				" *\n" +
				" * You should have received a copy of the GNU General Public License\n" +
				" * along with this program.  If not, see <http://www.gnu.org/licenses/>.\n" +
				" */";
	
	}

	public String getAboutText() {
		return d_aboutText;
	}
	public int getAboutLineCount() {
		return d_aboutNewLines;
	}
	
	public void writeHeader(String fileName) throws IOException {
		Writer out = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
	    try {
	      out.write(d_headerText);
	    } finally {
	      out.close();
	    }
	}
}