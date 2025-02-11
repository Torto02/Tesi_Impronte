package rendering.Utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileMover {

	public static void moveFile(String name) throws IOException {

			String dest = FilesName.getInstance().getFolderPath() + "\\ProcessedData\\" + name;
			Files.move(Paths.get(FilesName.getInstance().getFolderPath() + name), Paths.get(dest));
	}

	public static void moveImages(File directory) throws IOException {
		ArrayList<String> namesToMove = getAllImages(directory,true);

		for(int i = 0; i < namesToMove.size(); i++) {
			String temp = namesToMove.get(i);

			// Windows Path
			//String dest = FilesName.getInstance().getFolderPath() + "\\ProcessedData\\" + temp.substring(temp.lastIndexOf('\\'));

			// OSX Path
			String dest = FilesName.getInstance().getFolderPath() + "/ProcessedData/" + temp.substring(temp.lastIndexOf('/'));

			Files.move(Paths.get(temp), Paths.get(dest));
		}
	}

	private static ArrayList<String> getAllImages(File directory, boolean descendIntoSubDirectories) throws IOException {
		ArrayList<String> resultList = new ArrayList<String>(256);
		File[] f = directory.listFiles();
		for (File file : f) {
			if (file != null && file.getName().toLowerCase().endsWith(".png")) {
				resultList.add(file.getCanonicalPath());
			}
			else if (file != null && file.getName().toLowerCase().endsWith(".jpg")) {
				resultList.add(file.getCanonicalPath());
			}
			else if (file != null && file.getName().toLowerCase().endsWith(".gif")) {
				resultList.add(file.getCanonicalPath());
			}
			if (descendIntoSubDirectories && file.isDirectory()) {
				ArrayList<String> tmp = getAllImages(file, true);
				if (tmp != null) {
					resultList.addAll(tmp);
				}
			}
		}
		if (resultList.size() > 0)
			return resultList;
		else
			return null;
	}

}
