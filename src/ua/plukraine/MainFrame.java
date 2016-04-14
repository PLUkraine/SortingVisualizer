package ua.plukraine;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JFrame;

import ua.plukraine.algos.InsertionSorting;
import ua.plukraine.gui.SortPanel;

import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class MainFrame {

	private JFrame frame;
	private SortPanel s_panel;

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
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		s_panel = new SortPanel(new InsertionSorting());
		frame.getContentPane().add(s_panel);
		
		JButton btnAdvance = new JButton("Advance");
		btnAdvance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				s_panel.update();
				s_panel.repaint();
			}
		});
		frame.getContentPane().add(btnAdvance, BorderLayout.SOUTH);
	}

}
