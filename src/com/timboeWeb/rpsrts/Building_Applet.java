package com.timboeWeb.rpsrts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.timboe.rpsrts.Building;
import com.timboe.rpsrts.BuildingType;
import com.timboe.rpsrts.ObjectOwner;

public class Building_Applet extends Building {

	BufferedImage[] spriteGraphic;	
	protected Bitmaps_Applet theBitmaps = Bitmaps_Applet.GetBitmaps_Applet();
	protected TransformStore theTransforms = TransformStore.GetTransformStore();

	public Building_Applet(int _ID, int _x, int _y, int _r, BuildingType _bt, ObjectOwner _oo) {
		super(_ID, _x, _y, _r, _bt, _oo);
		
		if (type == BuildingType.Base) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = theBitmaps.base_player;
			} else {
				spriteGraphic = theBitmaps.base_enemy;
			}
		} else if (type == BuildingType.AttractorPaper) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = theBitmaps.attractor_paper_player;
			} else {
				spriteGraphic = theBitmaps.attractor_paper_enemy;
			}
		} else if (type == BuildingType.AttractorRock) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = theBitmaps.attractor_rock_player;
			} else {
				spriteGraphic = theBitmaps.attractor_rock_enemy;
			}
		} else if (type == BuildingType.AttractorScissors) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = theBitmaps.attractor_scissors_player;
			} else {
				spriteGraphic = theBitmaps.attractor_scissors_enemy;
			}
		} else if (type == BuildingType.Woodshop) {
				if (_oo == ObjectOwner.Player) {
					spriteGraphic = theBitmaps.woodshop_player;
				} else {
					spriteGraphic = theBitmaps.woodshop_enemy;
				}
		} else if (type == BuildingType.Rockery) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = theBitmaps.rockery_player;
			} else {
				spriteGraphic = theBitmaps.rockery_enemy;
			}
		} else if (type == BuildingType.Smelter) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = theBitmaps.smelter_player;
			} else {
				spriteGraphic = theBitmaps.smelter_enemy;
			}
		}
	}
	
	public void Render(Graphics2D _g2, int _tick_count) {
		if (dead == true) return;
		if (_tick_count % 2 == 0) {
			++animStep;
		}

		Point2D transform = null;
		transform = theTransforms.af_shear_rotate.transform(new Point(x, y), transform);
		final int _x = (int)Math.round(transform.getX());
		final int _y = (int)Math.round(transform.getY());

		if (delete_hover) {
			//Can't place here
			_g2.setTransform(theTransforms.af);
			_g2.setColor(Color.red);
			final int[] _x_points = new int[4];
			final int[] _y_points = new int[4];
			final int b_r = utility.buildingRadius;
			_x_points[0] = x - b_r - 2;
			_x_points[1] = x - b_r + 2;
			_x_points[2] = x + b_r + 2;
			_x_points[3] = x + b_r - 2;

			_y_points[0] = y - b_r + 2 + y_offset/6;
			_y_points[1] = y - b_r - 2 + y_offset/6;
			_y_points[2] = y + b_r - 2 + y_offset/6;
			_y_points[3] = y + b_r + 2 + y_offset/6;

			_g2.fillPolygon(_x_points, _y_points, 4);

			_x_points[0] = x - b_r - 2;
			_x_points[1] = x - b_r + 2;
			_x_points[2] = x + b_r + 2;
			_x_points[3] = x + b_r - 2;

			_y_points[0] = y + b_r - 2 + y_offset/6;
			_y_points[1] = y + b_r + 2 + y_offset/6;
			_y_points[2] = y - b_r + 2 + y_offset/6;
			_y_points[3] = y - b_r - 2 + y_offset/6;
			_g2.fillPolygon(_x_points, _y_points, 4);

			delete_hover = false;
			return;
		}

		//Do health
		if (health < maxHealth) {
			_g2.setTransform(theTransforms.af_translate_zoom);
			_g2.setColor(Color.black);
			_g2.fillRect(_x - r, _y - r - 8 - y_offset, r * 2, 1);
			if (owner == ObjectOwner.Player) {
				_g2.setColor(Color.red);
			} else {
				_g2.setColor(Color.blue);
			}
			_g2.fillRect(_x - r, _y - r - 8 - y_offset, (int) Math.round(r * 2 * ((float)health/(float)maxHealth) ), 1);
		}

		if (utility.dbg == true) {
			_g2.setTransform(theTransforms.af_translate_zoom);
			_g2.setColor(Color.red);
			_g2.drawString(Integer.toString(GetEmployees()), _x - r, _y - r - 10);
		}
		if (true) { //DBG
			_g2.setColor(Color.red);
			_g2.setTransform(theTransforms.af);
			_g2.fillOval(x - r, y - r, 2*r, 2*r);
		}
		if (underConstruction == true) {
			int conStep = 0;
			final float healthFrac = (float)health/(float)maxHealth;
			if (healthFrac <= 0.25) {
				conStep = 0;
			} else if (healthFrac <= 0.5) {
				conStep = 1;
			} else if (healthFrac <= 0.75) {
				conStep = 2;
			} else {
				conStep = 3;
			}
			_g2.setTransform(theTransforms.af_translate_zoom);
			if (owner == ObjectOwner.Player) {
				_g2.drawImage(theBitmaps.construction_player[conStep], _x - r, _y - r - 5, null);
			} else {
				_g2.drawImage(theBitmaps.construction_enemy[conStep], _x - r, _y - r - 5, null);
			}
		} else {
			_g2.setTransform(theTransforms.af_translate_zoom);
			_g2.drawImage(spriteGraphic[animStep % animSteps], _x - r, _y - r - 5 - y_offset, null);
		}
		
		if (true) { //DBG
			_g2.setColor(Color.blue);
			_g2.setTransform(theTransforms.af);
			_g2.fillOval(x - 1, y - 1, 2, 2);
		}
		

	}
//
//	public void SpecialRender(Graphics2D _g2, AffineTransform _af, AffineTransform _af_translate_zoom, AffineTransform _af_shear_rotate, AffineTransform _af_none, int _x, int _y, BufferedImage[] _graphic) {
//		//if (true) return;
//
//	}

}
