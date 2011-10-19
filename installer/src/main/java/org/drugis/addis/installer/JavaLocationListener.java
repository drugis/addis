/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

package org.drugis.addis.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.izforge.izpack.Pack;
import com.izforge.izpack.PackFile;
import com.izforge.izpack.event.InstallerListener;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.util.AbstractUIProgressHandler;

public class JavaLocationListener implements InstallerListener {

	@Override
	public void afterDir(File dir, PackFile pf) throws Exception {

	}

	@Override
	public void afterFile(File file, PackFile pf) throws Exception {
		String sp = pf.getRelativeSourcePath();
		if (sp != null && sp.equals("addis.cmd")) {
			File outFile = new File(file.getParent() + File.separator + "java.home.txt");
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outFile));
			osw.write(System.getProperty("java.home") + "\n");
			osw.close();
		}
	}

	@Override
	public void afterInstallerInitialization(AutomatedInstallData data)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPack(Pack pack, Integer i,
			AbstractUIProgressHandler handler) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPacks(AutomatedInstallData idata,
			AbstractUIProgressHandler handler) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeDir(File dir, PackFile pf) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeFile(File file, PackFile pf) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforePack(Pack pack, Integer i,
			AbstractUIProgressHandler handler) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforePacks(AutomatedInstallData idata, Integer npacks,
			AbstractUIProgressHandler handler) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFileListener() {
		// TODO Auto-generated method stub
		return true;
	}

}
