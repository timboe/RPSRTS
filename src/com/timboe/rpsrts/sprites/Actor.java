package com.timboe.rpsrts.sprites;

import java.util.HashSet;
import java.util.Vector;

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
	ActorJob job; //what i do for a living
	ActorBehaviour behaviour; //my current activity
	Building boss; //building employed by
	HashSet<Building> previous_bad_employers;
	Sprite target; //building designated target
	public Sprite attack_target; //Who i'm wailing on
	int attack_range;

	float RPS;
	
	Vector<ResourceType> iCollect = new Vector<ResourceType>(); //things this actor collects
	ResourceType carrying; //resource type in hands
	protected int carryAmount; //amount in `hands'
	protected int strength; //amount actor can carry / attack

	boolean tick;
	boolean tock;
	
	protected Vector<WorldPoint> waypoint_list_sync = null;  //pahfinding list ---may need thread protection---
	protected Sprite destination; //final pathfind destination
	protected WorldPoint waypoint;  //current pathfind destination
	Thread pathfinding_thread = null;
	Pathfinder pathfinder = null;
	PathfindStatus navagate_status;
	int direction = 0;

	int stuck;
	int stuck_wander_ticks;
	protected WorldPoint wander;
	int tocks_since_quit;

	protected Actor(int _ID, int _x, int _y, int _r, ActorType _at, ObjectOwner _oo) {
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
		speed = utility.actor_speed * RPS;
		strength = (int) (utility.actor_strength / RPS);
		maxHealth = (int) (utility.actor_starting_health / RPS);
		ticks_per_tock *= RPS;
		attack_range = utility.actor_attack_range;

		
		health = maxHealth;
		animStep = utility.rndI(animSteps);
	}

	private boolean AttemptMove(float _suggest_new_x, float _suggest_new_y) {
		if ((int) Math.round(_suggest_new_x) == x && (int) Math.round(_suggest_new_y) == y) { //if sub pixel
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
		if (theSpriteManager.CheckSafe(true,false,(int) Math.round(_suggest_new_x), (int) Math.round(_suggest_new_y), 1, _ID, 0) == true) { //TODO Check the effect of reducing the radius of the collision check here
			x_prec = _suggest_new_x;
			y_prec = _suggest_new_y;
			x = (int) Math.round(x_prec);
			y = (int) Math.round(y_prec);
			//System.out.println("ALLOWED - NEW COORDINATES ("+x+","+y+") for ID:"+ID);
			++animStep;
			return true;
		}
		return false;
	}

	public void ClearDestination() {
		destination = null;
		waypoint = null;
		waypoint_list_sync = null;
		if (pathfinder != null) {
			pathfinder.Kill();
		}
		pathfinding_thread = null;
		pathfinder = null;
		navagate_status = PathfindStatus.NotRun;
	}

	public Vector<ResourceType> GetCollects(){
		return iCollect;
	}

	public boolean GetIfPreferedTarget(Sprite to_compare) {
		if (to_compare.GetIsActor() == true) {
			if (type == ActorType.Paper && ((Actor) to_compare).GetType() == ActorType.Rock ) return true;
			else if (type == ActorType.Rock && ((Actor) to_compare).GetType() == ActorType.Scissors ) return true;
			else if (type == ActorType.Scissors && ((Actor) to_compare).GetType() == ActorType.Paper ) return true;
		} else if (to_compare.GetIsBuilding() == true) {
			if (type == ActorType.Paper && ((Building) to_compare).GetType() == BuildingType.Rockery ) return true;
			else if (type == ActorType.Rock && ((Building) to_compare).GetType() == BuildingType.Smelter ) return true;
			else if (type == ActorType.Scissors && ((Building) to_compare).GetType() == BuildingType.Woodshop ) return true;
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
					|| utility.rnd() < (0.05f * RPS)) { //TODO tweak this range, currently paper wander 20% tick
				//if (wander == null) wander = theSpriteManager.FindGoodSpot(attack_target.GetLoc(), r, attack_range, false);
				WanderAbout(attack_target, attack_range, attack_range/2);
			}
		} else if (tock == true) {
			final float _sep = utility.Seperation(GetLoc(), attack_target.GetLoc());
			if (_sep > 2 * attack_range) {
				attack_target = null;
				return;
			} else if (_sep > attack_range)
				return;
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
	
	public void Job_Gather() {
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
			if (((Resource) target).GetRemaining() == 0) {
				target.Kill();
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
				behaviour = ActorBehaviour.DoingNothing;
				return;
			}
			WanderAbout(boss, utility.wander_radius, utility.wander_pull_to_target);
		}
	}
	
	private void Job_Idle() {
		if (tick == true) {
			WanderAbout(theSpriteManager.GetBase(owner), utility.wander_radius, utility.wander_pull_to_target);
		}
		//TODO Add code here to try and drop off resources
	}
	
	public void Job_Stuck() {
		if (tick == true) {
			--stuck_wander_ticks;
			WanderAbout(destination, utility.wander_radius, utility.wander_pull_to_target); //Wander about here - pull to destination
			if (stuck_wander_ticks == 0) {
				SetDestination(destination);
			}
		}
	}
	
	@Override
	public void Kill() {
		if (dead == true) return;
		theSpriteManager.PlaceSpooge(x, y, GetOwner(), utility.spooges_actor_death, utility.spooges_scale_actor_death);
		QuitJob(true);
		dead = true;
		resource_manager.UnitDeath(type,owner);
	}

	public boolean Move() { //Goal in mind pathfinding
		if (waypoint != null) { //Have at least one destination in list
			//System.out.println("moveLoop FIRST WAYPOINT WP("+waypoint.getX()+","+waypoint.getY()+") POS ("+x_prec+","+y_prec+")");
			final float _hypotenuse = utility.Seperation(x_prec, waypoint.getX(), y_prec, waypoint.getY()); //  // Math.sqrt( Math.pow(x_prec - destination_list.lastElement().getX(),2) +  Math.pow(y_prec - destination_list.lastElement().getY(),2) );
			float radiusToAchieve = 1f; //If pathfinding to pathfind node
			//synchronized (waypoint_list_sync) {
				if (waypoint_list_sync == null || waypoint_list_sync.size() == 0)	 {
					radiusToAchieve = utility.tiles_size + r + destination.GetR(); //If final target (pathfinding accuracy now tiles_size)
				}
			//}
			if (_hypotenuse <= radiusToAchieve) { //At waypoint?
				//Is there another waypoint?
				//synchronized (waypoint_list_sync) {
					if (waypoint_list_sync != null && waypoint_list_sync.size() > 0) {
						//Set to go here - next waypoint
						waypoint = ((Vector<WorldPoint>) waypoint_list_sync).lastElement();
						waypoint_list_sync.remove( waypoint_list_sync.size() - 1 );
					} else {
						//OK, so no more waypoints - are we there?
						if (utility.Seperation(waypoint, destination.GetLoc()) < r + destination.GetR()) {
							//Reached final destination
							//System.out.println("INFO REACHED FINAL DESTINATION");
							ClearDestination();
							return true;
						} else {
							//NOPE, GO STRAIGHT LINE - IF WE HIT SOMETHING THEN ROUTE GETS RECALCULATED
							waypoint = destination.GetLoc();
						}
					}
				//}
				return false;
			}

			final float _suggest_new_x = x_prec - (((x_prec - waypoint.getX()) / _hypotenuse) * (speed * ( 1 - ((carryAmount/strength)/2.f))) );
			final float _suggest_new_y = y_prec - (((y_prec - waypoint.getY()) / _hypotenuse) * (speed * ( 1 - ((carryAmount/strength)/2.f))) );
			final boolean allowed = AttemptMove(_suggest_new_x, _suggest_new_y);
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

	public void QuitJob(boolean resign) {
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


	public void SetDestination(Sprite _d) {
		//waypoint_list = theSpriteManager.GetPath(this, _d);
		wander = null;
		navagate_status = PathfindStatus.NotRun;

		pathfinder = new Pathfinder(this, _d);
		pathfinding_thread = new Thread(pathfinder);
		pathfinding_thread.start();

		destination = _d;
	}

	public void SetDestinationInitial(Sprite _d) {
		stuck = 0;
		SetDestination(_d);
	}

	public void SetJob(ActorJob _j, Building _boss) {
		job = _j;
		boss = _boss;
		target = null;
		if (boss != null) {
			boss.AddEmployee(this);
		}
		behaviour = ActorBehaviour.DoingNothing;
	}

	public void SetNemesis(Sprite _nemesis) {
		attack_target = _nemesis;
		wander = null;
	}

	public void Tick(int _tick_count) {
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
				waypoint_list_sync = pathfinder.GetResult();
				if (waypoint_list_sync == null) {
					navagate_status = PathfindStatus.Failed;
					destination = null;
					waypoint = null;
				} else {
					synchronized (waypoint_list_sync) {
						waypoint = ((Vector<WorldPoint>) waypoint_list_sync).lastElement();
					}
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
	}

	public void Tock() {
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
		/////////////////////////////////////////////////////////////////////////////////
		} else if (job == ActorJob.Gather) {
			Job_Gather();
		}
		if (attack_target == null && health < maxHealth) ++health;
	}

	public void WanderAbout (Sprite _target, int _wander_radius, int _pull) { //target optional - random walk towards target
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
			//System.out.println("wander to ("+wander.getX()+","+wander.getY()+")");
		}
		final float _hypotenuse = utility.Seperation(x_prec, wander.getX(), y_prec, wander.getY());
		if (_hypotenuse <= r) {
			wander = null;
			return;
		}
		final float _suggest_new_x = x_prec - (((x_prec - wander.getX()) / _hypotenuse) * (speed * ( 1 - ((carryAmount/strength)/2.f))) );
		final float _suggest_new_y = y_prec - (((y_prec - wander.getY()) / _hypotenuse) * (speed * ( 1 - ((carryAmount/strength)/2.f))) );
		final boolean allowed = AttemptMove(_suggest_new_x, _suggest_new_y);
		if (allowed == false) {
			wander = null;
		}
	}
}

