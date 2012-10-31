package com.timboe.rpsrts;

import java.util.HashSet;
import java.util.Vector;

public class Building extends Sprite {

	protected BuildingType type;
	HashSet<ResourceType> iCollect = new HashSet<ResourceType>();
	HashSet<ActorType> iAttract = new HashSet<ActorType>(); 
	protected ObjectOwner owner;
	protected boolean delete_hover = false;
	Vector<Actor> employees = new Vector<Actor>();
	protected boolean underConstruction;
	protected int y_offset;
	
	int no_local_resource_penalty;
	int no_local_resource_counter;

	int cost_wood;
	int cost_rock;
	int cost_iron;

	protected Building(int _ID, int _x, int _y, int _r, BuildingType _bt, ObjectOwner _oo) {
		super(_ID, _x, _y, _r);
		type = _bt;
		owner = _oo;
		//employees = 0;
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
			iCollect.add(ResourceType.Cactus);
			iCollect.add(ResourceType.Tree);
			iCollect.add(ResourceType.Mine);
			iCollect.add(ResourceType.Rockpile);
		} else if (type == BuildingType.AttractorPaper) {
			maxHealth *= 6;
			health = maxHealth;
			y_offset = 14;
			iAttract.add(ActorType.Paper);
		} else if (type == BuildingType.AttractorRock) {
			maxHealth *= 6;
			health = maxHealth;
			y_offset = 14;
			iAttract.add(ActorType.Rock);
		} else if (type == BuildingType.AttractorScissors) {
			maxHealth *= 6;
			health = maxHealth;
			y_offset = 14;
			iAttract.add(ActorType.Scissors);
		} else {
			health = maxHealth / 4;
			underConstruction = true;
			if (type == BuildingType.Woodshop) {
				iCollect.add(ResourceType.Cactus);
				iCollect.add(ResourceType.Tree);
			} else if (type == BuildingType.Rockery) {
				iCollect.add(ResourceType.Rockpile);
			} else if (type == BuildingType.Smelter) {
				iCollect.add(ResourceType.Mine);
			}
		}
		animStep = utility.rndI(animSteps);
	}

	private void GridRegister() {
		//snap me to the world grid
		WeightedPoint _my_snap = theSpriteManager.ClipToGrid(this.GetLoc());
		if (_my_snap != null) {
			_my_snap.GiveSprite(this);
			for (WeightedPoint _s : _my_snap.GetNieghbours()) {
				_s.GiveCollision(this);
			}
		}
	}
	
	private void GridDeRegister() {
		//snap me to the world grid
		WeightedPoint _my_snap = theSpriteManager.ClipToGrid(this.GetLoc());
		_my_snap.RemoveSprite(this);
		if (_my_snap != null) {
			for (WeightedPoint _s : _my_snap.GetNieghbours()) {
				_s.RemoveSprite(this);
			}
		}
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

	public void MoveBuilding(int new_x, int new_y) {
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
		//de-register
		GridDeRegister();
		//move
		x = new_x;
		y = new_y;
		//register
		GridRegister();
	}
	
	public void DeleteHover() {
		delete_hover = true;
	}

	public Boolean GetBeingBuilt() {
		return underConstruction;
	}

	public HashSet<ActorType> GetAttracts() {
		return iAttract;
	}
	
	public HashSet<ResourceType> GetCollects(){
		return iCollect;
	}

	public int GetEmployees() {
		return employees.size();
	}

	@ Override
	public boolean GetIsBuilding() {
		return true;
	}

	public Resource GetNewGatherTask(Actor _client) {
		Resource toFetch = null;
		toFetch = theSpriteManager.GetNearestResource(this, _client, utility.building_gather_radius); //Get nearest resource - no distance requirement
		if (toFetch == null ) {
			no_local_resource_penalty += utility.building_no_resource_penalty;
			no_local_resource_counter++;
			System.out.println("BUILDING NOT TAKING ANY MORE WORKERS FOR "+no_local_resource_penalty+" SEC");
		}
		return toFetch;
	}

	@Override
	public ObjectOwner GetOwner() {
		return owner;
	}

	public BuildingType GetType() {
		return type;
	}

	@Override
	public void Kill() {
		if (dead == true) return;
		resource_manager.RemoveBuildingFromTally(owner, type);
		if (underConstruction == false) {
			resource_manager.DecreaseMaxUnits(type, owner);
			if (type == BuildingType.Base) theSpriteManager.PlaceSpooge(x, y, GetOwner(), 1500, 3f);
			else if (iAttract.size() == 0) theSpriteManager.PlaceSpooge(x, y, GetOwner(), 400, 1.2f);
			else theSpriteManager.PlaceSpooge(x, y, GetOwner(), 10, 1f);
		}
		dead = true;
		GridDeRegister();
	}
	
	public void RemoveEmployee(Actor _a) {
		employees.remove(_a);
	}
	
	public void AddEmployee(Actor _a) {
		employees.add(_a);
	}

	public void Tick(int _tick_count) {
		if ((_tick_count + tick_offset) % ticks_per_tock == 0) Tock();
	}
	
	public boolean Recruiting() {
		if (no_local_resource_penalty > 0) return false;
		return true;
	}
	
	public void Tock() {
		if (no_local_resource_penalty > 0) --no_local_resource_penalty;
		if (type == BuildingType.Base) return;
		//Garbage check on employees
		Vector<Actor> toRemove = new Vector<Actor>();
		employees.remove(null);
		for (Actor _a : employees) {
			if (_a.GetDead() == true) toRemove.add(_a);
		}
		for (Actor _toRem: toRemove) {
			employees.remove(_toRem);
		}
		
		if (underConstruction == true) return;
		
		if (health < maxHealth) ++health;
		
		if (iCollect.size() > 0) {
			resource_manager.TryToSpawnUnit(this); //If a gather
		} else if (iAttract.size() > 0 && GetEmployees() > 0) { //if attractor w employees
			if (GetEmployees() > resource_manager.GetActorsPerAttractor(owner, type)) {
				//Fire unit
				employees.elementAt(0).QuitJob(true);
			}
		}
	}

}
