package com.timboe.rpsrts.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;

import com.timboe.rpsrts.ai.AI;
import com.timboe.rpsrts.enumerators.ActorJob;
import com.timboe.rpsrts.enumerators.ActorType;
import com.timboe.rpsrts.enumerators.BiomeType;
import com.timboe.rpsrts.enumerators.BuildingType;
import com.timboe.rpsrts.enumerators.GameStatistics;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.enumerators.ResourceType;
import com.timboe.rpsrts.sprites.Actor;
import com.timboe.rpsrts.sprites.Building;
import com.timboe.rpsrts.sprites.Explosion;
import com.timboe.rpsrts.sprites.Projectile;
import com.timboe.rpsrts.sprites.Resource;
import com.timboe.rpsrts.sprites.SpecialSpawn;
import com.timboe.rpsrts.sprites.Spoogicles;
import com.timboe.rpsrts.sprites.Sprite;
import com.timboe.rpsrts.sprites.WaterfallSplash;
import com.timboe.rpsrts.world.Pathfinder;
import com.timboe.rpsrts.world.WeightedPoint;
import com.timboe.rpsrts.world.WorldPoint;
import com.timboe.rpsrts.world.WorldTile;

public class SpriteManager {

	protected static SpriteManager this_object;
	public static SpriteManager GetSpriteManager() {
		return this_object; //set by inherited Applet or Web
	}

	protected final Utility utility = Utility.GetUtility();
	private   final GameWorld theWorld = GameWorld.GetGameWorld();
	protected ResourceManager resource_manager = null; //Note, due to inter-dependence, we need to pick up this pointer after SpriteManager is made
	private   final PathfinderGrid thePathfinderGrid = PathfinderGrid.GetPathfinderGrid();

	private int ws_step = 0;
	private float ws_time_of_last_operation = 0;
	private boolean worldSeeded = false;
	protected int TickCount = 0;
	protected int FrameCount = 0;

	Thread pathfinding_thread;
	Pathfinder pathfinder;

	Thread AI_thread;
	AI theAI;
	Thread AIHuman_thread;
	AI theHumanAI;

	public int GlobalSpriteCounter;

	private Building player_base;
	private Building enemy_base;

	private final HashSet<Sprite> CollisionObjects = new HashSet<Sprite>();
	private final HashSet<Actor> ActorObjects = new HashSet<Actor>();
	private final HashSet<Building> BuildingOjects = new HashSet<Building>();
	private final HashSet<Resource> ResourceObjects = new HashSet<Resource>();
	private final HashSet<Projectile> ProjectileObjects = new HashSet<Projectile>();
	private final HashSet<Spoogicles> SpoogicleObjects = new HashSet<Spoogicles>();
	private final HashSet<WaterfallSplash> WaterfallSplahsObjects = new HashSet<WaterfallSplash>();
	private final HashSet<SpecialSpawn> SpecialSpawnObjects = new HashSet<SpecialSpawn>();
	private final HashSet<Explosion> ExplosionObjects = new HashSet<Explosion>();

	// Synchronised
	private final Collection<Sprite> CollisionObjectsSync = Collections.synchronizedCollection(CollisionObjects);
	private final Collection<Actor> ActorObjectsSync = Collections.synchronizedCollection(ActorObjects);
	private final Collection<Building> BuildingOjectsSync = Collections.synchronizedCollection(BuildingOjects);
	private final Collection<Resource> ResourceObjectsSync = Collections.synchronizedCollection(ResourceObjects);
	private final Collection<Projectile> ProjectileObjectsSync = Collections.synchronizedCollection(ProjectileObjects);
	private final Collection<Spoogicles> SpoogicleObjectsSync = Collections.synchronizedCollection(SpoogicleObjects);
	private final Collection<WaterfallSplash> WaterfallSplahsObjectsSync = Collections.synchronizedCollection(WaterfallSplahsObjects);
	private final Collection<SpecialSpawn> SpecialSpawnObjectsSync = Collections.synchronizedCollection(SpecialSpawnObjects);
	private final Collection<Explosion> ExplosionObjectsSync = Collections.synchronizedCollection(ExplosionObjects);

	//
	protected final HashSet<Resource> TempResourceHolder = new HashSet<Resource>();
	protected final HashSet<Integer> markedForDeath = new HashSet<Integer>(); //used to not spawn many special units

	protected SpriteManager() {
	}

	private void CheckActorCombat(){
		synchronized (GetActorObjects()) {
			for (final Actor _a : GetActorObjects()) {
				//TODO check this, currently won't change target mid-fight if targeting a person (unless spock)
				if (_a.GetAttackTarget() != null && _a.GetAttackTarget().GetIsActor() == true && _a.GetType() != ActorType.Spock) {
					continue;
				}
				Sprite chosen_target = null;
				float min_distance = utility.minimiser_start;
				for (final Sprite _target : ActorObjects) {
					if (_target.GetDead() == true) {
						continue;
					}
					if (_a.GetID() == _target.GetID()) {
						continue;
					}
					if (_a.GetOwner() == _target.GetOwner()) {
						continue;
					}
					if (utility.Seperation(_a.GetLoc(), _target.GetLoc()) > utility.actor_aggro_radius) {
						continue;
					}
					final boolean getPrefered =  _a.GetIfPreferedTarget(_target);
					final boolean getCloser = (utility.Seperation(_a.GetLoc(), _target.GetLoc()) < min_distance);
					if (chosen_target == null) {
						chosen_target = _target;
						min_distance = utility.Seperation(_a.GetLoc(), _target.GetLoc());
					} else if (getPrefered == true && getCloser == true) {
						chosen_target = _target;
						min_distance = utility.Seperation(_a.GetLoc(), _target.GetLoc());
					} else if (getPrefered == true && getCloser == false) {
						if (_a.GetIfPreferedTarget(chosen_target) == false) {
							chosen_target = _target;
							min_distance = utility.Seperation(_a.GetLoc(), _target.GetLoc());
						}
					} else if (getPrefered == false && getCloser == true) {
						if (_a.GetIfPreferedTarget(chosen_target) == false) {
							chosen_target = _target;
							min_distance = utility.Seperation(_a.GetLoc(), _target.GetLoc());
						}
					}
				}
				if (chosen_target != null) {
					_a.SetNemesis(chosen_target);
					continue;
				}
				for (final Sprite _target : BuildingOjects) {
					if (_target.GetDead() == true) {
						continue;
					}
					if (_a.GetOwner() == _target.GetOwner()) {
						continue;
					}
					if (utility.Seperation(_a.GetLoc(), _target.GetLoc()) > utility.actor_aggro_radius) {
						continue;
					}
					if (chosen_target == null || _a.GetIfPreferedTarget(_target) == true) {
						if (utility.Seperation(_a.GetLoc(), _target.GetLoc()) < min_distance) {
							chosen_target = _target;
							min_distance = utility.Seperation(_a.GetLoc(), _target.GetLoc());
						}
					}
				}
				if (chosen_target != null) {
					_a.SetNemesis(chosen_target);
				}
			}
		}
	}

	private void CheckBuildingEmployment() {
		synchronized (GetBuildingOjects()) {
			for (final Building _b : GetBuildingOjects()) {
				if (_b.GetDead() == true) {
					continue;
				}
				if (_b.GetType() == BuildingType.Base) {
					continue;
				}
				if (_b.Recruiting() == false) {
					continue;
				}

				//building could do with new employee
				float min_sep = utility.minimiser_start;
				Actor toHire = null;
				float min_sep_employed = utility.minimiser_start;
				Actor toHireEmployed = null;
				//If is a collector building type
				if (_b.GetCollects().size() > 0) { //GATHER TYPE
					if (_b.GetEmployees() >= utility.building_gatherers_per_site) {
						continue;
					}
					synchronized (GetActorObjects()) {
						for (final Actor _a : GetActorObjects()) {
							if (_a.GetOwner() != _b.GetOwner()) {
								continue;
							}
							if (_a.GetLastEmployer().contains(_b) == true) {
								continue; //Check was not recent employee
							}
							if (_a.GetCollects().containsAll( _b.GetCollects() ) == false){
								continue; //Actor collects correct res
							}
							//Below here - we can take them.
							final float sep = utility.Seperation(_a.GetLoc(), _b.GetLoc());
							if (sep < min_sep && _a.GetJob() == ActorJob.Idle) { //Ideally get closest unemployed.
								min_sep = utility.Seperation(_a.GetLoc(), _b.GetLoc());
								toHire = _a;
							} else if (sep < min_sep_employed && _a.GetJob() != ActorJob.Builder) {//find backup builder
								min_sep_employed = sep;
								toHireEmployed = _a;
							}
						}
					}
					if (toHire != null) {
						if (_b.GetBeingBuilt() == true) {
							toHire.SetJob(ActorJob.Builder, _b);
							//System.out.println("NEW JOB "+toHire.GetOwner()+" "+toHire.GetType()+" is going to BUILD for "+_b.GetType());
						} else {
							//System.out.println("NEW JOB "+toHire.GetOwner()+" "+toHire.GetType()+" is going to GATHER for "+_b.GetType());
							toHire.SetJob(ActorJob.Gather, _b);
						}
					} else if (toHireEmployed != null && _b.GetBeingBuilt() == true) {
						toHireEmployed.SetJob(ActorJob.Builder, _b);
						System.out.println("NEW JOB "+toHireEmployed.GetOwner()+" "+toHireEmployed.GetType()+" is going to --EHMERGEHNCY-- BUILD for "+_b.GetType());
					}
				} else { //ATTRACTOR TYPE
					if (_b.GetEmployees() >= resource_manager.GetActorsPerAttractor(_b.GetOwner(), _b.GetType())) {
						continue;
					}
					synchronized (GetActorObjects()) {
						for (final Actor _a : GetActorObjects()) {
							if (_a.GetOwner() != _b.GetOwner()) {
								continue;
							}
							if (_a.GetLastEmployer().contains(_b) == true) {
								continue; //Check was not recent employee
							}
							if (_a.GetJob() != ActorJob.Idle) {
								continue; //Check has no job
							}
							if (_b.GetAttracts().contains( _a.GetType() ) == false) {
								continue; //Actor not attracted
							}
							if (utility.Seperation(_a.GetLoc(), _b.GetLoc()) < min_sep ) { //Is closest?
								min_sep = utility.Seperation(_a.GetLoc(), _b.GetLoc());
								toHire = _a;
							}
						}
					}
					if (toHire != null) {
						toHire.SetJob(ActorJob.Guard, _b);
						//System.out.println("NEW JOB "+toHire.GetOwner()+" "+toHire.GetType()+" is going to GUARD "+_b.GetType());
					}
				}
			}
		}
	}

	public void CheckBuildingExplode(final WorldPoint location, final ObjectOwner _oo) {
		PlaceExplosion(location.getX(), location.getY(), _oo);
		synchronized (GetActorObjects()) {
			for (final Actor _a : GetActorObjects()) {
				if (_a.GetOwner() == _oo && utility.Seperation(location, _a.GetLoc()) < utility.building_Explode_radius) {
					_a.Shrapnel();
				}
			}
		}
		synchronized (GetBuildingOjects()) {
			for (final Building _b : GetBuildingOjects()) {
				if (_b.GetOwner() == _oo && utility.Seperation(location, _b.GetLoc()) < utility.building_Explode_radius) {
					_b.Shrapnel();
				}
			}
		}
	}

	public void CheckPoison(final WorldPoint location, final ObjectOwner _target) {
		synchronized (GetActorObjects()) {
			for (final Actor _a : GetActorObjects()) {
				if (_a.GetOwner() == _target && utility.Seperation(location, _a.GetLoc()) < utility.actor_poison_range) {
					_a.Poison();
					resource_manager.AddStatistic(GameStatistics.UnitsPoisoned);
				}
			}
		}
	}

	public boolean CheckSafe(final boolean RESOURCEBUILD, final boolean ACTOR, final int _x, final int _y, final float _r, final int _ID1_to_ignore_collision_of, final int _ID2_to_ignore_collision_of) {
		final WorldPoint _me = new WorldPoint(_x,_y);
		final WeightedPoint _loc = ClipToGrid( _me );
		if (_loc == null) return false;

		if (RESOURCEBUILD == true) {
			for (final Sprite _s : _loc.GetOwnedSprites()) {
				if (utility.Seperation(_me, _s.GetLoc()) < _r + _s.GetR() ) return false;
			}
			for (final WeightedPoint _p : _loc.GetNieghbours()) {
				for (final Sprite _s : _p.GetOwnedSprites()) {
					if (utility.Seperation(_me, _s.GetLoc()) < _r + _s.GetR() ) return false;
				}
			}

		}
		if (ACTOR == true) {
			synchronized (GetActorObjects()) {
				for (final Actor _a : GetActorObjects()) {
					if (_ID1_to_ignore_collision_of == _a.GetID()) {
						continue;
					}
					if (_ID2_to_ignore_collision_of == _a.GetID()) {
						continue;
					}
					final int _x_d = _a.GetX() - _x;
					final int _y_d = _a.GetY() - _y;
					final float sep = (float) Math.sqrt( (_x_d * _x_d) + (_y_d * _y_d) );
					final float combRad = _r + _a.GetR();
					if (sep < combRad) return false;
				}
			}
		}
		return true;
	}

	private void CheckSpecialSpawn() {

		//See if we can spawn a special unit type
		//spock = paper+scissors, lizard = paper+rock
		synchronized (GetBuildingOjects()) {
			for (final Building _b1 : GetBuildingOjects()) {
				if (_b1.GetType() == BuildingType.AttractorPaper) {
					for (final Building _b2 : GetBuildingOjects()) {
						if (_b2.GetID() == _b1.GetID())	continue;
						for (int i=0; i<2; ++i) {
							final ArrayList<Actor> l1 = new ArrayList<Actor>();
							final ArrayList<Actor> l2 = new ArrayList<Actor>();
							final ActorType l1_type = ActorType.Paper;
							ActorType l2_type = null;
							BuildingType b2_type = null;
							ActorType toSpawn = null;
							final int min_l1 = utility.EXTRA_Paper_PerWoodmill;
							int min_l2 = 0;
							if (i==0) {	//------------DO LIZARD------------
								toSpawn = ActorType.Lizard;
								l2_type = ActorType.Rock;
								min_l2 = utility.EXTRA_Rock_PerRockery;
								b2_type = BuildingType.AttractorRock;
							} else if (i==1) { //------------DO SPOCK------------
								toSpawn = ActorType.Spock;
								l2_type = ActorType.Scissors;
								min_l2 = utility.EXTRA_Scissors_PerSmelter;
								b2_type = BuildingType.AttractorScissors;
							}

							if (_b2.GetType() != b2_type) continue;
							if (_b2.GetOwner() != _b1.GetOwner()) continue;
							if (utility.Seperation(_b1.GetLoc(), _b2.GetLoc()) > utility.wander_radius * 2) continue;
							//seperation criteria met, are there enough units around?
							synchronized (GetActorObjects()) {
								for (final Actor _a : GetActorObjects()) {
									if (_a.GetOwner() != _b1.GetOwner()) continue;
									if (markedForDeath.contains(_a.GetID()) == true) continue;
									if (utility.Seperation(_a.GetLoc(), _b2.GetLoc()) > utility.wander_radius * 1.5f
												&& utility.Seperation(_a.GetLoc(), _b1.GetLoc()) > utility.wander_radius * 1.5f) continue;
									if (_a.GetType() == l1_type && l1.size() < min_l1) {
										l1.add(_a);
									} else if (_a.GetType() == l2_type && l2.size() < min_l2) {
										l2.add(_a);
									}
								}
							}
							if (l1.size() == min_l1 && l2.size() == min_l2) {
								//We're good for a new special!
								final WorldPoint location = FindGoodSpot(
										new WorldPoint((int) (_b1.GetX() + (_b2.GetX()-_b1.GetX())/2f), (int) (_b1.GetY() + (_b2.GetY()-_b1.GetY())/2f)),
										utility.actorRadius * 2,
										utility.wander_radius * 2,
										false);
								if (location != null) {
									PlaceSpecialSpawn(location.getX(), location.getY(), toSpawn, _b1.GetOwner(), l1, l2);
								}
							}
						}
					}
				}
			}
		}
	}

	public WeightedPoint ClipToGrid(final WorldPoint _P) {
		int _snap_x = _P.getX();
		int _snap_y = _P.getY();

		_snap_x = _snap_x - (_snap_x % theWorld.tiles_size) + (theWorld.tiles_size/2);
		_snap_y = _snap_y - (_snap_y % theWorld.tiles_size) + (theWorld.tiles_size/2);

		final WeightedPoint snap = thePathfinderGrid.point_collection_map.get(new WorldPoint(_snap_x,_snap_y));

		if (snap != null) {
			return snap;
		}
		return null;
	}

	public WorldPoint FindGoodSpot(final WorldPoint location, final int _r, final int _search_size, final boolean is_resource) {
		//is_resource == true prevents function returning true in vicinity of home base
		int loop = 0;
		while (++loop < _search_size) {
			final int _x = Math.round(location.getX() + (utility.rndG(0f,loop)));
			final int _y = Math.round(location.getY() + (utility.rndG(0f,loop)));
			if (CheckSafe(true, true, _x, _y, _r, 0, 0)) {
				if (is_resource) {
					if (utility.Seperation(_x, player_base.GetX(), _y, player_base.GetY() ) < utility.resources_kept_away_from_base) {
						continue;
					}
					if (utility.Seperation(_x, enemy_base.GetX(), _y, enemy_base.GetY() ) < utility.resources_kept_away_from_base) {
						continue;
					}
				}
				return new WorldPoint(_x,_y);
			}
		}
		return null;
	}

	public WorldPoint FindSpotForResource(final WorldPoint _loc) {
		return FindGoodSpot(_loc, utility.resourceRadius, utility.tiles_size, true);
	}


	public void Garbage() {
		final Vector<Sprite> toKill = new Vector<Sprite>();
		synchronized (GetResourceObjects()) {
			for (final Resource _r : GetResourceObjects()) {
				if (_r.GetDead() == true) {
					toKill.add(_r);
				}
			}
			GetResourceObjects().removeAll(toKill);
			synchronized (GetCollisionObjects()) {
				GetCollisionObjects().removeAll(toKill);
			}
		}
		toKill.clear();
		synchronized (GetActorObjects()) {
			for (final Actor _a : GetActorObjects()) {
				if (_a.GetDead() == true) {
					toKill.add(_a);
				}
			}
			GetActorObjects().removeAll(toKill);
		}
		toKill.clear();
		synchronized (GetBuildingOjects()) {
			for (final Building _b : GetBuildingOjects()) {
				if (_b.GetDead() == true) {
					toKill.add(_b);
				}
			}
			GetBuildingOjects().removeAll(toKill);
			synchronized (GetCollisionObjects()) {
				GetCollisionObjects().removeAll(toKill);
			}
		}
		toKill.clear();
		synchronized (GetProjectileObjects()) {
			for (final Projectile _p : GetProjectileObjects()) {
				if (_p.GetDead() == true) {
					toKill.add(_p);
				}
			}
			GetProjectileObjects().removeAll(toKill);
		}
		toKill.clear();
		synchronized (GetSpoogiclesObjects()) {
			for (final Spoogicles _s : GetSpoogiclesObjects()){
				if (_s.GetDead() == true) {
					toKill.add(_s);
				}
			}
			GetSpoogiclesObjects().removeAll(toKill);
		}
		toKill.clear();
		synchronized (GetWaterfallSplashObjects()) {
			for (final WaterfallSplash _w : GetWaterfallSplashObjects()) {
				if (_w.GetDead() == true) {
					toKill.add(_w);
				}
			}
			GetWaterfallSplashObjects().removeAll(toKill);
		}
		toKill.clear();
		synchronized (GetSpecialSpawnObjects()) {
			for (final SpecialSpawn _s : GetSpecialSpawnObjects()) {
				if (_s.GetDead() == true) {
					toKill.add(_s);
				}
			}
			GetSpecialSpawnObjects().removeAll(toKill);
		}
		toKill.clear();
		synchronized (GetExplosionObjects()) {
			for (final Explosion _e : GetExplosionObjects()) {
				if (_e.GetDead() == true) {
					toKill.add(_e);
				}
			}
			GetExplosionObjects().removeAll(toKill);
		}
		toKill.clear();
	}

	public Collection<Actor> GetActorObjects() {
		return ActorObjectsSync;
	}

	public Building GetBase(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Player) return player_base;
		return enemy_base;
	}

	public Building GetBuildingAtMouse(final int _x, final int _y) {
		final WorldPoint _mouse = new WorldPoint(_x, _y);
		synchronized (GetBuildingOjects()) {
			for (final Building _b : GetBuildingOjects()) {
				if (utility.Seperation(_b.GetLoc(), _mouse) < utility.buildingRadius) return _b;
			}
		}
		return null;
	}

	public Collection<Building> GetBuildingOjects() {
		return BuildingOjectsSync;
	}

	public Collection<Sprite> GetCollisionObjects() {
		return CollisionObjectsSync;
	}

	public Collection<Explosion> GetExplosionObjects() {
		return ExplosionObjectsSync;
	}

	public Resource GetNearestResource(final Building _b, final Actor _a, final int maxDistance) {
		float _dist = utility.minimiser_start;
		Resource r = null;
		synchronized (GetResourceObjects()) {
			for (final Resource _r : GetResourceObjects()) {
				if (_b.GetCollects().contains(_r.GetType())  //Boss accepts
						&& _r.GetReachable() == true //Not currently `unreachable'
						&& _a.GetCollects().contains(_r.GetType()) //Client can gather
						&& utility.Seperation(_r.GetLoc(), _b.GetLoc()) < _dist //Is nearer
						&& _r.GetRemaining() > 0 //Is not depleted
						&& (r == null || utility.rnd() < 0.5f)) { //50% random chance to change (if not first node found)
					_dist = utility.Seperation(_r.GetLoc(), _b.GetLoc());
					r = _r;
				}
			}
		}
		if (_dist < maxDistance) return r;
		else return null;
	}

	public Collection<Projectile> GetProjectileObjects() {
		return ProjectileObjectsSync;
	}

	public Collection<Resource> GetResourceObjects() {
		return ResourceObjectsSync;
	}

	public Collection<SpecialSpawn> GetSpecialSpawnObjects() {
		return SpecialSpawnObjectsSync;
	}

	public Collection<Spoogicles> GetSpoogiclesObjects() {
		return SpoogicleObjectsSync;
	}

	public TreeSet<Sprite> GetSpritesZOrdered() {
		final TreeSet<Sprite> ZOrder = new TreeSet<Sprite>();
		synchronized (GetActorObjects()) {
			for (final Sprite _s : GetActorObjects()) {
				ZOrder.add(_s);
			}
		}
		synchronized (GetBuildingOjects()) {
			for (final Sprite _s : GetBuildingOjects()) {
				ZOrder.add(_s);
			}
		}
		synchronized (GetResourceObjects()) {
			for (final Sprite _s : GetResourceObjects()) {
				ZOrder.add(_s);
			}
		}
		synchronized (GetProjectileObjects()) {
			for (final Sprite _s : GetProjectileObjects()) {
				ZOrder.add(_s);
			}
		}
		synchronized (GetSpoogiclesObjects()) {
			for (final Sprite _s : GetSpoogiclesObjects()) {
				ZOrder.add(_s);
			}
		}
		synchronized (GetSpecialSpawnObjects()) {
			for (final Sprite _s : GetSpecialSpawnObjects()) {
				ZOrder.add(_s);
			}
		}
		synchronized (GetExplosionObjects()) {
			for (final Sprite _e : GetExplosionObjects()) {
				ZOrder.add(_e);
			}
		}
		return ZOrder;
	}

	public HashSet<Resource> GetTempResourceObjects() {
		return TempResourceHolder;
	}

	public Collection<WaterfallSplash> GetWaterfallSplashObjects() {
		return WaterfallSplahsObjectsSync;
	}

	public boolean GetWorldSeeded() {
		return worldSeeded;
	}

	public Actor PlaceActor(final WorldPoint _p, final ActorType _at, final ObjectOwner _o) {
		//NO rechecks that coordinates are safe - be warned!
		final Actor newActor = PlatFormSpecific_PlaceActor(_p, _at, _o);
		if (_at == ActorType.Rock) resource_manager.AddStatistic(GameStatistics.RocksAssembled);
		synchronized (GetActorObjects()) {
			GetActorObjects().add(newActor);
		}
		return newActor;
	}

	public Building PlaceBuilding(final WorldPoint _p, final BuildingType _bt, final ObjectOwner _oo) {
		//Does NOT recheck that coordinates are safe - be warned!
		int _r = utility.buildingRadius;
		if (_bt == BuildingType.AttractorPaper
				|| _bt == BuildingType.AttractorRock
				|| _bt == BuildingType.AttractorScissors) {
			_r = utility.attractorRadius;
		}
		resource_manager.AddStatistic(GameStatistics.BuildingsConstructed);
		resource_manager.Buy(_bt, _oo);
		final Building newBuilding = PlatformSpecific_PlaceBuilding(_p, _r, _bt, _oo);
		synchronized (GetBuildingOjects()) {
			GetBuildingOjects().add(newBuilding);

		}
		synchronized (GetCollisionObjects()) {
			GetCollisionObjects().add(newBuilding);
		}
		return newBuilding;
	}

	public Explosion PlaceExplosion(final int _x, final int _y, final ObjectOwner _oo) {
		resource_manager.AddStatistic(GameStatistics.BuildingsExploded);
		final Explosion newE = PlatformSpecific_Explosion(_x, _y, _oo);
		synchronized (GetSpecialSpawnObjects()) {
			GetExplosionObjects().add(newE);
		}
		return newE;
	}

	public void PlaceProjectile(final Actor _source, final Sprite _target) {
		int r = utility.projectileRadius;
		if (_source.GetType() == ActorType.Lizard) {
			++r;
		}
		final Projectile newProjectile = PlatformSpecific_PlaceProjectile(_source, _target, r);
		resource_manager.AddStatistic(GameStatistics.ProjectilesFired);
		synchronized (GetProjectileObjects()) {
			GetProjectileObjects().add(newProjectile);
		}
	}

	public Resource PlaceResource(final WorldPoint _p, final ResourceType _rt, final boolean AddToTempList) {
		//NO rechecks that coordinates are safe - be warned!
		final Resource newResource = PlatformSpecific_PlaceResource(_p, _rt);
		if (AddToTempList == false) {
			synchronized (GetResourceObjects()) {
				GetResourceObjects().add(newResource);
			}
			synchronized (GetCollisionObjects()) {
				GetCollisionObjects().add(newResource);
			}
		} else { //resource can't be allowed to corrupt it's own list while it's ticking
			GetTempResourceObjects().add(newResource);
		}
		return newResource;
	}
	public SpecialSpawn PlaceSpecialSpawn(final int _x, final int _y, final ActorType _at, final ObjectOwner _oo, final ArrayList<Actor> _l1, final ArrayList<Actor> _l2) {
		final SpecialSpawn newSS = PlatformSpecific_SpecialSpawn(_x, _y, utility.specialSpawnRadius, _at, _oo);
		resource_manager.AddStatistic(GameStatistics.SpecialUnitsSpawned);
		synchronized (GetSpecialSpawnObjects()) {
			GetSpecialSpawnObjects().add(newSS);
			newSS.addToMurderList(_l1);
			newSS.addToMurderList(_l2);
		}
		if (_l1 != null) {
			for (final Actor _a : _l1) {
				markedForDeath.add(_a.GetID());
			}
		}
		if (_l2 != null) {
			for (final Actor _a : _l2) {
				markedForDeath.add(_a.GetID());
			}
		}
		return newSS;
	}
	public void PlaceSpooge(final int _x, final int _y, final ObjectOwner _oo, final int _n, final float _scale) {
		final Spoogicles newSpoogicles = PlatformSpecific_PlaceSpooge(_x, _y, _oo, _n, _scale);
		synchronized (GetSpoogiclesObjects()) {
			GetSpoogiclesObjects().add(newSpoogicles);
		}
	}
	public void PlaceWaterfallSplash(final int _x, final int _y, final int _r) {
		final WaterfallSplash newWFS = PlatformSpecific_PlaceWaterfallSplash(_x, _y, _r);
		synchronized (GetWaterfallSplashObjects()) {
			GetWaterfallSplashObjects().add(newWFS);
		}
	}
	protected Explosion PlatformSpecific_Explosion(final int _x, final int _y, final ObjectOwner _oo) { return null; }
	//These methods are overridden by Applet or Android sub-instances of SpriteManager
	protected Actor PlatFormSpecific_PlaceActor(final WorldPoint _p, final ActorType _at, final ObjectOwner _o) { return null; }
	protected Building PlatformSpecific_PlaceBuilding(final WorldPoint _p, final int _r, final BuildingType _bt, final ObjectOwner _oo) { return null; }
	protected Projectile PlatformSpecific_PlaceProjectile(final Actor _source, final Sprite _target, final int _r) { return null; }

	protected Resource PlatformSpecific_PlaceResource(final WorldPoint _p, final ResourceType _rt) { return null; }

	protected Spoogicles PlatformSpecific_PlaceSpooge(final int _x, final int _y, final ObjectOwner _oo, final int _n, final float _scale) { return null; }





	protected WaterfallSplash PlatformSpecific_PlaceWaterfallSplash(final int _x, final int _y, final int _r) { return null; }

	protected SpecialSpawn PlatformSpecific_SpecialSpawn(final int _x, final int _y, final int _r, final ActorType _at, final ObjectOwner _oo) { return null; }

	public void Reset() {
		ws_step = 0;
		player_base = null;
		enemy_base = null;
		worldSeeded = false;
		synchronized (GetCollisionObjects()) {
			GetCollisionObjects().clear();
		}
		synchronized (GetActorObjects()) {
			GetActorObjects().clear();
		}
		synchronized (GetBuildingOjects()) {
			GetBuildingOjects().clear();
		}
		synchronized (GetResourceObjects()) {
			GetResourceObjects().clear();
		}
		synchronized (GetProjectileObjects()) {
			GetProjectileObjects().clear();
		}
		synchronized (GetSpoogiclesObjects()) {
			GetSpoogiclesObjects().clear();
		}
		synchronized (GetWaterfallSplashObjects()) {
			GetWaterfallSplashObjects().clear();
		}
		synchronized (GetSpecialSpawnObjects()) {
			GetSpecialSpawnObjects().clear();
		}
		synchronized (GetExplosionObjects()) {
			GetExplosionObjects().clear();
		}
		//now _this_ singleton is fully spawned. Is safe to fetch the resourceManager (inter-depencence)
		 if (resource_manager == null) {
			 resource_manager = ResourceManager.GetResourceManager();
		 }
		resource_manager.Reset();
		//Start the AI
		theAI = new AI(ObjectOwner.Enemy); //AI playing as the bad guys (blue)
		AI_thread = new Thread(theAI);
		theHumanAI = new AI(ObjectOwner.Player);  //AI playing as the player (red)
		AIHuman_thread = new Thread(theHumanAI);

	}

	public int SeedWorld() {
		final WorldPoint player_starting = theWorld.GetIdeadStartingLocation(ObjectOwner.Player);
		final WorldPoint enemy_starting = theWorld.GetIdeadStartingLocation(ObjectOwner.Enemy);
		final float timeNow = (System.nanoTime() / 1000000000f);
		float time_to_wait = utility.wg_seconds_to_wait;
		if (utility.dbg == true || utility.fastForward == true) {
			time_to_wait = 0f;
		}

		if (utility.gamePaused == true) {
			return ws_step;
		}

		if (ws_step == 0 && (timeNow-ws_time_of_last_operation) > time_to_wait) {
			++ws_step;
			ws_time_of_last_operation = (System.nanoTime() / 1000000000f);
			System.out.println("STATE: START SEED " + ws_step + " RND_C:" + utility.rnd_count);

			//Setup the pathfinding grid
			System.out.println("CONSTRUCT GRID");
			thePathfinderGrid.Init();
			System.out.println("DONE CONSTRUCT GRID");

			final WorldPoint player_location = FindGoodSpot(player_starting, utility.buildingRadius, utility.look_for_spot_radius, false);
			final WorldPoint enemy_location = FindGoodSpot(enemy_starting, utility.buildingRadius, utility.look_for_spot_radius, false);

			if (player_location != null) {
				player_base = PlaceBuilding(player_location, BuildingType.Base, ObjectOwner.Player);
			} else {
				System.out.println("---- !!!! ---- PLAYER BASE LOCATION NOT FOUND");
				return -1;
			}
			if (enemy_location != null) {
				enemy_base = PlaceBuilding(enemy_location, BuildingType.Base, ObjectOwner.Enemy);
			} else {
				System.out.println("---- !!!! ---- ENEMY BASE LOCATION NOT FOUND");
				return -1;
			}

			//get passable route?
			pathfinder = new Pathfinder(player_base, enemy_base);
			pathfinding_thread = new Thread(pathfinder);
			pathfinding_thread.start();
		}

		if (ws_step == 1) {
			if (pathfinding_thread.isAlive() == true) return ws_step;
			else {
				final ArrayList<WorldPoint> waypoint_list = pathfinder.GetResult();
				if (waypoint_list == null) {
					System.out.println("---- !!!! ---- BASES NON-NAVAGABLE");
					return -1; //FAILED
				} else {
					++ws_step;
				}
			}
		}

		if (ws_step == 2 && (timeNow-ws_time_of_last_operation) > time_to_wait) {
			System.out.println("STATE: RESOURCE " + ws_step + " RND_C:" + utility.rnd_count);
			++ws_step;
			ws_time_of_last_operation = (System.nanoTime() / 1000000000f);
			//initial plant!
			final HashSet<WorldTile> render_tiles = theWorld.GetRenderTiles();
			for (final WorldTile _t : render_tiles) {
				ResourceType toPlant = null;
				if (_t.GetBiomeType() == BiomeType.FORREST) {
					if (resource_manager.GetGlobalIron() > resource_manager.GetGlobalWood()) {
						toPlant = ResourceType.Tree;
					} else {
						toPlant = ResourceType.Mine;
					}
				} else if (_t.GetBiomeType() == BiomeType.DESERT) {
					if (resource_manager.GetGlobalStone() > resource_manager.GetGlobalWood()) {
						toPlant = ResourceType.Cactus;
					} else {
						toPlant = ResourceType.Rockpile;
					}
				} else if (_t.GetBiomeType() == BiomeType.GRASS) {
					if (resource_manager.GetGlobalStone() > resource_manager.GetGlobalIron()) {
						toPlant = ResourceType.Mine;
					} else {
						toPlant = ResourceType.Rockpile;
					}
				}
				float resDensity = _t.GetOwner().GetResourceDensity();
				if (toPlant == ResourceType.Rockpile || toPlant == ResourceType.Mine) resDensity /= utility.place_res_gaussian; //Trees only come in one
				if (utility.rnd() < resDensity) {
					final WorldPoint look_around = new WorldPoint(_t.GetX() + utility.rndI(utility.tiles_size), _t.GetY() + utility.rndI(utility.tiles_size));
					WorldPoint ideal_resource_loation = FindSpotForResource(look_around);
					if (ideal_resource_loation != null) {
						//Try placing resources around a gaussian
						int toPlace = (int) (utility.rndG(utility.place_res_gaussian, utility.place_res_gaussian));
						if (utility.rnd() < 0.75f && (toPlant == ResourceType.Cactus || toPlant == ResourceType.Tree)) toPlace = 1; //Trees only come in one
						for (int place = 0; place < toPlace; ++place) {
							if (ideal_resource_loation != null) PlaceResource(ideal_resource_loation, toPlant, false);
							ideal_resource_loation = FindGoodSpot(look_around, utility.resourceRadius, utility.tiles_size*5, true); //get new location nearby
						}
					}
				}
			}
			//Unify resources
			final float avRes = (resource_manager.GetGlobalWood()+resource_manager.GetGlobalIron()+resource_manager.GetGlobalStone())/3f;
			final float resMult = utility.resource_desired_global / avRes;
			synchronized (GetResourceObjects()) {
				for (final Resource _r : GetResourceObjects()) {
					final int ToAdd = (int) (_r.GetRemaining() * (resMult - 1.));
					_r.Add( ToAdd );
				}
			}
			System.out.println("THERE IS IN THE WORLD: "+resource_manager.GetGlobalWood()+" WOOD, "+resource_manager.GetGlobalIron()+" IRON AND "+resource_manager.GetGlobalStone()+" STONE");
		}

		if (ws_step == 3 && (timeNow-ws_time_of_last_operation) > time_to_wait) {
			++ws_step;
			ws_time_of_last_operation = (System.nanoTime() / 1000000000f);
			WorldPoint starting_troops;
			for (int owner = 0; owner < 2; ++owner) {
				WorldPoint starting;
				ObjectOwner oo;
				if (owner == 0) {
					starting = player_starting;
					oo = ObjectOwner.Player;
				} else {
					starting = enemy_starting;
					oo = ObjectOwner.Enemy;
				}
				for (int i=0; i < utility.starting_actors; ++i) {
					starting_troops = FindGoodSpot(starting, utility.actorRadius, utility.look_for_spot_radius, false);
					if (starting_troops != null) {
						PlaceActor(starting_troops, ActorType.Scissors, oo);
					} else {
						System.out.println("---- !!!! ---- CANNOT PLACE STARTING SCISSOR ACTOR");
						return -1;
					}
					starting_troops = FindGoodSpot(starting, utility.actorRadius, utility.look_for_spot_radius, false);
					if (starting_troops != null) {
						PlaceActor(starting_troops, ActorType.Rock, oo);
					} else {
						System.out.println("---- !!!! ---- CANNOT PLACE STARTING ROCK ACTOR");
						return -1;
					}
					starting_troops = FindGoodSpot(starting, utility.actorRadius, utility.look_for_spot_radius, false);
					if (starting_troops != null) {
						PlaceActor(starting_troops, ActorType.Paper, oo);
					} else {
						System.out.println("---- !!!! ---- CANNOT PLACE STARTING PAPER ACTOR");
						return -1;
					}
				}
			}
		}

		if (ws_step == 4 && (timeNow-ws_time_of_last_operation) > time_to_wait) {
			++ws_step;
			ws_time_of_last_operation = (System.nanoTime() / 10000000000f);
			worldSeeded = true;
			System.out.println("STATE: MAGIC FINAL NUMBER AT SPRITE STATE " + ws_step + " RND_C:" + utility.rnd_count);
		}

		return ws_step;
	}

	public void Tick() {

		++TickCount;
		if (TickCount % utility.ticks_per_tock/2 == 0) Tock();
		synchronized (GetActorObjects()) {
			for (final Actor _a : GetActorObjects()) {
				_a.Tick(TickCount);
			}
		}
		synchronized (GetProjectileObjects()) {
			for (final Projectile _p : GetProjectileObjects()) {
				_p.Tick(TickCount);
			}
		}
		synchronized (GetResourceObjects()) {
			for (final Resource _r : GetResourceObjects()) {
				_r.Tick(TickCount);
			}
		}
		synchronized (GetBuildingOjects()) {
			for (final Building _b : GetBuildingOjects()) {
				_b.Tick(TickCount);
			}
		}
		synchronized (GetSpoogiclesObjects()) {
			for (final Spoogicles _s : GetSpoogiclesObjects()) {
				_s.Tick(TickCount);
			}
		}
		synchronized (GetWaterfallSplashObjects()) {
			for (final WaterfallSplash _w : GetWaterfallSplashObjects()) {
				_w.Tick(TickCount);
			}
		}
		synchronized (GetSpecialSpawnObjects()) {
			for (final SpecialSpawn _s : GetSpecialSpawnObjects()) {
				_s.Tick(TickCount);
			}
		}
		synchronized (GetExplosionObjects()) {
			for (final Explosion _e : GetExplosionObjects()) {
				_e.Tick(TickCount);
			}
		}

		//any resources added? now we're clear of the tick loop we can add these to the list
		if (GetTempResourceObjects().size() > 0) {
			synchronized (GetResourceObjects()) {
				GetResourceObjects().addAll(TempResourceHolder);
			}
			synchronized (GetCollisionObjects()) {
				GetCollisionObjects().addAll(TempResourceHolder);
			}
			TempResourceHolder.clear();
		}

		//check if we need more splashes
		if (GetWaterfallSplashObjects().size() < utility.waterfall_splashes && utility.rnd() < utility.waterfall_fall_rate) {
			final float angle = (float) (utility.rnd() * Math.PI * 2);
			final int _x = (int) (utility.world_size * utility.waterfall_disk_size * Math.cos(angle));
			final int _y = (int) (utility.world_size * utility.waterfall_disk_size * Math.sin(angle));
			PlaceWaterfallSplash(_x, _y, utility.waterfall_splash_radius);
		}
	}

	public void Tock() { //or, the 'long tick'
		Garbage();

		CheckActorCombat(); //FIGHT!

		CheckBuildingEmployment();

		CheckSpecialSpawn();

		if (AI_thread.isAlive() == false) AI_thread.run();
		if (utility.noPlayers == true && AIHuman_thread.isAlive() == false) AIHuman_thread.run();
	}
}
