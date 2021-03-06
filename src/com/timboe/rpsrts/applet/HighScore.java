package com.timboe.rpsrts.applet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.timboe.rpsrts.enumerators.GameStatistics;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.enumerators.Pass;
import com.timboe.rpsrts.managers.ResourceManager;
import com.timboe.rpsrts.managers.Utility;

public class HighScore implements Runnable {

	private final Utility utility = Utility.GetUtility();
	private final ResourceManager resource_manager = ResourceManager.GetResourceManager();

	public HighScore() {
	}

	@Override
	public void run() {
		try {
			SubmitHighScore();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void SubmitHighScore() throws IOException {
		//Create Post String
		//Thanks to http://robbamforth.wordpress.com/2009/04/27/java-how-to-post-to-a-htmlphp-post-form/
		String data = URLEncoder.encode("pwd",    "UTF-8") + "=" + URLEncoder.encode(Pass.GetPass(), "UTF-8");
		data += "&" + URLEncoder.encode("name",    "UTF-8") + "=" + URLEncoder.encode(utility.playerName, "UTF-8");
		data += "&" + URLEncoder.encode("seed",    "UTF-8") + "=" + URLEncoder.encode(utility.rndSeedTxt, "UTF-8");
		data += "&" + URLEncoder.encode("score",   "UTF-8") + "=" + URLEncoder.encode(Integer.toString(resource_manager.GetScore(ObjectOwner.Player)), "UTF-8");
		data += "&" + URLEncoder.encode("enemyscore",   "UTF-8") + "=" + URLEncoder.encode(Integer.toString(resource_manager.GetScore(ObjectOwner.Enemy)), "UTF-8");
		data += "&" + URLEncoder.encode("wintime", "UTF-8") + "=" + URLEncoder.encode(Long.toString(utility.game_time_count/1000l), "UTF-8");
		//Add statistics
		data += "&" + URLEncoder.encode("buildingsconstructed", "UTF-8") + "=" + URLEncoder.encode(resource_manager.GetStatistic(GameStatistics.BuildingsConstructed), "UTF-8");
		data += "&" + URLEncoder.encode("projectilesfired", "UTF-8") + "=" + URLEncoder.encode(resource_manager.GetStatistic(GameStatistics.ProjectilesFired), "UTF-8");
		data += "&" + URLEncoder.encode("resourcesplundered", "UTF-8") + "=" + URLEncoder.encode(resource_manager.GetStatistic(GameStatistics.ResourcesPlundered), "UTF-8");
		data += "&" + URLEncoder.encode("rocksassembled", "UTF-8") + "=" + URLEncoder.encode(resource_manager.GetStatistic(GameStatistics.RocksAssembled), "UTF-8");
		data += "&" + URLEncoder.encode("specialunitsspawned", "UTF-8") + "=" + URLEncoder.encode(resource_manager.GetStatistic(GameStatistics.SpecialUnitsSpawned), "UTF-8");
		data += "&" + URLEncoder.encode("buildingsexploded", "UTF-8") + "=" + URLEncoder.encode(resource_manager.GetStatistic(GameStatistics.BuildingsExploded), "UTF-8");
		data += "&" + URLEncoder.encode("treeschopped", "UTF-8") + "=" + URLEncoder.encode(resource_manager.GetStatistic(GameStatistics.TreesChopped), "UTF-8");
		data += "&" + URLEncoder.encode("troopsslaughtered", "UTF-8") + "=" + URLEncoder.encode(resource_manager.GetStatistic(GameStatistics.TroopsSlaughtered), "UTF-8");
		data += "&" + URLEncoder.encode("unitspoisoned", "UTF-8") + "=" + URLEncoder.encode(resource_manager.GetStatistic(GameStatistics.UnitsPoisoned), "UTF-8");

		// Send Data To Page
		final URL url = new URL("http://tim-martin.co.uk/rpsrts_score.php");
		final URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		final OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();

		// Get The Response
		final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			System.out.println(line);
		}
	}
}