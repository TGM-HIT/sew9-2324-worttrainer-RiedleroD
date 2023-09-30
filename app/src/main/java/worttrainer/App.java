package worttrainer;

import java.io.IOException;
import java.util.Map;

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
		this.updateGUI(null);

		this.gui.onSubmitWord(event -> {
			this.updateGUI(tm.guessWord(event.getActionCommand()));
		});
		this.gui.onClose(event -> {
			try {
				ConfigMaster.setCache("session", tm);
			} catch (IOException e) {
				throw new RuntimeException(e);
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
