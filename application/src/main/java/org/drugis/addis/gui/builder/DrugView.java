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

package org.drugis.addis.gui.builder;

import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.components.StudiesTablePanel;
import org.drugis.addis.presentation.DrugPresentation;
import org.drugis.addis.presentation.StudyListPresentation;
import org.drugis.addis.util.AtcParser;
import org.drugis.addis.util.AtcParser.AtcDescription;
import org.drugis.addis.util.RunnableReadyModel;
import org.drugis.common.gui.BuildViewWhenReadyComponent;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.LinkLabel;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DrugView implements ViewBuilder {
	private static final String SEARCH_DOMAIN = "medicines.org.uk";
	private DrugPresentation d_model;
	private AddisWindow d_parent;

	public DrugView(DrugPresentation model, AddisWindow parent) {
		d_model = model;
		d_parent = parent;
	}
	
	public JComponent buildPanel() {
		
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Drug.class).getSingularCapitalized(), cc.xy(1, 1));
		builder.add(createDrugOverviewPanel(d_model),
				cc.xy(1, 3));
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Study.class).getPlural()
							 + " measuring this "
							 + CategoryKnowledgeFactory.getCategoryKnowledge(Drug.class).getSingular() , cc.xy(1, 5));
		builder.add(buildStudyListComponent(d_model.getStudyListPresentation(), d_parent), cc.xy(1, 7));
				
		return builder.getPanel();	
	}

	public static JComponent buildStudyListComponent(StudyListPresentation model, AddisWindow parent) {
		JComponent studiesComp = null;
		if(model.getIncludedStudies().isEmpty()) {
			studiesComp = new JLabel("No studies found.");
		} else {
			studiesComp = new StudiesTablePanel(model, parent);
		}
		return studiesComp;
	}

	public static JPanel createDrugOverviewPanel(DrugPresentation model) {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, left:pref, center:8dlu, left:pref",
				"p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel("Name:", cc.xy(1, 1));
		AbstractValueModel drugname = model.getModel(Drug.PROPERTY_NAME);
		
		builder.add(new LinkLabel(drugname.getString() + " -", "Search for SmPC at " + SEARCH_DOMAIN, getSearchUrl(drugname)) , cc.xy(3,1));
		builder.addLabel("ATC Code:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(model.getModel(Drug.PROPERTY_ATCCODE)), cc.xy(3, 3));
		
		AtcDetailsRetriever retriever = new AtcDetailsRetriever(model);
		AtcDetailsPanelBuilder detailsBuilder = new AtcDetailsPanelBuilder(retriever);
		RunnableReadyModel readyModel = new RunnableReadyModel(retriever);
		BuildViewWhenReadyComponent c = new BuildViewWhenReadyComponent(detailsBuilder, readyModel, "Loading...");
		builder.add(c, cc.xy(3, 5));
		new Thread(readyModel).start();
			
		return builder.getPanel();
	}
	
	private static class AtcDetailsRetriever implements Runnable {
		private IOException d_error;
		private List<AtcDescription> d_drugDetails;
		private DrugPresentation d_model;
		private AbstractValueModel d_atcCode;
		
		public AtcDetailsRetriever(DrugPresentation model) {
			d_model = model;
		}

		public void run() {
			try {
				d_atcCode = d_model.getModel(Drug.PROPERTY_ATCCODE);
				d_drugDetails = new AtcParser().getAtcDetails(d_atcCode.getString());
			} catch (IOException e) {
				d_error = e;
			}
		}

		public IOException getError() {
			return d_error;
		}

		public List<AtcDescription> getDrugDetails() {
			return d_drugDetails;
		}

		public AbstractValueModel getAtcCode() {
			return d_atcCode;
		}
	}
	
	private static class AtcDetailsPanelBuilder implements ViewBuilder {
		private final AtcDetailsRetriever d_details;

		public AtcDetailsPanelBuilder(AtcDetailsRetriever details) {
			d_details = details;
		}

		public JComponent buildPanel() {
			FormLayout layout = new FormLayout("left:pref", "p");	
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			if (d_details.getError() != null) {
				builder.addLabel("Error communicating with WHO website: " + d_details.getError().getMessage());
			} else {
				int pos = 1;
				if(d_details.getDrugDetails().isEmpty()) {
					builder.addLabel("No details found for this ATC code.");
				}
				for(AtcDescription desc : d_details.getDrugDetails()) {
					if(!desc.getCode().equals(d_details.getAtcCode().getString())){
						builder.addLabel(desc.getCode() + ": " + desc.getDescription(), cc.xy(1, pos));
						LayoutUtil.addRow(layout);
						pos += 2;
					}
				}
			}
			return builder.getPanel();
		}		
	}

	private static String getSearchUrl(AbstractValueModel drugname) {
		return "http://www." + SEARCH_DOMAIN + "/EMC/searchresults.aspx?term=" + drugname.getValue().toString().replace(' ', '+');
	}
}