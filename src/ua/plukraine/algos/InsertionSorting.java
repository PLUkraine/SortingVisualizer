package ua.plukraine.algos;

import java.util.Arrays;

import ua.plukraine.utils.Cell;
import ua.plukraine.utils.CellState;

/**
 * Stateful iterative insertion sort algorithm
 */
public class InsertionSorting implements ISortingAlgortihm {
	
	protected int i=1, j=1;
	protected int arr[];

	@Override
	public Cell[] nextState() {
		Cell[] res = new Cell[arr.length];
		// mark first (i+1) elements as idle
		for (int cnt =0; cnt <= Math.min(i, arr.length-1); ++cnt) {
			res[cnt] = new Cell(CellState.Idle, arr[cnt]);
		}
		// mark rest as out of scope
		for (int cnt = Math.min(i, arr.length-1)+1; cnt < arr.length; ++cnt) {
			res[cnt] = new Cell(CellState.Out, arr[cnt]);
		}
		
		// if we're finished
		if (i >= arr.length) {
			i++;
			// all are sorted
			for (int cnt =0; cnt < arr.length; ++cnt) {
				res[cnt].state = CellState.Sorted;
			}
			return res;
		}
		
		// now we're looking at j and j-1 elements
		res[j].state = CellState.Active;
		res[j-1].state = CellState.Active;
		
		if (arr[j-1] > arr[j]) {
			// swap
			int t = arr[j-1];
			arr[j-1] = arr[j];
			res[j-1].val = arr[j];
			arr[j] = t;
			res[j].val = t;
			
			res[j-1].state = CellState.Swapped;
			res[j].state = CellState.Swapped;
		} else {
			i++;
			j = i+1;
		}
		
		// prepare new move
		j--;
		if (j < 1) {
			++i;
			j = i;
		}
		return res;
	}

	@Override
	public void init(int[] array) {
		arr = Arrays.copyOf(array, array.length);
		i = 1;
		j = 1;
	}

	@Override
	public boolean hasFinished() {
		return arr == null || i > arr.length;
	}

	@Override
	public String getName() {
		return "Insertion sorting";
	};
}
