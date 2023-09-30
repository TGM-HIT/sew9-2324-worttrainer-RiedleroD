package worttrainer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Map;

import javax.swing.JFrame;

import com.fasterxml.jackson.core.type.TypeReference;

public class App {
	private TrainingWindow gui;
	private TrainingMaster tm;

	private App() {
		this.tm = ConfigMaster.getCache("session", new TypeReference<TrainingMaster>(){});
		if (this.tm == null) {
			try {
				Map<String, WebReader> wrm = ConfigMaster.getSetting("datasets",
						new TypeReference<Map<String, WebReader>>(){});
				// dropdown choice
				WebReader wr = wrm.get("Länderflaggen");
				this.tm = new TrainingMaster(wr.parseSite());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		this.gui = new TrainingWindow();

		JFrame frame = new JFrame("WortTrainer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this.gui);
		frame.pack();
		frame.setVisible(true);

		this.updateGUI(null);

		this.gui.onSubmitWord(e -> {
			this.updateGUI(tm.guessWord(e.getActionCommand()));
		});
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				try {
					ConfigMaster.setCache("session", tm);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
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
