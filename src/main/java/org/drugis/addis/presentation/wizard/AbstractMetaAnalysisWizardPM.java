/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.DefaultListHolder;
import org.drugis.addis.presentation.DefaultSelectableStudyListPresentationModel;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyListPresentationModel;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.ValueHolder;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public abstract class AbstractMetaAnalysisWizardPM<G extends StudyGraphModel> extends AbstractWizardWithSelectableIndicationPM {

	protected PresentationModelFactory d_pmf;
	protected ModifiableHolder<OutcomeMeasure> d_outcomeHolder;
	protected OutcomeListHolder d_outcomeListHolder;
	protected DrugListHolder d_drugListHolder;
	protected G d_studyGraphPresentationModel;	
	private StudiesMeasuringValueModel d_studiesMeasuringValueModel;
	protected Map<Study, Map<Drug, ModifiableHolder<Arm>>> d_selectedArms;
	protected DefaultSelectableStudyListPresentationModel d_studyListPm;	

	public AbstractMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmf) {
		super(d);
		d_pmf = pmf;
	
		d_outcomeHolder = new ModifiableHolder<OutcomeMeasure>();
		
		d_indicationHolder.addPropertyChangeListener(new SetEmptyListener(d_outcomeHolder));
	
		d_outcomeListHolder = new OutcomeListHolder(d_indicationHolder, d_domain);		
		d_drugListHolder = new DrugListHolder();
		d_studyGraphPresentationModel = buildStudyGraphPresentation();
		buildDrugHolders();
		d_studyListPm = new DefaultSelectableStudyListPresentationModel(new StudyListHolder());

		d_studiesMeasuringValueModel = new StudiesMeasuringValueModel();
		
		d_selectedArms = new HashMap<Study, Map<Drug, ModifiableHolder<Arm>>>();
		
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ev) {
				updateArmHolders();
			}
		};
		d_studyListPm.getSelectedStudiesModel().addValueChangeListener(listener);
	}
	
	protected abstract void buildDrugHolders();
	
	abstract protected G buildStudyGraphPresentation();
	
	protected List<Study> getStudiesEndpointAndIndication() {
		if (d_outcomeHolder.getValue() == null || d_indicationHolder.getValue() == null) {
			return Collections.emptyList();
		}
		List<Study> studies = new ArrayList<Study>(d_domain.getStudies(d_outcomeHolder.getValue()).getValue());
		studies.retainAll(d_domain.getStudies(d_indicationHolder.getValue()).getValue());
		return studies;
	}	
	
	public ValueModel getStudiesMeasuringLabelModel() {
		return d_studiesMeasuringValueModel;
	}	
	
	public ValueHolder<OutcomeMeasure> getOutcomeMeasureModel() {
		return d_outcomeHolder;
	}

	public AbstractListHolder<Drug> getDrugListModel() {
		return d_drugListHolder;
	}

	public AbstractListHolder<OutcomeMeasure> getOutcomeMeasureListModel() {
		return d_outcomeListHolder;
	}

	public abstract ListHolder<Drug> getSelectedDrugsModel();

	public G getStudyGraphModel() {
		return d_studyGraphPresentationModel;
	}

	protected void updateArmHolders() {
		d_selectedArms.clear();
		
		for(Study s : getStudyListModel().getSelectedStudiesModel().getValue()) {
			d_selectedArms.put(s, new HashMap<Drug, ModifiableHolder<Arm>>());
			for(Drug d : getSelectedDrugsModel().getValue()){
				if(s.getDrugs().contains(d)){
					d_selectedArms.get(s).put(d, new ModifiableHolder<Arm>(getDefaultArm(s, d)));
				}
			}
		}
	}

	private Arm getDefaultArm(Study s, Drug d) {
		return getArmsPerStudyPerDrug(s, d).getValue().get(0);
	}

	public ListHolder<Arm> getArmsPerStudyPerDrug(Study study, Drug drug) {
		return new ArmListHolder(study, drug);
	}

	public ModifiableHolder<Arm> getSelectedArmModel(Study study, Drug drug) {
		return d_selectedArms.get(study).get(drug);
	}

	public SelectableStudyListPresentationModel getStudyListModel() {
		return d_studyListPm;
	}

	public MetaAnalysis saveMetaAnalysis(String name) throws EntityIdExistsException {
		MetaAnalysis ma = createMetaAnalysis(name);		
		d_domain.addMetaAnalysis(ma);
		return ma;
	}

	public abstract MetaAnalysis createMetaAnalysis(String name);

	@SuppressWarnings("serial")
	protected class DrugListHolder extends AbstractListHolder<Drug> implements PropertyChangeListener {
		public DrugListHolder() {
			d_outcomeHolder.addValueChangeListener(this);
		}
		
		@Override
		public List<Drug> getValue() {
			SortedSet<Drug> drugs = new TreeSet<Drug>();
			if (d_indicationHolder.getValue() != null && d_outcomeHolder.getValue() != null) {
				List<Study> studies = getStudiesEndpointAndIndication();
				for (Study s : studies) {
					drugs.addAll(s.getDrugs());
				}
			}			
			return new ArrayList<Drug>(drugs);
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			fireValueChange(null, getValue());
		}
	}
	
	@SuppressWarnings("serial")
	public class StudiesMeasuringValueModel extends AbstractValueModel implements PropertyChangeListener {
		
		public StudiesMeasuringValueModel() {
			d_outcomeHolder.addValueChangeListener(this);			
		}

		public Object getValue() {
			return constructString();
		}

		private Object constructString() {
			String indVal = d_indicationHolder.getValue() != null ? d_indicationHolder.getValue().toString() : "";
			String endpVal = d_outcomeHolder.getValue() != null ? d_outcomeHolder.getValue().toString() : "";
			return "Studies measuring " + indVal + " on " + endpVal;
		}
		
		public void setValue(Object newValue) {
			throw new RuntimeException("value set not allowed");
		}

		public void propertyChange(PropertyChangeEvent arg0) {
			fireValueChange(null, constructString());
		}		
	}

	@SuppressWarnings("serial")
	protected class StudyListHolder extends DefaultListHolder<Study> implements PropertyChangeListener {
		public StudyListHolder() {
			super(new ArrayList<Study>());
			getSelectedDrugsModel().addValueChangeListener(this);
		}
	
		public void propertyChange(PropertyChangeEvent ev) {
			List<Study> allStudies = getStudiesEndpointAndIndication();
			List<Study> okStudies = new ArrayList<Study>();
			for (Study s : allStudies) {
				List<Drug> drugs = new ArrayList<Drug>(s.getDrugs());
				drugs.retainAll(getSelectedDrugsModel().getValue());
				if (drugs.size() >= 2 && s.canBeUsedForAnalysis(d_outcomeHolder.getValue())) {
					okStudies.add(s);
				}
			}
			setValue(okStudies);
		}		
	}	
}
