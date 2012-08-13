package com.timboe.rpsrts;

public class WorldTile {
	protected int x;			// Start x - Position
	protected int y;
	protected int tile_s;
	protected boolean canWalk;
	protected int ID;
	protected boolean part_of_biome;
	protected Biome ownedBy;
	private final Utility utility;

	public WorldTile (int _x, int _y,int _tile_size, int _ID, Utility _utility) {
		ID = _ID;
		x = _x;
		y = _y;
		tile_s= _tile_size;
		utility = _utility;
	}

	public int GetAngle() {
		int _angle;
		if (GetXCentre() > 0 && GetYCentre() > 0) {
			_angle = (int) Math.round( Math.toDegrees(Math.atan(GetXCentre()/GetYCentre())) ) + ((3*utility.wg_DegInCircle)/4); //bot r
		} else if (GetXCentre() > 0 && GetYCentre() < 0) {
			_angle = (int) Math.round( Math.toDegrees(Math.atan(GetXCentre()/GetYCentre())) ) + (utility.wg_DegInCircle/4); //top r
		} else if (GetXCentre() < 0 && GetYCentre() < 0) {
			_angle = (int) Math.round( Math.toDegrees(Math.atan(GetXCentre()/GetYCentre())) ) + (utility.wg_DegInCircle/4); //top l
		} else {
			_angle = (int) Math.round( Math.toDegrees(Math.atan(GetXCentre()/GetYCentre())) ) + ((3*utility.wg_DegInCircle)/4); //bot l
		}
		if (_angle >= utility.wg_DegInCircle)
		 {
			_angle -= utility.wg_DegInCircle; //Can round to 360
		}
		return _angle;
	}

	public BiomeType GetBiomeType() {
		if (ownedBy != null)
			return ownedBy.GetBiomeType();
		else
			return BiomeType.NONE;
	}

	public float GetDistanceFromPoint(int _x, int _y) {
		return (float) Math.sqrt( Math.pow( GetXCentre() - _x, 2) + Math.pow( GetYCentre() - _y, 2));
	}


	public Biome GetOwner() {
		return ownedBy;
	}


	public boolean GetPartOfBiome() {
		return part_of_biome;
	}

	public boolean GetWalkable() {
		return canWalk;
	}

	public int GetX() {
		return x;
	}

	public float GetXCentre() {
		return x + (tile_s / 2.f);
	}

	public int GetY() {
		return y;
	}

	public float GetYCentre() {
		return y + (tile_s / 2.f);
	}


	public void SetPartOfBiome(Biome _biome) {
		part_of_biome = true;
		ownedBy = _biome;
	}

	public void SetPartOfBiome(boolean _part_of_biome) {
		part_of_biome = _part_of_biome;
	}

	public void SetWalkable(boolean _canWalk) {
		canWalk = _canWalk;
	}

	public void Reset() {
		// TODO Auto-generated method stub
		
	}

	public void SetColour(int R, int G, int B) {
		// TODO Auto-generated method stub
		
	}
	
	

}