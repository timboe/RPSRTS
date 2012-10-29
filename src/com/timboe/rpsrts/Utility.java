package com.timboe.rpsrts;

import java.util.Random;

public class Utility {
	
	private static Utility singleton = new Utility();
	
	public static Utility GetUtility() {
		return singleton;
	}

	//Program configuration
	public String rndSeedTxt = "Dave";
	public final Random rnd = new Random(rndSeedTxt.hashCode());
	
	//World parameters
	public int window_X = 1000;
	public int window_Y = 600;
	public int tiles_per_chunk = 8; //Used for coarse kT algo
	public int world_tiles = 176; //Number of tile elements on X
	public int tiles_size = 7; //Size of tile element in pixels
	
	public boolean dbg = false;
	public boolean wg = false;
	
	public float rotateAngle = 0f;
	
	public boolean doWorldGen = false;
	
	GameMode gameMode = GameMode.titleScreen;
	long loose_time;

	//time settings
	public final int ticks_per_tock = 30;
	
	//pathfinding counter
	public int pathfind_counter = 0;
	
	//world manager settings
	public final float wg_seconds_to_wait = 0f; //Time to wait between steps
	public final int wg_DegInCircle = 360;
	public final float island_scale = 0.75f; //Size of initial circular island w.r.t. world
	public final float wg_CrinkleScale = 30; //Multiplier to Gaussian edge smear
	public final int wg_CrinkleCoarseness = 3; //Maximum gradient to smear under
	public final float wg_kTStartPt = 1.f; //Min random chunk energy
	public final float wg_kTEndPt = 10.f; //Max random chunk energy
	public       float wg_kT_R = 70; //kT algorithm R parameter  (SET IN INIT)
	public final int wg_MinBiomes = 15; //Min number of generated biomes
	public final long wg_MaxBiomes = 65; //Max number of generated biomes
	public final float wg_MainBaseRadius = 0.8f; //How far out the main bases are placed
	public final int wg_ErrodeIterations = 4; //times to errode world
	public final int wg_EnemyBaseAngle = 135; //starting angle for enemy
	public final int wg_PlayerBaseAngle = 315; //starting angle for player
	public final float minimiser_start = 999999; //Where minimiser routines start
	public final float wg_PercChanceKeepWater = 0.03f;

	//sprite manager settings
	public final int pathfinding_max_depth = 100000; //kill pathfinding early in the 500-2000 range
	//Current code assumes that pathfinding accuracy == tile size
	public final int pathfinding_accuracy = 7; //MASSIVE performance hit for reducing this
	public final int look_for_spot_radius = 200; //loops to look for safe place location around point, search increasing each time
	public final int buildingRadius = 8;
	public final int attractorRadius = 4;
	public final int actorRadius = 3;
	public final int projectileRadius = 2;
	public final int resourceRadius = 3;
	public final int resources_kept_away_from_base = 50; //clear area around main bases
	public int place_res_gaussian = 2;

	//actor settings
	public final int starting_actors = 2;
	public final int amount_collect_per_tock = 1; //amount to take/give per tock
	public final int wander_radius = 30; //radius of square to choose destination when wander or idle
	public final int wander_pull_to_target = 4; //small pull to destination when wandering, player base when idle
	public final int tocks_before_retake_job_from_boss = 5; //How long before retake job from same boss once quit
	public final int actor_look_for_spot_radius = 50;
	public final int actor_aggro_radius = 60;

	//biome settings
	public       int biome_golbal_density_mod = 0; //set in init
	public final float biome_colour_range = 10; //smear of colours in biome
	public final int biome_desert_min_density = 2 + biome_golbal_density_mod;
	public final int biome_desert_rnd_density = 2 + biome_golbal_density_mod;
	public final int biome_grass_min_density = 3 + biome_golbal_density_mod;
	public final int biome_grass_rnd_density = 3 + biome_golbal_density_mod;
	public final int biome_forest_min_density = 6 + biome_golbal_density_mod;
	public final int biome_forest_rnd_density = 6 + biome_golbal_density_mod;

	//resource settings
	public final int not_reachable_penelty_tocks = 20; //if an actor fails to pathfind to this as a target, how long to quarantine for.
	//This floats with every fail.
	public final int resource_rnd = 20;
	public final int resource_min = 10;
	public final int resource_desired_global = 4000;
	public final int resource_mines_less_factor = 5; //* how much less for mines (as they cluster)
	public final float resource_chance_grow = 0.01f;
	public final float resource_change_spawn = 0.001f; //careful with this one! can fill board up with trees n' junk
	public final int resource_max_stuff = 50;

	//building settings
	public final int building_gather_radius = 100;
	public final int building_gatherers_per_site = 2;
	public final int building_health_per_build_action = 5;
	public final int building_max_health = 200;
	public final int building_AI_openSpace = 4; //How many radii around building to leave clear
	public final int building_AI_GranularityToStudy = 10; //Higher value makes AI place quicker, but leaves sub-optimal placement
	public final float building_refund_amount = 0.5f;
	public final int building_no_resource_penalty = 50;//s
	public final int building_Place_degrees = 32;
	public final int building_Place_degrees_show = 16;

	//AI
	public final int AI_BadBuilding_Before_Sell = 3; //building_no_resource_penalty seconds per tick
	public final int AI_NewUnitsWhenXClosetoCap = 2;
	public final int AI_BuildCooldown = 40;//tocks
	public final int AI_TargetUnitMultipler = 3;
	
	//COSTS
	public final int StartingResources = 500;
	//0_0\\
	public final int COST_Woodshop_Wood = 0; //sec
	public final int COST_Woodshop_Iron = 100; //pri
	public final int COST_Woodshop_Stone = 0; //ter
	//0_0\\
	public final int COST_Smelter_Wood = 0; //ter
	public final int COST_Smelter_Iron = 0; //sec
	public final int COST_Smelter_Stone = 100; //pri
	//0_0\\
	public final int COST_Rockery_Wood = 100; //pri
	public final int COST_Rockery_Iron = 0; //ter
	public final int COST_Rockery_Stone = 0; //sec
	//0_0\\
	public final int COST_Paper_Wood = 10;
	public final int COST_Scissors_Iron = 20;
	public final int COST_Rock_Stone = 40;

	public final int COST_AttractorPaper_Wood = 25;
	public final int COST_AttractorRock_Stone = 25;
	public final int COST_AttractorScissors_Iron = 25;

	public final int EXTRA_Paper_PerWoodmill = 12;
	public final int EXTRA_Rock_PerRockery = 3;
	public final int EXTRA_Scissors_PerSmelter = 6;
	
	private Utility() {
		//calculated on fly
		wg_kT_R = 14 * tiles_size;
		biome_golbal_density_mod = tiles_size;
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

	public int XYtoID(int _x, int _y, int _world_tiles, int tiles_size) {
		_x -= (_x % tiles_size);
		_y -= (_y % tiles_size);
		final int _ID = (((_x + ((tiles_size*_world_tiles)/2)) * _world_tiles) + (_y + ((tiles_size*_world_tiles)/2)))/tiles_size;
		if (_ID >= 0
				&& _ID < _world_tiles*_world_tiles
				&& Math.abs(_x) <= ((tiles_size*_world_tiles)/2)
				&& Math.abs(_y) <= ((tiles_size*_world_tiles)/2)) return _ID;
		else return -1; //if not legit
	}

}
