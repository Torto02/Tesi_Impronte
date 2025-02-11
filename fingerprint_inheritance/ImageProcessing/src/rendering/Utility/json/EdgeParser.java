package rendering.Utility.json;

import rendering.Utility.MyEdge;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EdgeParser {

	public static List<MyEdge> getEdges(String filename) throws IOException {
		List<MyEdge> edgesList = new ArrayList<>();

		File file = new File(filename);
		String content = FileUtils.readFileToString(file, "utf-8");

		// Convert JSON string to JSONObject
		JSONObject jsonObject = new JSONObject(content);

		JSONArray edges = (JSONArray) jsonObject.get("edges");

		for (int i = 0; i < edges.length(); ++i) {
			JSONObject edge = edges.getJSONObject(i);
			int x1 = edge.getInt("x1");
			int y1 = edge.getInt("y1");
			int x2 = edge.getInt("x2");
			int y2 = edge.getInt("y2");
			int id1 = edge.getInt("ID1");
			int id2 = edge.getInt("ID2");

			edgesList.add(new MyEdge(x1, y1, x2, y2, id1, id2));
		}

		return edgesList;
	}

	public static MyEdge getEdge(String filename, int index) throws IOException {

		File file = new File(filename);
		String content = FileUtils.readFileToString(file, "utf-8");

		// Convert JSON string to JSONObject
		JSONObject jsonObject = new JSONObject(content);

		JSONArray edges = (JSONArray) jsonObject.get("edges");


			JSONObject edge = edges.getJSONObject(index);
			int x1 = edge.getInt("x1");
			int y1 = edge.getInt("y1");
			int x2 = edge.getInt("x2");
			int y2 = edge.getInt("y2");
			int id1 = edge.getInt("ID1");
			int id2 = edge.getInt("ID2");

		return new MyEdge(x1, y1, x2, y2, id1, id2);

	}

	public static int edgesCount(String filename) throws IOException {
		List<MyEdge> edgesList = new ArrayList<>();

		File file = new File(filename);
		String content = FileUtils.readFileToString(file, "utf-8");

		// Convert JSON string to JSONObject
		JSONObject jsonObject = new JSONObject(content);

		JSONArray edges = (JSONArray) jsonObject.get("edges");

		return edges.length();

	}
}
