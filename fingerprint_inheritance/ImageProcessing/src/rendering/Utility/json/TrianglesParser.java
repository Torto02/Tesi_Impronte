package rendering.Utility.json;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import rendering.Utility.MyEdge;
import rendering.Utility.MyEdgeSCC;
import rendering.Utility.MyPoint;
import rendering.Utility.Triangle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrianglesParser {

    public static List<Triangle> getTriangles(String filename) throws IOException {
        List<Triangle> trianglesList = new ArrayList<>();

        File file = new File(filename);
        String content = FileUtils.readFileToString(file, "utf-8");

        JSONObject jsonObject = new JSONObject(content);

        JSONArray triangles = (JSONArray) jsonObject.get("triangles");

        for (int i = 0; i < triangles.length(); ++ i) {
            JSONObject triangleJSONObject = triangles.getJSONObject(i);
            JSONArray points = (JSONArray) triangleJSONObject.get("points");
            List<MyPoint> pointsList = new ArrayList<>();

            for (int j = 0; j < points.length(); ++ j) {
                JSONObject pointJSONObject = points.getJSONObject(j);
                pointsList.add(new MyPoint(pointJSONObject.getInt("X"), pointJSONObject.getInt("Y")));
            }

            trianglesList.add(new Triangle(pointsList));
        }

        return trianglesList;
    }
}
