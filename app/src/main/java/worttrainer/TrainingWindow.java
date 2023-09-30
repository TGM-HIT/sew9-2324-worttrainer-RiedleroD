package worttrainer;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public class TrainingWindow extends JFrame{
	private JLabel imageLabel;
	private Thread imageLoadingThread;
	private TextField textInput;
	private JComboBox<String> datasetDropdown;
	private JTextArea statField;

	public TrainingWindow(String[] datasets) {
		super("WortTrainer");

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 2, 2, 2);

		this.imageLoadingThread = null;
		this.imageLabel = new JLabel("", SwingConstants.CENTER);

		this.textInput = new TextField();

		this.datasetDropdown = new JComboBox<String>(datasets);

		this.statField = new JTextArea();
		this.statField.setEditable(false);

		this.setStats(null, 0, 0, 0);

		c.gridx = 0;
		c.gridy = 0;
		panel.add(this.datasetDropdown, c.clone());
		c.gridx = 1;
		c.gridy = 0;
		panel.add(this.textInput, c.clone());
		c.gridx = 0;
		c.gridy = 1;
		panel.add(this.statField, c.clone());
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		panel.add(this.imageLabel, c.clone());

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(panel);
		this.pack();
		this.setVisible(true);

		this.onSubmitWord(e -> textInput.setText(""));
	}

	/**
	 * Adds an action listener to the word input
	 * 
	 * @param al the actionListener callback
	 */
	public void onSubmitWord(ActionListener al) {
		this.textInput.addActionListener(al);
	}
	
	/**
	 * Adds a listener to the window closing
	 *
	 * @param cb the callback
	 */
	public void onClose(Consumer<WindowEvent> cb) {
		this.addWindowListener(
			new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent event) {
					cb.accept(event);
				}
			});
	}
	
	/**
	 * Adds a listener for when a dataset is selected
	 * @param al the callback
	 */
	public void onSelectDataset(ActionListener al){
		this.datasetDropdown.addActionListener(al);
	}

	/**
	 * Sets the center image to the source of the URL
	 * the url is assumed to be valid (a warning will be displayed if it isn't)
	 * a loading message is displayed until the image is loaded
	 * loads &amp; displays the image in a separate thread so the method doesn't
	 * block
	 * supports jpg, png
	 * does not support svg, webp
	 * 
	 * @param url the url to an image
	 */
	public void setImage(URL url) {
		this.imageLabel.setIcon(null);
		if(url==null) return;
		this.imageLabel.setText("Loading…");

		// creating a new thread to load, scale and display the image
		this.imageLoadingThread = new Thread() {
			public void run() {
				ImageIcon icon;
				try {
					icon = ConfigMaster.cachedImage(url);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				imageLabel.setIcon(icon);
				imageLabel.setText("");
			}
		};

		this.imageLoadingThread.start();
	}

	/**
	 * Sets the textbox containing the stats
	 * 
	 * @param prevCorrect whether the previous guess was correct (null to hide the
	 *                    line)
	 * @param mistakes    how many mistakes were made thus far
	 * @param completed   how many words have been correctly entered
	 * @param remaining   how many entries remain to be guessed on
	 */
	public void setStats(Boolean prevCorrect, int mistakes, int completed, int remaining) {
		StringBuilder sb = new StringBuilder();

		sb.append("Mistakes: ");
		sb.append(mistakes);
		sb.append("\nCorrect: ");
		sb.append(completed);
		sb.append("\nRemaining: ");
		sb.append(remaining);

		if (prevCorrect != null) {
			sb.append("\nThe previous guess was ");
			sb.append(prevCorrect);
		}

		this.statField.setText(sb.toString());
	}
	
	/**
	 * Replaces the stats field with a helper message
	 */
	public void clearStats(){
		this.statField.setText("Select a Category ↑");
	}
}