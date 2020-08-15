package org.dsher.loris.model.query;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import org.dsher.loris.model.panes.QueryPane;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public abstract class GetQuery implements Runnable {
	
	public static final String API_EXTENSION = "api/v0.0.3/";
	
	protected String url, token;
	
	protected QueryPane parentPane;
	
	public GetQuery(String url, String token, QueryPane parentPane) {
		this.url = url;
		this.token = token;
		this.parentPane = parentPane;
	}
	
	protected void initializeHttpUrlConnection(HttpURLConnection con) throws ProtocolException{
		con.setRequestMethod("GET");
		con.setRequestProperty("Content-Type","application/json");
		con.setRequestProperty("Authorization","Bearer " + token);
		con.setReadTimeout(15000);
	}
	
	@SuppressWarnings("deprecation")
	protected JsonElement parseJsonFromConnection(HttpURLConnection con) throws JsonIOException, JsonSyntaxException, UnsupportedEncodingException, IOException {
		JsonParser parser = new JsonParser();
		return parser.parse(new InputStreamReader(con.getInputStream(), "UTF-8"));
	}
	
	protected void notifyProgressBar (double progress) {
		parentPane.updateProgressBar(progress);
	}

}
