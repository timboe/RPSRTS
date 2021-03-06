package com.timboe.rpsrts.sprites;

import com.timboe.rpsrts.enumerators.ActorType;

public class Projectile extends Sprite {
	protected Sprite target;
	protected Actor source;
	int multiplier = 1;
	int strength;

	protected Projectile(final int _ID, final Actor _source, final int _r, final Sprite _target) {
		super(_ID, _source.GetX() - _r, _source.GetY() - _r, _r);
		speed = utility.projectile_speed;
		target = _target;
		source = _source;
		strength = _source.GetStrength();
		if (_source.GetIfPreferedTarget(_target) == true) {
			multiplier = 2;
		}
		if (_source.GetType() == ActorType.Lizard) {
			animSteps = 3;
			x -= r;
		} else if (_source.GetType() == ActorType.Spock && _target.GetIsBuilding() == true) {
			((Building) _target).SetPrimedToExplode(+1);
		}
	}

	@Override
	public boolean GetIsProjectile() {
		return true;
	}

	public void Tick(final int _tick_count) {
		if ((_tick_count + tick_offset) % ticks_per_tock == 0) Tock();

		if (dead == true) return;
		if (target == null || target.GetDead() == true) {
			Kill();
			return;
		}

		if (source.GetType() == ActorType.Spock && target != null && target.GetDead() == false) {
			target.Attack(((float)strength*(float)multiplier) / utility.ticks_per_tock);
			if (_tick_count % 2 == 0) {
				theSpriteManager.PlaceSpooge(target.GetX(), target.GetY(), target.GetOwner(), utility.spooges_hit, utility.spooges_scale_hit);
			}
			return;
		}

		final float _hypotenuse = utility.Seperation(GetLoc(), target.GetLoc());
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
		x = Math.round(x_prec);
		y = Math.round(y_prec);

	}

	public void Tock() {
		if (source.GetType() == ActorType.Spock) {
			if (target.GetIsBuilding() == true) {
				((Building) target).SetPrimedToExplode(-1);
			}
			Kill();
		}
	}
}
