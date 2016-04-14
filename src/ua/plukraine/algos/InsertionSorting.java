package ua.plukraine.algos;

import java.util.Arrays;

import ua.plukraine.utils.Cell;
import ua.plukraine.utils.CellState;

public class InsertionSorting implements ISortingAlgortihm {

	@Override
	public Cell[] nextState() {
		Cell[] res = new Cell[arr.length];
		for (int cnt =0; cnt < arr.length; ++cnt) {
			res[cnt] = new Cell(CellState.Idle, arr[cnt]);
		}
		
		if (i >= arr.length) {
			i++;
			return res;
		}
		
		res[j].state = CellState.Active;
		res[j-1].state = CellState.Active;
		if (st == InnerState.compare) {
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
				// advance to next i
				i++;
				j = i+1;
			}
			
			j--;
			if (j < 1) {
				++i;
				j = i;
			}
		}
		
		st = InnerState.nextState(st);
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
		return i > arr.length;
	}
	
	protected int i=1, j=1;
	protected int arr[];
	protected InnerState st = InnerState.activate;
	
	enum InnerState {
		activate, compare;
		static public InnerState nextState(InnerState s) {
			return s == compare ? activate : compare;
		}
	};
}
