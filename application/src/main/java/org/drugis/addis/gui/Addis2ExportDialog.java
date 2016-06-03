package org.drugis.addis.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.map.ObjectMapper;
import org.drugis.addis.entities.DomainManager;
import org.drugis.addis.gui.builder.Addis2ExportView;
import org.drugis.addis.rdf.AddisToRdfFactory;
import org.drugis.common.beans.AbstractObservable;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class Addis2ExportDialog extends JDialog {
	public static class ExportInfo extends AbstractObservable {
		private String name = "";
		private String title = "";
		private String apiKey = "";
		private String server = "https://addis.drugis.org/";
		private String datasetId = UUID.randomUUID().toString();
		private Integer userId = null;
		private boolean done = false;
		
		private String authStatus = "";
		private String status = "";
		
		public String getName() {
			return name;
		}
		public void setName(String newVal) {
			String oldVal = this.name;
			this.name = newVal;
			firePropertyChange("name", oldVal, newVal);
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String newVal) {
			String oldVal = this.title;
			this.title = newVal;
			firePropertyChange("title", oldVal, newVal);
		}
		public String getApiKey() {
			return apiKey;
		}
		public void setApiKey(String newVal) {
			String oldVal = this.apiKey;
			this.apiKey = newVal;
			firePropertyChange("apiKey", oldVal, newVal);
			if (!newVal.equals(oldVal)) {
				this.setUserId(null);
				this.setAuthStatus("");
			}
		}
		public String getServer() {
			return server;
		}
		public void setServer(String newVal) {
			String oldVal = this.server;
			this.server = newVal;
			firePropertyChange("server", oldVal, newVal);
			if (!newVal.equals(oldVal)) {
				this.setUserId(null);
				this.setAuthStatus("");
			}
		}
		public String getDatasetId() {
			return datasetId;
		}
		public void setDatasetId(String newVal) {
			String oldVal = this.datasetId;
			this.datasetId = newVal;
			firePropertyChange("datasetId", oldVal, newVal);
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String newVal) {
			String oldVal = this.status;
			this.status = newVal;
			firePropertyChange("status", oldVal, newVal);
		}
		public Integer getUserId() {
			return userId;
		}
		public void setUserId(Integer newVal) {
			Integer oldVal = this.userId;
			this.userId = newVal;
			firePropertyChange("userId", oldVal, newVal);
		}
		public String getAuthStatus() {
			return authStatus;
		}
		public void setAuthStatus(String newVal) {
			String oldVal = this.authStatus;
			this.authStatus = newVal;
			firePropertyChange("authStatus", oldVal, newVal);
		}
		public boolean getDone() {
			return done;
		}
		public void setDone(boolean newVal) {
			boolean oldVal = this.done;
			this.done = newVal;
			firePropertyChange("done", oldVal, newVal);
		}
	}
	
	private final class CredentialsChecker implements Runnable {
		private final ExportInfo d_info;

		public CredentialsChecker(ExportInfo info) {
			d_info = info;
		}

		@Override
		public void run() {
			try {
				d_info.setAuthStatus("Checking credentials...");
				
				HttpGet get = new HttpGet(new URIBuilder(d_info.getServer()).setPath("/whoami").build());
				get.setHeader("X-Auth-Application-Key", d_info.apiKey);
				HttpResponse response = HttpClients.createDefault().execute(get);
				
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == 200) {
					ObjectMapper mapper = new ObjectMapper();
					Map<?, ?> parsed = mapper.readValue(response.getEntity().getContent(), Map.class);
					d_info.setUserId((Integer) parsed.get("id")); 
					d_info.setAuthStatus("Authenticated as " + parsed.get("firstName") + " " + parsed.get("lastName"));
				} else {
					d_info.setAuthStatus("Authentication failed: " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
				}
			} catch (Exception e) {
				d_info.setAuthStatus("Error: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private final class Exporter implements Runnable {
		private final ExportInfo d_info;

		private Exporter(ExportInfo info) {
			d_info = info;
		}

		@Override
		public void run() {
			try {
				d_info.setStatus("Generating XML...");
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				d_mgr.saveXMLDomain(os);
				String xml = new String(os.toByteArray(), StandardCharsets.UTF_8);
				
				d_info.setStatus("Converting to RDF...");
				String rdf = AddisToRdfFactory.create().convert(xml, DATASET_URI_PREFIX + d_info.getDatasetId(), d_info.getName(), d_info.getTitle());
				
				d_info.setStatus("Uploading RDF...");
				URI writeDatasetUri = new URIBuilder(d_info.getServer())
						.setPath("/users/" + d_info.getUserId() + "/datasets/" + d_info.getDatasetId())
		        .setParameter("commitTitle", "Exported from ADDIS 1.x")
		        .build();
				HttpPost post = new HttpPost(writeDatasetUri);
				post.setHeader("Content-Type", "text/trig");
				post.setHeader("X-Auth-Application-Key", d_info.apiKey);
				post.setEntity(new ByteArrayEntity(rdf.getBytes()));
				HttpResponse response = HttpClients.createDefault().execute(post);
				
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == 200 || statusLine.getStatusCode() == 201) {
					d_info.setStatus("Export complete");
					d_info.setDone(true);
				} else {
					d_info.setStatus("Export failed: " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
				}
			} catch (Exception e) {
				d_info.setStatus("Error: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private DomainManager d_mgr;
	
	private static final String DATASET_URI_PREFIX = "http://trials.drugis.org/datasets/";

	public Addis2ExportDialog(JFrame parent, DomainManager mgr) {
		super(parent, "Export to ADDIS 2", true);
		d_mgr = mgr;
		final ExportInfo info = new ExportInfo();
		Addis2ExportView view = new Addis2ExportView(new PresentationModel<ExportInfo>(info), new CredentialsChecker(info), new Exporter(info));
		info.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("done") && evt.getNewValue().equals(true)) {
					JOptionPane.showMessageDialog(Addis2ExportDialog.this, "Dataset successfully exported to ADDIS 2");
					Addis2ExportDialog.this.dispose();
				}
			}
		});
		add(view.buildPanel());
		pack();
	}

}
