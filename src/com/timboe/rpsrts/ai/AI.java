package com.timboe.rpsrts.ai;

import java.util.ArrayList;
import java.util.Vector;

import com.timboe.rpsrts.enumerators.ActorType;
import com.timboe.rpsrts.enumerators.BuildingType;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.enumerators.PathfindStatus;
import com.timboe.rpsrts.enumerators.ResourceType;
import com.timboe.rpsrts.managers.GameWorld;
import com.timboe.rpsrts.managers.ResourceManager;
import com.timboe.rpsrts.managers.SpriteManager;
import com.timboe.rpsrts.managers.Utility;
import com.timboe.rpsrts.sprites.Actor;
import com.timboe.rpsrts.sprites.Building;
import com.timboe.rpsrts.sprites.Resource;
import com.timboe.rpsrts.sprites.Sprite;
import com.timboe.rpsrts.world.Pathfinder;
import com.timboe.rpsrts.world.WorldPoint;

public class AI implements Runnable {
	private final Utility utility = Utility.GetUtility();
	private final GameWorld theWorld = GameWorld.GetGameWorld();
	protected final SpriteManager theSpriteManager = SpriteManager.GetSpriteManager();
	private final ResourceManager resource_manager = ResourceManager.GetResourceManager();
	
	ArrayList<Thread> pathfinding_thread = new ArrayList<Thread>();
	ArrayList<Pathfinder> pathfinder = new ArrayList<Pathfinder>();
	ArrayList<PathfindStatus> navagate_status = new ArrayList<PathfindStatus>();// {PathfindStatus.NotRun,PathfindStatus.NotRun,PathfindStatus.NotRun};
	ArrayList<ArrayList<WorldPoint>> waypoint_list = new ArrayList<ArrayList<WorldPoint>>();
	ArrayList<WorldPoint> attack_dest = new ArrayList<WorldPoint>();
	ArrayList<WorldPoint> attack_source = new ArrayList<WorldPoint>();
	ArrayList<Building> attack_attractors = new ArrayList<Building>();
	ArrayList<Building> defence_attractors = new ArrayList<Building>();
	
	int woodshop_countdown = 0;
	int rockery_countdown = 0;
	int smelter_countdown = 0;
	
	int paper_attractor_speed = 0;
	int rock_attractor_speed = 0;
	int scissor_attractor_speed = 0;
	
	boolean attack_paper = false;
	boolean attack_rock = false;
	boolean attack_scissors = false;
	
	private ObjectOwner me;
	private ObjectOwner enemy;
	
	int AICount = 0;

	public AI (ObjectOwner _playing_for) {
		if (_playing_for == ObjectOwner.Enemy) {
			me = ObjectOwner.Enemy;
			enemy = ObjectOwner.Player;
		} else {
			me = ObjectOwner.Player;
			enemy = ObjectOwner.Enemy;
		}
		
		//These need to be appended for later storage
		for (int i=0; i<3; ++i) {
			pathfinding_thread.add(null);
			pathfinder.add(null);
			navagate_status.add(PathfindStatus.NotRun);
			waypoint_list.add(null);
			attack_dest.add(null);
			attack_source.add(null);
		}
		
	}

	void CheckOffenceAttractors() {
		//OFFENCE//
		final Vector<Sprite> toRemove = new Vector<Sprite>();
		for (Building _b : attack_attractors) {
			Enum<BuildingType> _bt = _b.GetType();
			
			//Clean up any of our attack attractors which have been taken down
			if (_b.GetDead() == true) {
				System.out.println("--AI["+me+"]: ATTACK ATTRACTOR IS DEAD! - "+_b.GetType());
				toRemove.add(_b);
				if(_b.GetType() == BuildingType.AttractorPaper) attack_paper = false;
				else if(_b.GetType() == BuildingType.AttractorRock) attack_rock = false;
				else if(_b.GetType() == BuildingType.AttractorScissors) attack_scissors = false;
				continue;
			}
			
			if (waypoint_list.get(_bt.ordinal()) == null || waypoint_list.get(_bt.ordinal()).size() > 0) continue;

			//Is everything fine with this attractor? (we know it's at its final destination)
			boolean aOK = false;
			synchronized (theSpriteManager.GetBuildingOjects()) {
				for (Building _bb : theSpriteManager.GetBuildingOjects()) {
					if (_bb.GetOwner() == me) continue;
					if (utility.Seperation(_b.GetLoc(), _bb.GetLoc()) < 2 * utility.wander_radius) {
						aOK = true;
						break;
					}
				}	
			}
			if (aOK == true) continue; //There is an enemy building nearby - good!
		
			//If this is executing then there is no enemy building nearby - can we move?
			BuildingType toAttackType = null;
			if (_b.GetType() == BuildingType.AttractorPaper) {
				toAttackType = BuildingType.Rockery;
			} else if (_b.GetType() == BuildingType.AttractorRock) {
				toAttackType = BuildingType.Smelter;
			} else if (_b.GetType() == BuildingType.AttractorScissors) {
				toAttackType = BuildingType.Woodshop;
			}
			Building toAttack = getWhatToAttack(toAttackType);
			if (toAttack == null && utility.rnd() < 0.75f) {
				toAttack = theSpriteManager.GetBase(enemy); //Go all out?
			}
			WorldPoint new_destination_location = null;
			if(toAttack != null) {
				new_destination_location = theSpriteManager.FindGoodSpot(toAttack.GetLoc(), utility.attractorRadius, 2 * utility.wander_radius, false);
			}
			
			if (new_destination_location == null) {
				//bah! refund then please
				System.out.println("--AI["+me+"]: ATTACK ATTRACTOR "+_b.GetType()+" CAN NOT FIND ALT TARGET, REFUNDING.");
				Refund(_b);
			} else {
				//now I go after yooo!
				System.out.println("--AI["+me+"]: ATTACK ATTRACTOR "+_b.GetType()+" HAS A NEW TARGET: "+toAttack.GetType());
				attack_source.set(_bt.ordinal(), attack_dest.get(_bt.ordinal()) );
				attack_dest.set(_bt.ordinal(), new_destination_location);
				pathfinding_thread.set(_bt.ordinal(), null);
				waypoint_list.set(_bt.ordinal(), null);
				navagate_status.set(_bt.ordinal(), PathfindStatus.NotRun);
			}
		}
		for (Sprite _s : toRemove) {
			attack_attractors.remove(_s);
		}
	}
	
	private void CheckDefenceAttractors() {
		//DEFENCE
		for (Building _b : defence_attractors) {
			boolean enemy_troop_nearby = false;
			boolean my_troop_nearby = false;
			synchronized (theSpriteManager.GetActorObjects()) {
				for (Actor _a : theSpriteManager.GetActorObjects()) {
					if (utility.Seperation(_b.GetLoc(), _a.GetLoc()) < 2 *  utility.wander_radius) {
						if (_a.GetOwner() == enemy) enemy_troop_nearby = true;
						else if (_a.GetOwner() == me) my_troop_nearby = true;
						if (enemy_troop_nearby == true && my_troop_nearby == true) break;
					}
				}
			}
			if (!(enemy_troop_nearby == true && my_troop_nearby == true)) { //note: negated
				//refund then remove
				Refund(_b);
			}
		}
	}

	void CheckIfAttack() {
		
		if (utility.rnd() > 0.01f) return; //only try attack 1% of tocks
		System.out.println("AI["+me+"]: TRY ATTACK");

		//Now to see with what to attack
		Vector<ActorType> toAttackWith = new Vector<ActorType>();
		if (utility.rnd() < GetAttackChance(ActorType.Paper) 
				&& attack_paper == false 
				&& resource_manager.CanAfford(BuildingType.AttractorPaper, me) == true) {
			toAttackWith.add(ActorType.Paper);
			System.out.println("--AI["+me+"]: ATTACK WITH: PAPER (Attack chance "+GetAttackChance(ActorType.Paper) +")");
		}
		
		if (utility.rnd() < GetAttackChance(ActorType.Rock)
				&& attack_rock == false
				&& resource_manager.CanAfford(BuildingType.AttractorRock, me) == true) {
			toAttackWith.add(ActorType.Rock);
			System.out.println("--AI["+me+"]: ATTACK WITH: ROCK (Attack chance "+GetAttackChance(ActorType.Rock) +")");
		}
		
		if (utility.rnd() < GetAttackChance(ActorType.Scissors) 
				&& attack_scissors == false 
				&& resource_manager.CanAfford(BuildingType.AttractorScissors, me) == true) {
			toAttackWith.add(ActorType.Scissors);
			System.out.println("--AI["+me+"]: ATTACK WITH: SCISSORS (Attack chance "+GetAttackChance(ActorType.Scissors) +")");
		}
		
		//Prefer to attack with multiple. 70% chance of calling off attack if only one type involved
		if (toAttackWith.size() == 1) {
			if (utility.rnd() < 0.7f) {
				toAttackWith.clear();
				System.out.println("----AI["+me+"]: ATTACK CANCELLED DUE TO POOR PARTICIPATION OF RPS");
			}
		}
		
		if (toAttackWith.size() > 0) {
			//decide rush or careful
			boolean rush = false;
			if (utility.rnd() < 0.5f) rush = true;
			
			if (rush == true) { //everyone goes max speed
				paper_attractor_speed = 1;
				rock_attractor_speed = 3;
				scissor_attractor_speed = 2;
				System.out.println("----AI["+me+"]: RUSH ATTACK");
			} else { //go speed on slowest
				System.out.println("----AI["+me+"]: SLOW ATTACK");
				if (toAttackWith.contains(ActorType.Rock)) {
					paper_attractor_speed = 3;
					rock_attractor_speed = 3;
					scissor_attractor_speed = 3;
				} else if (toAttackWith.contains(ActorType.Scissors)) {
					paper_attractor_speed = 2;
					rock_attractor_speed = 2;
					scissor_attractor_speed = 2;
				} else if (toAttackWith.contains(ActorType.Paper)) {
					paper_attractor_speed = 1;
					rock_attractor_speed = 1;
					scissor_attractor_speed = 1;
				}
			}
			
			for (ActorType _t : toAttackWith) {
				BuildingType toAttackType = null;
				BuildingType toBuild = null;
				if (_t == ActorType.Paper) {
					toAttackType = BuildingType.Rockery;
					toBuild = BuildingType.AttractorPaper;
				} else if (_t == ActorType.Rock) {
					toAttackType = BuildingType.Smelter;
					toBuild = BuildingType.AttractorRock;
				} else if (_t == ActorType.Scissors) {
					toAttackType = BuildingType.Woodshop;
					toBuild = BuildingType.AttractorScissors;
				}			
				//find a place to attack
				Building toAttack = getWhatToAttack(toAttackType);
				//if this comes back nill, go for base!
				if (toAttack == null) {
					toAttack = theSpriteManager.GetBase(enemy);
				}

				WorldPoint destination_location = theSpriteManager.FindGoodSpot(toAttack.GetLoc(), utility.attractorRadius, 2 * utility.wander_radius, false);
				WorldPoint starting_location = theSpriteManager.FindGoodSpot(theSpriteManager.GetBase(me).GetLoc(), utility.attractorRadius, 2 * utility.wander_radius, false);
				if (destination_location != null && starting_location != null) {
					Building attackor = theSpriteManager.PlaceBuilding(starting_location, toBuild, me);
					attack_attractors.add(attackor);
					
					attack_dest.set(toBuild.ordinal(), destination_location);
					attack_source.set(toBuild.ordinal(), starting_location);
					pathfinding_thread.set(toBuild.ordinal(), null);
					waypoint_list.set(toBuild.ordinal(), null);
					navagate_status.set(toBuild.ordinal(), PathfindStatus.NotRun);
					if (_t == ActorType.Paper) {
						attack_paper = true;
					} else if (_t == ActorType.Rock) {
						attack_rock = true;
					} else if (_t == ActorType.Scissors) {
						attack_scissors = true;
					}
					System.out.println("------AI["+me+"]: "+_t+" WILL BE ATTACKING "+toAttack.GetType());
				} else {
					System.out.println("------AI["+me+"]: "+_t+" ATTACK FAILED DUE TO LACK OF GOOD LOCATION");
				}
			}
		}
	}

	void CheckIfDefence() {
		if (utility.rnd() > 0.25f) return;
		
		final Vector<Sprite> toKill = new Vector<Sprite>();
		for (Building _b : defence_attractors) {
			if (_b.GetDead() == true) toKill.add(_b);
		}
		for (Sprite _s : toKill) defence_attractors.remove(_s);
		
		//look at my actors, are any of them under attack?
		synchronized (theSpriteManager.GetActorObjects()) {
			for (Actor _a : theSpriteManager.GetActorObjects()) {
				if (_a.GetOwner() == me) continue; ///THIS WAS ObjectOwner.ENEMY(me) TODO CHECK AS I THINK IT SHOULD BE PLAYER (enemy)
				if (_a.GetAttackTarget() == null) continue;
				//Get if close to a target.
				//EXTRA ALLOWANCE FOR DEFENCE, * 4 RATHER THAN * 2 TO KEEP CLEAR OF CURRENT WARZONES
				boolean aOK = false;
				for (Building _b : attack_attractors) {
					if (_b.getiCollect().contains( _a.GetType() ) == false) continue;
					if (utility.Seperation(_b.GetLoc(), _a.GetLoc()) < 4 * utility.wander_radius) {
						aOK = true;
						break;
					}
				}
				for (Building _b : defence_attractors) {
					if (_b.getiCollect().contains( _a.GetType() ) == false) continue;
					if (utility.Seperation(_b.GetLoc(), _a.GetLoc()) < 4 * utility.wander_radius) {
						aOK = true;
						break;
					}
				}
				if (aOK == false) { //gawd damn player, attacking our propahteh! (well, actors for now)
					//choose correct type
					BuildingType _bt = null;
					if (_a.GetType() == ActorType.Paper) _bt = BuildingType.AttractorScissors;
					else if (_a.GetType() == ActorType.Rock) _bt = BuildingType.AttractorPaper;
					else if (_a.GetType() == ActorType.Scissors) _bt = BuildingType.AttractorRock;
					
					boolean haveOne = false;
					for (Building _b : defence_attractors) {
						if (_b.GetType() == _bt) {
							haveOne = true;
							break;
						}
					}
					
					if (haveOne == false && resource_manager.CanAfford(_bt, me)) {
						//place new attractor
						WorldPoint loc = theSpriteManager.FindGoodSpot(_a.GetLoc(), utility.attractorRadius, utility.attractorRadius*10, false);
						if (loc == null) break;					
						Building defendor = theSpriteManager.PlaceBuilding(loc, _bt, me);
						defence_attractors.add(defendor);
					}
				}
			}
		}
		
	}
	
	void CheckPathfindingThreads() {
		for (BuildingType _bt : BuildingType.values()) {
			if (_bt.ordinal() > BuildingType.AttractorRock.ordinal()) { //Only do ATTRACTOR types
				continue;
			}
			if (pathfinding_thread.get(_bt.ordinal()) != null) {
				if (pathfinding_thread.get(_bt.ordinal()).isAlive() == true) {
					navagate_status.set(_bt.ordinal(), PathfindStatus.Running);
					return; //Am currently path finding
				} else {
					waypoint_list.set(_bt.ordinal(), pathfinder.get(_bt.ordinal()).GetResult());
					if (waypoint_list.get(_bt.ordinal()) == null) {
						navagate_status.set(_bt.ordinal(), PathfindStatus.Failed);
					} else {
						navagate_status.set(_bt.ordinal(), PathfindStatus.Passed);
					}
					pathfinder.set(_bt.ordinal(), null);
					pathfinding_thread.set(_bt.ordinal(), null);
				}
			}
		}

	}

	void DoAttractorMoving() {
		CheckPathfindingThreads();
		
		if (attack_attractors.size() > 0) {
			for (Building _b : attack_attractors) {
				BuildingType _bt  = _b.GetType();
				if (_bt == BuildingType.AttractorPaper) {
					if (AICount % paper_attractor_speed != 0) continue;
				} else if (_bt == BuildingType.AttractorRock) {
					if (AICount % rock_attractor_speed != 0) continue;
				} else if (_bt == BuildingType.AttractorScissors) {
					if (AICount % scissor_attractor_speed != 0) continue;
				}
				//have I pathfinded-yet?
				if (navagate_status.get(_bt.ordinal()) == PathfindStatus.NotRun) {
					pathfinder.set(_bt.ordinal(), new Pathfinder(attack_source.get(_bt.ordinal()), attack_dest.get(_bt.ordinal()), utility.attractorRadius) );
					pathfinding_thread.set(_bt.ordinal(), new Thread(pathfinder.get(_bt.ordinal())) );
					pathfinding_thread.get(_bt.ordinal()).start();
				} else if (navagate_status.get(_bt.ordinal()) == PathfindStatus.Failed) {
					//Navigation failed
					Refund(_b); //can i get some cash back on that one?
				} else if (navagate_status.get(_bt.ordinal()) == PathfindStatus.Passed) {
					//Navigation good - Move to next waypoint
					WorldPoint waypoint = null;
					
					if (waypoint_list.get(_bt.ordinal()).size() > 0) {
						waypoint = waypoint_list.get(_bt.ordinal()).remove( waypoint_list.get(_bt.ordinal()).size() - 1 );
					}
					
					if (waypoint != null) {
						_b.MoveBuilding(waypoint.getX(), waypoint.getY());
					}
				}
			}
		}
	}
	
	void DoUnitProduction() {
		//If we have enough resources to make a new unit and still have enough left for an attractor
		//OR there are less than AI_MinUnits left, then make units.
		
		if ((resource_manager.GetNWood(me) - utility.COST_Paper_Wood) > utility.COST_AttractorPaper_Wood 
				|| resource_manager.GetNPaper(me) < utility.AI_MinUnits) {
			resource_manager.SetGeneratingPaper(me, true);
		} else {
			resource_manager.SetGeneratingPaper(me, false);
		}
		if ((resource_manager.GetNIron(me) - utility.COST_Scissors_Iron) > utility.COST_AttractorScissors_Iron 
				|| resource_manager.GetNScissor(me) < utility.AI_MinUnits) {
			resource_manager.SetGeneratingScissors(me, true);
		} else {
			resource_manager.SetGeneratingScissors(me, false);
		}
		if ((resource_manager.GetNStone(me) - utility.COST_Rock_Stone) > utility.COST_AttractorRock_Stone 
				|| resource_manager.GetNRock(me) < utility.AI_MinUnits) {
			resource_manager.SetGeneratingRock(me, true);
		} else {
			resource_manager.SetGeneratingRock(me, false);
		}
	}
	
	private WorldPoint FindBestLocationForBuilding(BuildingType _bt) {
		int maxResource = 0;
		WorldPoint chosenLocation = null;

		//for buildings
		synchronized (theSpriteManager.GetBuildingOjects()) {
			for (final Building _b : theSpriteManager.GetBuildingOjects()) {
				if (_b.GetOwner() == enemy) {
					continue;
				}
				if (_b.getiAttract().size() > 0) { //Don't plant building near attractor
					continue;
				}
				for (int x = _b.GetX() - utility.building_gather_radius; x < _b.GetX() + utility.building_gather_radius; x += utility.building_AI_GranularityToStudy*utility.rndI(theWorld.tiles_size)) {
					for (int y = _b.GetY() - utility.building_gather_radius; y < _b.GetY() + utility.building_gather_radius; y += utility.building_AI_GranularityToStudy*utility.rndI(theWorld.tiles_size)) {
						//Check we're near enough the building
						if (utility.Seperation(new WorldPoint(x,y), _b.GetLoc()) > utility.building_gather_radius) {
							continue;
						}
						//check we can place here (RESOURCE/BUILDING and ACTOR)
						if (theSpriteManager.CheckSafe(true,true,x, y, utility.buildingRadius, 0, 0) == false) {
							continue;
						}
						//check is open
						boolean isOpen = true;
						synchronized (theSpriteManager.GetCollisionObjects()) {
							for (final Sprite _s : theSpriteManager.GetCollisionObjects()) {
								if (utility.Seperation(new WorldPoint(x,y), _s.GetLoc()) < utility.building_AI_openSpace * utility.buildingRadius) {
									isOpen = false;
									break;
								}
							}
						}
						if (isOpen == false) {
							continue;
						}
						//Count resources.
						int resourcesHere = 0;
						synchronized (theSpriteManager.GetResourceObjects()) {
							for (final Resource _r : theSpriteManager.GetResourceObjects()) {
								if (_bt == BuildingType.Smelter && _r.GetType() != ResourceType.Mine) {
									continue;
								} else if (_bt == BuildingType.Rockery && _r.GetType() != ResourceType.Rockpile) {
									continue;
								} else if (_bt == BuildingType.Woodshop
										&& !(_r.GetType() != ResourceType.Cactus || _r.GetType() != ResourceType.Tree)) {
									continue;
								}
								if (utility.Seperation(new WorldPoint(x,y), _r.GetLoc()) > utility.building_gather_radius) {
									continue;
								}
								resourcesHere += _r.GetRemaining();
							}	
						}
						if (resourcesHere > maxResource) {
							maxResource = resourcesHere;
							//System.out.println("UPDATED MAX RESOURCE TO " +maxResource);
							chosenLocation = new WorldPoint(x,y);
						}
					}
				}
			}
		}
		return chosenLocation;
	}
	
	Building getWhatToAttack(BuildingType toAttackType) {
		//Find nearest of building type to my base
		Building toAttack = null;
		float distanceToTarget = utility.minimiser_start;
		synchronized (theSpriteManager.GetBuildingOjects()) {
			for (Building _b : theSpriteManager.GetBuildingOjects()) {
				if (_b.GetOwner() == me) continue;
				if (_b.GetType() != toAttackType) continue;
				float sep = utility.Seperation(theSpriteManager.GetBase(me).GetLoc(), _b.GetLoc());
				if (sep < distanceToTarget) {
					distanceToTarget = sep;
					toAttack = _b;
				}
			}
		}
		return toAttack;
	}
	
	public float GetAttackChance(ActorType _actor_type) {
		float _units = 0;
		float _desired_cap = 0;
		if (_actor_type == ActorType.Paper) {
			_units = resource_manager.GetNPaper(me);
			_desired_cap = utility.AI_TargetUnitMultipler * utility.EXTRA_Paper_PerWoodmill;
		} else if (_actor_type == ActorType.Rock) {
			_units = resource_manager.GetNRock(me);
			_desired_cap = utility.AI_TargetUnitMultipler * utility.EXTRA_Rock_PerRockery;
		} else if (_actor_type == ActorType.Scissors) {
			_units = resource_manager.GetNScissor(me);
			_desired_cap = utility.AI_TargetUnitMultipler * utility.EXTRA_Scissors_PerSmelter;
		}
		
		//Subtract our minimum units
		_units -= utility.AI_MinUnits;
		
		//Get how close we are to desired population level
		float _fraction_of_desired = _units / _desired_cap;
		//Chance of attack is fraction of desired / 2
		return _fraction_of_desired / 2f;
	}
	
	void Refund(Building _b) {
		resource_manager.Refund(_b.GetType(), me);
		_b.Kill();
	}
	
	void RemoveOldBuildings() {
		synchronized (theSpriteManager.GetBuildingOjects()) {
			for (final Building _b : theSpriteManager.GetBuildingOjects()) {
				if (_b.GetOwner() == enemy) {
					continue;
				}
				if (_b.no_local_resource_counter >= utility.AI_BadBuilding_Before_Sell) {
					Refund(_b);
				}
			}
		}
	}
	
	@Override
	public void run() {
		++AICount;
		//System.out.println("AI TICK");
		
		//Can we get rid of old building?
		RemoveOldBuildings();
		
		//See if we have enough resources and buildings to be making new units
		DoUnitProduction();
		
		//Build curtailer cooldowns
		if (woodshop_countdown > 0) --woodshop_countdown;
		if (rockery_countdown > 0) --rockery_countdown;
		if (smelter_countdown > 0) --smelter_countdown;
		
		//Do we need all our attack and defence attractors any more?
		//refund/move if not
		CheckOffenceAttractors();
//		CheckDefenceAttractors();

		//check if need to defend
//		CheckIfDefence();
		
		//try an attack
		CheckIfAttack();
		//move attractors towards their destinations with their payloads
		DoAttractorMoving();

		//see if we can build new building, collect moar nomz.
		TryBuildResourceGathere();

	}

	void TryBuildResourceGathere() {
		final Vector<BuildingType> toPlace = new Vector<BuildingType>();
		if (resource_manager.CanAfford(BuildingType.Woodshop, me) == true
				&& utility.rnd() < 0.1f
				&& woodshop_countdown == 0
				&& resource_manager.GetNPaper(me) >= resource_manager.GetMaxPaper(me) - utility.AI_NewUnitsWhenXClosetoCap) {
				toPlace.add(BuildingType.Woodshop);

		}
		if (resource_manager.CanAfford(BuildingType.Smelter, me) == true
				&& utility.rnd() < 0.1f
				&& smelter_countdown == 0
				&& resource_manager.GetNScissor(me) >= resource_manager.GetMaxScissor(me) - utility.AI_NewUnitsWhenXClosetoCap) {
				toPlace.add(BuildingType.Smelter);
		}
		if (resource_manager.CanAfford(BuildingType.Rockery, me) == true
				&& utility.rnd() < 0.1f
				&& rockery_countdown == 0
				&& resource_manager.GetNRock(me) >= resource_manager.GetMaxRock(me) - utility.AI_NewUnitsWhenXClosetoCap) {
				toPlace.add(BuildingType.Rockery);
		}
		if (toPlace.size() > 0) {
			int random = utility.rndI(toPlace.size());
			random = 0;
			final WorldPoint location = FindBestLocationForBuilding(toPlace.elementAt(random));
			//System.out.println("ToPlace:"+location);

			if (location != null) {
				theSpriteManager.PlaceBuilding(location,toPlace.elementAt(random), me);
				if (toPlace.elementAt(random) == BuildingType.Rockery) rockery_countdown += utility.AI_BuildCooldown;
				if (toPlace.elementAt(random) == BuildingType.Smelter) smelter_countdown += utility.AI_BuildCooldown;
				if (toPlace.elementAt(random) == BuildingType.Woodshop) woodshop_countdown += utility.AI_BuildCooldown;
			}
		}
	}
}
	
	
