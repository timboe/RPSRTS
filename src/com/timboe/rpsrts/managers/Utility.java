package com.timboe.rpsrts.managers;

import java.util.Random;

import com.timboe.rpsrts.RPSRTS;
import com.timboe.rpsrts.enumerators.GameMode;
import com.timboe.rpsrts.world.WorldPoint;

public class Utility {
	private static Utility singleton = new Utility();
	public static Utility GetUtility() {
		return singleton;
	}

	//RANDOM NUMBER SOURCE
	public String rndSeedTxt = "";
	public int rnd_count = 0;
	private boolean rand_off = false;
	//
	//Global variables
	public RPSRTS _RPSRTS;
	public boolean dbg = false; //debug flag
	public boolean noPlayers = false; //AI takes over both teams
	public boolean soundOn = true;
	public boolean highQuality = true;
	public boolean fastForward = false;
	public float rotateAngle = 0f; //copied here as ROTATE stored in Transform(awt) or Matrix(andorid) classes
	public boolean doWorldGen = false; //proceed with building world
	public GameMode gameMode = GameMode.titleScreen; //what state is game currently in
	public long game_time_count; //gameplay time in ms;
	public long loose_time; //what time was game won/lost
	public long FPS = 0;
	public boolean mouseClick = false;
	public boolean mouseDrag = false;
	public boolean sendMouseDragPing = false;
	public boolean gamePaused = false;
	public boolean showRedScore = true;
	public String playerName = "";
	//
	public long _TICK;
	public long _TIME_OF_LAST_TICK = 0; // Internal
	private long _DESIRED_FPS = 30; // Frames per second to aim for
	private long _DESIRED_TPS;
	private long _TICKS_PER_RENDER = 1; //nominal is 2 (game will go suuuper fast if this is bumped up)

	//---------\\
	// STATICS \\
	//---------\\

	//World parameters
	public final int window_X = 1000;
	public final int window_Y = 600;
	public final int tiles_per_chunk = 8; //Used for coarse kT algo
	public final int world_tiles = 184; //Number of tile elements on X was 176
	public final int tiles_size = 7; //Size of tile element in pixels
	public final int world_size = world_tiles*tiles_size;
	public final int world_size2 = world_size/2;
	//time, fps & update settings
	public final int ticks_per_tock = 60; //at 30 FPS, 2 ticks per render, this is one tock per second
	public final int do_fps_every_x_ticks = 6; // refresh FPS after X frames
	public final int game_ticks_per_render = 1;
	public final int slowmo_ticks_per_render = 1;
	public final int fast_forward_speed = 10;

	//world manager settings
	public final float wg_seconds_to_wait = 0.5f; //Time to wait between steps
	public final int wg_DegInCircle = 360;
	public final float island_scale = 0.75f; //Size of initial circular island w.r.t. world
	public final float wg_CrinkleScale = 30; //Multiplier to Gaussian edge smear
	public final int wg_CrinkleCoarseness = 3; //Maximum gradient to smear under
	public final int wg_kt_break_alg_every_X_loops = 30;
	public final float wg_kTStartPt = 1.f; //Min random chunk energy
	public final float wg_kTEndPt = 10.f; //Max random chunk energy
	public final float wg_kT_R =  14 * tiles_size; //kT algorithm R parameter  (derived)
	public final int wg_MinBiomes = 15; //Min number of generated biomes
	public final long wg_MaxBiomes = 65*100; //Max number of generated biomes
	public final float wg_MainBaseRadius = 1.2f; //How far out the main bases are placed
	public final int wg_ErrodeIterations = 4; //times to errode world
	public final int wg_EnemyBaseAngle = 135; //starting angle for enemy
	public final int wg_PlayerBaseAngle = 315; //starting angle for player
	public final float minimiser_start = 999999; //Where minimiser routines start
	public final float wg_PercChanceKeepWater = 0.04f;

	//sprite manager settings
	public final int pathfinding_max_depth = 100000; //kill pathfinding early in the 500-2000 range
	//	public final int pathfinding_accuracy = 7; //Current code assumes that pathfinding accuracy == tile size
	public final int look_for_spot_radius = 200; //loops to look for safe place location around point, search increasing each time
	public final int buildingRadius = 8;
	public final int attractorRadius = 4;
	public final int actorRadius = 3;
	public final int projectileRadius = 2;
	public final int specialSpawnRadius = 10;
	public final int resourceRadius = 3;
	public final int resources_kept_away_from_base = 75; //clear area around main bases
	public final int place_res_gaussian = 4; //clustering of mines and rockpiles
	public final float projectile_speed = 1f;

	//actor settings
	public final int starting_actors = 2;
	public final int amount_collect_per_tock = 1; //amount to take/give per tock
	public final int wander_radius = 30; //radius of square to choose destination when wander or idle
	public final int wander_pull_to_target = 4; //small pull to destination when wandering, player base when idle
	public final int tocks_before_retake_job_from_boss = 5; //How long before retake job from same boss once quit
	public final int actor_look_for_spot_radius = 50;
	public final int actor_aggro_radius = 60;
	public final float actor_speed = 1f; //was 0.5f
	public final float actor_strength = 4f;
	public final float actor_starting_health = 50f;
	public final int actor_attack_range = 30;
	public final float actor_poison_rate = 0.15f;
	public final int actor_poison_ticks = 120;
	public final float actor_poison_range = 20;

	//biome settings
	public final int biome_golbal_density_mod = tiles_size;//tiles_size; //derived
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
	public final int resource_desired_global = 8000; //normalise starting resource amount to this number
	public final int resource_mines_less_factor = 5; //* how much less for mines (as they cluster)
	public final float resource_chance_grow = 0.005f;
	public final float resource_chance_spawn = 0.0005f; //careful with this one! can fill board up with trees n' junk
	public final int resource_max_stuff = 50;

	//spooge settings
	public final float gravity = 0.4f;
	public final int spooges_actor_death = 20;//20;
	public final int spooges_hit = 2;//2;
	public final int spooges_building_death = 200;// 200;
	public final int spooges_totem_death = 50; //30
	public final int spooges_base_death = 1000;
	public final float spooges_scale_actor_death = 0.2f;
	public final float spooges_scale_hit = 0.2f;
	public final float spooges_scale_building_death = 0.4f;
	public final float spooges_scale_totem_death = 0.3f;
	public final float spooges_scale_base_death = 0.5f;

	//score settings
	public final int extra_score_mins = 10; //how many mins until no quick win bonus
	public final float quick_win_bonus = 2f;

	//waterfall settings
	public final int waterfall_splash_radius = world_size/100;
	public final int waterfall_splashes = 400; //maximum splashes
	public final int waterfall_size = (int) (window_Y*4f); //this is vertical size / height
	public final float waterfall_disk_size = 0.71f;
	public final float waterfall_fall_rate = 1f;

	//building settings
	public final int building_gather_radius = 120;
	public final int building_gatherers_per_site = 2;
	public final int building_health_per_build_action = 5;
	public final int building_max_health = 200;
	public final int building_AI_openSpace = 4; //How many radii around building to leave clear
	public final int building_AI_GranularityToStudy = 10; //Higher value makes AI place quicker, but leaves sub-optimal placement
	public final float building_refund_amount = 0.5f;
	public final int building_no_resource_penalty = 50;//s
	public final int building_Place_degrees = 32; //can-be-placed markers
	public final int building_Place_degrees_show = 16; //can-be-placed markers
	public final int building_Explode_radius = 50;
	public final int building_Explode_ticks = 30;
	public final float building_Explode_damage = 0.5f; //damage per tick
	public final float building_Explode_vs_building_multiplier = 10f; //above times this for buildings

	//AI
	public final int AI_BadBuilding_Before_Sell = 3; //building_no_resource_penalty seconds per tick
	public final int AI_NewUnitsWhenXClosetoCap = 2;
	public final int AI_BuildCooldown = 40;//tocks (applies per building type)
	public final int AI_TargetUnitMultipler = 4; //How many fully-populated buildings worth of units is ideal? (increased chance of attack as gets close to cap)
	public final int AI_MinUnits = 3; //Minimum units before UnitGen is always kept TRUE
	public final float AI_AttackTocks = 0.01f; //try attack on 1% of tocks
	public final float AI_DefenceTocks = 0.25f; //Defend on 25% of tocks
	public final int AI_DefenceRadius = 3 * wander_radius;
	public final int AI_AttackRadius = 2 * wander_radius;
	public final float AI_ChanceToKillLoanAttack = 0.7f; //Suppress attacking with one unit type
	public final float AI_ChanceOfRushAttack = 0.5f;

	//COSTS
	public final int StartingResources = 7500;
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

	private final Random rndGen = new Random();
	private Utility() {
		System.out.println("--- Utility spawned (depends on nothing): "+this);
		SetDesiredFPS(_DESIRED_FPS); //To initially set desired TPS
	}

	public long GetDesiredTPS() {
		return _DESIRED_TPS;
	}

	public long GetTicksPerRender() {
		return _TICKS_PER_RENDER;
	}
	public WorldPoint PolarToCartesian(final float _angle, final float _radius) {
		final float _x = (float) (_radius * Math.cos(Math.toRadians(_angle)));
		final float _y = (float) (-_radius * Math.sin(Math.toRadians(_angle)));
		return new WorldPoint(Math.round(_x), Math.round(_y));
	}
	public float rnd() {
		++rnd_count;
		if (rand_off == true) return 0.3f;
		else
		return rndGen.nextFloat();
	}

	public float rndG(final float _mean, final float _sigma) {
		++rnd_count;
		if (rand_off == true) return (_mean + ( _sigma * 0.3f ));
		else return (float) (_mean + ( _sigma * rndGen.nextGaussian() ));
	}

	public int rndI() {
		++rnd_count;
		if (rand_off == true) return 1;
		else return rndGen.nextInt();
	}

	public int rndI(final int _n) {
		++rnd_count;
		if (rand_off == true) return 1;
		else return rndGen.nextInt(_n);
	}

	public float Seperation(final Number x1, final Number x2, final Number y1, final Number y2) {
		return (float) Math.hypot(x1.floatValue()-x2.floatValue(), y1.floatValue()-y2.floatValue());
	}

	public float Seperation(final WorldPoint a, final WorldPoint b) {
		return (float) Math.hypot(a.getX()-b.getX(), a.getY()-b.getY());
	}

	public void SetDesiredFPS(final long _DESIRED_FPS2) {
		_DESIRED_FPS = _DESIRED_FPS2;
		_DESIRED_TPS = _DESIRED_FPS * _TICKS_PER_RENDER;
	}

	public void setSeed() {
		rndGen.setSeed(rndSeedTxt.hashCode());
		rnd_count = 0;
	}

	public void SetTicksPerRender(final long l) {
		_TICKS_PER_RENDER = l;
		_DESIRED_TPS = _DESIRED_FPS * _TICKS_PER_RENDER;
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
