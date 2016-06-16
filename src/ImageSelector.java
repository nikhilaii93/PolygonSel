import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageSelector implements ActionListener {
	JPanel grid;
	JLabel label = null;

	ImageSelector(JPanel mainGrid) {
		grid = mainGrid;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		JFileChooser fileChooser = new JFileChooser();

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg");
		fileChooser.setFileFilter(filter);

		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			BufferedImage image = null;
			try {
				System.out.println(selectedFile.getName());
				image = ImageIO.read(new File(selectedFile.getAbsolutePath()));
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

			// TODO Resize Image to a predefined size
			int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();

			BufferedImage resizedImage = new BufferedImage(500, 500, type);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(image, 0, 0, 500, 500, null);
			g.dispose();
			g.setComposite(AlphaComposite.Src);

			// Improve Image Quality
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			grid.removeAll();
			ImageIcon icon = new ImageIcon(resizedImage);
			label = new JLabel();
			label.setIcon(icon);
			grid.add(label, BorderLayout.CENTER);
			grid.revalidate();
		}
	}
}
