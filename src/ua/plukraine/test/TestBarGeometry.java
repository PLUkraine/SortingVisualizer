package ua.plukraine.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ua.plukraine.utils.BarGeometry;

public class TestBarGeometry {
	public static double eps = 1e-6;
	
	@Test
	public void testGetBarWidth() {
		double w_width = 368;
		double gap = 2;
		double padding = 25;
		int arr_len = 10;
		double res = BarGeometry.getBarWidth(w_width, padding, gap, arr_len);
		assertEquals(30, res, eps);
		
		w_width = 550;
		gap = 2.5;
		padding = 11;
		arr_len = 13;
		res = BarGeometry.getBarWidth(w_width, padding, gap, arr_len);
		assertEquals(38.307692307, res, eps);
	}
}
