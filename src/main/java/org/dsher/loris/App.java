package org.dsher.loris;

import javafx.application.Application;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
    	Session session = Session.getInstance();
    	session.setStage(stage);
    	session.showLoginPane();
    }

    public static void main(String[] args) {
        launch();
    }

}