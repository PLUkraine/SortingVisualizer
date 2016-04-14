package ua.plukraine.utils;

/**
 * Hint on how to render given cell
 */
public enum CellState {
	/**
	 * Just swapped places with other cell
	 * Only two cells in one array should be labeled as Swapped
	 */
	Swapped,
	/**
	 * No changes were made, usual state
	 */
	Idle,
	/**
	 * Cell is active(current etc
	 */
	Active
}
