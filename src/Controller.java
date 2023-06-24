import org.json.JSONArray;
import org.json.JSONObject;

public class Controller {

	public static void callGetCars(String[] makes, String[] exteriorColour, String[] bodyStyle, int rows,
			boolean imagesNeeded, JSONObject oauthKeys) {

		for (String make : makes) {
			JSONArray cars = Model.getCars(make, exteriorColour, bodyStyle, rows, imagesNeeded, oauthKeys);
			Model.writeJson(cars);
		}

	}

}
