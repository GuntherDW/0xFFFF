package com.zones.accessresolver.interfaces;

import org.bukkit.block.Block;

import com.zones.model.ZoneBase;

public interface BlockFromToResolver extends Resolver {
    public boolean isAllowed(ZoneBase zone, Block block, Block to);
}
