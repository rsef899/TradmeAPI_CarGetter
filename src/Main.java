import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;

public class Main {

	public static void main(String[] args) {
		readKeys();

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
