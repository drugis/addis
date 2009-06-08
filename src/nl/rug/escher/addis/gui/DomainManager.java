/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package nl.rug.escher.addis.gui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.DomainImpl;

public class DomainManager {
	private DomainImpl d_domain = new DomainImpl();
	
	public Domain getDomain() {
		return d_domain;
	}
	
	/**
	 * Replace the Domain by a new instance loaded from a stream.
	 * @param is Stream to read objects from.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void loadDomain(InputStream is)
	throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(is);
		d_domain = (DomainImpl)ois.readObject();
	}
	
	/**
	 * Replace the Domain by a new instance loaded from a stream.
	 * @param os Stream to write objects to.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void saveDomain(OutputStream os)
	throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(d_domain);
	}
	
	/**
	 * Read domain from default location.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void loadDomain()
	throws IOException, ClassNotFoundException {
		FileInputStream s = new FileInputStream("domain.dat");
		try {
			loadDomain(new BufferedInputStream(s));
		} finally {
			s.close();
		}
	}
	
	/**
	 * Write domain to default location.
	 * @throws IOException
	 */
	public void saveDomain()
	throws IOException {
		FileOutputStream s = new FileOutputStream("domain.dat");
		try {
			saveDomain(new BufferedOutputStream(s));
		} finally {
			s.close();
		}
	}
}
