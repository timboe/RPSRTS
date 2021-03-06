package com.timboe.rpsrts.sprites;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

import com.timboe.rpsrts.enumerators.ActorType;
import com.timboe.rpsrts.enumerators.BuildingType;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.enumerators.ResourceType;
import com.timboe.rpsrts.world.WeightedPoint;

public class Building extends Sprite {

	protected BuildingType type;
	private HashSet<ResourceType> iCollect = new HashSet<ResourceType>();
	private HashSet<ActorType> iAttract = new HashSet<ActorType>();
	protected ObjectOwner owner;
	protected boolean delete_hover = false;
	protected boolean move_hover = false;
	private final ArrayList<Actor> employees = new ArrayList<Actor>();
	protected boolean underConstruction;
	protected int y_offset;
	private int primedToExplode = 0;
	protected int shrapnel = 0;

	private int no_local_resource_penalty;
	private int no_local_resource_counter;

	protected Building(final int _ID, final int _x, final int _y, final int _r, final BuildingType _bt, final ObjectOwner _oo) {
		super(_ID, _x, _y, _r);
		type = _bt;
		owner = _oo;
		animSteps = 4;
		y_offset = 0;
		underConstruction = false;
		maxHealth = utility.building_max_health;
		resource_manager.AddBuildingToTally(_oo, _bt);
		no_local_resource_counter = 0;

		GridRegister();

		if (type == BuildingType.Base) {
			maxHealth *= 20;
			health = maxHealth;
			getiCollect().add(ResourceType.Cactus);
			getiCollect().add(ResourceType.Tree);
			getiCollect().add(ResourceType.Mine);
			getiCollect().add(ResourceType.Rockpile);
		} else if (type == BuildingType.AttractorPaper) {
			maxHealth *= 6;
			health = maxHealth;
			y_offset = 14;
			getiAttract().add(ActorType.Paper);
			getiAttract().add(ActorType.Lizard);
			getiAttract().add(ActorType.Spock);
		} else if (type == BuildingType.AttractorRock) {
			maxHealth *= 6;
			health = maxHealth;
			y_offset = 14;
			getiAttract().add(ActorType.Rock);
			getiAttract().add(ActorType.Lizard);
		} else if (type == BuildingType.AttractorScissors) {
			maxHealth *= 6;
			health = maxHealth;
			y_offset = 14;
			getiAttract().add(ActorType.Scissors);
			getiAttract().add(ActorType.Spock);
		} else {
			health = maxHealth / 4;
			underConstruction = true;
			if (type == BuildingType.Woodshop) {
				getiCollect().add(ResourceType.Cactus);
				getiCollect().add(ResourceType.Tree);
			} else if (type == BuildingType.Rockery) {
				getiCollect().add(ResourceType.Rockpile);
			} else if (type == BuildingType.Smelter) {
				getiCollect().add(ResourceType.Mine);
			}
		}
		animStep = utility.rndI(animSteps);
	}

	public synchronized void AddEmployee(final Actor _a) {
		employees.add(_a);
	}

	public void BuildAction() {
		if (underConstruction == false) return;
		health += utility.building_health_per_build_action;
		if (health >= maxHealth) { //BUILT
			health = maxHealth;
			underConstruction = false;
			resource_manager.IncreaseMaxUnits(type, owner);
		}
	}

	public void DeleteHover() {
		delete_hover = true;
	}

	public HashSet<ActorType> GetAttracts() {
		return getiAttract();
	}


	public Boolean GetBeingBuilt() {
		return underConstruction;
	}

	public HashSet<ResourceType> GetCollects(){
		return getiCollect();
	}

	public synchronized int GetEmployees() {
		return employees.size();
	}

	public HashSet<ActorType> getiAttract() {
		return iAttract;
	}

	public HashSet<ResourceType> getiCollect() {
		return iCollect;
	}

	@ Override
	public boolean GetIsBuilding() {
		return true;
	}

	public Resource GetNewGatherTask(final Actor _client) {
		Resource toFetch = null;
		toFetch = theSpriteManager.GetNearestResource(this, _client, utility.building_gather_radius); //Get nearest resource - no distance requirement
		if (toFetch == null ) {
			no_local_resource_penalty += utility.building_no_resource_penalty;
			no_local_resource_counter++;
			//System.out.println(this+" NOT TAKING NO WORKERS FOR "+no_local_resource_penalty+"s (NO LOCAL RESOURCE, STRIKE "+no_local_resource_counter+")");
		}
		return toFetch;
	}

	public int GetNoLocalResourceCounter() {
		return no_local_resource_counter;
	}

	@Override
	public ObjectOwner GetOwner() {
		return owner;
	}

	public BuildingType GetType() {
		return type;
	}

	private void GridDeRegister() {
		//snap me to the world grid
		final WeightedPoint _my_snap = theSpriteManager.ClipToGrid(this.GetLoc());
		_my_snap.RemoveSprite(this);
		if (_my_snap != null) {
			for (final WeightedPoint _s : _my_snap.GetNieghbours()) {
				_s.RemoveSprite(this);
			}
		}
	}

	private void GridRegister() {
		//snap me to the world grid
		final WeightedPoint _my_snap = theSpriteManager.ClipToGrid(this.GetLoc());
		if (_my_snap != null) {
			_my_snap.GiveSprite(this);
			for (final WeightedPoint _s : _my_snap.GetNieghbours()) {
				_s.GiveCollision(this);
			}
		}
	}

	@Override
	public void Kill() {
		if (dead == true) return;
		resource_manager.RemoveBuildingFromTally(owner, type);
		if (underConstruction == false) {
			resource_manager.DecreaseMaxUnits(type, owner);
			if (type == BuildingType.Base) theSpriteManager.PlaceSpooge(x, y, GetOwner(), utility.spooges_base_death, utility.spooges_scale_base_death);
			else if (getiAttract().size() == 0) theSpriteManager.PlaceSpooge(x, y, GetOwner(), utility.spooges_building_death, utility.spooges_scale_building_death);
			else theSpriteManager.PlaceSpooge(x, y, GetOwner(), utility.spooges_totem_death, utility.spooges_scale_totem_death);
		}
		dead = true;
		if (primedToExplode > 0) {
			theSpriteManager.CheckBuildingExplode(GetLoc(), GetOwner());
		}
		GridDeRegister();
	}

	public synchronized void MoveBuilding(final int new_x, final int new_y) {
		if (x == new_x && y == new_y) return;
    	if (theSpriteManager.CheckSafe(true
    			, true
    			, new_x
    			, new_y
    			, r
    			, ID
    			, 0) == false) {
    		return;
    	}
		GridDeRegister();
		x = new_x;
		y = new_y;
		x_prec = x + utility.rnd() - .5f;
		y_prec = y + utility.rnd() - .5f;
		GridRegister();
		if (GetOwner() == ObjectOwner.Player) {
			move_hover = true;
		}
		//force update destination of followers
//		for (final Actor _a : employees) {
//			_a.Job_Guard_Renavagate();
//		}
	}

	public boolean Recruiting() {
		if (no_local_resource_penalty > 0) return false;
		return true;
	}

	public synchronized void RemoveEmployee(final Actor _a) {
		employees.remove(_a);
	}

	public synchronized void setiAttract(final HashSet<ActorType> iAttract) {
		this.iAttract = iAttract;
	}

	public synchronized void setiCollect(final HashSet<ResourceType> iCollect) {
		this.iCollect = iCollect;
	}

	public void SetPrimedToExplode(final int _pte) {
		primedToExplode += _pte;
	}

	public void Shrapnel() {
		shrapnel += utility.building_Explode_ticks;
	}

	public synchronized void Tick(final int _tick_count) {
		if ((_tick_count + tick_offset) % ticks_per_tock == 0) Tock();
		if (shrapnel > 0) {
			--shrapnel;
			//posibility for chain reaction
			SetPrimedToExplode(+1);
			Attack(utility.building_Explode_damage * utility.building_Explode_vs_building_multiplier);
			SetPrimedToExplode(-1);
		}
	}

	private void Tock() {
		if (no_local_resource_penalty > 0) --no_local_resource_penalty;
		if (type == BuildingType.Base) return;
		//Garbage check on employees
		final Vector<Actor> toRemove = new Vector<Actor>();
		employees.remove(null);
		for (final Actor _a : employees) {
			if (_a.GetDead() == true) toRemove.add(_a);
		}
		for (final Actor _toRem: toRemove) {
			employees.remove(_toRem);
		}

		if (underConstruction == true) return;

		if (health < maxHealth) ++health;

		if (getiCollect().size() > 0) {
			resource_manager.TryToSpawnUnit(this); //If a gather
		} else if (getiAttract().size() > 0 && GetEmployees() > 0) { //if attractor w employees
			if (GetEmployees() > resource_manager.GetActorsPerAttractor(owner, type)) {
				//Fire unit
				employees.get(0).QuitJob(true);
			}
		}
	}

}
