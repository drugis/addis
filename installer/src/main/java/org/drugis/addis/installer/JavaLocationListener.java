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
