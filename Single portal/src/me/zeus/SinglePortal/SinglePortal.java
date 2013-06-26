
package me.zeus.SinglePortal;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;



public class SinglePortal extends JavaPlugin implements Listener
{
	
	
	String permissionMSG;
	String playerOnlyMSG;
	String portalTypeMSG;
	String invalidPortalMSG;
	List<String> names;
	
	Location loc1;
	Location loc2;
	
	
	
	@Override
	public void onEnable()
	{
		Bukkit.getPluginManager().registerEvents(this, this);
		names = new ArrayList<String>();
		
		File config = new File(getDataFolder() + "/config.yml");
		if (!config.exists())
		{
			saveDefaultConfig();
		}
		else
		{
			String[] data = getConfig().getString("portal.main").split(":");
			String[] data2 = getConfig().getString("portal.nether").split(":");
			loc1 = new Location(Bukkit.getWorld(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
			loc2 = new Location(Bukkit.getWorld(data2[0]), Integer.parseInt(data2[1]), Integer.parseInt(data2[2]), Integer.parseInt(data2[3]));
		}
		
		permissionMSG = getConfig().getString("messages.no-permission").replace("&", "§");
		playerOnlyMSG = getConfig().getString("messages.player-only").replace("&", "§");
		portalTypeMSG = getConfig().getString("messages.portal-type").replace("&", "§");
		invalidPortalMSG = getConfig().getString("messages.invalid-portal").replace("&", "§");
		
	}
	
	
	
	@EventHandler
	public void onPortal(final PlayerPortalEvent e)
	{
		int radius = 5;
		final Block block = e.getFrom().getBlock(); //placed block
		e.setCancelled(true);
		
		if (loc1 == null || loc2 == null)
		{
			e.setCancelled(false);
			return;
		}
		
		for (int x = -(radius); x <= radius; x++)
			for (int y = -(radius); y <= radius; y++)
				for (int z = -(radius); z <= radius; z++)
				{
					Block b = block.getRelative(x, y, z);
					if (b.getType().equals(Material.WALL_SIGN))
					{
						Sign sign = (Sign) b.getState();
						if (sign.getLine(0).equalsIgnoreCase("§5[Portal]"))
							if (e.getPlayer().getWorld().getEnvironment().equals(Environment.NORMAL))
								e.getPlayer().teleport(loc2);
							else if (e.getPlayer().getWorld().getEnvironment().equals(Environment.NETHER))
								e.getPlayer().teleport(loc1);
					}
				}
	}
	
	
	
	@EventHandler
	public void onSign(SignChangeEvent e)
	{
		if (e.getLine(0).equalsIgnoreCase("[Portal]"))
			if (!e.getPlayer().hasPermission("portal.establish"))
			{
				e.setCancelled(true);
				e.getPlayer().sendMessage("§cInvalid perms.");
			}
			else
			{
				e.setLine(0, "§5[Portal]");
				if (e.getBlock().getWorld().getEnvironment().equals(Environment.NORMAL))
				{
					loc1 = e.getPlayer().getLocation();
					getConfig().set("portal.main", loc1.getWorld().getName() + ":" + loc1.getX() + ":" + loc1.getY() + ":" + loc1.getZ());
					saveConfig();
				}
				else if (e.getBlock().getWorld().getEnvironment().equals(Environment.NETHER))
				{
					loc2 = e.getPlayer().getLocation();
					getConfig().set("portal.nether", loc2.getWorld().getName() + ":" + loc2.getX() + ":" + loc2.getY() + ":" + loc2.getZ());
					saveConfig();
				}
			}
	}
	
}
