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

package org.drugis.addis.entities.relativeeffect;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;

public class RelativeEffectTestBase {
	protected final Drug d_fluox;
	protected final Drug d_sertr;
	protected final Indication d_ind;
	protected final Endpoint d_rateEndpoint;
	protected final Endpoint d_contEndpoint;
	
	protected final Study d_bennie;
	protected final Study d_boyer;
	protected final Study d_fava;
	protected final Study d_newhouse;
	protected final Study d_sechter;
	
	protected RelativeEffectTestBase() {
		d_ind = ExampleData.buildIndicationDepression();
		d_fluox = ExampleData.buildDrugFluoxetine();
		d_sertr = ExampleData.buildDrugSertraline();
		d_rateEndpoint = ExampleData.buildEndpointHamd();
		d_contEndpoint = ExampleData.buildEndpointCgi();
		
		d_bennie = ExampleData.buildRateStudy("Bennie 1995", 63, 144, 73, 142);
		d_boyer = ExampleData.buildRateStudy("Boyer 1998", 61, 120, 63, 122);
		d_fava = ExampleData.buildRateStudy("Fava 2002", 57, 92, 70, 96);
		d_newhouse = ExampleData.buildRateStudy("Newhouse 2000", 84, 119, 85, 117);
		d_sechter = ExampleData.buildRateStudy("Sechter 1999", 76, 120, 86, 118);
	}
}
