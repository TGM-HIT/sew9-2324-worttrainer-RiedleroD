package worttrainer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.TextField;
import java.awt.event.ActionListener;
import java.net.URL;

public class TrainingWindow extends JPanel {
	private JLabel imageLabel;
	private Thread imageLoadingThread;
	private TextField textInput;
	private JTextArea statField;

	public TrainingWindow() {
		this.setLayout(new BorderLayout());

		this.imageLoadingThread = null;
		this.imageLabel = new JLabel("Initializing…", SwingConstants.CENTER);
		this.add(this.imageLabel, BorderLayout.CENTER);

		this.textInput = new TextField();
		this.add(this.textInput, BorderLayout.NORTH);

		this.statField = new JTextArea();
		this.statField.setEditable(false);
		this.add(this.statField, BorderLayout.WEST);
		
		this.setStats(null, 0, 0, 0);
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
		this.imageLabel.setText("Loading…");

		// TODO: image cache

		// creating a new thread to load, scale and display the image
		this.imageLoadingThread = new Thread() {
			public void run() {
				ImageIcon icon = new ImageIcon(url);
				Image image = icon.getImage(); // transform to image
				// TODO: scale to what the container needs
				// int width = (int)(icon.getIconWidth() * (250.0 / icon.getIconHeight())); //
				// (250 / height) → scaling factor
				// image = image.getScaledInstance(width, 250, Image.SCALE_SMOOTH); // skalieren
				// auf gewünschte Größe
				ImageIcon ii = new ImageIcon(image);
				imageLabel.setIcon(ii); // anzeigen in einem JLabel
				imageLabel.setText(null);
			}
		};

		this.imageLoadingThread.start();
	}

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
}