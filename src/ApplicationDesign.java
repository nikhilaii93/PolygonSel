import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ApplicationDesign {

	public JFrame frmPolygonsel;
	public JTextField textField;
	// Add name and index to component whenever a component is added.
	private HashMap<String, Integer> componentMap;

	/**
	 * Create the application.
	 */
	public ApplicationDesign() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		componentMap = new HashMap<String, Integer>();
		
		frmPolygonsel = new JFrame();
		frmPolygonsel.setTitle("PolygonSel");
		frmPolygonsel.setIconImage(Toolkit.getDefaultToolkit().getImage("res/logo.png"));
		frmPolygonsel.setBounds(100, 100, 1366, 768);
		frmPolygonsel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 90, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		frmPolygonsel.getContentPane().setLayout(gridBagLayout);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		frmPolygonsel.getContentPane().add(panel_1, gbc_panel_1);
		componentMap.put("panel_1", frmPolygonsel.getContentPane().getComponentCount() - 1);

		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JButton btnBrowse = new JButton("Browse...");
		GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
		gbc_btnBrowse.insets = new Insets(0, 0, 5, 0);
		gbc_btnBrowse.gridx = 0;
		gbc_btnBrowse.gridy = 0;
		panel_1.add(btnBrowse, gbc_btnBrowse);

		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.BOTH;
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 1;
		panel_1.add(textField, gbc_textField);
		textField.setColumns(10);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;
		frmPolygonsel.getContentPane().add(panel, gbc_panel);
		componentMap.put("panel", frmPolygonsel.getContentPane().getComponentCount() - 1);

		// Add required listeners
		btnBrowse.addActionListener(new ImageSelector(panel));
		panel.addMouseListener(new PointerEvent(panel, textField));

		// createComponentMap();
	}

	/*
	 * private void createComponentMap() { componentMap = new HashMap<String,
	 * Component>(); Component[] components =
	 * frmPolygonsel.getContentPane().getComponents(); for (int i = 0; i <
	 * components.length; i++) { componentMap.put(components[i].getName(),
	 * components[i]); System.out.println("nm "+components[i]); } }
	 */
	public int getComponentIndex(String name) {
		if (componentMap.containsKey(name)) {
			return componentMap.get(name);
		} else
			return -1;
	}
}
