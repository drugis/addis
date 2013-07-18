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

import java.util.List;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.forestplot.BinnedScale;
import org.drugis.common.Interval;

public interface ForestPlotPresentation {

	public abstract int getNumRelativeEffects();

	public abstract RelativeEffect<?> getRelativeEffectAt(int i);

	public abstract BinnedScale getScale();

	public abstract AxisType getScaleType();

	public abstract Interval<Double> getRange();

	public abstract String getLowValueFavors();

	public abstract String getHighValueFavors();

	public abstract String getStudyLabelAt(int i);

	public abstract String getCIlabelAt(int i);

	public abstract List<Integer> getTicks();

	public abstract List<String> getTickVals();

	public abstract int getDiamondSize(int index);

	public abstract OutcomeMeasure getOutcomeMeasure();

	public abstract boolean isPooledRelativeEffect(int i);

	public abstract String getHeterogeneityI2();

	public abstract String getHeterogeneity();

	public abstract boolean hasPooledRelativeEffect();

}