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


	public SpecialSpawn_Applet(int _ID, int _x, int _y, int _r, ActorType _at, ObjectOwner _oo) {
		super(_ID, _x, _y, _r, _at, _oo);
	}

	public synchronized void Render(Graphics2D _g2, int _tick_count) {

		Point2D transform = theTransforms.getTransformedPoint(x , y );
		int _x_orig = (int)Math.round(transform.getX());
		int _y_orig = (int)Math.round(transform.getY());
		_g2.setTransform(theTransforms.af_translate_zoom);
		_g2.setColor(Color.magenta);
		_g2.fillRect((int) _x_orig-1, (int) _y_orig-1, 2, 2);


		for (int s = 0; s < nSpooge; ++s) {
			if (utility.Seperation(spooge_x.get(s), x, spooge_y.get(s), y) < r) {
				if (spawnType == ActorType.Spock) {
					_g2.setColor(Color.yellow);
				} else if (spawnType == ActorType.Lizard) {
					_g2.setColor(Color.green);
				}
				final float randomAngle = (float) (utility.rnd() * Math.PI * 2f);
				final float randomLen = utility.rnd();
				final int sx = (int) (r * randomLen * Math.cos(randomAngle));
				final int sy = (int) (r * randomLen * Math.sin(randomAngle));
				_g2.drawLine(_x_orig, _y_orig, _x_orig + sx, _y_orig + sy);
			}
			
			transform = theTransforms.getTransformedPoint(spooge_x.get(s).floatValue(), spooge_y.get(s).floatValue());
			int _x = (int)Math.round(transform.getX());
			int _y = (int)Math.round(transform.getY());
			
			//draw
			if (s % 2 == 0) {
				_g2.setColor(Color.white);
			}
			_g2.fillRect((int) _x, (int) _y, 1, 1);
			
		}	
	}
}
