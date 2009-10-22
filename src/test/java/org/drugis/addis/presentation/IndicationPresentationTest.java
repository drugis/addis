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

import java.beans.PropertyChangeListener;
import java.util.TreeSet;

import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.IndicationPresentation;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

import static org.easymock.EasyMock.*;

public class IndicationPresentationTest {
	@Test
	public void testCodeFireLabelChanged() {
		Indication i = new Indication(5L, "Some indication");
		IndicationPresentation p = new IndicationPresentation(i, new TreeSet<Study>());
		AbstractValueModel model = p.getLabelModel();
		PropertyChangeListener x = JUnitUtil.mockListener(model, "value", "5 Some indication", "6 Some indication");
		model.addPropertyChangeListener(x);
		i.setCode(6L);
		verify(x);
	}
	
	@Test
	public void testNameFireLabelChanged() {
		Indication i = new Indication(5L, "Some indication");
		IndicationPresentation p = new IndicationPresentation(i, new TreeSet<Study>());
		AbstractValueModel model = p.getLabelModel();
		PropertyChangeListener x = JUnitUtil.mockListener(model, "value", "5 Some indication", "5 Other indication");
		model.addPropertyChangeListener(x);
		i.setName("Other indication");
		verify(x);
	}

}
