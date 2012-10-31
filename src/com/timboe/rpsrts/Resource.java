package com.timboe.rpsrts;

public class Resource extends Sprite {

	protected ResourceType type;
	String type_str;
	protected int stuff;
	protected int toDraw;

	protected int not_reachable_penalty;
	int penalty_size;

	public Resource(int _ID, int _x, int _y, int _r, ResourceType _type) {
		super(_ID, _x, _y, _r);
		type = _type;
		int _start_stuff = utility.rndI(utility.resource_rnd) + utility.resource_min;
		
		penalty_size = utility.not_reachable_penelty_tocks;
		Add(_start_stuff);
		
		toDraw = utility.rndI(4); //for mines and rockpiles (different art)
		
		//snap me to the world grid
		WeightedPoint _my_snap = theSpriteManager.ClipToGrid(this.GetLoc());
		if (_my_snap != null) {
			_my_snap.GiveSprite(this);
			for (WeightedPoint _s : _my_snap.GetNieghbours()) {
				_s.GiveCollision(this);
			}
		}
	}
	
	@Override
	public boolean GetIsResource() {
		return true;
	}
	
	@Override
	public void Kill() {
		if (dead == true) return;
		dead = true;
		WeightedPoint _my_snap = theSpriteManager.ClipToGrid(this.GetLoc());
		_my_snap.RemoveSprite(this);
		if (_my_snap != null) {
			for (WeightedPoint _s : _my_snap.GetNieghbours()) {
				_s.RemoveSprite(this);
			}
		}
	}
	
	public void Add(int _n) {
		stuff += _n;
		if (type == ResourceType.Mine) {
			resource_manager.GLOBAL_IRON += GetRemaining();
		} else if (type == ResourceType.Rockpile) {
			resource_manager.GLOBAL_STONE += GetRemaining();
		} else { //cactus or tree
			resource_manager.GLOBAL_WOOD += GetRemaining();
		}
	}

	public boolean GetReachable() {
		if (not_reachable_penalty > 0) return false;
		return true;
	}

	public ResourceType GetType() {
		return type;
	}
	
	public int GetRemaining() {
		return stuff;
	}

	public int Plunder(int _requested) {
		//System.out.println("Resource: "+_requested+" requested");
		if (_requested <= stuff) {
			stuff -= _requested;
		} else {
			_requested = stuff;
			stuff = 0;
		}
		//System.out.println("Resource: "+_requested+" given");
		return _requested;
	}

	public void SetUnreachable() {
		not_reachable_penalty += penalty_size;
		System.out.println("WARN: Node unr. by actor on job! Quar. for tocks:"+not_reachable_penalty);
		penalty_size += utility.not_reachable_penelty_tocks;
	}

	public void Tick(int _tick_count) {
		if ((_tick_count + tick_offset) % ticks_per_tock == 0) Tock();
	}
	
	public void Tock() {
		if (not_reachable_penalty > 0) {
			--not_reachable_penalty;
		}
		if (utility.rnd() < utility.resource_chance_grow 
				&& stuff < utility.resource_max_stuff ) {
			++stuff;
		} else if (utility.rnd() < utility.resource_change_spawn) {
			WorldPoint spawn_loc = theSpriteManager.FindSpotForResource(this.loc);// theSpriteManagerFindGoodSpot(loc, theSpriteManager.utility.resourceRadius, theSpriteManager.theWorld.GetTileSize(), true);
			if (spawn_loc != null) {
				theSpriteManager.PlaceResource(spawn_loc, this.GetType(), true);
			}
		}
	}

}