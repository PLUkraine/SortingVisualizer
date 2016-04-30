package ua.plukraine.utils;

import java.util.Comparator;

/**
 * Comparator for cells. One cell is bigger than other if it's value is bigger
 */
public class CellComparator implements Comparator<Cell> {

	@Override
	public int compare(Cell o1, Cell o2) {
		if (o1.val < o2.val) 
			return -1;
		else if (o1.val > o2.val)
			return 1;
		else return 0;
	}

}
