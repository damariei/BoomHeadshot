package com.damariei.bh;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
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
	
	
	public void onEnable(){ 
		log = this.getLogger();
		
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
			
			this.saveConfig();
		} else {
			headMod = this.getConfig().getDouble("head");
			chestMod = this.getConfig().getDouble("chest");
			legMod = this.getConfig().getDouble("legs");
			feetMod = this.getConfig().getDouble("feet");
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
			if (event.getEntity().getType()==EntityType.ZOMBIE
					||event.getEntity().getType()==EntityType.PLAYER) {
				
				// Check to see where it got hit and apply a modifier
				Location arrowLoc = event.getDamager().getLocation();
				Location zombieLoc = event.getEntity().getLocation();
				
				/* Note: the following calculated Y-values where shamelessly taken
				 * from the MorePhyics mod (http://dev.bukkit.org/server-mods/morephysics/)
				 * Thank you!
				 */
				double diffY = arrowLoc.getY()-zombieLoc.getY();
				double modifier = 1;
				if(diffY < 2 && diffY > 1.55) {
					modifier = headMod;
					getServer().getPlayer("dim3000").sendMessage("HEAD!");
				} else if(diffY < 1.55 && diffY > .8) {
					modifier = chestMod;
				getServer().getPlayer("dim3000").sendMessage("HEAD!");
				} else if(diffY < .8 && diffY > .45) {
					modifier = legMod;
				getServer().getPlayer("dim3000").sendMessage("HEAD!");
				} else if(diffY < .45 && diffY > 0) {
					modifier = feetMod;
				getServer().getPlayer("dim3000").sendMessage("HEAD!");
				}
				event.setDamage((int) (event.getDamage()*modifier));
			}
			
		}
		
	}
}
