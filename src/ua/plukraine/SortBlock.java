package ua.plukraine;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import ua.plukraine.MainFrame.SortPanelMouseHandler;
import ua.plukraine.algos.ISortingAlgortihm;
import ua.plukraine.gui.SortPanel;

@SuppressWarnings("serial")
public class SortBlock extends JPanel {
	public static final int MAX_PANELS = 4;
	public static final int MAX_ARR_LEN = 100;
	private static final double ANIMATION_RATIO = 1.0/2;
	
	/** Array for sorting */
	private int[] cur_array = new int[] {13, 2, 16, 6, 7, 14, 3, 17, 20, 18, 2, 13, 6, 7, 3, 4, 6, 10, 20, 4};
	private List<SortPanel> sort_panels = new ArrayList<>(MAX_PANELS);
	private Timer tactTimer;
	private SortPanelMouseHandler panelMouseHandler;
	
	
	public SortBlock(int initDelay, SortPanelMouseHandler mouseHandler) {	
		tactTimer = new Timer(initDelay, (e) -> {
			updateSortingPanels();
		});
		this.setLayout(new GridBagLayout());
		this.panelMouseHandler = mouseHandler;
	}
	
	public void startSorting() {
		sort_panels.stream().forEach((p) -> p.abortAnimation());
		tactTimer.start();
	}
	public void stopSorting() {
		sort_panels.stream().forEach((p) -> p.abortAnimation());
		tactTimer.stop();
	}
	/**
	 * Prompt user to choose algorithm and create panel to represent that algorithm
	 */
	public void addPanel(ISortingAlgortihm algo, int updateDuration) throws IllegalArgumentException {
		int sqRoot = (int)Math.sqrt(MAX_PANELS);
		SortPanel panel = new SortPanel(algo, calculateAnimationDuration(updateDuration));
		panel.addMouseListener(panelMouseHandler);
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = (sort_panels.size()) % sqRoot; 
		c.gridy = (sort_panels.size()) / sqRoot;
		c.insets = new Insets(2, 2, 2, 2);
		this.add(panel, c);
		
		sort_panels.add(panel);
		
		panel.feedArray(cur_array);
		this.validate();
	}
	/**
	 * Remove given sort panel and move all remaining components
	 * @param panelToRemove - panel to remove
	 */
	public void removePanel(Component panelToRemove) {
		stopSorting();
		if (panelToRemove instanceof SortPanel) {
			sort_panels.removeIf((p) -> p.equals(panelToRemove));
			// reorder panels
			int sq = (int)Math.sqrt(MAX_PANELS);
			this.removeAll();
			for (int i=0; i<sort_panels.size(); ++i) {
				SortPanel p = sort_panels.get(i);
				GridBagConstraints c = new GridBagConstraints();
				c.weightx = c.weighty = 1.0;
				c.fill = GridBagConstraints.BOTH;
				c.insets = new Insets(2, 2, 2, 2);
				c.gridx = i%sq; c.gridy = i/sq;
				this.add(p, c);
			}
			// repaint component
			this.revalidate();
			this.repaint();
		}
	}
	/**
	 * Change array for sorting
	 */
	public void resetArray(int [] arr) {
		cur_array = arr;
		resetPanels();
	}
	/**
	 * Reset sort panels to initial state
	 */
	public void resetPanels() {
		stopSorting();
		for (SortPanel p : sort_panels) {
			p.feedArray(cur_array);
		}
	}
	/**
	 * Change update period using slider's value
	 */
	public void changeUpdateFrequency(int value, boolean goOn) {
		tactTimer.stop();
		
		int newval = calculateAnimationDuration(value);
		tactTimer.setDelay(value);
		for (SortPanel p : sort_panels) {
			p.setAnimationFramesDuration(newval);
		}
	}
	/**
	 * Make one move of the algorithms in sorting panels
	 */
	public void updateSortingPanels() {
		boolean allFinished = true;
		for (SortPanel p : sort_panels) {
			p.update();
			allFinished &= p.hasFinished();
		}
		if (allFinished) {
			tactTimer.stop();
		}
	}
	public int panelsCount() {
		return sort_panels.size();
	}
	
	protected int calculateAnimationDuration(int updateTime) {
		return (int)(updateTime * ANIMATION_RATIO / SortPanel.timer_tick);
	}
}
