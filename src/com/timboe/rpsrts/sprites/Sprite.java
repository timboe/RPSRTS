package com.timboe.rpsrts.sprites;

import com.timboe.rpsrts.enumerators.GameStatistics;
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
	private final WorldPoint loc;
	protected int r;

	protected int flashTicks;

	protected int animSteps;
	protected int animStep;
	int even_odd;

	float speed;
	protected float health;
	protected int maxHealth;
	protected boolean dead = false;

	int ticks_per_tock;
	int tick_offset;

	protected Sprite(final int _ID, final int _x, final int _y, final int _r) {
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

	public boolean Attack(final float _damage) {
		health -= _damage;
		if (GetOwner() == ObjectOwner.Player) {
			resource_manager.ScorePoints(ObjectOwner.Enemy, (int) Math.ceil(_damage/2));
		} else if (GetOwner() == ObjectOwner.Enemy) {
			resource_manager.ScorePoints(ObjectOwner.Player, (int) Math.ceil(_damage/2));
		}
		flashTicks = 12;
		if (health <= 0) {
			Kill();
			if (GetIsActor() == true) {
				resource_manager.AddStatistic(GameStatistics.TroopsSlaughtered);
			}
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(final Sprite comp) {
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

	public boolean GetIsExplosion() {
		return false;
	}

	public boolean GetIsProjectile() {
		return false;
	}


	public boolean GetIsResource() {
		return false;
	}

	public boolean GetIsSpecialSpawn() {
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
		//Projectiles look better if always rendered on top, therefore give them an offset;
		int offset = 0;
		if (GetIsProjectile() == true) {
			offset += utility.world_size;
		}
		return (float) ( -1f * (x_prec * Math.sin(utility.rotateAngle) + (y_prec * Math.cos(utility.rotateAngle)) + offset) );

	}

	public void Kill() {
		dead = true;
	}


}
