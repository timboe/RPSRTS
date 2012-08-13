package com.timboeWeb.rpsrts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.timboe.rpsrts.GameWorld;
import com.timboe.rpsrts.Resource;
import com.timboe.rpsrts.ResourceType;
import com.timboe.rpsrts.SpriteManager;
import com.timboeWeb.rpsrts.Bitmaps_Applet;

public class Resource_Applet extends Resource {
	
	BufferedImage[] spriteGraphic;	
	Bitmaps_Applet theBitmaps;

	public Resource_Applet(int _ID, int _x, int _y, int _r, GameWorld _gw,
			Bitmaps_Applet _bm, SpriteManager _sm, ResourceType _type) {
		super(_ID, _x, _y, _r, _gw, _sm, _type);
		
		theBitmaps = _bm;
	}
	
	public void Render(Graphics2D _g2, AffineTransform _af, AffineTransform _af_translate_zoom, AffineTransform _af_shear_rotate, AffineTransform _af_none, int _tick_count) {

		Point2D transformed_island = null;
		//apply  coordinate transform to convert to island coordinates
		transformed_island = _af_shear_rotate.transform(new Point(x,y), transformed_island);
		final int _x = (int)Math.round(transformed_island.getX());
		final int _y = (int)Math.round(transformed_island.getY());

		int anim = stuff / 5;
		if (anim > 5) {
			anim = 5;
		}

		if (type == ResourceType.Tree) {
			_g2.setTransform(_af_translate_zoom);
			_g2.drawImage(theBitmaps.tree[anim], _x - r, _y - r - 6, null);
		} else if (type == ResourceType.Cactus) {
			_g2.setTransform(_af_translate_zoom);
			_g2.drawImage(theBitmaps.cactus[anim], _x - r, _y - r - 6, null);
		} else if (type == ResourceType.Mine) {
			_g2.setTransform(_af);
			_g2.drawImage(theBitmaps.mine[toDraw], x - r - (anim/2) - 1, y - r - (anim/2) - 1, 2 * r + (anim/1) - 2, 2 * r + (anim/1) - 2, null);
		} else if (type == ResourceType.Rockpile) {
			_g2.setTransform(_af_translate_zoom);
			_g2.drawImage(theBitmaps.stone[toDraw], _x - r - (anim/2) - 1, _y - r - (anim/2) - 1, 2 * r + (anim/1) - 2, 2 * r + (anim/1) - 2, null);
		}
		if (not_reachable_penalty > 0) {
			_g2.setColor(Color.red);
			_g2.setTransform(_af);
			_g2.fillOval(x - r, y - r, r * 2, r * 2);
		}
		if (theSpriteManager.utility.dbg == true) {
			_g2.setColor(Color.white);
			_g2.setTransform(_af_translate_zoom);
			_g2.drawString(Integer.toString(stuff), _x - r, _y - r);
		}
	}

}
