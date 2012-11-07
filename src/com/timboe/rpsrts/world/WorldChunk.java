package com.timboe.rpsrts.world;


public class WorldChunk extends WorldTile {

	//Extra methods for kT algorithm
	protected float state;
	protected int biome_ID;

	public WorldChunk(final int _x, final int _y, final int _tile_size, final int _ID) {
		super(_x, _y, _tile_size, _ID);
		//As WorldTile
		state = 0;
		biome_ID = 0;
	}

	public void AddState(final float _state) {
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

	public void SetBiomeID(final int _ID) {
		biome_ID = _ID;
	}

	public void SetState(final float _state) {
		state = _state;
	}



}
