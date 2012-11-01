package com.timboe.rpsrts.sprites;

public class Projectile extends Sprite {
	Sprite target;
	int multiplier = 1;
	int strength;
	
	protected Projectile(int _ID, Actor _source, int _r, Sprite _target) {
		super(_ID, _source.GetX(), _source.GetY(), _r);
		speed = utility.projectile_speed;
		target = _target;
		strength = _source.GetStrength();
		if (_source.GetIfPreferedTarget(_target) == true) {
			multiplier = 2;
		}

	}
	
	@Override
	public boolean GetIsProjectile() {
		return true;
	}
	
	public void Tick(int _tick_count) {
		if (dead == true) return;
		if (target == null || target.GetDead() == true) {
			Kill();
			return;
		}
		
		float _hypotenuse = utility.Seperation(GetLoc(), target.GetLoc());
		if (_hypotenuse < r) {
			target.Attack(strength*multiplier);
			theSpriteManager.PlaceSpooge(x, y, target.GetOwner(), utility.spooges_hit, utility.spooges_scale_hit);
			Kill();
			return;
		}
		
		x_prec -= (((x_prec - target.GetX()) / _hypotenuse) * speed);
		y_prec -= (((y_prec - target.GetY()) / _hypotenuse) * speed);
		x = (int) Math.round(x_prec);
		y = (int) Math.round(y_prec);
	
	}
}
