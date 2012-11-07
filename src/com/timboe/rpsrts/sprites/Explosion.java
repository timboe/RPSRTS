package com.timboe.rpsrts.sprites;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.timboe.rpsrts.enumerators.ObjectOwner;

public class Explosion extends Sprite {

	protected ObjectOwner owner;
	private final int explode_time = 30;
	private int explode_tick;
	protected ArrayList<AtomicInteger> shells = new ArrayList<AtomicInteger>();
	protected ArrayList<AtomicInteger> chunks = new ArrayList<AtomicInteger>();
	protected ArrayList<Float> chunk_angle = new ArrayList<Float>();

	int shell_velocity = 2;

	protected Explosion(final int _ID, final int _x, final int _y, final int _r, final ObjectOwner _oo) {
		super(_ID, _x, _y, _r);
		owner = _oo;
	}

	@Override
	public boolean GetIsExplosion() {
		return true;
	}

	public synchronized void Tick(final int _tick_count) {
		if (explode_tick < explode_time && utility.rnd() < 0.2) {
			shells.add( new AtomicInteger(0) );
		}
		if (explode_tick < explode_time) {
			for (int i=0; i<5; ++i) {
				chunks.add( new AtomicInteger(0) );
				chunk_angle.add( new Float(utility.rnd() * Math.PI * 2) );
			}
		}
		++explode_tick;
		boolean alive = false;
		for (final AtomicInteger _f : shells) {
			_f.set( _f.get() + shell_velocity-1);
			if (_f.get() > r) {
				_f.set( 10000 );
			} else {
				alive = true;
			}
		}
		for (final AtomicInteger _f : chunks) {
			_f.set( _f.get() + shell_velocity);
			if (_f.get() > r) {
				_f.set( 10000 );
			} else {
				alive = true;
			}
		}
		if (explode_tick > explode_time && alive == false) {
			Kill();
		}
	}

}
