package com.timboe.rpsrts.applet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.timboe.rpsrts.enumerators.ResourceType;
import com.timboe.rpsrts.sprites.Resource;

public class Resource_Applet extends Resource {
	
	BufferedImage[] spriteGraphic;	
	protected Bitmaps_Applet theBitmaps = Bitmaps_Applet.GetBitmaps_Applet();
	protected TransformStore theTransforms = TransformStore.GetTransformStore();

	public Resource_Applet(int _ID, int _x, int _y, int _r, ResourceType _type) {
		super(_ID, _x, _y, _r, _type);
	}
	
	public void Render(Graphics2D _g2, int _tick_count) {

		Point2D transformed_island = null;
		//apply  coordinate transform to convert to island coordinates
		transformed_island = theTransforms.af_shear_rotate.transform(new Point(x,y), transformed_island);
		final int _x = (int)Math.round(transformed_island.getX());
		final int _y = (int)Math.round(transformed_island.getY());

		int anim = stuff / 5;
		if (anim > 5) {
			anim = 5;
		}

		if (not_reachable_penalty > 0 || true) {
			_g2.setColor(Color.red);
			_g2.setTransform(theTransforms.af);
			_g2.fillOval(x - r, y - r, 2*r, 2*r);
		}
		
		if (type == ResourceType.Tree) {
			_g2.setTransform(theTransforms.af_translate_zoom);
			_g2.drawImage(theBitmaps.tree[anim], _x - r, _y - r - 9, null);
		} else if (type == ResourceType.Cactus) {
			_g2.setTransform(theTransforms.af_translate_zoom);
			_g2.drawImage(theBitmaps.cactus[anim], _x - r, _y - r - 9, null);
		} else if (type == ResourceType.Mine) {
			_g2.setTransform(theTransforms.af);
			_g2.drawImage(theBitmaps.mine[toDraw], x - r - (anim/2), y - r - (anim/2), 2 * r + anim, 2 * r + anim, null);
		} else if (type == ResourceType.Rockpile) {
			_g2.setTransform(theTransforms.af_translate_zoom);
			_g2.drawImage(theBitmaps.stone[toDraw], _x - r, _y - r - 3, null);
		}
		
		if (not_reachable_penalty > 0 || true) {
			_g2.setColor(Color.blue);
			_g2.setTransform(theTransforms.af);
			_g2.fillOval(x - 1, y - 1, 2, 2);
		}

		if (utility.dbg == true) {
			_g2.setColor(Color.white);
			_g2.setTransform(theTransforms.af_translate_zoom);
			_g2.drawString(Integer.toString(stuff), _x - r, _y - r); //usually - stuff XXX
		}
	}

}
