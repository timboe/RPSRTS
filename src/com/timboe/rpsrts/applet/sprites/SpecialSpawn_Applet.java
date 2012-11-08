package com.timboe.rpsrts.applet.sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import com.timboe.rpsrts.applet.managers.TransformStore;
import com.timboe.rpsrts.enumerators.ActorType;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.sprites.SpecialSpawn;

public class SpecialSpawn_Applet extends SpecialSpawn {
	protected TransformStore theTransforms = TransformStore.GetTransformStore();


	public SpecialSpawn_Applet(final int _ID, final int _x, final int _y, final int _r, final ActorType _at, final ObjectOwner _oo) {
		super(_ID, _x, _y, _r, _at, _oo);
	}

	public synchronized void Render(final Graphics2D _g2, final int _tick_count) {

		Point2D transform = theTransforms.getTransformedPoint(x , y );
		final int _x_orig = (int)(transform.getX());
		final int _y_orig = (int)(transform.getY());
		_g2.setTransform(theTransforms.af_translate_zoom);

		if (utility.dbg == true) {
			_g2.setColor(Color.magenta);
			_g2.fillRect(_x_orig-1, _y_orig-1, 2, 2);
		}


		for (int s = 0; s < nSpooge; ++s) {
			if (spawnType == ActorType.Spock) {
				_g2.setColor(Color.yellow);
			} else if (spawnType == ActorType.Lizard) {
				_g2.setColor(Color.green);
			}
			if (utility.Seperation(spooge_x.get(s), x, spooge_y.get(s), y) < r) {
				final float randomAngle = (float) (utility.rnd() * Math.PI * 2f);
				final float randomLen = utility.rnd();
				final int sx = (int) (r * randomLen * Math.cos(randomAngle));
				final int sy = (int) (r * randomLen * Math.sin(randomAngle));
				_g2.drawLine(_x_orig, _y_orig, _x_orig + sx, _y_orig + sy);
			}

			transform = theTransforms.getTransformedPoint(spooge_x.get(s).floatValue(), spooge_y.get(s).floatValue());
			final int _x = (int)Math.round(transform.getX());
			final int _y = (int)Math.round(transform.getY());

			//draw
			if (s % 2 == 0) {
				if (owner == ObjectOwner.Player) {
					_g2.setColor(Color.red);
				} else if (owner == ObjectOwner.Enemy) {
					_g2.setColor(Color.blue);
				}
			}
			_g2.fillRect(_x, _y, 1, 1);

		}
	}
}
