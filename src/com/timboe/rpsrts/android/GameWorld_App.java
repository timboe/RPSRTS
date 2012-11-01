package com.timboe.rpsrts.android;

import java.util.HashSet;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.timboe.rpsrts.applet.WorldChunk_Applet;
import com.timboe.rpsrts.applet.WorldTile_Applet;
import com.timboe.rpsrts.managers.GameWorld;
import com.timboe.rpsrts.world.WorldTile;

public class GameWorld_App extends GameWorld {

	public GameWorld_App() {
		super();
	}
	
	public void DrawChunks(Canvas canvas, boolean _useEnergy, boolean _aa) {
		
		for (final WorldChunk_App c : (WorldChunk_App[]) chunks) {
			c.DrawTileState(canvas, _useEnergy, _aa);
		}
	}
	
	public void DrawIslandEdge(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		int angle = 0;
		for (final long edge : island_offset) {
			final int e = (int)edge;
			final int new_radius = (island_size/2)+e;
			RectF _rect = new RectF(-new_radius, -new_radius, new_radius, new_radius);
			int toDraw = angle + 30;
			if (toDraw > 360) toDraw -= 360;
			canvas.drawArc(_rect, toDraw, 1, false, paint);
			++angle;
		}
	}
	
	public void DrawTiles(Canvas canvas, boolean _renderAll, boolean _aa) {
		if (_renderAll == true) {
			for (final WorldTile_App t : (WorldTile_App[]) tiles) {
				t.DrawTile(canvas,_aa,false);
			}
		} else {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			HashSet<WorldTile_App> render_tiles_app = (HashSet) render_tiles;
			for (final WorldTile_App t : render_tiles_app) {
				t.DrawTile(canvas,_aa,false);
			}
		}
	}

	@Override
	public void Init() {
		world_tiles = utility.world_tiles;
		tiles_per_chunk = utility.tiles_per_chunk;
		world_chunks = world_tiles / tiles_per_chunk;
		tiles_size = utility.tiles_size;
		chunks_size = utility.tiles_size * utility.tiles_per_chunk;
		world_size = world_tiles * tiles_size;
		island_size = (int) Math.round(utility.island_scale * world_size);
		
		tiles = new WorldTile_Applet[world_tiles*world_tiles];
		chunks = new WorldChunk_Applet[world_chunks*world_chunks];
		render_tiles = new HashSet<WorldTile>();
		
		int ID = 0;
	    for (int x = -(world_size/2); x < (world_size/2); x = x + tiles_size) {
		    for (int y = -(world_size/2); y < (world_size/2); y = y + tiles_size) {
		    	tiles[ID++] = new WorldTile_App(x,y,tiles_size,ID);
		    }
	    }

	    ID = 0;
	    for (int x = -(world_size/2); x < (world_size/2); x = x + chunks_size) {
		    for (int y = -(world_size/2); y < (world_size/2); y = y + chunks_size) {
		    	chunks[ID++] = new WorldChunk_App(x,y,chunks_size,ID);
		    }
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
