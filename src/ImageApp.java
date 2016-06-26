import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageApp extends JPanel {

	/**
	 * Auto-generated serial ID
	 */
	private static final long serialVersionUID = 5062796287923742976L;
	private static final int MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	private JFileChooser chooser = new JFileChooser();
	private Action openAction = new ImageOpenAction("Open");
	private Action clearAction = new ClearAction("Clear");
	private JPopupMenu popup = new JPopupMenu();
	public BufferedImage image;
	private JButton clearAllPtsBtn;
	private JButton clearLastPtBtn;
	public JButton savePolygon;
	public JButton writeOutContext;
	public JToggleButton editContext;
	public JToggleButton markCorners;
	public JPanel statusPanel;

	public boolean isEditMode = false;
	public boolean isCornerMode = false;

	public void create() {
		JFrame f = new JFrame();
		f.setTitle("PolygonSel");
		f.add(new JScrollPane(this), BorderLayout.CENTER);
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menu.setMnemonic('F');
		menu.add(new JMenuItem(openAction));
		menu.add(new JMenuItem(clearAction));
		menuBar.add(menu);

		menuBar.add(clearAllPtsBtn);
		clearAllPtsBtn.setEnabled(false);
		clearAllPtsBtn.setToolTipText("Clears all the points marked");
		menuBar.add(clearLastPtBtn);
		clearLastPtBtn.setEnabled(false);
		clearLastPtBtn.setToolTipText("Clears the last point marked");
		menuBar.add(savePolygon);
		savePolygon.setEnabled(false);
		savePolygon.setToolTipText("Saves the polygon(red)");
		menuBar.add(editContext);
		editContext.setEnabled(false);
		editContext.setToolTipText("Edit details of a polygon(green)");
		menuBar.add(markCorners);
		markCorners.setEnabled(false);
		markCorners.setSelected(false);
		markCorners.setToolTipText("Corner Mode toggle, to mark corners");
		menuBar.add(writeOutContext);
		writeOutContext.setEnabled(false);
		writeOutContext.setToolTipText("Generates output file, after sufficient details saved");

		f.setIconImage(Toolkit.getDefaultToolkit().getImage("res/logo.png"));
		f.setJMenuBar(menuBar);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setSize(new Dimension(640, 480));
		f.setLocationRelativeTo(null);

		statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		f.add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setPreferredSize(new Dimension(f.getWidth(), 16));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		JLabel statusLabel = new JLabel("Status:");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);

		f.setVisible(true);
	}

	public ImageApp() {
		this.setComponentPopupMenu(popup);
		this.addMouseListener(new PointerEvent(this));
		popup.add("Popup Menu");
		popup.add(new JMenuItem(openAction));
		popup.add(new JMenuItem(clearAction));

		clearAllPtsBtn = new JButton("CLEAR");
		clearAllPtsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isCornerMode) {
					Utility.reduceCoords(0, savePolygon);
					savePolygon.setEnabled(false);
				} else {
					Utility.reduceCorners(0);
					writeOutContext.setEnabled(false);
				}
				removeAll();
				repaint();
			}
		});

		clearLastPtBtn = new JButton("UNDO");
		clearLastPtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isCornerMode) {
					Utility.reduceCoords(1, savePolygon);
				} else {
					Utility.reduceCorners(1);
					writeOutContext.setEnabled(false);
				}
				removeAll();
				repaint();
			}
		});

		savePolygon = new JButton("SAVE");
		savePolygon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					if (ContextDialogBox.getContextDialogBox(-1)) {
						Utility.reduceCoords(0, savePolygon);
						if (Utility.corners.size() == 4) {
							writeOutContext.setEnabled(true);
						}
						editContext.setEnabled(true);
						editContext.setSelected(false);

						removeAll();
						repaint();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		writeOutContext = new JButton("Generate Output");
		writeOutContext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						Utility.writeOutContext(file);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					image = null;
					Utility.clearDataStructures();
					savePolygon.setEnabled(false);
					writeOutContext.setEnabled(false);

					removeAll();
					revalidate();
					repaint();
				}
			}
		});

		editContext = new JToggleButton("Edit Mode");
		editContext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				System.out.println("Action - selected=" + selected + "\n");
				editContext.setSelected(selected);

				if (selected) {
					isEditMode = true;
					clearAllPtsBtn.setEnabled(false);
					clearLastPtBtn.setEnabled(false);
					savePolygon.setEnabled(false);
					markCorners.setEnabled(false);
					writeOutContext.setEnabled(false);

					JLabel status = (JLabel) statusPanel.getComponent(0);
					status.setText("Status: EDIT MODE");
				} else {
					isEditMode = false;

					markCorners.setEnabled(true);
					clearAllPtsBtn.setEnabled(true);
					clearLastPtBtn.setEnabled(true);
					if (Utility.corners.size() == 4) {
						writeOutContext.setEnabled(true);
					}

					JLabel status = (JLabel) statusPanel.getComponent(0);
					status.setText("Status: ");
				}
			}
		});

		markCorners = new JToggleButton("Corner Mode");
		markCorners.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				markCorners.setSelected(selected);

				if (selected) {
					isCornerMode = true;
					removeAll();
					revalidate();
					repaint();
				} else {
					isCornerMode = false;
					if (Utility.corners.size() == 4 && Utility.polygons.size() > 0) {
						writeOutContext.setEnabled(true);
					}
					removeAll();
					revalidate();
					repaint();
				}
			}
		});
	}

	@Override
	public Dimension getPreferredSize() {
		if (image == null) {
			return new Dimension();
		} else {
			return new Dimension(image.getWidth(), image.getHeight());
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null);
		if (isCornerMode) {
			Utility.drawCorners(g);
		} else {
			Utility.drawPoints(g);
		}
	}

	private class ClearAction extends AbstractAction {

		/**
		 * Auto-generated serial ID
		 */
		private static final long serialVersionUID = -7003124085477899888L;

		public ClearAction(String name) {
			super(name);
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			image = null;
			Utility.clearDataStructures();
			savePolygon.setEnabled(false);
			writeOutContext.setEnabled(false);
			clearLastPtBtn.setEnabled(false);
			clearAllPtsBtn.setEnabled(false);
			markCorners.setEnabled(false);
			revalidate();
			repaint();
		}
	}

	private class ImageOpenAction extends AbstractAction {

		/**
		 * Auto-generated serial ID
		 */
		private static final long serialVersionUID = 8471497794328093321L;

		public ImageOpenAction(String name) {
			super(name);
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(chooser);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				try {
					image = ImageIO.read(f);
					Utility.clearDataStructures();
					savePolygon.setEnabled(false);
					writeOutContext.setEnabled(false);
					clearLastPtBtn.setEnabled(true);
					clearAllPtsBtn.setEnabled(true);
					markCorners.setEnabled(true);
					revalidate();
					repaint();
				} catch (IOException ex) {
					ex.printStackTrace(System.err);
				}
			}
		}
	}
}
