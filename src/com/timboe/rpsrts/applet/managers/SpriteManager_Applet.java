package com.timboe.rpsrts.applet.managers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.timboe.rpsrts.applet.sprites.Actor_Applet;
import com.timboe.rpsrts.applet.sprites.Building_Applet;
import com.timboe.rpsrts.applet.sprites.Explosion_Applet;
import com.timboe.rpsrts.applet.sprites.Projectile_Applet;
import com.timboe.rpsrts.applet.sprites.Resource_Applet;
import com.timboe.rpsrts.applet.sprites.SpecialSpawn_Applet;
import com.timboe.rpsrts.applet.sprites.Spoogicles_Applet;
import com.timboe.rpsrts.applet.sprites.WaterfallSplash_Applet;
import com.timboe.rpsrts.enumerators.ActorType;
import com.timboe.rpsrts.enumerators.BuildingType;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.enumerators.ResourceType;
import com.timboe.rpsrts.managers.SpriteManager;
import com.timboe.rpsrts.sprites.Actor;
import com.timboe.rpsrts.sprites.Building;
import com.timboe.rpsrts.sprites.Explosion;
import com.timboe.rpsrts.sprites.Projectile;
import com.timboe.rpsrts.sprites.Resource;
import com.timboe.rpsrts.sprites.SpecialSpawn;
import com.timboe.rpsrts.sprites.Spoogicles;
import com.timboe.rpsrts.sprites.Sprite;
import com.timboe.rpsrts.sprites.WaterfallSplash;
import com.timboe.rpsrts.world.WorldPoint;

public class SpriteManager_Applet extends SpriteManager {
	private static SpriteManager_Applet singleton = new SpriteManager_Applet();
	public static SpriteManager_Applet GetSpriteManager_Applet() {
		return singleton;
	}

	private final Bitmaps_Applet theBitmaps = Bitmaps_Applet.GetBitmaps_Applet();
	private final ShapeStore theShapeStore = ShapeStore.GetShapeStore();
	private final TransformStore theTransforms = TransformStore.GetTransformStore();

	private SpriteManager_Applet() {
		super();
		this_object = this;
		System.out.println("--- Sprite Manager spawned (depends on Util,World,Path,Resource[linked on Reset()]) : "+this);
	}

	@Override
	protected Explosion PlatformSpecific_Explosion(final int _x, final int _y, final ObjectOwner _oo) {
		return new Explosion_Applet(++GlobalSpriteCounter, _x, _y, utility.building_Explode_radius, _oo) ;
	}
	@Override
	protected Actor PlatFormSpecific_PlaceActor(final WorldPoint _p, final ActorType _at, final ObjectOwner _o) {
		return new Actor_Applet(++GlobalSpriteCounter, _p.getX(), _p.getY(), utility.actorRadius, _at, _o);
	}
	@Override
	protected Building PlatformSpecific_PlaceBuilding(final WorldPoint _p, final int _r, final BuildingType _bt, final ObjectOwner _oo) {
		return new Building_Applet(++GlobalSpriteCounter, _p.getX(), _p.getY(), _r, _bt, _oo);
	}
	@Override
	protected Projectile PlatformSpecific_PlaceProjectile(final Actor _source, final Sprite _target, final int _r) {
		return new Projectile_Applet(++GlobalSpriteCounter, _source, _r, _target);
	}
	@Override
	protected Resource PlatformSpecific_PlaceResource(final WorldPoint _p, final ResourceType _rt) {
		return new Resource_Applet(++GlobalSpriteCounter, _p.getX(), _p.getY(), utility.resourceRadius, _rt);
	}
	@Override
	protected Spoogicles PlatformSpecific_PlaceSpooge(final int _x, final int _y, final ObjectOwner _oo, final int _n, final float _scale) {
		return new Spoogicles_Applet(++GlobalSpriteCounter, _x, _y, _oo, _n, _scale);
	}
	@Override
	protected WaterfallSplash PlatformSpecific_PlaceWaterfallSplash(final int _x, final int _y, final int _r) {
		return new WaterfallSplash_Applet(++GlobalSpriteCounter, _x, _y, _r);
	}
	@Override
	protected SpecialSpawn PlatformSpecific_SpecialSpawn(final int _x, final int _y, final int _r, final ActorType _at, final ObjectOwner _oo) {
		return new SpecialSpawn_Applet(++GlobalSpriteCounter, _x, _y, _r, _at, _oo);
	}

	public void Render(final Graphics2D _g2) {
		++FrameCount;
		//Waterfall splashs' are rendered by the Scene Drawer (we just look after them here with other sprites)
		for (final Sprite _Z : GetSpritesZOrdered()) {
			if (_Z.GetIsActor() == true) {
				((Actor_Applet) _Z).Render(_g2, FrameCount);
			} else if (_Z.GetIsResource() == true) {
				((Resource_Applet) _Z).Render(_g2, FrameCount);
			} else if (_Z.GetIsBuilding() == true) {
				((Building_Applet) _Z).Render(_g2, FrameCount);
			} else if (_Z.GetIsProjectile() == true) {
				((Projectile_Applet) _Z).Render(_g2, FrameCount);
			} else if (_Z.GetIsSpoogicle() == true) {
				((Spoogicles_Applet) _Z).Render(_g2, FrameCount);
			} else if (_Z.GetIsSpecialSpawn() == true) {
				((SpecialSpawn_Applet) _Z).Render(_g2, FrameCount);
			} else if (_Z.GetIsExplosion()) {
				((Explosion_Applet) _Z).Render(_g2, FrameCount);
			}
		}
		if (utility.dbg == true && theAI != null && enemy_base != null) {
			_g2.setTransform(theTransforms.af);
			float _r = theAI.GetDefenceRadius();
			_g2.setColor(Color.white);
			_g2.drawOval((int)(enemy_base.GetX() - _r), (int)(enemy_base.GetY() - _r), (int)(2*_r), (int)(2*_r));
		}
		if (utility.dbg == true && theHumanAI != null && player_base != null) {
			_g2.setTransform(theTransforms.af);
			float _r = theHumanAI.GetDefenceRadius();
			_g2.setColor(Color.white);
			_g2.drawOval((int)(player_base.GetX() - _r), (int)(player_base.GetY() - _r), (int)(2*_r), (int)(2*_r));
		}

	}

	public void SpecialRender(final Graphics2D _g2, final int _x, final int _y, final BufferedImage[] _graphic, final boolean drawingTopBar) {
		Point2D transform = null;
		transform = theTransforms.af_shear_rotate.transform(new Point(_x, _y), transform);
		final int __x = (int)Math.round(transform.getX());
		final int __y = (int)Math.round(transform.getY());

		int _r = 0;
		int _y_offset = 0;
		final int animSteps = 4;
		if (_graphic == theBitmaps.attractor_paper_player
				||  _graphic == theBitmaps.attractor_scissors_player
				||  _graphic == theBitmaps.attractor_rock_player) {
			_r = 4;
			_y_offset = 14;
		} else if (_graphic == theBitmaps.off || _graphic == theBitmaps.on) {
			_r = 6;
			_y_offset = -6;
		} else if (_graphic == theBitmaps.paper_player
				|| _graphic == theBitmaps.rock_player
				|| _graphic == theBitmaps.scissor_player) {
			_r = 3;
			_y_offset = 4;
		} else {
			_r = 8;
		}

		if (_graphic == theBitmaps.X) {
			if (drawingTopBar == false) {
				_g2.setTransform(theTransforms.af);
			}
			//Can't place here
			_g2.setColor(Color.red);
			final GeneralPath X = theShapeStore.GetCross();
			X.transform(AffineTransform.getTranslateInstance(_x, _y));
			_g2.fill(X);
			return;
		}

		if (drawingTopBar == false) {
			_g2.setTransform(theTransforms.af_translate_zoom);
			if (_graphic != null) {
				_g2.drawImage(_graphic[FrameCount/2 % animSteps], __x - _r, __y - _r - _y_offset, null);
			}
		} else { //Drawing top bar
			int f=0;
			if (_graphic == theBitmaps.sound) {
				if (utility.soundOn == false) f = 1;
			} else if (_graphic == theBitmaps.quality) {
				if (utility.highQuality == false) f = 1;
			} else if (_graphic == theBitmaps.pause) {
				if (utility.gamePaused == true) f = 1;
			} else if (_graphic == theBitmaps.ff) {
				if (utility.fastForward == true) f = 1;
			} else {
				f = FrameCount/2 % animSteps;
			}

			if (_graphic != null) {
				_g2.drawImage(_graphic[f], _x - _r, _y - _r - _y_offset, null);
			}
		}
	}


	public boolean TryPlaceItem(final BuildingType _bt, final Graphics2D _g2, final int _mouse_x, final int _mouse_y, final boolean _place_remove) {
		boolean closeToExisting = false;
		_g2.setColor(Color.white);
		_g2.setTransform(theTransforms.af);
		int radius_to_check = utility.buildingRadius;
		boolean doDistanceCheck = true;
		if (_bt == BuildingType.AttractorPaper
				|| _bt == BuildingType.AttractorRock
				|| _bt == BuildingType.AttractorScissors) {
			radius_to_check = utility.attractorRadius;
			doDistanceCheck = false;
		} else { //do distance check
			doDistanceCheck = true;
			synchronized (GetBuildingOjects()) {
				for (final Building _b : GetBuildingOjects()) {
					if (_b.GetOwner() != ObjectOwner.Player) {
						continue;
					}
					if (_b.GetCollects().size() == 0) { //not gatherer
						continue;
					}
					for (int angle = (FrameCount%utility.building_Place_degrees); angle <= utility.wg_DegInCircle; angle += utility.building_Place_degrees) {
						_g2.drawArc(_b.GetX() - utility.building_gather_radius, _b.GetY() - utility.building_gather_radius,
								utility.building_gather_radius * 2, utility.building_gather_radius * 2, angle, utility.building_Place_degrees_show);
					}
					if (utility.Seperation(_mouse_x, _b.GetX(), _mouse_y, _b.GetY()) < utility.building_gather_radius) {
						closeToExisting = true;
					}
				}
			}
		}

		if (_bt == BuildingType.X) {
			//REMOVE BUILDING
			synchronized (GetBuildingOjects()) {
				for (final Building _b : GetBuildingOjects()) {
					if (utility.Seperation(_mouse_x, _b.GetX(), _mouse_y, _b.GetY()) < utility.buildingRadius) {
						if (utility.dbg == false && _b.GetType() == BuildingType.Base) {
							continue;
						}
						if (utility.dbg == false && _b.GetOwner() == ObjectOwner.Enemy) {
							continue;
						}
						_b.DeleteHover();
						if (_place_remove == true) {
							resource_manager.Refund(_b.GetType(), ObjectOwner.Player);
							_b.Kill();
							return true;
						}
					}
				}
			}
		} else if ( CheckSafe(true,true,_mouse_x, _mouse_y, radius_to_check, 0, 0) == true
				&& resource_manager.CanAfford(_bt, ObjectOwner.Player)
				&& ( (doDistanceCheck == true && closeToExisting == true) || doDistanceCheck == false) ) {
			//PLACE BUILDING and location is A'OK
			if (_place_remove == true) {
				PlaceBuilding(new WorldPoint(_mouse_x,_mouse_y), _bt, ObjectOwner.Player);
//				PlaceExplosion(_mouse_x, _mouse_y, ObjectOwner.Player); //TODO TEMP!
//				CheckBuildingExplode(new WorldPoint(_mouse_x,_mouse_y), ObjectOwner.Enemy);
//				PlaceSpecialSpawn(_mouse_x, _mouse_y, ActorType.Spock, ObjectOwner.Player, null, null);
				return true;
			}

			//Draw with player base, special method, for it has the render code
			if (_bt == BuildingType.Woodshop) {
				SpecialRender(_g2, _mouse_x, _mouse_y, theBitmaps.woodshop_player, false);
			} else if (_bt == BuildingType.Rockery) {
				SpecialRender(_g2, _mouse_x, _mouse_y, theBitmaps.rockery_player, false);
			} else if (_bt == BuildingType.Smelter) {
				SpecialRender(_g2, _mouse_x, _mouse_y, theBitmaps.smelter_player, false);
			} else if (_bt == BuildingType.AttractorPaper) {
				SpecialRender(_g2, _mouse_x, _mouse_y, theBitmaps.attractor_paper_player, false);
			} else if (_bt == BuildingType.AttractorRock) {
				SpecialRender(_g2, _mouse_x, _mouse_y, theBitmaps.attractor_rock_player, false);
			} else if (_bt == BuildingType.AttractorScissors) {
				SpecialRender(_g2, _mouse_x, _mouse_y, theBitmaps.attractor_scissors_player, false);
			}

			//Draw circle of collection
			if (doDistanceCheck == true) {
				_g2.setColor(Color.white);
				_g2.setTransform(theTransforms.af);
				for (int angle = (FrameCount%utility.building_Place_degrees); angle <= utility.wg_DegInCircle; angle += utility.building_Place_degrees) {
					_g2.drawArc(_mouse_x - utility.building_gather_radius, _mouse_y - utility.building_gather_radius,
							utility.building_gather_radius * 2, utility.building_gather_radius * 2, angle, utility.building_Place_degrees_show);
				}
			}
		} else {
			//Not safe, show X
			SpecialRender(_g2, _mouse_x, _mouse_y, theBitmaps.X, false);
		}
		return false;
	}


}
