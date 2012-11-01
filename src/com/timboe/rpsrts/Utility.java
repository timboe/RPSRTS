package com.timboe.rpsrts;

import java.util.Random;

public class Utility {
	private static Utility singleton = new Utility();
	public static Utility GetUtility() {
		return singleton;
	}

	//RANDOM NUMBER SOURCE
	public String rndSeedTxt = "Dave";
	private final Random rndGen = new Random(rndSeedTxt.hashCode());
	//public final Random rnd2 = new Random(0);
	public boolean worldGenLock = false;
	public int rnd_count = 0;
	private boolean rand_off = false;
	public float rnd() {
		++rnd_count;
		if (rand_off == true) return 0.3f;
		else 
		return rndGen.nextFloat();
	}
	public int rndI(int _n) {
		++rnd_count;
		if (rand_off == true) return 1;
		else return rndGen.nextInt(_n);
	}
	public int rndI() {
		++rnd_count;
		if (rand_off == true) return 1;
		else return rndGen.nextInt();
	}
	public float rndG(float _mean, float _sigma) {
		++rnd_count;
		if (rand_off == true) return (_mean + ( _sigma * 0.3f ));
		else return (float) (_mean + ( _sigma * rndGen.nextGaussian() ));
	}
	public void setSeed() {
		rndGen.setSeed(rndSeedTxt.hashCode());
		rnd_count = 0;
	}
	
	//Global variables
	public RPSRTS _RPSRTS;
	public boolean dbg = true; //debug flag
	public float rotateAngle = 0f; //copied here as ROTATE stored in Transform(awt) or Matrix(andorid) classes
	public boolean doWorldGen = false; //proceed with building world
	public int pathfind_counter = 0; //pathfinding counter
	public GameMode gameMode = GameMode.titleScreen; //what state is game currently in
	public long loose_time; //what time was game won/lost
	public long FPS = 0;
	public int TICK;
	public boolean mouseClick = false;
	public boolean mouseDrag = false;
	public boolean sendMouseDragPing = false;


	//---------\\
	// STATICS \\
	//---------\\
	
	//World parameters
	public final int window_X = 1000;
	public final int window_Y = 600;
	public final int tiles_per_chunk = 8; //Used for coarse kT algo
	public final int world_tiles = 176; //Number of tile elements on X
	public final int tiles_size = 7; //Size of tile element in pixels
	public final int world_size = world_tiles*tiles_size;
	public final int world_size2 = world_size/2;

	
	//time settings
	public final int ticks_per_tock = 60; //one per second

	//world manager settings
	public final float wg_seconds_to_wait = 1f; //Time to wait between steps
	public final int wg_DegInCircle = 360;
	public final float island_scale = 0.75f; //Size of initial circular island w.r.t. world
	public final float wg_CrinkleScale = 30; //Multiplier to Gaussian edge smear
	public final int wg_CrinkleCoarseness = 3; //Maximum gradient to smear under
	public final float wg_kTStartPt = 1.f; //Min random chunk energy
	public final float wg_kTEndPt = 10.f; //Max random chunk energy
	public final float wg_kT_R =  14 * tiles_size; //kT algorithm R parameter  (derived)
	public final int wg_MinBiomes = 15; //Min number of generated biomes
	public final long wg_MaxBiomes = 65*100; //Max number of generated biomes
	public final float wg_MainBaseRadius = 0.8f; //How far out the main bases are placed
	public final int wg_ErrodeIterations = 4; //times to errode world
	public final int wg_EnemyBaseAngle = 135; //starting angle for enemy
	public final int wg_PlayerBaseAngle = 315; //starting angle for player
	public final float minimiser_start = 999999; //Where minimiser routines start
	public final float wg_PercChanceKeepWater = 0.03f;

	//sprite manager settings
	public final int pathfinding_max_depth = 100000; //kill pathfinding early in the 500-2000 range
//	public final int pathfinding_accuracy = 7; //Current code assumes that pathfinding accuracy == tile size
	public final int look_for_spot_radius = 200; //loops to look for safe place location around point, search increasing each time
	public final int buildingRadius = 8;
	public final int attractorRadius = 4;
	public final int actorRadius = 3;
	public final int projectileRadius = 2;
	public final int resourceRadius = 3;
	public final int resources_kept_away_from_base = 50; //clear area around main bases
	public final int place_res_gaussian = 2;

	//actor settings
	public final int starting_actors = 2;
	public final int amount_collect_per_tock = 1; //amount to take/give per tock
	public final int wander_radius = 30; //radius of square to choose destination when wander or idle
	public final int wander_pull_to_target = 4; //small pull to destination when wandering, player base when idle
	public final int tocks_before_retake_job_from_boss = 5; //How long before retake job from same boss once quit
	public final int actor_look_for_spot_radius = 50;
	public final int actor_aggro_radius = 60;
	public final float actor_speed = 0.5f;
	public final float actor_strength = 4f;
	public final float actor_starting_health = 50f;
	public final int actor_attack_range = 30;
	
	//biome settings
	public final int biome_golbal_density_mod = 1;//tiles_size; //derived
	public final float biome_colour_range = 10; //smear of colours in biome
	public final int biome_desert_min_density = 2 + biome_golbal_density_mod;
	public final int biome_desert_rnd_density = 2 + biome_golbal_density_mod;
	public final int biome_grass_min_density = 3 + biome_golbal_density_mod;
	public final int biome_grass_rnd_density = 3 + biome_golbal_density_mod;
	public final int biome_forest_min_density = 6 + biome_golbal_density_mod;
	public final int biome_forest_rnd_density = 6 + biome_golbal_density_mod;

	//resource settings
	public final int not_reachable_penelty_tocks = 20; //if an actor fails to pathfind to this as a target, how long to quarantine for. This floats with every fail.
	public final int resource_rnd = 20;
	public final int resource_min = 10;
	public final int resource_desired_global = 4000;
	public final int resource_mines_less_factor = 5; //* how much less for mines (as they cluster)
	public final float resource_chance_grow = 0.01f;
	public final float resource_change_spawn = 0.001f; //careful with this one! can fill board up with trees n' junk
	public final int resource_max_stuff = 50;

	//spooge settings
	public final float gravity = 0.2f;
	public final int spooges_actor_death = 20;
	public final int spooges_building_death = 200;
	public final int spooges_totem_death = 30;
	public final int spooges_base_death = 1000;

	//waterfall settings
	public final int waterfall_splash_radius = 30;
	public final int waterfall_splashes = 400;
	public final int waterfall_size = (int) (window_Y*4f);
	public final float waterfall_disk_size = 0.71f;
	public final float waterfall_fall_rate = 1f;

	
	//building settings
	public final int building_gather_radius = 100;
	public final int building_gatherers_per_site = 2;
	public final int building_health_per_build_action = 5;
	public final int building_max_health = 200;
	public final int building_AI_openSpace = 4; //How many radii around building to leave clear
	public final int building_AI_GranularityToStudy = 10; //Higher value makes AI place quicker, but leaves sub-optimal placement
	public final float building_refund_amount = 0.5f;
	public final int building_no_resource_penalty = 50;//s
	public final int building_Place_degrees = 32; //can-be-placed markers
	public final int building_Place_degrees_show = 16; //can-be-placed markers

	//AI
	public final int AI_BadBuilding_Before_Sell = 3; //building_no_resource_penalty seconds per tick
	public final int AI_NewUnitsWhenXClosetoCap = 2;
	public final int AI_BuildCooldown = 40;//tocks (applies per building type)
	public final int AI_TargetUnitMultipler = 3;
	
	//COSTS
	public final int StartingResources = 500;
	public final int COST_Building_Base = 100;
	public final int COST_Attractor_Base = 25;
	public final int COST_Actor_Base = 20;
	public final int EXTRA_Base = 6;
	//0_0\\
	public final int COST_Woodshop_Wood = 0; //sec
	public final int COST_Woodshop_Iron = COST_Building_Base; //pri
	public final int COST_Woodshop_Stone = 0; //ter
	//0_0\\
	public final int COST_Smelter_Wood = 0; //ter
	public final int COST_Smelter_Iron = 0; //sec
	public final int COST_Smelter_Stone = COST_Building_Base; //pri
	//0_0\\
	public final int COST_Rockery_Wood = COST_Building_Base; //pri
	public final int COST_Rockery_Iron = 0; //ter
	public final int COST_Rockery_Stone = 0; //sec
	//0_0\\
	public final int COST_Paper_Wood = COST_Actor_Base/2;
	public final int COST_Scissors_Iron = COST_Actor_Base;
	public final int COST_Rock_Stone = COST_Actor_Base*2;

	public final int COST_AttractorPaper_Wood = COST_Attractor_Base;
	public final int COST_AttractorRock_Stone = COST_Attractor_Base;
	public final int COST_AttractorScissors_Iron = COST_Attractor_Base;

	public final int EXTRA_Paper_PerWoodmill = EXTRA_Base*2;
	public final int EXTRA_Rock_PerRockery = EXTRA_Base/2;
	public final int EXTRA_Scissors_PerSmelter = EXTRA_Base;
	
	private Utility() {
		System.out.println("--- Utility spawned (depends on nothing): "+this);
	}

	public WorldPoint PolarToCartesian(float _angle, float _radius) {
		final float _x = (float) (_radius * Math.cos(Math.toRadians(_angle)));
		final float _y = (float) (-_radius * Math.sin(Math.toRadians(_angle)));
		return new WorldPoint((int)Math.round(_x), (int)Math.round(_y));
	}

	public float Seperation(Number x1, Number x2, Number y1, Number y2) {
		return (float) Math.hypot(x1.floatValue()-x2.floatValue(), y1.floatValue()-y2.floatValue());
		//return (float) Math.sqrt( (x1.floatValue()-x2.floatValue())*(x1.floatValue()-x2.floatValue()) + (y1.floatValue()-y2.floatValue())*(y1.floatValue()-y2.floatValue()) );
	}

	public float Seperation(WorldPoint a, WorldPoint b) {
		return (float) Math.hypot(a.getX()-b.getX(), a.getY()-b.getY());
		//return (float) Math.sqrt( (a.getX()-b.getX())*(a.getX()-b.getX()) + (a.getY()-b.getY())*(a.getY()-b.getY()) );
	}

	public int XYtoID(int _x, int _y) {
		_x -= (_x % tiles_size);
		_y -= (_y % tiles_size);
		final int _ID = (((_x + ((tiles_size*world_tiles)/2)) * world_tiles) + (_y + ((tiles_size*world_tiles)/2)))/tiles_size;
		if (_ID >= 0
				&& _ID < world_tiles*world_tiles
				&& Math.abs(_x) <= ((tiles_size*world_tiles)/2)
				&& Math.abs(_y) <= ((tiles_size*world_tiles)/2)) return _ID;
		else {
			//System.out.println("--- Illigitimate ID requested in XYtoID ("+_x+","+_y+")");
			return -1; //if not legit
		}
	}

}
