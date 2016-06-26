import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class AudioPane extends JPanel {
	/**
	 * Auto-generated serialID
	 */
	private static final long serialVersionUID = -6483413280444018893L;

	private int polygonIndex = -1;
	private JToggleButton recordButton;
	private JButton playButton;
	private JButton stopButton;
	private TargetDataLine line;
	protected boolean stopRecordingCalled = false;

	SourceDataLine sourceDataLine;
	AudioInputStream audioInputStream;
	boolean stopPlayback = false;
	protected String audioFileName = null;

	public AudioPane(int polygonId) {
		polygonIndex = polygonId;
		
		setLayout(new GridBagLayout());
		recordButton = new JToggleButton("Record");
		recordButton.setEnabled(false);
		recordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (recordButton.isSelected()) {
					startRecording();
					recordButton.setText("Stop");
				} else {
					stopRecording();
					playButton.setEnabled(true);
					recordButton.setText("Record");
				}
			}
		});
		add(recordButton);

		playButton = new JButton("Play");
		playButton.setEnabled(false);
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopButton.setEnabled(true);
				playAudio(audioFileName);
			}
		});
		add(playButton);

		stopButton = new JButton("Stop");
		stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Terminate playback before EOF
				stopPlayback = true;
			}
		}
		);
		add(stopButton);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200, 200);
	}

	protected void stopRecording() {
		stopRecordingCalled = true;
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
						stopRecordingCalled = false;
						ContextDialogBox.format = getAudioFormat();
						DataLine.Info info = new DataLine.Info(TargetDataLine.class, ContextDialogBox.format);

						// checks if system supports the data line
						if (!AudioSystem.isLineSupported(info)) {
							System.out.println("Line not supported");
							System.exit(0);
						}
						line = (TargetDataLine) AudioSystem.getLine(info);
						line.open(ContextDialogBox.format);
						line.start(); // start capturing

						System.out.println("In utils.Recorder: Start capturing...");

						ContextDialogBox.ais = new AudioInputStream(line);
						if (audioFileName == null) {
							if (polygonIndex == -1) {
								audioFileName = "$AUDIO$" + Utility.audioCounter + ".wav";
							} else {
								audioFileName = "$AUDIO$" + polygonIndex + ".wav";
							}
							
						}
						File f = new File(audioFileName);
						ContextDialogBox.absPathTempFiles = f.getAbsoluteFile().getParent();

						System.out.println("tempPath : " + ContextDialogBox.absPathTempFiles);

						AudioSystem.write(ContextDialogBox.ais, AudioFileFormat.Type.WAVE, f);
						/*
						 * if (finalStream == null) { finalStream =
						 * copyAudioInputStream(ais); } else { toAppendStream =
						 * copyAudioInputStream(ais); }
						 */
					} catch (LineUnavailableException ex) {
						ex.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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

	// TODO Append doesn't work for wav format getFrameLength() returns -1
	/*
	 * protected void appendRecordings() throws IOException { if (finalStream ==
	 * null && toAppendStream != null) { finalStream =
	 * copyAudioInputStream(toAppendStream); toAppendStream = null; } else if
	 * (toAppendStream != null) { AudioInputStream appendedStreams = new
	 * AudioInputStream( new SequenceInputStream(finalStream, toAppendStream),
	 * finalStream.getFormat(), finalStream.getFrameLength() +
	 * toAppendStream.getFrameLength()); finalStream =
	 * copyAudioInputStream(appendedStreams); toAppendStream = null; } }
	 */

	private void playAudio(String fileName) {
		try {
			File soundFile = new File(fileName);
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, ContextDialogBox.format);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

			// Create a thread to play back the data and
			// start it running. It will run until the
			// end of file, or the Stop button is
			// clicked, whichever occurs first.
			// Because of the data buffers involved,
			// there will normally be a delay between
			// the click on the Stop button and the
			// actual termination of playback.
			new PlayThread().start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} // end catch
	}// end playAudio

	// Inner class to play back the data from the
	// audio file.
	class PlayThread extends Thread {
		byte tempBuffer[] = new byte[10000];

		public void run() {
			try {
				sourceDataLine.open(ContextDialogBox.format);
				sourceDataLine.start();

				int cnt;
				// Keep looping until the input read method
				// returns -1 for empty stream or the
				// user clicks the Stop button causing
				// stopPlayback to switch from false to
				// true.
				while ((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1 && stopPlayback == false) {
					if (cnt > 0) {
						// Write data to the internal buffer of
						// the data line where it will be
						// delivered to the speaker.
						sourceDataLine.write(tempBuffer, 0, cnt);
					} // end if
				} // end while
					// Block and wait for internal buffer of the
					// data line to empty.
				sourceDataLine.drain();
				sourceDataLine.close();

				// Prepare to playback another file
				stopPlayback = false;
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			} // end catch
		}// end run
	}// end inner class PlayThread
}