package com.timboe.rpsrts.sprites;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.world.AtomicFloat;

public class Spoogicles extends Sprite {

	protected int nSpooge;
	protected int nDead;
	
	protected LinkedList<AtomicFloat> x = new LinkedList<AtomicFloat>();
	protected LinkedList<AtomicFloat> y = new LinkedList<AtomicFloat>();
	protected LinkedList<AtomicFloat> spooge_x_vel = new LinkedList<AtomicFloat>();
	protected LinkedList<AtomicFloat> spooge_y_vel = new LinkedList<AtomicFloat>();
	protected LinkedList<AtomicBoolean> isDead = new LinkedList<AtomicBoolean>();
	protected float floorLevel;
	protected float floorX;
	
	protected ObjectOwner oo;
	
	protected float scale;	
	
	protected Spoogicles(int _ID, int _x, int _y, ObjectOwner _oo, int _spoogicles, float _scale) {
		super(_ID, _x, _y, 1);
	
		scale = _scale;
		floorLevel = _y;
		floorX = _x;
		oo = _oo;
				
		nSpooge = _spoogicles;
		for (int s = 0; s < _spoogicles; ++s) {
			x.add( new AtomicFloat( 0f ) );
			y.add( new AtomicFloat( 0f ) );
			spooge_x_vel.add( new AtomicFloat( (float) (utility.rndG(0f, 0.5f * scale) )) );
			spooge_y_vel.add( new AtomicFloat( (float) (-2.5 - Math.abs(utility.rndG(0f, 2f * scale))) ));
			isDead.add(new AtomicBoolean(false));
		}
	
		
	}
	
	@Override
	public boolean GetIsSpoogicle() {
		return true;
	}
	
	public void Tick(int tick) {
		for (int s = 0; s < nSpooge; ++s) {
			if (isDead.get(s).get()  == true) continue;
			spooge_y_vel.get(s).mod(utility.gravity);
			
			x.get(s).mod( spooge_x_vel.get(s).floatValue() );
			y.get(s).mod( spooge_y_vel.get(s).floatValue() );
			
			//check height
			if (floorLevel + y.get(s).floatValue() > floorLevel) {
				if (utility.rnd() < 0.75f) {
					isDead.get(s).set(true);
					++nDead;
				}
				spooge_y_vel.get(s).set( -Math.abs(spooge_y_vel.get(s).floatValue()) );
			}
		}
	}
}
