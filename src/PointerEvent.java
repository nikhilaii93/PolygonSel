import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

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
			if (!frame.isCornerMode) {
				Utility.addCoords(frame, e.getX(), e.getY());
			} else {
				Utility.addCorners(frame, e.getX(), e.getY());
			}
			frame.removeAll();
			frame.repaint();
		} else {
			int result = Utility.pointPolygonTest(e.getX(), e.getY());
			try {
				ContextDialogBox.getContextDialogBox(result);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
