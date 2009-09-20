/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.entities.MetaStudy;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("unchecked")
public class PresentationModelManager {
	private Map<Object, PresentationModel> cache = new
		HashMap<Object, PresentationModel>();
	
	public PresentationModel getModel(Object obj) {
		PresentationModel mod = cache.get(obj);
		if (mod != null) {
			return mod;
		}
		mod = createModel(obj);
		cache.put(obj, mod);
		return mod;
	}

	private PresentationModel createModel(Object obj) {
		if (obj instanceof MetaStudy) {
			return new MetaStudyPresentationModel((MetaStudy) obj);
		}
		return new PresentationModel(obj);
	}
}
