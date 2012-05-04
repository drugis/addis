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

package org.drugis.addis.presentation;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.VariableType;
import org.drugis.addis.entities.analysis.MetaAnalysis;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;


@SuppressWarnings("serial")
public abstract class AbstractMetaAnalysisPresentation<T extends MetaAnalysis> extends PresentationModel<T>
implements StudyListPresentation {

	protected PresentationModelFactory d_mgr;
	protected DefaultStudyListPresentation d_studyModel;
	
	public AbstractMetaAnalysisPresentation(T bean, PresentationModelFactory mgr) {
		super(bean);
		d_mgr = mgr;
		d_studyModel = new DefaultStudyListPresentation(new ArrayListModel<Study>(bean.getIncludedStudies()));
	}

	public LabeledPresentation getIndicationModel() {
		return d_mgr.getLabeledModel(getBean().getIndication());
	}


	public LabeledPresentation getOutcomeMeasureModel() {
		return d_mgr.getLabeledModel(getBean().getOutcomeMeasure());
	}


	public AbstractValueModel getCharacteristicVisibleModel(Characteristic c) {
		return d_studyModel.getCharacteristicVisibleModel(c);
	}


	public ObservableList<Study> getIncludedStudies() {
		return d_studyModel.getIncludedStudies();
	}


	public VariableType getAnalysisType() {
		return getBean().getOutcomeMeasure().getVariableType();
	}
}
