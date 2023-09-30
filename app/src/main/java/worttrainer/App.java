package worttrainer;

import java.io.IOException;
import java.util.Map;

import javax.swing.JComboBox;

import com.fasterxml.jackson.core.type.TypeReference;

public class App {
	private TrainingWindow gui;
	private TrainingMaster tm;
	private Map<String, WebReader> datasets;

	private App() {
		this.tm = ConfigMaster.getCache("session", new TypeReference<TrainingMaster>(){});
		
		try {
			this.datasets = ConfigMaster.getSetting("datasets",
				new TypeReference<Map<String, WebReader>>(){});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.gui = new TrainingWindow(this.datasets.keySet().toArray(new String[this.datasets.size()]));
		this.updateGUI(null);

		this.gui.onSubmitWord(event -> {
			if (tm != null){
				boolean correct = tm.guessWord(event.getActionCommand());
				if(tm.getRemainingEntries()==0)
					tm=null;
				this.updateGUI(correct);
			}
			
		});
		this.gui.onClose(event -> {
			try {
				ConfigMaster.setCache("session", tm);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		this.gui.onSelectDataset(event -> {
			gui.setImage(null);
			String name = (String)((JComboBox<String>) event.getSource()).getSelectedItem();
			WebReader wr = datasets.get(name);
			new Thread() {
				public void run() {
					tm = new TrainingMaster(wr.parseSite(), name);
					updateGUI(null);
				}
			}.start();
		});
	}

	/**
	 * update the GUI with updated data from the TrainingMaster
	 * 
	 * @param correct whether the previous guess was correct (null for N/A)
	 */
	private void updateGUI(Boolean correct) {
		if (this.tm == null) {
			this.gui.clearStats();
		} else {
			this.gui.setStats(
					correct,
					this.tm.getIncorrectEntries(),
					this.tm.getCompletedEntries(),
					this.tm.getRemainingEntries());
			this.gui.setImage(
					this.tm.getImage());
		}
	}

	public static void main(String[] args) {
		new App();
	}
}
