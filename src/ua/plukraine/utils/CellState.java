package ua.plukraine.utils;

/**
 * Hint on how to render given cell
 */
public enum CellState {
	/**
	 * Just swapped places with other cell
	 * Only two or no cells in one array should be labeled as Swapped
	 */
	Swapped,
	/**
	 * No changes were made, usual state
	 */
	Idle,
	/**
	 * Cell is active(current etc)
	 */
	Active,
	/**
	 * Cell is in it's position
	 */
	Sorted,
	/**
	 * Cell is out of algorithm's scope (for example, out of invariant that is being sorted)
	 */
	Out
}
