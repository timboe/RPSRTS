package com.timboe.rpsrts.applet.sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.timboe.rpsrts.applet.managers.Bitmaps_Applet;
import com.timboe.rpsrts.applet.managers.TransformStore;
import com.timboe.rpsrts.enumerators.ResourceType;
import com.timboe.rpsrts.sprites.Resource;

public class Resource_Applet extends Resource {

	BufferedImage[] spriteGraphic;
	protected Bitmaps_Applet theBitmaps = Bitmaps_Applet.GetBitmaps_Applet();
	protected TransformStore theTransforms = TransformStore.GetTransformStore();

	public Resource_Applet(final int _ID, final int _x, final int _y, final int _r, final ResourceType _type) {
		super(_ID, _x, _y, _r, _type);
	}

	public void Render(final Graphics2D _g2, final int _tick_count) {

		final Point2D transformed_island = theTransforms.getTransformedPoint(x, y);
		//apply  coordinate transform to convert to island coordinates
		final int _x = (int)(transformed_island.getX());
		final int _y = (int)(transformed_island.getY());

		int anim = stuff / 5;
		if (anim > 5) {
			anim = 5;
		}

		if (not_reachable_penalty > 0 && utility.dbg == true) {
			_g2.setColor(Color.white);
			_g2.setTransform(theTransforms.af);
			_g2.fillOval(x - 2*r, y - 2*r, 4*r, 4*r);
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

//		if (utility.dbg == true) {
//			_g2.setColor(Color.blue);
//			_g2.setTransform(theTransforms.af);
//			_g2.fillOval(x - 1, y - 1, 2, 2);
//			_g2.setColor(Color.white);
//			_g2.setTransform(theTransforms.af_translate_zoom);
//			_g2.drawString(Integer.toString(stuff), _x - r, _y - r);
//		}
	}
}
