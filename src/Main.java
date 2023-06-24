import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

	public static void main(String[] args) {

		String[] exteriorColour = { "Blue", "Red", "Black", "White", "Silver" };
		String[] makes = { "Audi", "BMW" };
		String[] bodyStyle = { "Coupe", "Hatchback" };
		Controller.callGetCars(makes, exteriorColour, bodyStyle, 31, false, readKeys());
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

}
