/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.drugis.addis.AppInfo;

public final class CopyrightInfo {
	private final int YEAR2009 = 0;
	private final int YEAR2010 = 1;
	private final int YEAR2011 = 2;
	private final int YEAR2012 = 3;
	private final String[] years = {"2009", "2010", "2011", "2012"};
	private final static String HEADER_FILENAME = "HEADER";
	private String [][] d_authors;
	private static String d_aboutText;
	private static String d_headerText;
	private static int d_aboutNewLines;
	
	public CopyrightInfo() {
		d_aboutNewLines = 2;
		d_authors = new String[years.length+1][];
		d_authors[YEAR2009] = new String[] {"Gert van Valkenhoef" , "Tommi Tervonen"};
		d_authors[YEAR2010] = new String[] {"Gert van Valkenhoef" , "Tommi Tervonen", "Tijs Zwinkels", "Maarten Jacobs",
									"Hanno Koeslag", "Florin Schimbinschi", "Ahmad Kamal", "Daniel Reid"};
		d_authors[YEAR2011] = new String[] {"Gert van Valkenhoef", "Ahmad Kamal", "Daniel Reid", "Florin Schimbinschi" };
		d_authors[YEAR2012] = new String[] {"Gert van Valkenhoef", "Daniel Reid", "Joël Kuiper", "Wouter Reckman" };
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
				if(c > 72) {
					d_headerText += "\n * ";
					c = 3 + author.length();
				}				 
				d_aboutText += author + "\n";
				++d_aboutNewLines;
			}
			
			d_headerText = d_headerText.subSequence(0, d_headerText.lastIndexOf(",") ) + ".";
			d_headerText += "\n";
			d_aboutText += "\n";
			++d_aboutNewLines;
		}
		
		d_headerText +=
				" *\n" +
				" * This program is free software: you can redistribute it and/or modify\n" +
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
				" */\n\n";
	
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
	
	public static void main(String [] args) {
		try {
			new CopyrightInfo().writeHeader(HEADER_FILENAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
