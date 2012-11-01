package com.timboe.rpsrts.ai;

import java.util.Vector;

import com.timboe.rpsrts.enumerators.ActorType;
import com.timboe.rpsrts.enumerators.BuildingType;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.enumerators.ResourceType;
import com.timboe.rpsrts.managers.GameWorld;
import com.timboe.rpsrts.managers.ResourceManager;
import com.timboe.rpsrts.managers.SpriteManager;
import com.timboe.rpsrts.managers.Utility;
import com.timboe.rpsrts.sprites.Actor;
import com.timboe.rpsrts.sprites.Building;
import com.timboe.rpsrts.sprites.Resource;
import com.timboe.rpsrts.sprites.Sprite;
import com.timboe.rpsrts.world.WorldPoint;

public class AI implements Runnable {
	private final Utility utility = Utility.GetUtility();
	private final GameWorld theWorld = GameWorld.GetGameWorld();
	protected final SpriteManager theSpriteManager = SpriteManager.GetSpriteManager();
	private final ResourceManager resource_manager = ResourceManager.GetResourceManager();

	
	int woodshop_countdown = 0;
	int rockery_countdown = 0;
	int smelter_countdown = 0;
	
	float paper_attractor_speed = 0;
	float rock_attractor_speed = 0;
	float scissor_attractor_speed = 0;
	
	boolean attack_paper = false;
	boolean attack_rock = false;
	boolean attack_scissors = false;
	
	WorldPoint attack_paper_dest;
	WorldPoint attack_rock_dest;
	WorldPoint attack_scissors_dest;
	WorldPoint attack_paper_source;
	WorldPoint attack_rock_source;
	WorldPoint attack_scissors_source;
	
	Vector<Building> attack_attractors = new Vector<Building>();
	Vector<Building> defence_attractors = new Vector<Building>();

	public AI () {
	}

	void CheckAttractors() {
		//OFFENCE
		for (Building _b : attack_attractors) {
			if (_b.GetType() == BuildingType.AttractorPaper) {
				if (attack_paper_source != attack_paper_dest) continue;
			} else if (_b.GetType() == BuildingType.AttractorRock) {
				if (attack_rock_source != attack_rock_dest) continue;
			} else if (_b.GetType() == BuildingType.AttractorScissors) {
				if (attack_scissors_source != attack_scissors_dest) continue;
			}
			
			boolean aOK = false;
			synchronized (theSpriteManager.GetBuildingOjects()) {
				for (Building _bb : theSpriteManager.GetBuildingOjects()) {
					if (_bb.GetOwner() == ObjectOwner.Enemy) continue;
					if (utility.Seperation(_b.GetLoc(), _bb.GetLoc()) < 2 * utility.wander_radius) {
						aOK = true;
						break;
					}
				}	
			}
			if (aOK == true) continue;
			
			//if no nearby enemy buildings then not aOK
			//find something new to attack
			BuildingType toAttackType = null;
			if (_b.GetType() == BuildingType.AttractorPaper) {
				toAttackType = BuildingType.Rockery;
			} else if (_b.GetType() == BuildingType.AttractorRock) {
				toAttackType = BuildingType.Smelter;
			} else if (_b.GetType() == BuildingType.AttractorScissors) {
				toAttackType = BuildingType.Woodshop;
			}
			Building toAttack = getWhatToAttack(toAttackType);
			WorldPoint destination_location= null;
			
			if (toAttack != null) {
				destination_location = theSpriteManager.FindGoodSpot(toAttack.GetLoc(), utility.attractorRadius, utility.attractorRadius*10, false);
			}
			
			if (toAttack == null || destination_location == null) {
				//refund plz
				Refund(_b);
			} else {
				//now I go after yooo
				if (_b.GetType() == BuildingType.AttractorPaper) attack_paper_dest = destination_location;
				else if (_b.GetType() == BuildingType.AttractorRock) attack_rock_dest = destination_location;
				else if (_b.GetType() == BuildingType.AttractorScissors) attack_scissors_dest = destination_location;
			}
		}

		//DEFENCE
		for (Building _b : defence_attractors) {
			boolean player_nearby = false;
			boolean enemy_nearby = false;
			synchronized (theSpriteManager.GetActorObjects()) {
				for (Actor _a : theSpriteManager.GetActorObjects()) {
					if (utility.Seperation(_b.GetLoc(), _a.GetLoc()) < 2 *  utility.wander_radius) {
						if (_a.GetOwner() == ObjectOwner.Player) player_nearby = true;
						else if (_a.GetOwner() == ObjectOwner.Enemy) enemy_nearby = true;
						if (player_nearby == true && enemy_nearby == true) break;
					}
				}
			}
			if (!(player_nearby == true && enemy_nearby == true)) { //note: negated
				//refund then remove
				Refund(_b);
			}
		}
	}

	void CheckIfAttack() {
		
		if (utility.rnd() > 0.01f) return; //only try attack 1% of tocks
		System.out.println("TRY ATTACK");

		final Vector<Sprite> toKill = new Vector<Sprite>();
		for (Building _b : attack_attractors) {
			if (_b.GetDead() == true) {
				toKill.add(_b);
				if(_b.GetType() == BuildingType.AttractorPaper) attack_paper = false;
				else if(_b.GetType() == BuildingType.AttractorRock) attack_rock = false;
				else if(_b.GetType() == BuildingType.AttractorScissors) attack_scissors = false;
			}
		}
		for (Sprite _s : toKill) {
			attack_attractors.remove(_s);
		}

		Vector<ActorType> toAttackWith = new Vector<ActorType>();
		
		//grow to 50%, cap chance at 50%
		float paper_chance = (((float) resource_manager.ENEMY_PAPER -2)/ (float)(utility.AI_TargetUnitMultipler * utility.EXTRA_Paper_PerWoodmill))/2f;
		if (paper_chance > 0.5) paper_chance = 0.5f;
		if (utility.rnd() < paper_chance 
				&& attack_paper == false 
				&& resource_manager.CanAffordBuy(BuildingType.AttractorPaper,ObjectOwner.Enemy,false,false) == true) {
			attack_paper = true;
			toAttackWith.add(ActorType.Paper);
			System.out.println("ATTACK WITH: PAPER");
		}
		
		float rock_chance = (((float) resource_manager.ENEMY_ROCK -2)/ (float)(utility.AI_TargetUnitMultipler * utility.EXTRA_Rock_PerRockery))/2f;
		if (rock_chance > 0.5) rock_chance = 0.5f;
		if (utility.rnd() < rock_chance 
				&& attack_rock == false
				&& resource_manager.CanAffordBuy(BuildingType.AttractorRock,ObjectOwner.Enemy,false,false) == true) {
			attack_rock = true;
			toAttackWith.add(ActorType.Rock);
			System.out.println("ATTACK WITH: ROCK");
		}
		
		float scissors_chance = (((float) resource_manager.ENEMY_SCISSORS -2)/ (float)(utility.AI_TargetUnitMultipler * utility.EXTRA_Scissors_PerSmelter))/2f;
		if (scissors_chance > 0.5) scissors_chance = 0.5f;
		if (utility.rnd() < scissors_chance 
				&& attack_scissors == false 
				&& resource_manager.CanAffordBuy(BuildingType.AttractorScissors,ObjectOwner.Enemy,false,false) == true) {
			attack_scissors = true;
			toAttackWith.add(ActorType.Scissors);
			System.out.println("ATTACK WITH: SCISSORS");
		}
		
		if (toAttackWith.size() > 0) {
			//decide rush or careful
			boolean rush = false;
			if (utility.rnd() > 0.5) rush = true;
			
			float _multiplier = 0.2f * utility.ticks_per_tock;
			if (rush == true) { //everyone goes max speed
				paper_attractor_speed = _multiplier * 2f;
				rock_attractor_speed = _multiplier * 0.5f;
				scissor_attractor_speed = _multiplier * 1f;
			} else { //go speed on slowest
				if (toAttackWith.contains(ActorType.Rock)) {
					paper_attractor_speed = _multiplier * 0.5f;
					rock_attractor_speed = _multiplier * 0.5f;
					scissor_attractor_speed = _multiplier * 0.5f;
				} else if (toAttackWith.contains(ActorType.Scissors)) {
					paper_attractor_speed = _multiplier * 1f;
					rock_attractor_speed = _multiplier * 1f;
					scissor_attractor_speed = _multiplier * 1f;
				} else if (toAttackWith.contains(ActorType.Paper)) {
					paper_attractor_speed = _multiplier * 2f;
					rock_attractor_speed = _multiplier * 2f;
					scissor_attractor_speed = _multiplier * 2f;
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
				//since 1st attack then if this comes back nill, go for base!
				if (toAttack == null) toAttack = theSpriteManager.player_base;

				WorldPoint destination_location = theSpriteManager.FindGoodSpot(toAttack.GetLoc(), utility.attractorRadius, utility.attractorRadius*10, false);
				WorldPoint starting_location = theSpriteManager.FindGoodSpot(theSpriteManager.enemy_base.GetLoc(), utility.attractorRadius, utility.attractorRadius*10, false);
				if (destination_location != null && starting_location != null) {
					Building attackor = theSpriteManager.PlaceBuilding(starting_location, toBuild, ObjectOwner.Enemy);
					resource_manager.CanAffordBuy(toBuild, ObjectOwner.Enemy, true, false);
					attack_attractors.add(attackor);
					if (_t == ActorType.Paper) {
						attack_paper_dest = destination_location;
						attack_paper_source = starting_location;
					} else if (_t == ActorType.Rock) {
						attack_rock_dest = destination_location;
						attack_rock_source = starting_location;
					} else if (_t == ActorType.Scissors) {
						attack_scissors_dest = destination_location;
						attack_scissors_source = starting_location;
					}
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
				if (_a.GetOwner() == ObjectOwner.Enemy) continue;
				if (_a.attack_target == null) continue;
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
					
					if (haveOne == false && resource_manager.CanAffordBuy(_bt, ObjectOwner.Enemy, false, false)) {
						//place new attractor
						WorldPoint loc = theSpriteManager.FindGoodSpot(_a.GetLoc(), utility.attractorRadius, utility.attractorRadius*10, false);
						if (loc == null) break;					
						Building defendor = theSpriteManager.PlaceBuilding(loc, _bt, ObjectOwner.Enemy);
						defence_attractors.add(defendor);
						resource_manager.CanAffordBuy(_bt, ObjectOwner.Enemy, true, false);
					}
				}
			}
		}
		
	}
	
	void DoAttractorMoving() {
		if (attack_attractors.size() > 0) {
			for (Building _b : attack_attractors) {
				WorldPoint _d = null;
				WorldPoint _f = null;
				float _speed = 0;
				if (_b.GetType() == BuildingType.AttractorPaper) {
					_d = attack_paper_dest;
					_f = attack_paper_source;
					_speed = paper_attractor_speed;
				} else if (_b.GetType() == BuildingType.AttractorRock) {
					_d = attack_rock_dest;
					_f = attack_rock_source;
					_speed = rock_attractor_speed;
				} else if (_b.GetType() == BuildingType.AttractorScissors ) {
					_d = attack_scissors_dest;
					_f = attack_scissors_source;
					_speed = scissor_attractor_speed;
				}
				
				if (utility.Seperation(_d, _f) > _b.GetR() * 5) { //try move me closer
					final float _hypotenuse = utility.Seperation(_b.GetLoc(), _d);
					final int new_x = (int) (_f.getX() - ((_f.getX() - _d.getX()) / _hypotenuse) * _speed);
					final int new_y = (int) (_f.getX() - ((_f.getX() - _d.getY()) / _hypotenuse) * _speed);
					
					_f.setLocation(new_x, new_y);					
					_b.MoveBuilding(new_x, new_y);
				} else {
					_f = _d;
				}
			}
		}
	}
	
	void DoUnitProduction() {
		//if we're not _really_ short on people then turn off unit production if peeps too low
		if (resource_manager.ENEMY_WOOD - utility.COST_Paper_Wood < utility.COST_AttractorPaper_Wood 
				&& resource_manager.ENEMY_PAPER > 3) {
			resource_manager.GEN_PAPER_ENEMY = false;
		} else {
			resource_manager.GEN_PAPER_ENEMY = true;
		}
		//if we're not _really_ short on people then turn off unit production if peeps too low
		if (resource_manager.ENEMY_IRON - utility.COST_Scissors_Iron < utility.COST_AttractorScissors_Iron 
				&& resource_manager.ENEMY_SCISSORS > 3) {
			resource_manager.GEN_SCISSORS_ENEMY = false;
		} else {
			resource_manager.GEN_SCISSORS_ENEMY = true;
		}
		//if we're not _really_ short on people then turn off unit production if peeps too low
		if (resource_manager.ENEMY_STONE - utility.COST_Rock_Stone < utility.COST_AttractorRock_Stone 
				&& resource_manager.ENEMY_ROCK > 3) {
			resource_manager.GEN_ROCK_ENEMY = false;
		} else {
			resource_manager.GEN_ROCK_ENEMY = true;
		}
	}
	
	private WorldPoint FindBestLocationForBuilding(BuildingType _bt) {
		int maxResource = 0;
		WorldPoint chosenLocation = null;

		//for buildings
		synchronized (theSpriteManager.GetBuildingOjects()) {
			for (final Building _b : theSpriteManager.GetBuildingOjects()) {
				if (_b.GetOwner() == ObjectOwner.Player) {
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
		Building toAttack = null;
		float distanceToTarget = utility.minimiser_start;
		synchronized (theSpriteManager.GetBuildingOjects()) {
			for (Building _b : theSpriteManager.GetBuildingOjects()) {
				if (_b.GetOwner() == ObjectOwner.Enemy) continue;
				if (_b.GetType() != toAttackType) continue;
				float sep = utility.Seperation(theSpriteManager.enemy_base.GetLoc(), _b.GetLoc());
				if (sep < distanceToTarget) {
					distanceToTarget = sep;
					toAttack = _b;
				}
			}
		}
		return toAttack;
	}
	
	void Refund(Building _b) {
		resource_manager.CanAffordBuy(_b.GetType(), ObjectOwner.Enemy, false, true);
		_b.Kill();
	}
	
	void RemoveOldBuildings() {
		synchronized (theSpriteManager.GetBuildingOjects()) {
			for (final Building _b : theSpriteManager.GetBuildingOjects()) {
				if (_b.GetOwner() == ObjectOwner.Player) {
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
		//System.out.println("AI TICK");
		
		//Can we get rid of old building?
		RemoveOldBuildings();
		
		//See if we have enough resources and buildings to be making new units
		DoUnitProduction();
		
		//Build curtailer cooldowns
		if (woodshop_countdown > 0) --woodshop_countdown;
		if (rockery_countdown > 0) --rockery_countdown;
		if (smelter_countdown > 0) --smelter_countdown;
		
		//try an attack
		CheckIfAttack();
		
		//move attractors towards their destinations with their payloads
		DoAttractorMoving();
		
		//check if need to defend
		CheckIfDefence();
		
		//Do we need all our attack and defence attractors any more?
		//refund/move if not
		CheckAttractors();

		//see if we can build new building, collect moar nomz.
		TryBuildResourceGathere();

	}
	
	public void Tock() {

	}
	
	void TryBuildResourceGathere() {
		final Vector<BuildingType> toPlace = new Vector<BuildingType>();
		if (resource_manager.CanAffordBuy(BuildingType.Woodshop, ObjectOwner.Enemy, false, false) == true
				&& utility.rnd() < 0.1f
				&& woodshop_countdown == 0
				&& resource_manager.ENEMY_PAPER >= resource_manager.ENEMY_MAX_PAPER - utility.AI_NewUnitsWhenXClosetoCap) {
				toPlace.add(BuildingType.Woodshop);

		}
		if (resource_manager.CanAffordBuy(BuildingType.Smelter, ObjectOwner.Enemy, false, false) == true
				&& utility.rnd() < 0.1f
				&& smelter_countdown == 0
				&& resource_manager.ENEMY_SCISSORS >= resource_manager.ENEMY_MAX_SCISSORS - utility.AI_NewUnitsWhenXClosetoCap) {
				toPlace.add(BuildingType.Smelter);
		}
		if (resource_manager.CanAffordBuy(BuildingType.Rockery, ObjectOwner.Enemy, false, false) == true
				&& utility.rnd() < 0.1f
				&& rockery_countdown == 0
				&& resource_manager.ENEMY_ROCK >= resource_manager.ENEMY_MAX_ROCK - utility.AI_NewUnitsWhenXClosetoCap) {
				toPlace.add(BuildingType.Rockery);
		}
		if (toPlace.size() > 0) {
			int random = utility.rndI(toPlace.size());
			random = 0;
			final WorldPoint location = FindBestLocationForBuilding(toPlace.elementAt(random));
			//System.out.println("ToPlace:"+location);

			if (location != null) {
				theSpriteManager.PlaceBuilding(location,toPlace.elementAt(random),ObjectOwner.Enemy);
				resource_manager.CanAffordBuy(toPlace.elementAt(random), ObjectOwner.Enemy, true, false);
				if (toPlace.elementAt(random) == BuildingType.Rockery) rockery_countdown += utility.AI_BuildCooldown;
				if (toPlace.elementAt(random) == BuildingType.Smelter) smelter_countdown += utility.AI_BuildCooldown;
				if (toPlace.elementAt(random) == BuildingType.Woodshop) woodshop_countdown += utility.AI_BuildCooldown;
			}
		}
	}
}
	
	
