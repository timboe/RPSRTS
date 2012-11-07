package com.timboe.rpsrts.sprites;

import java.util.ArrayList;
import java.util.LinkedList;

import com.timboe.rpsrts.enumerators.ActorType;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.world.AtomicFloat;

public class SpecialSpawn extends Sprite {

	protected LinkedList<AtomicFloat> spooge_x = new LinkedList<AtomicFloat>();
	protected LinkedList<AtomicFloat> spooge_y = new LinkedList<AtomicFloat>();
	protected LinkedList<AtomicFloat> spooge_x_vel = new LinkedList<AtomicFloat>();
	protected LinkedList<AtomicFloat> spooge_y_vel = new LinkedList<AtomicFloat>();
	protected int nSpooge = 0; //number live
	protected int nAdded = 0;//total number added so far
	protected int targetN = 240;//target numer to add
	protected int nTicks = 120;//ticks over which to add
	protected int tick;
	protected int killEveryNTicks;

	protected boolean isSpawend;
	protected ObjectOwner owner;

	protected ActorType spawnType;
	protected ArrayList<Actor> toKillOff = new ArrayList<Actor>();

	protected SpecialSpawn(final int _ID, final int _x, final int _y, final int _r, final ActorType _at, final ObjectOwner _oo) {
		super(_ID, _x, _y, _r);
		spawnType = _at;
		isSpawend = false;
		owner = _oo;
	}

	private void Add() {
		for (int i = 0; i < targetN/nTicks; ++i) {
			++nSpooge;
			++nAdded;
			final float angle = (float) (utility.rnd() * Math.PI * 2);
			final float _r_rand = utility.rnd() * r * 2;
			spooge_x.add( new AtomicFloat( (float) (x + (_r_rand * Math.cos(angle))) ) );
			spooge_y.add( new AtomicFloat( (float) (y + (_r_rand * Math.sin(angle))) ) );
			spooge_x_vel.add( new AtomicFloat( (float) (_r_rand * Math.cos(angle + Math.PI/2f) * 0.2f) ) );
			spooge_y_vel.add( new AtomicFloat( (float) (_r_rand * Math.sin(angle + Math.PI/2f) * 0.2f ) ) );
		}
	}

	public void addToMurderList(final ArrayList<Actor> _hsa) {
		if (_hsa != null) {
			toKillOff.addAll(_hsa);
		}
		if (toKillOff.size() > 0) {
			killEveryNTicks = (nTicks / toKillOff.size());
		}
	}

	@Override
	public boolean GetIsSpecialSpawn() {
		return true;
	}

	@Override
	public ObjectOwner GetOwner() {
		return owner;
	}

	public boolean GetSpawned() {
		return isSpawend;
	}

	public ActorType GetType() {
		return spawnType;
	}

	public void SetSpawned() {
		if (GetSpawned() == true) return;
		theSpriteManager.PlaceActor(GetLoc(), GetType(), GetOwner());
		isSpawend = true;
	}

	public synchronized void Tick(final int _tick_count) {
		if (++tick <= nTicks) Add();

		if (killEveryNTicks == 0) killEveryNTicks = 1;
		if (tick % killEveryNTicks == 0 && toKillOff.size() > 0) {
			toKillOff.remove( toKillOff.size() - 1 ).Kill();
		}
		if (tick == nTicks) {
			while (toKillOff.size() > 0) {
				toKillOff.remove( toKillOff.size() - 1 ).Kill();
			}
		}

		if (nAdded == targetN) {
			SetSpawned();
		}

		for (int s = 0; s < nSpooge; ++s) {
			spooge_x_vel.get(s).mod( (float) (Math.sin(Math.atan2(x - spooge_x.get(s).floatValue(), y - spooge_y.get(s).floatValue())) * 0.15f) );
			spooge_y_vel.get(s).mod( (float) (Math.cos(Math.atan2(x - spooge_x.get(s).floatValue(), y - spooge_y.get(s).floatValue())) * 0.15f) );

			spooge_x_vel.get(s).set( spooge_x_vel.get(s).floatValue() * 0.99f);
			spooge_y_vel.get(s).set( spooge_y_vel.get(s).floatValue() * 0.99f);

			spooge_x.get(s).mod( spooge_x_vel.get(s).floatValue() );
			spooge_y.get(s).mod( spooge_y_vel.get(s).floatValue() );

			if (utility.Seperation(spooge_x.get(s), x, spooge_y.get(s), y) < r/2f) {
				spooge_x_vel.remove(s);
				spooge_y_vel.remove(s);
				spooge_x.remove(s);
				spooge_y.remove(s);
				--nSpooge;
			}
		}

		if (nSpooge == 0 && tick > nTicks) {
			Kill(); //Kill _me_
		}
	}

}
