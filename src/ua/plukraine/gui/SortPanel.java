package ua.plukraine.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.Timer;
import javax.swing.JComponent;
import ua.plukraine.utils.*;
import ua.plukraine.algos.*;
@SuppressWarnings("serial")
/**
 * Component that draws array and sorts it
 */
public class SortPanel extends JComponent {
	/**
	 * Helper structure for animation list
	 * Contains animation and index of corresponding bar
	 *
	 */
	protected class IndexAndAnimation {
		public IndexAndAnimation(int i, TransitionAnimation a) {
			this.ind = i;
			this.anime = a;
		}
		public int ind;
		public TransitionAnimation anime;
	}
	
	/* VARS */
	protected final ISortingAlgortihm algorithm;
	protected Cell[] arrState;
	protected Rectangle2D[] bars;
	protected LinkedList<IndexAndAnimation> animations;
	protected Timer animationTimer;
	protected int animationDuration;
	
	/* Constants */
	public static final int timer_tick = 5;
	protected static final double padding = 20;
	protected static final double gap = 1;
	protected static final int min_animation_duration = 10;
	
	
	/**
	 * Create panel with specific algorithm
	 * @param algorithm - Sorting algorithm that is being shown
	 * @param FPA - frames per animation
	 */
	public SortPanel(ISortingAlgortihm algorithm, int FPA) {
		initActions();
		
		this.algorithm = algorithm;
		arrState = null;
		animations = new LinkedList<IndexAndAnimation>();
		bars = null;
		animationDuration = FPA;
	}
	/**
	 * Abort all animations and redraw bars 
	 */
	public void abortAnimation() {
		animations.clear();
		if (bars != null) {
			bars = BarGeometry.generateBars(arrState, getSize().getWidth(), getSize().getHeight(), padding, gap);
		}
	}
	/**
	 * Check if panel finished sorting
	 * @return true if algorithm stopped else false
	 */
	public boolean hasFinished() {
		return algorithm.hasFinished();
	}
	/**
	 * Reinitialize panel with new array
	 * @param arr - new array (must contain positive values)
	 */
	public void feedArray(int [] arr) {
		int n = arr.length;
		
		algorithm.init(arr);
		animations.clear();
		arrState = new Cell[n];
		for (int i=0; i<n; ++i) {
			arrState[i] = new Cell(CellState.Idle, arr[i]);
		}
		bars = BarGeometry.generateBars(arrState, getSize().getWidth(), getSize().getHeight(), padding, gap);
		repaint();
	}
	/**
	 * Check if animation is currently present
	 * @return true if 
	 */
	public boolean hasAnimation() {
		animations.removeIf((a) -> a.anime.hasFinished());
		return !animations.isEmpty();
	}
	/**
	 * Advance animation or do one step of the algorithm.
	 * Create animation if needed
	 */
	public void update() {
		if (algorithm == null || algorithm.hasFinished()) {
			return;
		}
		
		// otherwise, get next step from algorithm
		arrState = algorithm.nextState();
		
		if (Arrays.stream(arrState).anyMatch((s) -> s.state == CellState.Swapped)) {
			// create swap animation
			ArrayList<Integer> swapped = new ArrayList<>();
			for (int i=0; i<arrState.length; ++i) {
				if (arrState[i].state == CellState.Swapped) {
					swapped.add(i);
				}
			}
			//int max_val = Arrays.stream(arrState).max(new CellComparator()).get().val;
			int i0 = swapped.get(0);
			int i1 = swapped.get(1);
			Point2D p0 = new Point2D.Double(bars[i0].getX(), bars[i0].getY());
			Point2D p1 = new Point2D.Double(bars[i1].getX(), bars[i1].getY());
			Point2D f0 = new Point2D.Double(bars[i1].getX(), bars[i0].getY());
			Point2D f1 = new Point2D.Double(bars[i0].getX(), bars[i1].getY());
			animations.add(new IndexAndAnimation(i1, new TransitionAnimation(p0, f0, animationDuration)));
			animations.add(new IndexAndAnimation(i0, new TransitionAnimation(p1, f1, animationDuration)));
			
			// swap bars
			Rectangle2D t = bars[i0];
			bars[i0] = bars[i1];
			bars[i1] = t;
			animationTimer.start();
		}
		
		repaint();
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		if (algorithm != null) {
			String name = algorithm.getName();
			g2.drawString(name, (float)padding, 15);
		}
		if (bars == null || bars.length == 0) {
			return;
		}
		
		int n = arrState.length;
		
		for (int i=0; i<n; ++i) {
			if (arrState[i].state == CellState.Swapped) {
				g2.setColor(Color.darkGray);
				g2.fill(BarGeometry.generateTriangle(arrState, getSize().getWidth(), getSize().getHeight(), padding, gap, i));
				g2.setColor(Color.gray);
				g2.fill(bars[i]);
			} else if (arrState[i].state == CellState.Active) {
				g2.setColor(Color.darkGray);
				g2.fill(BarGeometry.generateTriangle(arrState, getSize().getWidth(), getSize().getHeight(), padding, gap, i));
				g2.fill(bars[i]);
			} else if (arrState[i].state == CellState.Sorted) {
				g2.setColor(new Color(20, 130, 20));
				g2.fill(bars[i]);
			} else if (arrState[i].state == CellState.Out) {
				g2.setColor(new Color(220, 220, 220));
				g2.fill(bars[i]);
			} else if (arrState[i].state == CellState.Idle) {
				g2.setColor(Color.darkGray);
				g2.fill(bars[i]);
			}
			
		}
		
	}
	/**
	 * Change animation duration. Real time will be {@value #timer_tick}*newdur milliseconds
	 * @param newdur - frames per animation
	 */
	public void setAnimationFramesDuration(int newdur){
		animationTimer.stop();
		abortAnimation();
		animationDuration = Math.max(min_animation_duration, newdur);
		animationTimer.start();
	}
	/**
	 * Get animation duration in frames per animation(FPA)
	 * @return current FPA
	 */
	public int getAnimationDuration(){ 
		return animationDuration;
	}
	/**
	 * Update bars position using animation
	 * @return true if animations were updated and false if no animation existed
	 */
	protected boolean updateAnimation() {
		if (!hasAnimation()) {
			return false;
		}
		for (IndexAndAnimation a : animations) {
			a.anime.update();
			Point2D left_top = a.anime.getCurrentPoint();
			bars[a.ind].setRect(left_top.getX(), left_top.getY(),
					bars[a.ind].getWidth(), bars[a.ind].getHeight());
		}
		
		return true;
	}
	
	/**
	 * Initialize all panel actions(like on timer tick and resize event)
	 */
	protected void initActions()  {
		ActionListener animationAction = (e) -> {
			if (!updateAnimation()) {
				animationTimer.stop();
			}
			repaint();
		};
		this.animationTimer = new Timer(timer_tick, animationAction);
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				abortAnimation();
				repaint();
			}
		});
	}
	
	
}
