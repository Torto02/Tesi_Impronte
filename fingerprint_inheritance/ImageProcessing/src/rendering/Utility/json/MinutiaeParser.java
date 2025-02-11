package rendering.Utility.json;

import org.json.JSONException;
import rendering.MinutiaeImages;
import rendering.Utility.FilesName;
import rendering.Utility.Minutia;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MinutiaeParser {

	public static List<Minutia> getMinutiae() throws IOException {
		List<Minutia> minutiaeList = new ArrayList<>();
		String filename = FilesName.getInstance().getMinutiaeFile();
		int typeMinutia = 0;

		File file = new File(filename);
		String content = FileUtils.readFileToString(file, "utf-8");

		// Convert JSON string to JSONObject
		JSONObject jsonObject = new JSONObject(content);

		JSONArray minutiae = (JSONArray) jsonObject.get("minutiae");

		for (int i = 0; i < minutiae.length(); ++i) {
			JSONObject minutia = minutiae.getJSONObject(i);
			int x = minutia.getInt("x");
			int y = minutia.getInt("y");
			double direction = minutia.getDouble("direction");
			String type = minutia.getString("type");
			int id;
			try{id = minutia.getInt("ID");}
			catch (JSONException e){
				id = 0;
			}

			if (type.equals("ending")) {
				typeMinutia = MinutiaeImages.ENDING;
			} else
				typeMinutia = MinutiaeImages.BIFURCATION;


			minutiaeList.add(new Minutia(x, y, direction, typeMinutia,id));
		}

		return minutiaeList;


	}
	
	
	 public static List<Minutia> getMinutiae(String filename) throws IOException {
		List<Minutia> minutiaeList = new ArrayList<>();
	//	String filename = FilesName.getInstance().getMinutiaeFile();
		int typeMinutia = 0;
		
		File file = new File(filename);
		String content = FileUtils.readFileToString(file, "utf-8");

		// Convert JSON string to JSONObject
		JSONObject jsonObject = new JSONObject(content);

		JSONArray minutiae = (JSONArray) jsonObject.get("minutiae");

		for (int i = 0; i < minutiae.length(); ++i) {
			JSONObject minutia = minutiae.getJSONObject(i);
			int x = minutia.getInt("x");
			int y = minutia.getInt("y");
			double direction = minutia.getDouble("direction");
			String type = minutia.getString("type");

			minutiaeList.add(new Minutia(x, y, direction, typeMinutia));
		}

		return minutiaeList;


	}
}