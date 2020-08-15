package org.dsher.loris.model.panes;

import java.util.ArrayList;
import java.util.Arrays;

import org.controlsfx.control.CheckComboBox;
import org.dsher.loris.Session;
import org.dsher.loris.model.query.projects.Project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StagingPane extends GridPane {

	/*private final TextArea idsField = new TextArea();
	private final ToggleGroup idsType = new ToggleGroup();*/

	private ArrayList<CheckComboBox<String>> instrumentNodes = new ArrayList<>();

	private ArrayList<ListView<String>> selectedListNodes = new ArrayList<>();

	private ArrayList<VBox> projectNodes = new ArrayList<>(); 

	private ArrayList<Project> results;

	private String curProject;

	public StagingPane(ArrayList<Project> results) {
		super();
		this.results = results;
	}

	public StagingPane build() {

		this.setAlignment(Pos.CENTER);
		this.setHgap(10);
		this.setVgap(10);
		this.setPadding(new Insets(25, 25, 25, 25));

		final ObservableList<String> projectLabels = FXCollections.observableArrayList();

		for (Project project : results) {
			projectLabels.add(project.getName());

			final VBox vbox = new VBox(10);
			final GridPane pane = new GridPane();
			pane.setAlignment(Pos.CENTER);
			pane.setHgap(10);
			pane.setVgap(10);
			pane.setId(project.getName());

			vbox.setId(project.getName());
			vbox.getChildren().add(pane);
			projectNodes.add(vbox);

			final ObservableList<String> visitLabels = FXCollections.observableArrayList();
			for (String visit : project.getVisits()) {
				visitLabels.add(visit);
				ObservableList<String> instrumentLabels = FXCollections.observableArrayList();
				for (String instrument : project.getInstruments()) {
					instrumentLabels.add(instrument);
				}
				final CheckComboBox<String> instrumentsBox = new CheckComboBox<>(instrumentLabels);
				instrumentsBox.setId(visit);
				instrumentsBox.setMaxWidth(200);
				instrumentsBox.setVisible(false);
				pane.add(instrumentsBox, 1, 0, 2, 1);
				instrumentNodes.add(instrumentsBox);
			}
			final ComboBox<String> visitsBox = new ComboBox<>(visitLabels);
			visitsBox.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
				setInstrumentVisibility(newValue);
			}); 
			visitsBox.setPromptText("Visits");
			visitsBox.setId("visits-combo-box");
			pane.add(visitsBox, 0, 0);

			final Label candidatesLbl = new Label("Candidates");
			GridPane.setHalignment(candidatesLbl, HPos.CENTER);
			pane.add(candidatesLbl, 0, 2);

			final Label selectedLbl = new Label("Selected");
			pane.add(selectedLbl, 2, 2);
			GridPane.setHalignment(selectedLbl, HPos.CENTER);

			final ObservableList<String> candidates = FXCollections.observableArrayList();
			for (String candidate : project.getCandidates()) {
				candidates.add(candidate);
			}
			final ListView<String> candidatesListView = new ListView<>(candidates);
			candidatesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			pane.add(candidatesListView, 0, 3);

			final ObservableList<String> selected = FXCollections.observableArrayList();
			final ListView<String> selectedListView = new ListView<>(selected);
			selectedListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			selectedListNodes.add(selectedListView);
			pane.add(selectedListView, 2, 3);

			final Button sendRightButton = new Button(" > ");
			sendRightButton.setOnAction((ActionEvent event) -> {
				ObservableList<String> selections = candidatesListView.getSelectionModel().getSelectedItems();

				String[] potentials = new String[selections.size()];

				for (int i = 0; i < potentials.length; i++) {
					potentials[i] = selections.get(i);
				}

				for (String potential : potentials) {
					if (potential != null) {
						candidates.remove(potential);
						selected.add(potential);
					}
					candidatesListView.getSelectionModel().clearSelection();
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
						candidates.add(potential);
					}
					selectedListView.getSelectionModel().clearSelection();
				}
			});

			final VBox btnBox = new VBox(5);
			btnBox.getChildren().addAll(sendRightButton, sendLeftButton);

			pane.add(btnBox, 1, 3);

			this.add(vbox, 0, 1);
			vbox.setVisible(false);

		}

		final ComboBox<String> projectsBox = new ComboBox<>(projectLabels);
		projectsBox.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			setProjectVisibility(newValue);
		}); 
		projectsBox.setPromptText("Projects");
		this.add(projectsBox, 0, 0);

		Button query = new Button("Query");
		Button logout = new Button("Log Out");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_LEFT);
		hbBtn.getChildren().add(query);
		hbBtn.getChildren().add(logout);
		this.add(hbBtn, 0, 3);

		query.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				query.setDisable(true);
				ArrayList<String> endpointsAL = new ArrayList<>();
				ArrayList<String> instrumentsAL = new ArrayList<>();

				for (CheckComboBox<String> instrumentNode : instrumentNodes) {
					if (!instrumentNode.getParent().getId().equals(getCurProject())) {
						continue;
					}
					for (String selected : instrumentNode.getCheckModel().getCheckedItems()) {
						instrumentsAL.add(instrumentNode.getId() + "/instruments/" + selected);
					}
				}

				for (ListView<String> listViewNode : selectedListNodes) {
					if (!listViewNode.getParent().getId().equals(getCurProject())) {
						continue;
					}
					for (String selected : listViewNode.getItems()) {
						for (String instEP : instrumentsAL) {
							endpointsAL.add("candidates/" + selected + "/" + instEP);
						}
					}
				}
				
				Object[] arr = endpointsAL.toArray();
				String[] endpoints = Arrays.copyOf(arr, arr.length, String[].class);
				
				Session.getInstance().queryInstruments(endpoints);
			}
		});

		logout.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				logout.setDisable(true);
				Session.getInstance().setRoot("");
				Session.getInstance().setToken("");
				Session.getInstance().setProjects(null);
				Session.getInstance().showLoginPane();
			}

		});

		return this;
	}

	private void setProjectVisibility(String projectLabel) {
		this.curProject = projectLabel;
		for (VBox vbox : projectNodes) {
			if (vbox.getId().equals(projectLabel)) {
				vbox.setVisible(true);
			} else {
				vbox.setVisible(false);
			}
		}
	}

	private void setInstrumentVisibility(String visitLabel) {
		for (CheckComboBox<String> i : instrumentNodes) {
			if (i.getId().equals(visitLabel) && i.getParent().getId().equals(this.curProject)) {
				i.setVisible(true);
			} else {
				i.setVisible(false);
			}
		}
	}

	private String getCurProject() {
		return this.curProject;
	}

}
