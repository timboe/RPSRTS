package com.timboe.rpsrts;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Spoogicles extends Sprite {

	protected int nSpooge;
	protected int nDead;
	
	protected LinkedList<AtomicFloat> x_start = new LinkedList<AtomicFloat>();
	protected LinkedList<AtomicFloat> y_start = new LinkedList<AtomicFloat>();
	protected LinkedList<AtomicFloat> x = new LinkedList<AtomicFloat>();
	protected LinkedList<AtomicFloat> y = new LinkedList<AtomicFloat>();
	protected LinkedList<AtomicFloat> spooge_x_vel = new LinkedList<AtomicFloat>();
	protected LinkedList<AtomicFloat> spooge_y_vel = new LinkedList<AtomicFloat>();
	protected LinkedList<AtomicBoolean> isDead = new LinkedList<AtomicBoolean>();
	protected float floorLevel;
	
	protected ObjectOwner oo;
	
	protected float scale;	
	
	protected Spoogicles(int _ID, int _x, int _y, ObjectOwner _oo, int _spoogicles, float _scale) {
		super(_ID, _x, _y, 1);
	
		scale = _scale;
		floorLevel = _y;
		oo = _oo;
				
		nSpooge = _spoogicles;
		for (int s = 0; s < _spoogicles; ++s) {
			x.add( new AtomicFloat( 0f ) );
			y.add( new AtomicFloat( 0f ) );
			x_start.add( new AtomicFloat( (float) _x ) );
			y_start.add( new AtomicFloat( (float) _y ) );
			spooge_x_vel.add( new AtomicFloat( (float) (utility.rndG(0f, 0.5f * scale) )) );
			spooge_y_vel.add( new AtomicFloat( (float) (-2.5 - Math.abs(utility.rndG(0f, 2f * scale))) ));
			isDead.add(new AtomicBoolean(false));

		}
	
		
	}
	
	@Override
	public boolean GetIsSpoogicle() {
		return true;
	}
}
