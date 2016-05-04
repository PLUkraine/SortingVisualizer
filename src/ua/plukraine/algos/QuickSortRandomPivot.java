package ua.plukraine.algos;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;

import ua.plukraine.utils.Cell;
import ua.plukraine.utils.CellState;

/**
 * Stateful iterative quick sort algorithm with random pivot choosing
 *
 */
public class QuickSortRandomPivot implements ISortingAlgortihm {
	/**
	 * Swap elements in given array
	 * @param arr - given array
	 * @param a - index of the first value
	 * @param b - index of the second value
	 */
	protected void swap(int[]arr, int a, int b) {
		int tmp = arr[a];
		arr[a] = arr[b];
		arr[b] = tmp;
	}

	
//	void iterativeQuickSort(int[]arr, int left, int right) {
//		Random rand = new Random();
//		
//		ArrayDeque<pair> q = new ArrayDeque<>();
//		q.addLast(new pair(left,right));
//		while (!q.isEmpty()) {
//			pair p = q.removeFirst();
//			if (p.l>=p.r) continue;
//			// partition the array
//			int n = p.r - p.l + 1;
//			int randPivot = rand.nextInt(n) + p.l;
//			swap(arr, p.r, randPivot); // put random pivot as rightmost element
//			int b, j;
//			b = j = p.l;
//			for (; j<p.r; ++j) {
//				if (arr[j] <= arr[p.r]) {
//					swap(arr, j, b);
//					b++;
//				}
//			}
//			// push both parts to stack
//			swap(arr, b, p.r);
//			q.addLast(new pair(p.l, b-1));
//			q.addLast(new pair(b+1, p.r));
//		}
//	}
	
//	public static void main(String[] args) {
//		//int[] a = new int[] {7,-1,3,2,7,3,2,5};
//		int[] a = new int[]{20,14, 9, 16, 8, 10, 17, 14, 10, 9, 19, 14, 3, 19, 18, 17, 19, 14, 7, 8, 10};
//		QuickSort s = new QuickSort();
//		s.init(a);
//		while (!s.hasFinished()) {
//			for (Cell c : s.nextState()) {
//				System.out.printf("%-13s%-3d ", c.state, c.val);
//			}
//			System.out.println();
//		}
//	}
	// real algorithm here ----------------------------------------------------------
	protected class pair {
		int l; int r;
		/**
		 * Produce segment
		 * @param left - left bound (inclusive)
		 * @param right - right bound (inclusive)
		 */
		public pair(int left, int right) {
			l = left; r = right;
		}
		/**
		 * Check if bounds aren't misplaced
		 * @return
		 */
		public boolean mustSort() {
			return l < r;
		}
		/**
		 * Check if given value is in segment
		 * @param i - given value
		 * @return true if i is in bounds of segment
		 */
		public boolean inBound(int i) {
			return l<=i&&i<=r;
		}
	}
	protected enum InnerState {
		Partitioning, Pop, End, Push
	}
	
	protected Deque<pair> stack = new ArrayDeque<>();
	protected int[] a = null;
	// b - bound of <=, j - bound between unknown and partitioned (leftmost unknown symbol)
	protected int b, j;
	protected InnerState state;
	protected pair subarray;
	
	@Override
	public Cell[] nextState() {
		Cell[] res = null;
		switch (state) {
		case Push:
			res = push();
			break;
		case Partitioning:
			res = partition();
			break;
		case End:
			res = new Cell[a.length]; 
			for (int i = 0; i<a.length; ++i) {
				res[i] = new Cell(CellState.Sorted, a[i]);
			}
			break;
		case Pop:
			res = popNext();
			break;
		}
		return res;
	}
	
	/**
	 * Pop subarray from stack and start sorting
	 * @return new state
	 */
	protected Cell[] popNext() {	
		// get next subarray
		do {
			// check if all array is sorted
			if (stack.isEmpty()) {
				state = InnerState.End;
				// all are sorted, nothing to do
				Cell[] cs = new Cell[a.length]; 
				for (int i = 0; i<a.length; ++i) {
					cs[i] = new Cell(CellState.Sorted, a[i]);
				}
				return cs;
			}
			// else, get next subarray
			subarray = stack.removeLast();
		} while (!subarray.mustSort());
		
		// choose pivot and put index pointer to the left
		int n = subarray.r - subarray.l + 1;
		Random rand = new Random();
		int pEl = rand.nextInt(n)+subarray.l;
		swap(a, pEl, subarray.r);
		b = j = subarray.l;
		state = InnerState.Partitioning;
		Cell[] cs = new Cell[a.length];
		for (int i=0; i<a.length; ++i) {
			CellState s;
			if (subarray.inBound(i)) {
				if (pEl == subarray.r) {
					if (pEl == i)
						s = CellState.Active;
					else 
						s = CellState.Idle;
				} else {
					if (pEl == i || subarray.r == i)
						s =CellState.Swapped;
					else 
						s = CellState.Idle;
				}
			} else {
				s = CellState.Out;
			}
			cs[i] = new Cell(s, a[i]);
		}
		return cs;
	}
	
	/**
	 * Push subarrays on stack
	 * @return new state
	 */
	protected Cell[] push() {
		swap(a, b, subarray.r);
		Cell[] cs = new Cell[a.length];
		for (int i=0; i<a.length; ++i){			
			
			if (subarray.r!=b && (i == subarray.r || i == b)) {
				cs[i] = new Cell(CellState.Swapped, a[i]);
			} else if (subarray.r==b && (i == b)) {
				cs[i] = new Cell(CellState.Active, a[i]);
			} else if (subarray.inBound(i)) {
				cs[i] = new Cell(CellState.Idle, a[i]);
			} else {
				cs[i] = new Cell(CellState.Out, a[i]);
			}
		}
		
		stack.addLast(new pair(b+1, subarray.r));
		stack.addLast(new pair(subarray.l, b-1));
		state = InnerState.Pop;
		
		return cs;
	}
	
	/**
	 * Continue partitioning the array
	 * @return new state
	 */
	protected Cell[] partition() {
		Cell[] cs = new Cell[a.length];
		for (int i=0;i<a.length;++i) {
			if (i == subarray.r)
				cs[i] = new Cell(CellState.Active, a[i]);
			else if (subarray.inBound(i))
				cs[i] = new Cell(CellState.Idle, a[i]);
			else
				cs[i] = new Cell(CellState.Out, a[i]);
		}
		cs[j].state = CellState.Active;
		cs[b].state = CellState.Active;
		if (a[j] <= a[subarray.r]) {
			swap(a, j, b);
			if (j != b) {
				cs[j].state = cs[b].state = CellState.Swapped;
			}
			cs[j].val = a[j];
			cs[b].val = a[b];
			b++;
		} 
//		else {
//			cs[j].state = CellState.Active;
//		}
		
		++j;
		if (j == subarray.r) {
			state = InnerState.Push;
		}
		
		return cs;
	}

	@Override
	public void init(int[] array) {
		// clear stack, create copy of array and push all array on stack
		stack.clear();
		a = Arrays.copyOf(array, array.length);
		stack.addLast(new pair(0, array.length-1));
		state = InnerState.Pop;
	}

	@Override
	public boolean hasFinished() {
		return state == InnerState.End; 
	}

	@Override
	public String getName() {
		return "Quick Sort, random pivot";
	}

}
