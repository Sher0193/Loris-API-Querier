package org.dsher.loris;

import java.util.ArrayList;

import org.dsher.loris.model.panes.LoginPane;
import org.dsher.loris.model.panes.QueryPane;
import org.dsher.loris.model.panes.ResultPane;
import org.dsher.loris.model.panes.StagingPane;
import org.dsher.loris.model.query.candidates.Candidate;
import org.dsher.loris.model.query.impl.QueryInstruments;
import org.dsher.loris.model.query.impl.QueryProjects;
import org.dsher.loris.model.query.projects.Project;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Session {
	
	private static Session session_instance = null;
	
	private Stage stage;
	private String token = "";
	private String root = "";
	
	private Session() {

	}
	
	public static Session getInstance() {
		if (session_instance == null) {
			session_instance = new Session();
		}
		return session_instance;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public void queryProjects() {
		QueryPane pane = new QueryPane("Querying projects...").build();
		QueryProjects query = new QueryProjects(this.root, this.token, pane);
		showQueryPane(pane);
		Thread thread = new Thread(query);
		thread.start();
	}
	
	public void queryInstruments(String[] endpoints) {
		QueryPane pane = new QueryPane("Querying data...").build();
		QueryInstruments query = new QueryInstruments(this.root, this.token, pane, endpoints);
		showQueryPane(pane);
		Thread thread = new Thread(query);
		thread.start();
	}
	
	private void showQueryPane(QueryPane pane) {
		Scene scene = new Scene(pane, 640, 480);
		stage.setTitle("Querying...");
		stage.setScene(scene);
		stage.show();
	}
	
	public void showLoginPane() {
		Scene scene = new Scene(new LoginPane().build(), 640, 480);
        stage.setTitle("Loris Querier - Login");
        stage.setScene(scene);
        stage.show();
	}
	
	@SuppressWarnings("exports")
	public void showStagingPane(ArrayList<Project> results) {
		Scene scene = new Scene(new StagingPane(results).build(), 640, 480);
		stage.setTitle("Loris Querier - Staging");
        stage.setScene(scene);
        stage.show();
	}
	
	@SuppressWarnings("exports")
	public void showResultPane(ArrayList<Candidate> results) {
		Scene scene = new Scene(new ResultPane(results).build(), 640, 480);
		stage.setTitle("Loris Querier - Query Results");
		stage.setScene(scene);
		stage.show();
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

}
