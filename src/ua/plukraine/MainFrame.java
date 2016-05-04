package ua.plukraine;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

import ua.plukraine.algos.AlgorithmClassLoader;
import ua.plukraine.algos.ISortingAlgortihm;
import ua.plukraine.algos.InsertionSorting;
import ua.plukraine.algos.QuickSortRandomPivot;
import ua.plukraine.gui.SortPanel;

import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JCheckBoxMenuItem;


public class MainFrame {
	/**
	 * State for mouse event
	 */
	private enum MouseState {
		Normal, DeletePanel
	}

	private JFileChooser fileChooser;
	private JFrame frame;
	private List<SortPanel> sort_panels = new ArrayList<>(MAX_PANELS);
	private JSlider slider;
	private JButton start_stop_btn;
	private Timer tactTimer;
	/** Array for sorting */
	private int[] cur_array = new int[] {13, 2, 16, 6, 7, 14, 3, 17, 20, 18, 2, 13, 6, 7, 3, 4, 6, 10, 20, 4};
	private Set<Class> algo_list = new HashSet<Class>();
	
	/** Initial timer tick period */
	private static final int LONG_DELAY = 500;
	private static final int MAX_PANELS = 4;
	private static final int MAX_ARR_LEN = 100;
	
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
	private JMenuItem resetPanelMenu;

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
		// TODO remove
		algo_list.add(InsertionSorting.class);
		algo_list.add(QuickSortRandomPivot.class);
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
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
	 * Stop timer
	 */
	public void stopSorting() {
		//Arrays.stream(sort_panels).forEach((p) -> p.abortAnimation());
		btn_must_start = true; // !!!
		sort_panels.stream().forEach((p) -> p.abortAnimation());
		tactTimer.stop();
	}
	
	/**
	 * Start timer
	 */
	public void startSorting() {
		//Arrays.stream(sort_panels).forEach((p) -> p.abortAnimation());
		btn_must_start = false; // !!!
		sort_panels.stream().forEach((p) -> p.abortAnimation());
		tactTimer.start();
	}
	
	/**
	 * Prompt user to choose algorithm and create panel to represent that algorithm
	 */
	@SuppressWarnings({"rawtypes"})
	protected void onAddPanel() {
		stopSorting();
		if (sort_panels.size() >= MAX_PANELS) {
			JOptionPane.showMessageDialog(frame, "Can't add panel, too many of them!");
			return;
		}
		
		// TODO put in separate function
		List<ISortingAlgortihm> instances = new ArrayList<>();
		for (Class a : algo_list) {
			try {
				ISortingAlgortihm algo = (ISortingAlgortihm)a.newInstance();
				instances.add(algo);
			} catch ( IllegalAccessException 
					| InstantiationException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
		// Add numbers to the names of algorithms
		Object[] choises = IntStream.range(0, instances.size())
			.mapToObj((i) -> i + ". " + instances.get(i).getName()).toArray();
		String o = JOptionPane.showInputDialog(frame, "Choose algorithm", "Algorithm required", JOptionPane.QUESTION_MESSAGE,
				null, choises, choises[0]).toString();
		if (o == null) {
			return;
		}
		// Use numbers on beginning to determine the algorithm
		ISortingAlgortihm chosen = instances.get(Integer.parseInt(o.substring(0, o.indexOf('.'))));
		
		
		int sqRoot = (int)Math.sqrt(MAX_PANELS);
		int col = (sort_panels.size()) / sqRoot;
		int row = (sort_panels.size()) % sqRoot;
		SortPanel panel = new SortPanel(chosen);
		panel.addMouseListener(new SortPanelMouseHandler());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = row; c.gridy = col;
		c.insets = new Insets(2, 2, 2, 2);
		sortPanelsCont.add(panel, c);
		
		sort_panels.add(panel);
		
		panel.feedArray(cur_array);
		frame.getContentPane().validate();
		
		resetPanels();
	}
	
	/**
	 * Sort panel mouse event handler
	 */
	protected class SortPanelMouseHandler extends MouseAdapter{			
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
	}
	
	/**
	 * Change update period using slider's value
	 */
	protected void onChangeUpdateFrequency() {
		tactTimer.stop();
		
		int newval = (int)(slider.getValue() * (1 - 3./4) / SortPanel.timer_tick);
		tactTimer.setDelay(slider.getValue());
		for (SortPanel p : sort_panels) {
			p.setAnimationFramesDuration(newval);
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
		for (SortPanel p : sort_panels) {
			p.update();
			allFinished &= p.hasFinished();
		}
		if (allFinished) {
			tactTimer.stop();
		}
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
		if (panelToRemove instanceof SortPanel) {
			sort_panels.removeIf((p) -> p.equals(panelToRemove));
			// reorder panels
			int sq = (int)Math.sqrt(MAX_PANELS);
			sortPanelsCont.removeAll();
			for (int i=0; i<sort_panels.size(); ++i) {
				SortPanel p = sort_panels.get(i);
				GridBagConstraints c = new GridBagConstraints();
				c.weightx = c.weighty = 1.0;
				c.fill = GridBagConstraints.BOTH;
				c.insets = new Insets(2, 2, 2, 2);
				c.gridx = i%sq; c.gridy = i/sq;
				sortPanelsCont.add(p, c);
			}
			// repaint component
			frame.getContentPane().revalidate();
			frame.repaint();
		}
	}
	
	/**
	 * Change array for sorting
	 */
	protected void resetArray() {
		String str_arr = (String)JOptionPane.showInputDialog(
                frame,
                "Enter array of positive ints",
                "Enter array",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                "1,2,3");
		if (str_arr == null) {
			return;
		}
		String[] tokens = str_arr.split(",");
		
		if (tokens.length == 0) {
			String mes = "Array is empty";
			JOptionPane.showMessageDialog(frame, mes);
			return;
		}
		
		if (tokens.length > MAX_ARR_LEN) {
			String mes = String.format("Array must be less than %d elements long", MAX_ARR_LEN);
			JOptionPane.showMessageDialog(frame, mes);
			return;
		}
		
		int[] arr = new int[tokens.length];
		for (int i=0; i<tokens.length; ++i) {
			try {
				arr[i] = Integer.parseUnsignedInt(tokens[i].trim());
				if (arr[i] == 0)
					throw new NumberFormatException("Number isn't positive");
			} catch (NumberFormatException ex) {
				String mes = String.format("Element #%d, \"%s\" namely, isn't positive integer", i+1, tokens[i]);
				JOptionPane.showMessageDialog(frame, mes);
				return;
			}
		}
		
		cur_array = arr;
		resetPanels();
	}
	
	/**
	 * Reset sort panels to initial state
	 */
	protected void resetPanels() {
		for (SortPanel p : sort_panels) {
			p.feedArray(cur_array);
		}
	}
	
	/**
	 * Show dialog and load classes from chosen folder
	 */
	protected void loadClasses() {
		int dialogRes = fileChooser.showOpenDialog(frame);
		if (dialogRes == JFileChooser.APPROVE_OPTION) {
			try {
				File folder = fileChooser.getSelectedFile();
				List<Class> classes = null;
				if (folder.exists() && folder.isDirectory()) {
					classes = new AlgorithmClassLoader().loadFromFolder(folder);
				}
				String mes = "Not loaded";
				// form list of loaded classes
				if (classes != null && !classes.isEmpty()) {
					StringBuilder listOfClasses = new StringBuilder();
					for (Class c : classes) {
						listOfClasses.append(c.getName());
						listOfClasses.append(", ");
					}
					mes = "Loaded " + listOfClasses.substring(0, listOfClasses.length()-2);
				}
				// show if loaded something
				JOptionPane.showMessageDialog(frame, mes);
				
				algo_list.addAll(classes);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(frame, ex.getMessage());
			}
		}
	}
}
