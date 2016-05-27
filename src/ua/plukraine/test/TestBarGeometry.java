package ua.plukraine.test;

import static org.junit.Assert.*;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import org.junit.Test;

import ua.plukraine.utils.BarGeometry;
import ua.plukraine.utils.Cell;
import ua.plukraine.utils.CellComparator;
import ua.plukraine.utils.CellState;

public class TestBarGeometry {
	public static double eps = 1e-6;
	
	@Test
	public void testGetBarWidth() {
		// case 1
		double w_width = 368;
		double gap = 2;
		double padding = 25;
		int arr_len = 10;
		double res = BarGeometry.getBarWidth(w_width, padding, gap, arr_len);
		assertEquals(30, res, eps);
		
		// case 2
		w_width = 550;
		gap = 2.5;
		padding = 11;
		arr_len = 13;
		res = BarGeometry.getBarWidth(w_width, padding, gap, arr_len);
		assertEquals(38.307692307, res, eps);
	}
	@Test
	public void testHeightMultiplier() {
		// case 1
		double padding = 11.5;
		double w_height = 323.0;
		Cell[] states = new Cell[] {
			new Cell(CellState.Active, 100),
			new Cell(CellState.Active, 0),
			new Cell(CellState.Active, 4),
			new Cell(CellState.Active, 57),
			new Cell(CellState.Active, 98),
			new Cell(CellState.Active, 32)
		};
		double h_mult = BarGeometry.getHeightMultiplier(states, w_height, padding);
		assertEquals(3, h_mult, eps);
		
		// case 2
		padding = 4;
		w_height = 243;
		states = new Cell[] {
			new Cell(CellState.Active, 143),
			new Cell(CellState.Active, 125),
			new Cell(CellState.Active, 43),
			new Cell(CellState.Active, 55)
		};
		h_mult = BarGeometry.getHeightMultiplier(states, w_height, padding);
		assertEquals((243 - 4*2)/143.0, h_mult, eps);
	}
	@Test
	public void testGenerateBars() {
		double padding = 11.5;
		double w_height = 323.0;
		double w_width = 267.0;
		double gap = 3;
		Cell[] states = new Cell[] {
			new Cell(CellState.Active, 100),
			new Cell(CellState.Active, 0),
			new Cell(CellState.Active, 4),
			new Cell(CellState.Active, 57),
			new Cell(CellState.Active, 98),
			new Cell(CellState.Active, 32)
		};
		double h_mult = BarGeometry.getHeightMultiplier(states, w_height, padding);
		double bar_width = BarGeometry.getBarWidth(w_width, padding, gap, states.length);
		Rectangle2D[] bars = BarGeometry.generateBars(states, w_width, w_height, padding, gap);
		int max_val = Arrays.stream(states).max(new CellComparator()).get().val;
		assertEquals(states.length, bars.length);
		for (int i=0; i<bars.length; ++i) {
			Rectangle2D cur = bars[i];
			assertEquals(cur.getX(), padding + gap*i + bar_width*i, eps);
			assertEquals(cur.getY(), padding + (max_val - states[i].val)*h_mult, eps);
			assertEquals(cur.getWidth(), bar_width, eps);
			assertEquals(cur.getHeight(), h_mult * states[i].val, eps);
		}
	}
}
