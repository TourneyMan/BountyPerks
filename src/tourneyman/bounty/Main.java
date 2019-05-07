package tourneyman.bounty;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy; 

public class Main extends JavaPlugin implements Listener{
	public static Economy economy = null;
	
	@Override
	public void onEnable() {
		setupEconomy();
		getConfig().options().copyDefaults(true);
		saveConfig();
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("BountyPerks+ has been Enabled!");
	}
	
	@Override
	public void onDisable() {
		saveConfig();
		getLogger().info("BountyPerks+ has closed down");
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	
        /******************************
         ******** BOUNTY GUIDE ********
         *****************************/
        if (command.getName().equalsIgnoreCase("bountyguide")) {
        	if (sender instanceof Player){
        		sender.sendMessage(ChatColor.GREEN + "If you kill another player, money is automatically added to your bounty. Players can also add money to a person's bounty with /addbounty. "
        				+ "If you kill someone, you get all the money in their bounty and their bounty is reset. As your bounty rises, so does your rank. "
        				+ "This gives perks like permanent resistance and strength. However, it also makes it "
        				+ "easier for people to track you down with /bountytrack");
        	}
            return true;
        }
        
        /******************************
         ******** BOUNTY INFO *********
         *****************************/
        if (command.getName().equalsIgnoreCase("bountyinfo")) {
        	if (sender instanceof Player){
        		sender.sendMessage(ChatColor.RED + "[BountyPerks+]" + ChatColor.GREEN + " List of Bounty Ranks:"); 
        		sender.sendMessage(ChatColor.GREEN + "Rebel ~ $" + getConfig().getInt("BountyLevel.rebel") + " ~ Enemies can make you glow");
        		sender.sendMessage(ChatColor.GREEN + "Petty Criminal ~ $" + getConfig().getInt("BountyLevel.pettycriminal") + " ~ Permanent Speed I");
        		sender.sendMessage(ChatColor.GREEN + "Bandit ~ $" + getConfig().getInt("BountyLevel.bandit") + " ~ Permanent Strength I");
        		sender.sendMessage(ChatColor.GREEN + "Rogue ~ $" + getConfig().getInt("BountyLevel.rogue") + " ~ Enemies can view your coordinates");
        		sender.sendMessage(ChatColor.GREEN + "Ruffian ~ $" + getConfig().getInt("BountyLevel.ruffian") + " ~ Permanent Resistance I");
        		sender.sendMessage(ChatColor.GREEN + "Thug ~ $" + getConfig().getInt("BountyLevel.thug") + " ~ Permanent Speed II");
        		sender.sendMessage(ChatColor.GREEN + "Outlaw ~ $" + getConfig().getInt("BountyLevel.outlaw") + " ~ Enemies can teleport to you");
        		sender.sendMessage(ChatColor.GREEN + "Hitman ~ $" + getConfig().getInt("BountyLevel.hitman") + " ~ Permanent Strength II");
        		sender.sendMessage(ChatColor.GREEN + "Mafia Boss ~ $" + getConfig().getInt("BountyLevel.mafiaboss") + " ~ Far too many bonuses to list");
        	}
            return true;
        }
    	
        /******************************
         ********* MY BOUNTY **********
         *****************************/
        else if (command.getName().equalsIgnoreCase("mybounty")) {
        	if (sender instanceof Player)
        		sender.sendMessage(ChatColor.RED + "[BountyPerks+]" + ChatColor.GREEN + " Your bounty is $" + getConfig().getInt("Bounty." + sender.getName().toLowerCase()));
            return true;
        }
        
        /******************************
         ********** BOUNTY ************
         *****************************/
        else if (command.getName().equalsIgnoreCase("bounty")) {
        	//break this into 2 error messages
        	if (args.length == 1 && getConfig().isSet("Bounty." + args[0].toLowerCase())) {
        		sender.sendMessage(ChatColor.RED + "[BountyPerks+]" + " " + ChatColor.BLUE +  args[0] + ChatColor.GREEN + " has a bounty of $" + Integer.toString(getConfig().getInt("Bounty." + args[0].toLowerCase())));
        	}
        	else {
        		sender.sendMessage(ChatColor.RED  + "[BountyPerks+]" + ChatColor.GREEN + " Command should be of the form /bounty {player name}");
        	}
        	return true;
        }
        
        /******************************
         ******* MY BOUNTY RANK *******
         *****************************/
        else if (command.getName().equalsIgnoreCase("mybountyrank")) {
    		String rankName = getBountyRank(sender.getName().toLowerCase());
    		if (rankName.equals("Outlaw")) {
    			sender.sendMessage(ChatColor.RED + "[BountyPerks+]" + ChatColor.GREEN + " You are an " + rankName);
    		}
    		else {
    			sender.sendMessage(ChatColor.RED + "[BountyPerks+]" + ChatColor.GREEN + " You are a " + rankName);
    		}
        	return true;
        }
        
         /*****************************
         ******** BOUNTY RANK *********
         *****************************/
        else if (command.getName().equalsIgnoreCase("bountyrank")) {
        	if (args.length == 1 && getConfig().isSet("Bounty." + args[0].toLowerCase())) {
        		String rankName = getBountyRank(args[0].toLowerCase());
        		if (rankName.equals("Outlaw")) {
        			sender.sendMessage(ChatColor.RED + "[BountyPerks+]" + " " + ChatColor.BLUE +  args[0] + ChatColor.GREEN + " is an " + rankName);
        		}
        		else {
        			sender.sendMessage(ChatColor.RED + "[BountyPerks+]" + " " + ChatColor.BLUE +  args[0] + ChatColor.GREEN + " is a " + rankName);
        		}
        	}
        	else {
        		sender.sendMessage(ChatColor.RED  + "[BountyPerks+]" + ChatColor.GREEN + " Command should be of the form /bountyrank {player name}");
        	}
        	return true;
        }
        
        /******************************
         ******** ADD BOUNTY **********
         *****************************/
        else if (command.getName().equalsIgnoreCase("addbounty")) {
        	if (args.length == 2) {
        		if (sender instanceof Player) {
        			Player pSender = (Player) sender;
        			if (getConfig().isSet("Bounty." + args[0].toLowerCase())) {
        				try {
                			if (economy.getBalance(pSender) >= Integer.parseInt(args[1])) {
                				if (Integer.parseInt(args[1]) >= 0) {
                					economy.withdrawPlayer(pSender, Integer.parseInt(args[1]));
                					getConfig().set("Bounty." + args[0].toLowerCase(), getConfig().getInt("Bounty." + args[0].toLowerCase()) + Integer.parseInt(args[1]));
                					saveConfig();
                					reloadConfig();
                					updateBountyPerks(Bukkit.getServer().getPlayer(args[0]));
                					Bukkit.broadcastMessage(ChatColor.RED + "[BountyPerks+]" + ChatColor.BLUE + " " + pSender.getName() + ChatColor.GREEN + " has added $" + args[1] + " to " + ChatColor.BLUE + args[0].toLowerCase() + ChatColor.GREEN + "'s bounty.");
                				}
                				
                				else {
                					Bukkit.broadcastMessage(ChatColor.RED + "[BountyPerks+] " + ChatColor.GREEN + "Trying to add a negative bounty? That's clever. Nice try...");
                				}

                			}
                			else {
                				Bukkit.broadcastMessage(ChatColor.RED + "[BountyPerks+] " + ChatColor.GREEN + "Error: You do not have that much money. You only have $" + economy.getBalance(pSender));
                			}
        				}
        				
        				catch (NumberFormatException e) {
        					sender.sendMessage(ChatColor.RED  + "[BountyPerks+]" + ChatColor.GREEN + args[1] + " is not a valid bounty amount");
        				}
        			}
        			
        			else {
        				sender.sendMessage(ChatColor.RED + "[BountyPerks+] " + ChatColor.BLUE + args[0] + ChatColor.GREEN + " is not a valid player");
        			}
        		}
        	}
        	else
        		sender.sendMessage(ChatColor.RED + "[BountyPerks+]" + ChatColor.GREEN + " Command must be of the form /addbounty {player name} {bounty amount}");
        	return true;
        }
        
        /******************************
         ******** TOP BOUNTY **********
         *****************************/
        else if (command.getName().equalsIgnoreCase("topbounty")) {
        	String[] topList = new String[10];
        	for (int i = 0; i < 10; i++) {
        		topList[i] = "";
        	}
        	
        	HashMap<String, Object> rawList = (HashMap<String, Object>) this.getConfig().getConfigurationSection("Bounty").getValues(false);

        	for (int i = 0; i < 10; i++) { 
            	int highestValue = 0;
            	String highestPerson = "";
            	
            	if (!rawList.isEmpty()) {
                    for (Map.Entry<String, Object> entry : rawList.entrySet())
                    {
                    	int valueToCheck = (Integer) entry.getValue();
                        if (valueToCheck > highestValue) {
                        	highestValue = valueToCheck;
                        	highestPerson = entry.getKey();
                        }
                    }
                    rawList.remove(highestPerson);
            	}
            	
                topList[i] = highestPerson;
        	}
        	
        	for (int i = 0; i < 10; i++) {
        		if (!topList[i].equals("")) {
        			sender.sendMessage(ChatColor.GREEN + Integer.toString(i + 1) + ". " + ChatColor.BLUE + topList[i] + ChatColor.GREEN  + " - $" + getConfig().getInt("Bounty." + topList[i].toLowerCase()));
        		}
        	}
        	return true;
        }
        
        /******************************
         ******* BOUNTY TRACK *********
         *****************************/
        else if (command.getName().equalsIgnoreCase("bountytrack")) {
        	//break this into 2 error messages
        	if (args.length == 1 && getConfig().isSet("Bounty." + args[0].toLowerCase())) {
        		int amount = getConfig().getInt("Bounty." + args[0].toLowerCase());
        		if (amount >= getConfig().getInt("BountyLevel.rebel")) {
        			Player player = getServer().getPlayer(args[0]);
        			player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0));
            		if (amount >= getConfig().getInt("BountyLevel.rogue")) {
            			Location loc = player.getLocation();
            			sender.sendMessage(ChatColor.RED + "[BountyPerks+]" + ChatColor.GREEN + " Coordinates of " + ChatColor.BLUE + args[0] + ChatColor.GREEN + ":");
            			sender.sendMessage(ChatColor.GREEN + "X: " + loc.getBlockX());
            			sender.sendMessage(ChatColor.GREEN + "Y: " + loc.getBlockY());
            			sender.sendMessage(ChatColor.GREEN + "Z: " + loc.getBlockZ());
                		if (amount >= getConfig().getInt("BountyLevel.outlaw")) {
                			if (sender instanceof Player) {
                				Player senderPlayer = (Player) sender;
                				Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) this, new Runnable() 
                		    	{
                					@Override
                					public void run() {
                						senderPlayer.teleport(loc);
                					}
                		    	}, 40L);
                				
                			}
                		}
            		}
        		}
        	}
        	else {
        		sender.sendMessage(ChatColor.RED  + "[BountyPerks+]" + ChatColor.GREEN + " Command should be of the form /bountytrack {player name}");
        	}
        	return true;
        }       
        
        /******************************
         ******** SET BOUNTY **********
         *****************************/
        else if (command.getName().equalsIgnoreCase("setbounty")) {
        	if (args.length == 2) {
        		if (getConfig().isSet("Bounty." + args[0].toLowerCase())) {
        			try {
        				getConfig().set("Bounty." + args[0].toLowerCase(), Integer.parseInt(args[1]));
        				saveConfig();
        				reloadConfig();
        				updateBountyPerks(Bukkit.getServer().getPlayer(args[0]));
        				sender.sendMessage(ChatColor.RED  + "[BountyPerks+]" + ChatColor.BLUE + " " + args[0] + ChatColor.GREEN + " now has a bounty of $" + getConfig().getInt("Bounty." + args[0].toLowerCase()));
        			}
    				catch (NumberFormatException e) {
    					sender.sendMessage(ChatColor.RED  + "[BountyPerks+] " + ChatColor.GREEN + " " + args[1] + " is not a valid bounty amount");
    				}
        		}
        		else {
        			sender.sendMessage(ChatColor.RED + "[BountyPerks+]" + ChatColor.BLUE + " " + args[0] + ChatColor.GREEN + " is not a valid player");
        		}
        	}
        	else {
        		sender.sendMessage(ChatColor.RED + "[BountyPerks+]" + ChatColor.GREEN + " Command must be of the form /setbounty {player name} {bounty amount}");
        	}
        	return true;
        }
        
        /******************************
         **** SET BOUNTY PER KILL *****
         *****************************/
        
        else if (command.getName().equalsIgnoreCase("setbountyperkill")) {
        	//break this into 2 error messages
        	if (args.length == 1) {
        		try {
        			int newAmount = Integer.parseInt(args[0]);
        			getConfig().set("AddBountyPerKill", newAmount);
    				saveConfig();
    				reloadConfig();
        		}
        		catch (Exception e) {
        			sender.sendMessage(ChatColor.RED  + "[BountyPerks+]" + ChatColor.GREEN + " Error: \"Bounty per kill amount\" must be an integer");
        		}
        	}
        	else {
        		sender.sendMessage(ChatColor.RED  + "[BountyPerks+]" + ChatColor.GREEN + " Command should be of the form /setbountyperkill {amount}");
        	}
        	return true;
        }
        
        /******************************
         ******* TOGGLE MOB PAY *******
         *****************************/
        
        else if (command.getName().equalsIgnoreCase("togglemobpay")) {
        	//break this into 2 error messages
        	if (getConfig().getString("PayForMobKills").equals("true")) {
        		getConfig().set("PayForMobKills", "false");
    			saveConfig();
    			reloadConfig();
        		sender.sendMessage(ChatColor.RED  + "[BountyPerks+]" + ChatColor.GREEN + " Players will no longer be paid for killing mobs");
        	}
        	else {
        		getConfig().set("PayForMobKills", "true");
    			saveConfig();
    			reloadConfig();
        		sender.sendMessage(ChatColor.RED  + "[BountyPerks+]" + ChatColor.GREEN + " Players will now get paid for killing mobs");
        	}
        	return true;
        }
        
        /******************************
         ***** TOGGLE MOB MESSAGES ****
         *****************************/
        
        else if (command.getName().equalsIgnoreCase("togglemobmessages")) {
        	//break this into 2 error messages
        	if (getConfig().getString("MobKillMessages").equals("true")) {
        		getConfig().set("MobKillMessages", "false");
    			saveConfig();
    			reloadConfig();
        		sender.sendMessage(ChatColor.RED  + "[BountyPerks+]" + ChatColor.GREEN + " Players will no longer see messages when they get paid for killing mobs");
        	}
        	else {
        		getConfig().set("MobKillMessages", "true");
    			saveConfig();
    			reloadConfig();
        		sender.sendMessage(ChatColor.RED  + "[BountyPerks+]" + ChatColor.GREEN + " Players will now see messages when they get paid for killing mobs");
        	}
        	return true;
        }
        
        /******************************
         ***** CHANGE RANK AMOUNT *****
         *****************************/
        else if (command.getName().equalsIgnoreCase("changerankamount")) {
        	if (args.length == 2) {
        		if (getConfig().isSet("Bounty." + args[0].toLowerCase())) {
        			try {
        				getConfig().set("Bounty." + args[0].toLowerCase(), Integer.parseInt(args[1]));
        				saveConfig();
        				reloadConfig();
        				updateBountyPerks(Bukkit.getServer().getPlayer(args[0]));
        				sender.sendMessage(ChatColor.RED  + "[BountyPerks+] " + args[0] + ChatColor.GREEN + " is now set to $" + getConfig().getInt("Bounty." + args[0].toLowerCase()));
        			}
    				catch (NumberFormatException e) {
    					sender.sendMessage(ChatColor.RED  + "[BountyPerks+] " + ChatColor.GREEN + " " + args[1] + " is not a valid amount");
    				}
        		}
        		else {
        			sender.sendMessage(ChatColor.RED + "[BountyPerks+]" + ChatColor.GREEN + " " + args[0] + " is not a valid rank (no spaces in rank names)");
        		}
        	}
        	else {
        		sender.sendMessage(ChatColor.RED + "[BountyPerks+]" + ChatColor.GREEN + " Command must be of the form /changerankamount {rank name without spaces} {new amount} ");
        	}
        	return true;
        }
        
        return false;
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
    	LivingEntity deadEntity = event.getEntity();
    	LivingEntity killerEntity = event.getEntity().getKiller();
    	if (deadEntity instanceof Player && killerEntity instanceof Player) {
    		Player killerPlayer = (Player) killerEntity;
    		Player deadPlayer = (Player) deadEntity;
        	
			//Telling player about bounty they earned
        	if (getConfig().getInt("Bounty." + deadPlayer.getName().toLowerCase()) > 0) {
        		killerPlayer.sendMessage(ChatColor.RED + "[BountyPerks+]" + ChatColor.GREEN + " You collected " + ChatColor.BLUE + deadPlayer.getName() + ChatColor.GREEN + "'s bounty of $" + getConfig().getInt("Bounty." + deadPlayer.getName().toLowerCase()));
        	}
        		
    		//Adding money to the killer's bounty

        	if (!getConfig().isSet("LastKilled." + killerPlayer.getName().toLowerCase() + "." + deadPlayer.getName().toLowerCase()) || !getConfig().getString("LastKilled." + killerPlayer.getName().toLowerCase() + "." + deadPlayer.getName().toLowerCase()).equals((new SimpleDateFormat("MM/dd/yy")).format(Calendar.getInstance().getTime()))) {
        		getConfig().set("Bounty." + killerPlayer.getName().toLowerCase(), getConfig().getInt("Bounty." + killerPlayer.getName().toLowerCase()) + getConfig().getInt("AddBountyPerKill"));
        		getConfig().set("LastKilled." + killerPlayer.getName().toLowerCase() + "." + deadPlayer.getName().toLowerCase(), (new SimpleDateFormat("MM/dd/yy")).format(Calendar.getInstance().getTime()));
        		Bukkit.broadcastMessage(ChatColor.RED + "[BountyPerks+]" + ChatColor.BLUE + " " + killerPlayer.getName() + ChatColor.GREEN + "'s bounty has increased to $" + getConfig().getInt("Bounty." + killerPlayer.getName().toLowerCase()));
        	}
        	
			//Paying killer the deadPlayer's bounty
			economy.depositPlayer(killerPlayer, getConfig().getInt("Bounty." + deadPlayer.getName().toLowerCase()));
			getConfig().set("Bounty." + deadPlayer.getName().toLowerCase(), 0);
			saveConfig();
			reloadConfig();
			
			updateBountyPerks(deadPlayer);
			updateBountyPerks(killerPlayer);   	
    	}
    	
    	//Paying killer for killing mobs
    	else if (getConfig().getString("PayForMobKills").equals("true") && getConfig().isSet("MobMoney." + deadEntity.getName()) && killerEntity instanceof Player) {
    		Player killerPlayer = (Player) killerEntity;
    		economy.depositPlayer(killerPlayer, getConfig().getInt("MobMoney." + deadEntity.getName()));
    		if (getConfig().getString("MobKillMessages").equals("true") && getConfig().getInt("MobMoney." + deadEntity.getName()) != 0) {
    			killerPlayer.sendMessage(ChatColor.RED + "[BountyPerks+] " + ChatColor.GREEN + "You have been paid $" + getConfig().getInt("MobMoney." + deadEntity.getName()) + " for killing the " + deadEntity.getName());
    		}
    		
    	}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
    	String playerName = event.getPlayer().getName().toLowerCase();
    	updateBountyPerks(event.getPlayer());
    	if (!getConfig().isSet("Bounty." + playerName)) {
    		getConfig().set("Bounty." + playerName, 0);
    		saveConfig();
    		reloadConfig();
    	}
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onSpawn(PlayerRespawnEvent event) {
		Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) this, new Runnable() 
    	{
			@Override
			public void run() {
				updateBountyPerks(event.getPlayer());
				
			}
    		
    	}, 1L);
        
    }
    
    public void updateBountyPerks(Player player) {
    	for (PotionEffect effect : player.getActivePotionEffects()) {
    		player.removePotionEffect(effect.getType());
    	}
            
    	int currBounty = getConfig().getInt("Bounty." + player.getName().toLowerCase());
    	
		if (currBounty >= getConfig().getInt("BountyLevel.pettycriminal")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
    		if (currBounty >= getConfig().getInt("BountyLevel.bandit")) {
    			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
            	if (currBounty >= getConfig().getInt("BountyLevel.ruffian")) {
            		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
                	if (currBounty >= getConfig().getInt("BountyLevel.thug")) {
                		player.removePotionEffect(PotionEffectType.SPEED);
                		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                   		if (currBounty >= getConfig().getInt("BountyLevel.hitman")) {
                   			player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                   			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
                       		if (currBounty >= getConfig().getInt("BountyLevel.mafiaboss")) {
                       			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                       			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                       			player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, Integer.MAX_VALUE, 2));
                       			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
                       			player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
                       			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
                       			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
                       			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0));
                       			player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0));
                       		}
                   		}
               		}
           		}
    		}
		}
    }
    
    public String getBountyRank(String userName) {
    	int currBounty = getConfig().getInt("Bounty." + userName);
    	
    	if (currBounty < getConfig().getInt("BountyLevel.rebel")) {
    		return "Citizen";
    	}
    	
    	else if (currBounty < getConfig().getInt("BountyLevel.pettycriminal")) {
    		return "Rebel";
    	}
    	
    	else if (currBounty < getConfig().getInt("BountyLevel.bandit")) {
    		return "Petty Criminal";
    	}
    	
    	else if (currBounty < getConfig().getInt("BountyLevel.rogue")) {
    		return "Bandit";
    	}
    	
    	else if (currBounty < getConfig().getInt("BountyLevel.ruffian")) {
    		return "Rogue";
    	}
    	
    	else if (currBounty < getConfig().getInt("BountyLevel.thug")) {
    		return "Ruffian";
    	}
    	
    	else if (currBounty < getConfig().getInt("BountyLevel.outlaw")) {
    		return "Thug";
    	}
    	
    	else if (currBounty < getConfig().getInt("BountyLevel.hitman")) {
    		return "Outlaw";
    	}
    	
    	else if (currBounty < getConfig().getInt("BountyLevel.mafiaboss")) {
    		return "Hitman";
    	}
    	
    	else {
    		return "Mafia Boss";
    	}
    }
    
    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
