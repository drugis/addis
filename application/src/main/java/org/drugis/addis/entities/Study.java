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

package org.drugis.addis.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.collections15.Transformer;
import org.drugis.addis.entities.StudyActivity.UsedBy;
import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.DateUtil;
import org.drugis.common.EqualsUtil;
import org.drugis.common.beans.ContentAwareListModel;
import org.drugis.common.beans.FilteredObservableList;
import org.drugis.common.beans.FilteredObservableList.Filter;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class Study extends AbstractNamedEntity<Study> implements TypeWithNotes {

	public final static String PROPERTY_INDICATION = "indication";
	public final static String PROPERTY_CHARACTERISTICS = "characteristics";

	public final static String PROPERTY_ENDPOINTS = "endpoints";
	public final static String PROPERTY_ADVERSE_EVENTS = "adverseEvents";
	public final static String PROPERTY_POPULATION_CHARACTERISTICS = "populationCharacteristics";

	public final static String PROPERTY_ARMS = "arms";
	public final static String PROPERTY_EPOCHS = "epochs";
	public final static String PROPERTY_STUDY_ACTIVITIES = "studyActivities";

	private ObjectWithNotes<Indication> d_indication;
	private CharacteristicsMap d_chars = new CharacteristicsMap();

	private final ObservableList<StudyOutcomeMeasure<Endpoint>> d_endpoints = new ArrayListModel<StudyOutcomeMeasure<Endpoint>>();
	private final ObservableList<StudyOutcomeMeasure<AdverseEvent>> d_adverseEvents = new ArrayListModel<StudyOutcomeMeasure<AdverseEvent>>();
	private final ObservableList<StudyOutcomeMeasure<PopulationCharacteristic>> d_populationChars = new ArrayListModel<StudyOutcomeMeasure<PopulationCharacteristic>>();

	private final ObservableList<Arm> d_arms = new ArrayListModel<Arm>();
	private final ObservableList<Epoch> d_epochs = new ArrayListModel<Epoch>();
	private final ObservableList<StudyActivity> d_studyActivities = new ArrayListModel<StudyActivity>();

	private Map<MeasurementKey, BasicMeasurement> d_measurements = new TreeMap<MeasurementKey, BasicMeasurement>();
	private final ObservableList<Note> d_notes = new ArrayListModel<Note>();

	public Study() {
		this(null, null);
	}

	public Study(String id, Indication i) {
		super(id);
		d_indication = new ObjectWithNotes<Indication>(i);
		setCharacteristic(BasicStudyCharacteristic.CREATION_DATE, DateUtil.getCurrentDateWithoutTime());
		setCharacteristic(BasicStudyCharacteristic.TITLE, "");
		setCharacteristic(BasicStudyCharacteristic.PUBMED, new PubMedIdList());

		ListDataListener orphanListener = new ListDataListener() {
			@Override
			public void intervalRemoved(ListDataEvent e) {
				removeOrphanMeasurements();
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				removeOrphanMeasurements();
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				removeOrphanMeasurements();
			}
		};
		d_arms.addListDataListener(orphanListener);
		new ContentAwareListModel<StudyOutcomeMeasure<Endpoint>>(d_endpoints).addListDataListener(orphanListener);
		new ContentAwareListModel<StudyOutcomeMeasure<AdverseEvent>>(d_adverseEvents).addListDataListener(orphanListener);
		new ContentAwareListModel<StudyOutcomeMeasure<PopulationCharacteristic>>(d_populationChars).addListDataListener(orphanListener);
	}

	@Override
	public Study clone() {
		Study newStudy = new Study();
		newStudy.setName(getName());
		replace(newStudy.d_notes, d_notes);
		newStudy.d_indication = d_indication.clone();

		// First clone the basic structure
		replace(newStudy.d_arms, d_arms);
		replace(newStudy.d_epochs, d_epochs);
		replace(newStudy.d_endpoints, cloneStudyOutcomeMeasures(d_endpoints));
		replace(newStudy.d_adverseEvents, cloneStudyOutcomeMeasures(d_adverseEvents));
		replace(newStudy.d_populationChars, cloneStudyOutcomeMeasures(d_populationChars));
		for (StudyActivity sa : d_studyActivities) {
			newStudy.d_studyActivities.add(sa.clone());
		}
		for (MeasurementKey key : d_measurements.keySet()) {
			newStudy.d_measurements.put(key, d_measurements.get(key).clone());
		}
		newStudy.setCharacteristics(cloneCharacteristics());

		// Now clone objects that act as keys
		for (Arm arm : d_arms) {
			newStudy.replaceArm(arm, arm.clone());
		}
		for (Epoch epoch : d_epochs) {
			newStudy.replaceEpoch(epoch, epoch.clone());
		}
		// WhenTakens are already cloned by StudyOutcomeMeasure.clone(), but the old versions are referenced by MeasurementKeys
		for (StudyOutcomeMeasure<?> som : newStudy.getStudyOutcomeMeasures()) {
			for (WhenTaken wt : som.getWhenTaken()) {
				newStudy.updateMeasurementKeys(som, wt, wt);
			}
		}
		
		return newStudy;
	}
	
	/**
	 * Replace oldArm with newArm. All references to oldArm will be updated.
	 * @param oldArm The arm to replace.
	 * @param newArm The new arm.
	 */
	public void replaceArm(final Arm oldArm, final Arm newArm) {
		transformUsedBy(new Transformer<UsedBy, UsedBy>(){
			public UsedBy transform(UsedBy ub) {
				if (ub.getArm().equals(oldArm)) {
					return new UsedBy(newArm, ub.getEpoch());
				}
				return ub;
			}
		});
		
		transformMeasurementKeys(new Transformer<MeasurementKey, MeasurementKey>() {
			public MeasurementKey transform(MeasurementKey key) {
				if (key.getArm() != null && key.getArm().equals(oldArm)) {
					return new MeasurementKey(key.getVariable(), newArm, key.getWhenTaken());
				}
				return key;
			}
		});
		
		d_arms.set(d_arms.indexOf(oldArm), newArm);
	}
	
	/**
	 * Replace oldEpoch with newEpoch. All references to oldEpoch will be updated.
	 * @param oldEpoch The epoch to replace.
	 * @param newEpoch The new epoch.
	 */
	public void replaceEpoch(final Epoch oldEpoch, final Epoch newEpoch) {
		transformUsedBy(new Transformer<UsedBy, UsedBy>(){
			public UsedBy transform(UsedBy ub) {
				if (ub.getEpoch().equals(oldEpoch)) {
					return new UsedBy(ub.getArm(), newEpoch);
				}
				return ub;
			}
		});
		
		for (StudyOutcomeMeasure<?> som : getStudyOutcomeMeasures()) {
			for (int i = 0; i < som.getWhenTaken().size(); ++i) {
				WhenTaken oldWhenTaken = som.getWhenTaken().get(i);
				if (oldWhenTaken.getEpoch().equals(oldEpoch)) {
					WhenTaken newWhenTaken = new WhenTaken(oldWhenTaken.getDuration(), oldWhenTaken.getRelativeTo(), newEpoch);
					newWhenTaken.commit();
					replaceWhenTaken(som, oldWhenTaken, newWhenTaken);
				}
			}
		}
		
		d_epochs.set(d_epochs.indexOf(oldEpoch), newEpoch);
	}
	

	public <V extends Variable> void replaceWhenTaken(final StudyOutcomeMeasure<V> studyOutcomeMeasure, 
			final WhenTaken oldWhenTaken, final WhenTaken newWhenTaken) {
		if (!newWhenTaken.isCommitted()) {
			throw new IllegalArgumentException("The new WhenTaken must be committed");
		}
		updateMeasurementKeys(studyOutcomeMeasure, oldWhenTaken, newWhenTaken);
		
		ObservableList<WhenTaken> whenTakens = studyOutcomeMeasure.getWhenTaken();
		whenTakens.set(whenTakens.indexOf(oldWhenTaken), newWhenTaken);
	}

	private <V extends Variable> void updateMeasurementKeys(final StudyOutcomeMeasure<V> studyOutcomeMeasure,
			final WhenTaken oldWhenTaken, final WhenTaken newWhenTaken) {
		transformMeasurementKeys(new Transformer<MeasurementKey, MeasurementKey>() {
			public MeasurementKey transform(MeasurementKey input) {
				if (input.getVariable().equals(studyOutcomeMeasure.getValue()) && input.getWhenTaken().equals(oldWhenTaken)) {
					return new MeasurementKey(input.getVariable(), input.getArm(), newWhenTaken);
				}
				return input;
			}
		});
	}
	
	private void transformMeasurementKeys(Transformer<MeasurementKey, MeasurementKey> transform) {
		for (MeasurementKey oldKey : new HashSet<MeasurementKey>(d_measurements.keySet())) {
			MeasurementKey newKey = transform.transform(oldKey);
			if (oldKey != newKey) {
				BasicMeasurement measurement = d_measurements.get(oldKey);
				d_measurements.remove(oldKey);
				d_measurements.put(newKey, measurement);
			}
		}
	}

	private void transformUsedBy(Transformer<UsedBy, UsedBy> transformer) {
		for (StudyActivity sa : d_studyActivities) {
			Set<UsedBy> newUsedBys = new HashSet<UsedBy>();
			for (UsedBy oldUsedBy : sa.getUsedBy()) {
				newUsedBys.add(transformer.transform(oldUsedBy));
			}
			sa.setUsedBy(newUsedBys);
		}
	}


	private static <T> void replace(ObservableList<T> target, Collection<T> newValues) {
		target.clear();
		target.addAll(newValues);
	}

	private <T extends Variable> List<StudyOutcomeMeasure<T>> cloneStudyOutcomeMeasures(List<StudyOutcomeMeasure<T>> soms) {
		List<StudyOutcomeMeasure<T>> list = new ArrayList<StudyOutcomeMeasure<T>>();
		for (StudyOutcomeMeasure<T> som : soms) {
			list.add(som.clone());
		}
		return list;
	}

	private CharacteristicsMap cloneCharacteristics() {
		CharacteristicsMap cm = new CharacteristicsMap();
		for (Characteristic c : d_chars.keySet()) {
			cm.put(c, d_chars.get(c).clone());
		}
		return cm;
	}

	public ObservableList<Arm> getArms() {
		return d_arms;
	}

	public ObservableList<Epoch> getEpochs() {
		return d_epochs;
	}

	public ObservableList<StudyActivity> getStudyActivities() {
		return d_studyActivities;
	}

	/**
	 * Set a particular studyActivity as being used by an (arm, epoch) pair.
	 * Constraint: At most one StudyActivity exists for each (arm, epoch) pair;
	 * any previous entry will be overwritten.
	 * 
	 * @param arm
	 * @param epoch
	 * @param activity
	 *            A StudyActivity or null; when null, clears any activity at
	 *            that (arm, epoch) pair.
	 */
	public void setStudyActivityAt(Arm arm, Epoch epoch, StudyActivity activity) {
		assertContains(d_arms, arm);
		assertContains(d_epochs, epoch);

		if (activity == null) {
			clearStudyActivityAt(arm, epoch);
		} else {
			assertContains(d_studyActivities, activity);
			activity = d_studyActivities.get(d_studyActivities.indexOf(activity)); // ensure we have the *same* object, not just an *equal* one.
			clearStudyActivityAt(arm, epoch);
			Set<UsedBy> usedBy = new HashSet<UsedBy>(activity.getUsedBy());
			usedBy.add(new UsedBy(arm, epoch));
			activity.setUsedBy(usedBy);
		}
	}

	public StudyActivity getStudyActivityAt(Arm arm, Epoch epoch) {
		UsedBy coordinate = new UsedBy(arm, epoch);
		for (StudyActivity activity : d_studyActivities) {
			if (activity.getUsedBy().contains(coordinate)) {
				return activity;
			}
		}
		return null;
	}

	private <E> void assertContains(ObservableList<E> list, E item) {
		if (!list.contains(item)) {
			throw new IllegalArgumentException("The "
					+ item.getClass().getSimpleName() + " <" + item
					+ " > does not exist in this study");
		}
	}

	private void clearStudyActivityAt(Arm arm, Epoch epoch) {
		UsedBy coordinate = new UsedBy(arm, epoch);
		for (StudyActivity activity : d_studyActivities) {
			if (activity.getUsedBy().contains(coordinate)) {
				Set<UsedBy> usedBy = new HashSet<UsedBy>(activity.getUsedBy());
				usedBy.remove(coordinate);
				activity.setUsedBy(usedBy);
			}
		}
	}

	public Set<DrugSet> getDrugs() {
		Set<DrugSet> drugs = new HashSet<DrugSet>();
		for (Arm a : getArms()) {
			drugs.add(getDrugs(a));
		}
		return drugs;
	}

	public Indication getIndication() {
		return d_indication.getValue();
	}

	public void setIndication(Indication indication) {
		Indication oldInd = d_indication.getValue();
		d_indication.setValue(indication);
		firePropertyChange(PROPERTY_INDICATION, oldInd, indication);
	}

	@Override
	public Set<Entity> getDependencies() {
		HashSet<Entity> dep = EntityUtil.flatten(getDrugs());
		dep.addAll(getOutcomeMeasures());
		dep.addAll(extractVariables(getPopulationChars()));
		dep.add(d_indication.getValue());
		for (StudyActivity sa : getStudyActivities()) {
			if (sa.getActivity() instanceof TreatmentActivity) {
				TreatmentActivity ta = (TreatmentActivity) sa.getActivity();
				for (AbstractDose d : ta.getDoses()) {
					if (d != null) {
						dep.add(d.getDoseUnit().getUnit());
					}
				}
			}
		}
		return dep;
	}

	public Object getCharacteristic(Characteristic c) {
		return d_chars.get(c) != null ? d_chars.get(c).getValue() : null;
	}

	public void setCharacteristic(BasicStudyCharacteristic c, Object val) {
		ObjectWithNotes<?> charVal = getCharacteristicWithNotes(c);
		if (charVal != null) {
			charVal.setValue(val);
			setCharacteristicWithNotes(c, charVal); // FIXME: this is a hack because d_chars is exposed & also firing events.
		} else {
			setCharacteristicWithNotes(c, new ObjectWithNotes<Object>(val));
		}
	}

	public ObjectWithNotes<?> getCharacteristicWithNotes(Characteristic c) {
		return d_chars.get(c);
	}

	public void setCharacteristicWithNotes(BasicStudyCharacteristic c, ObjectWithNotes<?> val) {
		d_chars.put(c, val);
		firePropertyChange(PROPERTY_CHARACTERISTICS, c, c);
	}

	public void setCharacteristics(CharacteristicsMap m) {
		d_chars = m;
	}

	public CharacteristicsMap getCharacteristics() {
		return d_chars;
	}

	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Study) {
			Study other = (Study) o;
			if (other.getName() == null) {
				return getName() == null;
			}
			return other.getName().equals(getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getName() == null ? 0 : getName().hashCode();
	}

	@Override
	public int compareTo(Study other) {
		return getName().compareTo(other.getName());
	}

	public BasicMeasurement getMeasurement(Variable v, Arm a, WhenTaken wt) {
		MeasurementKey key = new MeasurementKey(v, a, wt);
		BasicMeasurement basicMeasurement = d_measurements.get(key);
		return basicMeasurement;
	}

	public BasicMeasurement getMeasurement(Variable v, Arm a) {
		WhenTaken mm = defaultMeasurementMoment();
		return mm == null ? null : d_measurements.get(new MeasurementKey(v, a, mm));
	}

	public BasicMeasurement getMeasurement(Variable v) {
		return getMeasurement(v, null);
	}

	public Object getMeasurement(StudyOutcomeMeasure<AdverseEvent> dV, Arm dA) {
		return getMeasurement(dV.getValue(), dA);
	}

	private void forceLegalArguments(OutcomeMeasure e, Arm a, Measurement m) {
		if (!getArms().contains(a)) {
			throw new IllegalArgumentException("Arm " + a
					+ " not part of this study.");
		}
		if (!getOutcomeMeasures().contains(e)) {
			throw new IllegalArgumentException("Outcome " + e
					+ " not measured by this study.");
		}
		if (m != null && !m.isOfType(e.getVariableType())) {
			throw new IllegalArgumentException(
			"Measurement does not conform with outcome");
		}
	}

	public void setMeasurement(OutcomeMeasure e, Arm a, BasicMeasurement m) {
		forceLegalArguments(e, a, m);
		d_measurements.put(new MeasurementKey(e, a, defaultMeasurementMoment()), m);
	}

	/**
	 * Set population characteristic measurement on arm.
	 * 
	 * @param v
	 * @param a
	 * @param m
	 */
	public void setMeasurement(Variable v, Arm a, BasicMeasurement m) {
		forceLegalArguments(v, a, m);
		d_measurements.put(new MeasurementKey(v, a, defaultMeasurementMoment()), m);
	}

	/**
	 * Set population characteristic measurement on study.
	 * 
	 * @param v
	 * @param m
	 */
	public void setMeasurement(Variable v, BasicMeasurement m) {
		forceLegalArguments(v, null, m);
		setMeasurement(v, null, m);
	}

	private void forceLegalArguments(Variable v, Arm a, Measurement m) {
		if (!extractVariables(getPopulationChars()).contains(v)) {
			throw new IllegalArgumentException("Variable " + v
					+ " not in study");
		}
		if (a != null && !d_arms.contains(a)) {
			throw new IllegalArgumentException("Arm " + a + " not in study");
		}
		if (!m.isOfType(v.getVariableType())) {
			throw new IllegalArgumentException(
			"Measurement does not conform with outcome");
		}
		if (findTreatmentEpoch() == null) {
			throw new IllegalStateException("Attempting to add measurement before treatment epoch is defined.");
		}
	}

	public List<OutcomeMeasure> getOutcomeMeasures() {
		List<OutcomeMeasure> sortedList = new ArrayList<OutcomeMeasure>(extractVariables(getEndpoints()));
		sortedList.addAll(extractVariables(getAdverseEvents()));
		Collections.sort(sortedList);
		return sortedList;
	}

	public static <T extends Variable> List<T> extractVariables(List<StudyOutcomeMeasure<T>> soms) {
		List<T> vars = new ArrayList<T>();
		for (StudyOutcomeMeasure<T> som : soms) {
			vars.add(som.getValue());
		}
		return vars;
	}

	public List<? extends Variable> getVariables(Class<? extends Variable> type) {
		return extractVariables(getStudyOutcomeMeasures(type));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends Variable> ObservableList<StudyOutcomeMeasure<T>> getStudyOutcomeMeasures(Class<T> type) {
		if (type == Endpoint.class) {
			return (ObservableList) getEndpoints();
		} else if (type == AdverseEvent.class) {
			return (ObservableList) getAdverseEvents();
		} else if (type == PopulationCharacteristic.class) {
			return (ObservableList) getPopulationChars();
		}
		throw new IllegalArgumentException("Unknown variable type " + type.getSimpleName());
	}

	private List<StudyOutcomeMeasure<?>> getStudyOutcomeMeasures() {
		List<StudyOutcomeMeasure<?>> l = new ArrayList<StudyOutcomeMeasure<?>>();
		l.addAll(getAdverseEvents());
		l.addAll(getEndpoints());
		l.addAll(getPopulationChars());
		return l;
	}



	public void addVariable(Variable om) {
		addVariable(om, null);
	}


	public void addVariable(Variable om, WhenTaken wt) {
		if (om instanceof Endpoint) {
			getEndpoints().add(new StudyOutcomeMeasure<Endpoint>(((Endpoint) om), wt));
		} else if (om instanceof AdverseEvent) {
			getAdverseEvents().add(new StudyOutcomeMeasure<AdverseEvent>(((AdverseEvent) om), wt));
		} else if (om instanceof PopulationCharacteristic) {
			getPopulationChars().add(new StudyOutcomeMeasure<PopulationCharacteristic>(((PopulationCharacteristic) om), wt));
		} else {
			throw new IllegalStateException("Illegal OutcomeMeasure type " + om.getClass());
		}
	}

	private void removeOrphanMeasurements() {
		for (MeasurementKey k : new HashSet<MeasurementKey>(d_measurements.keySet())) {
			if (orphanKey(k)) {
				d_measurements.remove(k);
			}
		}
	}

	public BasicMeasurement buildDefaultMeasurement(Variable v, Arm a) {
		return v.buildMeasurement(a == null ? getSampleSize() : a.getSize());
	}

	private boolean orphanKey(MeasurementKey k) {
		StudyOutcomeMeasure<Variable> som = findStudyOutcomeMeasure(k.getVariable());
		if (som == null) {
			return true;
		}
		if (!som.getWhenTaken().contains(k.getWhenTaken())) {
			return true;
		}
		// OutcomeMeasure measurement
		if (k.getVariable() instanceof OutcomeMeasure) {
			if (!d_arms.contains(k.getArm())) {
				return true;
			}
			return false;
		}
		// PopulationChar measurements
		if (k.getVariable() instanceof PopulationCharacteristic) {
			if (k.getArm() != null && !d_arms.contains(k.getArm())) {
				return true;
			}
			return false;
		}

		throw new IllegalStateException(k + " is not a valid measurement key");
	}

	public int getSampleSize() {
		int s = 0;
		for (Arm pg : d_arms) {
			s += pg.getSize();
		}
		return s;
	}

	public Map<MeasurementKey, BasicMeasurement> getMeasurements() {
		return d_measurements;
	}

	public void setMeasurement(MeasurementKey key, BasicMeasurement value) {
		d_measurements.put(key, value);
	}

	public ObjectWithNotes<?> getIndicationWithNotes() {
		return d_indication;
	}

	@SuppressWarnings("unchecked")
	public void addStudyOutcomeMeasure(StudyOutcomeMeasure<?> value) {
		if (value.getValue() instanceof Endpoint) {
			getEndpoints().add((StudyOutcomeMeasure<Endpoint>) value);
		} else if (value.getValue() instanceof AdverseEvent) {
			getAdverseEvents().add((StudyOutcomeMeasure<AdverseEvent>) value);
		} else if (value.getValue() instanceof PopulationCharacteristic) {
			getPopulationChars().add(
					(StudyOutcomeMeasure<PopulationCharacteristic>) value);
		} else {
			throw new IllegalArgumentException("Unknown StudyOutcomeMeasure type: " + value.getValue());
		}
	}

	public TreatmentActivity getTreatment(Arm arm) {
		return getActivity(arm) instanceof TreatmentActivity ? (TreatmentActivity) getActivity(arm) : null;
	}

	public Activity getActivity(Arm arm) {
		assertContains(d_arms, arm);
		if (d_epochs.isEmpty()) {
			return null;
		}
		Epoch epoch = findTreatmentEpoch();
		if (epoch == null) {
			return null;
		}
		return getStudyActivityAt(arm, epoch).getActivity();
	}

	public Epoch findTreatmentEpoch() {
		for (Epoch epoch : d_epochs) {
			if (isTreatmentEpoch(epoch)) {
				return epoch;
			}
		}
		return null;
	}

	public WhenTaken defaultMeasurementMoment() {
		return treatmentWhenTaken(RelativeTo.BEFORE_EPOCH_END);
	}
	
	public WhenTaken baselineMeasurementMoment() {
		return treatmentWhenTaken(RelativeTo.FROM_EPOCH_START);
	}

	private WhenTaken treatmentWhenTaken(RelativeTo relativeTo) {
		Epoch epoch = findTreatmentEpoch();
		if (epoch == null) {
			return null;
		}
		WhenTaken whenTaken = new WhenTaken(EntityUtil.createDuration("P0D"), relativeTo, epoch);
		whenTaken.commit();
		return whenTaken;
	}


	private boolean isTreatmentEpoch(Epoch epoch) {
		for (Arm arm : d_arms) {
			StudyActivity sa = getStudyActivityAt(arm, epoch);
			if (sa == null || !(sa.getActivity() instanceof TreatmentActivity)) {
				return false;
			}
		}
		return true;
	}

	public Epoch findEpochWithActivity(Activity a) {
		for (Epoch epoch : d_epochs) {
			if (isActivityEpoch(epoch, a)) {
				return epoch;
			}
		}
		return null;
	}

	private boolean isActivityEpoch(Epoch epoch, Activity a) {
		for (Arm arm : d_arms) {
			StudyActivity sa = getStudyActivityAt(arm, epoch);
			if (sa == null || !(sa.getActivity().equals(a))) {
				return false;
			}
		}
		return true;
	}

	public DrugSet getDrugs(Arm a) {
		Activity activity = getActivity(a);
		if (activity instanceof TreatmentActivity) {
			return new DrugSet(((TreatmentActivity) activity).getDrugs());
		}
		return new DrugSet();
	}

	@Override
	public boolean deepEquals(Entity obj) {
		if (!equals(obj)) {
			return false;
		}
		Study other = (Study) obj;
		return EntityUtil.deepEqual(other.getIndication(), getIndication())
		&& EntityUtil.deepEqual(other.getCharacteristics(), getCharacteristics())
		&& EntityUtil.deepEqual(Study.extractVariables(other.getEndpoints()), extractVariables(getEndpoints()))
		&& EntityUtil.deepEqual(Study.extractVariables(other.getAdverseEvents()), extractVariables(getAdverseEvents()))
		&& EntityUtil.deepEqual(Study.extractVariables(other.getPopulationChars()), extractVariables(getPopulationChars()))
		&& EntityUtil.deepEqual(other.getArms(), getArms())
		&& EntityUtil.deepEqual(other.getEpochs(), getEpochs())
		&& EntityUtil.deepEqual(other.getStudyActivities(), getStudyActivities())
		&& EntityUtil.deepEqual(other.getMeasurements(), getMeasurements())
		&& EqualsUtil.equal(other.getNotes(), getNotes());
	}

	public Arm findArm(String armName) {
		for (Arm a : d_arms) {
			if (a.getName().equals(armName)) {
				return a;
			}
		}
		return null;
	}

	public Epoch findEpoch(String epochName) {
		for (Epoch e : d_epochs) {
			if (e.getName().equals(epochName)) {
				return e;
			}
		}
		return null;
	}

	public StudyActivity findStudyActivity(String activityName) {
		for (StudyActivity sa : d_studyActivities) {
			if (sa.getName().equals(activityName)) {
				return sa;
			}
		}
		return null;
	}

	/**
	 * Creates an Arm, adds it to the Study and creates an appropriate
	 * TreatmentActivity in the last Epoch.
	 * 
	 * @param name
	 *            Name of the arm to be created.
	 * @param size
	 *            Number of subjects in the arm to be created.
	 * @param drug
	 *            The drug administered.
	 * @param dose
	 *            The dose administered.
	 * @return The created arm, already added and embedded in the study
	 *         structure.
	 */
	public Arm createAndAddArm(String name, Integer size, Drug drug, AbstractDose dose) {
		Arm arm = new Arm(name, size);
		getArms().add(arm);
		StudyActivity studyActivity = new StudyActivity(name + " treatment",
				new TreatmentActivity(new DrugTreatment(drug, dose)));
		getStudyActivities().add(studyActivity);
		Epoch epoch = getEpochs().get(getEpochs().size() - 1);
		this.setStudyActivityAt(arm, epoch, studyActivity);
		return arm;
	}

	/**
	 * @param wt TODO
	 * @return The Drugs that have at least one Arm with a complete measurement
	 *         for the Variable v.
	 */
	public Set<DrugSet> getMeasuredDrugs(Variable v, WhenTaken wt) {
		Set<DrugSet> drugs = new HashSet<DrugSet>();
		for (DrugSet d : getDrugs()) {
			if (isMeasured(v, d, wt)) {
				drugs.add(d);
			}
		}
		return drugs;
	}

	public Set<DrugSet> getMeasuredDrugs(Variable v) {
		return getMeasuredDrugs(v, defaultMeasurementMoment());
	}

	public ObservableList<Arm> getMeasuredArms(Variable v, DrugSet d) {
		return getMeasuredArms(v, d, defaultMeasurementMoment());
	}

	public ObservableList<Arm> getMeasuredArms(Variable v, DrugSet d, WhenTaken wt) {
		return new FilteredObservableList<Arm>(getArms(d), new IsMeasuredFilter(v, wt));
	}

	private boolean isMeasured(Variable v, DrugSet d, WhenTaken wt) {
		for (Arm a : getArms(d)) {
			if (isMeasured(v, a, wt)) {
				return true;
			}
		}
		return false;
	}

	public boolean isMeasured(Variable v, Arm a, WhenTaken wt) {
		return getMeasurement(v, a, wt) != null	&& getMeasurement(v, a, wt).isComplete();
	}

	private ObservableList<Arm> getArms(DrugSet d) {
		return new FilteredObservableList<Arm>(d_arms, new DrugArmFilter(d));
	}

	public ObservableList<StudyOutcomeMeasure<Endpoint>> getEndpoints() {
		return d_endpoints;
	}

	public ObservableList<StudyOutcomeMeasure<AdverseEvent>> getAdverseEvents() {
		return d_adverseEvents;
	}

	public ObservableList<StudyOutcomeMeasure<PopulationCharacteristic>> getPopulationChars() {
		return d_populationChars;
	}

	public static <T extends Variable> List<StudyOutcomeMeasure<T>> wrapVariables(List<T> vars) {
		List<StudyOutcomeMeasure<T>> soms = new ArrayList<StudyOutcomeMeasure<T>>();
		for (T v : vars) {
			soms.add(wrapVariable(v));
		}
		return soms;
	}

	public class IsMeasuredFilter implements Filter<Arm> {
		private final Variable d_v;
		private final WhenTaken d_wt;

		public IsMeasuredFilter(Variable v, WhenTaken wt) {
			d_v = v;
			d_wt = wt;
		}

		public boolean accept(Arm a) {
			return isMeasured(d_v, a, d_wt);
		}
	}

	public class DrugArmFilter implements Filter<Arm> {
		private final DrugSet d_d;

		public DrugArmFilter(DrugSet d) {
			d_d = d;
		}

		public boolean accept(Arm a) {
			return getDrugs(a).equals(d_d);
		}
	}

	@Override
	public ObservableList<Note> getNotes() {
		return d_notes;
	}

	public static <T extends Variable> StudyOutcomeMeasure<T> wrapVariable(T om) {
		return new StudyOutcomeMeasure<T>(om);
	}

	@SuppressWarnings("unchecked")
	public <T extends Variable> StudyOutcomeMeasure<T> findStudyOutcomeMeasure(T v) {
		ObservableList<StudyOutcomeMeasure<T>> soms = getStudyOutcomeMeasures((Class<T>)v.getClass());
		for (StudyOutcomeMeasure<T> som : soms) {
			if (som.getValue().equals(v)) {
				return som;
			}
		}
		return null;
	}
}
