package rendering.Utility;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Triangle {
    private List<MyPoint> points;

    public List<MyPoint> getPoints() {
        return points;
    }

    public void setPoints(List<MyPoint> points) {
        this.points = points;
    }

    public Triangle(List<MyPoint> points) {
        this.points = points;
    }
}
