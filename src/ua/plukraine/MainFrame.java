package ua.plukraine;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

import ua.plukraine.algos.ISortingAlgortihm;
import ua.plukraine.gui.SortPanel;
import ua.plukraine.utils.UserDialogHelper;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JCheckBoxMenuItem;


public class MainFrame {
	/**
	 * State for mouse event
	 */
	private enum MouseState {
		Normal, DeletePanel
	}

	private UserDialogHelper dialogHelper = new UserDialogHelper();
	private SortBlock block;
	private MouseState mouseState = MouseState.Normal;
	private JFrame frame;
	private JSlider slider;
	private JButton start_stop_btn;
	
	/** Check if next button press must start sorting */
	private boolean btn_must_start = true;
	private JMenuBar menuBar;
	private JMenu algoMenu;
	private JMenuItem addPanelMenu;
	private JSeparator separPanelAndLoad;
	private JMenuItem loadAlgosMenu;
	private JMenuItem resetArrMenu;
	private JCheckBoxMenuItem toggleRemovePanelMenu;
	private JMenuItem resetPanelMenu;
	private JButton btnStep;
	private JButton btnReset;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frame.setTitle("Sorting Visualizer");
					window.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					window.frame.setVisible(true);
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
		final int initDelay = 500;
		
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
		block = new SortBlock(initDelay, new SortPanelMouseHandler());
		frame.getContentPane().add(block, BorderLayout.CENTER);
		
		// Create lower panel with slider and button
		JPanel utilsPanel = new JPanel();
		frame.getContentPane().add(utilsPanel, BorderLayout.SOUTH);
		utilsPanel.setLayout(new GridLayout(2, 2, 0, 3));
		
		slider = new JSlider(230, 2000, initDelay);
		slider.addChangeListener((e) -> {
			onChangeUpdateFrequency();
		});
		slider.setInverted(true);
		utilsPanel.add(slider);
		
		start_stop_btn = new JButton("Start/Stop");
		start_stop_btn.addActionListener((e) -> {
			onStartStopToggled();
		});
		
		btnStep = new JButton("Step");
		btnStep.addActionListener((e) -> {
			onStep();
		});
		utilsPanel.add(btnStep);
		utilsPanel.add(start_stop_btn);
		
		btnReset = new JButton("Reset");
		btnReset.addActionListener((e) -> {
			resetPanels();
		});
		utilsPanel.add(btnReset);
		
		
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
		resetArrMenu.addActionListener((e) -> {
			resetArray();
		});
		algoMenu.add(resetArrMenu);
		
		resetPanelMenu = new JMenuItem("Reset panel");
		resetPanelMenu.addActionListener((e) -> {
			resetPanels();
		});
		algoMenu.add(resetPanelMenu);
		
		separPanelAndLoad = new JSeparator();
		algoMenu.add(separPanelAndLoad);
		
		loadAlgosMenu = new JMenuItem("Load algorithms...");
		loadAlgosMenu.addActionListener((e) -> {
			loadClasses();
		});
		algoMenu.add(loadAlgosMenu);
	}

	/**
	 * Stop sorting and make toggle button start on press
	 */
	public void stopSorting() {
		btn_must_start = true;
		block.stopSorting();
	}
	
	/**
	 * Start sorting and make toggle button pause on press
	 */
	public void startSorting() {
		btn_must_start = false;
		block.startSorting();
	}
	
	/**
	 * Add one panel
	 */
	public void onAddPanel() {
		stopSorting();
		if (block.panelsCount() >= SortBlock.MAX_PANELS) {
			JOptionPane.showMessageDialog(frame, "Can't add panel, too many of them!");
			return;
		}
		
		ISortingAlgortihm chosen = dialogHelper.pickAlgorithm(frame);
		if (chosen == null) return;
		block.addPanel(chosen, slider.getValue());
		resetPanels();
	}
	
	/**
	 * Change update period using slider's value
	 */
	protected void onChangeUpdateFrequency() {
		block.changeUpdateFrequency(slider.getValue(), !btn_must_start);
		if (!btn_must_start)
			block.startSorting();
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
		block.updateSortingPanels();
	}
	
	/**
	 * Repaint correctly on resize event
	 */
	protected void onResize() {
		frame.getContentPane().invalidate();
	}
	/**
	 * Toggle delete panel state
	 */
	protected void onToggleRemoveMenu() {
		if (mouseState != MouseState.DeletePanel) {
			mouseState = MouseState.DeletePanel;
		} else {
			mouseState = MouseState.Normal;
		}
	}
	/**
	 * Remove given sort panel and move all remaining components
	 * @param panelToRemove - panel to remove
	 */
	protected void removePanel(Component panelToRemove) {
		stopSorting();
		block.removePanel(panelToRemove);
	}
	/**
	 * Change array for sorting
	 */
	protected void resetArray() {
		stopSorting();
		int[] arr = dialogHelper.getArray(frame, SortBlock.MAX_ARR_LEN);
		if (arr != null) {
			block.resetArray(arr);
		}
	}
	
	/**
	 * Reset sort panels to initial state
	 */
	protected void resetPanels() {
		stopSorting();
		block.resetPanels();
	}
	
	/**
	 * Show dialog and load classes from chosen folder
	 */
	protected void loadClasses() {
		dialogHelper.loadAlgorithms(frame);
	}
	/**
	 * Human step handler
	 */
	protected void onStep() {
		stopSorting();
		updateSortingPanels();
	}
	
	/**
	 * Sort panel mouse event handler
	 */
	public class SortPanelMouseHandler extends MouseAdapter{			
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			if (mouseState == MouseState.DeletePanel) {
				removePanel(e.getComponent());
			}
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			super.mouseEntered(e);
			// create a border if mouse hovers over panel and state is DELETE
			if (mouseState == MouseState.DeletePanel && e.getComponent() instanceof SortPanel) {
				SortPanel p = (SortPanel)e.getComponent();
				p.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			}
		}
		@Override
		public void mouseExited(MouseEvent e) {
			super.mouseExited(e);
			// destroy border if mouse gets out of panel
			if (mouseState == MouseState.DeletePanel && e.getComponent() instanceof SortPanel) {
				SortPanel p = (SortPanel)e.getComponent();
				p.setBorder(BorderFactory.createEmptyBorder());
			}
		}
	}
}


