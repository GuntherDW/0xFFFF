package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.selection.ZoneSelection;
import com.zones.selection.ZoneSelection.Confirm;

/**
 * 
 * @author Meaglin
 *
 */
public class ZSaveCommand extends ZoneCommand {
    
    public ZSaveCommand(Zones plugin) {
        super("zsave", plugin);
        this.setRequiresZoneSelection(true);
    }

    @Override
    public void run(Player player, String[] vars) {
        ZoneSelection selection = getZoneSelection(player);
        if(!selection.getSelection().isValid()) {
            player.sendMessage(ChatColor.RED + "You don't have a valid selection.");
            return;
        }
        if (selection.getSelection().getHeight().getMax() == 130 && selection.getSelection().getHeight().getMin() == 0)
            player.sendMessage(ChatColor.RED.toString() + "WARNING: default z values not changed!");

        player.sendMessage(ChatColor.YELLOW.toString() + "If you are sure you want to save this zone do /zconfirm");

        selection.setConfirm(Confirm.SAVE);
    }
}
