package com.timboeWeb.rpsrts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;

import com.timboe.rpsrts.GameWorld;
import com.timboe.rpsrts.Utility;
import com.timboe.rpsrts.WorldTile;


public class GameWorld_Applet extends GameWorld {

	public GameWorld_Applet(int _world_tiles, int _tiles_size, int _tiles_per_chunk, Utility _utility) {
		super(_world_tiles, _tiles_size, _tiles_per_chunk, _utility);
		
		tiles = new WorldTile_Applet[world_tiles*world_tiles];
		chunks = new WorldChunk_Applet[world_chunks*world_chunks];
		render_tiles = new HashSet<WorldTile>();
		
		int ID = 0;
	    for (int x = -(world_size/2); x < (world_size/2); x = x + tiles_size) {
		    for (int y = -(world_size/2); y < (world_size/2); y = y + tiles_size) {
		    	tiles[ID++] = new WorldTile_Applet(x,y,tiles_size,ID,utility);
		    }
	    }

	    ID = 0;
	    for (int x = -(world_size/2); x < (world_size/2); x = x + chunks_size) {
		    for (int y = -(world_size/2); y < (world_size/2); y = y + chunks_size) {
		    	chunks[ID++] = new WorldChunk_Applet(x,y,chunks_size,ID,utility);
		    }
	    }
	}
	
	public void DrawTiles(Graphics2D g2, boolean _renderAll, boolean _aa) {
		if (_renderAll == true) {
			for (final WorldTile_Applet t : (WorldTile_Applet[]) tiles) {
				t.DrawTile(g2,_aa,false);
			}
		} else {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			HashSet<WorldTile_Applet> render_tiles_app = (HashSet) render_tiles;
			for (final WorldTile_Applet t : render_tiles_app) {
				t.DrawTile(g2,_aa,false);
			}
		}
	}
	
	public void DrawChunks(Graphics2D _g2, boolean _useEnergy, boolean _aa) {
		
		for (final WorldChunk_Applet c : (WorldChunk_Applet[]) chunks) {
			c.DrawTileState(_g2, _useEnergy, _aa);
		}
	}

	public void DrawIslandEdge(Graphics2D g2) {
		g2.setColor (Color.white);
		int angle = 0;
		for (final long edge : island_offset) {
			final int e = (int)edge;
			final int new_radius = (island_size/2)+e;
			g2.drawArc(-new_radius,-new_radius,2*new_radius,2*new_radius,angle,1);
			++angle;
		}
	}
	
//	public void HighlightTile(int _x, int _y, Graphics2D _g2, boolean _aa) {
//	final int _ID = utility.XYtoID(_x, _y, world_tiles, tiles_size);
//	if (_ID != -1) {
//		tiles[_ID].DrawTile(_g2, _aa, true);
//	}
//
//}
}