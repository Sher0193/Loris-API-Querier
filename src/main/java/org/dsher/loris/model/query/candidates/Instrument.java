package org.dsher.loris.model.query.candidates;

import java.util.HashMap;

public class Instrument {
	
	private HashMap<String, String> fields = new HashMap<>();
	
	private String visit;
	
	private String name;
	
	public Instrument(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, String> getFields() {
		return fields;
	}
	
	public void addField(String key, String content) {
		fields.put(key, content);
	}
	
	public String getField(String key) {
		return fields.get(key);
	}

	public String getVisit() {
		return visit;
	}

	public void setVisit(String visit) {
		this.visit = visit;
	}

	public String getFieldByCSVHeader(String header) {
		String[] parts = header.split("&&");
		String visit = parts[0], fieldName = parts[1];
		String value = "";
		if (visit.equals(this.visit)) {
			if (getField(fieldName) != null) {
				value = getField(fieldName);
			}
		}
		return value;
	}

}
