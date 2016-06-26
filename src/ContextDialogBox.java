import java.awt.Point;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class ContextDialogBox {
	public static int TITLE_LEN = 50;

	public static JToggleButton audioCumText;
	protected static AudioInputStream ais;

	// public static AudioInputStream finalStream = null;
	// public static AudioInputStream toAppendStream = null;

	protected static AudioFormat format;
	protected static AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;

	// polygonIndex:
	// -1 indicates that this is not an edit mode
	// >=0 value is the index of the polygon detected by polygon test
	// -2 edit mode is on but the pointer is not inside any polygon
	public static boolean getContextDialogBox(int polygonIndex) throws IOException {
		if (polygonIndex > -2) {
			JTextField title = new JTextField();
			title.setColumns(TITLE_LEN);

			audioCumText = new JToggleButton("Text");
			audioCumText.setSelected(false);

			JPanel desc = new JPanel();
			final TextArea textDesc = new TextArea();
			final AudioPane audioDesc = new AudioPane(polygonIndex);
			desc.add(textDesc);
			desc.add(audioDesc);

			if (polygonIndex >= 0) {
				title.setText(Utility.titles.get(polygonIndex));
				if (!Utility.descText.get(polygonIndex).contains("$AUDIO$")) {
					textDesc.setText(Utility.descText.get(polygonIndex));
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
						// finalStream = null;
						// toAppendStream = null;
						audioDesc.setEnabled(false);
						audioDesc.getComponent(0).setEnabled(false);
						audioDesc.getComponent(1).setEnabled(false);
						audioDesc.getComponent(2).setEnabled(false);
					}
				}
			});

			int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter Context Details",
					JOptionPane.OK_CANCEL_OPTION);
			boolean contextAdded = false;
			if (result == JOptionPane.OK_OPTION) {
				if (polygonIndex == -1) {
					if (audioCumText.isSelected() && /* finalStream */ais != null && audioDesc.stopRecordingCalled) {
						Utility.titles.add(title.getText());
						Utility.descText.add("$AUDIO$" + Utility.audioCounter);
						// AudioInputStream copyStream = copyAudioInputStream(/*
						// finalStream */ais);
						// descAudio.add(copyStream);
						Utility.audioCounter++;
						@SuppressWarnings("unchecked")
						ArrayList<Point> copyCoords = (ArrayList<Point>) Utility.coords.clone();
						Utility.polygons.add(copyCoords);
						contextAdded = true;
					} else if (!audioCumText.isSelected()) {
						Utility.titles.add(title.getText());
						Utility.descText.add(textDesc.getText());
						// descAudio.add(null);
						Utility.audioCounter++;
						@SuppressWarnings("unchecked")
						ArrayList<Point> copyCoords = (ArrayList<Point>) Utility.coords.clone();
						Utility.polygons.add(copyCoords);
						contextAdded = true;
					}
				} else {
					if (audioCumText.isSelected() && /* finalStream */ais != null) {
						Utility.titles.set(polygonIndex, title.getText());
						Utility.descText.set(polygonIndex, "$AUDIO$" + polygonIndex);
						// AudioInputStream copyStream = copyAudioInputStream(/*
						// finalStream */ais);
						// descAudio.add(copyStream);
						contextAdded = true;
					} else if (!audioCumText.isSelected()) {
						Utility.titles.set(polygonIndex, title.getText());
						Utility.descText.set(polygonIndex, textDesc.getText());
						// descAudio.set(polygonIndex, null);
						contextAdded = true;
					}
				}
				System.out.println("Title: " + title.getText());
				System.out.println("Description: " + textDesc.getText());
			}
			if (!contextAdded) {
				audioDesc.stopRecording();
			}
			return ((result == JOptionPane.OK_OPTION) && contextAdded);
		}
		return false;
	}

	protected static AudioInputStream copyAudioInputStream(AudioInputStream ais) throws IOException {
		AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File("C:/Users/Nikhil/Documents/trial2.wav"));
		File tempFile = File.createTempFile("wav", "tmp");
		AudioSystem.write(ais, AudioFileFormat.Type.WAVE, tempFile);
		// The fileToByteArray() method reads the file
		// into a byte array; omitted for brevity
		AudioInputStream aisCopy = null;
		try {
			aisCopy = AudioSystem.getAudioInputStream(tempFile);
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tempFile.delete();
		AudioSystem.write(aisCopy, AudioFileFormat.Type.WAVE, new File("C:/Users/Nikhil/Documents/trial1.wav"));

		return aisCopy;
	}

	public static void writeRecording(File file, AudioInputStream ais) {
		try {
			System.out.println("In utils.Recorder: Start recording...");
			// start recording
			System.out.println("Is recoding");
			AudioSystem.write(ais, FILE_TYPE, file);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
