package com.zones.model;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Animals;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.nijiko.permissions.PermissionHandler;
import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.util.Properties;

public class WorldConfig {
    
    private String filename;
    private WorldManager manager;

    protected static final Logger      log             = Logger.getLogger("Minecraft");
    
    public boolean BORDER_ENABLED;
    public int BORDER_RANGE;
    public int BORDER_TYPE;
    
    public boolean ALLOW_TNT_TRIGGER;
    public int TNT_RANGE;
    public boolean EXPLOSION_DAMAGE_ENTITIES;
    public boolean TNT_DAMAGE_BLOCKS;
    
    public boolean ALLOW_CREEPER_TRIGGER;
    public int CREEPER_EXPLOSION_RANGE;
    
    public boolean LIGHTER_ALLOWED;
    public boolean FIRE_ENABLED;
    public boolean LAVA_FIRE_ENABLED;
    
    public List<Integer> FIRE_PROTECTED_BLOCKS;
    
    public boolean LAVA_FLOW_ENABLED;
    public List<Integer> LAVA_PROTECTED_BLOCKS;
    
    public boolean WATER_FLOW_ENABLED;
    public List<Integer> WATER_PROTECTED_BLOCKS;
    
    public boolean LEAF_DECAY_ENABLED;
    
    public boolean PROTECTED_BLOCKS_ENABLED;
    public List<Integer> PROTECTED_BLOCKS_PLACE;
    public List<Integer> PROTECTED_BLOCKS_BREAK;
    
    public boolean LOGGED_BLOCKS_ENABLED;
    public List<Integer> LOGGED_BLOCKS_PLACE;
    public List<Integer> LOGGED_BLOCKS_BREAK;
    
    public boolean MOB_SPAWNING_ENABLED;
    public List<CreatureType> ALLOWED_MOBS;
    
    public boolean ANIMAL_SPAWNING_ENABLED;
    public List<CreatureType> ALLOWED_ANIMALS;
    
    public boolean LIMIT_BUILD_BY_FLAG;
    
    public boolean PLAYER_HEALTH_ENABLED;
    
    public boolean PLAYER_ENTITY_DAMAGE_ENABLED;
    public boolean PLAYER_FALL_DAMAGE_ENABLED;
    public boolean PLAYER_LAVA_DAMAGE_ENABLED;
    public boolean PLAYER_SUFFOCATION_DAMAGE_ENABLED;
    public boolean PLAYER_FIRE_DAMAGE_ENABLED;
    public boolean PLAYER_BURN_DAMAGE_ENABLED;
    public boolean PLAYER_DROWNING_DAMAGE_ENABLED;
    public boolean PLAYER_TNT_DAMAGE_ENABLED;
    public boolean PLAYER_CREEPER_DAMAGE_ENABLED;
    public boolean PLAYER_VOID_DAMAGE_ENABLED;
    public boolean PLAYER_CONTACT_DAMAGE_ENABLED;
    
    private PermissionHandler permission;
    
    public WorldConfig(WorldManager manager,String filename) {
        this.filename = filename;
        this.manager = manager;
        permission = manager.getPlugin().getP();
        if(!(new File(filename)).exists()) {
            try {
                (new File(filename)).mkdirs();
                byte[] buffer = new byte[(int) new File(Zones.class.getResource("/com/zones/config/world.properties").toURI()).length()];
                InputStream input = Zones.class.getResourceAsStream("/com/zones/config/world.properties");
                BufferedInputStream f = new BufferedInputStream(input);
                f.read(buffer);
                f.close();
                input.close();
                String str = new String(buffer);
                str.replace("{$worldname}", manager.getWorld().getName());
                //For Overwrite the file.
                FileWriter output = new FileWriter(new File(filename));
                BufferedWriter b = new BufferedWriter(output);
                b.write(str);
                b.close();
                output.close();

            } catch (Throwable e) {
                log.info("[Zones]Error while restoring configuration file of world '" + manager.getWorld().getName() + "'!");
                e.printStackTrace();
            }
            log.info("[Zones]Restored configuration file of world '" + manager.getWorld().getName() + "'.");
        }
        load();
    }
    
    public void load() {
        try {
            Properties p = new Properties(new File(filename));
            
            BORDER_ENABLED = p.getBool("BorderEnabled", false);
            BORDER_RANGE = p.getInt("", 0);
            BORDER_TYPE = (p.getProperty("BorderShape", "CUBOID").equalsIgnoreCase("CYLINDER") ? 2 : 1);
            
            ALLOW_TNT_TRIGGER = p.getBool("AllowTntTrigger", true);
            TNT_RANGE = p.getInt("TntRange", 4);
            
            ALLOW_CREEPER_TRIGGER = p.getBool("AllowCreeperTrigger", true);
            CREEPER_EXPLOSION_RANGE = p.getInt("CreeperExplosionRange", 3);
            
            EXPLOSION_DAMAGE_ENTITIES = p.getBool("ExplosionDamageEntities", true);
            
            LIGHTER_ALLOWED = p.getBool("LighterAllowed", true);
            FIRE_ENABLED = p.getBool("FireEnabled", true);
            LAVA_FIRE_ENABLED = p.getBool("LavaFireEnabled", true);
            
            FIRE_PROTECTED_BLOCKS = new ArrayList<Integer>();
            for(String b : p.getProperty("FireProtectedBlocks", "").split(","))
                FIRE_PROTECTED_BLOCKS.add(Integer.parseInt(b));
            
            LAVA_FLOW_ENABLED = p.getBool("LavaFlowEnabled", true);
            LAVA_PROTECTED_BLOCKS = new ArrayList<Integer>();
            for(String b : p.getProperty("LavaProtectedBlock", "").split(","))
                LAVA_PROTECTED_BLOCKS.add(Integer.parseInt(b));
            
            
            WATER_FLOW_ENABLED = p.getBool("WaterFlowEnabled", true);
            WATER_PROTECTED_BLOCKS = new ArrayList<Integer>();
            for(String b : p.getProperty("WaterProtectedBlock", "").split(","))
                WATER_PROTECTED_BLOCKS.add(Integer.parseInt(b));
            
            LEAF_DECAY_ENABLED = p.getBool("LeafDecayEnabled", true);
            
            PROTECTED_BLOCKS_ENABLED = p.getBool("ProtectedBlocksEnabled", true);
            if(PROTECTED_BLOCKS_ENABLED) {
                PROTECTED_BLOCKS_PLACE = new ArrayList<Integer>();
                for(String b : p.getProperty("ProtectedBlocksPlace", "").split(","))
                    PROTECTED_BLOCKS_PLACE.add(Integer.parseInt(b));
                PROTECTED_BLOCKS_BREAK = new ArrayList<Integer>();
                for(String b : p.getProperty("ProtectedBlocksBreak", "").split(","))
                    PROTECTED_BLOCKS_BREAK.add(Integer.parseInt(b));
            }
            
            LOGGED_BLOCKS_ENABLED = p.getBool("LoggedBlocksEnabled", true);
            if(LOGGED_BLOCKS_ENABLED){
                LOGGED_BLOCKS_PLACE = new ArrayList<Integer>();
                for(String b : p.getProperty("LoggedBlocksPlace", "").split(","))
                    LOGGED_BLOCKS_PLACE.add(Integer.parseInt(b));
                
                LOGGED_BLOCKS_BREAK = new ArrayList<Integer>();
                for(String b : p.getProperty("LoggedBlocksBreak", "").split(","))
                    LOGGED_BLOCKS_BREAK.add(Integer.parseInt(b));
            }
            
            MOB_SPAWNING_ENABLED = p.getBool("MobSpawningEnabled", true);
            if(MOB_SPAWNING_ENABLED) {
                ALLOWED_MOBS = new ArrayList<CreatureType>();
                CreatureType t = null;
                for(String m : p.getProperty("AllowedMobs", "Creeper,Ghast,PigZombie,Skeleton,Spider,Zombie").split(",")) {
                    t = CreatureType.fromName(m);
                    if(t != null && !ALLOWED_MOBS.contains(t)) {
                        ALLOWED_MOBS.add(t);
                    }
                    if(t == null)log.warning("[Zones] unknown creaturetype '" + m + "' in allowedmobs in worldConfig of '" + manager.getWorld().getName() + "' !");
                }
            }
            
            ANIMAL_SPAWNING_ENABLED = p.getBool("AnimalSpawningEnabled", true);
            if(ANIMAL_SPAWNING_ENABLED) {
                ALLOWED_ANIMALS = new ArrayList<CreatureType>();
                CreatureType t = null;
                for(String a : p.getProperty("AllowedAnimals", "Chicken,Cow,Pig,Sheep,Squid").split(",")) {
                    t = CreatureType.fromName(a);
                    if(t != null && !ALLOWED_ANIMALS.contains(t))
                        ALLOWED_ANIMALS.add(t);
                    
                    if(t == null)log.warning("[Zones] unknown creaturetype '" + a + "' in allowedanimals in worldConfig of '" + manager.getWorld().getName() + "' !");
                }
            }
            LIMIT_BUILD_BY_FLAG = p.getBool("LimitBuildByFlag", false);
            
            PLAYER_HEALTH_ENABLED = p.getBool("PlayerHealthEnabled", true);
            
            PLAYER_ENTITY_DAMAGE_ENABLED = p.getBool("PlayerEntityDamageEnabled", true);
            PLAYER_FALL_DAMAGE_ENABLED = p.getBool("PlayerFallDamageEnabled", true);
            PLAYER_LAVA_DAMAGE_ENABLED = p.getBool("PlayerLavaDamageEnabled", true);
            PLAYER_SUFFOCATION_DAMAGE_ENABLED = p.getBool("PlayerSuffocationDamageEnabled", true);
            PLAYER_FIRE_DAMAGE_ENABLED = p.getBool("PlayerFireDamageEnabled", true);
            PLAYER_BURN_DAMAGE_ENABLED = p.getBool("PlayerBurnDamageEnabled", true);
            PLAYER_DROWNING_DAMAGE_ENABLED = p.getBool("PlayerDrowningDamagaEnabled", true);
            PLAYER_TNT_DAMAGE_ENABLED = p.getBool("PlayerTntDamagaEnabled", true);
            PLAYER_CREEPER_DAMAGE_ENABLED = p.getBool("PlayerCreeperDamageEnabled", true);
            PLAYER_VOID_DAMAGE_ENABLED = p.getBool("PlayerVoidDamageEnabled", true);
            PLAYER_CONTACT_DAMAGE_ENABLED = p.getBool("PlayerContactDamageEnabled", true);
            
        } catch (Exception e) {
            log.warning("[Zones]Error loading configurations for world '" + manager.getWorld().getName() + "' !");
            e.printStackTrace();
        }
    }
    
    public boolean isProtectedPlaceBlock(Player player, int type) { 
        if(PROTECTED_BLOCKS_ENABLED) {
            if(this.PROTECTED_BLOCKS_BREAK.contains(type) && this.permission.permission(player, "zones.override.place")) {
                player.sendMessage(ChatColor.RED + "This blocktype is blacklisted!");
                return true;
            }
        }
        return false;
    }
    
    public boolean isProtectedPlaceBlock(Player player, Block b) {
        return isProtectedPlaceBlock(player,b.getTypeId());
    } 

    public boolean isProtectedBreakBlock(Player player, Block b) {
        if(PROTECTED_BLOCKS_ENABLED) {
            if(this.PROTECTED_BLOCKS_BREAK.contains(b.getTypeId()) && this.permission.permission(player, "zones.override.break")) {
                player.sendMessage(ChatColor.RED + "This blocktype is protected!");
                return true;
            }
        }
        return false;
    }
    
    public void logBlockBreak(Player player, Block block) {
        if(LOGGED_BLOCKS_ENABLED) {
            if(this.LOGGED_BLOCKS_BREAK.contains(block.getTypeId())){
                for(Player p : manager.getPlugin().getServer().getOnlinePlayers())
                    if(permission.permission(p, "zones.log.break")) {
                        p.sendMessage(ChatColor.RED + "Player " + player.getName() + " has broken " + block.getType().name() + "[" + block.getTypeId() + "] at " + block.getLocation().toString() + "!");
                    }
                log.info("Player " + player.getName() + " has broken " + block.getType().name() + "[" + block.getTypeId() + "] at " + block.getLocation().toString() + "!");
            }
        }
    }
    
    public void logBlockPlace(Player player, Block block) {
        if(LOGGED_BLOCKS_ENABLED) {
            if(this.LOGGED_BLOCKS_PLACE.contains(block.getTypeId())){
                for(Player p : manager.getPlugin().getServer().getOnlinePlayers())
                    if(permission.permission(p, "zones.log.place")) {
                        p.sendMessage(ChatColor.RED + "Player " + player.getName() + " has placed " + block.getType().name() + "[" + block.getTypeId() + "] at " + block.getLocation().toString() + "!");
                    }
                log.info("Player " + player.getName() + " has placed " + block.getType().name() + "[" + block.getTypeId() + "] at " + block.getLocation().toString() + "!");
            }
        }
    }
    
    public boolean canReceiveDamage(Player player, DamageCause cause) {
        if(this.PLAYER_HEALTH_ENABLED) {
            switch(cause) {
                case CONTACT:
                    return this.PLAYER_CONTACT_DAMAGE_ENABLED;
                case ENTITY_ATTACK:
                    return this.PLAYER_ENTITY_DAMAGE_ENABLED;
                case SUFFOCATION:
                    return this.PLAYER_SUFFOCATION_DAMAGE_ENABLED;
                case FALL:
                    return this.PLAYER_FALL_DAMAGE_ENABLED;
                case FIRE:
                    return this.PLAYER_FIRE_DAMAGE_ENABLED;
                case FIRE_TICK:
                    return this.PLAYER_BURN_DAMAGE_ENABLED;
                case LAVA:
                    return this.PLAYER_LAVA_DAMAGE_ENABLED;
                case DROWNING:
                    return this.PLAYER_DROWNING_DAMAGE_ENABLED;
                case BLOCK_EXPLOSION:
                    return this.PLAYER_TNT_DAMAGE_ENABLED;
                case ENTITY_EXPLOSION:
                    return this.PLAYER_CREEPER_DAMAGE_ENABLED;
                case VOID:
                    return this.PLAYER_VOID_DAMAGE_ENABLED;
                default:
                    return true;
                    
            }
        }
        return false;
    }
    
    public boolean canSpawn(Entity entity, CreatureType type) {
        if(entity instanceof Animals) {
            return this.ANIMAL_SPAWNING_ENABLED && this.ALLOWED_ANIMALS.contains(type);
        } else if(entity instanceof Monster) {
            return this.MOB_SPAWNING_ENABLED && this.ALLOWED_MOBS.contains(type);
        } else {
            return true;
        }
    }
    
    public boolean canFlow(Block from, Block to) {
        if (from.getTypeId() == 8 || from.getTypeId() == 9) {
            if(this.WATER_FLOW_ENABLED && !this.WATER_PROTECTED_BLOCKS.contains(to.getTypeId()))
                return true;
            return false;
        }

        if (from.getTypeId() == 10 || from.getTypeId() == 11) {
            if(this.LAVA_FLOW_ENABLED && !this.LAVA_PROTECTED_BLOCKS.contains(to.getTypeId()) && !this.LAVA_PROTECTED_BLOCKS.contains(to.getRelative(BlockFace.DOWN)))
                return true;
            return false;
        }
        
        return true;
        
    }
    
    public boolean canBurn(Player player, Block block, IgniteCause cause) {
        switch(cause) {
            case FLINT_AND_STEEL:
                return this.LIGHTER_ALLOWED || permission.permission(player, "zones.override.lighter");
            case LAVA:
                return this.FIRE_ENABLED && this.LAVA_FIRE_ENABLED;
            case SPREAD:
                return this.FIRE_ENABLED;
            default:
                return true;
        }
    }
    
}
