package org.dsher.loris.model.query.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Set;

import org.dsher.loris.Session;
import org.dsher.loris.model.panes.QueryPane;
import org.dsher.loris.model.query.GetQuery;
import org.dsher.loris.model.query.projects.Project;
import org.dsher.loris.utils.SSLUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javafx.application.Platform;

public class QueryProjects extends GetQuery {

	private ArrayList<Project> results = new ArrayList<>();

	public QueryProjects(String url, String token, QueryPane parent) {
		super(url, token, parent);
	}

	@Override
	public void run() {
		try {
			SSLUtils.trustAllCertificiates();
			if (executeProjectsQuery())
				deliver(null);
			else { // projects not found
				deliver(new FileNotFoundException());
			}
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} finally {
			try {
				SSLUtils.revertToDefaultCertificateTrust();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean executeProjectsQuery() {
		try {
			URL url = new URL(this.url + API_EXTENSION + "projects");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			initializeHttpUrlConnection(con);
			con.connect();

			JsonElement tree = parseJsonFromConnection(con);

			if (tree.isJsonObject() && tree.getAsJsonObject().get("Projects") != null && tree.getAsJsonObject().get("Projects").isJsonObject()) {
				JsonObject projects = tree.getAsJsonObject().get("Projects").getAsJsonObject();

				//Set<Entry<String, JsonElement>> projectSet = projects.entrySet();
				Set<String> keys = projects.keySet();

				double progressDenom = keys.size();

				int i = 1;
				for (String projectEntry : keys) {
					executeProjectQuery(projectEntry);
					final double index = i++;
					System.out.println("Progress: " + (index / progressDenom));
					Platform.runLater(() -> notifyProgressBar(index / progressDenom));
				}
				return true;
			}

		} catch (IOException e) {
			deliver(e);
			return false;
		}
		return false;
	}

	private boolean executeProjectQuery(String projectName) {
		Project project = new Project(projectName);
		this.results.add(project);

		try {
			URL url = new URL(this.url + API_EXTENSION + "projects/" + project.getName());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			initializeHttpUrlConnection(con);
			con.connect();

			JsonElement tree = parseJsonFromConnection(con);

			if (tree.isJsonObject()) {
				System.out.println("Processing " + projectName);
				JsonObject obj = tree.getAsJsonObject();

				// Visits
				if (obj.get("Visits") != null && obj.get("Visits").isJsonArray()) {
					JsonArray visits = obj.get("Visits").getAsJsonArray();
					for (int i = 0; i < visits.size(); i++) {
						project.addVisit(visits.get(i).getAsString());
					}
				}

				// Instruments
				if (obj.get("Instruments") != null && obj.get("Instruments").isJsonArray()) {
					JsonArray instruments = obj.get("Instruments").getAsJsonArray();
					for (int i = 0; i < instruments.size(); i++) {
						project.addInstrument(instruments.get(i).getAsString());
					}
				}

				// Candidates
				if (obj.get("Candidates") != null && obj.get("Candidates").isJsonArray()) {
					JsonArray candidates = obj.get("Candidates").getAsJsonArray();
					for (int i = 0; i < candidates.size(); i++) {
						project.addCandidate(candidates.get(i).getAsString());
					}
				}

				return true;
			}

		} catch (IOException e) {
			this.results.remove(project);
			return false;
		}
		return false;

	}

	private void deliver(final Exception e) {
		Platform.runLater(() ->{ 
			if (e != null) { // deliver exception

			} else { // deliver results
				Session.getInstance().setProjects(results);
				Session.getInstance().showStagingPane();
			}
		});
	}

}
