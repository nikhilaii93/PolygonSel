import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PointerEvent implements MouseListener {
	ImageApp frame;
	// JTextField coordinates;

	PointerEvent(ImageApp mainGrid /*, JTextField mainCoordinates*/) {
		frame = mainGrid;
		// coordinates = mainCoordinates;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// int x = e.getX();
		// int y = e.getY();
		// coordinates.setText("X:" + x + " Y:" + y);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Utility.addCoords(frame, e.getX(), e.getY());
		frame.removeAll();
		frame.repaint();
        // Utility.drawPoint(frame, e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
