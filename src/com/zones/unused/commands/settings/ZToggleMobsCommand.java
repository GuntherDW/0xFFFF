package com.zones.unused.commands.settings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;
import com.zones.unused.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZToggleMobsCommand extends ZoneCommand {

    public ZToggleMobsCommand(Zones plugin) {
        super("ztogglemobs", plugin);
        this.setRequiresSelected(true);
        this.setRequiredAccess("zones.toggle.mobs");
    }

    @Override
    public void run(Player player, String[] vars) {
        ZoneBase z = getSelectedZone(player);
        if(z.setSetting(ZoneVar.SPAWN_MOBS, !z.getSettings().getBool(ZoneVar.SPAWN_MOBS, z.getWorldManager().getConfig().MOB_SPAWNING_ENABLED)))
            player.sendMessage(ChatColor.GREEN.toString() + "Mobs spawning is now "+(z.getSettings().getBool(ZoneVar.SPAWN_MOBS, z.getWorldManager().getConfig().MOB_SPAWNING_ENABLED) ? "enabled" : "disabled" )+".");
        else
            player.sendMessage(ChatColor.RED.toString() + "Unable to change mobs flag, please contact a admin.");
    }
}