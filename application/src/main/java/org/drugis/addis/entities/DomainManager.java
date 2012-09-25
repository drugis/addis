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

package org.drugis.addis.entities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.gui.Main.XMLStreamFilter;
import org.drugis.addis.util.jaxb.JAXBConvertor;
import org.drugis.addis.util.jaxb.JAXBHandler;
import org.drugis.addis.util.jaxb.JAXBConvertor.ConversionException;



public class DomainManager {
	private Domain d_domain = new DomainImpl();

	public Domain getDomain() {
		return d_domain;
	}

	public void resetDomain() {
		d_domain = new DomainImpl();
	}

	/**
	 * Replace the Domain by a new instance loaded from a XML stream (old format, .xml).
	 * @param is Stream to read objects from.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void loadLegacyXMLDomain(InputStream is) throws IOException {
		try {
			InputStream transformedXmlStream = JAXBConvertor.transformLegacyXML(is);
			is.close();
			loadXMLDomain(transformedXmlStream, 1);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Replace the Domain by a new instance loaded from a XML stream (new format, .addis).
	 * @param is XML stream to read objects from.
	 * @param version Schema version the xml is in.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void loadXMLDomain(InputStream is, int version) throws IOException {
		try {
			AddisData data = JAXBHandler.unmarshallAddisData(JAXBConvertor.transformToLatest(is, version));
			d_domain = (Domain) JAXBConvertor.convertAddisDataToDomain(data);
			is.close();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		} catch (ConversionException e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Save the domain to an XML file (new format, .addis)
	 * @param file File to write the domain to.
	 */
	public void saveXMLDomain(File file) throws IOException {
		try {
			AddisData addisData = JAXBConvertor.convertDomainToAddisData(d_domain);

			// Open stream *after* conversion so we don't clear the file if it can't be saved.
			FileOutputStream os = new FileOutputStream(file);
			saveAddisData(addisData, os);
		} catch (ConversionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Save the domain to an XML stream (new format, .addis). WARNING: only for use in tests, as it increases the risk of data loss.
	 * @see {@link #saveXMLDomain(File)}
	 * @param os Stream to write domain to.
	 */
	public void saveXMLDomain(OutputStream os) throws IOException {
		try {
			AddisData addisData = JAXBConvertor.convertDomainToAddisData(d_domain);
			saveAddisData(addisData, os);
		} catch (ConversionException e) {
			throw new RuntimeException(e);
		}
	}

	private void saveAddisData(AddisData data, OutputStream os) throws IOException {
		try {
			FilterOutputStream fos = new XMLStreamFilter(os);
			JAXBHandler.marshallAddisData(data, fos);
			fos.close();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}
