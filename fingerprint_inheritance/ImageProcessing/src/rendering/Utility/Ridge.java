package rendering.Utility;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Ridge {

    public static int foundedColor = 0;


    private int id1;
    private int id2;
    private List<MyPoint> pixels;
    private int colorCode;

    public int getTotalColoredRidges() {
        return totalColoredRidges;
    }

    private int totalColoredRidges;


    public int getColorCode() {
        return colorCode;
    }

    public void setColorCode() {
        List<Ridge> ridgesList = new ArrayList<>();

        File file = new File(FilesName.getInstance().getRidgesColorFile());
        String content = null;
        try {
            content = FileUtils.readFileToString(file, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert JSON string to JSONObject
        JSONObject jsonObject = new JSONObject(content);

        JSONArray ridges = (JSONArray) jsonObject.get("ridges");
        totalColoredRidges = ridges.length();
       for (int i = 0; i < ridges.length(); ++i) {
            JSONObject ridgesJSONObject = ridges.getJSONObject(i);
            int local_id1 = ridgesJSONObject.getInt("ID1");
            int local_id2 = ridgesJSONObject.getInt("ID2");
            if ((local_id1 == this.id1 && local_id2 == this.id2) ||
                    (local_id1 == this.id2 && local_id2 == this.id1)){
                i = ridges.length();
               this.colorCode = ridgesJSONObject.getInt("color");
               // foundedColor++;
                //this.colorCode = foundedColor;

            }
            else {
                this.colorCode = 0;
            }
        }
    }


    public int getId1() {
        return id1;
    }

    public void setId1(int id1) {
        this.id1 = id1;
    }

    public int getId2() {
        return id2;
    }

    public void setId2(int id2) {
        this.id2 = id2;
    }

    public List<MyPoint> getPixels() {
        return pixels;
    }

    public void setPixels(List<MyPoint> pixels) {
        this.pixels = pixels;
    }

    public Ridge(int id1, int id2, List<MyPoint> pixels) {
        this.id1 = id1;
        this.id2 = id2;
        this.pixels = pixels;
        setColorCode();
    }

    public void print(){
        System.out.println("ID1: " + getId1());
        System.out.println("ID2: " + getId2());
        System.out.println("pixels: ");

        for (int i = 0; i < pixels.size(); i++){
            System.out.println("\t{X: " + pixels.get(i).getX() + ", Y:" + pixels.get(i).getY() + "}");
        }

    }

}


