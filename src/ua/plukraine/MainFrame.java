package ua.plukraine;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import ua.plukraine.algos.InsertionSorting;
import ua.plukraine.algos.QuickSortRandomPivot;
import ua.plukraine.gui.SortPanel;

import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JCheckBoxMenuItem;


public class MainFrame {
	private enum MouseState {
		Normal, DeletePanel
	}
	private class PanelWithPosition {
		public int r, c;
		public SortPanel p;
		public PanelWithPosition(int row, int column, SortPanel panel) {
			r = row; c = column;
			p = panel;
		}
	}

	private JFrame frame;
	private List<PanelWithPosition> sort_panels = new ArrayList<>(MAX_PANELS);
	private JSlider slider;
	private JButton start_stop_btn;
	private Timer tactTimer;
	
	/** Initial timer tick period */
	private static final int LONG_DELAY = 500;
	private static final int MAX_PANELS = 4;
	
	/** Check if next button press must start sorting */
	private boolean btn_must_start = true;
	private MouseState mouseState = MouseState.Normal;
	private JPanel sortPanelsCont;
	private JMenuBar menuBar;
	private JMenu algoMenu;
	private JMenuItem addPanelMenu;
	private JSeparator separPanelAndLoad;
	private JMenuItem loadAlgosMenu;
	private JMenuItem resetArrMenu;
	private JCheckBoxMenuItem toggleRemovePanelMenu;

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
//					for (SortPanel panel : window.sort_panels) {
//						//panel.feedArray(new int[]{ 4, 3, 2, 8, 8, 3, 2, 6 });
//						//panel.feedArray(new int[] {20,14, 9, 16, 8, 10, 17, 14, 10, 9, 19, 14, 3, 19, 18, 17, 19, 14, 7, 8, 10});
//						panel.feedArray(new int[]{14, 13, 14, 21, 6, 10, 49, 35, 4, 47, 13, 45, 33, 23, 5, 18, 15, 6, 43, 27, 7, 4, 2, 27, 38, 30, 38, 18, 34, 17, 17, 28, 3, 46, 7, 15, 9, 27, 47, 12, 20, 30, 38, 33, 35, 37, 35, 20, 44, 11});
//					}
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
		tactTimer = new Timer(LONG_DELAY, (e) -> {
			updateSortingPanels();
		});
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				onResize();
			}
		});
		
		// Create center panel with sorting panels
		sortPanelsCont = new JPanel();
		frame.getContentPane().add(sortPanelsCont, BorderLayout.CENTER);
		sortPanelsCont.setLayout(new GridBagLayout());
		
		// Create lower panel with slider and button
		JPanel utilsPanel = new JPanel();
		frame.getContentPane().add(utilsPanel, BorderLayout.SOUTH);
		utilsPanel.setLayout(new GridLayout(2, 0, 0, 3));
		
		slider = new JSlider(275, 2000, LONG_DELAY);
		slider.addChangeListener((e) -> {
			onChangeUpdateFrequency();
		});
		utilsPanel.add(slider);
		
		start_stop_btn = new JButton("Start/Stop");
		start_stop_btn.addActionListener((e) -> {
			onStartStopToggled();
		});
		utilsPanel.add(start_stop_btn);
		
		
		// Menu bar here
		menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		algoMenu = new JMenu("Algorithms");
		menuBar.add(algoMenu);
		
		addPanelMenu = new JMenuItem("Add panel...");
		addPanelMenu.addActionListener((e) -> {
			onAddPanel();
		});
		algoMenu.add(addPanelMenu);
		
		toggleRemovePanelMenu = new JCheckBoxMenuItem("Delete panel on click");
		toggleRemovePanelMenu.addActionListener((e) -> {
			onToggleRemoveMenu();
		});
		algoMenu.add(toggleRemovePanelMenu);
		
		resetArrMenu = new JMenuItem("Reset array...");
		algoMenu.add(resetArrMenu);
		
		separPanelAndLoad = new JSeparator();
		algoMenu.add(separPanelAndLoad);
		
		loadAlgosMenu = new JMenuItem("Load algorithms...");
		algoMenu.add(loadAlgosMenu);
	}

	/**
	 * Stop timer
	 */
	public void stopSorting() {
		//Arrays.stream(sort_panels).forEach((p) -> p.abortAnimation());
		btn_must_start = true; // !!!
		sort_panels.stream().forEach((p) -> p.p.abortAnimation());
		tactTimer.stop();
	}
	
	/**
	 * Start timer
	 */
	public void startSorting() {
		//Arrays.stream(sort_panels).forEach((p) -> p.abortAnimation());
		btn_must_start = false; // !!!
		sort_panels.stream().forEach((p) -> p.p.abortAnimation());
		tactTimer.start();
	}
	
	/**
	 * Prompt user to choose algorithm and create panel to represent that algorithm
	 */
	protected void onAddPanel() {
		// TODO implement 
		if (sort_panels.size() >= MAX_PANELS) {
			JOptionPane.showMessageDialog(frame, "Can't add panel, too many of them!");
			return;
		}
		
		int sqRoot = (int)Math.sqrt(MAX_PANELS);
		int col = (sort_panels.size()) / sqRoot;
		int row = (sort_panels.size()) % sqRoot;
		SortPanel panel = new SortPanel(new InsertionSorting());
		
		PanelWithPosition pwp = new PanelWithPosition(row, col, panel);
		panel.addMouseListener(new MouseAdapter() {			
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				onSortPanelMouseClick(e.getComponent());
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				if (mouseState == MouseState.DeletePanel && e.getComponent() instanceof SortPanel) {
					SortPanel p = (SortPanel)e.getComponent();
					p.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				if (mouseState == MouseState.DeletePanel && e.getComponent() instanceof SortPanel) {
					SortPanel p = (SortPanel)e.getComponent();
					p.setBorder(BorderFactory.createEmptyBorder());
				}
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = row; c.gridy = col;
		c.insets = new Insets(2, 2, 2, 2);
		sortPanelsCont.add(panel, c);
		
		sort_panels.add(pwp);
		
		panel.feedArray(new int[]{14, 13, 14, 21, 6, 10, 49, 35, 4, 47, 13, 45, 33, 23, 5, 18, 15, 6, 43, 27, 7, 4, 2, 27, 38, 30, 38, 18, 34, 17, 17, 28, 3, 46, 7, 15, 9, 27, 47, 12, 20, 30, 38, 33, 35, 37, 35, 20, 44, 11});
		frame.getContentPane().validate();
		JOptionPane.showMessageDialog(frame, "Added panel");
	}
	
	/**
	 * Change update period using slider's value
	 */
	protected void onChangeUpdateFrequency() {
		tactTimer.stop();
		
		int newval = (int)(slider.getValue() * (1 - 2./3) / SortPanel.timer_tick);
		tactTimer.setDelay(slider.getValue());
		for (PanelWithPosition p : sort_panels) {
			p.p.setAnimationFramesDuration(newval);
		}
		if (!btn_must_start)
			tactTimer.start();
	}
	
	/**
	 * Toggle start/stop
	 */
	protected void onStartStopToggled() {
		if (btn_must_start) {
			startSorting();
		} else {
			stopSorting();
		}
	}
	
	/**
	 * Make one move of the algorithms in sorting panels
	 */
	protected void updateSortingPanels() {
		boolean allFinished = true;
		for (PanelWithPosition p : sort_panels) {
			p.p.update();
			allFinished &= p.p.hasFinished();
		}
		if (allFinished) {
			tactTimer.stop();
		}
	}
	
	/**
	 * Process click on sort panel
	 * @param sender - event source
	 */
	protected void onSortPanelMouseClick(Component sender) {
		if (mouseState == MouseState.DeletePanel) {
			removePanel(sender);
		}
	}
	
	protected void onResize() {
		frame.getContentPane().invalidate();
	}
	
	protected void onToggleRemoveMenu() {
		if (mouseState != MouseState.DeletePanel) {
			mouseState = MouseState.DeletePanel;
		} else {
			mouseState = MouseState.Normal;
		}
	}
	
	protected void removePanel(Component panelToRemove) {
		if (panelToRemove instanceof SortPanel) {
			sort_panels.removeIf((p) -> p.p.equals(panelToRemove));
			int sq = (int)Math.sqrt(MAX_PANELS);
			for (int i=0; i<sort_panels.size(); ++i) {
				PanelWithPosition p = sort_panels.get(i);
				p.r = i%sq;
				p.c = i/sq;
			}
			sortPanelsCont.removeAll();
			for (PanelWithPosition p : sort_panels) {
				GridBagConstraints c = new GridBagConstraints();
				c.weightx = c.weighty = 1.0;
				c.fill = GridBagConstraints.BOTH;
				c.gridx = p.r; c.gridy = p.c;
				c.insets = new Insets(2, 2, 2, 2);
				sortPanelsCont.add(p.p, c);
			}
			frame.getContentPane().revalidate();
			frame.repaint();
		}
	}
}
