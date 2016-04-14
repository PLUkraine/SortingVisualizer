package ua.plukraine.utils;

/**
 * One cell in array we're visualizing
 */
public class Cell {
	public Cell(CellState s, int v) {
		state = s;
		val = v;
	}
	/**
	 * Hint for renderer
	 */
	public CellState state;
	/**
	 * Positive array cell value
	 */
	public int val;
}
