package worttrainer;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;

public class App {
	private TrainingWindow gui;

	public App() {
		this.gui = new TrainingWindow();

		JFrame frame = new JFrame("WortTrainer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this.gui);
		frame.pack();
		frame.setVisible(true);

		// TODO: test behaviour
		URL url;
		try {
			url = new URL("https://riedler.wien/favicon.png");
			this.gui.onSubmitWord(e -> this.gui.setImage(url));
		} catch (MalformedURLException e) {

		}
	}

	public static void main(String[] args) {
		new App();
	}
}
