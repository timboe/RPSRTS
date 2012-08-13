package com.timboe.rpsrts;

public class Projectile extends Sprite {
	Sprite target;
	int multiplier = 1;
	int strength;
	
	protected Projectile(int _ID, Actor _source, int _r, GameWorld _gw, SpriteManager _sm, Sprite _target) {
		super(_ID, _source.GetX(), _source.GetY(), _r, _gw, _sm);
		speed = 2;
		target = _target;
		strength = _source.GetStrength();
		if (_source.GetIfPreferedTarget(_target) == true) {
			multiplier = 2;
		}

	}
	
	void Tick(int _tick_count) {
		if (dead == true) return;
		if (target == null || target.GetDead() == true) {
			Kill();
			return;
		}
		
		float _hypotenuse = theSpriteManager.utility.Seperation(GetLoc(), target.GetLoc());
		if (_hypotenuse < r) {
			target.Attack(strength*multiplier);
			int n = 2;
			//if (target instanceof Building) n = 2;
			theSpriteManager.PlaceSpooge(x, y, target.GetOwner(), n, 0.5f);
			Kill();
			return;
		}
		
		x_prec -= (((x_prec - target.GetX()) / _hypotenuse) * speed);
		y_prec -= (((y_prec - target.GetY()) / _hypotenuse) * speed);
		x = (int) Math.round(x_prec);
		y = (int) Math.round(y_prec);
	
	}
}
