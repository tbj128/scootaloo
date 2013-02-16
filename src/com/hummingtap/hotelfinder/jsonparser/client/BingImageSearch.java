package com.hummingtap.hotelfinder.jsonparser.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BingImageSearch implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	
	private Label errorMsgLabel = new Label();
	private Image tileImage = new Image();

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Button fetchDataButton = new Button("Fetch rainbow dash");
		fetchDataButton.addStyleName("sendButton");

		RootPanel.get("cloudside-inn").add(fetchDataButton);
		
		tileImage.setSize("200", "200");
		RootPanel.get("cloudside-inn").add(tileImage);
		
		fetchDataButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				fetchBingJSON("vancouver");
			}
		});
	}

	private void fetchBingJSON(String searchTerm) {
		System.out.println("FetchData");
		
		// URL to access the JSON search results
		String url = "https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Image?Query='" + searchTerm + "'&$top=5&$format=json";
		url = URL.encode(url);
		
		// Microsoft Azure Marketplace ID (may have to swap out after 5000 requests/month)
		String secret_id_bing = ":iGn/1fJnBbEHMmLSGb+hMx3O8bByKqGExGEI3s48wP4=";
		// Encode in base 64 (Bing requires this)
		String accountKeyEncoded = Base64.encode(secret_id_bing);
		
		// Builds request with appropriate authentication headers
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		builder.setHeader("Authorization", "Basic " + accountKeyEncoded);
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					displayError("Couldn't retrieve JSON");
				}

				public void onResponseReceived(Request request,
						Response response) {
					if (200 == response.getStatusCode()) {
						try {
							// JSON call returns appropriate object

							parseJsonData(response.getText());
							
							// updateTable(jsonArray);

							// Window.alert(response.getText());
						} catch (JSONException e) {
							displayError("Could not parse JSON");
						}
					} else {
						displayError("Couldn't retrieve JSON ("
								+ response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			displayError("Couldn't retrieve JSON");
		}
	}

	private void displayError(String error) {
		errorMsgLabel.setText(error);
		errorMsgLabel.setVisible(true);
	}

	/**
	 * Parses the JSON string
	 */
	private void parseJsonData(String json) {

		System.out.println("parser JSON");


		JSONValue value = JSONParser.parse(json);

		JSONObject searchObj = value.isObject();
		searchObj = searchObj.get("d").isObject();
		JSONArray searchArray = searchObj.get("results").isArray();
		
		JSONObject searchElement = searchArray.get(0).isObject();
		String imgSrc = searchElement.get("MediaUrl").isString().stringValue();
		tileImage.setUrl(imgSrc);
		
		}
	}

