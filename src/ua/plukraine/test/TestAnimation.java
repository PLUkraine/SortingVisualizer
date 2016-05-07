package ua.plukraine.test;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.util.Random;

import org.junit.Test;

import ua.plukraine.gui.TransitionAnimation;

public class TestAnimation {
	static final double eps = 1e-6;

	protected double getDouble(Random rand, double dist) {
		double n = rand.nextDouble() * dist;
		if (rand.nextBoolean())
			n = -n;
		return n;
	}
	
	@Test
	public void test() {
		Random rand = new Random();
		int testCases = 1000;
		int MAX_FRAME = 5000;
		double dist = 500;
		for (int test = 0; test < testCases; ++test) {
			Point2D st = new Point2D.Double(getDouble(rand, dist), getDouble(rand, dist));
			Point2D en = new Point2D.Double(getDouble(rand, dist), getDouble(rand, dist));
			int frames = rand.nextInt(MAX_FRAME)+1;
			TransitionAnimation a = new TransitionAnimation(st, en, frames);
			assertEquals(st.getX(), a.getCurrentPoint().getX(), eps);
			assertEquals(st.getY(), a.getCurrentPoint().getY(), eps);
			for (int i=0; i<frames; ++i) {
				assertFalse(a.hasFinished());
				a.update();
			}
			assertEquals(en.getX(), a.getCurrentPoint().getX(), eps);
			assertEquals(en.getY(), a.getCurrentPoint().getY(), eps);
		}
	}

}
