import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageSelector implements ActionListener {
	JPanel grid;
	// Save original image for resizes so that quality is not degraded.
	public BufferedImage originalImage;

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

			try {
				System.out.println(selectedFile.getName());
				originalImage = ImageIO.read(new File(selectedFile.getAbsolutePath()));

			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			grid.addComponentListener(new resizeListener());

			BufferedImage resizedImage = Utility.resizeImage(originalImage, grid);
			Utility.repaintImage(resizedImage, grid);
		}
	}

	class resizeListener extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			BufferedImage resizedImage = Utility.resizeImage(originalImage, (JPanel)e.getComponent());
			Utility.repaintImage(resizedImage, grid);
		}
	}
}
