package ua.plukraine;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Timer;

import ua.plukraine.algos.InsertionSorting;
import ua.plukraine.gui.SortPanel;

import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class MainFrame {

	private JFrame frame;
	private SortPanel s_panel;
	private Timer long_timer;
	private Timer short_timer;
	
	private static final int LONG_DELAY = 750;
	private static final int SHORT_DELAY = 5;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frame.setVisible(true);
					window.s_panel.feedArray(new int[]{ 4, 3, 2, 8, 8, 3, 2, 6 });
					window.long_timer.start();
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
		long_timer = new Timer(LONG_DELAY, (e) -> {
			System.out.println("LONG TICKS");
			s_panel.update();
			s_panel.repaint();
			if (s_panel.hasFinished())
				long_timer.stop();
			if (s_panel.hasAnimation()) {
				long_timer.stop();
				short_timer.start();
			}
		});
		short_timer = new Timer(SHORT_DELAY, (e) -> {
			System.out.println("SHORT TICKS");
			boolean updated = s_panel.updateAnimation();
			s_panel.repaint();
			if (s_panel.hasFinished())
				short_timer.stop();
			if (!updated) {
				short_timer.stop();
				long_timer.start();
			}
		});
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		s_panel = new SortPanel(new InsertionSorting());
		frame.getContentPane().add(s_panel);
	}

}
