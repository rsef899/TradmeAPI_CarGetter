import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

public class Model {

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

	static void downloadImage(String imageUrl, String fileName, String saveLocation) {
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
