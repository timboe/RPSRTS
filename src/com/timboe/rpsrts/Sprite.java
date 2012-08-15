package com.timboe.rpsrts;

public class Sprite implements Comparable<Sprite> {

	GameWorld theWorld;
	protected SpriteManager theSpriteManager;

	int ID;
	protected int x;
	protected int y;
	protected float x_prec;
	protected float y_prec;
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
		x_prec = _x + theSpriteManager.utility.rnd.nextFloat() - .5f;
		y_prec = _y + theSpriteManager.utility.rnd.nextFloat() - .5f;
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
	
	public boolean GetIsResource() {
		return false;
	}
	
	public boolean GetIsSpoogicle() {
		return false;
	}
	
	public boolean GetIsProjectile() {
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
	
	public float GetPreciseX() {
		return x_prec;
	}

	public float GetPreciseY() {
		return y_prec;
	}

	public void Kill() {
		dead = true;
	}

	public float GetZOrder() {
		return (float) (x_prec * Math.cos(theSpriteManager.utility.rotateAngle) - (y_prec * Math.sin(theSpriteManager.utility.rotateAngle)));

	}
	
	@Override
	public int compareTo(Sprite comp) {
		//return (int) (this.GetZOrder() - comp.GetZOrder());
		return (int) ((GetZOrder() - comp.GetZOrder())*100000);
	}


}
