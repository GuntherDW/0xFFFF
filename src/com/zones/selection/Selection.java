package com.zones.selection;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.zones.model.ZoneForm;
import com.zones.model.ZoneVertice;

public abstract class Selection {
    
    private ZoneSelection selection;
    private ZoneVertice height = new ZoneVertice(0,130);

    public Selection(ZoneSelection selection) {
        this.selection = selection;
    }
    
    public ZoneSelection getSelection() {
        return selection;
    }
    
    public Player getPlayer() {
        return getSelection().getPlayer();
    }
    
    public void setHeight(ZoneVertice height) {
        setHeight(height, false);
    }
    
    public void setHeight(ZoneVertice height, boolean silent) {
        this.height = height;
        if(!silent) getPlayer().sendMessage(ChatColor.GREEN + "Selection height changed to: " + height.getMin() + "-" + height.getMax() + ".");
    }
    
    public ZoneVertice getHeight() {
        return height;
    }
    
    public abstract void onRightClick(Block block);
    public abstract void onLeftClick(Block block);
    
    public abstract boolean isValid();
    
    public abstract int getSize();
    
    public abstract int getPointsSize();
    public abstract List<ZoneVertice> getPoints();
    
    public abstract  Class<? extends ZoneForm> getType();
}