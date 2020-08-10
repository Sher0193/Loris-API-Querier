package org.dsher.loris.model.query.candidates;

import java.util.ArrayList;

public class Candidate {

	String candidateId;

	ArrayList<Instrument> instruments = new ArrayList<>();

	public Candidate(String id) {
		this.candidateId = id;
	}

	public ArrayList<Instrument> getInstruments() {
		return instruments;
	}

	public void addInstrument(Instrument instrument) {
		if (getInstrumentByName(instrument.getName()) == null)
			instruments.add(instrument);
	}

	public Instrument getInstrumentByName(String name) {
		for (Instrument instrument : instruments) {
			if (instrument.getName().equals(name)) {
				return instrument;
			}
		}
		return null;
	}

	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}
	
	public ArrayList<String> getCSVHeaders() {
		ArrayList<String> headers = new ArrayList<>();
		for (Instrument i : instruments) {
			for (String field : i.getFields().keySet()) {
				headers.add(i.getVisit() + "&&" + field);
			}
		}
		return headers;
	}

	public String getFieldByCSVHeader(String header) {
		String field = "";
		for (Instrument i : instruments) {
			field = i.getFieldByCSVHeader(header);
			if (!field.isEmpty())
				break;
		}
		return field;
	}

}
