package com.timboe.rpsrts.sprites;

import com.timboe.rpsrts.enumerators.ActorType;

public class Projectile extends Sprite {
	protected Sprite target;
	protected Actor source;
	int multiplier = 1;
	int strength;
	
	protected Projectile(int _ID, Actor _source, int _r, Sprite _target) {
		super(_ID, _source.GetX(), _source.GetY(), _r);
		speed = utility.projectile_speed;
		target = _target;
		source = _source;
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
		if ((_tick_count + tick_offset) % ticks_per_tock == 0) Tock();

		if (dead == true) return;
		if (target == null || target.GetDead() == true) {
			Kill();
			return;
		}
		
		if (source.GetType() == ActorType.Spock && target != null && target.GetDead() == false) {
			target.Attack(((float)strength*(float)multiplier) / (float)utility.ticks_per_tock);
			theSpriteManager.PlaceSpooge(target.GetX(), target.GetY(), target.GetOwner(), utility.spooges_hit, utility.spooges_scale_hit);
			return;
		}
		
		float _hypotenuse = utility.Seperation(GetLoc(), target.GetLoc());
		if (_hypotenuse < r) {
			target.Attack(strength*multiplier);
			theSpriteManager.PlaceSpooge(x, y, target.GetOwner(), utility.spooges_hit, utility.spooges_scale_hit);
			Kill();
			if (source.GetType() == ActorType.Lizard) {
				theSpriteManager.CheckPoison(GetLoc(), target.GetOwner());
			}
			return;
		}
		
		x_prec -= (((x_prec - target.GetX()) / _hypotenuse) * speed);
		y_prec -= (((y_prec - target.GetY()) / _hypotenuse) * speed);
		x = (int) Math.round(x_prec);
		y = (int) Math.round(y_prec);
	
	}
	
	public void Tock() {
		if (source.GetType() == ActorType.Spock) {
			Kill();
		}
	}
}
