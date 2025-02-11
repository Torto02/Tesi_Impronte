package rendering.Utility;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Minutia implements Comparable<Minutia>{
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public double getDirection() {
		return direction;
	}

	public int getType() {
		return type;
	}
	
	public boolean getVisited() {
		return visited;
	}
	
	public int getId() {
		return this.ID;
	}
	
	private int ID;
	private int x;
	private int y;
	private double direction;
	private int type;
	private boolean visited;

	public void setColor(Color color) {		this.color = color;
	}

	private CharSequence fromFile(String filename) throws IOException {
		FileInputStream input = new FileInputStream(filename);
		FileChannel channel = input.getChannel();

		// Create a read-only CharBuffer on the file
		ByteBuffer bbuf = channel.map(FileChannel.MapMode.READ_ONLY, 0, (int)channel.size());
		CharBuffer cbuf = Charset.forName("8859_1").newDecoder().decode(bbuf);
		return cbuf;
	}

	public void setColorFromFileHere() {
		try {
			// Create matcher on file
			Pattern pattern = Pattern.compile("pattern");
			Matcher matcher = pattern.matcher(fromFile("infile.txt"));

			// Find all matches
			while (matcher.find()) {
				// Get the matching string
				String match = matcher.group();
			}
		} catch (IOException e) {
		}
	}

	private Color color = new Color(0,0,0);

	public Minutia() {
		this.x = 0;
		this.y = 0;
		this.direction = 0.0;
		this.type = 0;
		this.visited = false;
		this.ID=0;
	}
	public Minutia(int x, int y) {
		this.x = x;
		this.y = y;
		this.direction = 0.0;
		this.type = 0;
		this.visited = false;
		this.ID=0;
	}
	
	public Minutia(int x, int y, int id) {
		this.x = x;
		this.y = y;
		this.direction = 0.0;
		this.type = 0;
		this.visited = false;
		this.ID = id;
	}
	
	public Minutia(int x, int y, double d, int t) {
		this.x = x;
		this.y = y;
		this.direction = d;
		this.type = t;
		this.ID=0;
	}
	
	public Minutia(int x, int y, double d, int t, int ID) {
		this.x = x;
		this.y = y;
		this.direction = d;
		this.type = t;
		this.ID = ID;
	}
	
	public Minutia(int x, int y, boolean v) {
		this.x = x;
		this.y = y;
		this.direction = 0.0;
		this.type = 0;
		this.visited = v;
		this.ID=0;
	}
	
	public Minutia(int x, int y, double direction, int type, boolean v) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.type = type;
		this.visited = v;
		this.ID=0;

	}

	
	@Override
	public int compareTo(Minutia minutia) {
		int result;
		result = this.y - minutia.getY();
		if (result == 0){
			result = this.x - minutia.getX();
		}
		return result;
	}
}
