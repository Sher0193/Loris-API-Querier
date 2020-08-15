package org.dsher.loris.model.query.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map.Entry;

import org.dsher.loris.Session;
import org.dsher.loris.model.panes.QueryPane;
import org.dsher.loris.model.query.GetQuery;
import org.dsher.loris.model.query.candidates.Candidate;
import org.dsher.loris.model.query.candidates.Instrument;
import org.dsher.loris.utils.SSLUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import javafx.application.Platform;

public class QueryInstruments extends GetQuery {

	private String[] endpoints;
	private ArrayList<Candidate> candidates = new ArrayList<Candidate>();

	public QueryInstruments(String url, String token, QueryPane parentPane, String[] endpoints) {
		super(url, token, parentPane);
		this.endpoints = endpoints;
	}

	@Override
	public void run() {
		queryEndpoints();
	}

	private boolean queryEndpoints() {
		try {
			for (int i = 0; i < endpoints.length; i++) {
				queryEndpoint(endpoints[i]);
				double index = i + 1;
				Platform.runLater(() -> notifyProgressBar((index) / (double)endpoints.length));
				Thread.sleep(100);
			}
			deliver(null);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean queryEndpoint(String endpoint) {

		try {
			System.out.println("Querying " + endpoint + "...");
			URL url = new URL(this.url + API_EXTENSION + endpoint);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			initializeHttpUrlConnection(con);
			con.connect();

			JsonElement tree = parseJsonFromConnection(con);

			if (tree.isJsonObject()) {
				JsonObject root = tree.getAsJsonObject();

				// Meta Fields
				if (root.get("Meta") != null && root.get("Meta").isJsonObject()) {
					JsonObject metaObj = tree.getAsJsonObject().get("Meta").getAsJsonObject();

					if (metaObj.get("Instrument") == null || metaObj.get("Visit") == null || metaObj.get("Candidate") == null)
						return false;

					String instLabel = metaObj.get("Instrument").getAsString();
					String visLabel = metaObj.get("Visit").getAsString();
					String candLabel = metaObj.get("Candidate").getAsString();

					// create candidate regardless of instrument results
					Candidate cand = getCandidateById(candLabel);

					Instrument instrument = new Instrument(instLabel);
					instrument.setVisit(visLabel);


					// Instrument Fields
					if (root.get(instLabel) != null && root.get(instLabel).isJsonObject()) {
						JsonObject instObj = tree.getAsJsonObject().get(instLabel).getAsJsonObject();

						Set<Entry<String, JsonElement>> set = instObj.entrySet();
						for (Entry<String, JsonElement> e : set) {
							String value = (e.getValue().isJsonNull() ? "" : e.getValue().getAsString());
							instrument.addField(e.getKey(), value);
						}
						// we add the instrument to the candidate only if we got something interesting back
						cand.addInstrument(instrument);
						System.out.println("Successfully found information.");
						return true;
					}

				}
			}


		} catch (FileNotFoundException e) {
			System.out.println("File not found... " + endpoint);
			return false;
		} catch (IOException e) {
			deliver(e);
			return false;
		}
		return false;
	}

	private Candidate getCandidateById(String candID) {
		for (Candidate c : candidates) {
			if (c.getCandidateId().equals(candID))
				return c;
		}
		Candidate newCand = new Candidate(candID);
		candidates.add(newCand);
		return newCand;
	}

	private void deliver(final Exception e) {
		Platform.runLater(() -> {
			if (e != null) { // deliver exception

			} else { // deliver results
				Session.getInstance().showResultPane(candidates);
			}
		});
	}

}
