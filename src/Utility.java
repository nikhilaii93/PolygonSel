import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

class CornerComparator implements Comparator<Point> {
	@Override
	public int compare(Point o1, Point o2) {
		return (o1.x + o1.y) - (o2.x + o2.y);
	}
}

public class Utility {

	public static ArrayList<Point> coords = new ArrayList<Point>();
	public static ArrayList<ArrayList<Point>> polygons = new ArrayList<ArrayList<Point>>();
	public static ArrayList<Point> corners = new ArrayList<Point>();

	public static ArrayList<String> titles = new ArrayList<String>();
	public static ArrayList<String> descText = new ArrayList<String>();
	// public static ArrayList<AudioInputStream> descAudio = new
	// ArrayList<AudioInputStream>();
	static int audioCounter = 0;
	protected static String absPathTempFiles = null;
	
	public static int radius = 4;

	// Returns resizedImage whose either height or width fits is equal to that
	// of the panel.
	// The ratio of height:width is same for resizedImage and originalImage.
	/*
	 * public static BufferedImage resizeImage(BufferedImage originalImage,
	 * JPanel panel) { int orgHt = originalImage.getHeight(); int orgWd =
	 * originalImage.getWidth();
	 * 
	 * int panelHt = panel.getHeight(); int panelWd = panel.getWidth();
	 * 
	 * int newHt, newWd; // Compare the ratio of dimensions and scale
	 * accordingly so that the // image fits the JPanel if ((float) orgHt /
	 * (float) orgWd >= (float) panelHt / (float) panelWd) { newHt = panelHt;
	 * newWd = (int) (((float) orgWd / (float) orgHt) * newHt); } else { newWd =
	 * panelWd; newHt = (int) (((float) orgHt / (float) orgWd) * newWd); }
	 * System.out.println("org: " + orgHt + " " + orgWd + "\n" + "panel: " +
	 * panelHt + " " + panelWd); System.out.println("New: " + newHt + " " +
	 * newWd);
	 * 
	 * // Scale and obtain new resizedImage from originalImage. int type =
	 * originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB :
	 * originalImage.getType();
	 * 
	 * BufferedImage resizedImage = new BufferedImage(newHt, newWd, type);
	 * Graphics2D g = resizedImage.createGraphics(); g.drawImage(originalImage,
	 * 0, 0, newHt, newWd, null); g.dispose();
	 * g.setComposite(AlphaComposite.Src);
	 * 
	 * // Improve Image Quality
	 * g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	 * RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	 * g.setRenderingHint(RenderingHints.KEY_RENDERING,
	 * RenderingHints.VALUE_RENDER_QUALITY);
	 * g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	 * RenderingHints.VALUE_ANTIALIAS_ON);
	 * 
	 * return resizedImage; }
	 * 
	 * public static void repaintImage(BufferedImage resizedImage, JPanel panel)
	 * { panel.removeAll(); ImageIcon icon = new ImageIcon(resizedImage); JLabel
	 * label = new JLabel(); label.setIcon(icon); panel.add(label,
	 * BorderLayout.CENTER); panel.revalidate(); }
	 */

	public static void drawPoint(ImageApp frame, int X, int Y) {
		if (frame.image != null && X <= frame.image.getWidth() && Y <= frame.image.getHeight()) {
			Graphics g = frame.getGraphics(); // Getting the Graphic object
			g.setColor(Color.red); // Setting color to red
			int x = X - (radius / 2); // Position X (mouse will be in the center
										// of the point)
			int y = Y - (radius / 2);
			System.out.println("Coords: " + x + " " + y);
			g.fillOval(x, y, radius, radius); // Drawing the circle/point
			g.dispose();
		}
	}

	public static void drawPoints(Graphics g) {
		for (int i = 0; i < coords.size(); i++) {
			// Graphics g = frame.getGraphics(); // Getting the Graphic object
			g.setColor(Color.red); // Setting color to red
			int x = coords.get(i).x - (radius / 2); // Position X (mouse will be
													// in the center of the
													// point)
			int y = coords.get(i).y - (radius / 2);

			g.fillOval(x, y, radius, radius); // Drawing the circle/point
			// g.dispose();
		}
		boolean circled = false;
		for (int i = 1; i < coords.size(); i++) {
			if (!circled && coords.size() > 2) {
				g.drawLine(coords.get(coords.size() - 1).x, coords.get(coords.size() - 1).y, coords.get(0).x,
						coords.get(0).y);
				circled = true;
			}
			g.drawLine(coords.get(i - 1).x, coords.get(i - 1).y, coords.get(i).x, coords.get(i).y);
		}
		if (coords.size() > 2) {
			int[] xPoints = new int[coords.size()];
			int[] yPoints = new int[coords.size()];
			for (int i = 0; i < coords.size(); i++) {
				xPoints[i] = coords.get(i).x;
				yPoints[i] = coords.get(i).y;
			}
			g.setColor(new Color(1, 0, 0, 0.25f));
			g.fillPolygon(xPoints, yPoints, xPoints.length);
		}
		drawPolygons(g);
	}

	public static void drawCorners(Graphics g) {
		g.setColor(Color.blue); // Setting color to red
		for (int i = 0; i < corners.size(); i++) {
			int x = corners.get(i).x - (radius / 2);
			int y = corners.get(i).y - (radius / 2);

			g.fillOval(x, y, radius, radius);
		}
	}

	private static void drawPolygons(Graphics g) {

		for (int j = 0; j < polygons.size(); j++) {
			ArrayList<Point> cXY = polygons.get(j);
			for (int i = 0; i < cXY.size(); i++) {
				// Graphics g = frame.getGraphics(); // Getting the Graphic
				// object
				g.setColor(Color.green); // Setting color to red
				int x = cXY.get(i).x - (radius / 2); // Position X (mouse will
														// be
														// in the center of the
														// point)
				int y = cXY.get(i).y - (radius / 2);

				g.fillOval(x, y, radius, radius); // Drawing the circle/point
				// g.dispose();
			}
			boolean circled = false;
			for (int i = 1; i < cXY.size(); i++) {
				if (!circled && cXY.size() > 2) {
					g.drawLine(cXY.get(cXY.size() - 1).x, cXY.get(cXY.size() - 1).y, cXY.get(0).x, cXY.get(0).y);
					circled = true;
				}
				g.drawLine(cXY.get(i - 1).x, cXY.get(i - 1).y, cXY.get(i).x, cXY.get(i).y);
			}
			if (cXY.size() > 2) {
				int[] xPoints = new int[cXY.size()];
				int[] yPoints = new int[cXY.size()];
				for (int i = 0; i < cXY.size(); i++) {
					xPoints[i] = cXY.get(i).x;
					yPoints[i] = cXY.get(i).y;
				}
				g.setColor(new Color(0, 1, 0, 0.25f));
				g.fillPolygon(xPoints, yPoints, xPoints.length);
			}
		}
	}

	// Adds coordinates of a point only if the image is open and the point is on
	// the image.
	// Also, the point selected should form a convex polygon.
	public static void addCoords(ImageApp frame, int X, int Y) {
		if (frame.image != null && X <= frame.image.getWidth() && Y <= frame.image.getHeight()) {
			coords.add(new Point(X, Y));

			if (pointPolygonTest(X, Y) >= 0) {
				coords.remove(coords.size() - 1);

				JOptionPane.showMessageDialog(frame, "Point is already part of a polygon.", "Error",
						JOptionPane.ERROR_MESSAGE);

				return;
			}

			JLabel status = (JLabel) frame.statusPanel.getComponent(0);
			status.setText("Status: X: " + X + " Y: " + Y);
			if (coords.size() > 2 && !frame.savePolygon.isEnabled()) {
				frame.savePolygon.setEnabled(true);
			}
		}
		return;
	}

	public static void addCorners(ImageApp frame, int X, int Y) {
		if (corners.size() == 4) {
			JOptionPane.showMessageDialog(frame, "4 Corners are marked already.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (frame.image != null && X <= frame.image.getWidth() && Y <= frame.image.getHeight()) {
			corners.add(new Point(X, Y));

			JLabel status = (JLabel) frame.statusPanel.getComponent(0);
			status.setText("Status: CORNER MODE X: " + X + " Y: " + Y);
		}
		return;
	}

	// Removes one coordinates from each coordsX & coordsY if num == 1, else
	// for any other num value it clears the ArrayLists
	// Disable savePolygon button if frame is not null.
	public static void reduceCoords(int num, JButton savePolygon) {
		if (coords.size() > 0 && num == 1) {
			coords.remove(coords.size() - 1);
		} else {
			coords.clear();
		}
		if (coords.size() <= 2 && savePolygon.isEnabled()) {
			savePolygon.setEnabled(false);
		}
		return;
	}

	public static void reduceCorners(int num) {
		if (corners.size() > 0 && num == 1) {
			corners.remove(corners.size() - 1);
		} else {
			corners.clear();
		}
		return;
	}

	public static void clearDataStructures() {
		coords.clear();
		polygons.clear();
		corners.clear();
		titles.clear();
		descText.clear();
		// ContextDialogBox.descAudio.clear();
		audioCounter = 0;
	}

	// fillPolygon and contains both are of the class Polygon
	// their behavior is expected to be the same
	public static int pointPolygonTest(int X, int Y) {
		int polygonIndex = -2;
		for (int j = 0; j < polygons.size(); j++) {
			ArrayList<Point> cXY = polygons.get(j);
			int[] xPoints = new int[cXY.size()];
			int[] yPoints = new int[cXY.size()];
			for (int i = 0; i < cXY.size(); i++) {
				xPoints[i] = cXY.get(i).x;
				yPoints[i] = cXY.get(i).y;
			}
			Polygon curr = new Polygon(xPoints, yPoints, xPoints.length);
			if (curr.contains(X, Y)) {
				polygonIndex = j;
			}
		}

		return polygonIndex;
	}

	public static void sortCorners() {
		Collections.sort(corners, new CornerComparator());
		Point a = (Point) corners.get(1).clone();
		Point b = (Point) corners.get(2).clone();

		if (a.y < b.y) {
			corners.set(1, a);
			corners.set(2, b);
		} else {
			corners.set(1, b);
			corners.set(2, a);
		}
	}

	public static void writeOutContext(File file) throws IOException {
		sortCorners();
		file.mkdir();
		String path = file.getAbsolutePath();
		String fileName = path.substring(path.lastIndexOf(File.separator) + 1);

		File newFile = new File(path + File.separator + fileName + ".txt");
		newFile.createNewFile();

		PrintWriter writer = new PrintWriter(newFile, "UTF-8");

		writer.println("corners");
		for (int i = 0; i < corners.size(); i++) {
			writer.println('\t' + corners.get(i).x + '\t' + corners.get(i).y);
		}
		writer.println("=");
		for (int i = 0; i < titles.size(); i++) {
			writer.println(titles.get(i));
			if (descText.get(i).contains("$AUDIO$")) {
				writer.println(descText.get(i));
				String audioName = descText.get(i);
				Path source = FileSystems.getDefault()
						.getPath(absPathTempFiles + File.separator + audioName + ".wav");
				Path target = FileSystems.getDefault().getPath(path + File.separator + audioName + ".wav");
				System.out.println("source: " + source);
				System.out.println("target: " + target);
				
				// Copy files instead of move, as the source if busy if another process
				// will generate error, delete such files afterwards (using deleteAllWaveFiles) and generate
				// warning for user in case of failure.
				Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
				
				// int audioIndex =
				// Integer.parseInt(audioName.substring(audioName.lastIndexOf("$")+1));
				// System.out.println(path + File.separator +
				// ContextDialogBox.descText.get(i));
				// ContextDialogBox.writeRecording(new File(path +
				// File.separator + ContextDialogBox.descText.get(i) + ".wav"),
				// ContextDialogBox.descAudio.get(audioIndex));
			} else {
				writer.println("$TEXT$");
				writer.println(descText.get(i));
			}
			writer.println("=");
			for (int j = 0; j < polygons.get(i).size(); j++) {
				writer.println('\t' + polygons.get(i).get(j).x + '\t' + polygons.get(i).get(j).y);
			}
			writer.println("=");
		}
		writer.close();

		// zip file
		ZipUtils appZip = new ZipUtils(path);
		appZip.generateFileList(new File(path));
		appZip.zipIt(path + ".zip");

		// deletes the folder created
		if (file.delete()) {
			System.out.println("Folder Deleted Sucessfully");
		} else {
			System.out.println("Folder Cannot Be Deleted");
		}
	}
	
	public static boolean deleteAllWavFiles(String dir) {
		// Lists all files in folder
		File folder = new File(dir);
		File fList[] = folder.listFiles();
		// Searchs .wav
		boolean success = true;
		for (int i = 0; i < fList.length; i++) {
		    File pes = fList[i];
		    if (pes.getName().endsWith(".wav") && pes.getName().contains("$AUDIO$")) {
		        // and deletes
		        success = success && (pes.delete());
		    }
		}
		return success;
	}
	
}
