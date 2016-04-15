package ua.plukraine.gui;

import java.awt.geom.Point2D;

public class TransitionAnimation {
	public TransitionAnimation(Point2D initPos, Point2D finalPos, int duration) {
		curFrame = 0;
		this.duration = duration;
		_init = initPos;
		_final = finalPos;
		x_func = (frame) -> {
			return initPos.getX() + (finalPos.getX() - initPos.getX()) * frame / duration; 
		};
		y_func = (frame) -> {
			return initPos.getY() + (finalPos.getY() - initPos.getY()) * frame / duration;
		};
	}
	
	public Point2D initPos() {
		return _init;
	}
	
	public Point2D finalPos() {
		return _final;
	}
	
	/**
	 * Change transition coordinates without changing current frame
	 * @param initPos - new initial position
	 * @param finalPos - new final position
	 */
	public void resetTransition(Point2D initPos, Point2D finalPos) {
		_init = initPos;
		_final = finalPos;
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
	
	protected TransFunc x_func;
	protected TransFunc y_func;
	protected int curFrame;
	protected int duration;
	protected Point2D _init;
	protected Point2D _final;
	
	@FunctionalInterface
	protected interface TransFunc {
		double f(int frame);
	}
}
