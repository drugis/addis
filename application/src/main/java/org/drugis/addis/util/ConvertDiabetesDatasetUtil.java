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

package org.drugis.addis.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.CategoricalVariableType;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.WhenTaken;
import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.imports.PubMedIDRetriever;
import org.drugis.addis.util.jaxb.JAXBConvertor;
import org.drugis.addis.util.jaxb.JAXBConvertor.ConversionException;
import org.drugis.addis.util.jaxb.JAXBHandler;
import org.drugis.common.beans.AffixedObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.jgoodies.binding.list.ObservableList;

/**
 * Used to clean up the Diabetes Dataset on http://mantis.drugis.org/file_download.php?file_id=29
 */
public class ConvertDiabetesDatasetUtil {
	private Domain d_domain;

	public ConvertDiabetesDatasetUtil(Domain domain) {
		d_domain = domain;
	}

	public static void main(String[] args) throws JAXBException, ConversionException, IOException {
		InputStream is = new FileInputStream("diabetes-cleaner.addis");

		AddisData addisData = JAXBHandler.unmarshallAddisData(is);
		is.close();
		Domain domain = JAXBConvertor.convertAddisDataToDomain(addisData);
		ConvertDiabetesDatasetUtil util = new ConvertDiabetesDatasetUtil(domain);
		util.run();
		final AddisData out = JAXBConvertor.convertDomainToAddisData(domain);
		OutputStream fileWrite = new FileOutputStream("converted.addis");
		JAXBHandler.marshallAddisData(out, fileWrite);
		fileWrite.close();

	}

	private void run() throws IOException {
		changeRaceEndpoints();
		removeMeasuredOnceOutcomeMeasures();
		renameStudies();
	}

	private void renameStudies() throws IOException {
		for(Study study : d_domain.getStudies()) {
			PubMedIdList pubmed = (PubMedIdList)study.getCharacteristic(BasicStudyCharacteristic.PUBMED);

			try {
				Document doc = getPubMedXML(pubmed);

				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();

				XPathExpression yearExpr = xpath.compile("/PubmedArticleSet/PubmedArticle[1]/MedlineCitation[1]/DateCreated[1]/Year[1]");
				Object yearResults = yearExpr.evaluate(doc, XPathConstants.NODESET);

				String year = ((NodeList) yearResults).item(0).getTextContent();

				XPathExpression authorExpr = xpath.compile("/PubmedArticleSet/PubmedArticle[1]/MedlineCitation[1]/Article[1]/AuthorList[1]/Author/LastName");
				Object authorResults = authorExpr.evaluate(doc, XPathConstants.NODESET);
				NodeList authorNodes = (NodeList)authorResults;

				List<String> authors = new ArrayList<String>();

				for (int i = 0; i < authorNodes.getLength(); i++) {
					authors.add(authorNodes.item(i).getTextContent());
				}
				String title = "";
				if(authors.size() > 2) {
					title = authors.get(0) + " et al, " + year;
				} else {
					title = StringUtils.join(authors, ", ") + ", " + year;
				}
				study.setName(title);

			} catch (Exception e) {
				continue;
			}
		}
	}

	private Document getPubMedXML(PubMedIdList pubmed) throws ParserConfigurationException, IOException, SAXException {
		String id = pubmed.get(0).getId();
		String url = PubMedIDRetriever.PUBMED_API + "efetch.fcgi?db=pubmed&id=" + id + "&retmode=xml";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(false);
		dbf.setIgnoringElementContentWhitespace(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputStream openStream = PubMedIDRetriever.openUrl(url);
		Document doc = db.parse(openStream);
		return doc;
	}

	private void removeMeasuredOnceOutcomeMeasures() {
		List<Variable> variables = new ArrayList<Variable>();
		variables.addAll(d_domain.getEndpoints());
		variables.addAll(d_domain.getAdverseEvents());
		variables.addAll(d_domain.getPopulationCharacteristics());

		for(Variable var : variables) {
			ObservableList<Study> studies = d_domain.getStudies(var);
			if(studies.getSize() < 2) {
				for(Study study : studies) {
					StudyOutcomeMeasure<Variable> som = study.findStudyOutcomeMeasure(var);
					study.getStudyOutcomeMeasures().remove(som);

				}
				d_domain.getAdverseEvents().remove(var);
				d_domain.getEndpoints().remove(var);
				d_domain.getPopulationCharacteristics().remove(var);

			}
		}

	}

	private void changeRaceEndpoints() {
		PopulationCharacteristic newChar = EntityUtil.findByName(d_domain.getPopulationCharacteristics(), "Race (Taxonomic)");
		ObservableList<String> newCats = ((CategoricalVariableType)newChar.getVariableType()).getCategories();

		for(Study study : d_domain.getStudies()) {
			StudyOutcomeMeasure<PopulationCharacteristic> oldSom = getPopulationChar(d_domain, study);
			if(oldSom == null) continue;
			ObservableList<String> oldCats = ((CategoricalVariableType) oldSom.getValue().getVariableType()).getCategories();

			StudyOutcomeMeasure<PopulationCharacteristic> newSom = oldSom.clone();
			newSom.setValue(newChar);
			study.getStudyOutcomeMeasures().add(study.getStudyOutcomeMeasures().indexOf(oldSom), newSom);
			for(WhenTaken wt : oldSom.getWhenTaken()) {
				for(Arm arm : AffixedObservableList.createSuffixed(study.getArms(),  (Arm)null)) {
					FrequencyMeasurement m = (FrequencyMeasurement) study.getMeasurement(oldSom.getValue(), arm, wt);
					if(m == null) continue;
					FrequencyMeasurement newFreq = new FrequencyMeasurement(newChar);

					for(String oldCat : oldCats) {
						setNewFreq(newCats, m, newFreq, oldCat);
					}
					study.setMeasurement(newSom, arm, wt, newFreq);
				}
			}
			newSom.getNotes().add(new Note(Source.MANUAL, "Re-encoded from: " + StringUtils.join(oldCats, ", ")));
			study.getStudyOutcomeMeasures().remove(oldSom);
		}
	}

	private void setNewFreq(ObservableList<String> newCats, FrequencyMeasurement oldFreq, FrequencyMeasurement newFreq,
			String oldCat) {
		oldCat = StringUtils.capitalize(oldCat);
		int frequency = oldFreq.getFrequency(oldCat.toLowerCase());
		String newCat = null;
		for(String cat : newCats) {
			if(oldCat.toLowerCase().startsWith(cat.toLowerCase())) {
				newCat = cat;
			}
		}
		oldCat = (newCat == null) ? "Other" : newCat;
		Integer f = newFreq.getFrequency(oldCat);
		newFreq.setFrequency(oldCat, (f == null ? 0 : f) + frequency);
	}

	private StudyOutcomeMeasure<PopulationCharacteristic> getPopulationChar(Domain domainData, Study study) {
		for(StudyOutcomeMeasure<PopulationCharacteristic> popChar  : study.getPopulationChars())  {
			if(popChar.getValue().getName().matches("(?i)race.*")) {
				return popChar;
			}
		}
		return null;
	}
}
