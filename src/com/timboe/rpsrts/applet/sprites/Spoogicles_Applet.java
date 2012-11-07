package com.timboe.rpsrts.applet.sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import com.timboe.rpsrts.applet.managers.TransformStore;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.sprites.Spoogicles;

public class Spoogicles_Applet extends Spoogicles {
	protected TransformStore theTransforms = TransformStore.GetTransformStore();

	public Spoogicles_Applet(final int _ID, final int _x, final int _y, final ObjectOwner _oo, final int _spoogicles, final float _scale) {
		super(_ID, _x, _y, _oo, _spoogicles, _scale);
	}

	public void Render(final Graphics2D _g2, final int _tick_count) {

		if (nDead == nSpooge) {
			Kill();
			return;
		}

		final Point2D transform = theTransforms.getTransformedPoint(floorX,floorLevel);
		final int _x = (int)(transform.getX());
		final int _y = (int)(transform.getY());

		for (int s = 0; s < nSpooge; ++s) {
			if (isDead.get(s).get()  == true) continue;

			//draw
			if (s % 2 == 0) {
				_g2.setColor(Color.white);
			} else {
				if (oo == ObjectOwner.Player) _g2.setColor(Color.red);
				else _g2.setColor(Color.blue);
			}
			_g2.setTransform(theTransforms.af_translate_zoom);
			_g2.fillRect((int) (_x + x.get(s).floatValue()), (int) (_y + y.get(s).floatValue()), 1, 1);
		}
	}
}
