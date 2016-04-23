package ua.plukraine.algos;

import ua.plukraine.utils.Cell;

/**
 * Stateful array sorting algorithm.<br>
 * It gives hints on how to render
 * array values 
 */
public interface ISortingAlgortihm {
	/**
	 * Get next step
	 * @return Cells with new state
	 */
	Cell[] nextState();
	/**
	 * Initialize algorithm for given array
	 * @param array - array to be sorted. It will be copied so no worries mate :)
	 */
	void init(int[] array);
	/**
	 * Check if algorithm has nothing to do
	 * @return true if array is sorted, false otherwise
	 */
	boolean hasFinished();
	/**
	 * Get name of algorithm
	 */
	String getName();
}
