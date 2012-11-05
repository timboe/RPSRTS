package com.timboe.rpsrts.applet.sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.timboe.rpsrts.applet.managers.Bitmaps_Applet;
import com.timboe.rpsrts.applet.managers.TransformStore;
import com.timboe.rpsrts.enumerators.ActorType;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.sprites.Actor;
import com.timboe.rpsrts.sprites.Projectile;
import com.timboe.rpsrts.sprites.Sprite;

public class Projectile_Applet extends Projectile {
	
	BufferedImage[] spriteGraphic;	
	protected Bitmaps_Applet theBitmaps = Bitmaps_Applet.GetBitmaps_Applet();
	protected TransformStore theTransforms = TransformStore.GetTransformStore();

	public Projectile_Applet(int _ID, Actor _source, int _r, Sprite _target) {
		super(_ID, _source, _r, _target);

		if (_source.GetOwner() == ObjectOwner.Player) {
			if (_source.GetType() == ActorType.Paper) spriteGraphic = theBitmaps.proj_paper_player;
			else if (_source.GetType() == ActorType.Rock) spriteGraphic = theBitmaps.proj_rock_player;
			else if (_source.GetType() == ActorType.Scissors) spriteGraphic = theBitmaps.proj_scissor_player;
		} else if (_source.GetOwner() == ObjectOwner.Enemy) {
			if (_source.GetType() == ActorType.Paper) spriteGraphic = theBitmaps.proj_paper_enemy;
			else if (_source.GetType() == ActorType.Rock) spriteGraphic = theBitmaps.proj_rock_enemy;
			else if (_source.GetType() == ActorType.Scissors) spriteGraphic = theBitmaps.proj_scissor_enemy;
		}
	}
	
	public void Render(Graphics2D _g2, int _tick_count) {
		if (dead == true) return;
		
		if (_tick_count % 4 == 0) {
			++animStep;
		}
		

				
		_g2.setTransform(theTransforms.af_translate_zoom);
		if (source.GetType() == ActorType.Spock) {
			if (animStep % animSteps == 0) {
				_g2.setColor(Color.white);
			} else if (source.GetOwner() == ObjectOwner.Player) {
				_g2.setColor(Color.red);
			} else {
				_g2.setColor(Color.blue);
			}
			Point2D transform = theTransforms.getTransformedPoint(target.GetX(), target.GetY());
			final int _x1 = (int) transform.getX();
			final int _y1 = (int) transform.getY(); 
			transform = theTransforms.getTransformedPoint(source.GetX(), source.GetY());
			_g2.drawLine((int)transform.getX(), (int) (transform.getY() - source.GetR() * 1.5), _x1, _y1);
		} else if (source.GetType() == ActorType.Lizard) {
			Point2D transform = theTransforms.getTransformedPoint(x, y);
			final int _x = (int) transform.getX();
			final int _y = (int) transform.getY();
			_g2.setColor(Color.cyan);
			_g2.drawOval(_x - 2, _y - 2, 4, 4);
		} else {
			Point2D transform = theTransforms.getTransformedPoint(x, y);
			final int _x = (int) transform.getX();
			final int _y = (int) transform.getY();
			_g2.drawImage(spriteGraphic[animStep % animSteps], _x - r, _y - r, null);
		}
	}

}
