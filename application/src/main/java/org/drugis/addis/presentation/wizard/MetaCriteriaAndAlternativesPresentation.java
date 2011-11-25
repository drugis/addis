package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.AbstractList;
import java.util.TreeSet;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.beans.FilteredObservableList;
import org.drugis.common.beans.SortedSetModel;
import org.drugis.common.beans.TransformedObservableList;
import org.drugis.common.beans.FilteredObservableList.Filter;
import org.drugis.common.beans.TransformedObservableList.Transform;
import org.pietschy.wizard.InvalidStateException;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

public class MetaCriteriaAndAlternativesPresentation extends CriteriaAndAlternativesPresentation<DrugSet> {
	public static class IndicationFilter implements Filter<MetaAnalysis> {
		private final Indication d_indication;
		public IndicationFilter(Indication i) {
			d_indication = i;
		}
		public boolean accept(MetaAnalysis ma) {
			return ma.getIndication().equals(d_indication);
		}
	}
	private final ObservableList<OutcomeMeasure> d_availableCriteria;
	private final ObservableList<OutcomeMeasure> d_metaAnalysesCriteria;
	private final FilteredObservableList<MetaAnalysis> d_metaAnalyses;

	public MetaCriteriaAndAlternativesPresentation(ValueHolder<Indication> indication, ModifiableHolder<AnalysisType> analysisType, SortedSetModel<MetaAnalysis> metaAnalyses) {
		super(indication, analysisType);
		d_metaAnalyses = new FilteredObservableList<MetaAnalysis>(metaAnalyses, new IndicationFilter(d_indication.getValue()));
		d_indication.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				d_metaAnalyses.setFilter(new IndicationFilter(d_indication.getValue()));
			}
		});
		d_metaAnalysesCriteria = new TransformedObservableList<MetaAnalysis, OutcomeMeasure>(d_metaAnalyses, 
			new Transform<MetaAnalysis, OutcomeMeasure>() {
				public OutcomeMeasure transform(MetaAnalysis a) {
					return a.getOutcomeMeasure();
				}
			});
		d_availableCriteria = new SortedSetModel<OutcomeMeasure>();
		d_metaAnalysesCriteria.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				updateCriteria();
			}
			public void intervalAdded(ListDataEvent e) {
				updateCriteria();
			}
			public void contentsChanged(ListDataEvent e) {
				updateCriteria();
			}
		});
	}

	protected void updateCriteria() {
		d_availableCriteria.clear();
		d_availableCriteria.addAll(d_metaAnalysesCriteria);
	}

	@Override
	public BenefitRiskAnalysis<DrugSet> createAnalysis(String id)
			throws InvalidStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObservableList<DrugSet> getAlternativesListModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueModel getCompleteModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObservableList<OutcomeMeasure> getCriteriaListModel() {
		return d_availableCriteria;
	}

	@Override
	protected void reset() {
	}

}
