package org.dsher.loris.model.panes;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.dsher.loris.Session;
import org.dsher.loris.model.query.candidates.Candidate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ResultPane extends GridPane {

	private final ArrayList<Candidate> results;

	public ResultPane(ArrayList<Candidate> results) {
		super();
		this.results = results;
	}

	public ResultPane build() {

		this.setAlignment(Pos.CENTER);
		this.setHgap(10);
		this.setVgap(10);
		this.setPadding(new Insets(25, 25, 25, 25));

		ArrayList<String> csvHeaders = new ArrayList<>();
		for (Candidate candidate : results) 
			for (String header : candidate.getCSVHeaders()) {
				boolean found = false;
				for (String existing : csvHeaders) {
					if (header.equals(existing)) {
						found = true;
						break;
					}
				}
				if (!found)
					csvHeaders.add(header);
			}


		final Label candidatesLbl = new Label("Headers");
		GridPane.setHalignment(candidatesLbl, HPos.CENTER);
		this.add(candidatesLbl, 0, 0);

		final Label selectedLbl = new Label("Selected");
		this.add(selectedLbl, 2, 0);
		GridPane.setHalignment(selectedLbl, HPos.CENTER);

		final ObservableList<String> headers = FXCollections.observableArrayList();
		for (String csvHeader : csvHeaders) {
			headers.add(csvHeader);
		}
		final ListView<String> headersListView = new ListView<>(headers);
		headersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.add(headersListView, 0, 1);

		final ObservableList<String> selected = FXCollections.observableArrayList();
		final ListView<String> selectedListView = new ListView<>(selected);
		selectedListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.add(selectedListView, 2, 1);

		final Button sendRightButton = new Button(" > ");
		sendRightButton.setOnAction((ActionEvent event) -> {
			ObservableList<String> selections = headersListView.getSelectionModel().getSelectedItems();

			String[] potentials = new String[selections.size()];

			for (int i = 0; i < potentials.length; i++) {
				potentials[i] = selections.get(i);
			}

			for (String potential : potentials) {
				if (potential != null) {
					headers.remove(potential);
					selected.add(potential);
				}
				headersListView.getSelectionModel().clearSelection();
			}
		});
		final Button sendLeftButton = new Button(" < ");
		sendLeftButton.setOnAction((ActionEvent event) -> {
			ObservableList<String> selections = selectedListView.getSelectionModel().getSelectedItems();

			String[] potentials = new String[selections.size()];

			for (int i = 0; i < potentials.length; i++) {
				potentials[i] = selections.get(i);
			}

			for (String potential : potentials) {
				if (potential != null) {
					selected.remove(potential);
					headers.add(potential);
				}
				selectedListView.getSelectionModel().clearSelection();
			}
		});

		final VBox btnBox = new VBox(5);
		btnBox.getChildren().addAll(sendRightButton, sendLeftButton);

		this.add(btnBox, 1, 1);

		final Button query = new Button("New Query");
		final Button csv = new Button("Generate CSV File");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_LEFT);
		hbBtn.getChildren().add(query);
		hbBtn.getChildren().add(csv);
		this.add(hbBtn, 0, 2);
		
		query.setOnAction((ActionEvent event) -> {
			Session.getInstance().showStagingPane();		
		});

		csv.setOnAction((ActionEvent event) -> {

			//csv.setDisable(true);
			String file = "candidate, ";

			//ObservableList<String> selections = ;

			String[] headersArr = new String[selected.size()];

			for (int i = 0; i < headersArr.length; i++) {
				headersArr[i] = selected.get(i);
				file += headersArr[i];
				if (i != headersArr.length - 1) {
					file += ", ";
				} else {
					file += "\n";
				}
			}

			for (Candidate candidate : results) {
				file += candidate.getCandidateId();
				for (String header : headersArr) {
					file += ", " + candidate.getFieldByCSVHeader(header);
				}
				file += "\n";
			}

			try (PrintStream out = new PrintStream(new FileOutputStream("output.csv"))) {
				out.print(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}



		});

		return this;
	}



}
