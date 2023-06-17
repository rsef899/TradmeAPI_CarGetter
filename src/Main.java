import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Main {

	public static void main(String[] args) {

		JSONObject cars = getCars(readKeys());
	}

	public static JSONObject getCars(JSONObject oauthKeys) {
		try {
			// The response
			Response response;

			OkHttpClient client = new OkHttpClient().newBuilder().build();

			String consumerKey = oauthKeys.getString("consumerKey");
			String ConsumerSecret = oauthKeys.getString("consumerSecret");
			String oauthSignature = ConsumerSecret + "&";
			// Build the Authorization header
			String authorizationHeader = "OAuth " + "oauth_consumer_key=\"" + consumerKey + "\","
					+ "oauth_signature_method=\"PLAINTEXT\"," + "oauth_signature=\"" + oauthSignature + "\"";

			// the actual API
			String baseUrl = "https://api.trademe.co.nz/v1/Search/Motors/Used.json";
			String make = "Toyota";
			String bodyStyle = "Sedan";
			String condition = "Used";
			int yearMin = 2000;
			String[] exteriorColour = { "Blue", "Red", "Black", "White", "Silver" };
			List<String> colourList = new ArrayList<>(Arrays.asList(exteriorColour));
			String[] Chassis = { "Hatchback", "Sedan", "Coupe", "SUV" };
			List<String> chasisList = new ArrayList<>(Arrays.asList(exteriorColour));
			String photoSize = "FullSize";
			int rows = 99;

			StringBuilder urlBuilder = new StringBuilder(baseUrl);
			urlBuilder.append("?");

			// get multiple colours
			for (String colour : exteriorColour) {
				urlBuilder.append("ExteriorColour=").append(colour).append("&");
			}
			for (String chassis : Chassis) {
				urlBuilder.append("body_style=").append(chassis).append("&");
			}

			// make the url
			urlBuilder.append("make=").append(make).append("&condition=").append(condition).append("&year_min=")
					.append(yearMin).append("&photo_size=").append(photoSize).append("&rows=").append(rows);

			String url = urlBuilder.toString();

			Request request = new Request.Builder().url(url).addHeader("Authorization", authorizationHeader).build();

			response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				ResponseBody responseBody = response.body();
				if (responseBody != null) {
					String responseBodyString = responseBody.string();

					JSONObject allData = new JSONObject(responseBodyString);
					// create a new array where the object will be stored
					JSONArray newCarsArray = new JSONArray();

					for (Integer id = 0; id < rows - 1; id++) {
						// Each new object:
						JSONObject car = new JSONObject();

						JSONObject individualCar = (JSONObject) allData.getJSONArray("List").get(id);

						// get the car details
						String colour = "";
						try {
							colour = individualCar.getString("ExteriorColour");
							// if odd case of random colour, skip it
							if (!colourList.contains(colour)) {
								continue;
							}
						}
						// if null colour skip it
						catch (JSONException e) {
							continue;

						}

						// get the details
						String description;
						String chassis = individualCar.getString("BodyStyle");
						Integer price = individualCar.getInt("StartPrice");
						Integer year = individualCar.getInt("Year");
						String model = individualCar.getString("Model");
						String Make = individualCar.getString("Make");

						// assign the car details
						car.put("colour", colour != null ? colour : "");
						car.put("chassis", chassis != null ? chassis : "");
						car.put("price", price != null ? price : 0);
						car.put("year", year != null ? year : 0);
						car.put("model", model != null ? model : "");
						car.put("make", Make != null ? Make : "");
						car.put("ID", id != null ? id.toString() : "");
						newCarsArray.put(car);
					}
				}
			}

		} catch (IOException e) {

		}
		return null;

	}

	public static void writeJson(JSONArray returnedCars) {
		// Read the existing JSON file
		String jsonFilePath = "C:\\Users\\schoo\\OneDrive\\Documents\\Other\\Projects\\TradmeAPI_CarGetter\\src\\recievedCars.json";
		try {
			String jsonString = new String(Files.readAllBytes(Paths.get(jsonFilePath)));

			// Create a JSONArray or JSONObject from the JSONTokener
			JSONArray updatedJsonFileArray = new JSONArray(jsonString);

			// add the new cars to the file
			for (int i = 0; i < returnedCars.length(); i++) {
				JSONObject movignCar = returnedCars.getJSONObject(i);
				updatedJsonFileArray.put(movignCar);
			}

			String stringUpdatedJsonFileArray = updatedJsonFileArray.toString();

			// Write the updated JSON string back to the file
			FileWriter fileWriter = new FileWriter(jsonFilePath);
			fileWriter.write(stringUpdatedJsonFileArray);
			fileWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public static JSONObject readKeys() {
		String jsonFilePath = "C:\\Users\\schoo\\OneDrive\\Documents\\Other\\Projects\\TradmeAPI_CarGetter\\src\\keys.json";

		try {
			FileReader fr = new FileReader(jsonFilePath);

			BufferedReader bufferedReader = new BufferedReader(fr);
			StringBuilder jsonKeyString = new StringBuilder();
			String line;

			// get the JSON string
			while ((line = bufferedReader.readLine()) != null) {
				jsonKeyString.append(line);
			}
			bufferedReader.close();

			// convert to a JSON object
			JSONObject keys = new JSONObject(jsonKeyString.toString());

			return keys;

		} catch (IOException e) {
			// TODO: Handle no key exception
			System.out.println("No Keys Found!");
			return null;
		}
	}

}
