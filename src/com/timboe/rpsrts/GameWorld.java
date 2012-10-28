package com.timboe.rpsrts;

import java.util.HashSet;
import java.util.Vector;

public class GameWorld {

	protected int world_tiles;
	protected int world_chunks;
	int tiles_per_chunk;
	public int tiles_size;
	protected int chunks_size;
	protected int world_size;
	protected int island_size;

	protected WorldTile tiles[];
	protected WorldChunk chunks[];
	private final HashSet<Biome> biomes;
	protected HashSet<WorldTile> render_tiles;
	protected final long island_offset[];
	private Biome ocean;

	private boolean wg_finished;
	private int wg_state;
	private int wg_times_erroded;
	private float wg_time_of_last_operation;

	protected Utility utility;

	public GameWorld(int _world_tiles, int _tiles_size, int _tiles_per_chunk, Utility _utility) {
		utility = _utility;
		world_tiles = _world_tiles;
		tiles_per_chunk = _tiles_per_chunk;
		world_chunks = world_tiles / tiles_per_chunk;
		tiles_size = _tiles_size;
		chunks_size = _tiles_size * _tiles_per_chunk;
		world_size = world_tiles * tiles_size;
		island_size = (int) Math.round(utility.island_scale * world_size);

//		tiles = new WorldTile[world_tiles*world_tiles];
//		chunks = new WorldChunk[world_chunks*world_chunks];
//		render_tiles = new HashSet<WorldTile>();
		biomes = new HashSet<Biome>();
		island_offset = new long[utility.wg_DegInCircle];

		wg_finished = false;
		wg_state = 0;

//		int ID = 0;
//	    for (int x = -(world_size/2); x < (world_size/2); x = x + tiles_size) {
//		    for (int y = -(world_size/2); y < (world_size/2); y = y + tiles_size) {
//		    	tiles[ID++] = new WorldTile(x,y,tiles_size,ID,utility);
//		    }
//	    }
//
//	    ID = 0;
//	    for (int x = -(world_size/2); x < (world_size/2); x = x + chunks_size) {
//		    for (int y = -(world_size/2); y < (world_size/2); y = y + chunks_size) {
//		    	chunks[ID++] = new WorldChunk(x,y,chunks_size,ID,utility);
//		    }
//	    }
	}

	public boolean CheckSafeToPlaceTile(float _x, float _y, int _pice_radius) {
		int _ID = -1;
		//HACK!
		//_x -= tiles_size;
		//_y -= tiles_size/2;
		//HACK!
		for (int dir = 0; dir < 4; ++dir) {
			switch (dir) {
				case 0: _ID = utility.XYtoID((int) Math.round(_x - _pice_radius), (int) Math.round(_y), world_tiles, tiles_size); break;
				case 1: _ID = utility.XYtoID((int) Math.round(_x + _pice_radius), (int) Math.round(_y), world_tiles, tiles_size); break;
				case 2: _ID = utility.XYtoID((int) Math.round(_x), (int)  Math.round(_y - _pice_radius), world_tiles, tiles_size); break;
				case 3: _ID = utility.XYtoID((int) Math.round(_x), (int)  Math.round(_y + _pice_radius), world_tiles, tiles_size); break;
			}
			//_ID = utility.XYtoID((int)_x, (int)_y, world_tiles, tiles_size);
			if (_ID != -1) {
				//System.out.println("Tile not walkable! ("+_x+","+_y+") ID:"+_ID);
				if (tiles[_ID].GetWalkable() == false) return false;
					
			} else{
				//System.out.println("WARN Tile check safe to place out of range! ("+_x+","+_y+")");
				return false;
			}
		}
		return true;
	}

	public int GenerateWorld() {
		final float timeNow = (System.nanoTime() / 1000000) / 1000.0f;

		if (wg_state == 0) {
			Reset();
			wg_time_of_last_operation = (System.nanoTime() / 1000000) / 1000.0f;
			wg_state = 1;
		}
		
		if (utility.doWorldGen == false) return 1; //Stop until player clicks GO

		if (wg_state == 1 && (timeNow-wg_time_of_last_operation) > utility.wg_seconds_to_wait ) {
			GenerateWorld_CrinkleIslandEdge();
			wg_time_of_last_operation = (System.nanoTime() / 1000000) / 1000.0f;
			++wg_state;
		}

		if (wg_state == 2 && (timeNow-wg_time_of_last_operation) > utility.wg_seconds_to_wait ) {
			GenerateWorld_RandomSeed();
			wg_time_of_last_operation = (System.nanoTime() / 1000000) / 1000.0f;
			++wg_state;
		}

		if (wg_state == 3 && (timeNow-wg_time_of_last_operation) > utility.wg_seconds_to_wait ) {
			++wg_state;
			return wg_state;
		}

		if (wg_state == 4 && (timeNow-wg_time_of_last_operation) > utility.wg_seconds_to_wait ) {
			final int nBiomes = GenerateWorld_Apply_kT();
			if (nBiomes == -1)
				return wg_state;
			else if (nBiomes < utility.wg_MinBiomes || nBiomes > utility.wg_MaxBiomes) {
				wg_state = 1;
			} else {
				wg_time_of_last_operation = (System.nanoTime() / 1000000) / 1000.0f;
				++wg_state;
			}
		}

		if (wg_state == 5 && (timeNow-wg_time_of_last_operation) > utility.wg_seconds_to_wait ) {
			final boolean allGood = GenerateWorld_AssignBiomes();
			if (allGood == false) {
				wg_state = 1;
			} else {
				wg_time_of_last_operation = (System.nanoTime() / 1000000) / 1000.0f;
				++wg_state;
			}
		}

		if (wg_state == 6 && (timeNow-wg_time_of_last_operation) > utility.wg_seconds_to_wait ) {
			GenerateWorld_ErodeEdges();
			wg_time_of_last_operation = (System.nanoTime() / 1000000) / 1000.0f;
			++wg_state;
		}

		if (wg_state == 7 && (timeNow-wg_time_of_last_operation) > utility.wg_seconds_to_wait ) {
			++wg_times_erroded;
			wg_time_of_last_operation = (System.nanoTime() / 1000000) / 1000.0f;
			if (wg_times_erroded == utility.wg_ErrodeIterations)  {
				++wg_state;
			} else {
				GenerateWorld_ErodeEdges();
			}
		}

		if (wg_state == 8) {
			//Remove un-neaded water
			GenerateWorld_TrimWater();
			wg_finished = true;
		}

		return wg_state;
	}

	private int GenerateWorld_Apply_kT() {
		//apply kT
		int loop = 0;
		while (true) {
			if (++loop % 20 == 0) return -1; //Algo in progress
			float min = utility.minimiser_start;
			WorldChunk _c1 = null;
			WorldChunk _c2 = null;
			//Do loop
			for (final WorldChunk c1 : chunks) {
				float d_i = c1.GetState();
				if (d_i == 0) {
					continue;
				}
				d_i = d_i * d_i;
				if (d_i < min) {
					//System.out.println("d_i is a min:"+d_i);
					min = d_i;
					_c1 = c1;
					_c2 = null;
				}
				for (final WorldChunk c2 : chunks) {
					if (c1 == c2) {
						continue;
					}
					float d_j = c2.GetState();
					if (d_j == 0) {
						continue;
					}
					d_j = d_j * d_j;
					//System.out.println("separation of ("+c1.GetX()+","+c1.GetY()+") ("+c2.GetX()+","+c2.GetY()+") :"+GetSeperation(c1, c2));
					final float d_ij = (float) (Math.min(d_i, d_j) * ( Math.pow(GetSeperation(c1, c2), 2) / Math.pow(utility.wg_kT_R, 2) ));
					if (d_ij < min) {
						//System.out.println("d_ij is a min:"+d_ij);
						min = d_ij;
						_c1 = c1;
						_c2 = c2;
					}
				}
			}
			if (min == utility.minimiser_start)
			 {
				break; //If no change in loop
			}
			if (_c2 == null) { //Smallest is a d_i
				_c1.SetState(0);
			} else {  //Smallest is a d_ij - combine
				if (_c2.GetState() > _c1.GetState()) {//Assume c1 > c2 so switch if not
					final WorldChunk _temp = _c2;
					_c2 = _c1;
					_c1 = _temp;
				}
				_c1.AddState( _c2.GetState() );
				_c2.SetState(0);
				MergeBiomes(_c1.GetBiomeID(), _c2.GetBiomeID());
			}
		}
		//Done, count biomes in tally
		final int tally[] = new int[(world_chunks*world_chunks)+1];
		int nBiomes = 0;
		for (final WorldChunk c : chunks) {
			if (c.GetBiomeID() > 0 && tally[c.GetBiomeID()] == 0) {
				tally[c.GetBiomeID()] = ++nBiomes;
			}
		}
		//Give sequential IDs
		for (final WorldChunk c : chunks)
		 {
			c.SetBiomeID( tally[c.GetBiomeID()] + 1 ); //Go from 2, ocean biome is one
		}
		System.out.println("NBiomes:"+nBiomes);
		return nBiomes;
	}

	private boolean GenerateWorld_AssignBiomes() {
		//Create Biome objects
		biomes.clear();
		ocean = new Biome(1, this);
		ocean.AssignBiomeType(BiomeType.WATER);
		for (final WorldChunk c : chunks) {
			Biome _b = null;
			final int BiomeID = c.GetBiomeID();
			if (BiomeID == 0) {
				continue;
			}
			for (final Biome b : biomes) {
				if (b.GetID() == BiomeID) { // add this chunk to biome
					_b = b;
					break;
				}
			}
			if (_b == null) {
				//System.out.println("make biome with ID:"+BiomeID);
				_b = new Biome(BiomeID, this);
				biomes.add(_b);
			}
			//Now loop over tiles which belong to this chunk, add them to the biome
			final int x_offset = c.GetX();
			final int y_offset = c.GetY();
		    for (int x = x_offset; x < x_offset + chunks_size; x = x + tiles_size) {
			    for (int y = y_offset; y < y_offset + chunks_size; y = y + tiles_size) {
					final int tileID = utility.XYtoID(x, y, world_tiles, tiles_size);
					//ystem.out.println("want:("+x+","+y+") TileID:"+tileID);
					//System.out.println("   Got:("+tiles[tileID].GetX()+","+tiles[tileID].GetY()+")");
					final WorldTile t = tiles[tileID];
					if (t == null) continue;
					final float radius = (island_size/2) + island_offset[t.GetAngle()];
					if (t.GetDistanceFromPoint(0,0) < radius) {
						_b.AddTile(t);
						render_tiles.add(t);
					}
				}
			}
		}

	    //Each biome can now work out its centre of gravity
		for (final Biome b : biomes) {
			b.CalculateCentre();
		}

		//Now biomes own their base tiles, need to assign biome types.
		//First populate nearest to home base of teams.
		//PLAYER = 315 degrees (SE). ENEMY = 135 degrees (NW).
		final float player_radius = (island_size + (2 * island_offset[utility.wg_PlayerBaseAngle])) / 2;
		final float enemy_radius = (island_size + (2 * island_offset[utility.wg_EnemyBaseAngle])) / 2;
		final int home_player = (int) Math.round( Math.sqrt(Math.pow(player_radius * utility.wg_MainBaseRadius, 2) / 2) );
		final int home_enemy = (int) -Math.round( Math.sqrt(Math.pow(enemy_radius * utility.wg_MainBaseRadius, 2) / 2) );

		//System.out.println("homePlayer:"+home_player+" homeEnemy:"+home_enemy);

		BiomeType toAssign_type = null;
		for (int toAssign = 0; toAssign < 7; ++toAssign) {
			float minDistancePlayer = utility.minimiser_start;
			float minDistanceEnemy = utility.minimiser_start;
			Biome toAssign_payer = null;
			Biome toAssign_enemy = null;
			for (final Biome b : biomes) {
				if (b.GetBiomeType() == BiomeType.NONE) {
					if (b.GetDistanceFromPoint(home_player, home_player) < minDistancePlayer) {
						minDistancePlayer = b.GetDistanceFromPoint(home_player, home_player);
						toAssign_payer = b;
					}
					if (b.GetDistanceFromPoint(home_enemy, home_enemy) < minDistanceEnemy) {
						minDistanceEnemy = b.GetDistanceFromPoint(home_enemy, home_enemy);
						toAssign_enemy = b;
					}
				}
			}
			if (toAssign_payer == null || toAssign_enemy == null || toAssign_payer == toAssign_enemy) return false; //catastrophic fail
			switch (toAssign) {
				case 0:	toAssign_type = BiomeType.GRASS;
				break;
				case 1: toAssign_type= BiomeType.DESERT;
				break;
				case 2: toAssign_type = BiomeType.FORREST;
				break;
				case 3:	toAssign_type = BiomeType.GRASS;
				break;
				case 4: toAssign_type= BiomeType.FORREST;
				break;
				case 5: toAssign_type = BiomeType.DESERT;
				break;
				case 6: toAssign_type = BiomeType.WATER;
				break;
			}
			toAssign_payer.AssignBiomeType(toAssign_type);
			toAssign_enemy.AssignBiomeType(toAssign_type);
		}

		//Assign rest at random
		for (final Biome b : biomes) {
			if (b.GetBiomeType() != BiomeType.NONE) {
				continue;
			}
			final int toAssign = utility.rnd.nextInt(7);
			switch (toAssign) {
				case 0: case 1:	toAssign_type = BiomeType.GRASS;
				break;
				case 2: case 3: toAssign_type = BiomeType.DESERT;
				break;
				case 4: case 5: toAssign_type = BiomeType.FORREST;
				break;
				case 6: toAssign_type = BiomeType.WATER;
				break;
			}
			b.AssignBiomeType(toAssign_type);
		}

		//Assign outside water
		GenerateWorld_PopulateOcean();
		return true;
	}

	public void GenerateWorld_CrinkleIslandEdge() {
		//Seed edges
		for (int current = 0; current < utility.wg_DegInCircle; ++current) {
			//System.out.println("-ANGLE "+current+" 100*cosAngle:"+100*Math.cos(Math.toRadians(current))+" 100*sinAngle:"+100*-Math.sin(Math.toRadians(current)));
			island_offset[current] = Math.round( utility.rnd.nextGaussian() * tiles_size * utility.wg_CrinkleScale );
		}
		//Smooth edges
		boolean changes = true;
		while (changes == true) {
			changes = false;
			for (int current = 0; current < utility.wg_DegInCircle; ++current) {
				int prev = current - 1;
				if (prev == -1) {
					prev = (utility.wg_DegInCircle-1);
				}
				int next = current + 1;
				if (next == utility.wg_DegInCircle) {
					next = 0;
				}
				final long left_diff = island_offset[prev] - island_offset[current];
				final long right_diff = island_offset[next] - island_offset[current];
				//System.out.println("BEFORE leftDiff:"+left_diff+" rightDiff:"+right_diff);
				if (left_diff > utility.wg_CrinkleCoarseness) {
					island_offset[prev] -= 1;
					changes = true;
				} else if (left_diff < -utility.wg_CrinkleCoarseness) {
					island_offset[prev] += 1;
					changes = true;
				}
				if (right_diff > utility.wg_CrinkleCoarseness) {
					island_offset[next] -= 1;
					changes = true;
				} else if (right_diff < -utility.wg_CrinkleCoarseness) {
					island_offset[next] += 1;
					changes = true;
				}
			}
		}
	}

	public void GenerateWorld_ErodeEdges() {
		for (final WorldTile t : render_tiles) {
			final Biome myOwner = t.GetOwner();
			final Vector<WorldTile> neighbours = GetNeighbour(t,false);
			final Vector<WorldTile> possibleSwaps = new Vector<WorldTile>();
			for (final WorldTile n : neighbours) {
				if (n.GetOwner() != myOwner) {
					possibleSwaps.add(n);
				}
			}
			if (possibleSwaps.size() > 0 && utility.rnd.nextFloat() > 0.5) {
				//Give this tile to the other biome
				final WorldTile tile_to_give = possibleSwaps.elementAt( utility.rnd.nextInt(possibleSwaps.size()) );
				//Biome OwnerBiome = tile_to_give.GetOwner(); //TODO STILL NOT WORKINg
				//OwnerBiome.RemoveTile(tile_to_give);
				myOwner.AddTile(tile_to_give);
			}
		}
	}

	private void GenerateWorld_PopulateOcean() {
		for (final WorldTile t : tiles) {
			if (t.GetPartOfBiome() == false) {
				render_tiles.add(t);
				ocean.AddTile(t);
			}
		}
	}
	
	private void GenerateWorld_TrimWater() {
		for (final WorldTile t : tiles) {
			if (t.GetPartOfBiome() == true && t.GetBiomeType() == BiomeType.WATER) {
				final Vector<WorldTile> neighbours = GetNeighbour(t,true);
				int nLand = 0;
				for (final WorldTile n : neighbours) {
					if (n.GetPartOfBiome() == true && n.GetBiomeType() != BiomeType.WATER) {
						++nLand;
					}
				}
				if (nLand == 0 && utility.rnd.nextFloat() > utility.wg_PercChanceKeepWater) {
					render_tiles.remove(t);
				}
			}
		}
	}




	private void GenerateWorld_RandomSeed() {
		int BiomeID = 0;
		for (final WorldChunk c : chunks) {
			final int angle = c.GetAngle();
			final float radius = (island_size/2) + island_offset[angle];
			//System.out.println("RandSeed angle:"+angle+" has dev:"+island_offset[angle]+" and tot radius:"+radius);
			if (c.GetDistanceFromPoint(0,0) < radius + chunks_size ) { //Only leaves the corners
				c.SetState((utility.rnd.nextFloat()*(utility.wg_kTEndPt-utility.wg_kTStartPt))+utility.wg_kTStartPt);
				c.SetBiomeID(++BiomeID);
				//System.out.println("state:"+c.GetState()+" ID:"+c.GetBiomeID());
			}
		}
	}


	public WorldPoint GetIdeadStartingLocation(ObjectOwner _o) {
		float home_rad;
		float home_ang;
		if (_o == ObjectOwner.Player) {
			home_rad = (float) Math.sqrt(Math.pow(((island_size + (2 * island_offset[utility.wg_PlayerBaseAngle])) / 2) * utility.wg_MainBaseRadius, 2) / 2);
			home_ang = utility.wg_PlayerBaseAngle;
		} else {
			home_rad = (float) Math.sqrt(Math.pow(((island_size + (2 * island_offset[utility.wg_EnemyBaseAngle])) / 2) * utility.wg_MainBaseRadius, 2) / 2);
			home_ang = utility.wg_EnemyBaseAngle;
		}
		return utility.PolarToCartesian(home_ang, home_rad);
	}


	private Vector<WorldTile> GetNeighbour(WorldTile _t, boolean _nnn) {
		int toGet = 4;
		if (_nnn == true) {
			toGet = 8;
		}
		final Vector<WorldTile> _n_tiles = new Vector<WorldTile>();
		for (int _neighbour = 0; _neighbour < toGet; ++_neighbour) {
			int _x = _t.GetX();
			int _y = _t.GetY();
			switch (_neighbour) {
				case 0:
				_x += tiles_size; break;
				case 1:
				_x -= tiles_size; break;
				case 2:
				_y += tiles_size; break;
				case 3:
				_y -= tiles_size; break;
				case 4:
				_x += tiles_size;
				_y += tiles_size; break;
				case 5:
				_x += tiles_size;
				_y -= tiles_size; break;
				case 6:
				_x -= tiles_size;
				_y -= tiles_size; break;
				case 7:
				_x -= tiles_size;
				_y += tiles_size; break;
			}
			final int _ID = utility.XYtoID(_x, _y, world_tiles, tiles_size);
			if (_ID != -1) {
				_n_tiles.add(tiles[_ID]);
			}
		}
		return _n_tiles;
	}

	public HashSet<WorldTile> GetRenderTiles() {
		return render_tiles;
	}

	private float GetSeperation(WorldChunk c1, WorldChunk c2) {
		final float x_diff = c1.GetXCentre() - c2.GetXCentre();
		final float y_diff = c1.GetYCentre() - c2.GetYCentre();
		return (float) Math.sqrt( Math.pow(x_diff, 2) + Math.pow(y_diff, 2));
	}

	public int GetTileSize() {
		return tiles_size;
	}

	public boolean GetWorldGenerated(){
		return wg_finished;
	}

	private void MergeBiomes(int _b1, int _b2) {
		for (final WorldChunk c : chunks) {
			if (c.GetBiomeID() == _b2) {
				c.SetBiomeID(_b1);
			}
		}
	}

	public void Reset() {
		for (final WorldChunk c : chunks) {
			c.Reset();
		}
		for (final WorldTile t : tiles) {
			t.Reset();
		}
		for (int current = 0; current < utility.wg_DegInCircle; ++current) {
			island_offset[current] = 0;
		}
		render_tiles.clear();
		biomes.clear();
		wg_times_erroded = 0;
		wg_finished = false;
		wg_state = 0;
	}

//	public Vector<Base> GenerateWorld_AddStartingUnits() {
//		//Find somewhere to put player base
//		Vector<Base> bases = new Vector<Base>();
//		float player_radius = (island_size + (2 * island_offset[315])) / 2;
//		float enemy_radius = (island_size + (2 * island_offset[135])) / 2;
//		int home_player = (int) Math.round( Math.sqrt(Math.pow(player_radius * wg_MainBaseRadius, 2) / 2) );
//		int home_enemy = (int) Math.round( Math.sqrt(Math.pow(enemy_radius * wg_MainBaseRadius, 2) / 2) );
//		int loop = 0;
//		Point location_payer = PolarToCartesian(315, home_player);
//		while (++loop < 200) {
//			int _x = (int) Math.round(location_payer.getX() + (rnd.nextGaussian() * loop));
//			int _y = (int) Math.round(location_payer.getY() + (rnd.nextGaussian() * loop));
//			if (CheckSafeToPlace(_x, _y, 8)) {
//				System.out.println("PLACED PLAYER AT X:"+_x+" Y:"+_y);
//				bases.add( new Base(_x, _y, 8, Sprite.PLAYER) );
//				break;
//			} else System.out.println("FAILED TO PLACED PLAYER AT X:"+_x+" Y:"+_y);
//		}
//		loop = 0;
//		Point location_enemy = PolarToCartesian(135, home_enemy);
//		while (++loop < 200) {
//			int _x = (int) Math.round(location_enemy.getX() + (rnd.nextGaussian() * loop));
//			int _y = (int) Math.round(location_enemy.getY() + (rnd.nextGaussian() * loop));
//			if (CheckSafeToPlace(_x, _y, 8)) {
//				System.out.println("PLACED ENEMY AT X:"+_x+" Y:"+_y);
//				bases.add( new Base(_x, _y, 8, Sprite.ENEMY) );
//				break;
//			} else System.out.println("FAILED TO PLACED ENEMY AT X:"+_x+" Y:"+_y);
//		}
//		if (bases.size() == 2) return bases;
//		System.out.println("COULD NOT PLACE PLAYER AND ENEMY");
//		return null;
//	}

}
