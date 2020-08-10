package org.dsher.loris.model.alerts;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class InvalidCertAlert extends Alert {
	
	private Exception e;
	
	public InvalidCertAlert(AlertType alertType, String contentText, ButtonType[] buttons, Exception e) {
		this(alertType, contentText, buttons);
		this.e = e;
		
	}

	public InvalidCertAlert(AlertType alertType, String contentText, ButtonType[] buttons) {
		super(alertType, contentText, buttons);
	}
	
	public void build() {
		this.setHeaderText("Invalid CA Certificate");
		this.setContentText("The given site has provided a self-signed or unsigned SSL Certificate. For now, you may choose to ignore this certificate and connect anyway, but you should add this server's certificate to your Java keystore if you trust it.");

		Label label = new Label("The exception stacktrace was:");

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		this.getDialogPane().setExpandableContent(expContent);
	}

}
