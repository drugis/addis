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

package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.DerivedStudyCharacteristic;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.DerivedStudyCharacteristic.Dosing;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class StudyPresentation extends PresentationModel<Study> {
	
	private StudyCharacteristicHolder d_armsHolder;
	private StudyCharacteristicHolder d_doseHolder;
	private StudyCharacteristicHolder d_drugHolder;
	private StudyCharacteristicHolder d_sizeHolder;
	private StudyCharacteristicHolder d_indicationHolder;
	private PresentationModelFactory d_pmf;
	
	private Map<Characteristic, StudyCharacteristicHolder> d_characteristicModelMap;
	
	public StudyPresentation(Study s, PresentationModelFactory pmf) {
		super(s);
		
		d_characteristicModelMap = new HashMap<Characteristic, StudyCharacteristicHolder>();
		d_pmf = pmf;
		
		d_armsHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.ARMS) {
			@Override
			protected Object getNewValue() {
				return getBean().getArms().size();
			}
		};
		addToCharMap(d_armsHolder);
		
		d_doseHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.DOSING) {
			@Override
			protected Object getNewValue() {
				Dosing dosing = DerivedStudyCharacteristic.Dosing.FIXED;
				for (Arm a : getBean().getArms()) {
					if (getBean().getTreatment(a) != null) {
						for (DrugTreatment treatment : getBean().getTreatment(a).getTreatments()) {
							AbstractDose dose = treatment.getDose();
							if (dose != null && dose instanceof FlexibleDose) {
								dosing = DerivedStudyCharacteristic.Dosing.FLEXIBLE;
							}
						}
					}
				}
				return dosing;
			}			
		};
		addToCharMap(d_doseHolder);
		
		d_drugHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.DRUGS) {
			@Override
			protected Object getNewValue() {
				return getBean().getTreatmentDefinitions();				
			}
		};
		addToCharMap(d_drugHolder);
		
		d_sizeHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.STUDYSIZE) {
			@Override
			protected Object getNewValue() {
				return getBean().getSampleSize();				
			}
		};
		addToCharMap(d_sizeHolder);
		
		d_indicationHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.INDICATION) {
			@Override
			protected Object getNewValue() {
				return getBean().getIndication();				
			}
		};
		addToCharMap(d_indicationHolder);
	}

	private void addToCharMap(StudyCharacteristicHolder holder) {
		d_characteristicModelMap.put(holder.getCharacteristic(), holder);
	}
	
	public StudyCharacteristicHolder getCharacteristicModel(Characteristic c) {
		StudyCharacteristicHolder holder = d_characteristicModelMap.get(c);
		return holder != null ? holder : new StudyCharacteristicHolder(getBean(), c);
	}
		
	public boolean isStudyFinished() {
		Object status = getBean().getCharacteristic(BasicStudyCharacteristic.STATUS);
		if (status != null) {
			return status.equals(BasicStudyCharacteristic.Status.COMPLETED);
		}
		return false;
	}
	
	public int getArmCount() {
		return getBean().getArms().size();
	}
	
	public List<BasicArmPresentation> getArms() {
		List<BasicArmPresentation> list = new ArrayList<BasicArmPresentation>();
		for (Arm arm : getBean().getArms()) {
			list.add((BasicArmPresentation) d_pmf.getModel(arm));
		}
		return list;
	}

	public int getPopulationCharacteristicCount() {
		return getPopulationCharacteristics().size();
	}

	public List<? extends Variable> getPopulationCharacteristics() {
		return getBean().getVariables(PopulationCharacteristic.class);
	}
	
	public LabeledPresentation getCharacteristicModel(BasicStudyCharacteristic c) {
		if (getBean().getCharacteristic(c) != null) {
			return d_pmf.getLabeledModel(getBean().getCharacteristic(c));
		}
		return null;
	}
	
	public List<OutcomeMeasure> getEndpoints() {
		List<OutcomeMeasure> endpoints = new ArrayList<OutcomeMeasure>();
		endpoints.addAll(Study.extractVariables(getBean().getEndpoints()));
		return endpoints;
	}
	
	public List<OutcomeMeasure> getAdverseEvents() {
		List<OutcomeMeasure> adverseEvents = new ArrayList<OutcomeMeasure>();
		adverseEvents.addAll(Study.extractVariables(getBean().getAdverseEvents()));
		return adverseEvents;
	}
	
	public PopulationCharTableModel getPopulationCharTableModel() {
		return new PopulationCharTableModel(getBean(), d_pmf);
	}
	
	public TableModel getEndpointTableModel() {
		return new StudyMeasurementTableModel(getBean(), d_pmf, Endpoint.class, false);
	}
	
	public TableModel getAdverseEventTableModel() {
		return new StudyMeasurementTableModel(getBean(), d_pmf, AdverseEvent.class, false);
	}

}
