package ua.plukraine;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import ua.plukraine.algos.InsertionSorting;
import ua.plukraine.algos.QuickSortRandomPivot;
import ua.plukraine.gui.SortPanel;

import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.awt.event.ActionEvent;


public class MainFrame {

	private JFrame frame;
	private SortPanel[] sort_panels;
	private JSlider slider;
	private JButton start_stop_btn;
	private Timer tactTimer;
	
	private static final int LONG_DELAY = 500;
	
	private boolean btn_must_start = true;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					window.frame.setVisible(true);
					for (SortPanel panel : window.sort_panels) {
						//panel.feedArray(new int[]{ 4, 3, 2, 8, 8, 3, 2, 6 });
						//panel.feedArray(new int[] {20,14, 9, 16, 8, 10, 17, 14, 10, 9, 19, 14, 3, 19, 18, 17, 19, 14, 7, 8, 10});
						panel.feedArray(new int[]{14, 13, 14, 21, 6, 10, 49, 35, 4, 47, 13, 45, 33, 23, 5, 18, 15, 6, 43, 27, 7, 4, 2, 27, 38, 30, 38, 18, 34, 17, 17, 28, 3, 46, 7, 15, 9, 27, 47, 12, 20, 30, 38, 33, 35, 37, 35, 20, 44, 11});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		ActionListener timerAction = (e) -> {
			boolean allFinished = true;
			for (SortPanel p : sort_panels) {
				p.update();
				allFinished &= p.hasFinished();
			}
			if (allFinished) {
				tactTimer.stop();
			}
		};
		tactTimer = new Timer(LONG_DELAY, timerAction);
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gridBagLayout);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		
		// TODO client should add and remove panels
		sort_panels = new SortPanel[2];
		sort_panels[0] = new SortPanel(new InsertionSorting());
		sort_panels[1] = new SortPanel(new QuickSortRandomPivot());
		//sort_panels[2] = new SortPanel(new InsertionSorting());
		
		slider = new JSlider(275, 2000, LONG_DELAY);
		slider.addChangeListener((e) -> {
			tactTimer.stop();
			
			int newval = (slider.getValue() - 3*slider.getValue()/8) / SortPanel.timer_tick;
			tactTimer.setDelay(slider.getValue());
			for (SortPanel p : sort_panels) {
				p.setAnimationFramesDuration(newval);
			}
			if (!btn_must_start)
				tactTimer.start();
		});
		
		start_stop_btn = new JButton("Start/Stop");
		start_stop_btn.addActionListener((e) -> {
			if (btn_must_start) {
				startSorting();
			} else {
				stopSorting();
			}
			btn_must_start = !btn_must_start;
		});
		
		frame.getContentPane().add(sort_panels[0], c);
		c.gridx = 1;
		frame.getContentPane().add(sort_panels[1], c);
//		c.gridx = 0; c.gridy = 1;
//		frame.getContentPane().add(sort_panels[2], c);
		
		c.gridx = 0; c.gridy = 1;
		c.weighty = 0; c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		frame.add(slider, c);
		
		c.gridx = 0; c.gridy = 2;
		c.weighty = 0; c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		frame.add(start_stop_btn, c);
	}

	public void stopSorting() {
		Arrays.stream(sort_panels).forEach((p) -> p.abortAnimation());
		tactTimer.stop();
	}
	
	public void startSorting() {
		Arrays.stream(sort_panels).forEach((p) -> p.abortAnimation());
		tactTimer.start();
	}
}
