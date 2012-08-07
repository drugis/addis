/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
import org.drugis.addis.entities.treatment.TreatmentDefinition;
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

	private final ObservableList<StudyOutcomeMeasure<? extends Variable>> d_outcomeMeasures = new ArrayListModel<StudyOutcomeMeasure<? extends Variable>>();
	private final ObservableList<StudyOutcomeMeasure<Endpoint>> d_endpoints;
	private final ObservableList<StudyOutcomeMeasure<AdverseEvent>> d_adverseEvents;
	private final ObservableList<StudyOutcomeMeasure<PopulationCharacteristic>> d_populationChars;

	private final ObservableList<Arm> d_arms = new ArrayListModel<Arm>();
	private final ObservableList<Epoch> d_epochs = new ArrayListModel<Epoch>();
	private final ObservableList<StudyActivity> d_studyActivities = new ArrayListModel<StudyActivity>();

	private final Map<MeasurementKey, BasicMeasurement> d_measurements = new TreeMap<MeasurementKey, BasicMeasurement>();
	private final ObservableList<Note> d_notes = new ArrayListModel<Note>();

	public Study() {
		this(null, null);
	}

	public Study(final String id, final Indication i) {
		super(id);
		d_indication = new ObjectWithNotes<Indication>(i);
		setCharacteristic(BasicStudyCharacteristic.CREATION_DATE, DateUtil.getCurrentDateWithoutTime());
		setCharacteristic(BasicStudyCharacteristic.TITLE, "");
		setCharacteristic(BasicStudyCharacteristic.PUBMED, new PubMedIdList());

		final ListDataListener orphanListener = new ListDataListener() {
			@Override
			public void intervalRemoved(final ListDataEvent e) {
				removeOrphanMeasurements();
			}

			@Override
			public void intervalAdded(final ListDataEvent e) {
				removeOrphanMeasurements();
			}

			@Override
			public void contentsChanged(final ListDataEvent e) {
				removeOrphanMeasurements();
			}
		};
		d_arms.addListDataListener(orphanListener);
		new ContentAwareListModel<StudyOutcomeMeasure<?>>(d_outcomeMeasures).addListDataListener(orphanListener);

		d_endpoints = convert(Endpoint.class, d_outcomeMeasures);
		d_adverseEvents = convert(AdverseEvent.class, d_outcomeMeasures);
		d_populationChars = convert(PopulationCharacteristic.class, d_outcomeMeasures);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T extends Variable> ObservableList<StudyOutcomeMeasure<T>> convert(final Class<T> cls, final ObservableList<StudyOutcomeMeasure<? extends Variable>> list) {
		return (ObservableList) new FilteredObservableList<StudyOutcomeMeasure<? extends Variable>>(list, new Filter<StudyOutcomeMeasure<? extends Variable>>() {
			@Override
			public boolean accept(final StudyOutcomeMeasure<? extends Variable> obj) {
				return cls.equals(obj.getValueClass());
			}
		});
	}

	@Override
	public Study clone() {
		final Study newStudy = new Study();
		newStudy.setName(getName());
		replace(newStudy.d_notes, d_notes);
		newStudy.d_indication = d_indication.clone();

		// First clone the basic structure
		replace(newStudy.d_arms, d_arms);
		replace(newStudy.d_epochs, d_epochs);
		replace(newStudy.d_outcomeMeasures, cloneStudyOutcomeMeasures(d_outcomeMeasures));
		for (final StudyActivity sa : d_studyActivities) {
			newStudy.d_studyActivities.add(sa.clone());
		}
		for (final MeasurementKey key : d_measurements.keySet()) {
			newStudy.d_measurements.put(key, d_measurements.get(key).clone());
		}
		newStudy.setCharacteristics(cloneCharacteristics());

		// Now clone objects that act as keys
		for (final Arm arm : d_arms) {
			newStudy.replaceArm(arm, arm.clone());
		}
		for (final Epoch epoch : d_epochs) {
			newStudy.replaceEpoch(epoch, epoch.clone());
		}
		// WhenTakens are already cloned by StudyOutcomeMeasure.clone(), but the old versions are referenced by MeasurementKeys
		for (final StudyOutcomeMeasure<?> som : newStudy.getStudyOutcomeMeasures()) {
			for (final WhenTaken wt : som.getWhenTaken()) {
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
			@Override
			public UsedBy transform(final UsedBy ub) {
				if (ub.getArm().equals(oldArm)) {
					return new UsedBy(newArm, ub.getEpoch());
				}
				return ub;
			}
		});

		transformMeasurementKeys(new Transformer<MeasurementKey, MeasurementKey>() {
			@Override
			public MeasurementKey transform(final MeasurementKey key) {
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
			@Override
			public UsedBy transform(final UsedBy ub) {
				if (ub.getEpoch().equals(oldEpoch)) {
					return new UsedBy(ub.getArm(), newEpoch);
				}
				return ub;
			}
		});

		for (final StudyOutcomeMeasure<?> som : getStudyOutcomeMeasures()) {
			for (int i = 0; i < som.getWhenTaken().size(); ++i) {
				final WhenTaken oldWhenTaken = som.getWhenTaken().get(i);
				if (oldWhenTaken.getEpoch().equals(oldEpoch)) {
					final WhenTaken newWhenTaken = new WhenTaken(oldWhenTaken.getDuration(), oldWhenTaken.getRelativeTo(), newEpoch);
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

		final ObservableList<WhenTaken> whenTakens = studyOutcomeMeasure.getWhenTaken();
		whenTakens.set(whenTakens.indexOf(oldWhenTaken), newWhenTaken);
	}

	private <V extends Variable> void updateMeasurementKeys(final StudyOutcomeMeasure<V> studyOutcomeMeasure,
			final WhenTaken oldWhenTaken, final WhenTaken newWhenTaken) {
		transformMeasurementKeys(new Transformer<MeasurementKey, MeasurementKey>() {
			@Override
			public MeasurementKey transform(final MeasurementKey input) {
				if (input.getVariable().equals(studyOutcomeMeasure.getValue()) && input.getWhenTaken().equals(oldWhenTaken)) {
					return new MeasurementKey(input.getVariable(), input.getArm(), newWhenTaken);
				}
				return input;
			}
		});
	}

	private void transformMeasurementKeys(final Transformer<MeasurementKey, MeasurementKey> transform) {
		for (final MeasurementKey oldKey : new HashSet<MeasurementKey>(d_measurements.keySet())) {
			final MeasurementKey newKey = transform.transform(oldKey);
			if (oldKey != newKey) {
				final BasicMeasurement measurement = d_measurements.get(oldKey);
				d_measurements.remove(oldKey);
				d_measurements.put(newKey, measurement);
			}
		}
	}

	private void transformUsedBy(final Transformer<UsedBy, UsedBy> transformer) {
		for (final StudyActivity sa : d_studyActivities) {
			final Set<UsedBy> newUsedBys = new HashSet<UsedBy>();
			for (final UsedBy oldUsedBy : sa.getUsedBy()) {
				newUsedBys.add(transformer.transform(oldUsedBy));
			}
			sa.setUsedBy(newUsedBys);
		}
	}


	private static <T> void replace(final ObservableList<T> target, final Collection<T> newValues) {
		target.clear();
		target.addAll(newValues);
	}

	private <T extends Variable> List<StudyOutcomeMeasure<? extends Variable>> cloneStudyOutcomeMeasures(final List<StudyOutcomeMeasure<? extends Variable>> soms) {
		final List<StudyOutcomeMeasure<? extends Variable>> list = new ArrayList<StudyOutcomeMeasure<? extends Variable>>();
		for (final StudyOutcomeMeasure<? extends Variable> som : soms) {
			list.add(som.clone());
		}
		return list;
	}

	private CharacteristicsMap cloneCharacteristics() {
		final CharacteristicsMap cm = new CharacteristicsMap();
		for (final Characteristic c : d_chars.keySet()) {
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
	public void setStudyActivityAt(final Arm arm, final Epoch epoch, StudyActivity activity) {
		assertContains(d_arms, arm);
		assertContains(d_epochs, epoch);

		if (activity == null) {
			clearStudyActivityAt(arm, epoch);
		} else {
			assertContains(d_studyActivities, activity);
			activity = d_studyActivities.get(d_studyActivities.indexOf(activity)); // ensure we have the *same* object, not just an *equal* one.
			clearStudyActivityAt(arm, epoch);
			final Set<UsedBy> usedBy = new HashSet<UsedBy>(activity.getUsedBy());
			usedBy.add(new UsedBy(arm, epoch));
			activity.setUsedBy(usedBy);
		}
	}

	public StudyActivity getStudyActivityAt(final Arm arm, final Epoch epoch) {
		final UsedBy coordinate = new UsedBy(arm, epoch);
		for (final StudyActivity activity : d_studyActivities) {
			if (activity.getUsedBy().contains(coordinate)) {
				return activity;
			}
		}
		return null;
	}

	private <E> void assertContains(final ObservableList<E> list, final E item) {
		if (!list.contains(item)) {
			throw new IllegalArgumentException("The "
					+ item.getClass().getSimpleName() + " <" + item
					+ " > does not exist in this study");
		}
	}

	private void clearStudyActivityAt(final Arm arm, final Epoch epoch) {
		final UsedBy coordinate = new UsedBy(arm, epoch);
		for (final StudyActivity activity : d_studyActivities) {
			if (activity.getUsedBy().contains(coordinate)) {
				final Set<UsedBy> usedBy = new HashSet<UsedBy>(activity.getUsedBy());
				usedBy.remove(coordinate);
				activity.setUsedBy(usedBy);
			}
		}
	}

	public Set<TreatmentDefinition> getTreatmentDefinition() {
		final Set<TreatmentDefinition> drugs = new HashSet<TreatmentDefinition>();
		for (final Arm a : getArms()) {
			drugs.add(getTreatmentDefinition(a));
		}
		return drugs;
	}

	public Indication getIndication() {
		return d_indication.getValue();
	}

	public void setIndication(final Indication indication) {
		final Indication oldInd = d_indication.getValue();
		d_indication.setValue(indication);
		firePropertyChange(PROPERTY_INDICATION, oldInd, indication);
	}

	@Override
	public Set<Entity> getDependencies() {
		final Set<Entity> dep = new HashSet<Entity>();
		dep.addAll(getOutcomeMeasures());
		dep.addAll(extractVariables(getPopulationChars()));
		dep.add(d_indication.getValue());
		for (final StudyActivity sa : getStudyActivities()) {
			dep.addAll(sa.getDependencies());
		}
		return dep;
	}

	public Object getCharacteristic(final Characteristic c) {
		return d_chars.get(c) != null ? d_chars.get(c).getValue() : null;
	}

	public void setCharacteristic(final BasicStudyCharacteristic c, final Object val) {
		final ObjectWithNotes<?> charVal = getCharacteristicWithNotes(c);
		if (charVal != null) {
			charVal.setValue(val);
			setCharacteristicWithNotes(c, charVal); // FIXME: this is a hack because d_chars is exposed & also firing events.
		} else {
			setCharacteristicWithNotes(c, new ObjectWithNotes<Object>(val));
		}
	}

	public ObjectWithNotes<?> getCharacteristicWithNotes(final Characteristic c) {
		return d_chars.get(c);
	}

	public void setCharacteristicWithNotes(final BasicStudyCharacteristic c, final ObjectWithNotes<?> val) {
		d_chars.put(c, val);
		firePropertyChange(PROPERTY_CHARACTERISTICS, c, c);
	}

	public void setCharacteristics(final CharacteristicsMap m) {
		d_chars = m;
	}

	public CharacteristicsMap getCharacteristics() {
		return d_chars;
	}

	public void setName(final String name) {
		final String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof Study) {
			final Study other = (Study) o;
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
	public int compareTo(final Study other) {
		return getName().compareTo(other.getName());
	}

	public BasicMeasurement getMeasurement(final Variable v, final Arm a, final WhenTaken wt) {
		final MeasurementKey key = new MeasurementKey(v, a, wt);
		final BasicMeasurement basicMeasurement = d_measurements.get(key);
		return basicMeasurement;
	}

	public BasicMeasurement getMeasurement(final Variable v, final Arm a) {
		final WhenTaken mm = defaultMeasurementMoment();
		return mm == null ? null : d_measurements.get(new MeasurementKey(v, a, mm));
	}

	public BasicMeasurement getMeasurement(final Variable v) {
		return getMeasurement(v, null);
	}

	public Object getMeasurement(final StudyOutcomeMeasure<AdverseEvent> dV, final Arm dA) {
		return getMeasurement(dV.getValue(), dA);
	}

	private void forceLegalArguments(final OutcomeMeasure e, final Arm a, final Measurement m) {
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

	public void setMeasurement(final OutcomeMeasure e, final Arm a, final BasicMeasurement m) {
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
	public void setMeasurement(final Variable v, final Arm a, final BasicMeasurement m) {
		forceLegalArguments(v, a, m);
		d_measurements.put(new MeasurementKey(v, a, defaultMeasurementMoment()), m);
	}

	/**
	 * Set population characteristic measurement on study.
	 *
	 * @param v
	 * @param m
	 */
	public void setMeasurement(final Variable v, final BasicMeasurement m) {
		forceLegalArguments(v, null, m);
		setMeasurement(v, null, m);
	}

	private void forceLegalArguments(final Variable v, final Arm a, final Measurement m) {
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
		final List<OutcomeMeasure> sortedList = new ArrayList<OutcomeMeasure>(extractVariables(getEndpoints()));
		sortedList.addAll(extractVariables(getAdverseEvents()));
		Collections.sort(sortedList);
		return sortedList;
	}

	public static <T extends Variable> List<T> extractVariables(final List<StudyOutcomeMeasure<T>> soms) {
		final List<T> vars = new ArrayList<T>();
		for (final StudyOutcomeMeasure<T> som : soms) {
			vars.add(som.getValue());
		}
		return vars;
	}

	public List<? extends Variable> getVariables(final Class<? extends Variable> type) {
		return extractVariables(getStudyOutcomeMeasures(type));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends Variable> ObservableList<StudyOutcomeMeasure<T>> getStudyOutcomeMeasures(final Class<T> type) {
		if (type == Endpoint.class) {
			return (ObservableList) getEndpoints();
		} else if (type == AdverseEvent.class) {
			return (ObservableList) getAdverseEvents();
		} else if (type == PopulationCharacteristic.class) {
			return (ObservableList) getPopulationChars();
		}
		throw new IllegalArgumentException("Unknown variable type " + type.getSimpleName());
	}

	public ObservableList<StudyOutcomeMeasure<?>> getStudyOutcomeMeasures() {
		return d_outcomeMeasures;
	}

	public void addVariable(final Variable om) {
		addVariable(om, null);
	}


	public void addVariable(final Variable om, final WhenTaken wt) {
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
		for (final MeasurementKey k : new HashSet<MeasurementKey>(d_measurements.keySet())) {
			if (orphanKey(k)) {
				d_measurements.remove(k);
			}
		}
	}

	public BasicMeasurement buildDefaultMeasurement(final Variable v, final Arm a) {
		return v.buildMeasurement(a == null ? getSampleSize() : a.getSize());
	}

	private boolean orphanKey(final MeasurementKey k) {
		final StudyOutcomeMeasure<Variable> som = findStudyOutcomeMeasure(k.getVariable());
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
		for (final Arm pg : d_arms) {
			s += pg.getSize();
		}
		return s;
	}

	public Map<MeasurementKey, BasicMeasurement> getMeasurements() {
		return d_measurements;
	}

	public void setMeasurement(final MeasurementKey key, final BasicMeasurement value) {
		d_measurements.put(key, value);
	}

	public ObjectWithNotes<?> getIndicationWithNotes() {
		return d_indication;
	}

	public void addStudyOutcomeMeasure(final StudyOutcomeMeasure<?> value) {
		d_outcomeMeasures.add(value);
	}

	public TreatmentActivity getTreatment(final Arm arm) {
		return getActivity(arm) instanceof TreatmentActivity ? (TreatmentActivity) getActivity(arm) : null;
	}

	public Activity getActivity(final Arm arm) {
		assertContains(d_arms, arm);
		if (d_epochs.isEmpty()) {
			return null;
		}
		final Epoch epoch = findTreatmentEpoch();
		if (epoch == null) {
			return null;
		}
		return getStudyActivityAt(arm, epoch).getActivity();
	}

	public Epoch findTreatmentEpoch() {
		for (final Epoch epoch : d_epochs) {
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

	private WhenTaken treatmentWhenTaken(final RelativeTo relativeTo) {
		final Epoch epoch = findTreatmentEpoch();
		if (epoch == null) {
			return null;
		}
		final WhenTaken whenTaken = new WhenTaken(EntityUtil.createDuration("P0D"), relativeTo, epoch);
		whenTaken.commit();
		return whenTaken;
	}


	private boolean isTreatmentEpoch(final Epoch epoch) {
		for (final Arm arm : d_arms) {
			final StudyActivity sa = getStudyActivityAt(arm, epoch);
			if (sa == null || !(sa.getActivity() instanceof TreatmentActivity)) {
				return false;
			}
		}
		return true;
	}

	public Epoch findEpochWithActivity(final Activity a) {
		for (final Epoch epoch : d_epochs) {
			if (isActivityEpoch(epoch, a)) {
				return epoch;
			}
		}
		return null;
	}

	private boolean isActivityEpoch(final Epoch epoch, final Activity a) {
		for (final Arm arm : d_arms) {
			final StudyActivity sa = getStudyActivityAt(arm, epoch);
			if (sa == null || !(sa.getActivity().equals(a))) {
				return false;
			}
		}
		return true;
	}

	public TreatmentDefinition getTreatmentDefinition(final Arm a) {
		final Activity activity = getActivity(a);
		if (activity instanceof TreatmentActivity) {
			return getTreatmentDefinition((TreatmentActivity) activity);
		}
		return new TreatmentDefinition();
	}

	private TreatmentDefinition getTreatmentDefinition(final TreatmentActivity activity) {
		final List<Drug> drugs = new ArrayList<Drug>();
		for(final DrugTreatment ta : activity.getTreatments()) {
			drugs.add(ta.getDrug());
		}
		return TreatmentDefinition.createTrivial(drugs);
	}

	@Override
	public boolean deepEquals(final Entity obj) {
		if (!equals(obj)) {
			return false;
		}
		final Study other = (Study) obj;
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

	public Arm findArm(final String armName) {
		for (final Arm a : d_arms) {
			if (a.getName().equals(armName)) {
				return a;
			}
		}
		return null;
	}

	public Epoch findEpoch(final String epochName) {
		for (final Epoch e : d_epochs) {
			if (e.getName().equals(epochName)) {
				return e;
			}
		}
		return null;
	}

	public StudyActivity findStudyActivity(final String activityName) {
		for (final StudyActivity sa : d_studyActivities) {
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
	public Arm createAndAddArm(final String name, final Integer size, final Drug drug, final AbstractDose dose) {
		final Arm arm = new Arm(name, size);
		getArms().add(arm);
		final StudyActivity studyActivity = new StudyActivity(name + " treatment",
				new TreatmentActivity(new DrugTreatment(drug, dose)));
		getStudyActivities().add(studyActivity);
		final Epoch epoch = getEpochs().get(getEpochs().size() - 1);
		this.setStudyActivityAt(arm, epoch, studyActivity);
		return arm;
	}

	/**
	 * @param wt TODO
	 * @return The Drugs that have at least one Arm with a complete measurement
	 *         for the Variable v.
	 */
	public Set<TreatmentDefinition> getMeasuredTreatmentDefinitions(final Variable v, final WhenTaken wt) {
		final Set<TreatmentDefinition> definitions = new HashSet<TreatmentDefinition>();
		for (final TreatmentDefinition d : getTreatmentDefinition()) {
			if (wt != null && isMeasured(v, d, wt)) {
				definitions.add(d);
			}
		}
		return definitions;
	}

	public Set<TreatmentDefinition> getMeasuredTreatmentDefinitions(final Variable v) {
		return getMeasuredTreatmentDefinitions(v, defaultMeasurementMoment());
	}

	public ObservableList<Arm> getMeasuredArms(final Variable v, final TreatmentDefinition d) {
		return getMeasuredArms(v, d, defaultMeasurementMoment());
	}

	public ObservableList<Arm> getMeasuredArms(final Variable v, final TreatmentDefinition d, final WhenTaken wt) {
		return new FilteredObservableList<Arm>(getArms(d), new IsMeasuredFilter(v, wt));
	}

	private boolean isMeasured(final Variable v, final TreatmentDefinition d, final WhenTaken wt) {
		for (final Arm a : getArms(d)) {
			if (isMeasured(v, a, wt)) {
				return true;
			}
		}
		return false;
	}

	public boolean isMeasured(final Variable v, final Arm a, final WhenTaken wt) {
		return getMeasurement(v, a, wt) != null	&& getMeasurement(v, a, wt).isComplete();
	}


	public boolean isMeasured(final Variable v, final Arm a) {
		return getMeasurement(v, a) != null	&& getMeasurement(v, a).isComplete();
	}

	private ObservableList<Arm> getArms(final TreatmentDefinition d) {
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

	public static <T extends Variable> List<StudyOutcomeMeasure<T>> wrapVariables(final List<T> vars) {
		final List<StudyOutcomeMeasure<T>> soms = new ArrayList<StudyOutcomeMeasure<T>>();
		for (final T v : vars) {
			soms.add(wrapVariable(v));
		}
		return soms;
	}

	public class IsMeasuredFilter implements Filter<Arm> {
		private final Variable d_v;
		private final WhenTaken d_wt;

		public IsMeasuredFilter(final Variable v, final WhenTaken wt) {
			d_v = v;
			d_wt = wt;
		}

		@Override
		public boolean accept(final Arm a) {
			return isMeasured(d_v, a, d_wt);
		}
	}

	public class DrugArmFilter implements Filter<Arm> {
		private final TreatmentDefinition d_d;

		public DrugArmFilter(final TreatmentDefinition d) {
			d_d = d;
		}

		@Override
		public boolean accept(final Arm a) {
			return getTreatmentDefinition(a).equals(d_d);
		}
	}

	@Override
	public ObservableList<Note> getNotes() {
		return d_notes;
	}

	public static <T extends Variable> StudyOutcomeMeasure<T> wrapVariable(final T om) {
		return new StudyOutcomeMeasure<T>(om);
	}

	@SuppressWarnings("unchecked")
	public <T extends Variable> StudyOutcomeMeasure<T> findStudyOutcomeMeasure(final T v) {
		final ObservableList<StudyOutcomeMeasure<T>> soms = getStudyOutcomeMeasures((Class<T>)v.getClass());
		for (final StudyOutcomeMeasure<T> som : soms) {
			if (EqualsUtil.equal(som.getValue(), v)) {
				return som;
			}
		}
		return null;
	}

	public Arm findMatchingArm(TreatmentDefinition def) {
		for (Arm a : getArms()) {
			TreatmentActivity treatment = getTreatment(a);
			if (treatment != null && def.match(treatment)) {
				return a;
			}
		}
		return null;
	}
}
