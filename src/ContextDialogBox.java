import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class ContextDialogBox {
	public static int TITLE_LEN = 50;

	public static ArrayList<String> titles = new ArrayList<String>();
	public static ArrayList<String> descText = new ArrayList<String>();
	public static ArrayList<AudioInputStream> descAudio = new ArrayList<AudioInputStream>();
	
	public static JToggleButton audioCumText;

	public static AudioInputStream finalStream = null;
	public static AudioInputStream toAppendStream = null;
	
	protected AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;

	// polygonIndex:
	// -1 indicates that this is not an edit mode
	// >=0 value is the index of the polygon detected by polygon test
	// -2 edit mode is on but the pointer is not inside any polygon
	public static boolean getContextDialogBox(int polygonIndex) {
		if (polygonIndex > -2) {
			JTextField title = new JTextField();
			title.setColumns(TITLE_LEN);

			audioCumText = new JToggleButton("Text");
			audioCumText.setSelected(false);

			JPanel desc = new JPanel();
			final TextArea textDesc = new TextArea();
			final AudioPane audioDesc = new AudioPane();
			desc.add(textDesc);
			desc.add(audioDesc);

			if (polygonIndex >= 0) {
				title.setText(titles.get(polygonIndex));
				if (!descText.get(polygonIndex).contains("$AUDIO$")) {
					textDesc.setText(descText.get(polygonIndex));
				}
			}

			final JPanel myPanel = new JPanel();
			myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
			myPanel.add(new JLabel("Title")); // 0
			myPanel.add(title); // 1
			myPanel.add(new JLabel("Discription")); // 2
			myPanel.add(audioCumText); // 3
			myPanel.add(desc); // 4

			audioCumText.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AbstractButton abstractButton = (AbstractButton) e.getSource();
					boolean selected = abstractButton.getModel().isSelected();
					System.out.println("Action - selected=" + selected + "\n");
					audioCumText.setSelected(selected);

					if (selected) {
						audioCumText.setText("Audio");
						textDesc.setEnabled(false);
						audioDesc.setEnabled(true);
						textDesc.setText(null);
						audioDesc.getComponent(0).setEnabled(true);
					} else {
						audioCumText.setText("Text");
						textDesc.setEnabled(true);
						finalStream = null;
						toAppendStream = null;
						audioDesc.setEnabled(false);
						audioDesc.getComponent(0).setEnabled(false);
						audioDesc.getComponent(1).setEnabled(false);
						audioDesc.getComponent(2).setEnabled(false);
					}
				}
			});

			int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter Context Details",
					JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {

				if (polygonIndex == -1) {
					titles.add(title.getText());
					if (audioCumText.isSelected()) {
						descText.add("$AUDIO$" + descAudio.size());
						descAudio.add(finalStream);
					} else {
						descText.add(textDesc.getText());
						descAudio.add(null);
					}
					@SuppressWarnings("unchecked")
					ArrayList<Integer> copyX = (ArrayList<Integer>) Utility.coordsX.clone();
					@SuppressWarnings("unchecked")
					ArrayList<Integer> copyY = (ArrayList<Integer>) Utility.coordsY.clone();
					Utility.polygonX.add(copyX);
					Utility.polygonY.add(copyY);
				} else {
					titles.set(polygonIndex, title.getText());
					if (audioCumText.isSelected()) {
						descText.set(polygonIndex, "$AUDIO$" + polygonIndex);
						descAudio.add(finalStream);
					} else {
						descText.set(polygonIndex, textDesc.getText());
						descAudio.set(polygonIndex, null);
					}
				}
				System.out.println("Title: " + title.getText());
				System.out.println("Description: " + textDesc.getText());
			}
			return (result == JOptionPane.OK_OPTION);
		}
		return false;
	}

	public static class AudioPane extends JPanel {
		/**
		 * Auto-generated serialID
		 */
		private static final long serialVersionUID = -6483413280444018893L;

		private JToggleButton recordButton;
		private TargetDataLine line;

		public AudioPane() {
			setLayout(new GridBagLayout());
			recordButton = new JToggleButton("Record");
			recordButton.setEnabled(false);
			recordButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (recordButton.isSelected()) {
						startRecording();
						appendRecordings();
						recordButton.setText("Stop");
					} else {
						stopRecording();
						recordButton.setText("Record");
					}
				}
			});
			add(recordButton);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(200, 200);
		}

		protected void stopRecording() {
			if (line != null) {
				line.stop();
				line.close();
				line = null;
			}
		}

		protected void startRecording() {
			if (line == null) {
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							AudioFormat format = getAudioFormat();
							DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

							// checks if system supports the data line
							if (!AudioSystem.isLineSupported(info)) {
								System.out.println("Line not supported");
								System.exit(0);
							}
							line = (TargetDataLine) AudioSystem.getLine(info);
							line.open(format);
							line.start(); // start capturing

							System.out.println("In utils.Recorder: Start capturing...");

							AudioInputStream ais = new AudioInputStream(line);
							toAppendStream = ais;
						} catch (LineUnavailableException ex) {
							ex.printStackTrace();
						}
						System.out.println("Recording is done");
					}
				});
				t.start();
			}
		}

		protected AudioFormat getAudioFormat() {
			float sampleRate = 16000;
			int sampleSizeInBits = 8;
			int channels = 2;
			boolean signed = true;
			boolean bigEndian = true;
			AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
			return format;
		}

		protected void appendRecordings() {
			if (finalStream == null) {
				finalStream = toAppendStream;
				toAppendStream = null;
			} else if (toAppendStream != null) {
				AudioInputStream appendedStreams = new AudioInputStream(
						new SequenceInputStream(finalStream, toAppendStream), finalStream.getFormat(),
						finalStream.getFrameLength() + toAppendStream.getFrameLength());
				finalStream = appendedStreams;
				toAppendStream = null;
			}
		}
	}
	protected void writeRecording(String fileName) {
		try {
			System.out.println("In utils.Recorder: Start recording...");
			// start recording
			System.out.println("Is recoding");
			AudioSystem.write(finalStream, FILE_TYPE, new File(fileName));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
