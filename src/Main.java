import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Main {

	public static void main(String[] args) {

		getCars(readKeys());
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

			Request request = new Request.Builder().url(
					"https://api.trademe.co.nz/v1/Search/Motors/Used.json?make=Toyota&body_style=Sedan&condition=Used&year_min=2000&photo_size=FullSize&rows=3")
					.addHeader("Authorization", authorizationHeader).build();

			response = client.newCall(request).execute();
			System.out.println(response);

			if (response.isSuccessful()) {
				ResponseBody responseBody = response.body();
				if (responseBody != null) {
					String responseBodyString = responseBody.string();

					JSONObject allData = new JSONObject(responseBodyString);
					JSONObject individualCar = (JSONObject) allData.getJSONArray("List").get(0);

					String make = individualCar.getString("Make");
					String model = individualCar.getString("Model");
					Integer year = individualCar.getInt("Year");
					Integer price = individualCar.getInt("StartPrice");
					String chassis = individualCar.getString("BodyStyle");

					String colour = individualCar.getString("ExteriorColour");
					System.out.println(make);
					System.out.println(model);
					System.out.println(year.toString());
					System.out.println(price.toString());
					System.out.println(chassis);
				}
			}

		} catch (IOException e) {

		}
		return null;

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
