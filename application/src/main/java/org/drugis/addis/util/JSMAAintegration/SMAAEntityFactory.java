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

package org.drugis.addis.util.JSMAAintegration;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;

import fi.smaa.jsmaa.model.SMAAModel;

public class SMAAEntityFactory {
	public static <AltType extends Entity> SMAAModel createSMAAModel(BenefitRiskAnalysis<AltType> brAnalysis) {
		AbstractBenefitRiskSMAAFactory<AltType> factory = createFactory(brAnalysis);
		return factory.createSMAAModel();
	}

	@SuppressWarnings("unchecked")
	public static <AltType extends Entity> AbstractBenefitRiskSMAAFactory<AltType> createFactory(BenefitRiskAnalysis<AltType> brAnalysis) {
		AbstractBenefitRiskSMAAFactory<AltType> factory;
		if (brAnalysis instanceof StudyBenefitRiskAnalysis) {
			factory = (AbstractBenefitRiskSMAAFactory<AltType>) new StudyBenefitRiskSMAAFactory((StudyBenefitRiskAnalysis) brAnalysis);
		} else if (brAnalysis instanceof MetaBenefitRiskAnalysis) {
			factory = (AbstractBenefitRiskSMAAFactory<AltType>) new MetaBenefitRiskSMAAFactory((MetaBenefitRiskAnalysis) brAnalysis);
		} else {
			throw new IllegalArgumentException("BR Analysis " + brAnalysis.getName() + " is of unknown type " + brAnalysis.getClass().getCanonicalName());
		}
		return factory;
	}
}