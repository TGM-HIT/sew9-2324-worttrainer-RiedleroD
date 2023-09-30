package worttrainer;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;

public class App {
	private TrainingWindow gui;
	private TrainingMaster tm;

	private App() {
		this.gui = new TrainingWindow();

		JFrame frame = new JFrame("WortTrainer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this.gui);
		frame.pack();
		frame.setVisible(true);

		// TODO: select different lists
		try {
			this.tm = new TrainingMaster(WebReader.parseSite(
					new URL("https://de.wikipedia.org/wiki/Liste_der_Staaten_Europas"),
					".wikitable",
					":eq(0) .noviewer img",
					":eq(0) .noviewer+a"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		this.updateGUI(null);

		this.gui.onSubmitWord(e -> {
			this.updateGUI(tm.guessWord(e.getActionCommand()));
		});
	}

	/**
	 * update the GUI with updated data from the TrainingMaster
	 * 
	 * @param correct whether the previous guess was correct
	 */
	private void updateGUI(Boolean correct) {
		this.gui.setStats(correct, tm.getIncorrectEntries(), tm.getCompletedEntries(), tm.getRemainingEntries());
		this.gui.setImage(tm.getImage());
	}

	public static void main(String[] args) {
		new App();
	}
}
