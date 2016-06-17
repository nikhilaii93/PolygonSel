import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Utility {
	// Returns resizedImage whose either height or width fits is equal to that of the panel.
	// The ratio of height:width is same for resizedImage and originalImage.
	public static BufferedImage resizeImage(BufferedImage originalImage, JPanel panel) {
		int orgHt = originalImage.getHeight();
		int orgWd = originalImage.getWidth();

		int panelHt = panel.getHeight();
		int panelWd = panel.getWidth();
		
		int newHt, newWd;
		// Compare the ratio of dimensions and scale accordingly so that the image fits the JPanel
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
}
