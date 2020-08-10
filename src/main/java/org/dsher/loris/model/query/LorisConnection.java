package org.dsher.loris.model.query;

/*import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import org.dsher.loris.Session;
import org.dsher.loris.model.query.candidates.Candidate;
import org.dsher.loris.model.query.candidates.Instrument;
import org.dsher.loris.model.query.candidates.Visit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;*/

public class LorisConnection {

	@SuppressWarnings("unused")
	private String url, token;

	/*public static final String API_EXTENSION = "api/v0.0.2/";

	private Candidate modelCandidate; // model for selecting relevant information
	private ArrayList<Candidate> candidates = new ArrayList<>();
	private ArrayList<String> unfoundIds = new ArrayList<>();*/

	public LorisConnection(String url, String token) {
		this.url = url;
		this.token = token;
		//this.setModelCandidate(new Candidate("loris-query-application-model"));
	}

	/*public boolean queryCandidates(String list, String flag) {
		System.out.println("Flag: " + flag);
		if (buildCandidateList(list, flag) && populateCandidates()) {
			System.out.println("Not found: " + unfoundIds.toString());
			Session.getInstance().showResultPane();
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private boolean buildCandidateList(String list, String flag) {
		String[] ids = list.split(",");
		try {
			URL url = new URL(this.url + API_EXTENSION + "candidates");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			initializeHttpUrlConnection(con);
			con.connect();

			JsonParser parser = new JsonParser();
			JsonElement tree = parser.parse(new InputStreamReader(con.getInputStream(), "UTF-8"));

			if (tree.isJsonObject()) {
				if (tree.getAsJsonObject().get("Candidates") == null) {
					return false;
				}
				JsonArray arr = tree.getAsJsonObject().get("Candidates").getAsJsonArray();

				for (String id : ids) {
					id = id.trim();
					System.out.println("Processing " + id);
					boolean found = false;
					for (int i = 0; i < arr.size(); i++) {
						System.out.println("Processing JSON array element " + i);
						if (arr.get(i).isJsonObject()) {
							JsonObject obj = arr.get(i).getAsJsonObject();

							if (flag.equals("CandID")) {
								if (obj.get("CandID") == null || !obj.get("CandID").getAsString().equals(id))
									continue;
							} else if (flag.equals("PSCID")) {
								if (obj.get("PSCID") == null || !obj.get("PSCID").getAsString().equals(id))
									continue;
							}
							if (obj.get("CandID") != null) {
								Candidate candidate = new Candidate(obj.get("CandID").getAsString());
								candidates.add(candidate);
								found = true;
								break;
							}
						}
					}
					if (!found) {
						unfoundIds.add(id);
					}
				}
				return true;
			}
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public ArrayList<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(ArrayList<Candidate> candidates) {
		this.candidates = candidates;
	}

	public boolean populateCandidates() {
		for (Candidate candidate : candidates) {
			try {
				if (!populateCandidate(candidate))
					return false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	private boolean populateCandidate(Candidate c) throws InterruptedException {
		//Thread.sleep(100);
		try {
			URL url = new URL(this.url + API_EXTENSION + "candidates/" + c.getCandidateId());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			initializeHttpUrlConnection(con);
			con.connect();

			JsonParser parser = new JsonParser();
			JsonElement tree = parser.parse(new InputStreamReader(con.getInputStream(), "UTF-8"));

			if (tree.isJsonObject()) {
				JsonObject obj = tree.getAsJsonObject();

				if (obj.get("Visits") != null) {
					JsonArray visits = obj.get("Visits").getAsJsonArray();
					for (int i = 0; i < visits.size(); i++) {
						populateVisit(c, visits.get(i).getAsString());
					}
				}
				return true;
			}
		} catch (FileNotFoundException e) {
			System.out.println("Nothing found for candidate " + c.getCandidateId() + ".");
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private boolean populateVisit(Candidate c, String visitLabel) throws InterruptedException {
		//Thread.sleep(100);
		Visit v = new Visit(visitLabel);
		c.addVisit(v);
		modelCandidate.addVisit(v);
		try {
			URL url = new URL(this.url + API_EXTENSION + "candidates/" + c.getCandidateId() + "/" + visitLabel + "/instruments");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			initializeHttpUrlConnection(con);
			con.connect();

			JsonParser parser = new JsonParser();
			JsonElement tree = parser.parse(new InputStreamReader(con.getInputStream(), "UTF-8"));

			if (tree.isJsonObject()) {
				JsonObject obj = tree.getAsJsonObject();

				if (obj.get("Instruments") != null) {
					JsonArray instruments = obj.get("Instruments").getAsJsonArray();
					for (int i = 0; i < instruments.size(); i++) {
						populateInstrument(c, v, instruments.get(i).getAsString());
					}
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Nothing found for visit " + v.getName() + ".");
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private boolean populateInstrument(Candidate c, Visit v, String instrumentLabel) throws InterruptedException {
		//Thread.sleep(100);
		Instrument in = new Instrument(instrumentLabel);
		v.addInstrument(in);
		modelCandidate.getVisitByName(v.getName()).addInstrument(in);
		try {
			URL url = new URL(this.url + API_EXTENSION + "candidates/" + c.getCandidateId() + "/" + v.getName() + "/instruments/" + instrumentLabel);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			initializeHttpUrlConnection(con);
			con.connect();

			JsonParser parser = new JsonParser();
			JsonElement tree = parser.parse(new InputStreamReader(con.getInputStream(), "UTF-8"));

			if (tree.isJsonObject()) {
				JsonObject obj = tree.getAsJsonObject();
				if (obj.get(instrumentLabel) != null) {
					JsonObject examiner = obj.get(instrumentLabel).getAsJsonObject();
					Set<Entry<String, JsonElement>> set = examiner.entrySet();
					for (Entry<String, JsonElement> e : set) {
						in.addField(e.getKey(), e.getValue().getAsString());
					}
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Nothing found for instrument " + in.getName() + ".");
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void initializeHttpUrlConnection(HttpURLConnection con) throws ProtocolException{
		con.setRequestMethod("GET");
		con.setRequestProperty("Content-Type","application/json");
		con.setRequestProperty("Authorization","Bearer "+token);
		con.setReadTimeout(15000);
	}

	public Candidate getModelCandidate() {
		return modelCandidate;
	}

	public void setModelCandidate(Candidate modelCandidate) {
		this.modelCandidate = modelCandidate;
	}
*/
}
