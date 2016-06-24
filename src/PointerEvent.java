import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PointerEvent implements MouseListener {
	ImageApp frame;

	PointerEvent(ImageApp mainGrid) {
		frame = mainGrid;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!frame.isEditMode) {
			Utility.addCoords(frame, e.getX(), e.getY());
			frame.removeAll();
			frame.repaint();
		} else {
			int result = Utility.pointPolygonTest(e.getX(), e.getY());
			ContextDialogBox.getContextDialogBox(result);
		}
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
