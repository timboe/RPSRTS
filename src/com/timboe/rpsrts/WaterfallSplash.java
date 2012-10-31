package com.timboe.rpsrts;

public class WaterfallSplash extends Sprite {

	protected float offset;
	
	public WaterfallSplash (int _ID, int _x, int _y, int _r) {
		super(_ID, _x, _y, _r);
		offset = -3 * _r; //Start hidden
		
	}
	
	public void Tick(int _tick_count) {
		offset += utility.gravity * utility.waterfall_fall_speed;
		if (offset + y > utility.waterfall_size) Kill();
	}
	
}
