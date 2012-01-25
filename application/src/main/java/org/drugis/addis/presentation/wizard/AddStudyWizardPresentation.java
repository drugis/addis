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

package org.drugis.addis.presentation.wizard;

import java.io.IOException;
import java.util.HashSet;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.TableModel;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.DependentEntitiesException;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.drugis.addis.entities.TypeWithNotes;
import org.drugis.addis.entities.WhenTaken;
import org.drugis.addis.entities.StudyActivity.UsedBy;
import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.imports.ClinicaltrialsImporter;
import org.drugis.addis.presentation.BasicArmPresentation;
import org.drugis.addis.presentation.MutableCharacteristicHolder;
import org.drugis.addis.presentation.PopulationCharTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectAdverseEventsPresentation;
import org.drugis.addis.presentation.SelectEndpointPresentation;
import org.drugis.addis.presentation.SelectFromFiniteListPresentation;
import org.drugis.addis.presentation.SelectPopulationCharsPresentation;
import org.drugis.addis.presentation.StudyMeasurementTableModel;
import org.drugis.addis.presentation.StudyPresentation;
import org.drugis.addis.presentation.TreatmentActivityPresentation;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.beans.ContentAwareListModel;
import org.drugis.common.beans.SortedSetModel;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class AddStudyWizardPresentation {
	
	public static class WhenTakenFactory {
		private final AddEpochsPresentation d_epochs;

		public WhenTakenFactory(AddEpochsPresentation epochs) {
			d_epochs = epochs;
		}
		
		public WhenTaken buildDefault() {
			return new WhenTaken(EntityUtil.createDuration("P0D"), RelativeTo.BEFORE_EPOCH_END, d_epochs.getList().get(0));
		}
	}

	public abstract class OutcomeMeasurementsModel {
		abstract public TableModel getMeasurementTableModel();
	}
	
	private Domain d_domain;
	private PresentationModelFactory d_pmf;
	private StudyPresentation d_newStudyPM;
	
	private SelectAdverseEventsPresentation d_adverseEventSelect;
	private SelectPopulationCharsPresentation d_populationCharSelect;
	private SelectEndpointPresentation d_endpointSelect;
	private AddArmsPresentation d_arms;
	private AddEpochsPresentation d_epochs;
	
	private Study d_origStudy = null;
	private AddisWindow d_mainWindow;
	public AddStudyWizardPresentation(Domain d, PresentationModelFactory pmf, AddisWindow mainWindow) {
		d_domain = d;
		d_pmf = pmf;
		d_newStudyPM = new StudyPresentation(new Study(), pmf);
		d_mainWindow = mainWindow;
		d_epochs = new AddEpochsPresentation(getNewStudy(), "Epoch", 1);
		WhenTakenFactory wtf = new WhenTakenFactory(d_epochs);
		d_endpointSelect = new SelectEndpointPresentation(d_domain.getEndpoints(), wtf, d_mainWindow);
		d_adverseEventSelect = new SelectAdverseEventsPresentation(d_domain.getAdverseEvents(), wtf, d_mainWindow);
		d_populationCharSelect = new SelectPopulationCharsPresentation(d_domain.getPopulationCharacteristics(), wtf, d_mainWindow);
		d_arms = new AddArmsPresentation(getNewStudy(), "Arm", 2);
		resetStudy();
	}
	
	private void updateSelectionHolders() {
		getAddArmsModel().setStudy(getNewStudy());
		getAddEpochsModel().setStudy(getNewStudy());
		
		d_endpointSelect.setSlots(getNewStudy().getEndpoints());
		d_adverseEventSelect.setSlots(getNewStudy().getAdverseEvents());
		d_populationCharSelect.setSlots(getNewStudy().getPopulationChars());
		
		ListDataListener removeOrphansListener = new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				deleteOrphanUsedBys();
			}
			
			public void intervalAdded(ListDataEvent e) {
				deleteOrphanUsedBys();
			}
			
			public void contentsChanged(ListDataEvent e) {
				deleteOrphanUsedBys();
			}
		};
		getAddArmsModel().getList().addListDataListener(removeOrphansListener);
		getAddEpochsModel().getList().addListDataListener(removeOrphansListener);
		
		new ContentAwareListModel<StudyOutcomeMeasure<Endpoint>>(getNewStudy().getEndpoints());
		new ContentAwareListModel<StudyOutcomeMeasure<AdverseEvent>>(getNewStudy().getAdverseEvents());
		new ContentAwareListModel<StudyOutcomeMeasure<PopulationCharacteristic>>(getNewStudy().getPopulationChars());
	}
	
	void deleteOrphanUsedBys() {
		for (StudyActivity sa : getNewStudy().getStudyActivities()) {
			for (UsedBy ub: sa.getUsedBy()) {
				if(getNewStudy().findArm(ub.getArm().getName()) == null || getNewStudy().findEpoch(ub.getEpoch().getName()) == null) {
					HashSet<UsedBy> usedBy = new HashSet<UsedBy>(sa.getUsedBy());
					usedBy.remove(ub);
					sa.setUsedBy(usedBy);
				}
			}
		}
	}
	
	public AddStudyWizardPresentation(Domain d, PresentationModelFactory pmf, AddisWindow mainWindow, Study origStudy) {
		this(d, pmf, mainWindow);
		d_origStudy = origStudy;
		setNewStudy(origStudy.clone());
	}
	
	public ValueModel getSourceModel() {
		return getCharacteristicModel(BasicStudyCharacteristic.SOURCE);
	}
	
	public ValueModel getSourceNoteModel() {
		return getCharacteristicNoteModel(BasicStudyCharacteristic.SOURCE);
	}
	
	public ValueModel getIdModel() {
		return d_newStudyPM.getModel(Study.PROPERTY_NAME);
	}
	
	public ValueModel getIdNoteModel() {
		return new NoteModel(getNewStudy());
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
			Study clinicaltrialsData = ClinicaltrialsImporter.getClinicaltrialsData(url);
			setNewStudy(clinicaltrialsData);
		}
	}
	
	public void setNewStudy(Study study) {
		d_newStudyPM = new StudyPresentation(study, d_pmf);
		updateSelectionHolders();
	}

	public SortedSetModel<Indication> getIndicationsModel() {
		return d_domain.getIndications();
	}
	
	public ValueModel getIndicationModel() {
		return d_newStudyPM.getModel(Study.PROPERTY_INDICATION);
	}

	public ValueModel getIndicationNoteModel() {
		return new NoteModel(getNewStudy().getIndicationWithNotes());
	}
	
	public void resetStudy() {
		d_newStudyPM = (StudyPresentation) new StudyPresentation(new Study(), d_pmf);
		getSourceModel().setValue(Source.MANUAL);
		
		// Add 2 arms by default:
		getArms().add(getAddArmsModel().createItem());
		getArms().add(getAddArmsModel().createItem());
		
		// Add 1 epoch by default:
		getEpochs().add(getAddEpochsModel().createItem());
		
		updateSelectionHolders();
		
		d_endpointSelect.addSlot(); // by default have 1 endpoint slot.
	}
	
	
	public MutableCharacteristicHolder getCharacteristicModel(BasicStudyCharacteristic c) {
		return new MutableCharacteristicHolder(getNewStudy(),c);
	}

	public ValueModel getCharacteristicNoteModel(BasicStudyCharacteristic c) {
		return new NoteModel(getNewStudy().getCharacteristicWithNotes(c));
	}

	public ObservableList<Arm> getArms() {
		return getNewStudy().getArms();
	}
	
	public ObservableList<Epoch> getEpochs() {
		return getNewStudy().getEpochs();
	}
	
	public SortedSetModel<Drug> getDrugsModel(){
		return d_domain.getDrugs();
	}
	
	public BasicArmPresentation getArmModel(int armNumber){
		return new BasicArmPresentation(getArms().get(armNumber), d_pmf);
	}
	
	public AddArmsPresentation getAddArmsModel() {
		return d_arms;
	}

	public AddEpochsPresentation getAddEpochsModel() {
		return d_epochs;
	}

	public TreatmentActivityPresentation getTreatmentActivityModel(int armNumber){
		Arm arm = getArms().get(armNumber);
		return new TreatmentActivityPresentation(getNewStudy().getTreatment(arm));
	}
	
	public ValueModel getArmNoteModel(int idx) {
		if(getArms().size() <= idx)
			return null;
		return new ArmNoteModel(getArms().get(idx));
	}
	
	@SuppressWarnings("serial")
	static class ArmNoteModel extends AbstractValueModel {
		private final Arm d_arm;

		public ArmNoteModel(Arm arm) {
			d_arm = arm;
		}
		
		public String getValue() {
			return d_arm.getNotes().size() > 0 ? d_arm.getNotes().get(0).getText() : null;
		}

		public void setValue(Object newValue) {
		}
	}
	
	@SuppressWarnings("serial")
	static class NoteModel extends AbstractValueModel {
		private final TypeWithNotes d_obj;

		public NoteModel(TypeWithNotes obj) {
			d_obj = obj;
		}
		
		public String getValue() {
			return (d_obj != null && d_obj.getNotes().size() > 0) ? d_obj.getNotes().get(0).getText() : null;
		}

		public void setValue(Object newValue) {
		}
	}

	public StudyMeasurementTableModel getEndpointMeasurementTableModel() {
		return new StudyMeasurementTableModel(getNewStudy(), d_pmf, Endpoint.class, false);
	}
	
	
	public Study saveStudy() {
		if (getArms().isEmpty()) 
			throw new IllegalStateException("No arms selected in study.");
		Study r = getNewStudy();
		if (Study.extractVariables(r.getEndpoints()).isEmpty()) 
			throw new IllegalStateException("No endpoints selected in study.");
		if (!isIdAvailable())
			throw new IllegalStateException("Study with this ID already exists in domain");

		if (isEditing()) {
			try {
				d_domain.deleteEntity(d_origStudy);
			} catch (DependentEntitiesException e) {
				e.printStackTrace();
			}
		}
		
		// Add the study to the domain.
		d_domain.getStudies().add(getNewStudy());
		
		return getNewStudy();
	}

	public boolean isIdAvailable() {
		if(getNewStudy().getName() == null) return true;
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

	private Study getNewStudy() {
		return d_newStudyPM.getBean();
	}
	
	public StudyPresentation getNewStudyPM() {
		return d_newStudyPM;
	}

	private StudyMeasurementTableModel getAdverseEventMeasurementTableModel() {
		return new StudyMeasurementTableModel(getNewStudy(),d_pmf, AdverseEvent.class, false);
	}
	
	private PopulationCharTableModel getPopulationCharMeasurementTableModel() {
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

	public Study getOldStudy() {
		return d_origStudy;
	}
}