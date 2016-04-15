package ua.plukraine.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.JPanel;
import ua.plukraine.utils.*;
import ua.plukraine.algos.*;
@SuppressWarnings("serial")
public class SortPanel extends JPanel {
	/**
	 * Create panel with specific algorithm
	 * @param algorithm - Sorting algorithm that is being shown
	 */
	public SortPanel(ISortingAlgortihm algorithm) {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
			}
		});
		this.algorithm = algorithm;
		arrState = null;
		anime = new LinkedList<IndexAnime>();
		bars = null;
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
		anime.clear();
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
		
		return !anime.isEmpty();
	}
	/**
	 * Advance animation or do one step of the algorithm.
	 * Create animation if needed
	 */
	public void update() {
		// otherwise, get next step from algorithm
		arrState = algorithm.nextState();
		//bars = BarGeometry.generateBars(arrState, getSize().getWidth(), getSize().getHeight(), padding, gap);
		
		if (Arrays.stream(arrState).anyMatch((s) -> s.state == CellState.Swapped)) {
			// create swap animation
			ArrayList<Integer> swapped = new ArrayList<>();
			for (int i=0; i<arrState.length; ++i) {
				if (arrState[i].state == CellState.Swapped) {
					swapped.add(i);
				}
			}
			int max_val = Arrays.stream(arrState).max(new CellComparator()).get().val;
			int i0 = swapped.get(0);
			int i1 = swapped.get(1);
			Point2D p0 = new Point2D.Double(bars[i0].getX(), bars[i0].getY());
			Point2D p1 = new Point2D.Double(bars[i1].getX(), bars[i1].getY());
			Point2D f0 = new Point2D.Double(bars[i1].getX(), bars[i0].getY());
			Point2D f1 = new Point2D.Double(bars[i0].getX(), bars[i1].getY());
			anime.add(new IndexAnime(i1, new TransitionAnimation(p0, f0, animation_duration)));
			anime.add(new IndexAnime(i0, new TransitionAnimation(p1, f1, animation_duration)));
			// swap bars
			Rectangle2D t = bars[i0];
			bars[i0] = bars[i1];
			bars[i1] = t;
		}
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bars == null || bars.length == 0) {
			return;
		}
		
		Graphics2D g2 = (Graphics2D)g;
		int n = arrState.length;
		
		for (int i=0; i<n; ++i) {
			if (arrState[i].state == CellState.Swapped) {
				g2.setColor(Color.darkGray);
				g2.fill(BarGeometry.generateTriangle(arrState, getSize().getWidth(), getSize().getHeight(), padding, gap, i));
				g2.setColor(Color.gray);
				g2.fill(bars[i]);
			} else {
				g2.setColor(Color.darkGray);
				if (arrState[i].state == CellState.Active) {
					g2.fill(BarGeometry.generateTriangle(arrState, getSize().getWidth(), getSize().getHeight(), padding, gap, i));
				}
				g2.fill(bars[i]);
			}
			
		}
		
	}
	/**
	 * Update bars position using animation
	 * @return true if animations were updated and false if no animation existed
	 */
	public boolean updateAnimation() {
		while (!anime.isEmpty() && anime.getFirst().anime.hasFinished()) {
			anime.removeFirst();
		}
		if (!hasAnimation()) {
			return false;
		}
		
		for (IndexAnime a : anime) {
			a.anime.update();
			Point2D left_top = a.anime.getCurrentPoint();
			bars[a.ind].setRect(left_top.getX(), left_top.getY(),
					bars[a.ind].getWidth(), bars[a.ind].getHeight());
		}
		
		return true;
	}
	
	/* VARS */
	protected final ISortingAlgortihm algorithm;
	protected Cell[] arrState;
	protected Rectangle2D[] bars;
	protected LinkedList<IndexAnime> anime;
	
	/* Constants */
	protected double padding = 20;
	protected double gap = 3;
	protected int animation_duration = 60;
	
	class IndexAnime {
		public IndexAnime(int i, TransitionAnimation a) {
			this.ind = i;
			this.anime = a;
		}
		public int ind;
		public TransitionAnimation anime;
	}
}
