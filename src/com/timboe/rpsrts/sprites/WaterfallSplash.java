package com.timboe.rpsrts.sprites;

public class WaterfallSplash extends Sprite {

	protected float offset;
	protected float speed;

	public WaterfallSplash (final int _ID, final int _x, final int _y, final int _r) {
		super(_ID, _x, _y, _r);
		offset = -3 * _r; //Start hidden

	}

	public void Tick(final int _tick_count) {
		speed += utility.gravity;
		offset += speed;
		if (offset + y > utility.waterfall_size) Kill();
	}

}
