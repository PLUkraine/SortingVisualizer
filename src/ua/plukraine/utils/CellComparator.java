package ua.plukraine.utils;

import java.util.Comparator;

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
