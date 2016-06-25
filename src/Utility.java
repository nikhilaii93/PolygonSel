import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Utility {
	// Returns resizedImage whose either height or width fits is equal to that
	// of the panel.
	// The ratio of height:width is same for resizedImage and originalImage.
	public static ArrayList<Integer> coordsX = new ArrayList<Integer>();
	public static ArrayList<Integer> coordsY = new ArrayList<Integer>();
	public static ArrayList<ArrayList<Integer>> polygonX = new ArrayList<ArrayList<Integer>>();
	public static ArrayList<ArrayList<Integer>> polygonY = new ArrayList<ArrayList<Integer>>();
	public static ArrayList<Integer> cornersX = new ArrayList<Integer>();
	public static ArrayList<Integer> cornersY = new ArrayList<Integer>();

	public static int radius = 4;

	public static BufferedImage resizeImage(BufferedImage originalImage, JPanel panel) {
		int orgHt = originalImage.getHeight();
		int orgWd = originalImage.getWidth();

		int panelHt = panel.getHeight();
		int panelWd = panel.getWidth();

		int newHt, newWd;
		// Compare the ratio of dimensions and scale accordingly so that the
		// image fits the JPanel
		if ((float) orgHt / (float) orgWd >= (float) panelHt / (float) panelWd) {
			newHt = panelHt;
			newWd = (int) (((float) orgWd / (float) orgHt) * newHt);
		} else {
			newWd = panelWd;
			newHt = (int) (((float) orgHt / (float) orgWd) * newWd);
		}
		System.out.println("org: " + orgHt + " " + orgWd + "\n" + "panel: " + panelHt + " " + panelWd);
		System.out.println("New: " + newHt + " " + newWd);

		// Scale and obtain new resizedImage from originalImage.
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

		BufferedImage resizedImage = new BufferedImage(newHt, newWd, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newHt, newWd, null);
		g.dispose();
		g.setComposite(AlphaComposite.Src);

		// Improve Image Quality
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		return resizedImage;
	}

	public static void repaintImage(BufferedImage resizedImage, JPanel panel) {
		panel.removeAll();
		ImageIcon icon = new ImageIcon(resizedImage);
		JLabel label = new JLabel();
		label.setIcon(icon);
		panel.add(label, BorderLayout.CENTER);
		panel.revalidate();
	}

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
		System.out.println("Utility.drawPoints called out.");
		for (int i = 0; i < coordsX.size(); i++) {
			// Graphics g = frame.getGraphics(); // Getting the Graphic object
			g.setColor(Color.red); // Setting color to red
			int x = coordsX.get(i) - (radius / 2); // Position X (mouse will be
													// in the center of the
													// point)
			int y = coordsY.get(i) - (radius / 2);

			g.fillOval(x, y, radius, radius); // Drawing the circle/point
			// g.dispose();
		}
		boolean circled = false;
		for (int i = 1; i < coordsX.size(); i++) {
			if (!circled && coordsX.size() > 2) {
				g.drawLine(coordsX.get(coordsX.size() - 1), coordsY.get(coordsX.size() - 1), coordsX.get(0),
						coordsY.get(0));
				circled = true;
			}
			g.drawLine(coordsX.get(i - 1), coordsY.get(i - 1), coordsX.get(i), coordsY.get(i));
		}
		if (coordsX.size() > 2) {
			int[] xPoints = new int[coordsX.size()];
			int[] yPoints = new int[coordsX.size()];
			for (int i = 0; i < coordsX.size(); i++) {
				xPoints[i] = coordsX.get(i);
				yPoints[i] = coordsY.get(i);
			}
			g.setColor(new Color(1, 0, 0, 0.25f));
			g.fillPolygon(xPoints, yPoints, xPoints.length);
		}
		drawPolygons(g);
	}

	public static void drawCorners(Graphics g) {
		g.setColor(Color.blue); // Setting color to red
		for (int i = 0; i < cornersX.size(); i++) {
			int x = cornersX.get(i) - (radius / 2);
			int y = cornersY.get(i) - (radius / 2);

			g.fillOval(x, y, radius, radius);
		}
	}

	private static void drawPolygons(Graphics g) {

		for (int j = 0; j < polygonX.size(); j++) {
			System.out.println("PlX: " + polygonX.size());
			System.out.println("PlX0: " + polygonX.get(0).size());
			ArrayList<Integer> cX = polygonX.get(j);
			ArrayList<Integer> cY = polygonY.get(j);
			for (int i = 0; i < cX.size(); i++) {
				// Graphics g = frame.getGraphics(); // Getting the Graphic
				// object
				g.setColor(Color.green); // Setting color to red
				int x = cX.get(i) - (radius / 2); // Position X (mouse will
													// be
													// in the center of the
													// point)
				int y = cY.get(i) - (radius / 2);

				g.fillOval(x, y, radius, radius); // Drawing the circle/point
				// g.dispose();
			}
			boolean circled = false;
			for (int i = 1; i < cX.size(); i++) {
				if (!circled && cX.size() > 2) {
					g.drawLine(cX.get(cX.size() - 1), cY.get(cX.size() - 1), cX.get(0), cY.get(0));
					circled = true;
				}
				g.drawLine(cX.get(i - 1), cY.get(i - 1), cX.get(i), cY.get(i));
			}
			if (cX.size() > 2) {
				int[] xPoints = new int[cX.size()];
				int[] yPoints = new int[cX.size()];
				for (int i = 0; i < cX.size(); i++) {
					xPoints[i] = cX.get(i);
					yPoints[i] = cY.get(i);
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
			coordsX.add(X);
			coordsY.add(Y);

			if (pointPolygonTest(X, Y) >= 0) {
				coordsX.remove(coordsX.size() - 1);
				coordsY.remove(coordsY.size() - 1);

				JOptionPane.showMessageDialog(frame, "Point is already part of a polygon.", "Error",
						JOptionPane.ERROR_MESSAGE);

				return;
			}

			JLabel status = (JLabel) frame.statusPanel.getComponent(0);
			status.setText("Status: X: " + X + " Y: " + Y);
			if (coordsX.size() > 2 && !frame.savePolygon.isEnabled()) {
				frame.savePolygon.setEnabled(true);
			}
		}
		return;
	}

	public static void addCorners(ImageApp frame, int X, int Y) {
		if (cornersX.size() == 4) {
			JOptionPane.showMessageDialog(frame, "4 Corners are marked already.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (frame.image != null && X <= frame.image.getWidth() && Y <= frame.image.getHeight()) {
			cornersX.add(X);
			cornersY.add(Y);

			JLabel status = (JLabel) frame.statusPanel.getComponent(0);
			status.setText("Status: CORNER MODE X: " + X + " Y: " + Y);
		}
		return;
	}

	// Removes one coordinates from each coordsX & coordsY if num == 1, else
	// for any other num value it clears the ArrayLists
	// Disable savePolygon button if frame is not null.
	public static void reduceCoords(int num, JButton savePolygon) {
		if (coordsX.size() > 0 && coordsY.size() > 0 && num == 1) {
			coordsX.remove(coordsX.size() - 1);
			coordsY.remove(coordsY.size() - 1);
		} else {
			coordsX.clear();
			coordsY.clear();
		}
		if (coordsX.size() <= 2 && savePolygon.isEnabled()) {
			savePolygon.setEnabled(false);
		}
		return;
	}

	public static void reduceCorners(int num) {
		if (cornersX.size() > 0 && cornersY.size() > 0 && num == 1) {
			cornersX.remove(cornersX.size() - 1);
			cornersY.remove(cornersY.size() - 1);
		} else {
			cornersX.clear();
			cornersY.clear();
		}
		return;
	}

	public static void clearDataStructures() {
		coordsX.clear();
		coordsY.clear();
		polygonX.clear();
		polygonY.clear();
		ContextDialogBox.titles.clear();
		ContextDialogBox.descText.clear();
		ContextDialogBox.descAudio.clear();
	}

	public static void writeOutContext(String filePath) {
		
	}

	public static void sortCorners() {
		
	}
	
	// fillPolygon and contains both are of the class Polygon
	// their behavior is expected to be the same
	public static int pointPolygonTest(int X, int Y) {
		int polygonIndex = -2;
		for (int j = 0; j < polygonX.size(); j++) {
			ArrayList<Integer> cX = polygonX.get(j);
			ArrayList<Integer> cY = polygonY.get(j);
			int[] xPoints = new int[cX.size()];
			int[] yPoints = new int[cY.size()];
			for (int i = 0; i < cX.size(); i++) {
				xPoints[i] = cX.get(i);
				yPoints[i] = cY.get(i);
			}
			Polygon curr = new Polygon(xPoints, yPoints, xPoints.length);
			if (curr.contains(X, Y)) {
				polygonIndex = j;
			}
		}

		return polygonIndex;
	}
}
