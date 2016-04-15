package ua.plukraine.utils;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
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
		int max_val = Arrays.stream(arrState).max(new CellComparator()).get().val;
		Rectangle2D[] bars = new Rectangle2D[n];
		double b_width = BarGeometry.getBarWidth(w_width, padding, gap, n);
		double h_mult  = BarGeometry.getHeightMultiplier(arrState, w_height, padding);
		for (int i=0; i<n; ++i) {
			double x_left = padding + i*(b_width + gap);
			double b_heigth = arrState[i].val * h_mult;
			double y_top = padding + (max_val - arrState[i].val)*h_mult;
			bars[i] = new Rectangle2D.Double(x_left, y_top, b_width, b_heigth);
		}
		return bars;
	}
	public static Path2D generateTriangle(Cell[] arrState, double w_width, double w_height, double padding, double gap, int ind) {
		Path2D triangle = new Path2D.Double();
		
		double b_width = getBarWidth(w_width, padding, gap, arrState.length);
		double h = padding / 3;
		double a = h / Math.sin(Math.PI / 3);
		
		Point2D top = new Point2D.Double(padding + (b_width+gap)*ind + b_width/2, w_height - 2*padding/3);
		Point2D v = new Point2D.Double(Math.cos(Math.PI / 3), Math.sin(Math.PI / 3));
		//Point2D right = new Point2D.Double(bottom.getX() + a/2, padding/3);
		Point2D right = new Point2D.Double(top.getX() + a * v.getX(), top.getY() + a * v.getY());
		Point2D left = new Point2D.Double(right.getX() - a, right.getY());
		
		triangle.moveTo(top.getX(), top.getY());
		triangle.lineTo(right.getX(), right.getY());
		triangle.lineTo(left.getX(), left.getY());
		
		return triangle;
	}
}
