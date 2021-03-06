package com.timboe.rpsrts.managers;

import com.timboe.rpsrts.enumerators.ActorType;
import com.timboe.rpsrts.enumerators.BuildingType;
import com.timboe.rpsrts.enumerators.GameMode;
import com.timboe.rpsrts.enumerators.GameStatistics;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.enumerators.ResourceType;
import com.timboe.rpsrts.sprites.Building;
import com.timboe.rpsrts.world.WorldPoint;

public class ResourceManager {
	private static ResourceManager singleton = new ResourceManager();
	public static ResourceManager GetResourceManager() {
		return singleton;
	}

	private final Utility utility = Utility.GetUtility();
	private final SpriteManager theSpriteManager = SpriteManager.GetSpriteManager();

	private int PLAYER_SCORE = 0;
	private int ENEMY_SCORE = 0;

	//
	private boolean GEN_PAPER_PLAYER = true;
	private boolean GEN_ROCK_PLAYER = true;
	private boolean GEN_SCISSORS_PLAYER= true;

	private boolean GEN_PAPER_ENEMY = true;
	private boolean GEN_ROCK_ENEMY = true;
	private boolean GEN_SCISSORS_ENEMY= true;

	//

	private int GLOBAL_WOOD = 0;
	private int GLOBAL_STONE = 0;
	private int GLOBAL_IRON = 0;

	private int PLAYER_WOOD = 0;
	private int PLAYER_STONE = 0;
	private int PLAYER_IRON = 0;

	private int ENEMY_WOOD = 0;
	private int ENEMY_STONE = 0;
	private int ENEMY_IRON = 0;

	//

	private int N_PLAYER_WOODSHOPS = 0;
	private int N_PLAYER_PAPER_ATTRACTORS = 0;
	private int N_PLAYER_SMELTERS = 0;
	private int N_PLAYER_SCISSOR_ATTRACTORS = 0;
	private int N_PLAYER_ROCKERY = 0;
	private int N_PLAYER_ROCK_ATTRACTORS = 0;

	private int N_ENEMY_WOODSHOPS = 0;
	private int N_ENEMY_PAPER_ATTRACTORS = 0;
	private int N_ENEMY_SMELTERS = 0;
	private int N_ENEMY_SCISSOR_ATTRACTORS = 0;
	private int N_ENEMY_ROCKERY = 0;
	private int N_ENEMY_ROCK_ATTRACTORS = 0;

	//

	private int PLAYER_MAX_SCISSORS;
	private int PLAYER_MAX_ROCK;
	private int PLAYER_MAX_PAPER;

	private int ENEMY_MAX_SCISSORS;
	private int ENEMY_MAX_ROCK;
	private int ENEMY_MAX_PAPER;

	private int PLAYER_SCISSORS;
	private int PLAYER_ROCK;
	private int PLAYER_PAPER;

	private int ENEMY_SCISSORS;
	private int ENEMY_ROCK;
	private int ENEMY_PAPER;

	//

	private int STAT_TreesChopped;
	private int STAT_ProjectilesFired;
	private int STAT_SpecialUnitsSpawned;
	private int STAT_ResourcesPlundered;
	private int STAT_BuildingsConstructed;
	private int STAT_UnitsPoisoned;
	private int STAT_RocksAssembled;
	private int STAT_TroopsSlaughtered;
	private int STAT_BuildingsExploded;
	//One more

	private ResourceManager() {
		Reset();
		System.out.println("--- Resource Manager spawned (depends on Util,Sprite) : "+this);
	}

	public void AddBuildingToTally(final ObjectOwner _oo, final BuildingType _bt) {
		AddRmBuildingToTally(_oo, _bt, +1);
	}

	public void AddResources(final ResourceType _rt, final int _amount, final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Player) {
			if (_rt == ResourceType.Mine) {
				PLAYER_IRON += _amount;
				GLOBAL_IRON -= _amount;
			} else if (_rt == ResourceType.Rockpile) {
				PLAYER_STONE += _amount;
				GLOBAL_STONE -= _amount;
			} else {
				PLAYER_WOOD += _amount;
				GLOBAL_WOOD -= _amount;
			}
		} else if (_oo == ObjectOwner.Enemy) {
			if (_rt == ResourceType.Mine) {
				ENEMY_IRON += _amount;
				GLOBAL_IRON -= _amount;
			} else if (_rt == ResourceType.Rockpile) {
				ENEMY_STONE += _amount;
				GLOBAL_STONE -= _amount;
			} else {
				ENEMY_WOOD += _amount;
				GLOBAL_WOOD -= _amount;
			}
		}
	}

	private void AddRmBuildingToTally(final ObjectOwner _oo, final BuildingType _bt, final int _amount) {
		if (_oo == ObjectOwner.Player) {
			if (_bt == BuildingType.AttractorPaper) N_PLAYER_PAPER_ATTRACTORS += _amount;
			else if (_bt  == BuildingType.AttractorRock) N_PLAYER_ROCK_ATTRACTORS += _amount;
			else if (_bt ==  BuildingType.AttractorScissors) N_PLAYER_SCISSOR_ATTRACTORS += _amount;
			else if (_bt == BuildingType.Woodshop)	N_PLAYER_WOODSHOPS += _amount;
			else if (_bt == BuildingType.Rockery) N_PLAYER_ROCKERY += _amount;
			else if (_bt == BuildingType.Smelter) N_PLAYER_SMELTERS += _amount;
		} else {
			if (_bt == BuildingType.AttractorPaper) N_ENEMY_PAPER_ATTRACTORS += _amount;
			else if (_bt  == BuildingType.AttractorRock) N_ENEMY_ROCK_ATTRACTORS += _amount;
			else if (_bt ==  BuildingType.AttractorScissors) N_ENEMY_SCISSOR_ATTRACTORS += _amount;
			else if (_bt == BuildingType.Woodshop)	N_ENEMY_WOODSHOPS += _amount;
			else if (_bt == BuildingType.Rockery) N_ENEMY_ROCKERY += _amount;
			else if (_bt == BuildingType.Smelter) N_ENEMY_SMELTERS += _amount;
		}
	}

	public void AddStatistic(final GameStatistics _stat) {
		switch (_stat) {
		case BuildingsConstructed: ++STAT_BuildingsConstructed; return;
		case ProjectilesFired: ++STAT_ProjectilesFired; return;
		case ResourcesPlundered: ++STAT_ResourcesPlundered; return;
		case RocksAssembled: ++STAT_RocksAssembled; return;
		case SpecialUnitsSpawned: ++STAT_SpecialUnitsSpawned; return;
		case BuildingsExploded: ++STAT_BuildingsExploded; return;
		case TreesChopped: ++STAT_TreesChopped; return;
		case TroopsSlaughtered: ++STAT_TroopsSlaughtered; return;
		case UnitsPoisoned: ++STAT_UnitsPoisoned; return;
		}
	}

	private boolean AffordBuyRefund(final BuildingType _bt, final ObjectOwner _oo, final boolean doPurchase, final boolean doRefund) {
		int cost_wood = 0;
		int cost_iron = 0;
		int cost_stone = 0;
		if (_bt == BuildingType.Woodshop) {
			cost_wood = utility.COST_Woodshop_Wood;
			cost_iron = utility.COST_Woodshop_Iron;
			cost_stone = utility.COST_Woodshop_Stone;
		} else if (_bt == BuildingType.Rockery) {
			cost_wood = utility.COST_Rockery_Wood;
			cost_iron = utility.COST_Rockery_Iron;
			cost_stone = utility.COST_Rockery_Stone;
		} else if (_bt == BuildingType.Smelter) {
			cost_wood = utility.COST_Smelter_Wood;
			cost_iron = utility.COST_Smelter_Iron;
			cost_stone = utility.COST_Smelter_Stone;
		} else if (_bt == BuildingType.AttractorPaper) {
			cost_wood = utility.COST_AttractorPaper_Wood;
		} else if (_bt == BuildingType.AttractorRock) {
			cost_stone = utility.COST_AttractorRock_Stone;
		} else if (_bt ==BuildingType.AttractorScissors) {
			cost_iron = utility.COST_AttractorScissors_Iron;
		}
		if (doPurchase == true) {
			ScorePoints(_oo, cost_wood);
			ScorePoints(_oo, cost_iron);
			ScorePoints(_oo, cost_stone);
			if (_oo == ObjectOwner.Player) {
				PLAYER_WOOD -= cost_wood;
				PLAYER_IRON -= cost_iron;
				PLAYER_STONE -= cost_stone;
			} else {
				ENEMY_WOOD -= cost_wood;
				ENEMY_IRON -= cost_iron;
				ENEMY_STONE -= cost_stone;
			}
		} else if (doRefund == true) {
			ScorePoints(_oo, (int) (-cost_wood * utility.building_refund_amount));
			ScorePoints(_oo, (int) (-cost_iron * utility.building_refund_amount));
			ScorePoints(_oo, (int) (-cost_stone * utility.building_refund_amount));
			if (_oo == ObjectOwner.Player) {
				PLAYER_WOOD += cost_wood * utility.building_refund_amount;
				PLAYER_IRON += cost_iron * utility.building_refund_amount;
				PLAYER_STONE += cost_stone * utility.building_refund_amount;
			} else {
				ENEMY_WOOD += cost_wood * utility.building_refund_amount;
				ENEMY_IRON += cost_iron * utility.building_refund_amount;
				ENEMY_STONE += cost_stone * utility.building_refund_amount;
			}
		} else { //can afford?
			if (_oo == ObjectOwner.Player) {
				if (PLAYER_WOOD < cost_wood ) return false;
				if (PLAYER_IRON < cost_iron) return false;
				if (PLAYER_STONE < cost_stone) return false;
			} else {
				if (ENEMY_WOOD < cost_wood) return false;
				if (ENEMY_IRON < cost_iron) return false;
				if (ENEMY_STONE < cost_stone) return false;
			}
		}
		return true;
	}

	public void Buy(final BuildingType _bt, final ObjectOwner _oo) {
		AffordBuyRefund(_bt, _oo, true, false);
	}

	public boolean CanAfford(final BuildingType _bt, final ObjectOwner _oo) {
		return AffordBuyRefund(_bt, _oo, false, false);
	}

	public void DecreaseMaxUnits(final BuildingType _bt, final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Player) {
			if (_bt == BuildingType.Woodshop) {
				PLAYER_MAX_PAPER -= utility.EXTRA_Paper_PerWoodmill;
			} else if (_bt == BuildingType.Rockery) {
				PLAYER_MAX_ROCK -= utility.EXTRA_Rock_PerRockery;
			} else if (_bt == BuildingType.Smelter) {
				PLAYER_MAX_SCISSORS -= utility.EXTRA_Scissors_PerSmelter;
			}
		} else if (_oo == ObjectOwner.Enemy) {
			if (_bt == BuildingType.Woodshop) {
				ENEMY_MAX_PAPER -= utility.EXTRA_Paper_PerWoodmill;
			} else if (_bt == BuildingType.Rockery) {
				ENEMY_MAX_ROCK -= utility.EXTRA_Rock_PerRockery;
			} else if (_bt == BuildingType.Smelter) {
				ENEMY_MAX_SCISSORS -= utility.EXTRA_Scissors_PerSmelter;
			}
		}
	}

	public int GetActorsPerAttractor(final ObjectOwner _oo, final BuildingType _bt) {
		int amount_per_attractor = 0;
		if (_oo == ObjectOwner.Player) {
			if (_bt == BuildingType.AttractorPaper) {
				amount_per_attractor = PLAYER_PAPER;
				amount_per_attractor -= (N_PLAYER_ROCKERY * utility.building_gatherers_per_site);
				if (N_PLAYER_PAPER_ATTRACTORS > 0) amount_per_attractor /= N_PLAYER_PAPER_ATTRACTORS;
			} else if (_bt == BuildingType.AttractorRock) {
				amount_per_attractor = PLAYER_ROCK;
				amount_per_attractor -= (N_PLAYER_SMELTERS * utility.building_gatherers_per_site);
				if (N_PLAYER_ROCK_ATTRACTORS > 0) amount_per_attractor /= N_PLAYER_ROCK_ATTRACTORS;
			} else if (_bt == BuildingType.AttractorScissors) {
				amount_per_attractor = PLAYER_SCISSORS;
				amount_per_attractor -= (N_PLAYER_WOODSHOPS * utility.building_gatherers_per_site);
				if (N_PLAYER_SCISSOR_ATTRACTORS > 0) amount_per_attractor /= N_PLAYER_SCISSOR_ATTRACTORS;
			}
		} else if (_oo == ObjectOwner.Enemy) {
			if (_bt == BuildingType.AttractorPaper) {
				amount_per_attractor = ENEMY_PAPER;
				amount_per_attractor -= (N_ENEMY_WOODSHOPS * utility.building_gatherers_per_site);
				if (N_ENEMY_PAPER_ATTRACTORS > 0) amount_per_attractor /= N_ENEMY_PAPER_ATTRACTORS;
			} else if (_bt == BuildingType.AttractorRock) {
				amount_per_attractor = ENEMY_ROCK;
				amount_per_attractor -= (N_ENEMY_ROCKERY * utility.building_gatherers_per_site);
				if (N_ENEMY_ROCK_ATTRACTORS > 0) amount_per_attractor /= N_ENEMY_ROCK_ATTRACTORS;
			} else if (_bt == BuildingType.AttractorScissors) {
				amount_per_attractor = ENEMY_SCISSORS;
				amount_per_attractor -= (N_ENEMY_SMELTERS * utility.building_gatherers_per_site);
				if (N_ENEMY_SCISSOR_ATTRACTORS > 0) amount_per_attractor /= N_ENEMY_SCISSOR_ATTRACTORS;
			}
		}
		return amount_per_attractor;
	}

	public boolean GetGeneratingPaper(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Enemy) return GEN_PAPER_ENEMY;
		else return GEN_PAPER_PLAYER;
	}

	public boolean GetGeneratingRock(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Enemy) return GEN_ROCK_ENEMY;
		else return GEN_ROCK_PLAYER;
	}

	public boolean GetGeneratingScissors(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Enemy) return GEN_SCISSORS_ENEMY;
		else return GEN_SCISSORS_PLAYER;
	}

	public int GetGlobalIron() {
		return GLOBAL_IRON;
	}




	public int GetGlobalStone() {
		return GLOBAL_STONE;
	}

	public int GetGlobalWood() {
		return GLOBAL_WOOD;
	}

	public int GetMaxPaper(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Enemy) return ENEMY_MAX_PAPER;
		else return PLAYER_MAX_PAPER;
	}

	public int GetMaxRock(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Enemy) return ENEMY_MAX_ROCK;
		else return PLAYER_MAX_ROCK;
	}

	public int GetMaxScissor(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Enemy) return ENEMY_MAX_SCISSORS;
		else return PLAYER_MAX_SCISSORS;
	}

	public int GetNIron(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Enemy) return ENEMY_IRON;
		else return PLAYER_IRON;
	}

	public int GetNPaper(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Enemy) return ENEMY_PAPER;
		else return PLAYER_PAPER;
	}

	public int GetNRock(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Enemy) return ENEMY_ROCK;
		else return PLAYER_ROCK;
	}

	public int GetNScissor(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Enemy) return ENEMY_SCISSORS;
		else return PLAYER_SCISSORS;
	}

	public int GetNStone(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Enemy) return ENEMY_STONE;
		else return PLAYER_STONE;
	}

	public int GetNWood(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Enemy) return ENEMY_WOOD;
		else return PLAYER_WOOD;
	}

	public String GetResourceText() {
		return "WOOD:"+Integer.toString(PLAYER_WOOD)
				+" | STONE:"+Integer.toString(PLAYER_STONE)
				+" | IRON:"+Integer.toString(PLAYER_IRON);
	}
//	public String GetResourceText() {
//		return "EWOOD:"+Integer.toString(ENEMY_WOOD)
//				+" | ESTONE:"+Integer.toString(ENEMY_STONE)
//				+" | EIRON:"+Integer.toString(ENEMY_IRON);
//	}

	public int GetScore(final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Player) {
			return PLAYER_SCORE;
		} else {
			return ENEMY_SCORE;
		}
	}

	public String GetStatistic(final GameStatistics _stat) {
		switch (_stat) {
			case BuildingsConstructed: return Integer.toString(STAT_BuildingsConstructed);
			case ProjectilesFired: return Integer.toString(STAT_ProjectilesFired);
			case ResourcesPlundered: return Integer.toString(STAT_ResourcesPlundered);
			case RocksAssembled: return Integer.toString(STAT_RocksAssembled);
			case SpecialUnitsSpawned: return Integer.toString(STAT_SpecialUnitsSpawned);
			case BuildingsExploded: return Integer.toString(STAT_BuildingsExploded);
			case TreesChopped: return Integer.toString(STAT_TreesChopped);
			case TroopsSlaughtered: return Integer.toString(STAT_TroopsSlaughtered);
			case UnitsPoisoned: return Integer.toString(STAT_UnitsPoisoned);
		}
		return "0";
	}

	public String GetUnitText() {
		return "PAPER:"+Integer.toString(PLAYER_PAPER)+"/"+Integer.toString(PLAYER_MAX_PAPER)
				+" | ROCK:"+Integer.toString(PLAYER_ROCK)+"/"+Integer.toString(PLAYER_MAX_ROCK)
				+" | SCISSORS:"+Integer.toString(PLAYER_SCISSORS)+"/"+Integer.toString(PLAYER_MAX_SCISSORS);
	}
//	public String GetUnitText() {
//		return "EPAPER:"+Integer.toString(ENEMY_PAPER)+"/"+Integer.toString(ENEMY_MAX_PAPER)
//				+" | EROCK:"+Integer.toString(ENEMY_ROCK)+"/"+Integer.toString(ENEMY_MAX_ROCK)
//				+" | ESCISSORS:"+Integer.toString(ENEMY_SCISSORS)+"/"+Integer.toString(ENEMY_MAX_SCISSORS);
//	}

	public void IncreaseMaxUnits(final BuildingType _bt, final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Player) {
			if (_bt == BuildingType.Woodshop) {
				PLAYER_MAX_PAPER += utility.EXTRA_Paper_PerWoodmill;
			} else if (_bt == BuildingType.Rockery) {
				PLAYER_MAX_ROCK += utility.EXTRA_Rock_PerRockery;
			} else if (_bt == BuildingType.Smelter) {
				PLAYER_MAX_SCISSORS += utility.EXTRA_Scissors_PerSmelter;
			}
		} else if (_oo == ObjectOwner.Enemy) {
			if (_bt == BuildingType.Woodshop) {
				ENEMY_MAX_PAPER += utility.EXTRA_Paper_PerWoodmill;
			} else if (_bt == BuildingType.Rockery) {
				ENEMY_MAX_ROCK += utility.EXTRA_Rock_PerRockery;
			} else if (_bt == BuildingType.Smelter) {
				ENEMY_MAX_SCISSORS += utility.EXTRA_Scissors_PerSmelter;
			}
		}
	}

	public void ModGlobalIron(final int _mod) {
		GLOBAL_IRON += _mod;
	}

	public void ModGlobalStone(final int _mod) {
		GLOBAL_STONE += _mod;
	}

	public void ModGlobalWood(final int _mod) {
		GLOBAL_WOOD += _mod;
	}

	public void Refund(final BuildingType _bt, final ObjectOwner _oo) {
		AffordBuyRefund(_bt, _oo, false, true);
	}

	public void RemoveBuildingFromTally(final ObjectOwner _oo, final BuildingType _bt) {
		AddRmBuildingToTally(_oo, _bt, -1);
	}

	public void Reset() {
		PLAYER_IRON = utility.StartingResources;
		PLAYER_STONE = utility.StartingResources;
		PLAYER_WOOD = utility.StartingResources;
		ENEMY_IRON = utility.StartingResources;
		ENEMY_STONE = utility.StartingResources;
		ENEMY_WOOD = utility.StartingResources;

		PLAYER_MAX_SCISSORS = utility.starting_actors;
		PLAYER_MAX_ROCK = utility.starting_actors;
		PLAYER_MAX_PAPER = utility.starting_actors;
		ENEMY_MAX_SCISSORS = utility.starting_actors;
		ENEMY_MAX_ROCK = utility.starting_actors;
		ENEMY_MAX_PAPER = utility.starting_actors;
		PLAYER_SCISSORS = utility.starting_actors;
		PLAYER_ROCK = utility.starting_actors;
		PLAYER_PAPER = utility.starting_actors;
		ENEMY_SCISSORS = utility.starting_actors;
		ENEMY_ROCK = utility.starting_actors;
		ENEMY_PAPER = utility.starting_actors;

		PLAYER_SCORE = 0;
		ENEMY_SCORE = 0;

		GLOBAL_WOOD = 0;
		GLOBAL_STONE = 0;
		GLOBAL_IRON = 0;

		STAT_TreesChopped = 0;
		STAT_ProjectilesFired = 0;
		STAT_SpecialUnitsSpawned = 0;
		STAT_ResourcesPlundered = 0;
		STAT_BuildingsConstructed = 0;
		STAT_UnitsPoisoned = 0;
		STAT_RocksAssembled = 0;
		STAT_TroopsSlaughtered = 0;
		STAT_BuildingsExploded = 0;
	}

	public void ScorePoints(final ObjectOwner _oo, final int _points) {
		if (utility.gameMode != GameMode.gameOn) {
			return; //No scoring points when the game has ended!
		}
		if (_oo == ObjectOwner.Player) {
			PLAYER_SCORE += _points;
		} else {
			ENEMY_SCORE += _points;
		}
	}

	public void SetGeneratingPaper(final ObjectOwner _oo, final boolean _set) {
		if (_oo == ObjectOwner.Enemy) GEN_PAPER_ENEMY = _set;
		else GEN_PAPER_PLAYER = _set;
	}

	public void SetGeneratingRock(final ObjectOwner _oo, final boolean _set) {
		if (_oo == ObjectOwner.Enemy) GEN_ROCK_ENEMY = _set;
		else GEN_ROCK_PLAYER = _set;
	}

	public void SetGeneratingScissors(final ObjectOwner _oo, final boolean _set) {
		if (_oo == ObjectOwner.Enemy) GEN_SCISSORS_ENEMY = _set;
		else GEN_SCISSORS_PLAYER = _set;
	}

	public void TryToSpawnUnit(final Building _b) {
		final ObjectOwner owner = _b.GetOwner();
		final BuildingType type = _b.GetType();
		ActorType toPlace = null;
		if (owner == ObjectOwner.Player) {
			if (type == BuildingType.Woodshop
					&& PLAYER_PAPER < PLAYER_MAX_PAPER
					&& PLAYER_WOOD >= utility.COST_Paper_Wood
					&& GEN_PAPER_PLAYER == true) {
				toPlace = ActorType.Paper;
			} else if (type == BuildingType.Smelter
					&& PLAYER_SCISSORS < PLAYER_MAX_SCISSORS
					&& PLAYER_IRON >= utility.COST_Scissors_Iron
					&& GEN_SCISSORS_PLAYER == true) {
				toPlace = ActorType.Scissors;
			} else if (type == BuildingType.Rockery
					&& PLAYER_ROCK < PLAYER_MAX_ROCK
					&& PLAYER_STONE >= utility.COST_Rock_Stone
					&& GEN_ROCK_PLAYER == true) {
				toPlace = ActorType.Rock;
			}
		} else if (owner == ObjectOwner.Enemy) {
			if (type == BuildingType.Woodshop
					&& ENEMY_PAPER < ENEMY_MAX_PAPER
					&& ENEMY_WOOD >= utility.COST_Paper_Wood
					&& GEN_PAPER_ENEMY == true) {
				toPlace = ActorType.Paper;
			} else if (type == BuildingType.Smelter
					&& ENEMY_SCISSORS < ENEMY_MAX_SCISSORS
					&& ENEMY_IRON >= utility.COST_Scissors_Iron
					&& GEN_SCISSORS_ENEMY == true) {
				toPlace = ActorType.Scissors;
			} else if (type == BuildingType.Rockery
					&& ENEMY_ROCK < ENEMY_MAX_ROCK
					&& ENEMY_STONE >= utility.COST_Rock_Stone
					&& GEN_ROCK_ENEMY == true) {
				toPlace = ActorType.Rock;
			}
		}

		if (toPlace != null) {
			final WorldPoint where =  theSpriteManager.FindGoodSpot(_b.GetLoc(), _b.GetR(), utility.actor_look_for_spot_radius, false);
			if (where != null) {
				theSpriteManager.PlaceActor(where, toPlace, owner);
				if (owner == ObjectOwner.Player) {
					if (toPlace == ActorType.Paper) {
						++PLAYER_PAPER;
						PLAYER_WOOD -= utility.COST_Paper_Wood;
						ScorePoints(ObjectOwner.Player, utility.COST_Paper_Wood);
					} else if (toPlace == ActorType.Scissors) {
						++PLAYER_SCISSORS;
						PLAYER_IRON -= utility.COST_Scissors_Iron;
						ScorePoints(ObjectOwner.Player, utility.COST_Scissors_Iron);
					} else if (toPlace == ActorType.Rock) {
						++PLAYER_ROCK;
						PLAYER_STONE -= utility.COST_Rock_Stone;
						ScorePoints(ObjectOwner.Player, utility.COST_Rock_Stone);
					}
				} else if (owner == ObjectOwner.Enemy) {
					if (toPlace == ActorType.Paper) {
						++ENEMY_PAPER;
						ENEMY_WOOD -= utility.COST_Paper_Wood;
						ScorePoints(ObjectOwner.Enemy, utility.COST_Paper_Wood);
					} else if (toPlace == ActorType.Scissors) {
						++ENEMY_SCISSORS;
						ENEMY_IRON -= utility.COST_Scissors_Iron;
						ScorePoints(ObjectOwner.Enemy, utility.COST_Scissors_Iron);
					} else if (toPlace == ActorType.Rock) {
						++ENEMY_ROCK;
						ENEMY_STONE -= utility.COST_Rock_Stone;
						ScorePoints(ObjectOwner.Enemy, utility.COST_Rock_Stone);
					}
				}
			}
		}
	}

	public void UnitDeath(final ActorType _at, final ObjectOwner _oo) {
		if (_oo == ObjectOwner.Player) {
			if (_at == ActorType.Paper) {
				--PLAYER_PAPER;
			} else if (_at == ActorType.Rock) {
				--PLAYER_ROCK;
			} else if (_at == ActorType.Scissors) {
				--PLAYER_SCISSORS;
			}
		} else if (_oo == ObjectOwner.Enemy) {
			if (_at == ActorType.Paper) {
				--ENEMY_PAPER;
			} else if (_at == ActorType.Rock) {
				--ENEMY_ROCK;
			} else if (_at == ActorType.Scissors) {
				--ENEMY_SCISSORS;
			}
		}
	}


}
