import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

public class ImportContext {
	public static void openContext(File zipFile) throws IOException {
		ZipUtils.unZipIt(zipFile);

		String zipName = zipFile.getName();
		int pos = zipName.lastIndexOf(".");
		if (pos > 0) {
			zipName = zipName.substring(0, pos);
		}
		String extractedFolder = zipFile.getParent() + File.separator + zipName;
		String contextFile = extractedFolder + File.separator + zipName + ".txt";
		parseFile(contextFile, extractedFolder);
		System.out.println(extractedFolder + File.separator + zipName + ".jpg");
		ImageApp.image = ImageIO.read(new File(extractedFolder + File.separator + zipName + ".jpg"));
	}

	// File generated will only have integer coordinates, image is shown in
	// original size
	// Each coordinate represents a pixel
	private static Point getPoint(String line) {
		line = line.trim();
		StringTokenizer st = new StringTokenizer(line);
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		Point P = new Point(x, y);

		return P;
	}

	public static void parseFile(String filename, String folderName) {
		try {
			File file = new File(filename);
			BufferedReader br = new BufferedReader(new FileReader(file));

			// Skip first line
			String line = br.readLine();
			System.out.println("line1: " + line);
			// Fill corners
			int t = 0;
			while (t < 4 && (line = br.readLine()) != null) {
				System.out.println("line2: " + line);
				Utility.corners.add(getPoint(line));
				t++;
			}
			
			
			// Skip line = "="
			line = br.readLine();
			System.out.println("line3: " + line);
			while ((line = br.readLine()) != null) {
				System.out.println("line4: " + line);
				Utility.titles.add(line.trim());
				line = br.readLine();
				System.out.println("line5: " + line);
				if (line.startsWith("$AUDIO$")) {
					Utility.descriptions.add(line.trim());
					copyAudioFile(folderName, line.trim());
					line = br.readLine();
				} else {
					String desc = "";
					// Skip line = "$TEXT""
					line = br.readLine();
					while (!line.equals("=")) {
						desc += line;
						line = br.readLine();
					}
					Utility.descriptions.add(desc);
					System.out.println("line6: " + desc);
				}
				// Skip line = "="
				line = br.readLine();
				ArrayList<Point> contour = new ArrayList<Point>();
				while (!line.equals("=")) {
					System.out.println("line8: " + line);
					Point gP = getPoint(line);
					contour.add(gP);
					line = br.readLine();
				}
				System.out.println("line9: " + line);
				Utility.polygons.add(contour);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyAudioFile(String audioFolder, String audioFileWithoutExt) throws IOException {
		Path source = FileSystems.getDefault().getPath(audioFolder + File.separator + audioFileWithoutExt + ".wav");
		Path target = FileSystems.getDefault()
				.getPath(Utility.absPathTempFiles + File.separator + audioFileWithoutExt + ".wav");
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}
}
