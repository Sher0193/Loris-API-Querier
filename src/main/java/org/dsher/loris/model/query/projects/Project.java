package org.dsher.loris.model.query.projects;

import java.util.ArrayList;

public class Project {

	private ArrayList<String> visits = new ArrayList<>();
	private ArrayList<String> instruments = new ArrayList<>();
	private ArrayList<String> candidates = new ArrayList<>();

	private String name;

	public Project(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getVisits() {
		return visits;
	}

	public void setVisits(ArrayList<String> visits) {
		this.visits = visits;
	}

	public String getVisitByName(String visitName) {
		for (String visit: visits) {
			if (visit.equals(visitName))
				return visit;
		}
		return null;
	}

	public void addVisit(String visit) {
		if (getVisitByName(visit) == null)
			this.visits.add(visit);
	}

	public ArrayList<String> getInstruments() {
		return instruments;
	}

	public void setInstruments(ArrayList<String> instruments) {
		this.instruments = instruments;
	}
	
	public String getInstrumentByName(String instrumentName) {
		for (String instrument: instruments) {
			if (instrument.equals(instrumentName))
				return instrument;
		}
		return null;
	}

	public void addInstrument(String instrument) {
		if (getInstrumentByName(instrument) == null)
			this.instruments.add(instrument);
	}

	public ArrayList<String> getCandidates() {
		return candidates;
	}

	public void setCandidates(ArrayList<String> candidates) {
		this.candidates = candidates;
	}
	
	public String getCandidateByName(String candidateName) {
		for (String candidate: candidates) {
			if (candidate.equals(candidateName))
				return candidate;
		}
		return null;
	}

	public void addCandidate(String candidate) {
		if (getCandidateByName(candidate) == null)
			this.candidates.add(candidate);
	}

}
