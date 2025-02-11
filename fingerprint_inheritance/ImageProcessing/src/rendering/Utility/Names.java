package rendering.Utility;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Names {

	public String[] names;
	public int size;

	public Names(String file) {
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(file);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int i = 0;
			//Read File Line By Line
			while (br.readLine() != null) {
				// Print the content on the console
				i++;
			}
			size = i;
			names = new String[size];

			FileInputStream fstream1 = new FileInputStream(file);
			// Get the object of DataInputStream
			DataInputStream in1 = new DataInputStream(fstream1);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
			i = 0;
			while ((strLine = br1.readLine()) != null) {
				names[i] = strLine;
				i++;
			}

			//Close the input stream
			in.close();
		} catch (Exception e) {//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}


}
