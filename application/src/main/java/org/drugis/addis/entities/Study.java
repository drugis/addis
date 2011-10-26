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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.StudyActivity.UsedBy;
import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.util.EntityUtil;
import org.drugis.addis.util.RebuildableTreeMap;
import org.drugis.common.DateUtil;
import org.drugis.common.EqualsUtil;
import org.drugis.common.beans.FilteredObservableList;
import org.drugis.common.beans.FilteredObservableList.Filter;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class Study extends AbstractNamedEntity<Study> implements TypeWithNotes {

	public static class MeasurementKey extends AbstractEntity implements Entity, Comparable<MeasurementKey> {

		private final Variable d_variable;
		private final Arm d_arm;
		private final WhenTaken d_wt;

		public MeasurementKey(Variable v, Arm a, WhenTaken wt) {
			if (v == null) {
				throw new NullPointerException("Variable may not be null");
			}
			if (v instanceof OutcomeMeasure && a == null) {
				throw new NullPointerException(
						"Arm may not be null for Endpoints/ADEs");
			}
			if (wt == null) {
				throw new NullPointerException("Moment of measurement may not be null");
			}
			d_variable = v;
			d_arm = a;
			d_wt = wt;
		}

		public MeasurementKey(StudyOutcomeMeasure<? extends Variable> som, Arm a, WhenTaken wt) {
			this(som.getValue(), a, wt);
		}

		public Variable getVariable() {
			return d_variable;
		}

		public Arm getArm() {
			return d_arm;
		}

		public WhenTaken getWhenTaken() {
			return d_wt;
		}

		@Override
		public String toString() {
			return "<" + d_variable + ", " + d_arm + " at " + d_wt + ">";
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof MeasurementKey) {
				MeasurementKey other = (MeasurementKey) o;
				return d_variable.equals(other.d_variable)
						&& EqualsUtil.equal(d_arm, other.d_arm)
						&& EqualsUtil.equal(d_wt, other.d_wt);
			}
			return false;
		}

		@Override
		public int hashCode() {
			int code = 1;
			code = code * 31 + d_variable.hashCode();
			code = code * 31 + (d_arm == null ? 0 : d_arm.hashCode());
			code = code * 31 + (d_wt == null ? 0 : d_wt.hashCode());
			return code;
		}

		@Override
		public Set<? extends Entity> getDependencies() {
			return Collections.emptySet();
		}

		@Override
		public int compareTo(MeasurementKey o) {
			if (d_variable.compareTo(o.d_variable) == 0) {
				if (d_arm != null) {
					if (d_arm.compareTo(o.d_arm) == 0) {
						return d_wt.compareTo(o.d_wt);
					}
					return d_arm.compareTo(o.d_arm);
				} else if (o.d_arm == null) {
					return d_wt.compareTo(o.d_wt);
				} else {
					return -1;
				}
			}
			return d_variable.compareTo(o.d_variable);
		}
	}

	@SuppressWarnings("serial")
	public static class StudyOutcomeMeasure<T extends Variable> extends ObjectWithNotes<T> {
		public static final String PROPERTY_IS_PRIMARY = "isPrimary";

		private Boolean d_isPrimary = false;
		private ObservableList<WhenTaken> d_whenTaken = new ArrayListModel<WhenTaken>();

		public StudyOutcomeMeasure(T obj) {
			super(obj);
		}

		public StudyOutcomeMeasure(T obj, WhenTaken whenTaken) {
			this(obj);
			if (whenTaken != null) {
				d_whenTaken.add(whenTaken);
			}
		}

		@Override
		public StudyOutcomeMeasure<T> clone() {
			StudyOutcomeMeasure<T> clone = new StudyOutcomeMeasure<T>(getValue());
			clone.setIsPrimary(getIsPrimary());
			clone.getNotes().addAll(getNotes());
			for (WhenTaken wt : getWhenTaken()) {
				clone.getWhenTaken().add(wt.clone());
			}
			return clone;
		}

		public Boolean getIsPrimary() {
			return d_isPrimary;
		}
		
		public void setIsPrimary(Boolean isPrimary) {
			Boolean oldValue = new Boolean(d_isPrimary);
			d_isPrimary = isPrimary;
			firePropertyChange(PROPERTY_IS_PRIMARY, oldValue, d_isPrimary);
		}

		public ObservableList<WhenTaken> getWhenTaken() {
			return d_whenTaken;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof StudyOutcomeMeasure<?>) {
				StudyOutcomeMeasure<?> other = (StudyOutcomeMeasure<?>) o;
				return  EqualsUtil.equal(getValue(), other.getValue());
			}
			return false;
		}
		
		@Override
		public String toString() {
			return d_isPrimary ? "primary measure: " : "secondary measure: " + getValue().getName() + " " + d_whenTaken + " " + getNotes();
		}
	}

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

	private ObservableList<StudyOutcomeMeasure<Endpoint>> d_endpoints = new ArrayListModel<StudyOutcomeMeasure<Endpoint>>();
	private ObservableList<StudyOutcomeMeasure<AdverseEvent>> d_adverseEvents = new ArrayListModel<StudyOutcomeMeasure<AdverseEvent>>();
	private ObservableList<StudyOutcomeMeasure<PopulationCharacteristic>> d_populationChars = new ArrayListModel<StudyOutcomeMeasure<PopulationCharacteristic>>();

	private ObservableList<Arm> d_arms = new ArrayListModel<Arm>();
	private ObservableList<Epoch> d_epochs = new ArrayListModel<Epoch>();
	private ObservableList<StudyActivity> d_studyActivities = new ArrayListModel<StudyActivity>();

	private RebuildableTreeMap<MeasurementKey, BasicMeasurement> d_measurements = new RebuildableTreeMap<MeasurementKey, BasicMeasurement>();
	private ObservableList<Note> d_notes = new ArrayListModel<Note>();

	public Study() {
		this(null, null);
	}

	@Override
	public Study clone() {
		Study newStudy = new Study();
		newStudy.setName(getName());
		newStudy.getNotes().addAll(getNotes());
		newStudy.d_indication = d_indication.clone();

		newStudy.d_arms = cloneArms();

		for (Epoch e : getEpochs()) {
			newStudy.getEpochs().add(e.clone());
		}

		newStudy.d_endpoints = cloneStudyOutcomeMeasures(getEndpoints(), newStudy.getEpochs());
		newStudy.d_adverseEvents = cloneStudyOutcomeMeasures(getAdverseEvents(), newStudy.getEpochs());
		newStudy.d_populationChars = cloneStudyOutcomeMeasures(getPopulationChars(), newStudy.getEpochs());

		for (StudyActivity sa : getStudyActivities()) {
			newStudy.getStudyActivities().add(
					cloneStudyActivity(sa, newStudy.getArms(), newStudy
							.getEpochs()));
		}

		// Copy measurements _AFTER_ the outcomes, since setEndpoints() etc
		// removes orphan measurements from the study.
		// Also copy AFTER the epochs/SAs because measurements need to be placed within their coordinate system.
		newStudy.setMeasurements(cloneMeasurements(newStudy.getArms(), newStudy.getEpochs()));

		newStudy.setCharacteristics(cloneCharacteristics());


		return newStudy;
	}

	private StudyActivity cloneStudyActivity(StudyActivity sa, ObservableList<Arm> newArms, ObservableList<Epoch> newEpochs) {
		StudyActivity newSA = sa.clone();
		Set<UsedBy> newUsedBys = new HashSet<UsedBy>();
		for (UsedBy ub : sa.getUsedBy()) {
			newUsedBys.add(fixUsedBy(ub, newArms, newEpochs));
		}
		newSA.setUsedBy(newUsedBys);
		return newSA;
	}

	private UsedBy fixUsedBy(UsedBy ub, ObservableList<Arm> newArms,
			ObservableList<Epoch> newEpochs) {
		return new UsedBy(newArms.get(newArms.indexOf(ub.getArm())), newEpochs
				.get(newEpochs.indexOf(ub.getEpoch())));
	}

	private <T extends Variable> ObservableList<StudyOutcomeMeasure<T>> cloneStudyOutcomeMeasures(List<StudyOutcomeMeasure<T>> soms, List<Epoch> epochs) {
		ObservableList<StudyOutcomeMeasure<T>> list = new ArrayListModel<StudyOutcomeMeasure<T>>(); 
		for (StudyOutcomeMeasure<T> som : soms) {
			StudyOutcomeMeasure<T> clone = som.clone();
			for (WhenTaken wt : clone.getWhenTaken()) {
				wt.setEpoch(epochs.get(epochs.indexOf(wt.getEpoch())));
			}
			list.add(clone);
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

	private Map<MeasurementKey, BasicMeasurement> cloneMeasurements(List<Arm> newArms, List<Epoch> newEpochs) {
		HashMap<MeasurementKey, BasicMeasurement> hashMap = new HashMap<MeasurementKey, BasicMeasurement>();
		for (MeasurementKey key : d_measurements.keySet()) {
			hashMap.put(fixKey(key, newArms, newEpochs), d_measurements.get(key).clone());
		}
		return hashMap;
	}

	private MeasurementKey fixKey(MeasurementKey key, List<Arm> newArms, List<Epoch> newEpochs) {
		WhenTaken wt = key.getWhenTaken();
		return new MeasurementKey(key.getVariable(), key.getArm() == null ? null : newArms.get(getArms().indexOf(key.getArm())), 
				new WhenTaken(wt.getDuration(), wt.getRelativeTo(), newEpochs.get(getEpochs().indexOf(wt.getEpoch()))));
	}

	private ObservableList<Arm> cloneArms() {
		ObservableList<Arm> newList = new ArrayListModel<Arm>();
		for (Arm a : getArms()) {
			newList.add(a.clone());
		}
		return newList;
	}

	public Study(String id, Indication i) {
		super(id);
		d_indication = new ObjectWithNotes<Indication>(i);
		d_arms = new ArrayListModel<Arm>();
		setCharacteristic(BasicStudyCharacteristic.CREATION_DATE, DateUtil
				.getCurrentDateWithoutTime());
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
		d_endpoints.addListDataListener(orphanListener);
		d_populationChars.addListDataListener(orphanListener);
		d_adverseEvents.addListDataListener(orphanListener);
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
			activity = d_studyActivities.get(d_studyActivities
					.indexOf(activity)); // ensure we have the *same* object,
											// not just an *equal* one.
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
					dep.add(d.getDoseUnit().getUnit());
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
			setCharacteristicWithNotes(c, charVal); // FIXME: this is a hack
													// because d_chars is
													// exposed & also firing
													// events.
		} else {
			setCharacteristicWithNotes(c, new ObjectWithNotes<Object>(val));
		}
	}

	public ObjectWithNotes<?> getCharacteristicWithNotes(Characteristic c) {
		return d_chars.get(c);
	}

	public void setCharacteristicWithNotes(BasicStudyCharacteristic c,
			ObjectWithNotes<?> val) {
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

	public int compareTo(Study other) {
		return getName().compareTo(other.getName());
	}

	public BasicMeasurement getMeasurement(Variable v, Arm a, WhenTaken wt) {
		MeasurementKey key = new MeasurementKey(v, a, wt);
		BasicMeasurement basicMeasurement = d_measurements.get(key);
		return basicMeasurement;
	}
	
	public BasicMeasurement getMeasurement(Variable v, Arm a) {
		return d_measurements.get(new MeasurementKey(v, a, defaultMeasurementMoment()));
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
		List<OutcomeMeasure> sortedList = new ArrayList<OutcomeMeasure>(
				extractVariables(getEndpoints()));
		sortedList.addAll(extractVariables(getAdverseEvents()));
		Collections.sort(sortedList);
		return sortedList;
	}

	public static <T extends Variable> List<T> extractVariables(
			List<StudyOutcomeMeasure<T>> soms) {
		List<T> vars = new ArrayList<T>();
		for (StudyOutcomeMeasure<T> som : soms) {
			vars.add(som.getValue());
		}
		return vars;
	}

	public List<? extends Variable> getVariables(Class<? extends Variable> type) {
		return extractVariables(getStudyOutcomeMeasures(type));
	}

	@SuppressWarnings("unchecked")
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
	
	public void addVariable(Variable om) {
		addVariable(om, null);
	}


	public void addVariable(Variable om, WhenTaken wt) {
		if (om instanceof Endpoint)
			getEndpoints().add(
					new StudyOutcomeMeasure<Endpoint>(((Endpoint) om), wt));
		else if (om instanceof AdverseEvent) {
			getAdverseEvents().add(
					new StudyOutcomeMeasure<AdverseEvent>(((AdverseEvent) om), wt));
		} else if (om instanceof PopulationCharacteristic) {
			getPopulationChars().add(
					new StudyOutcomeMeasure<PopulationCharacteristic>(
							((PopulationCharacteristic) om), wt));
		} else {
			throw new IllegalStateException("Illegal OutcomeMeasure type "
					+ om.getClass());
		}
	}
	
	private void removeOrphanMeasurements() {
		for (MeasurementKey k : new HashSet<MeasurementKey>(d_measurements
				.keySet())) {
			if (orphanKey(k)) {
				d_measurements.remove(k);
			}
		}
	}

	public BasicMeasurement buildDefaultMeasurement(Variable v, Arm a) {
		return v.buildMeasurement(a == null ? getSampleSize() : a.getSize());
	}

	private boolean orphanKey(MeasurementKey k) {
		// OutcomeMeasure measurement
		if (k.d_variable instanceof OutcomeMeasure) {
			if (!getAdverseEvents().contains(
					new StudyOutcomeMeasure<Variable>(k.d_variable))
					&& !getEndpoints().contains(
							new StudyOutcomeMeasure<Variable>(k.d_variable))
					&& !getPopulationChars().contains(
							new StudyOutcomeMeasure<Variable>(k.d_variable))) {
				return true;
			}
			if (!d_arms.contains(k.d_arm)) {
				return true;
			}
			return false;
		}
		// PopulationChar measurements
		if (k.d_variable instanceof PopulationCharacteristic) {
			if (!getPopulationChars().contains(
					new StudyOutcomeMeasure<Variable>(k.d_variable))) {
				return true;
			}
			if (k.d_arm != null && !d_arms.contains(k.d_arm)) {
				return true;
			}
			return false;
		}

		throw new IllegalStateException(k + " is not a valid measurement key");
	}

	public int getSampleSize() {
		int s = 0;
		for (Arm pg : d_arms)
			s += pg.getSize();
		return s;
	}

	public Map<MeasurementKey, BasicMeasurement> getMeasurements() {
		return d_measurements;
	}

	private void setMeasurements(Map<MeasurementKey, BasicMeasurement> m) {
		d_measurements = new RebuildableTreeMap<MeasurementKey, BasicMeasurement>(m);
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
			throw new IllegalArgumentException(
					"Unknown StudyOutcomeMeasure type: " + value.getValue());
		}
	}

	public TreatmentActivity getTreatment(Arm arm) {
		return getActivity(arm) instanceof TreatmentActivity ? (TreatmentActivity) getActivity(arm)
				: null;
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
			if (isTreatmentEpoch(epoch))
				return epoch;
		}
		return null;
	}

	public WhenTaken defaultMeasurementMoment() {
		return new WhenTaken(EntityUtil.createDuration("P0D"), RelativeTo.BEFORE_EPOCH_END, findTreatmentEpoch());
	}
	
	public WhenTaken baselineMeasurementMoment() {
		return new WhenTaken(EntityUtil.createDuration("P0D"), RelativeTo.FROM_EPOCH_START, findTreatmentEpoch());
	}

	private boolean isTreatmentEpoch(Epoch epoch) {
		for (Arm arm : d_arms) {
			StudyActivity sa = getStudyActivityAt(arm, epoch);
			if (sa == null
					|| (!(sa.getActivity() instanceof DrugTreatment) && !(sa
							.getActivity() instanceof TreatmentActivity))) {
				return false;
			}
		}
		return true;
	}

	public Epoch findEpochWithActivity(Activity a) {
		for (Epoch epoch : d_epochs) {
			if (isActivityEpoch(epoch, a))
				return epoch;
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

	public boolean deepEquals(Entity obj) {
		if (!equals(obj)) {
			return false;
		}
		Study other = (Study) obj;
		return EntityUtil.deepEqual(other.getIndication(), getIndication())
				&& EntityUtil.deepEqual(other.getCharacteristics(),
						getCharacteristics())
				&& EntityUtil.deepEqual(Study.extractVariables(other
						.getEndpoints()), extractVariables(getEndpoints()))
				&& EntityUtil.deepEqual(Study.extractVariables(other
						.getAdverseEvents()),
						extractVariables(getAdverseEvents()))
				&& EntityUtil.deepEqual(Study.extractVariables(other
						.getPopulationChars()),
						extractVariables(getPopulationChars()))
				&& EntityUtil.deepEqual(other.getArms(), getArms())
				&& EntityUtil.deepEqual(other.getEpochs(), getEpochs())
				&& EntityUtil.deepEqual(other.getStudyActivities(),
						getStudyActivities())
				&& EntityUtil.deepEqual(other.getMeasurements(),
						getMeasurements())
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

	public void rehashMeasurements() {
		d_measurements.rebuild();
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
		return new FilteredObservableList<Arm>(getArms(d),
				new IsMeasuredFilter(v, wt));
	}

	private boolean isMeasured(Variable v, DrugSet d, WhenTaken wt) {
		for (Arm a : getArms(d)) {
			if (isMeasured(v, a, wt)) {
				return true;
			}
		}
		return false;
	}

	private boolean isMeasured(Variable v, Arm a, WhenTaken wt) {
		return getMeasurement(v, a) != null
				&& getMeasurement(v, a).isComplete();
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

}
