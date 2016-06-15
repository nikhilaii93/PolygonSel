import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Launch {
	public static void main(String[] args) {
		// TODO Change look and feel.
		JFrame.setDefaultLookAndFeelDecorated(true);
		final JFrame frame = new JFrame();
		// To open frame in the center of the screen.
		frame.setLocationRelativeTo(null);
		frame.setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(250, 250);

		JButton button = new JButton("Select File");
		button.addActionListener(new ImageSelector(frame));
		frame.add(button);
		frame.addMouseListener(new PointerEvent(frame));
		
		frame.setVisible(true);
	}
}
