package rendering;

import rendering.Utility.FilesName;
import rendering.Utility.MyEdge;
import rendering.Utility.MyEdgeSCC;
import rendering.Utility.json.EdgeParser;
import rendering.Utility.json.EdgeSCCParser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class EdgesImages {

    public static void drawEdges(int height, int width) throws IOException {

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        List<MyEdge> edges = new ArrayList<>();
        String filename = FilesName.getInstance().getEdgesFile();
        edges = EdgeParser.getEdges(filename);


        for (int i = 0; i < edges.size(); i++) {
            MyEdge temp = edges.get(i);
            EdgesImages.drawEdge(img, temp.getX1(), temp.getY1(), temp.getX2(), temp.getY2());
        }


        File f = null;
        try {
            String noExtension = filename.substring(0, filename.lastIndexOf('.'));
            f = new File(noExtension + "_img.png");
            ImageIO.write(img, "png", f);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        String printName = filename.substring(filename.lastIndexOf('\\') + 1, filename.lastIndexOf('.'));
        //System.out.println(printName + " done.");

    }

	/*public void drawEdges(String filename, BufferedImage img, String fileInfo) throws IOException {

		List<MyEdge> edges = new ArrayList<>();
		edges = EdgeParser.getEdges(filename);


		for (int i = 0; i < edges.size(); i++) {
			MyEdge temp = edges.get(i);
			this.drawEdge(img, temp.getX1(), temp.getY1(), temp.getX2(), temp.getY2());
		}

		File f = null;
		try {
			String noExtension = filename.substring(0, filename.lastIndexOf('0'));
			f = new File(noExtension + fileInfo + "_edges_processed.png");
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

		String printName = filename.substring(filename.lastIndexOf('\\') + 1, filename.lastIndexOf('.'));
		System.out.println(printName + " done.");

	}*/

    public static void drawEdges(BufferedImage img) throws IOException {

        List<MyEdge> edges = new ArrayList<>();
        edges = EdgeParser.getEdges(FilesName.getInstance().getEdgesFile());

        for (int i = 0; i < edges.size(); i++) {
            MyEdge temp = edges.get(i);
            if ((temp.getId1() == 80 || temp.getId2() == 80) ||
                    (temp.getId1() == 16 || temp.getId2() == 16) ||
                    (temp.getId1() == 17 || temp.getId2() == 17) ||
                    (temp.getId1() == 20 || temp.getId2() == 20)) {
                EdgesImages.drawEdge(img, temp.getX1(), temp.getY1(), temp.getX2(), temp.getY2());
            }
        }
    }

    public static void drawColoredEdges(BufferedImage img) throws IOException {

        List<MyEdgeSCC> edgesSCC = new ArrayList<>();
        edgesSCC = EdgeSCCParser.getEdgesSCC(FilesName.getInstance().getEdgesSCCFile());

        for (int i = 0; i < edgesSCC.size(); i++) {
            MyEdgeSCC temp = edgesSCC.get(i);

            EdgesImages.drawEdgeColor(img, temp.getX1(), temp.getY1(), temp.getX2(), temp.getY2(), temp.getColor());
        }
    }

    public static void drawEdge(BufferedImage img, int x1, int y1, int x2, int y2) {

        Color color;

        color = new Color(204, 0, 0);

        Graphics graphics = img.getGraphics();
        graphics.setColor(color);
        graphics.drawLine(x1, y1, x2, y2);

    }

    public static void drawEdge(BufferedImage img, MyEdge edge) {

        Color color;

        color = new Color(204, 0, 0);

        Graphics graphics = img.getGraphics();
        graphics.setColor(color);
        graphics.drawLine(edge.getX1(), edge.getY1(), edge.getX2(), edge.getY2());

    }

    public static void drawEdgeColor(BufferedImage img, int x1, int y1, int x2, int y2, int colorId) {

        Color color;

        if (colorId == 0) {
            color = Color.BLACK;
        } else if (colorId == 1) {
            color = Color.CYAN;
        } else if (colorId == 2) {
            color = Color.YELLOW;
        } else if (colorId == 3) {
            color = Color.GREEN;
        } else if (colorId == 4) {
            color = Color.GRAY;
        } else if (colorId == 5) {
            color = Color.ORANGE;
        } else if (colorId == 6) {
            color = Color.BLUE;
        } else if (colorId == 7) {
            color = Color.PINK;
        } else if (colorId == 8) {
            color = Color.MAGENTA;
        } else if (colorId == 9) {
            color = Color.DARK_GRAY;
        } else {
            color = Color.RED;
        }


        Graphics graphics = img.getGraphics();
        graphics.setColor(color);
        graphics.drawLine(x1, y1, x2, y2);

    }



    private void drawEdgeCircle(BufferedImage img, int x1, int y1, int x2, int y2) {

        Color color;

        color = new Color(204, 0, 0);

        Graphics graphics = img.getGraphics();
        graphics.setColor(color);
        graphics.drawLine(x1, y1, x2, y2);

        Color color1 = new Color(204, 204, 0);
        graphics.setColor(color1);
        this.drawCircle(x1, y1, 6, color, img);
        this.drawCircle(x2, y2, 6, color1, img);

    }

    private void drawCircle(int x, int y, int r, Color color, BufferedImage img) {
        Graphics graphics = img.getGraphics();
        graphics.setColor(color);
        x = x - (r / 2);
        y = y - (r / 2);
        graphics.drawOval(x, y, r, r);
    }
}
