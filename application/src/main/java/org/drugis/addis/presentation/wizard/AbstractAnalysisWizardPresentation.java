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

package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.TypeWithName;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.EqualsUtil;
import org.drugis.common.beans.SortedSetModel;

public abstract class AbstractAnalysisWizardPresentation<Analysis extends TypeWithName> implements AnalysisWizardPresentation {

	private final class NameEqualsPredicate implements Predicate<TypeWithName> {
		private final String d_name;

		public NameEqualsPredicate(String name) {
			d_name = name;
		}

		@Override
		public boolean evaluate(TypeWithName object) {
			return EqualsUtil.equal(d_name, object.getName());
		}
	}
	protected Domain d_domain;
	protected ModifiableHolder<Indication> d_indicationHolder;
	private ModifiableHolder<String> d_name;
	private ModifiableHolder<Boolean> d_nameValidModel;
	private final List<Analysis> d_categoryContents;

	public AbstractAnalysisWizardPresentation(Domain d, final List<Analysis> categoryContents) {
		d_domain = d;
		d_categoryContents = categoryContents;
		d_indicationHolder = new ModifiableHolder<Indication>();
		d_name = new ModifiableHolder<String>();
		d_name.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				NameEqualsPredicate pred = new NameEqualsPredicate(d_name.getValue());
				d_nameValidModel.setValue(
					getNameModel().getValue() != null &&
					!getNameModel().getValue().isEmpty() &&
					CollectionUtils.find(categoryContents, pred) == null 
				);			
			}
		});
		d_nameValidModel = new ModifiableHolder<Boolean>(false);
	}
	
	/**
	 * Build an analysis based on the inputs of the creation wizard (to be called when the wizard is complete).
	 * @param name The name of the analysis to be created.
	 * @return A new analysis with the correct contents.
	 */
	public abstract Analysis createAnalysis(String name);

	public Analysis saveAnalysis() {
		if (!d_nameValidModel.getValue()) {
			throw new RuntimeException("Attempt to save an analysis with an invalid or duplicate name: " + d_name.getValue());
		}
		Analysis a = createAnalysis(d_name.getValue());		
		d_categoryContents.add(a);
		return a;
	}
	
	public ValueHolder<Indication> getIndicationModel() {
		return d_indicationHolder; 
	}

	public SortedSetModel<Indication> getIndicationsModel() {
		return d_domain.getIndications();
	}

	@Override
	public ValueHolder<String> getNameModel() {
		return d_name;
	}

	@Override
	public ValueHolder<Boolean> getNameValidModel() {
		return d_nameValidModel;
	}
}