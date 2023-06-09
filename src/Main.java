import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
			MediaType mediaType = MediaType.parse("text/plain");
			RequestBody body = RequestBody.create(mediaType, "");

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
			Integer idi = 0;
			String responseBodyString = null;
			if (response.isSuccessful()) {
				ResponseBody responseBody = response.body();
				responseBodyString = responseBody.string();
			}

			return new JSONObject(responseBodyString);
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
