package org.drugis.addis.gui;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.drugis.addis.entities.DomainManager;
import org.drugis.addis.gui.Addis2ExportDialog.ExportInfo;
import org.drugis.addis.gui.builder.Addis2ExportView;
import org.drugis.addis.gui.util.NonEmptyValueModel;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.rdf.AddisToRdfFactory;
import org.drugis.common.beans.AbstractObservable;
import org.drugis.common.gui.OkCancelDialog;

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
		}
		public String getServer() {
			return server;
		}
		public void setServer(String newVal) {
			String oldVal = this.server;
			this.server = newVal;
			firePropertyChange("server", oldVal, newVal);
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
		public void setUserId(Integer userId) {
			this.userId = userId;
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
				d_info.setStatus("Checking connection...");
				HttpClient client = HttpClients.createDefault();
				URI meUri = new URIBuilder(d_info.getServer()).setPath("/whoami").build();
				HttpGet get = new HttpGet(meUri);
				get.setHeader("X-Auth-Application-Key", d_info.apiKey);
				HttpResponse response = client.execute(get);
				ObjectMapper mapper = new ObjectMapper();
				Map parsed = mapper.readValue(response.getEntity().getContent(), Map.class);
				d_info.setUserId((Integer) parsed.get("id")); 
				d_info.setStatus("Authenticated as user " + d_info.getUserId().toString());
			} catch (Exception e) {
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
				d_info.setStatus("Checking connection...");
				HttpClient client = HttpClients.createDefault();
				URI meUri = new URIBuilder(d_info.getServer()).setPath("/whoami").build();
				HttpGet get = new HttpGet(meUri);
				get.setHeader("X-Auth-Application-Key", d_info.apiKey);
				HttpResponse response = client.execute(get);
				ObjectMapper mapper = new ObjectMapper();
				Map parsed = mapper.readValue(response.getEntity().getContent(), Map.class);
				Integer userId = (Integer) parsed.get("id"); 
				
				
				d_info.setStatus("Generating XML...");
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				d_mgr.saveXMLDomain(os);
				String xml = new String(os.toByteArray(), StandardCharsets.UTF_8);
				
				d_info.setStatus("Converting to RDF...");
				String rdf = AddisToRdfFactory.create().convert(xml, DATASET_URI_PREFIX + d_info.getDatasetId(), d_info.getName(), d_info.getTitle());
				
				d_info.setStatus("Uploading RDF...");
				URI writeDatasetUri = new URIBuilder(d_info.getServer())
						.setPath("/users/" + userId + "/datasets/" + d_info.getDatasetId())
		        .setParameter("commitTitle", "Exported from ADDIS 1.x")
		        .build();
				System.err.println(writeDatasetUri);
				HttpPost post = new HttpPost(writeDatasetUri);
				post.setHeader("Content-Type", "text/trig");
				post.setHeader("X-Auth-Application-Key", d_info.apiKey);
				post.setEntity(new ByteArrayEntity(rdf.getBytes()));
				HttpResponse execute = client.execute(post);
				d_info.setStatus("Response status: " + execute.getStatusLine());
			} catch (Exception e) {
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
		add(view.buildPanel());
		pack();
	}

}
