package com.timboe.rpsrts.managers;

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
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.enumerators.ResourceType;
import com.timboe.rpsrts.sprites.Actor;
import com.timboe.rpsrts.sprites.Building;
import com.timboe.rpsrts.sprites.Projectile;
import com.timboe.rpsrts.sprites.Resource;
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

	public int GlobalSpriteCounter;

	public Building player_base;
	public Building enemy_base;

	private final HashSet<Sprite> CollisionObjects = new HashSet<Sprite>();
	private final HashSet<Actor> ActorObjects = new HashSet<Actor>();
	private final HashSet<Building> BuildingOjects = new HashSet<Building>();
	private final HashSet<Resource> ResourceObjects = new HashSet<Resource>();
	private final HashSet<Projectile> ProjectileObjects = new HashSet<Projectile>();
	private final HashSet<Spoogicles> SpoogicleObjects = new HashSet<Spoogicles>();
	private final HashSet<WaterfallSplash> WaterfallSplahsObjects = new HashSet<WaterfallSplash>();
	// Synchronised
	private final Collection<Sprite> CollisionObjectsSync = Collections.synchronizedCollection(CollisionObjects);
	private final Collection<Actor> ActorObjectsSync = Collections.synchronizedCollection(ActorObjects);
	private final Collection<Building> BuildingOjectsSync = Collections.synchronizedCollection(BuildingOjects);
	private final Collection<Resource> ResourceObjectsSync = Collections.synchronizedCollection(ResourceObjects);
	private final Collection<Projectile> ProjectileObjectsSync = Collections.synchronizedCollection(ProjectileObjects);
	private final Collection<Spoogicles> SpoogicleObjectsSync = Collections.synchronizedCollection(SpoogicleObjects);
	private final Collection<WaterfallSplash> WaterfallSplahsObjectsSync = Collections.synchronizedCollection(WaterfallSplahsObjects);

	//
	protected final HashSet<Resource> TempResourceHolder = new HashSet<Resource>();

	protected SpriteManager() {
	}

	private void CheckActorCombat(){
		synchronized (GetActorObjects()) {
			for (final Actor _a : GetActorObjects()) {
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
	
	public boolean CheckSafe(boolean RESOURCEBUILD, boolean ACTOR,
			int _x, int _y, float _r, int _ID1_to_ignore_collision_of, int _ID2_to_ignore_collision_of) { //TODO this is now double _r
		//NO LONGER NEEDED - CANNOT JUMP TO INVALID NODE
//		if (TILE == true && theWorld.CheckSafeToPlaceTile(_x, _y, (int)Math.round(_r)) == false) {
//			//System.out.println("Tile is bad, place/move DENIED");
//			return false;
//		}
		WorldPoint _me = new WorldPoint(_x,_y);
		WeightedPoint _loc = ClipToGrid( _me );
		if (_loc == null) return false;

		if (RESOURCEBUILD == true) {
			for (Sprite _s : _loc.GetOwnedSprites()) {
				if (utility.Seperation(_me, _s.GetLoc()) < _r + _s.GetR() ) return false;
			}
			for (WeightedPoint _p : _loc.GetNieghbours()) {
				for (Sprite _s : _p.GetOwnedSprites()) {
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
		//System.out.println("Place/Move ALLOWED");
		return true;
	}

	public WeightedPoint ClipToGrid(WorldPoint _P) {
		int _snap_x = _P.getX();
		int _snap_y = _P.getY();

		_snap_x = _snap_x - (_snap_x % theWorld.tiles_size) + (theWorld.tiles_size/2);
		_snap_y = _snap_y - (_snap_y % theWorld.tiles_size) + (theWorld.tiles_size/2);
		
		//System.out.println("SNAP ("+_snap_x+","+_snap_y+")");
		WeightedPoint snap = thePathfinderGrid.point_collection_map.get(new WorldPoint(_snap_x,_snap_y));
		
		if (snap != null) { 
			//System.out.println("SNAPPED AT i ("+i+")");
			return snap;
		}
		return null;
	}

	//TODO CHECK THAT CHANGING THIS TO INCLUDE ACTORS IN THE CHECK WAS A GOOD IDEA (has overhead)
	public WorldPoint FindGoodSpot(WorldPoint location, int _r, int _search_size, boolean is_resource) {
		//is_resource == true prevents function returning true in vicinity of home base
		int loop = 0;
		while (++loop < _search_size) {
			final int _x = (int) Math.round(location.getX() + (utility.rndG(0f,loop)));
			final int _y = (int) Math.round(location.getY() + (utility.rndG(0f,loop)));
			if (CheckSafe(true, true, _x, _y, _r, 0, 0)) {
				if (is_resource) {
					if (utility.Seperation(_x, player_base.GetX(), _y, player_base.GetY() ) < utility.resources_kept_away_from_base) {
						continue;
					}
					if (utility.Seperation(_x, enemy_base.GetX(), _y, enemy_base.GetY() ) < utility.resources_kept_away_from_base) {
						continue;
					}
				}
				//System.out.println("PLACE OBJECT AT X:"+_x+" Y:"+_y);
				return new WorldPoint(_x,_y);
			} //else System.out.println("FAILED TO PLACED oBJECT AT X:"+_x+" Y:"+_y);
		}
		return null;
	}
	
//	public boolean CheckSafeIncludingActors(boolean DELETEME, int _x, int _y, float _r, int _ID_to_ignore_collision_of) {
//		//final boolean normalSafe = CheckSafe(_x, _y, _r, _ID_to_ignore_collision_of, 0);
//		if (normalSafe == false) return false;
//		for (final Actor _a : ActorObjects) {
//			if (_ID_to_ignore_collision_of == _a.GetID()) {
//				continue;
//			}
//			final int _x_d = _a.GetX() - _x;
//			final int _y_d = _a.GetY() - _y;
//			final float sep = Math.sqrt( (_x_d * _x_d) + (_y_d * _y_d) );
//			final float combRad = _r + _a.GetR();
//			if (sep < combRad) return false;
//		}
//		return true;
//	}

	public WorldPoint FindSpotForResource(WorldPoint _loc) {
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
	}

	public Collection<Actor> GetActorObjects() {
		return ActorObjectsSync;
	}

	public Building GetBase(ObjectOwner _oo) {
		if (_oo == ObjectOwner.Player) return player_base;
		return enemy_base;
	}

	
	public Building GetBuildingAtMouse(int _x, int _y) {
		WorldPoint _mouse = new WorldPoint(_x, _y);
		synchronized (GetBuildingOjects()) {
			for (Building _b : GetBuildingOjects()) {
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
	
	public Resource GetNearestResource(Building _b, Actor _a, int maxDistance) {
		float _dist = utility.minimiser_start;
		Resource r = null;
		synchronized (GetResourceObjects()) {
			for (final Resource _r : GetResourceObjects()) {
				if (_b.GetCollects().contains(_r.GetType())  //Boss accepts
						&& _r.GetReachable() == true //Not currently `unreachable'
						&& _a.GetCollects().contains(_r.GetType()) //Client can gather
						&& utility.Seperation(_r.GetLoc(), _b.GetLoc()) < _dist //Is nearer
						&& _r.GetRemaining() > 0 //Is not depleted
						&& (r == null || utility.rnd() < 1f)) { //50% random chance to change (if not first node found) //TODO change back to 50%
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

	public Collection<Spoogicles> GetSpoogiclesObjects() {
		return SpoogicleObjectsSync;
	}

	public TreeSet<Sprite> GetSpritesZOrdered() {
		TreeSet<Sprite> ZOrder = new TreeSet<Sprite>();
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
	
	protected Actor PlaceActor(WorldPoint starting_troops, ActorType scissors, ObjectOwner oo) {
		//OVERRIDE
		return null;
	}

	public Building PlaceBuilding(WorldPoint enemy_location, BuildingType base, ObjectOwner enemy) {
		//OVERRIDE
		return null;
	}

	public void PlaceProjectile(Actor _source, Sprite _target) {
		//OVER RIDE
	}

	public Resource PlaceResource(WorldPoint ideal_resource_loation,	ResourceType toPlant, boolean AddToTempList) {
		return null;
		//OVER RIDE
	}
	
	public void PlaceSpooge(int _x, int _y, ObjectOwner _oo, int _n, float _scale) {
		//OVER RIDE
	}

	protected void PlaceWaterfallSplash(int _x, int _y, int _r) {
		//OVER RIDE
	}

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
		synchronized (GetWaterfallSplashObjects()) {
			GetWaterfallSplashObjects().clear();
		}
		//now _this_ singleton is fully spawned. Is safe to fetch the resourceManager (inter-depencence)
		 if (resource_manager == null) {
			 resource_manager = ResourceManager.GetResourceManager();
		 }
		resource_manager.Reset();
		//Start the AI
		theAI = new AI();
		AI_thread = new Thread(theAI);

	}
	
	public int SeedWorld() {
		//System.out.println("ENTER SEED");
		final WorldPoint player_starting = theWorld.GetIdeadStartingLocation(ObjectOwner.Player);
		final WorldPoint enemy_starting = theWorld.GetIdeadStartingLocation(ObjectOwner.Enemy);
		final float timeNow = (System.nanoTime() / 1000000000f);
		float time_to_wait = utility.wg_seconds_to_wait;
		if (utility.dbg == true) {
			time_to_wait = 0f;
		}

		if (ws_step == 0 && (timeNow-ws_time_of_last_operation) > time_to_wait) {
			++ws_step;
			ws_time_of_last_operation = (System.nanoTime() / 1000000000f);
			System.out.println("STATE: START SEED " + ws_step + " RND_C:" + utility.rnd_count);

			//Setup the pathfinding grid 
			System.out.println("CONSTRUCT GRID");
			thePathfinderGrid.Init();
			//thePathfinderGridThread = new Thread(thePathfinderGrid);
			//thePathfinderGridThread.start();
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
				Vector<WorldPoint> waypoint_list = pathfinder.GetResult();
				if (waypoint_list == null) {
					System.out.println("---- !!!! ---- BASES NON NAVAGABLE");
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
					if (resource_manager.GLOBAL_IRON > resource_manager.GLOBAL_WOOD) {
						toPlant = ResourceType.Tree;
					} else {
						toPlant = ResourceType.Mine;
					}
				} else if (_t.GetBiomeType() == BiomeType.DESERT) {
					if (resource_manager.GLOBAL_STONE > resource_manager.GLOBAL_WOOD) {
						toPlant = ResourceType.Cactus;
					} else {
						toPlant = ResourceType.Rockpile;
					}
				} else if (_t.GetBiomeType() == BiomeType.GRASS) {
					if (resource_manager.GLOBAL_STONE > resource_manager.GLOBAL_IRON) {
						toPlant = ResourceType.Mine;
					} else {
						toPlant = ResourceType.Rockpile;
					}
				}
				float resDensity = _t.GetOwner().GetResourceDensity();
				if (toPlant == ResourceType.Rockpile || toPlant == ResourceType.Mine) resDensity /= (float) utility.place_res_gaussian; //Trees only come in one
				if (utility.rnd() < resDensity) { //TODO check reduction factor here
					final WorldPoint look_around = new WorldPoint(_t.GetX() + utility.rndI(utility.tiles_size), _t.GetY() + utility.rndI(utility.tiles_size));
					WorldPoint ideal_resource_loation = FindSpotForResource(look_around);// FindGoodSpot(look_around, utility.resourceRadius, tile_size, true);
					if (ideal_resource_loation != null) {
						//Try placing resources around a gaussian centred on 5
						//int toPlace = (int) (utility.place_res_gaussian + (utility.rnd.nextGaussian() * utility.place_res_gaussian));
						int toPlace = (int) (utility.rndG(utility.place_res_gaussian, utility.place_res_gaussian));
						if (utility.rnd() < 0.75f && (toPlant == ResourceType.Cactus || toPlant == ResourceType.Tree)) toPlace = 1; //Trees only come in one
						for (int place = 0; place < toPlace; ++place) {
							if (ideal_resource_loation != null) PlaceResource(ideal_resource_loation, toPlant, false);
							//get new location nearby
							ideal_resource_loation = FindGoodSpot(look_around, utility.resourceRadius, utility.tiles_size*5, true);
						}
					}
				}
			}
			System.out.println("THERE IS IN THE WORLD: "+resource_manager.GLOBAL_WOOD+" WOOD, "+resource_manager.GLOBAL_IRON+" IRON AND "+resource_manager.GLOBAL_STONE+" STONE");
//			float avRes = (resource_manager.GLOBAL_WOOD+resource_manager.GLOBAL_IRON+resource_manager.GLOBAL_STONE)/3.f;
//			float resMult = utility.resource_desired_global / avRes;
//			for (Resource _r : ResourceObjects) {
//				int ToAdd = (int) (_r.GetRemaining() * (resMult - 1.));
//				//System.out.println("current:"+_r.GetRemaining()+" toAdd:"+ToAdd );
//				_r.Add( ToAdd ); //TODO broken fix me
//			}
//			System.out.println("AV Res:"+avRes+" multiplier:"+resMult);
//			System.out.println("THERE IS IN THE WORLD: "+resource_manager.GLOBAL_WOOD+" WOOD, "+resource_manager.GLOBAL_IRON+" IRON AND "+resource_manager.GLOBAL_STONE+" STONE");
//			avRes = (resource_manager.GLOBAL_WOOD+resource_manager.GLOBAL_IRON+resource_manager.GLOBAL_STONE)/3.f;
//			System.out.println("AV Res:"+avRes+" avRes Target:"+utility.resource_desired_global);
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
		
		//any resources added?
		//now we're clear of the tick loop we can add these to the list
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
			float angle = (float) (utility.rnd() * Math.PI * 2);
			int _x = (int) (utility.world_size * utility.waterfall_disk_size * Math.cos(angle));
			int _y = (int) (utility.world_size * utility.waterfall_disk_size * Math.sin(angle));
			PlaceWaterfallSplash(_x, _y, utility.waterfall_splash_radius);
		}
	}

	public void Tock() { //or, the 'long tick'
		Garbage();

		//This is where we assign tasks, take care of house keeping.
		//Does player have enough resources to make new buildings?
//		if (PLAYER_WOOD < 50000 && PLAYER_STONE < 50000 && PLAYER_IRON < 50000) {
//			//emergency! Find a unemployed person and send them collecting
//			float distance = 999999;
//			Actor a = null;
//			for (final Actor _a : ActorObjects) {
//				if (_a.GetLastEmployer() == player_base) {
//					continue;
//				}
//				if (_a.GetJob() == ActorJob.Idle && utility.Seperation(_a.GetLoc(), player_base.GetLoc()) < distance) {
//					distance = utility.Seperation(_a.GetLoc(), player_base.GetLoc());
//					a = _a;
//				}
//			}
//			if (a != null) {
//				//System.out.println("NEW JOB "+a+" is going to emergency gather for "+player_base);
//				a.SetJob(ActorJob.Gather, player_base);// Gather resources for the main base
//			}
//		}

		//FIGHT!
		CheckActorCombat();

		//
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

				//TODO collectors should build if not enough builders
				
				//building could do with new employee
				float min_sep = utility.minimiser_start;
				Actor toHire = null;
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
							if (_a.GetJob() != ActorJob.Idle) {
								continue; //Check has no job
							}
							if (_a.GetCollects().containsAll( _b.GetCollects() ) == false){
								continue; //Actor collects correct res
							}
							if (utility.Seperation(_a.GetLoc(), _b.GetLoc()) < min_sep ) { //Is closest?
								min_sep = utility.Seperation(_a.GetLoc(), _b.GetLoc());
								toHire = _a;
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
						System.out.println("NEW JOB "+toHire.GetOwner()+" "+toHire.GetType()+" is going to GUARD "+_b.GetType());
					}
				}
			}

			if (AI_thread.isAlive() == false) AI_thread.run();
		}
	}
}
