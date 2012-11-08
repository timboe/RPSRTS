package com.timboe.rpsrts.sprites;

import java.util.ArrayList;
import java.util.HashSet;

import com.timboe.rpsrts.enumerators.ActorBehaviour;
import com.timboe.rpsrts.enumerators.ActorJob;
import com.timboe.rpsrts.enumerators.ActorType;
import com.timboe.rpsrts.enumerators.BuildingType;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.enumerators.PathfindStatus;
import com.timboe.rpsrts.enumerators.ResourceType;
import com.timboe.rpsrts.world.Pathfinder;
import com.timboe.rpsrts.world.WorldPoint;

public class Actor extends Sprite {
	protected ObjectOwner owner;
	protected ActorType type;
	protected ActorJob job; //what i do for a living
	protected ActorBehaviour behaviour; //my current activity
	protected Building boss; //building employed by
	protected HashSet<Building> previous_bad_employers;
	protected Sprite target; //building designated target
	protected Sprite attack_target; //Who i'm wailing on
	protected int attack_range;
	protected int poisoned = 0;
	protected int shrapnel = 0;

	protected float RPS;

	protected ArrayList<ResourceType> iCollect = new ArrayList<ResourceType>(); //things this actor collects
	protected ResourceType carrying; //resource type in hands
	protected int carryAmount; //amount in `hands'
	protected int strength; //amount actor can carry / attack

	protected boolean tick;
	protected boolean tock;

	protected ArrayList<WorldPoint> waypoint_list = null;  //pahfinding list
	protected Sprite destination; //final pathfind destination
	protected WorldPoint waypoint;  //current pathfind destination
	protected Thread pathfinding_thread = null;
	protected Pathfinder pathfinder = null;
	protected PathfindStatus navagate_status;
	protected int direction = 0;

	protected int stuck;
	protected int stuck_wander_ticks;
	protected WorldPoint wander;
	protected int tocks_since_quit;

	protected Actor(final int _ID, final int _x, final int _y, final int _r, final ActorType _at, final ObjectOwner _oo) {
		super(_ID, _x, _y, _r);
		owner = _oo;
		type = _at;
		waypoint = null;
		destination = null;
		wander = null;
		previous_bad_employers = new HashSet<Building>();
		SetJob(ActorJob.Idle, null);
		stuck = 0;
		navagate_status = PathfindStatus.NotRun;
		animSteps = 4;
		RPS = 1f;

		if (type == ActorType.Paper) {
			RPS = 2f;
			iCollect.add(ResourceType.Rockpile);
		} else if (type == ActorType.Rock) {
			RPS = 0.5f;
			iCollect.add(ResourceType.Mine);
		} else if (type == ActorType.Scissors) {
			RPS = 1f;
			iCollect.add(ResourceType.Cactus);
			iCollect.add(ResourceType.Tree);
		}
		speed = Math.round((float)utility.actor_speed * RPS);
		strength = Math.round((float)utility.actor_strength / RPS);
		maxHealth = Math.round((float)utility.actor_starting_health / RPS);
		ticks_per_tock = Math.round((float)ticks_per_tock * RPS);
		attack_range = utility.actor_attack_range;

		if (type == ActorType.Spock || type == ActorType.Lizard) {
			//spock = paper+scissors, lizard = paper+rock
			maxHealth = (int) (utility.actor_starting_health * utility.EXTRA_Scissors_PerSmelter * 2);
			strength = (int) (utility.actor_strength * utility.EXTRA_Scissors_PerSmelter * 2);
			ticks_per_tock = Math.round((float)ticks_per_tock * 0.5f);
		}

		health = maxHealth;
		animStep = utility.rndI(animSteps);
	}

	private boolean AttemptMove(final float _suggest_new_x, final float _suggest_new_y, final boolean force) {
		if (Math.round(_suggest_new_x) == x && Math.round(_suggest_new_y) == y) { //if sub pixel
			x_prec = _suggest_new_x;
			y_prec = _suggest_new_y;
			return true; //allowed
		}
		//System.out.println("TRY TO MOVE FROM ("+x+","+y+") TO ("+_suggest_new_x+","+_suggest_new_y+") for ID:"+ID+" and Hyp:"+_hypotenuse);
		int _ID = 0;
		if (destination != null) {
			_ID = destination.GetID();
		}
		//Building&Resouce=YES Actor=NO
		if (force == true || theSpriteManager.CheckSafe(true,false,Math.round(_suggest_new_x), Math.round(_suggest_new_y), 1, _ID, 0) == true) { //TODO Check the effect of reducing the radius of the collision check here
			x_prec = _suggest_new_x;
			y_prec = _suggest_new_y;
			x = Math.round(x_prec);
			y = Math.round(y_prec);
			//System.out.println("ALLOWED - NEW COORDINATES ("+x+","+y+") for ID:"+ID);
			++animStep;
			return true;
		}
		return false;
	}

	private void ClearDestination() {
		destination = null;
		waypoint = null;
		waypoint_list = null;
		if (pathfinder != null) {
			pathfinder.Kill();
		}
		pathfinding_thread = null;
		pathfinder = null;
		navagate_status = PathfindStatus.NotRun;
	}

	public synchronized Sprite GetAttackTarget() {
		return attack_target;
	}

	public ArrayList<ResourceType> GetCollects(){
		return iCollect;
	}

	public boolean GetFacingWest() {
		//The lizard sprite is east/west dependent
		WorldPoint dest;
		if (waypoint != null) { 
			dest = waypoint;
		} else if (wander != null) {
			dest = wander;
		} else {
			return true;
		}
		if ( ((GetX() - dest.getX()) * Math.cos(utility.rotateAngle)) + ((GetY() - dest.getY()) * Math.sin(utility.rotateAngle)) > 0) {
			return true;
		}
		return false;
	}
	
	public boolean GetIfPreferedTarget(final Sprite to_compare) {
		if (to_compare.GetIsActor() == true) {
			if (type == ActorType.Paper && ((Actor) to_compare).GetType() == ActorType.Rock ) return true;
			else if (type == ActorType.Paper && ((Actor) to_compare).GetType() == ActorType.Spock ) return true;
			else if (type == ActorType.Rock && ((Actor) to_compare).GetType() == ActorType.Scissors ) return true;
			else if (type == ActorType.Rock && ((Actor) to_compare).GetType() == ActorType.Lizard ) return true;
			else if (type == ActorType.Scissors && ((Actor) to_compare).GetType() == ActorType.Paper ) return true;
			else if (type == ActorType.Scissors && ((Actor) to_compare).GetType() == ActorType.Lizard ) return true;
			else if (type == ActorType.Spock && ((Actor) to_compare).GetType() == ActorType.Rock ) return true;
			else if (type == ActorType.Spock && ((Actor) to_compare).GetType() == ActorType.Scissors ) return true;
			else if (type == ActorType.Lizard && ((Actor) to_compare).GetType() == ActorType.Spock ) return true;
			else if (type == ActorType.Lizard && ((Actor) to_compare).GetType() == ActorType.Paper ) return true;
		} else if (to_compare.GetIsBuilding() == true) {
			if (type == ActorType.Paper && ((Building) to_compare).GetType() == BuildingType.Rockery ) return true;
			else if (type == ActorType.Rock && ((Building) to_compare).GetType() == BuildingType.Smelter ) return true;
			else if (type == ActorType.Scissors && ((Building) to_compare).GetType() == BuildingType.Woodshop ) return true;
			else if (type == ActorType.Spock && ((Building) to_compare).GetType() == BuildingType.Smelter) return true;
			else if (type == ActorType.Spock && ((Building) to_compare).GetType() == BuildingType.Rockery) return true;
			else if (type == ActorType.Lizard && ((Building) to_compare).GetType() == BuildingType.Woodshop) return true;
		}
		return false;
	}

	@Override
	public boolean GetIsActor() {
		return true;
	}

	public ActorJob GetJob() {
		return job;
	}

	public HashSet<Building> GetLastEmployer() {
		return previous_bad_employers;
	}

	@Override
	public ObjectOwner GetOwner() {
		return owner;
	}

	public int GetStrength() {
		return strength;
	}

	public ActorType GetType() {
		return type;
	}

	private void Job_Attack(){
		if (attack_target.GetDead() == true) {
			attack_target = null;
			return;
		}
		if (tick == true) {
			if (wander != null
					|| utility.Seperation(GetLoc(), attack_target.GetLoc()) > attack_range
					|| utility.rnd() < (0.001f * RPS)) { //Binomial prob for paper at 60 TPS of this firing is 6%
				WanderAbout(attack_target, attack_range/2, attack_range/4);
			}
		}
		if (tock == true) {
			final float _sep = utility.Seperation(GetLoc(), attack_target.GetLoc());
			if (_sep > 2 * attack_range) {
				attack_target = null;
				return;
			} else if (_sep > attack_range) {
				return;
			}
			//in range -- attack!
			++animStep;
			theSpriteManager.PlaceProjectile(this, attack_target);
		}
	}

	private void Job_Build() {
		if (boss == null || boss.GetBeingBuilt() == false) { /* SAFETY CHECK */
			//System.out.println("FATAL Boss is dead or building is constructed");
			QuitJob(true);
		}
		if (tick == true && behaviour == ActorBehaviour.DoingNothing) { //Get a new assignment - come to build
			if (navagate_status == PathfindStatus.NotRun) {
				SetDestinationInitial(boss);
				return;
			} else if (navagate_status == PathfindStatus.Failed) {//Navagation failed - I quit!
				QuitJob(false);
			} else if (navagate_status == PathfindStatus.Passed) { //navagation good
				behaviour = ActorBehaviour.MovingToTarget;
			}
		} else if (tick == true && behaviour == ActorBehaviour.MovingToTarget) {
			final boolean arrivedAtTarget = Move();
			if (arrivedAtTarget == true) {
				behaviour = ActorBehaviour.Constructing;
			}
		} else if (tock == true && behaviour == ActorBehaviour.Constructing) {
			boss.BuildAction();
			++animStep;
		}
	}

	private void Job_Gather() {
		if (boss == null) { /* SAFETY CHECK */
			//System.out.println("FATAL Boss is dead, quit gathering job");
			QuitJob(false);
		}
		if (target == null || ((Resource) target).GetRemaining() == 0) { /* SAFETY CHECK */
			ClearDestination();
			if (carryAmount == strength && boss.getiCollect().contains(carrying)) {
				//System.out.println("WARN Resource node gone, but i have stuff to depost! Taking it back");
				behaviour = ActorBehaviour.DroppingOffGoods;
			} else { //Get a new job
				//System.out.println("WARN Resource is dead get new Gather Job");
				behaviour = ActorBehaviour.DoingNothing;
			}
		}
		if (tick == true && behaviour == ActorBehaviour.DoingNothing) { //Get a new assignment
			if (carryAmount >= strength) {
				behaviour = ActorBehaviour.ReturningFromTarget;
				return;
			}
			target = boss.GetNewGatherTask(this);
			if (target == null) { //I quit!
				//System.out.println("FATAL Boss unable to assing task, quitting job");
				QuitJob(false);
			} else {
				//System.out.println("INFO Boss issued goal node: ("+target.GetX()+","+target.GetY()+")");
				if (navagate_status == PathfindStatus.NotRun) { //Not yet tried
					//Begin pathfinding
					SetDestinationInitial(target);
					return;
				} else if (navagate_status == PathfindStatus.Failed) {//Navagation failed - I quit!
					//System.out.println("FATAL Pathfinder unable to get me to nearest node, quitting job - flaggin node as unreachable");
					((Resource) target).SetUnreachable();
					QuitJob(false);
				} else if (navagate_status == PathfindStatus.Passed) { //navagation good
					//System.out.println("INFO Navagating to ("+target.GetX()+","+target.GetY()+") to gather resources");
					behaviour = ActorBehaviour.MovingToTarget;
				}
			}
		} else if (tick == true && behaviour == ActorBehaviour.MovingToTarget) {
			final boolean arrivedAtTarget = Move();
			if (arrivedAtTarget == true) {
				//System.out.println("INFO Arrived at node, gathering commencing");
				behaviour = ActorBehaviour.Gathering;
			}
		} else if (tock == true && behaviour == ActorBehaviour.Gathering) {
			final ResourceType nodeType = ((Resource) target).GetType();
			if (nodeType != carrying) {
				//System.out.println("INFO Switching actor to resource type "+nodeType);
				if (nodeType == ResourceType.Cactus && carrying == ResourceType.Tree) {}
				else if (nodeType == ResourceType.Tree && carrying == ResourceType.Cactus) {} else {
					carryAmount = 0;
				}
				carrying = nodeType;
			}
			final int amountGathered = ((Resource) target).Plunder(utility.amount_collect_per_tock);
			++animStep;
			//System.out.println("Gathered "+amountGathered);
			carryAmount += amountGathered;
			if (carryAmount >= strength) {
				//System.out.println("INFO Gatherer has its fill, returning to gather target");
				behaviour = ActorBehaviour.ReturningFromTarget;
			}
		} else if (tick == true && behaviour == ActorBehaviour.ReturningFromTarget) {
			if (destination == null) {
				if (navagate_status == PathfindStatus.NotRun) { //Not yet tried
					SetDestinationInitial(boss);
				} else if (navagate_status == PathfindStatus.Failed) {
					QuitJob(false);
				}
			} else {
				final boolean arrivedAtTarget = Move();
				if (arrivedAtTarget == true) {
					behaviour = ActorBehaviour.DroppingOffGoods;
				}
			}
		} else if (tock == true && behaviour == ActorBehaviour.DroppingOffGoods) {
			if (carryAmount > utility.amount_collect_per_tock) {
				//System.out.println("Dropping off "+amount_collect_per_tock);
				resource_manager.AddResources(carrying, utility.amount_collect_per_tock, owner);
				carryAmount -= utility.amount_collect_per_tock;
				++animStep;
			} else if (carryAmount > 0) {
				resource_manager.AddResources(carrying, carryAmount, owner);
				carryAmount = 0;
			} else {
				target = null;
			}
		}
	}

	private void Job_Guard() {
		if (tick == true && behaviour == ActorBehaviour.DoingNothing) { //Get a new assignment - come to build
			if (navagate_status == PathfindStatus.NotRun) {
				SetDestinationInitial(boss);
				return;
			} else if (navagate_status == PathfindStatus.Failed) {//Navagation failed - I quit!
				System.out.println("#################### QUIT GUARD INITIAL PATHFIND Job:"+job+" boss:"+boss.GetType()+" behaviour:"+behaviour+" navStat:"+navagate_status+" WPL:"+waypoint_list+" OO:"+owner);
				QuitJob(false);
			} else if (navagate_status == PathfindStatus.Passed) { //navagation good
				behaviour = ActorBehaviour.MovingToTarget;
			}
		} else if (tick == true && behaviour == ActorBehaviour.MovingToTarget) {
			final boolean arrivedAtTarget = Move();
			if (arrivedAtTarget == true) {
				behaviour = ActorBehaviour.Guarding;
			}
		} else if (tick == true && behaviour == ActorBehaviour.Guarding) {
			if (utility.Seperation(GetLoc(), boss.GetLoc()) > 2 * utility.wander_radius) {
				Job_Guard_Renavagate();
				return;
			}
			WanderAbout(boss, utility.wander_radius, utility.wander_pull_to_target);
		}
	}

	public synchronized void Job_Guard_Renavagate() {
		behaviour = ActorBehaviour.DoingNothing;
		navagate_status = PathfindStatus.NotRun;
	}

	private void Job_Idle() {
		if (tick == true) {
			WanderAbout(theSpriteManager.GetBase(owner), utility.wander_radius, utility.wander_pull_to_target);
		}
		//TODO Add code here to try and drop off resources
	}

	private void Job_Stuck() {
		if (tick == true) {
			--stuck_wander_ticks;
			WanderAbout(destination, utility.wander_radius, utility.wander_pull_to_target); //Wander about here - pull to destination
			if (stuck_wander_ticks == 0) {
				SetDestination(destination);
			}
		}
	}

	@Override
	public synchronized void Kill() {
		if (dead == true) return;
		theSpriteManager.PlaceSpooge(x, y, GetOwner(), utility.spooges_actor_death, utility.spooges_scale_actor_death);
		QuitJob(true);
		dead = true;
		resource_manager.UnitDeath(type,owner);
	}

	private boolean Move() { //Goal in mind pathfinding
		if (waypoint != null) { //Have at least one destination in list
			//System.out.println("moveLoop FIRST WAYPOINT WP("+waypoint.getX()+","+waypoint.getY()+") POS ("+x_prec+","+y_prec+")");
			final float _hypotenuse = utility.Seperation(x_prec, waypoint.getX(), y_prec, waypoint.getY()); //
			final float radiusToAchieve = 1f; //If pathfinding to pathfind node
			if (_hypotenuse <= radiusToAchieve) { //At waypoint?
				//Is there another waypoint?
				if (waypoint_list != null && waypoint_list.size() > 0) {
					waypoint = waypoint_list.remove( waypoint_list.size() - 1 );
				} else {
					//OK, so no more waypoints - are we there?
					if (utility.Seperation(GetLoc(), destination.GetLoc()) < r + destination.GetR() + (utility.tiles_size * 2)) {
						//System.out.println("INFO REACHED FINAL DESTINATION");
						ClearDestination();
						return true;
					} else {
						//NOPE, MUST HAVE MOVED!
						SetDestination(destination);
					}
				}
				return false;
			}

			final float _suggest_new_x = x_prec - (((x_prec - waypoint.getX()) / _hypotenuse) * (speed * ( 1 - ((carryAmount/strength)/2.f))) );
			final float _suggest_new_y = y_prec - (((y_prec - waypoint.getY()) / _hypotenuse) * (speed * ( 1 - ((carryAmount/strength)/2.f))) );
			final boolean allowed = AttemptMove(_suggest_new_x, _suggest_new_y, true); //FORCE as following waypoints
			if (allowed == false) {
				++stuck;
				if (stuck == 8) {
					//System.out.println("FATAL OBEJCT BLOCKED with ID:"+ID+" QuitJob, STUCK="+stuck);
					QuitJob(false);
				} else if (stuck % 2 == 0) {
					//System.out.println("PERSISTANT OBEJCT BLOCKED with ID:"+ID+" TRYING TO WANDER, STUCK="+stuck);
					stuck_wander_ticks = 100;
				} else {
					//System.out.println("OBEJCT BLOCKED with ID:"+ID+" RENAVAGATE. STUCK="+stuck);
					SetDestination(destination);
				}
			}
		} else {
			//TESTING - GO WANDER
			//Point p = new Point();
			//if (p != null) SetDestination(p);
		}
		return false;
	}


	public synchronized void Poison() {
		poisoned += utility.actor_poison_ticks;
	}

	public synchronized void QuitJob(final boolean resign) {
		ClearDestination();
		if (resign == false) {
			tocks_since_quit = utility.tocks_before_retake_job_from_boss;
			if (boss != null) {
				previous_bad_employers.add(boss);
			}
		}
		if (boss != null) {
			boss.RemoveEmployee(this);
		}
		SetJob(ActorJob.Idle, null);
	}

	public synchronized void SetDestination(final Sprite _d) {
		wander = null;
		navagate_status = PathfindStatus.NotRun;
		pathfinder = new Pathfinder(this, _d);
		pathfinding_thread = new Thread(pathfinder);
		pathfinding_thread.start();
		destination = _d;
	}

	public synchronized void SetDestinationInitial(final Sprite _d) {
		stuck = 0;
		SetDestination(_d);
	}

	public synchronized void SetJob(final ActorJob _j, final Building _boss) {
		//if (job == ActorJob.Guard) System.out.println("@@@@@@@@@@@@@@@@@@@ Assigning a new job to current guard Job:"+_j+" Boss:"+_boss);
		job = _j;
		boss = _boss;
		target = null;
		if (boss != null) {
			boss.AddEmployee(this);
		}
		behaviour = ActorBehaviour.DoingNothing;
	}

	public synchronized void SetNemesis(final Sprite _nemesis) {
		attack_target = _nemesis;
		wander = null;
	}

	public synchronized void Shrapnel() {
		shrapnel += utility.building_Explode_ticks;
	}

	public synchronized void Tick(final int _tick_count) {
		if ((_tick_count + tick_offset) % ticks_per_tock == 0) Tock();
		tick = true;
		tock = false;
		if (dead == true) return;
		if (boss != null && boss.GetDead() == true) {
			QuitJob(false);
		}
		if (pathfinding_thread != null) {
			//Am currently path finding
			if (pathfinding_thread.isAlive() == true) return;
			else {
				//System.out.println("PATHFINDING DONE! Result is:" + pathfinder.GetResult());
				waypoint_list = pathfinder.GetResult();
				if (waypoint_list == null) {
					navagate_status = PathfindStatus.Failed;
					destination = null;
					waypoint = null;
				} else {
					waypoint = waypoint_list.get( waypoint_list.size() - 1 );
					navagate_status = PathfindStatus.Passed;
				}
				pathfinder = null;
				pathfinding_thread = null;
			}
		}
		if (attack_target != null) { //FORGET ABOUT THAT BORING JOB - IT'S FIGHT TIME
			Job_Attack();
		////////////////////////////////////////////////////////////////////////////////////////
	    } else if (stuck_wander_ticks > 0) { //I'm stuck! have a random walk before re-pathfinding
	    	Job_Stuck();
	    } else if (job == ActorJob.Idle) {
	    	Job_Idle();
		} else if (job == ActorJob.Guard) {
			Job_Guard();
		} else if (job == ActorJob.Builder) {
			Job_Build();
		} else if (job == ActorJob.Gather) {
			Job_Gather();
		}
		////////////////////////////////////////////////////////////////////////////////////////////
		if (poisoned > 0) {
			--poisoned;
			Attack(utility.actor_poison_rate);
		}
		if (shrapnel > 0) {
			--shrapnel;
			Attack(utility.building_Explode_damage);
		}

	}

	private void Tock() {
		tock = true;
		tick = false;
		if (tocks_since_quit > 0) { //Chill out period before I can be hired by the same building again
			--tocks_since_quit;
			if (tocks_since_quit == 0) {
				previous_bad_employers.clear();
			}
		}
		/////////////////////////////////////////////////////////////////////////////////
		if (attack_target != null) {
			Job_Attack();
	    } else if (job == ActorJob.Builder) {
	    	Job_Build();
		} else if (job == ActorJob.Gather) {
			Job_Gather();
		}
		/////////////////////////////////////////////////////////////////////////////////
		UnStick();

		if (attack_target == null && health < maxHealth) ++health;
	}

	private void UnStick() {
		//If i have been left somewhere which should not be occupiable
		if (waypoint == null && theSpriteManager.CheckSafe(true,false,Math.round(x_prec), Math.round(y_prec), 1, 0, 0) == false) { //My current pixel is BAD
			final int max_search = 50;
			for (int search_area = 1; search_area <= max_search; search_area += 2) {
				//Try and unstick me!
				final WorldPoint jumpTo = theSpriteManager.FindGoodSpot( GetLoc(), r, search_area, false);
				if (jumpTo != null) {
					if (AttemptMove(jumpTo.getX(), jumpTo.getY(), false) == true) {
						//System.out.println(GetOwner()+" "+GetType()+" has been unstuck at search radius "+search_area+".");
						return;
					}
				}
			}
			System.out.println(GetOwner()+" "+GetType()+" has FAILED TO BE UNSTUCK SEARCHING UP TO  "+max_search+".");
		}
	}

	private void WanderAbout (final Sprite _target, final int _wander_radius, final int _pull) { //target optional - random walk towards target
		if (wander == null) {
			int pull_x = (int) (x_prec - _wander_radius + utility.rndI(_wander_radius*2));
			int pull_y = (int) (y_prec - _wander_radius + utility.rndI(_wander_radius*2));
			if (_target != null) {
				if (x_prec > _target.GetX()) {
					pull_x -= _pull;
				} else {
					pull_x += _pull;
				}
				if (y_prec > _target.GetY()) {
					pull_y -= _pull;
				} else {
					pull_y += _pull;
				}
			}
			final WorldPoint _p = new WorldPoint(pull_x, pull_y);
			wander = theSpriteManager.FindGoodSpot( _p, r, utility.wander_radius, false);
			if (wander == null) return;
		}
		final float _hypotenuse = utility.Seperation(x_prec, wander.getX(), y_prec, wander.getY());
		if (_hypotenuse <= r) {
			wander = null;
			return;
		}
		final float _suggest_new_x = x_prec - (((x_prec - wander.getX()) / _hypotenuse) * (speed * ( 1 - ((carryAmount/strength)/2.f))) );
		final float _suggest_new_y = y_prec - (((y_prec - wander.getY()) / _hypotenuse) * (speed * ( 1 - ((carryAmount/strength)/2.f))) );
		final boolean allowed = AttemptMove(_suggest_new_x, _suggest_new_y, false); //Don't FORCE
		if (allowed == false) {
			wander = null;
		}
	}
}

