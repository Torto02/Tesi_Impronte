package rendering.Utility;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Match {
	private String fingerprintName1, fingerprintName2;
	private String fingerprintString1, fingerprintString2;
	private double score;
	private String lcs;
	private double percentage;

	public Match() {
		this.fingerprintName2 = "";
		this.fingerprintName2 = "";
		this.fingerprintString1 = "";
		this.fingerprintString2 = "";
		this.score = 0;
		this.lcs = null;
		this.percentage = 0;
	}

	public Match(String f1, String f2, double s, String l) {
		this.fingerprintName2 = f1;
		this.fingerprintName2 = f2;
		this.score = s;
		this.lcs = l;
	}

	public void setFirstFingerprintName(String f1) {
		this.fingerprintName1 = f1;
	}

	public void setSecondFingerprintName(String f2) {
		this.fingerprintName2 = f2;
	}

	public void setFirstFingerprintString(String f1) {
		this.fingerprintString1 = f1;
	}

	public void setSecondFingerprintString(String f2) {
		this.fingerprintString2 = f2;
	}

	public void setScore(double s) {
		this.score = s;
	}

	public void setLCS(String l) {
		this.lcs = l;
	}

	public void setPercentage(double p) {
		this.percentage = p;
	}

	public String getFirstFingerprintName() {
		return fingerprintName1;
	}

	public String getSecondFingerprintName() {
		return fingerprintName2;
	}

	public String getFirstFingerprintString() {
		return fingerprintString1;
	}

	public String getSecondFingerprintString() {
		return fingerprintString2;
	}

	public double getScore() {
		return score;
	}

	public String getLCS() {
		return lcs;
	}

	public int getLCSLength() {
		return lcs.length();
	}

	public double getPercentage() {
		return percentage;
	}

	@SuppressWarnings("resource")
	public void printTableOnAFile(List<Match> match) throws IOException {
		FileWriter w = new FileWriter("matching_table.txt", true);
		for (int k = 0; k < match.size(); k++) {
			w.write(match.get(k).getFirstFingerprintName() + ", " + match.get(k).getSecondFingerprintName() + ", " + match.get(k).getScore()
					+ ", " + match.get(k).getLCS().length() + ", " + match.get(k).getPercentage() + "\n");
			w.flush();
		}
		w.close();
	}

	public void printTableOnACSV(double[] score) throws IOException {
		FileWriter w = new FileWriter("score_result.csv", true);
		for (int k = 0; k < score.length; k++) {
			w.write(score[k] + ", ");
			w.flush();
		}
		w.write("\n");
		w.close();
	}
	
	public void printPercentageOnACSV(double[] percentage) throws IOException {
		FileWriter w = new FileWriter("percentage_result.csv", true);
		for (int k = 0; k < percentage.length; k++) {
			w.write(percentage[k] + ", ");
			w.flush();
		}
		w.write("\n");
		w.close();
	}
	

}
