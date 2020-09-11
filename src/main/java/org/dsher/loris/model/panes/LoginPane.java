package org.dsher.loris.model.panes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

import org.dsher.loris.Session;
import org.dsher.loris.model.alerts.InvalidCertAlert;
import org.dsher.loris.model.query.GetQuery;
import org.dsher.loris.utils.SSLUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class LoginPane extends GridPane {

	private final TextField siteTextField = new TextField();
	private final TextField userTextField = new TextField();
	private final PasswordField pwBox = new PasswordField();
	
	public LoginPane() {
		super();
	}

	public LoginPane build() {
		this.setAlignment(Pos.CENTER);
		this.setHgap(10);
		this.setVgap(10);
		this.setPadding(new Insets(25, 25, 25, 25));

		Text sceneTitle = new Text("Welcome");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		this.add(sceneTitle, 0, 0, 2, 1);

		Label site = new Label("Site Root:");
		this.add(site, 0, 1);

		siteTextField.setText("https://demo.loris.ca");
		this.add(siteTextField, 1, 1);

		Label userName = new Label("User Name:");
		this.add(userName,  0,  2);

		userTextField.setText("admin");
		this.add(userTextField, 1, 2);

		Label pw = new Label("Password:");
		this.add(pw, 0, 3);

		pwBox.setText("demo20!7");
		this.add(pwBox, 1, 3);

		Button btn = new Button("Authenticate");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		this.add(hbBtn, 1, 5);

		final Text actionTarget = new Text();
		actionTarget.setFill(Color.FIREBRICK);
		this.add(actionTarget, 1, 7);

		btn.setOnAction(new EventHandler<ActionEvent>() {

			@SuppressWarnings("deprecation")
			@Override
			public void handle(ActionEvent e) {
				String eMsg = login();
				if (eMsg != null) {
					actionTarget.setText(eMsg);
				}
				
			}
		});
		return this;
	}

	private static void initializeHttpUrlConnection(HttpURLConnection con) throws ProtocolException{
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setRequestProperty("Accept", "application/json");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setReadTimeout(15000);
	}
	
	/**
	 * Attempts to login and set pane to staging pane.
	 * @return Result string for outcome of login (null if successful or nothing useful to report to user)
	 */
	private String login() {
		JsonObject payloadJson = new JsonObject();
		payloadJson.addProperty("username", userTextField.getText());
		payloadJson.addProperty("password", pwBox.getText());
		String payload = payloadJson.toString();
		try {
			URLEncoder.encode(payload, "utf-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return null;
		}

		URL url;
		try {
			if (siteTextField.getText().charAt(siteTextField.getText().length() - 1) != '/')
				siteTextField.setText(siteTextField.getText() + "/");
			url = new URL(siteTextField.getText() + GetQuery.API_EXTENSION + "login");

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			initializeHttpUrlConnection(con);

			try {
				con.connect();
			} catch (SSLHandshakeException e1) {
				ButtonType ignore = new ButtonType("Ignore", ButtonBar.ButtonData.OK_DONE);
				ButtonType cancel = new ButtonType("Cancel Request", ButtonBar.ButtonData.CANCEL_CLOSE);

				ButtonType[] buttons = {ignore, cancel};

				InvalidCertAlert alert = new InvalidCertAlert(AlertType.WARNING, "Invalid CA Certificate", buttons, e1);

				alert.build();

				Optional<ButtonType> result = alert.showAndWait();
				if (result.orElse(cancel) != ignore){
					return null;   
				}

				// User has elected to trust the certificate, so now we'll just ignore certs

				// Install the all-trusting trust manager and redo the connection
				try {
					SSLUtils.trustAllCertificiates();
					con = (HttpsURLConnection) url.openConnection();
					initializeHttpUrlConnection(con);
					con.connect();

				} catch (GeneralSecurityException ex) {
					ex.printStackTrace();
					return null;
				} finally {
					// Now go back to default - we don't want to continue trusting every certificate
					try {
						SSLUtils.revertToDefaultCertificateTrust();
					} catch (GeneralSecurityException ex) {
						// Unfortunately we have to check for an exception again
						ex.printStackTrace();
						return null;
					} 
				}
			}

			try (OutputStream os = con.getOutputStream()) {
				byte[] input = payload.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);			
			}

			if (con.getResponseCode() == 500) {
				return "Server error at " + siteTextField.getText() + ".";
			} else if (con.getResponseCode() == 401) {
				return "Invalid username or password for this domain.";
			}

			JsonParser parser = new JsonParser();
			JsonElement tree = parser.parse(new InputStreamReader(con.getInputStream(), "utf-8"));

			if (tree.isJsonObject()) {
				JsonObject obj = tree.getAsJsonObject();

				if (obj.get("token") != null) {
					String token = obj.get("token").getAsString();
					Session.getInstance().setToken(token);
					Session.getInstance().setRoot(siteTextField.getText());
					Session.getInstance().queryProjects();
					return null;
				}
			}

		} catch (JsonSyntaxException e1) {
			return "Response did not look as expected, please double check your site url.";
		} catch (MalformedURLException e1) {
			return "Malformed URL.";
		} catch (SocketTimeoutException e1) {
			return "Request timed out.";
		} catch (FileNotFoundException e1) {
			return "Site does not appear to be a valid Loris instance.";
		} catch (UnknownHostException e1) {
			return "Unknown host.";
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return "Authentication error.";
	}
	

}
