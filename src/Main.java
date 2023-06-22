import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
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

	static Integer id = 0;

	public static void main(String[] args) {

		String[] makes = { "Toyota" };
		String[] exteriorColour = { "Blue", "Red", "Black", "White", "Silver" };
		JSONArray cars = getCars(makes, exteriorColour, "SUV", 30, false, readKeys());
		writeJson(cars);
	}

	public static JSONArray getCars(String[] make, String[] exteriorColour, String bodyStyle, int rows,
			boolean imagesNeeded, JSONObject oauthKeys) {
		Response response;
		// create a new array where the object will be stored
		JSONArray newCarsArray = new JSONArray();

		{

			try {
				// the Queries
				String baseUrl = "https://api.trademe.co.nz/v1/Search/Motors/Used.json";
				String condition = "Used";
				int priceMin = 5500;
				int yearMin = 2000;
				List<String> colourList = new ArrayList<>(Arrays.asList(exteriorColour));
				/*
				 * String[] Chassis = { "Hatchback", "Sedan", "Coupe", "SUV" }; List<String>
				 * chasisList = new ArrayList<>(Arrays.asList(exteriorColour));
				 */
				String sellerType = "Private";
				String photoSize = "FullSize";

				String consumerKey = oauthKeys.getString("consumerKey");
				String ConsumerSecret = oauthKeys.getString("consumerSecret");
				String oauthSignature = ConsumerSecret + "&";
				// Build the Authorization header
				String authorizationHeader = "OAuth " + "oauth_consumer_key=\"" + consumerKey + "\","
						+ "oauth_signature_method=\"PLAINTEXT\"," + "oauth_signature=\"" + oauthSignature + "\"";

				// make the url

				StringBuilder urlBuilder = new StringBuilder(baseUrl);
				urlBuilder.append("?");

				// get multiple colours
				for (String colour : exteriorColour) {
					urlBuilder.append("ExteriorColour=").append(colour).append("&");
				}
				/*
				 * for (String chassis : Chassis) {
				 * urlBuilder.append("body_style=").append(chassis).append("&"); }
				 */

				boolean firstMake = true;
				// add all make quries
				for (String makes : make) {
					if (firstMake) {
						urlBuilder.append("make=").append(makes);
						firstMake = false;
					} else {
						urlBuilder.append("&make=").append(makes);
					}

				}
				urlBuilder.append("&body_style=").append(bodyStyle).append("&condition=").append(condition)
						.append("&year_min=").append(yearMin).append("&price_min=").append(priceMin)
						.append("&listing_type=").append(sellerType).append("&photo_size=").append(photoSize)
						.append("&rows=").append(rows);

				String url = urlBuilder.toString();

				OkHttpClient client = new OkHttpClient().newBuilder().build();

				// the actual API

				Request request = new Request.Builder().url(url).addHeader("Authorization", authorizationHeader)
						.build();

				// make the call
				response = client.newCall(request).execute();

				if (response.isSuccessful()) {
					ResponseBody responseBody = response.body();
					if (responseBody != null) {
						String responseBodyString = responseBody.string();

						JSONObject allData = new JSONObject(responseBodyString);

						// get every item in the queried results
						for (Integer k = 0; k < rows; k++) {

							// Each new object:
							JSONObject car = new JSONObject();

							JSONObject individualCar = (JSONObject) allData.getJSONArray("List").get(k);

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
							// asign chassis ENUM
							String chassis = individualCar.getString("BodyStyle");
							if (chassis.equals("RV/SUV")) {
								chassis = "SUV";
							}

							// get the details
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
							car.put("images", k != null ? "" : "");

							if (imagesNeeded) {
								// get the images
								JSONArray allImages = null;
								try {
									allImages = new JSONArray(individualCar.getJSONArray("PhotoUrls"));
								} catch (JSONException e) {
									continue;
								}
								JSONArray carImages = new JSONArray();
								for (Integer j = 0; j < 3; j++) {
									String formatedModel = model.replaceAll("[-\\s]", "");

									String fileName = ("z_" + Make + formatedModel + id.toString() + j.toString())
											.toLowerCase();
									String resourceName = fileName;

									downloadImage(allImages.getString(j), fileName + ".jpg",
											"C:\\Users\\schoo\\OneDrive\\Documents\\Other\\Projects\\TradmeAPI_CarGetter\\src\\images");
									carImages.put(resourceName);
								}
								car.put("images", carImages);
							}
							newCarsArray.put(car);
							id++;

						}
						return newCarsArray;
					}

				}

			} catch (MalformedURLException e) {
				System.out.println("her1");
				throw new RuntimeException(e);
			} catch (UnsupportedEncodingException e) {
				System.out.println("her2");
				throw new RuntimeException(e);
			} catch (IOException e) {
				System.out.println("her3");
				throw new RuntimeException(e);
			} catch (JSONException e) {
				if (e.getMessage().contains("JSONObject[\"ExteriorColour\"] is not a string")) {
				}
			}
		}
		return newCarsArray;
	}

	public static void writeJson(JSONArray returnedCars) {
		// Read the existing JSON file
		String jsonFilePath = "C:\\Users\\schoo\\OneDrive\\Documents\\Other\\Projects\\TradmeAPI_CarGetter\\src\\recievedCars.json";
		try {
			String jsonString = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
			JSONArray updatedJsonFileArray;
			if (!jsonString.isEmpty()) {
				// Create a JSONArray or JSONObject
				updatedJsonFileArray = new JSONArray(jsonString);
			} else {
				updatedJsonFileArray = new JSONArray();
			}
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

	private static void downloadImage(String imageUrl, String fileName, String saveLocation) {
		// read the image data from the url
		try (BufferedInputStream inputStream = new BufferedInputStream(new URL(imageUrl).openStream())) {
			// get the file path object
			java.nio.file.Path filePath = Paths.get(saveLocation, fileName);
			// setup writer
			FileOutputStream fileOutputStream = new FileOutputStream(filePath.toString());

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
				// write to the file
				fileOutputStream.write(buffer, 0, bytesRead);
			}

			System.out.println("Image downloaded successfully: " + filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
