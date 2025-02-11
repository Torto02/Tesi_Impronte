package rendering.Utility.json;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import rendering.Utility.MyEdge;
import rendering.Utility.MyEdgeSCC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EdgeSCCParser {

    public static List<MyEdgeSCC> getEdgesSCC(String filename) throws IOException {
        List<MyEdgeSCC> edgesSCCList = new ArrayList<>();

        File file = new File(filename);
        String content = FileUtils.readFileToString(file, "utf-8");

        // Convert JSON string to JSONObject
        JSONObject jsonObject = new JSONObject(content);

        JSONArray edges = (JSONArray) jsonObject.get("edges");

        for (int i = 0; i < edges.length(); ++i) {
            JSONObject edgeSCC = edges.getJSONObject(i);
            int id1 = edgeSCC.getInt("ID1");
            int id2 = edgeSCC.getInt("ID2");
            int color = edgeSCC.getInt("color");

            edgesSCCList.add(new MyEdgeSCC(id1,id2,color));
        }

        return edgesSCCList;
    }

    public static MyEdgeSCC getEdgeSCC(String filename, int index) throws IOException {

        File file = new File(filename);
        String content = FileUtils.readFileToString(file, "utf-8");

        // Convert JSON string to JSONObject
        JSONObject jsonObject = new JSONObject(content);

        JSONArray edges = (JSONArray) jsonObject.get("edges");

        JSONObject edgeSCC = edges.getJSONObject(index);
        int id1 = edgeSCC.getInt("ID1");
        int id2 = edgeSCC.getInt("ID2");
        int color = edgeSCC.getInt("color");

        return new MyEdgeSCC(id1,id2,color);

    }

    public static int edgesSCCCount(String filename) throws IOException {
        List<MyEdge> edgesList = new ArrayList<>();

        File file = new File(filename);
        String content = FileUtils.readFileToString(file, "utf-8");

        // Convert JSON string to JSONObject
        JSONObject jsonObject = new JSONObject(content);

        JSONArray edgesSCC = (JSONArray) jsonObject.get("edges");

        return edgesSCC.length();

    }
}
