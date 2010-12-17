/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.table.TableModel;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.DependentEntitiesException;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.imports.ClinicaltrialsImporter;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.BasicArmPresentation;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.MutableCharacteristicHolder;
import org.drugis.addis.presentation.PopulationCharTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectAdverseEventsPresentation;
import org.drugis.addis.presentation.SelectEndpointPresentation;
import org.drugis.addis.presentation.SelectFromFiniteListPresentation;
import org.drugis.addis.presentation.SelectPopulationCharsPresentation;
import org.drugis.addis.presentation.StudyMeasurementTableModel;
import org.drugis.addis.presentation.StudyNoteHolder;
import org.drugis.addis.presentation.StudyPresentation;

import com.jgoodies.binding.value.ValueModel;

public class AddStudyWizardPresentation {
	
	public interface OutcomeMeasurementsModel {
		TableModel getMeasurementTableModel();
	}
	
	@SuppressWarnings("serial")
	private class DrugListHolder extends AbstractListHolder<Drug> implements PropertyChangeListener, DomainListener {
		public DrugListHolder() {
			d_domain.addListener(this);
		}
		
		@Override
		public List<Drug> getValue() {
			return new ArrayList<Drug>(d_domain.getDrugs());
		}

		public void propertyChange(PropertyChangeEvent evt) {
			fireValueChange(null, getValue());
		}

		public void domainChanged(DomainEvent evt) {
			fireValueChange(null, getValue());
		}
	}
	
	@SuppressWarnings("serial")
	private abstract static class OutcomeListHolder<T extends OutcomeMeasure>
	extends AbstractListHolder<T> 
	implements PropertyChangeListener, DomainListener {
		protected Domain d_domain;
		
		public OutcomeListHolder(Domain domain) {
			domain.addListener(this);
			d_domain = domain;
		}

		public void propertyChange(PropertyChangeEvent arg0) {
			fireValueChange(null, getValue());			
		}

		public void domainChanged(DomainEvent evt) {
			fireValueChange(null, getValue());			
		}
	}
	
	@SuppressWarnings("serial")
	private static class EndpointListHolder extends OutcomeListHolder<Endpoint> {
		public EndpointListHolder(Domain domain) {
			super(domain);
		}
		
		@Override
		public List<Endpoint> getValue() {
			return new ArrayList<Endpoint>(d_domain.getEndpoints());
		}
	}

	@SuppressWarnings("serial")
	private static class AdverseEventListHolder extends OutcomeListHolder<AdverseEvent> {
		public AdverseEventListHolder(Domain domain) {
			super(domain);
		}
		
		@Override
		public List<AdverseEvent> getValue() {
			return new ArrayList<AdverseEvent>(d_domain.getAdverseEvents());
		}
	}
	
	@SuppressWarnings("serial")
	private class IndicationListHolder extends AbstractListHolder<Indication> {
		public IndicationListHolder() {
			d_domain.addListener(new DomainListener() {
				public void domainChanged(DomainEvent evt) {
					fireValueChange(null, getValue());
				}
			});
		}
		
		@Override
		public List<Indication> getValue() {
			return new ArrayList<Indication>(d_domain.getIndications());
		}
	}
	
	private Domain d_domain;
	private PresentationModelFactory d_pmf;
	private StudyPresentation d_newStudyPM;
	private StudyPresentation d_importedStudyPM;
	
	//List<ModifiableHolder<Endpoint>> d_selectedEndpointsList;
	
	List<BasicArmPresentation> d_selectedArmList;
	private ListHolder<Endpoint> d_endpointListHolder;
	private ListHolder<AdverseEvent> d_adverseEventListHolder;
	private ListHolder<PopulationCharacteristic> d_populationCharsListHolder;
	private SelectAdverseEventsPresentation d_adverseEventSelect;
	private SelectFromFiniteListPresentation<PopulationCharacteristic> d_populationCharSelect;
	private SelectFromFiniteListPresentation<Endpoint> d_endpointSelect;
	
	private Study d_origStudy = null;
	
	public AddStudyWizardPresentation(Domain d, PresentationModelFactory pmf, AddisWindow mainWindow) {
		d_domain = d;
		d_pmf = pmf;
		d_endpointListHolder = new EndpointListHolder(d_domain);
		d_adverseEventListHolder = new AdverseEventListHolder(d_domain);
		d_populationCharsListHolder = d_domain.getPopulationCharacteristicsHolder();
		d_endpointSelect = new SelectEndpointPresentation(d_endpointListHolder, mainWindow, this);
		d_adverseEventSelect = new SelectAdverseEventsPresentation(d_adverseEventListHolder, mainWindow);
		d_populationCharSelect = new SelectPopulationCharsPresentation(d_populationCharsListHolder, mainWindow);
		clearStudies();
	}
	
	public AddStudyWizardPresentation(Domain d, PresentationModelFactory pmf, AddisWindow mainWindow, Study origStudy) {
		this(d, pmf, mainWindow);
		d_origStudy  = origStudy;
		setNewStudy(origStudy.clone());
	}
	
	public ValueModel getSourceModel() {
		return new MutableCharacteristicHolder(getNewStudy(), BasicStudyCharacteristic.SOURCE);
	}
	
	public ValueModel getSourceNoteModel() {
		return new StudyNoteHolder(getImportStudy(), BasicStudyCharacteristic.SOURCE);
	}
	
	public ValueModel getIdModel() {
		return d_newStudyPM.getModel(Study.PROPERTY_ID);
	}
	
	public ValueModel getIdNoteModel() {
		return new StudyNoteHolder(getImportStudy(), Study.PROPERTY_ID);
	}
	
	public ValueModel getTitleModel() {
		return new MutableCharacteristicHolder(getNewStudy(), BasicStudyCharacteristic.TITLE);
	}
	
	public Domain getDomain() {
		return d_domain;
	}

	public void importCT() throws IOException {
		if(getIdModel().getValue().toString().length() != 0) {
			String studyID = getIdModel().getValue().toString().trim().replace(" ", "%20");
			String url = "http://clinicaltrials.gov/show/"+studyID+"?displayxml=true";
			d_importedStudyPM = (StudyPresentation) new StudyPresentation(ClinicaltrialsImporter.getClinicaltrialsData(url),d_pmf);
			d_newStudyPM = (StudyPresentation) new StudyPresentation(new Study("", new Indication(0l,"")),d_pmf);
			migrateImportToNew(studyID);
		} else {
			throw new IOException("No Study Id Entered");
		}
	}
	
	public void setNewStudy(Study study) {
		d_newStudyPM = new StudyPresentation(study, d_pmf);
		d_endpointSelect.clear();
		for (Endpoint e : study.getEndpoints()) {
			d_endpointSelect.addSlot();
			d_endpointSelect.getSlot(d_endpointSelect.countSlots() -1).setValue(e);
		}
		d_selectedArmList = new ArrayList<BasicArmPresentation>();
		for (Arm a : study.getArms()) {
			BasicArmPresentation armHolder = new BasicArmPresentation(a, d_pmf);
			d_selectedArmList.add(armHolder);
		}
		for (AdverseEvent ade : study.getAdverseEvents()) {
			d_adverseEventSelect.addSlot();
			d_adverseEventSelect.getSlot(d_adverseEventSelect.countSlots() - 1).setValue(ade);
		}
		for (PopulationCharacteristic pc : study.getPopulationCharacteristics()) {
			d_populationCharSelect.addSlot();
			d_populationCharSelect.getSlot(d_populationCharSelect.countSlots() - 1).setValue(pc);
		}
		
	}

	private void migrateImportToNew(Object studyID) {
		// Characteristics
		getNewStudy().getCharacteristics().putAll(getImportStudy().getCharacteristics());
		// Source
		getSourceModel().setValue(Source.CLINICALTRIALS);
		// Id & Title
		getIdModel().setValue(getImportStudy().getStudyId());
		getTitleModel().setValue(getImportStudy().getCharacteristic(BasicStudyCharacteristic.TITLE));
		
		// Endpoints.
		d_endpointSelect.clear();
		//d_selectedEndpointsList = new ArrayList<ModifiableHolder<Endpoint>>();
		for (int i=0 ; i < getImportStudy().getOutcomeMeasures().size(); i++)
			d_endpointSelect.addSlot();
		//addEndpointModels(getOldStudy().getOutcomeMeasures().size());
		
		// Arms & Dosage
		d_selectedArmList = new ArrayList<BasicArmPresentation>();
		addArmModels(getImportStudy().getArms().size());

	}

	public ListHolder<Indication> getIndicationListModel() {
		return new IndicationListHolder();
	}
	
	public ValueModel getIndicationModel() {
		return d_newStudyPM.getModel(Study.PROPERTY_INDICATION);
	}

	public ValueModel getIndicationNoteModel() {
		return new StudyNoteHolder(getImportStudy(), Study.PROPERTY_INDICATION);
	}
	
	public void clearStudies() {
		d_importedStudyPM = (StudyPresentation) new StudyPresentation(new Study("", new Indication(0l,"")),d_pmf);
		d_newStudyPM = (StudyPresentation) new StudyPresentation(new Study("", new Indication(0l,"")),d_pmf);
		getSourceModel().setValue(Source.MANUAL);
		d_endpointSelect.clear();
		while (d_adverseEventSelect.countSlots() > 0) {
			d_adverseEventSelect.removeSlot(0);
		}
		d_selectedArmList = new ArrayList<BasicArmPresentation>();
		d_endpointSelect.addSlot();
		addArmModels(2);
		
	}
	
	
	public MutableCharacteristicHolder getCharacteristicModel(BasicStudyCharacteristic c) {
		return new MutableCharacteristicHolder(getNewStudy(),c);
	}

	public ValueModel getCharacteristicNoteModel(BasicStudyCharacteristic c) {
		return new StudyNoteHolder(getImportStudy(), c);
	}

	public ValueModel getEndpointNoteModel(int i) {
		if(d_importedStudyPM.getEndpoints().size() <= i)
			return null;
		return new StudyNoteHolder(getImportStudy(),new ArrayList<OutcomeMeasure>(d_importedStudyPM.getEndpoints()).get(i));
	}

	public void removeImportEndpoint(int i) {
		if ( getImportStudy().getEndpoints().size() > i)
			getImportStudy().removeEndpoint(i);
	}

	
	public void addArmModels(int numArms) {
		for(int i = 0; i<numArms; ++i){
			d_selectedArmList.add(new BasicArmPresentation(new Arm(new Drug("", ""), new FixedDose(0l, SIUnit.MILLIGRAMS_A_DAY),0), d_pmf));
		}
	}
	
	public int getNumberArms(){
		return d_selectedArmList.size();
	}
	
	public void removeArm(int armNum){
		d_selectedArmList.remove(armNum);
		
		if( getImportStudy().getArms().size() > armNum )
			getImportStudy().getArms().remove(getImportStudy().getArms().get(armNum));	
	}
	
	public DrugListHolder getDrugsModel(){
		return new DrugListHolder();
	}
	
	public BasicArmPresentation getArmModel(int armNumber){
		return d_selectedArmList.get(armNumber);
	}
	
	public ValueModel getArmNoteModel(int curArmNumber) {
		if(d_importedStudyPM.getArms().size() <= curArmNumber)
			return null;
		return new StudyNoteHolder(getImportStudy(),getImportStudy().getArms().get(curArmNumber));
	}

	public StudyMeasurementTableModel getEndpointMeasurementTableModel() {
		commitOutcomesArmsToNew();
		return new StudyMeasurementTableModel(getNewStudy(),d_pmf, Endpoint.class);
	}
	
	
	public Study saveStudy() {
		if (d_selectedArmList.isEmpty()) 
			throw new IllegalStateException("No arms selected in study.");
		if (d_endpointSelect.countSlots() == 0) 
			throw new IllegalStateException("No endpoints selected in study.");
		if (!checkID())
			throw new IllegalStateException("Study with this ID already exists in domain");
		
		// transfer the notes from the imported study to the new one.
		if (d_importedStudyPM != null)
			transferNotes();
		

		if (isEditing()) {
			try {
				d_domain.deleteEntity(d_origStudy);
			} catch (DependentEntitiesException e) {
				e.printStackTrace();
			}
		}
		
		// Add the study to the domain.
		d_domain.addStudy(getNewStudy());
		
		d_newStudyPM.isStudyFinished();
		
		return getNewStudy();
	}

	public boolean checkID() {
		if (!d_domain.getStudies().contains(getNewStudy())) {
			return true;
		}
		
		if (isEditing()) {
			return getNewStudy().equals(d_origStudy);
		}
		
		return false;
	}
	
	Study getStudy() {
		return getNewStudy();
	}
	
	void commitOutcomesArmsToNew(){
		List<Endpoint> outcomeMeasures = new ArrayList<Endpoint>();
		for(ModifiableHolder<Endpoint> outcomeHolder : d_endpointSelect.getSlots()) {
			outcomeMeasures.add(outcomeHolder.getValue());
		}	
		getNewStudy().setEndpoints(outcomeMeasures);
		
		List<Arm> arms = new ArrayList<Arm>();
		for(BasicArmPresentation arm : d_selectedArmList) { 
			arms.add(arm.getBean());
		}
		
		getNewStudy().setArms(arms);
	}
	
	private void commitAdverseEventsToStudy() {
		List<AdverseEvent> outcomeMeasures = new ArrayList<AdverseEvent>();
		for(ModifiableHolder<AdverseEvent> outcomeHolder : d_adverseEventSelect.getSlots()) {
			outcomeMeasures.add(outcomeHolder.getValue());
		}	
		getNewStudy().setAdverseEvents(outcomeMeasures);
	}
	
	private void commitPopulationCharsToStudy() {
		List<PopulationCharacteristic> outcomeMeasures = new ArrayList<PopulationCharacteristic>();
		for(ModifiableHolder<PopulationCharacteristic> outcomeHolder : d_populationCharSelect.getSlots()) {
			outcomeMeasures.add(outcomeHolder.getValue());
		}	
		getNewStudy().setPopulationCharacteristics(outcomeMeasures);
	}
	
	private void transferNotes() {
		for (Entry<Object,Note> noteEntry : getImportStudy().getNotes().entrySet()) {
			Object key = noteEntry.getKey();
			Note value = noteEntry.getValue();
			/* If notes are keyed by either Characteristic or Property (String), we can just copy them */
			if ((key instanceof BasicStudyCharacteristic) || (key instanceof String)) {
				getNewStudy().putNote(key,value);
			} else if (key instanceof Endpoint) {
				int outcomeIndex = getImportStudy().getEndpoints().indexOf(key);
				if (outcomeIndex > -1) {
					Object newKey =  getNewStudy().getEndpoints().get(outcomeIndex);
					getNewStudy().putNote(newKey,value);
				}
			} else if (key instanceof Arm) {
				int armIndex = getImportStudy().getArms().indexOf(key);
				if (armIndex > -1) {
					Object newKey =  getNewStudy().getArms().get(armIndex);
					getNewStudy().putNote(newKey,value);
				}
			}
		}
	}

	private Study getNewStudy() {
		return d_newStudyPM.getBean();
	}
	
	public StudyPresentation getNewStudyPM() {
		return d_newStudyPM;
	}

	private Study getImportStudy() {
		return d_importedStudyPM.getBean();
	}

	private StudyMeasurementTableModel getAdverseEventMeasurementTableModel() {
		commitAdverseEventsToStudy();
		return new StudyMeasurementTableModel(getNewStudy(),d_pmf, AdverseEvent.class);
	}
	
	private PopulationCharTableModel getPopulationCharMeasurementTableModel() {
		commitPopulationCharsToStudy();
		return d_newStudyPM.getPopulationCharTableModel();
	}

	public OutcomeMeasurementsModel getAdverseEventsModel() {
		return new OutcomeMeasurementsModel() {
			public StudyMeasurementTableModel getMeasurementTableModel() {
				return getAdverseEventMeasurementTableModel();
			}
		};
	} 
	
	public OutcomeMeasurementsModel getEndpointsModel() {
		return new OutcomeMeasurementsModel() {
			public StudyMeasurementTableModel getMeasurementTableModel() {
				return getEndpointMeasurementTableModel();
			}
		};
	}
	
	public OutcomeMeasurementsModel getPopulationCharsModel() {
		return new OutcomeMeasurementsModel() {
			public TableModel getMeasurementTableModel() {
				return getPopulationCharMeasurementTableModel();
			}
		};
	} 

	public SelectFromFiniteListPresentation<AdverseEvent> getAdverseEventSelectModel() {
		return d_adverseEventSelect;
	}
	
	public SelectFromFiniteListPresentation<Endpoint> getEndpointSelectModel() {
		return d_endpointSelect;
	} 

	public SelectFromFiniteListPresentation<PopulationCharacteristic> getPopulationCharSelectModel() {
		return d_populationCharSelect;
	}

	public boolean isEditing() {
		return (d_origStudy != null);
	}
}
