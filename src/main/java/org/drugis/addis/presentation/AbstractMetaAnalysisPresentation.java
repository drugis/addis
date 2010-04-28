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

package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;


@SuppressWarnings("serial")
public class AbstractMetaAnalysisPresentation<T extends MetaAnalysis> extends PresentationModel<T>
implements StudyListPresentationModel {

	protected PresentationModelFactory d_mgr;
	protected DefaultStudyListPresentationModel d_studyModel;
	
	public AbstractMetaAnalysisPresentation(T bean, PresentationModelFactory mgr) {
		super(bean);
		d_mgr = mgr;
		d_studyModel = new DefaultStudyListPresentationModel(new MyListHolder());
	}
	
	
	protected class MyListHolder extends AbstractListHolder<Study> {
		@Override
		public List<Study> getValue() {
			List<Study> studies = new ArrayList<Study>(getBean().getIncludedStudies());
			for (Study s : studies) {
				if (!(s instanceof Study)) {
					studies.remove(s);
				}
			}
			return studies;
		}		
	}


	public LabeledPresentationModel getIndicationModel() {
		return d_mgr.getLabeledModel(getBean().getIndication());
	}


	public LabeledPresentationModel getOutcomeMeasureModel() {
		return d_mgr.getLabeledModel(getBean().getOutcomeMeasure());
	}


	public AbstractValueModel getCharacteristicVisibleModel(Characteristic c) {
		return d_studyModel.getCharacteristicVisibleModel(c);
	}


	public ListHolder<Study> getIncludedStudies() {
		return d_studyModel.getIncludedStudies();
	}


	public Variable.Type getAnalysisType() {
		return getBean().getOutcomeMeasure().getType();
	}
}
