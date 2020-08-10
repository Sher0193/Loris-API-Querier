package org.dsher.loris.model.panes;

import java.util.ArrayList;

import org.dsher.loris.model.query.candidates.Candidate;

import javafx.scene.layout.GridPane;

public class ResultPane extends GridPane {

	private ArrayList<Candidate> results;

	public ResultPane(ArrayList<Candidate> results) {
		super();
		this.results = results;
	}

	public ResultPane build() {
		
		return this;
	}
	
	

}
