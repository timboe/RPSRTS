package com.timboe.rpsrts.applet;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

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
		
		Point2D transform = null;
		transform = theTransforms.af_shear_rotate.transform(new Point(x, y), transform);
		final int _x = (int)Math.round(transform.getX());
		final int _y = (int)Math.round(transform.getY());
				
		_g2.setTransform(theTransforms.af_translate_zoom);
		_g2.drawImage(spriteGraphic[animStep % animSteps], _x - r, _y - r, null);
	}

}
