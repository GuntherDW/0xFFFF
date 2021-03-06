package com.zones.model.types.normal;

import java.util.List;

import org.bukkit.entity.*;

import com.zones.accessresolver.interfaces.EntitySpawnResolver;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;

public class NormalEntitySpawnResolver implements EntitySpawnResolver {

    @Override
    public boolean isAllowed(ZoneBase zone, Entity entity, EntityType type) {
        if(entity instanceof Animals || entity instanceof Ambient || entity instanceof WaterMob) {
            if(zone.getFlag(ZoneVar.ANIMALS)) {
                Object obj = zone.getSetting(ZoneVar.ALLOWED_ANIMALS);
                if(obj != null) {
                    if(!((List<?>)obj).contains(type))
                        return false;
                }
                return true;
            } 
            return false;
        } else if(entity instanceof Monster || entity instanceof Flying || entity instanceof Slime) {
            if(zone.getFlag(ZoneVar.MOBS)) {
                Object obj = zone.getSetting(ZoneVar.ALLOWED_MOBS);
                if(obj != null) {
                    if(!((List<?>)obj).contains(type))
                        return false;
                }
                return true;
            } 
            return false;
        } 
        return true;
    }
    
}
