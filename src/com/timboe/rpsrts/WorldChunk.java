package com.timboe.rpsrts;


public class WorldChunk extends WorldTile {

	//Extra methods for kT algorithm
	protected float state;
	protected int biome_ID;

	public WorldChunk(int _x, int _y, int _tile_size, int _ID, Utility _utility) {
		super(_x, _y, _tile_size, _ID, _utility);
		//As WorldTile
		state = 0;
		biome_ID = 0;
	}

	public void AddState(float _state) {
		state = state + _state;
	}

	public int GetBiomeID() {
		return biome_ID;
	}

	public float GetState() {
		return state;
	}

	@Override
	public void Reset() {
		state = 0;
		biome_ID = 0;
	}

	public void SetBiomeID(int _ID) {
		biome_ID = _ID;
	}

	public void SetState(float _state) {
		state = _state;
	}



}
