import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageSelector implements ActionListener {
	JFrame frame;
	ImageSelector(JFrame mainFrame) {
		frame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		JFileChooser fileChooser = new JFileChooser();
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			BufferedImage image = null;
			try {
				image = ImageIO.read(new File(selectedFile.getName()));
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

			ImageIcon icon = new ImageIcon(image);
			JLabel label = new JLabel();
			label.setIcon(icon);
			// frame.getContentPane().removeAll();
			frame.getContentPane().add(label, BorderLayout.CENTER);
			frame.revalidate();
		}
	}
}
