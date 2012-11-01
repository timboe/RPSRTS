package com.timboe.rpsrts.sprites;

import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.managers.GameWorld;
import com.timboe.rpsrts.managers.ResourceManager;
import com.timboe.rpsrts.managers.SpriteManager;
import com.timboe.rpsrts.managers.Utility;
import com.timboe.rpsrts.world.WorldPoint;

public class Sprite implements Comparable<Sprite> {
	protected final Utility utility = Utility.GetUtility();
	protected final GameWorld theWorld = GameWorld.GetGameWorld();
	protected final SpriteManager theSpriteManager = SpriteManager.GetSpriteManager();
	protected final ResourceManager resource_manager = ResourceManager.GetResourceManager();

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

	protected Sprite(int _ID, int _x, int _y, int _r) {
		ID = _ID;
		x= _x;
		y = _y;
		x_prec = _x + utility.rnd() - .5f;
		y_prec = _y + utility.rnd() - .5f;
		r = _r;
		loc = new WorldPoint();
		ticks_per_tock = utility.ticks_per_tock;
		tick_offset = utility.rndI(ticks_per_tock);
		animSteps = 4;
		animStep = 0;
		flashTicks = 0;
	}

	public boolean Attack(int _damage) {
		health -= _damage;
		if (GetOwner() == ObjectOwner.Player) {
			resource_manager.ScorePoints(ObjectOwner.Enemy, _damage);
		} else if (GetOwner() == ObjectOwner.Enemy) {
			resource_manager.ScorePoints(ObjectOwner.Enemy, _damage);
		}
		flashTicks = 12;
		if (health <= 0) {
			Kill();
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(Sprite comp) {
		//return (int) (this.GetZOrder() - comp.GetZOrder());
		return (int) (( comp.GetZOrder() - GetZOrder() )*100000);
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
	
	public boolean GetIsProjectile() {
		return false;
	}
	
	public boolean GetIsResource() {
		return false;
	}


	public boolean GetIsSpoogicle() {
		return false;
	}

	public WorldPoint GetLoc() {
		loc.setLocation(x, y);
		return loc;
	}

	//override
	public ObjectOwner GetOwner() {
		return null;
	}

	public float GetPreciseX() {
		return x_prec;
	}

	public float GetPreciseY() {
		return y_prec;
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

	public float GetZOrder() {
		return (float) ( -1f * (x_prec * Math.sin(utility.rotateAngle) + (y_prec * Math.cos(utility.rotateAngle))) );

	}
	
	public void Kill() {
		dead = true;
	}


}
