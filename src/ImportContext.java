import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
		parseFile(contextFile);
		copyAudioFiles(extractedFolder);
	}
	
	// File generated will only have integer coordinates, image is shown in original size
	// Each coordinate represents a pixel
	private static Point getPoint(String line) {
        line = line.trim();
        StringTokenizer st = new StringTokenizer(line);
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
       
        Point P = new Point(x, y);

        return P;
    }
	
	public static void parseFile(String filename){
        try {
            File file = new File(filename);
            BufferedReader br = new BufferedReader(new FileReader(file));

         // Skip first line
            String line = br.readLine();
            // Fill corners
            int t = 0;
            while (t < 4 && (line = br.readLine()) != null) {
                Utility.corners.add(getPoint(line));
                t++;
            }
            
            ArrayList<Point> contour = new ArrayList<Point>();
            
            // Skip the first empty line
            while ((line = br.readLine()) != null) {
                line = br.readLine();
                Utility.titles.add(line.trim());
                line = br.readLine();
                if (line.startsWith("$AUDIO$")) {
                    Utility.descriptions.add(line.trim());
                } else {
                    String desc = "";
                    while ((line = br.readLine()) != "=") {
                        desc += line;
                    }
                    Utility.descriptions.add(desc);
                }
                while ((line = br.readLine()) != "=") {
                    Point gP = getPoint(line);
                    contour.add(gP);
                }
                Utility.polygons.add(contour);
            }
            br.close();
        } catch (
                IOException e
                )
        {
            e.printStackTrace();
        }
    }
	
	public static void copyAudioFiles(String audioFolder) throws IOException {
		File dir = new File("audioFOlder");
		File[] files = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".txt");
		    }
		});
		for (int i = 0; i < files.length; i++) {
			String audioNameWithExtension = files[i].getName();
			Path source = FileSystems.getDefault()
					.getPath(audioFolder + File.separator + audioNameWithExtension + ".wav");
			Path target = FileSystems.getDefault().getPath(Utility.absPathTempFiles + File.separator + audioNameWithExtension + ".wav");
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
		}
	}
}
