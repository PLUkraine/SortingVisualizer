package ua.plukraine.utils;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;

public class BarGeometry {
	public static double getBarWidth(double w_width, double padding, double gap, double n_arr) {
		return (w_width - (n_arr - 1)*gap - 2 * padding) / n_arr;
	}
	public static double getHeightMultiplier(Cell[] arr, double w_height, double padding) {
		int max_val = Arrays.stream(arr).max(new CellComparator()).get().val;
		return (w_height - 2 * padding) / max_val;
	}
	public static Rectangle2D[] generateBars(Cell[] arrState, double w_width, double w_height, double padding, double gap) {
		int n = arrState.length;
		Rectangle2D[] bars = new Rectangle2D[n];
		double b_width = BarGeometry.getBarWidth(w_width, padding, gap, n);
		double h_mult  = BarGeometry.getHeightMultiplier(arrState, w_height, padding);
		for (int i=0; i<n; ++i) {
			double x_left = padding + i*(b_width + gap);
			double b_heigth = arrState[i].val * h_mult;
			bars[i] = new Rectangle2D.Double(x_left, padding, b_width, b_heigth);
		}
		return bars;
	}
}
