package com.timboe.rpsrts;

import java.util.LinkedList;

public class Spoogicles extends Sprite {

	protected int nSpooge;
	protected int nDead;
	
	protected LinkedList<MyFloat> x_start = new LinkedList<MyFloat>();
	protected LinkedList<MyFloat> y_start = new LinkedList<MyFloat>();
	protected LinkedList<MyFloat> x = new LinkedList<MyFloat>();
	protected LinkedList<MyFloat> y = new LinkedList<MyFloat>();
	protected LinkedList<MyFloat> spooge_x_vel = new LinkedList<MyFloat>();
	protected LinkedList<MyFloat> spooge_y_vel = new LinkedList<MyFloat>();
	protected LinkedList<MyFloat> isDead = new LinkedList<MyFloat>();
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
			x.add( new MyFloat( 0f ) );
			y.add( new MyFloat( 0f ) );
			x_start.add( new MyFloat( (float) _x ) );
			y_start.add( new MyFloat( (float) _y ) );
			spooge_x_vel.add( new MyFloat( (float) (utility.rndG(0f, 0.5f * scale) )) );
			spooge_y_vel.add( new MyFloat( (float) (-2.5 - Math.abs(utility.rndG(0f, 2f * scale))) ));
			
			isDead.add( new MyFloat( -1f ) );

		}
	
		
	}
	
	@Override
	public boolean GetIsSpoogicle() {
		return true;
	}
	

	
	

}
