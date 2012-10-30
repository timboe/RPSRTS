package com.timboe.rpsrts;

import java.util.HashSet;

public class Biome {
	private final Utility utility = Utility.GetUtility();

	int ID = 0;
	BiomeType type;
	HashSet<WorldTile> ownedTiles;
	float centre_x;
	float centre_y;
	int r;
	int g;
	int b;
	float resourceDensity;
	
	int set_r;
	int set_g;
	int set_b;


	public Biome(int _ID) {
		ID = _ID;
		ownedTiles = new HashSet<WorldTile>();
		type = BiomeType.NONE;
	}

	public void AddTile(WorldTile _t){
		ownedTiles.add(_t);
		_t.SetPartOfBiome(this);
		RandomColour();
		_t.SetColour(set_r,set_g,set_b);
		_t.SetWalkable(GetWalkable());
	}
	public void AssignBiomeType(BiomeType _type) {
		type = _type;
		if (type == BiomeType.DESERT) {
			r = (int) (218 + Math.round(utility.rndG(0, utility.biome_colour_range)));  //XXX RND BUG
			g = (int) (165 + Math.round(utility.rndG(0, utility.biome_colour_range)));
			b = (int) (32 + Math.round(utility.rndG(0, utility.biome_colour_range)));
			resourceDensity = (float) ((utility.rndI(utility.biome_desert_rnd_density) + utility.biome_desert_min_density) / 100.);
		} else if (type == BiomeType.FORREST) {
			r = (int) (46 + Math.round(utility.rndG(0, utility.biome_colour_range)));  //XXX RND BUG
			g = (int) (139 + Math.round(utility.rndG(0, utility.biome_colour_range)));
			b = (int) (87 + Math.round(utility.rndG(0, utility.biome_colour_range)));
			resourceDensity = (float) ((utility.rndI(utility.biome_forest_rnd_density) + utility.biome_forest_min_density) / 100.);
		} else if (type == BiomeType.GRASS) {
			r = (int) (154 + Math.round(utility.rndG(0, utility.biome_colour_range)));  //XXX RND BUG
			g = (int) (205 + Math.round(utility.rndG(0, utility.biome_colour_range)));
			b = (int) (50 + Math.round(utility.rndG(0, utility.biome_colour_range)));
			resourceDensity = (float) ((utility.rndI(utility.biome_grass_rnd_density) + utility.biome_grass_min_density) / 100.);
		} else if (type == BiomeType.WATER) {
			r = 65;
			g = 105;
			b = 225;
			resourceDensity = 0;
		}
		for (final WorldTile t : ownedTiles) {
			RandomColour();
			t.SetColour(set_r,set_g,set_b);
			t.SetWalkable(GetWalkable());
		}
	}

	public void CalculateCentre() {
		centre_x = 0;
		centre_y = 0;
		if (ownedTiles.size() == 0) return;
		for (final WorldTile t : ownedTiles) {
			centre_x += t.GetXCentre();
			centre_y += t.GetYCentre();
		}
		centre_x /= ownedTiles.size();
		centre_y /= ownedTiles.size();
		//System.out.println("centre of cluster "+ID+" is:("+centre_x+","+centre_y+")"); 
	}

	private int Constrain(int _c) {
		if (_c < 0) return 0;
		else if (_c > 255) return 255;
		else return _c;
	}

	public BiomeType GetBiomeType() {
		return type;
	}

	public float GetDistanceFromPoint(int _x, int _y) {
		return (float) Math.sqrt( Math.pow( centre_x - _x, 2) + Math.pow( centre_y - _y, 2));
	}

	public int GetID() {
		return ID;
	}

	public float GetResourceDensity() {
		return resourceDensity;
	}

	public boolean GetWalkable() {
		if (type == BiomeType.WATER || type == BiomeType.NONE)
			return false;
		else
			return true;
	}
	
	private void RandomColour() {
		int _r = Math.round(r + utility.rndG(0, utility.biome_colour_range));  //XXX RND BUG
		int _g = Math.round(g + utility.rndG(0, utility.biome_colour_range));
		int _b = Math.round(b + utility.rndG(0, utility.biome_colour_range));
		set_r = Constrain(_r);
		set_g = Constrain(_g);
		set_b = Constrain(_b);
	}

	public void RemoveTile(WorldTile _t){
		_t.Reset();
		ownedTiles.remove(_t);
	}
}
