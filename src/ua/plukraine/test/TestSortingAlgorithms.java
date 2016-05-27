package ua.plukraine.test;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import ua.plukraine.algos.ISortingAlgortihm;
import ua.plukraine.algos.InsertionSorting;
import ua.plukraine.algos.QuickSortRandomPivot;
import ua.plukraine.utils.Cell;

public class TestSortingAlgorithms {
	
	/**
	 * Test given algorithm on generated pseudorandom numbers
	 * @param algorithm - algorithm to test
	 * @param low - minimum array length (inclusive)
	 * @param high - maximum array length (exclusive)
	 * @param cases - number of arrays to generate
	 */
	private void testAlgorithm(ISortingAlgortihm algorithm, int low, int high, int cases) {
		long rgenseed = System.currentTimeMillis();
		Random rand = new Random();
		rand.setSeed(rgenseed);
		
		for (int suite = 0; suite < cases; ++suite) {
			int n = rand.nextInt(high - low) + low;
			int[] arr = rand.ints(n).toArray();
			Cell[] result = null;
			algorithm.init(arr);
			while (!algorithm.hasFinished()) {
				result = algorithm.nextState();
			}
			for (int i = 1; i < result.length; ++i) {
				if (result[i-1].val > result[i].val) {
					fail(String.format("Seed %d, test case #%d: failure. %d and %d elements out of order", 
							rgenseed, suite, i-1, i));
				}
			}
		}
	}
	
	@Test
	public void testInsertionSorting() {
		testAlgorithm(new InsertionSorting(), 200, 500, 40);
	}
	
	@Test
	public void testQuickSortRandomPivot() {
		testAlgorithm(new QuickSortRandomPivot(), 500, 1000, 20);
	}

}
