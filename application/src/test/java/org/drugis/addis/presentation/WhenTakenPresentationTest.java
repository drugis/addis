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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.WhenTaken;
import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.presentation.DurationPresentation.DateUnits;
import org.drugis.addis.util.EntityUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class WhenTakenPresentationTest {

	private Epoch d_epoch1;
	private Epoch d_epoch2;
	private ObservableList<Epoch> d_epochs;
	private WhenTakenPresentation d_wtp;

	@Before
	public void setUp() {
		d_epoch1 = new Epoch("Je moeder", EntityUtil.createDuration("P31D"));
		d_epoch2 = new Epoch("Je vader", EntityUtil.createDuration("P33D"));
		d_epochs = new ArrayListModel<Epoch>();
		d_epochs.addAll(Arrays.asList(d_epoch1, d_epoch2));
		d_wtp = new WhenTakenPresentation(new WhenTaken(EntityUtil.createDuration("P29D"), RelativeTo.FROM_EPOCH_START, d_epoch1), d_epochs);
	}


	@Test
	public void testOffsetPresentation() {
		DurationPresentation<WhenTaken> op = d_wtp.getOffsetPresentation();
		op.setQuantity(1396);
		op.setUnits(DateUnits.Minutes);
		assertEquals(EntityUtil.createDuration("PT1396M"), d_wtp.getBean().getOffset());
	}

}
