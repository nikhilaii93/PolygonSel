import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Utility {
	// Returns resizedImage whose either height or width fits is equal to that
	// of the panel.
	// The ratio of height:width is same for resizedImage and originalImage.
	public static ArrayList<Integer> coordsX = new ArrayList<Integer>();
	public static ArrayList<Integer> coordsY = new ArrayList<Integer>();
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

	public static void drawPoints(ImageApp frame, Graphics g) {
		System.out.println("Utility.drawPoints called out.");
		for (int i = 0; i < coordsX.size(); i++) {
			// Graphics g = frame.getGraphics(); // Getting the Graphic object
			g.setColor(Color.red); // Setting color to red
			int x = coordsX.get(i) - (radius / 2); // Position X (mouse will be
													// in the center of the
													// point)
			int y = coordsY.get(i) - (radius / 2);

			System.out.println("sdfs: " + x + " " + y);
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
		if (coordsX.size() > 2)  {
			int[] xPoints = new int[coordsX.size()];
			int[] yPoints = new int[coordsX.size()];
			for (int i = 0; i < coordsX.size(); i++) {
				xPoints[i] = coordsX.get(i);
				yPoints[i] = coordsY.get(i);
			}
			g.setColor(new Color(1, 0, 0, 0.25f));
			g.fillPolygon(xPoints, yPoints, xPoints.length);
		}
	}

	public static void addCoords(ImageApp frame, int X, int Y) {
		if (frame.image != null && X <= frame.image.getWidth() && Y <= frame.image.getHeight()) {
			coordsX.add(X);
			coordsY.add(Y);
		}
	}
}
