package rendering.Utility.json;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import rendering.Utility.MyEdge;
import rendering.Utility.MyEdgeSCC;
import rendering.Utility.MyPoint;
import rendering.Utility.Ridge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RidgesParser {

    public static List<Ridge> getRidges(String filename) throws IOException {
        List<Ridge> ridgesList = new ArrayList<>();

        File file = new File(filename);
        String content = FileUtils.readFileToString(file, "utf-8");

        // Convert JSON string to JSONObject
        JSONObject jsonObject = new JSONObject(content);

        JSONArray ridges = (JSONArray) jsonObject.get("ridges");

        for (int i = 0; i < ridges.length(); ++i) {
            JSONObject ridgesJSONObject = ridges.getJSONObject(i);
            int id1 = ridgesJSONObject.getInt("ID1");
            int id2 = ridgesJSONObject.getInt("ID2");
            JSONArray pixels = (JSONArray) ridgesJSONObject.get("pixels");
            List<MyPoint> pixelsList = new ArrayList<>();

            for (int j = 0; j < pixels.length(); ++j) {
                JSONObject pixelJSONObject = pixels.getJSONObject(j);
                pixelsList.add(new MyPoint(pixelJSONObject.getInt("X"), pixelJSONObject.getInt("Y")));
            }

            ridgesList.add(new Ridge(id1,id2,pixelsList));
        }

        return ridgesList;
    }

}
