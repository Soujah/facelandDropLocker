package me.joshuaemq.dropLocker;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


public class DropLocker extends JavaPlugin implements Listener{
		
	FileConfiguration unlockable = this.getConfig();
	
	public void onEnable() {
	        Bukkit.getServer().getPluginManager().registerEvents(this, this);
	        Bukkit.getServer().getLogger().info("Drop Locker By Joshuaemq Enabled!");
	        getConfig().options().copyDefaults(false);
	        saveConfig();
	}
	
	private static final String LOCK_SYMBOL = "✖";
	private Sound lockSound = Sound.BLOCK_ANVIL_PLACE;
	private Sound unlockSound = Sound.BLOCK_PISTON_CONTRACT;

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		Item itemInHand = event.getItemDrop();
		
		if (!itemInHand.getItemStack().getItemMeta().hasLore()) {
			//p.sendMessage("throw exception");
    		return;
    	}
		if (itemInHand.getItemStack().getItemMeta().getLore().contains(ChatColor.RED + LOCK_SYMBOL)) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.YELLOW + "You can not drop a locked item!");
		}
	}
		
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	
        if (!(sender instanceof Player)) {
            Bukkit.getServer().getLogger().info("Console can not run this command!");
            return false;
        }
    	Player p = (Player) sender;
        Location playerLocation = p.getLocation();

        if (cmd.getName().equalsIgnoreCase("lockitem")) {
					ItemStack itemInHand = p.getEquipment().getItemInMainHand();
        	//if item says locked return, if item says unlocked lock it, if item has no lore return.
        	if (itemInHand == null || !itemInHand.hasItemMeta() || !itemInHand.getItemMeta().hasLore()) {
        		p.sendMessage("This can not be locked!");
        		return true;
        	}
        	
        	if (!unlockable.getString("unlockableItems").equals(null)) {
        		for (String unlockableString : unlockable.getStringList("unlockableItems")) {
            		if (itemInHand.getItemMeta().getDisplayName().toString().contains(unlockableString)) {
    					p.sendMessage(ChatColor.RED + "This item can not be locked!");
    					return true;
    				}
            	}
        	}

        	if (itemInHand.getItemMeta().getLore().contains(ChatColor.RED + LOCK_SYMBOL)) {
        		p.sendMessage(ChatColor.RED + "Item is already locked!");
        	}
        	
        	else {
        		if (itemInHand.getItemMeta().getLore() == null) {
        			//p.sendMessage(ChatColor.RED + "TEST MESSAGE: item has no lore");
        			return true;
        		} else {
                    List<String> ItemLore = itemInHand.getItemMeta().getLore();
                    ItemLore.add(ChatColor.RED + LOCK_SYMBOL);
                    
                    ItemMeta meta = itemInHand.getItemMeta();
                    meta.setLore(ItemLore);
                    itemInHand.setItemMeta(meta);
        			p.sendMessage(ChatColor.GREEN + "You locked: " + ChatColor.WHITE + itemInHand.getItemMeta().getDisplayName() + ChatColor.GREEN + "!");
                    p.playSound(playerLocation, lockSound, 1.0f, 1.5f);

				}
        	}
        }
        
       	if (cmd.getName().equalsIgnoreCase("unlockitem")) {
					ItemStack itemInHand = p.getEquipment().getItemInMainHand();
        	//if item says unlock return, if item says locked unlock it, if item has no lore return.
        	
        	if (itemInHand == null || !itemInHand.hasItemMeta()  || !itemInHand.getItemMeta().hasLore()) {
        		p.sendMessage("This can not be locked!");
        		return true;
        	}
        	
        	if (itemInHand.getItemMeta().getLore().contains(ChatColor.RED + LOCK_SYMBOL)) {
        		List<String> ItemLore = itemInHand.getItemMeta().getLore();
                ItemLore.remove(ChatColor.RED + LOCK_SYMBOL);
                
                ItemMeta meta = itemInHand.getItemMeta();
                meta.setLore(ItemLore);
                itemInHand.setItemMeta(meta);
    			p.sendMessage(ChatColor.GREEN + "You unlocked: " + ChatColor.WHITE + itemInHand.getItemMeta().getDisplayName() + ChatColor.GREEN + "!");
                p.playSound(playerLocation, unlockSound, 1.0f, 1.5f);
        	}
        	
        	else {
        		if (itemInHand.getItemMeta().getLore() == null) {
        			//p.sendMessage(ChatColor.RED + "TEST MESSAGE: item has no lore");
        			return true;
        		} else if (!itemInHand.getItemMeta().getLore().contains(ChatColor.RED + LOCK_SYMBOL)) {
        			p.sendMessage(ChatColor.RED + "Item is already unlocked!");
        		}
        	}
        }
       	if (cmd.getName().equalsIgnoreCase("setunlockable")) {
       		if (!p.hasPermission("droplocker.setunlockables")) {
       			p.sendMessage(ChatColor.RED + "You do not have permission to do this!");
       			return true;
       		}

			ItemStack itemInHand = p.getEquipment().getItemInMainHand();
       		
			if (itemInHand == null || itemInHand.getType().equals(Material.AIR) || !itemInHand.hasItemMeta() || !itemInHand.getItemMeta().hasLore()) {
				p.sendMessage(ChatColor.RED + "Invalid Item!");
				return true;
			}
			if (unlockable.getString("unlockableItems") !=  null) {
				if (unlockable.getString("unlockableItems").contains(itemInHand.getItemMeta().getDisplayName().toString())) {
					p.sendMessage(ChatColor.RED + "This item has already been set as an unlockable!");
					return true;
				}
			}
			
			List<String> unlockableNames = new ArrayList<String>();

			if (unlockable.getStringList("unlockableItems") == null) {
				unlockable.set("unlockableItems", itemInHand.getItemMeta().getDisplayName());
				p.sendMessage(ChatColor.RED + itemInHand.getItemMeta().getDisplayName().toString() + ChatColor.GREEN + " can no longer be locked!");
				saveConfig();
				return true;
			}
			
			for (String unlockableItemName : unlockable.getStringList("unlockableItems")) {
				unlockableNames.add(unlockableItemName);
			}
			
			unlockableNames.add(itemInHand.getItemMeta().getDisplayName());
			//p.sendMessage("" + unlockableNames);
			unlockable.set("unlockableItems", unlockableNames);
			saveConfig();
			p.sendMessage(ChatColor.RED + itemInHand.getItemMeta().getDisplayName().toString() + ChatColor.GREEN + " can no longer be locked!");
       	}
       	
       	if (cmd.getName().equalsIgnoreCase("dlhelp")) {
            p.sendMessage(ChatColor.GREEN + "-=+=- " + ChatColor.GOLD +  "Drop Locker" + ChatColor.GREEN + " -=+=-");
            p.sendMessage(ChatColor.WHITE + "/LockItem" + ChatColor.GRAY + " - Hold the item you wish to lock!");
            p.sendMessage(ChatColor.WHITE + "/UnLockItem" + ChatColor.GRAY + " - Hold the item you wish to unlock!");
            if (p.hasPermission("droplocker.setunlockables")) {
                p.sendMessage(ChatColor.RED + "/SetUnlockable" + ChatColor.GRAY + " - Hold the item you wish to make unlockable!");
            }
            p.sendMessage(ChatColor.GREEN + "=================================================");
       	}
        
        
    	return true;
    }
}
