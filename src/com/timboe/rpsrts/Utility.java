package com.timboe.rpsrts;

import java.util.Random;

public class Utility {

	//Program configuration
	public long rndSeed;
	public Random rnd;
	
	public boolean dbg = false;
	public boolean wg = false;
	
	public float rotateAngle = 0f;

	//time settings
	int ticks_per_tock = 30;
	
	//pathfinding counter
	public int pathfind_counter = 0;
	
	//world manager settings
	public float wg_seconds_to_wait = 0.f; //Time to wait between steps
	public int wg_DegInCircle = 360;
	public float island_scale = 0.75f; //Size of initial circular island w.r.t. world
	public float wg_CrinkleScale = 30; //Multiplier to Gaussian edge smear
	public int wg_CrinkleCoarseness = 3; //Maximum gradient to smear under
	public float wg_kTStartPt = 1.f; //Min random chunk energy
	public float wg_kTEndPt = 10.f; //Max random chunk energy
	public float wg_kT_R = 70; //kT algorithm R parameter
	public int wg_MinBiomes = 15; //Min number of generated biomes
	public long wg_MaxBiomes = 65; //Max number of generated biomes
	public float wg_MainBaseRadius = 0.8f; //How far out the main bases are placed
	public int wg_ErrodeIterations = 4; //times to errode world
	public int wg_EnemyBaseAngle = 135; //starting angle for enemy
	public int wg_PlayerBaseAngle = 315; //starting angle for player
	public float minimiser_start = 999999; //Where minimiser routines start
	public float wg_PercChanceKeepWater = 0.03f;

	//sprite manager settings
	public int pathfinding_max_depth = 100000; //kill pathfinding early in the 500-2000 range
	//Current code assumes that pathfinding accuracy == tile size
	public int pathfinding_accuracy = 7; //MASSIVE performance hit for reducing this
	public int look_for_spot_radius = 200; //loops to look for safe place location around point, search increasing each time
	public int buildingRadius = 8;
	public int attractorRadius = 4;
	public int actorRadius = 3;
	public int projectileRadius = 2;
	public int resourceRadius = 3;
	public int resources_kept_away_from_base = 50; //clear area around main bases
	public int place_res_gaussian = 2;

	//actor settings
	public int starting_actors = 2;
	public int amount_collect_per_tock = 1; //amount to take/give per tock
	public int wander_radius = 30; //radius of square to choose destination when wander or idle
	public int wander_pull_to_target = 4; //small pull to destination when wandering, player base when idle
	public int tocks_before_retake_job_from_boss = 5; //How long before retake job from same boss once quit
	public int actor_look_for_spot_radius = 50;
	public int actor_aggro_radius = 60;

	//biome settings
	public int biome_golbal_density_mod = 0;
	public float biome_colour_range = 10; //smear of colours in biome
	public int biome_desert_min_density = 2 + biome_golbal_density_mod;
	public int biome_desert_rnd_density = 2 + biome_golbal_density_mod;
	public int biome_grass_min_density = 3 + biome_golbal_density_mod;
	public int biome_grass_rnd_density = 3 + biome_golbal_density_mod;
	public int biome_forest_min_density = 6 + biome_golbal_density_mod;
	public int biome_forest_rnd_density = 6 + biome_golbal_density_mod;

	//resource settings
	public int not_reachable_penelty_tocks = 20; //if an actor fails to pathfind to this as a target, how long to quarantine for.
	//This floats with every fail.
	public int resource_rnd = 20;
	public int resource_min = 10;
	public int resource_desired_global = 4000;
	public int resource_mines_less_factor = 5; //* how much less for mines (as they cluster)
	public float resource_chance_grow = 0.01f;
	public float resource_change_spawn = 0.001f; //careful with this one! can fill board up with trees n' junk
	public int resource_max_stuff = 50;

	//building settings
	public int building_gather_radius = 100;
	public int building_gatherers_per_site = 2;
	public int building_health_per_build_action = 5;
	public int building_max_health = 200;
	public int building_AI_openSpace = 4; //How many radii around building to leave clear
	public int building_AI_GranularityToStudy = 10; //Higher value makes AI place quicker, but leaves sub-optimal placement
	public float building_refund_amount = 0.5f;
	public int building_no_resource_penalty = 50;//s
	public int building_Place_degrees = 32;
	public int building_Place_degrees_show = 16;

	//AI
	public int AI_BadBuilding_Before_Sell = 3; //building_no_resource_penalty seconds per tick
	public int AI_NewUnitsWhenXClosetoCap = 2;
	public int AI_BuildCooldown = 40;//tocks
	public int AI_TargetUnitMultipler = 3;
	
	//COSTS
	public int StartingResources = 500;
	//0_0\\
	public int COST_Woodshop_Wood = 0; //sec
	public int COST_Woodshop_Iron = 100; //pri
	public int COST_Woodshop_Stone = 0; //ter
	//0_0\\
	public int COST_Smelter_Wood = 0; //ter
	public int COST_Smelter_Iron = 0; //sec
	public int COST_Smelter_Stone = 100; //pri
	//0_0\\
	public int COST_Rockery_Wood = 100; //pri
	public int COST_Rockery_Iron = 0; //ter
	public int COST_Rockery_Stone = 0; //sec
	//0_0\\
	public int COST_Paper_Wood = 10;
	public int COST_Scissors_Iron = 20;
	public int COST_Rock_Stone = 40;

	public int COST_AttractorPaper_Wood = 25;
	public int COST_AttractorRock_Stone = 25;
	public int COST_AttractorScissors_Iron = 25;


	public int EXTRA_Paper_PerWoodmill = 12;
	public int EXTRA_Rock_PerRockery = 3;
	public int EXTRA_Scissors_PerSmelter = 6;

	public Utility() {
		rnd = new Random();
		rndSeed = rnd.nextInt(10000);
		rnd = new Random(rndSeed);
	}


	public WorldPoint PolarToCartesian(float _angle, float _radius) {
		final float _x = (float) (_radius * Math.cos(Math.toRadians(_angle)));
		final float _y = (float) (-_radius * Math.sin(Math.toRadians(_angle)));
		return new WorldPoint((int)Math.round(_x), (int)Math.round(_y));
	}

	public float Seperation(Number x1, Number x2, Number y1, Number y2) {
		return (float) Math.sqrt( (x1.floatValue()-x2.floatValue())*(x1.floatValue()-x2.floatValue()) + (y1.floatValue()-y2.floatValue())*(y1.floatValue()-y2.floatValue()) );
	}

	public float Seperation(WorldPoint a, WorldPoint b) {
		return (float) Math.sqrt( (a.getX()-b.getX())*(a.getX()-b.getX()) + (a.getY()-b.getY())*(a.getY()-b.getY()) );
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
