package com.timboe.rpsrts;

public class Sprite {

	GameWorld theWorld;
	protected SpriteManager theSpriteManager;

	int ID;
	protected int x;
	protected int y;
	float x_prec;
	float y_prec;
	WorldPoint loc;
	protected int r;
	
	protected int flashTicks;

	protected int animSteps;
	protected int animStep;
	int even_odd;

	float speed;
	protected int health;
	protected int maxHealth;
	protected boolean dead = false;
	
	int ticks_per_tock; 
	int tick_offset;

	protected Sprite(int _ID, int _x, int _y, int _r, GameWorld _gw, SpriteManager _sm) {
		ID = _ID;
		theWorld = _gw;
		theSpriteManager = _sm;
		x= _x;
		y = _y;
		x_prec = _x;
		y_prec = _y;
		r = _r;
		loc = new WorldPoint();
		ticks_per_tock = theSpriteManager.utility.ticks_per_tock;
		tick_offset = theSpriteManager.utility.rnd.nextInt(ticks_per_tock);
		animSteps = 4;
		animStep = 0;
		flashTicks = 0;
	}

	public boolean Attack(int _damage) {
		health -= _damage;
		flashTicks = 12;
		if (health <= 0) {
			Kill();
			return true;
		}
		return false;
	}

	public boolean GetDead() {
		return dead;
	}

	public int GetID() {
		return ID;
	}

	public boolean GetIsActor() {
		return false;
	}

	public boolean GetIsBuilding() {
		return false;
	}

	public WorldPoint GetLoc() {
		loc.setLocation(x, y);
		return loc;
	}

	public ObjectOwner GetOwner() {
		return null;
	}

	public int GetR() {
		return r;
	}

	public int GetX() {
		return x;
	}

	public int GetY() {
		return y;
	}

	public void Kill() {
		dead = true;
	}


}
