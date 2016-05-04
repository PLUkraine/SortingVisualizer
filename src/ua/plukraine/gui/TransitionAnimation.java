package ua.plukraine.gui;

import java.awt.geom.Point2D;

public class TransitionAnimation {
	@FunctionalInterface
	protected interface TransFunc {
		double f(int frame);
	}
	protected TransFunc x_func;
	protected TransFunc y_func;
	protected int curFrame;
	protected int duration;
	
	public TransitionAnimation(Point2D initPos, Point2D finalPos, int duration) {
		curFrame = 0;
		x_func = (frame) -> {
			return initPos.getX() + (finalPos.getX() - initPos.getX()) * frame / duration; 
		};
		y_func = (frame) -> {
			return initPos.getY() + (finalPos.getY() - initPos.getY()) * frame / duration;
		};
	}
	
	public void update() {
		curFrame++;
	}
	
	public boolean hasFinished() {
		return duration <= curFrame;
	}
	
	public Point2D getCurrentPoint() {
		Point2D now = new Point2D.Double(x_func.f(curFrame), y_func.f(curFrame));
		return now;
	}
}
