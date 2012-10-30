package com.timboe.rpsrts;

import java.util.LinkedList;

public class Spoogicles extends Sprite {

	protected int nSpooge;
	protected int nDead;
	
	protected LinkedList<Float> x_start = new LinkedList<Float>();
	protected LinkedList<Float> y_start = new LinkedList<Float>();
	protected LinkedList<Float> x = new LinkedList<Float>();
	protected LinkedList<Float> y = new LinkedList<Float>();
	protected LinkedList<Float> spooge_x_vel = new LinkedList<Float>();
	protected LinkedList<Float> spooge_y_vel = new LinkedList<Float>();
	protected LinkedList<Boolean> isDead = new LinkedList<Boolean>();
	protected float floorLevel;
	//protected int lifetime;
	
	protected ObjectOwner oo;
	
	protected float scale;	
	
	protected Spoogicles(int _ID, int _x, int _y, ObjectOwner _oo, int _spoogicles, float _scale) {
		super(_ID, _x, _y, 1);
	
		scale = _scale;
		floorLevel = _y;
		oo = _oo;
		
		//lifetime = 30;
		
		nSpooge = _spoogicles;
		for (int s = 0; s < _spoogicles; ++s) {
			x.add( new Float( 0f ) );
			y.add( new Float( 0f ) );
			x_start.add( new Float( (float) _x ) );
			y_start.add( new Float( (float) _y ) );
			spooge_x_vel.add( new Float( (float) (utility.rndG(0f, 0.5f * scale) )) );
			spooge_y_vel.add( new Float( (float) (-2.5 - Math.abs(utility.rndG(0f, 2f * scale))) ));
			
			isDead.add(false);

		}
	
		
	}
	
	@Override
	public boolean GetIsSpoogicle() {
		return true;
	}
	

	
	

}
