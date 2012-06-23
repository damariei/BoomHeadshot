package com.damariei.bh;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BoomHeadshot extends JavaPlugin implements Listener {
	
	Logger log;
	
	// Damage modifiers
	double headMod = 1.0;
	double chestMod = 1.0;
	double legMod = 1.0;
	double feetMod = 1.0;
	
	// Additional Vars.
	boolean includePlayers = false;
	String zombieMsg = "";
	String playerMsg = "";
	
	
	public void onEnable(){ 
		log = this.getLogger();
		this.getServer().getPluginManager().registerEvents(this, this);
		
		// Load mods from config
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
			try {
				new File(getDataFolder(),"config.yml").createNewFile();
			} catch (IOException e) { }
			
			this.getConfig().set("head", headMod);
			this.getConfig().set("chest", chestMod);
			this.getConfig().set("legs", legMod);
			this.getConfig().set("feet", feetMod);
			this.getConfig().set("includePlayers", includePlayers);
			this.getConfig().set("zombieHitMsg", zombieMsg);
			this.getConfig().set("playerHitMsg", playerMsg);
			
			this.saveConfig();
		} else {
			headMod = this.getConfig().getDouble("head");
			chestMod = this.getConfig().getDouble("chest");
			legMod = this.getConfig().getDouble("legs");
			feetMod = this.getConfig().getDouble("feet");
			includePlayers = this.getConfig().getBoolean("includePlayers");
			playerMsg = this.getConfig().getString("playerHitMsg");
			zombieMsg = this.getConfig().getString("zombieHitMsg");
		}
		
		log.info("Enabled!");
	}
	
	
	public void onDisable(){
		log.info("Disabled!");
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageByEntityEvent event)
	{
		
		// Check to see if it's a zombie/player getting hit by an arrow
		if (event.getDamager().getType()==EntityType.ARROW) {
			
			EntityType typeHit = event.getEntity().getType();
			if (typeHit==EntityType.ZOMBIE||(typeHit==EntityType.PLAYER && includePlayers)) {
				
				// Check to see where it got hit and apply a modifier
				Location arrowLoc = event.getDamager().getLocation();
				Location zombieLoc = event.getEntity().getLocation();
				
				/* Note: the following calculated Y-values where shamelessly taken
				 * from the MorePhyics mod (http://dev.bukkit.org/server-mods/morephysics/)
				 * Thank you!
				 */
				double diffY = arrowLoc.getY()-zombieLoc.getY();
				double modifier = 1;
				boolean isHeadshot = false;
				
				if(diffY < 2 && diffY > 1.55) {
					modifier = headMod;
					isHeadshot = true;
				} else if(diffY < 1.55 && diffY > .8) {
					modifier = chestMod;
				} else if(diffY < .8 && diffY > .45) {
					modifier = legMod;
				} else if(diffY < .45 && diffY > 0) {
					modifier = feetMod;
				}
				event.setDamage((int) (event.getDamage()*modifier));
				
				// Show custom message
				Arrow arrow = (Arrow)event.getDamager();
				
				if (isHeadshot && arrow.getShooter() instanceof Player) {
					Player pl = (Player)arrow.getShooter();
					
					if (typeHit==EntityType.ZOMBIE && !zombieMsg.equals("")) 
						pl.sendMessage(zombieMsg);
					
					if (typeHit==EntityType.PLAYER && !playerMsg.equals(""))
						pl.sendMessage(playerMsg);
					
				}
				
			}
			
		}
		
	}
}
