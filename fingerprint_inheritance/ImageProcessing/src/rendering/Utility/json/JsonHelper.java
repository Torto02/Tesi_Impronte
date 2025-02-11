package rendering.Utility.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import rendering.Utility.FilesName;

import java.io.FileReader;
import java.io.IOException;

import static java.lang.Math.toIntExact;

public class JsonHelper {

	public static int getHeight() throws IOException, ParseException {
		JSONParser parser = new JSONParser();

		Object obj = parser.parse(new FileReader(FilesName.getInstance().getImageFile()));
		JSONObject jsonObject = (JSONObject) obj;

		JSONArray dimensions = (JSONArray) jsonObject.get("dimensions");

		return toIntExact((Long) dimensions.get(0));

	}

	public static int getWidth() throws IOException, ParseException {
		JSONParser parser = new JSONParser();

		Object obj = parser.parse(new FileReader(FilesName.getInstance().getImageFile()));
		JSONObject jsonObject = (JSONObject) obj;

		JSONArray dimensions = (JSONArray) jsonObject.get("dimensions");
		return toIntExact((Long) dimensions.get(1));
	}

}