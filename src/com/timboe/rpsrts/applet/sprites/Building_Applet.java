package com.timboe.rpsrts.applet.sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.timboe.rpsrts.applet.managers.Bitmaps_Applet;
import com.timboe.rpsrts.applet.managers.SceneRenderer_Applet;
import com.timboe.rpsrts.applet.managers.ShapeStore;
import com.timboe.rpsrts.applet.managers.TransformStore;
import com.timboe.rpsrts.enumerators.BuildingType;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.sprites.Building;

public class Building_Applet extends Building {

	BufferedImage[] spriteGraphic;	
	private Bitmaps_Applet theBitmaps = Bitmaps_Applet.GetBitmaps_Applet();
	private TransformStore theTransforms = TransformStore.GetTransformStore();
	private ShapeStore theShapeStore = ShapeStore.GetShapeStore();
	private SceneRenderer_Applet theSceneRenderer = SceneRenderer_Applet.GetSceneRenderer_Applet();


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
	
	public void Render(Graphics2D _g2, int _frame_count) {
		if (dead == true) return;
		if (_frame_count % 2 == 0) {
			++animStep;
		}

		Point2D transform = null;
		transform = theTransforms.af_shear_rotate.transform(new Point(x, y), transform);
		final int _x = (int)Math.round(transform.getX());
		final int _y = (int)Math.round(transform.getY());

		//Do delete hover
		if (delete_hover) {
			_g2.setTransform(theTransforms.af);
			_g2.setColor(Color.red);
			GeneralPath X = (GeneralPath) theShapeStore.GetCross();
			X.transform(AffineTransform.getTranslateInstance(x, y));
			_g2.fill(X);
			delete_hover = false;
			return;
		}
		
		//Do move symbol
		if (move_hover == true || 
				(getiAttract().size() > 0
				&& owner == ObjectOwner.Player
				&& theSceneRenderer.MouseTF != null 
				&& theSceneRenderer.MouseTF.distance(x, y) < r) ) {
			_g2.setTransform(theTransforms.af);
			_g2.setColor(Color.green);
			GeneralPath move = (GeneralPath) theShapeStore.GetMove();
			move.transform(AffineTransform.getTranslateInstance(x, y));
			_g2.fill(move);
			if (utility.mouseDrag == false) move_hover = false;
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
