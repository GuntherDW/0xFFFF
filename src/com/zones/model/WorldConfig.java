package com.zones.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.zones.WorldManager;
import com.zones.ZonesConfig;
import com.zones.permissions.Permissions;
import com.zones.util.FileUtil;
import com.zones.util.properties.ExtendedProperties;

public class WorldConfig {
    
    private String filename;
    private WorldManager manager;

    protected static final Logger      log             = Logger.getLogger("Minecraft");
    
    public boolean BORDER_ENABLED;
    public int BORDER_RANGE;
    public int BORDER_RANGE_X;
    public int BORDER_RANGE_Z;
    public int BORDER_TYPE;
    public boolean BORDER_USE_SPAWN;
    public ZoneVertice BORDER_ALTERNATE_CENTER;
    
    public boolean BORDER_ENFORCE;
    public boolean BORDER_OVERRIDE_ENABLED;
    public boolean ENDERMAN_ENABLED;
    
    public boolean DYNAMITE_ENABLED;
    public int DYNAMITE_RANGE;
    public boolean EXPLOSION_DAMAGE_ENTITIES;
    public boolean EXPLOSION_PROTECTED_BLOCKS_ENABLED;
    public List<Integer> EXPLOSION_PROTECTED_BLOCKS;
    
    
    public boolean ALLOW_CREEPER_TRIGGER;
    public int CREEPER_EXPLOSION_RANGE;
    
    public boolean LIGHTER_ALLOWED;
    public boolean FIRE_ENABLED;
    public boolean LAVA_FIRE_ENABLED;
    
    public List<Integer> FIRE_PROTECTED_BLOCKS;
    public boolean FIRE_ENFORCE_PROTECTED_BLOCKS;
    
    public boolean LAVA_FLOW_ENABLED;
    public List<Integer> LAVA_PROTECTED_BLOCKS;
    
    public boolean WATER_FLOW_ENABLED;
    public List<Integer> WATER_PROTECTED_BLOCKS;
    
    public boolean LEAF_DECAY_ENABLED;
    public boolean SNOW_FORM_ENABLED;
    public boolean ICE_FORM_ENABLED;
    public boolean MUSHROOM_GROWTH_ENABLED;
    public boolean VINES_GROWTH_ENABLED;
    public boolean GRASS_GROWTH_ENABLED;
    public boolean TREE_GROWTH_ENABLED;

    public boolean PHYSICS_ENABLED;

    public boolean ICE_MELT_ENABLED;
    public boolean SNOW_MELT_ENABLED;
    
    public boolean PROTECTED_BLOCKS_ENABLED;
    public List<Integer> PROTECTED_BLOCKS_PLACE;
    public List<Integer> PROTECTED_BLOCKS_BREAK;
    
    public boolean CROP_PROTECTION_ENABLED;
    public boolean ENDER_GRIEFING_ENABLED;
    
    public boolean LOGGED_BLOCKS_ENABLED;
    public List<Integer> LOGGED_BLOCKS_PLACE;
    public List<Integer> LOGGED_BLOCKS_BREAK;
    
    public boolean MOB_SPAWNING_ENABLED;
    public boolean ALLOWED_MOBS_ENABLED;
    public List<EntityType> ALLOWED_MOBS;
    
    public boolean ANIMAL_SPAWNING_ENABLED;
    public boolean ALLOWED_ANIMALS_ENABLED;
    public List<EntityType> ALLOWED_ANIMALS;
    
    public boolean LIMIT_BUILD_BY_FLAG;
    
    public boolean GOD_MODE_ENABLED;
    public boolean GOD_MODE_AUTOMATIC;
    
    public boolean PLAYER_FOOD_ENABLED;
    
    public boolean PLAYER_HEALTH_ENABLED;
    
    public boolean PLAYER_ENFORCE_SPECIFIC_DAMAGE;
    
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
    
    public boolean WEATHER_RAIN_ENABLED;
    public int     WEATHER_RAIN_DIVIDER;
    public boolean WEATHER_THUNDER_ENABLED;
    
    public boolean SPONGE_EMULATION;
    public int SPONGE_RADIUS;
    public boolean SPONGE_OVERRIDE_NEEDED;
    
    public boolean SPONGE_LAVA_EMULATION;
    public int SPONGE_LAVA_RADIUS;
    public boolean SPONGE_LAVA_OVERRIDE_NEEDED;
    
    private final ExtendedProperties defaultproperties;
    
    public WorldConfig(WorldManager manager,String filename) {
        defaultproperties = new ExtendedProperties(manager.getPlugin().getClass().getResourceAsStream("/com/zones/config/world.properties"));
        
        this.manager = manager;
        this.filename = filename;

        
        File worldConfigFile = new File(filename);
        if(!worldConfigFile.exists()) {
            if(FileUtil.writeFile(worldConfigFile, FileUtil.readFile(manager.getPlugin().getResource("/com/zones/config/world.properties")).replace("{$worldname}", manager.getWorldName()))) {                
                log.info("[Zones]Restored configuration file of world '" + manager.getWorldName() + "'.");
            } else {
                log.info("[Zones]Error while restoring configuration file of world '" + manager.getWorldName() + "'!");            
            }
        }
        load();
        
        for(Player player : manager.getPlugin().getServer().getOnlinePlayers())
            this.setGodMode(player, GOD_MODE_AUTOMATIC);

        /**
         * Weather
         */
        if(!WEATHER_THUNDER_ENABLED) {
            if(manager.getWorld().isThundering())
                manager.getWorld().setThundering(false);
        }
        if(!WEATHER_RAIN_ENABLED) {
            if(manager.getWorld().hasStorm())
                manager.getWorld().setStorm(false);
        }
    }
    
    public Permissions getPermissions() {
        return manager.getPlugin().getPermissions();
    }

    public int getBorderType(String config) {
        if(config.equalsIgnoreCase("CUBOID"))    return 1;
        if(config.equalsIgnoreCase("CIRCULAR"))  return 2;
        if(config.equalsIgnoreCase("RECTANGLE")) return 3;
        return 1;
    }

    public void load() {
        try {
            ExtendedProperties p = new ExtendedProperties(new File(filename));
            p.load();
            BORDER_ENABLED = p.getBool("BorderEnabled", false);
            BORDER_RANGE = p.getInt("BorderRange", 1000);
            BORDER_RANGE_X = p.getInt("BorderRangeX", 1000);
            BORDER_RANGE_Z = p.getInt("BorderRangeZ", 1000);
            BORDER_TYPE = getBorderType(p.getProperty("BorderShape", "CUBOID"));
            BORDER_USE_SPAWN = p.getBool("BorderUseSpawn", true);
            if(!BORDER_USE_SPAWN) {
                try {
                    String[] split = p.getProperty("BorderAlternateCenter","0,0").split(",");
                    if(split.length < 2) throw new Exception("Not enough parameters.");
                    BORDER_ALTERNATE_CENTER = new ZoneVertice(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                } catch(Exception e) {
                    log.warning("[Zones] Invalid BorderAlternateCenter!");
                    e.printStackTrace();
                }
            }
            BORDER_ENFORCE = p.getBool("EnforceBorder", false);
            BORDER_OVERRIDE_ENABLED = p.getBool("BorderOverrideEnabled", true);
            
            DYNAMITE_ENABLED = p.getBool("AllowTntTrigger", true);
            DYNAMITE_RANGE = p.getInt("TntRange", 4);
            
            ALLOW_CREEPER_TRIGGER = p.getBool("AllowCreeperTrigger", true);
            CREEPER_EXPLOSION_RANGE = p.getInt("CreeperExplosionRange", 3);
            
            EXPLOSION_DAMAGE_ENTITIES = p.getBool("ExplosionDamageEntities", true);
            
            EXPLOSION_PROTECTED_BLOCKS_ENABLED = p.getBool("ExplosionProtectedBlocksEnabled", false);
            EXPLOSION_PROTECTED_BLOCKS = p.getIntList("ExplosionProtectedBlocks", "");
            
            LIGHTER_ALLOWED = p.getBool("LighterAllowed", true);
            FIRE_ENABLED = p.getBool("FireEnabled", true);
            LAVA_FIRE_ENABLED = p.getBool("LavaFireEnabled", true);
            
            FIRE_ENFORCE_PROTECTED_BLOCKS = p.getBool("EnforceFireProtectedBlocks", true);
            FIRE_PROTECTED_BLOCKS = p.getIntList("FireProtectedBlocks", "");
            
            LAVA_FLOW_ENABLED = p.getBool("LavaFlowEnabled", true);
            LAVA_PROTECTED_BLOCKS = p.getIntList("LavaProtectedBlock", "");
            
            WATER_FLOW_ENABLED = p.getBool("WaterFlowEnabled", true);
            WATER_PROTECTED_BLOCKS = p.getIntList("WaterProtectedBlock", "");
            
            LEAF_DECAY_ENABLED = p.getBool("LeafDecayEnabled", true);
            SNOW_FORM_ENABLED = p.getBool("SnowFallEnabled", true);
            ICE_FORM_ENABLED = p.getBool("IceFormEnabled", true);
            MUSHROOM_GROWTH_ENABLED = p.getBool("MushroomSpreadEnabled", true);
            VINES_GROWTH_ENABLED = p.getBool("VinesSpreadEnabled", true);
            GRASS_GROWTH_ENABLED = p.getBool("GrassGrowthEnabled", true);
            TREE_GROWTH_ENABLED = p.getBool("TreeGrowthEnabled", true);
            
            PHYSICS_ENABLED             = p.getBool("PhysicsEnabled", true);
            ENDERMAN_ENABLED = p.getBool("EndermanEnabled", true);

            ICE_MELT_ENABLED = p.getBool("IceMeltEnabled", true);
            SNOW_MELT_ENABLED = p.getBool("SnowMeltEnabled", true);
            
            PROTECTED_BLOCKS_ENABLED = p.getBool("ProtectedBlocksEnabled", true);
            if(PROTECTED_BLOCKS_ENABLED) {
                PROTECTED_BLOCKS_PLACE = p.getIntList("ProtectedBlocksPlace", "");
                PROTECTED_BLOCKS_BREAK = p.getIntList("ProtectedBlocksBreak", "");
            }
            
            CROP_PROTECTION_ENABLED = p.getBool("ProtectCrops", false);
            ENDER_GRIEFING_ENABLED = p.getBool("AllowEnderGrief", true);
            
            LOGGED_BLOCKS_ENABLED = p.getBool("LoggedBlocksEnabled", true);
            if(LOGGED_BLOCKS_ENABLED){
                LOGGED_BLOCKS_PLACE = p.getIntList("LoggedBlocksPlace", "");
                LOGGED_BLOCKS_BREAK = p.getIntList("LoggedBlocksBreak", "");
            }
            
            MOB_SPAWNING_ENABLED = p.getBool("MobSpawningEnabled", true);
            ALLOWED_MOBS_ENABLED = p.getBool("EnableAllowedMobs", false);
            if(ALLOWED_MOBS_ENABLED) {
                ALLOWED_MOBS = new ArrayList<EntityType>();
                EntityType t = null;
                for(String m : p.getProperty("AllowedMobs", "Creeper,Ghast,PigZombie,Skeleton,Spider,Zombie").split(",")) {
                    t = EntityType.fromName(m);
                    if(t != null && !ALLOWED_MOBS.contains(t)) {
                        ALLOWED_MOBS.add(t);
                    }
                    if(t == null)log.warning("[Zones] unknown creaturetype '" + m + "' in allowedmobs in worldConfig of '" + manager.getWorld().getName() + "' !");
                }
            }
            
            ANIMAL_SPAWNING_ENABLED = p.getBool("AnimalSpawningEnabled", true);
            ALLOWED_ANIMALS_ENABLED = p.getBool("EnableAllowedAnimals", false);
            if(ALLOWED_ANIMALS_ENABLED) {
                ALLOWED_ANIMALS = new ArrayList<EntityType>();
                EntityType t = null;
                for(String a : p.getProperty("AllowedAnimals", "Chicken,Cow,Pig,Sheep,Squid").split(",")) {
                    t = EntityType.fromName(a);
                    if(t != null && !ALLOWED_ANIMALS.contains(t))
                        ALLOWED_ANIMALS.add(t);
                    
                    if(t == null)log.warning("[Zones] unknown creaturetype '" + a + "' in allowedanimals in worldConfig of '" + manager.getWorld().getName() + "' !");
                }
            }
            LIMIT_BUILD_BY_FLAG = p.getBool("LimitBuildByFlag", false);
            
            GOD_MODE_ENABLED = p.getBool("AllowGodMode", false);
            if(GOD_MODE_ENABLED) {
                godMode = new HashSet<Integer>();
                GOD_MODE_AUTOMATIC = p.getBool("AutoOnGodMode", true);
            }
            
            PLAYER_FOOD_ENABLED = p.getBool("PlayerFoodEnabled", true);

            PLAYER_HEALTH_ENABLED = p.getBool("PlayerHealthEnabled", true);
            
            PLAYER_ENFORCE_SPECIFIC_DAMAGE = p.getBool("EnforcePlayerSpecificDamage", true);
            
            PLAYER_ENTITY_DAMAGE_ENABLED = p.getBool("PlayerEntityDamageEnabled", true);
            PLAYER_FALL_DAMAGE_ENABLED = p.getBool("PlayerFallDamageEnabled", true);
            PLAYER_LAVA_DAMAGE_ENABLED = p.getBool("PlayerLavaDamageEnabled", true);
            PLAYER_SUFFOCATION_DAMAGE_ENABLED = p.getBool("PlayerSuffocationDamageEnabled", true);
            PLAYER_FIRE_DAMAGE_ENABLED = p.getBool("PlayerFireDamageEnabled", true);
            PLAYER_BURN_DAMAGE_ENABLED = p.getBool("PlayerBurnDamageEnabled", true);
            PLAYER_DROWNING_DAMAGE_ENABLED = p.getBool("PlayerDrowningDamageEnabled", true);
            PLAYER_TNT_DAMAGE_ENABLED = p.getBool("PlayerTntDamageEnabled", true);
            PLAYER_CREEPER_DAMAGE_ENABLED = p.getBool("PlayerCreeperDamageEnabled", true);
            PLAYER_VOID_DAMAGE_ENABLED = p.getBool("PlayerVoidDamageEnabled", true);
            PLAYER_CONTACT_DAMAGE_ENABLED = p.getBool("PlayerContactDamageEnabled", true);
            
            WEATHER_RAIN_ENABLED = p.getBool("RainEnabled", true);
            WEATHER_RAIN_DIVIDER = p.getInt("RainDivider", 1);
            WEATHER_THUNDER_ENABLED = p.getBool("LightningEnabled", true);
            
            SPONGE_EMULATION          = p.getBool("EmulateSponges", false);
            SPONGE_RADIUS           = p.getInt("SpongeRadius", 2);
            SPONGE_OVERRIDE_NEEDED = p.getBool("SpongeOverrideNeeded", false);
            SPONGE_LAVA_EMULATION     = p.getBool("EmulateLavaSponges", false);
            SPONGE_LAVA_RADIUS      = p.getInt("LavaSpongeRadius", 2);
            SPONGE_LAVA_OVERRIDE_NEEDED = p.getBool("LavaSpongeOverrideNeeded", false);
            if(p.isMissingProperties() && ZonesConfig.RESTORE_MISSING_PROPERTIES) {
                int count = p.restore(defaultproperties);
                p.save(true);
                log.info("[Zones] Restored " + count + " missing properties in " + p.getFile().getName() + "!");
            }
            
            //log.info("[Zones] Loaded world config for world " + manager.getWorldName() + "!");
            //if(BORDER_ENABLED) log.info("[Zones] Border Enabled, Range:" + BORDER_RANGE);
        } catch (Exception e) {
            log.warning("[Zones] Error loading configurations for world '" + manager.getWorld().getName() + "' !");
            e.printStackTrace();
        }
    }
    
    public boolean isProtectedPlaceBlock(Player player, int type, boolean message) { 
        if(PROTECTED_BLOCKS_ENABLED) {
            if(this.PROTECTED_BLOCKS_PLACE.contains(type) && !getPermissions().canUse(player, player.getWorld().getName(), "zones.override.place")) {
                if(message)player.sendMessage(ChatColor.RED + "This blocktype is blacklisted!");
                return true;
            }
        }
        return false;
    }
    public boolean isProtectedPlaceBlock(Player player, Block b, boolean message) {        
        return isProtectedPlaceBlock(player,b.getTypeId(), message);
    }
    public boolean isProtectedPlaceBlock(Player player, Block b) {
        return isProtectedPlaceBlock(player,b.getTypeId(), true);
    } 
    public boolean isProtectedBreakBlock(Player player, Block b) {
        return isProtectedBreakBlock(player,b,true);
    }
    
    public boolean isProtectedBreakBlock(Player player, Block b, boolean message) {
        if(PROTECTED_BLOCKS_ENABLED) {
            if(this.PROTECTED_BLOCKS_BREAK.contains(b.getTypeId()) && !getPermissions().canUse(player, player.getWorld().getName(), "zones.override.break")) {
                if(message)player.sendMessage(ChatColor.RED + "This blocktype is protected!");
                return true;
            }
        }
        return false;
    }
    
    /*
     * TODO: extend logging to allow logging to database.
     */
    public void logBlockBreak(Player player, Block block) {
        if(LOGGED_BLOCKS_ENABLED) {
            if(this.LOGGED_BLOCKS_BREAK.contains(block.getTypeId())){
                for(Player p : manager.getPlugin().getServer().getOnlinePlayers())
                    if(getPermissions().canUse(p, p.getWorld().getName(), "zones.log.break")) {
                        p.sendMessage(ChatColor.RED + "Player " + player.getName() + " has broken " + block.getType().name() + "[" + block.getTypeId() + "] at " + block.getLocation().toString() + "!");
                    }
                log.info("Player " + player.getName() + " has broken " + block.getType().name() + "[" + block.getTypeId() + "] at " + block.getLocation().toString() + "!");
            }
        }
        // Using getType().equals(Material.SPONGE) is actually less efficient because it makes more underlying calls (getType() calls to a hashmap.get() for example ;))
        if(block.getTypeId() == Material.SPONGE.getId()) {
            triggerSpongePhysics(block);
        }
    }
    
    private void triggerSpongePhysics(Block block) {
        if(this.SPONGE_EMULATION) {
            // We only update once every 2 Blocks since the basic notch physics check 1 block around the checked block.
            // TODO: optimize.
            for(int i = -SPONGE_RADIUS;i <= SPONGE_RADIUS;i++) {
                for(int o = -SPONGE_RADIUS;o <= SPONGE_RADIUS;o++) {
                    triggerPhysics(block.getWorld().getBlockAt(block.getX()-SPONGE_RADIUS-1, block.getY()+i, block.getZ()+o)); // North
                    triggerPhysics(block.getWorld().getBlockAt(block.getX()+SPONGE_RADIUS, block.getY()+i, block.getZ()+o)); // South
                    triggerPhysics(block.getWorld().getBlockAt(block.getX()+i, block.getY()+o, block.getZ()-SPONGE_RADIUS-1)); // West
                    triggerPhysics(block.getWorld().getBlockAt(block.getX()+i, block.getY()+o, block.getZ()+SPONGE_RADIUS+1)); // East
                    triggerPhysics(block.getWorld().getBlockAt(block.getX()+i, block.getY()+SPONGE_RADIUS+1, block.getZ()+o)); // Up
                }
            }
        }
        if(this.SPONGE_LAVA_EMULATION) {
            // We only update once every 2 Blocks since the basic notch physics check 1 block around the checked block.
            // TODO: optimize.
            for(int i = -SPONGE_LAVA_RADIUS;i <= SPONGE_LAVA_RADIUS;i++) {
                for(int o = -SPONGE_LAVA_RADIUS;o <= SPONGE_LAVA_RADIUS;o++) {
                    triggerPhysics(block.getWorld().getBlockAt(block.getX()-SPONGE_LAVA_RADIUS-1, block.getY()+i, block.getZ()+o)); // North
                    triggerPhysics(block.getWorld().getBlockAt(block.getX()+SPONGE_LAVA_RADIUS, block.getY()+i, block.getZ()+o)); // South
                    triggerPhysics(block.getWorld().getBlockAt(block.getX()+i, block.getY()+o, block.getZ()-SPONGE_LAVA_RADIUS-1)); // West
                    triggerPhysics(block.getWorld().getBlockAt(block.getX()+i, block.getY()+o, block.getZ()+SPONGE_LAVA_RADIUS+1)); // East
                    triggerPhysics(block.getWorld().getBlockAt(block.getX()+i, block.getY()+SPONGE_LAVA_RADIUS+1, block.getZ()+o)); // Up
                    // Wait where is down? Oh snap....
                }
            }
        }
    }
    
    private static void triggerPhysics(Block b) {
        int type = b.getTypeId();
        switch(type) {
            case 8: case 9: case 10: case 11:
                byte data = b.getData();
                b.setTypeId(0);
                b.setTypeIdAndData(type, data, true);
                break;
        }
    }
    /*
     * TODO: extend logging to allow logging to database.
     */
    public void logBlockPlace(Player player, Block block) {
        if(LOGGED_BLOCKS_ENABLED) {
            if(this.LOGGED_BLOCKS_PLACE.contains(block.getTypeId())){
                for(Player p : manager.getPlugin().getServer().getOnlinePlayers())
                    if(getPermissions().canUse(p, p.getWorld().getName(), "zones.log.place")) {
                        p.sendMessage(ChatColor.RED + "Player " + player.getName() + " has placed " + block.getType().name() + "[" + block.getTypeId() + "] at " + block.getLocation().toString() + "!");
                    }
                log.info("Player " + player.getName() + " has placed " + block.getType().name() + "[" + block.getTypeId() + "] at " + block.getLocation().toString() + "!");
            }
        }
        // Using getType().equals(Material.SPONGE) is actually less efficient because it makes more underlying calls (getType() calls to a hashmap.get() for example ;))
        if(block.getTypeId() == Material.SPONGE.getId()) {
            boolean affected = false;
            if(this.SPONGE_EMULATION && ((this.SPONGE_OVERRIDE_NEEDED && getPermissions().canUse(player, player.getWorld().getName(), "zones.override.sponge") || !this.SPONGE_OVERRIDE_NEEDED))) {
                int type = 0;
                for(int x = block.getX() - SPONGE_RADIUS ; x <= block.getX() + SPONGE_RADIUS;x++) {
                    for(int z = block.getZ() - SPONGE_RADIUS ; z <= block.getZ() + SPONGE_RADIUS;z++) {
                        for(int y = block.getY() - SPONGE_RADIUS ; y <= block.getY() + SPONGE_RADIUS;y++) {
                            type = block.getWorld().getBlockTypeIdAt(x, y, z);
                            if(type == 8 || type == 9) {
                                block.getWorld().getBlockAt(x, y, z).setTypeId(0);
                                affected = true;
                            }
                        }
                    }
                }
            }
            if(this.SPONGE_LAVA_EMULATION && ((this.SPONGE_LAVA_OVERRIDE_NEEDED && getPermissions().canUse(player, player.getWorld().getName(), "zones.override.lavasponge") || !this.SPONGE_LAVA_OVERRIDE_NEEDED))) {
                int type = 0;
                for(int x = block.getX() - SPONGE_LAVA_RADIUS ; x <= block.getX() + SPONGE_LAVA_RADIUS;x++) {
                    for(int z = block.getZ() - SPONGE_LAVA_RADIUS ; z <= block.getZ() + SPONGE_LAVA_RADIUS;z++) {
                        for(int y = block.getY() - SPONGE_LAVA_RADIUS ; y <= block.getY() + SPONGE_LAVA_RADIUS;y++) {
                            type = block.getWorld().getBlockTypeIdAt(x, y, z);
                            if(type == 10 || type == 11) {
                                block.getWorld().getBlockAt(x, y, z).setTypeId(0);
                                affected = true;
                            }
                        }
                    }
                }
            }
            if(affected) triggerSpongePhysics(block);
        }
        
    }
    
    /*
     * TODO: extend logging to allow logging to database.
     */
    public void logBlockPlace(Player player, Block block, ItemStack item) {
        if(LOGGED_BLOCKS_ENABLED) {
            if(this.LOGGED_BLOCKS_PLACE.contains(block.getTypeId())){
                for(Player p : manager.getPlugin().getServer().getOnlinePlayers())
                    if(getPermissions().canUse(p, p.getWorld().getName(), "zones.log.place")) {
                        p.sendMessage(ChatColor.RED + "Player " + player.getName() + " has placed " + item.getType().name() + "[" + item.getTypeId() + "] at " + block.getLocation().toString() + "!");
                    }
                log.info("Player " + player.getName() + " has placed " + block.getType().name() + "[" + block.getTypeId() + "] at " + block.getLocation().toString() + "!");
            }
        }
        // Using getType().equals(Material.SPONGE) is actually less efficient because it makes more underlying calls (getType() calls to a hashmap.get() for example ;))
        if(block.getTypeId() == Material.SPONGE.getId()) {
            boolean affected = false;
            if(this.SPONGE_EMULATION && ((this.SPONGE_OVERRIDE_NEEDED && getPermissions().canUse(player, player.getWorld().getName(), "zones.override.sponge") || !this.SPONGE_OVERRIDE_NEEDED))) {
                int type = 0;
                for(int x = block.getX() - SPONGE_RADIUS ; x <= block.getX() + SPONGE_RADIUS;x++) {
                    for(int z = block.getZ() - SPONGE_RADIUS ; z <= block.getZ() + SPONGE_RADIUS;z++) {
                        for(int y = block.getY() - SPONGE_RADIUS ; y <= block.getY() + SPONGE_RADIUS;y++) {
                            type = block.getWorld().getBlockTypeIdAt(x, y, z);
                            if(type == 8 || type == 9) {
                                // Prevent any physics calls since it could get messy :<
                                block.getWorld().getBlockAt(x, y, z).setTypeId(0);
                                affected = true;
                            }
                        }
                    }
                }
            }
            if(this.SPONGE_LAVA_EMULATION && ((this.SPONGE_LAVA_OVERRIDE_NEEDED && getPermissions().canUse(player, player.getWorld().getName(), "zones.override.lavasponge") || !this.SPONGE_LAVA_OVERRIDE_NEEDED))) {
                int type = 0;
                for(int x = block.getX() - SPONGE_LAVA_RADIUS ; x <= block.getX() + SPONGE_LAVA_RADIUS;x++) {
                    for(int z = block.getZ() - SPONGE_LAVA_RADIUS ; z <= block.getZ() + SPONGE_LAVA_RADIUS;z++) {
                        for(int y = block.getY() - SPONGE_LAVA_RADIUS ; y <= block.getY() + SPONGE_LAVA_RADIUS;y++) {
                            type = block.getWorld().getBlockTypeIdAt(x, y, z);
                            if(type == 10 || type == 11) {
                                // Prevent any physics calls since it could get messy :<
                                block.getWorld().getBlockAt(x, y, z).setTypeId(0);
                                affected = true;
                            }
                        }
                    }
                }
            }
            if(affected) triggerSpongePhysics(block);
        }
        
    }
    
    
    
    public boolean canReceiveDamage(Player player, DamageCause cause) {
        if(this.PLAYER_HEALTH_ENABLED) {
            return canReceiveSpecificDamage(player,cause);
        }
        return false;
    }
    
    public boolean canReceiveSpecificDamage(Player player, DamageCause cause) {
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
    
    public boolean canSpawn(Entity entity, EntityType type) {
        if(entity instanceof Animals || entity instanceof Ambient) {
            return this.ANIMAL_SPAWNING_ENABLED && (!this.ALLOWED_ANIMALS_ENABLED || this.ALLOWED_ANIMALS.contains(type));
        } else if(entity instanceof Monster || entity instanceof Flying || entity instanceof Slime) {
            return this.MOB_SPAWNING_ENABLED && (!this.ALLOWED_MOBS_ENABLED || this.ALLOWED_MOBS.contains(type));
        } else {
            return true;
        }
    }
    
    public boolean canFlow(Block from, Block to) {
        int type = from.getTypeId();
        if (type == 8 || type == 9) {
            if(this.WATER_FLOW_ENABLED && !isFlowProtectedBlock(from,to))
                return true;
            return false;
        }

        if (type == 10 || type == 11) {
            if(this.LAVA_FLOW_ENABLED && !isFlowProtectedBlock(from,to))
                return true;
            return false;
        }
        
        return true;
        
    }
    public boolean isNearSponge(Block b, int radius) {
        for(int x = b.getX() - radius ; x <= b.getX() + radius;x++) {
            for(int z = b.getZ() - radius ; z <= b.getZ() + radius;z++) {
                for(int y = b.getY() - radius ; y <= b.getY() + radius;y++) {
                    if(b.getWorld().getBlockTypeIdAt(x, y, z) == Material.SPONGE.getId())
                        return true;
                }
            }
        }
        return false;
    }
    
    public boolean isFlowProtectedBlock(Block from,Block to) {
        int type = from.getTypeId();
        if (type == 8 || type == 9 || type == 0) {
            if(!WATER_PROTECTED_BLOCKS.isEmpty() && this.WATER_PROTECTED_BLOCKS.contains(to.getTypeId()))
                return true;
            if(this.SPONGE_EMULATION && isNearSponge(to,this.SPONGE_RADIUS))
                return true;
            return false;
        }

        if (type == 10 || type == 11 || type == 0) {
            if(!LAVA_PROTECTED_BLOCKS.isEmpty() && (this.LAVA_PROTECTED_BLOCKS.contains(to.getTypeId()) || this.LAVA_PROTECTED_BLOCKS.contains(to.getRelative(BlockFace.DOWN))))
                return true;
            if(this.SPONGE_LAVA_EMULATION && isNearSponge(to,this.SPONGE_LAVA_RADIUS))
                return true;
            return false;
        }
        return false;
    }
    
    public boolean canBurn(Player player, Block block, IgniteCause cause) {
        switch(cause) {
            case FLINT_AND_STEEL:
                return this.LIGHTER_ALLOWED || getPermissions().canUse(player, player.getWorld().getName(), "zones.override.lighter");
            case LAVA:
                return this.FIRE_ENABLED && this.LAVA_FIRE_ENABLED && canBurnBlock(block);
            default:
                return this.FIRE_ENABLED && canBurnBlock(block);
        }
    }
    
    public boolean canBurnBlock(Block b) {
        return !this.FIRE_PROTECTED_BLOCKS.contains(b.getTypeId());
    }
    
    public boolean isOutsideBorder(Location loc) {
        int x = 0;
        int z = 0;
        int locx = WorldManager.toInt(loc.getX());
        int locz = WorldManager.toInt(loc.getZ());
        if(!BORDER_USE_SPAWN && BORDER_ALTERNATE_CENTER != null) {
            x = BORDER_ALTERNATE_CENTER.getX();
            z = BORDER_ALTERNATE_CENTER.getY();
        } else {
            Location spawn = loc.getWorld().getSpawnLocation();
            x = WorldManager.toInt(spawn.getX());
            z = WorldManager.toInt(spawn.getZ());
        }
        switch (this.BORDER_TYPE) {
            case 1:
                if(locz > (z+this.BORDER_RANGE) || locz < (z-this.BORDER_RANGE))
                    return true;
                if(locx > (x+this.BORDER_RANGE) || locx < (x-this.BORDER_RANGE))
                    return true;
                
                return false;
            case 2:
                int xdistance = x - locx;
                int zdistance = z - locz;
                double range = StrictMath.sqrt(xdistance * xdistance + zdistance * zdistance);
                if(range > this.BORDER_RANGE)
                    return true;
                
                return false;
            case 3:
                // The region is Cuboid, so we can simply calculate if the player is outside the border.
                if(locz > (z+this.BORDER_RANGE_Z) || locz < (z-this.BORDER_RANGE_Z))
                    return true;
                if(locx > (x+this.BORDER_RANGE_X) || locx < (x-this.BORDER_RANGE_X))
                    return true;

                return false;
            default:
                return false;
        }
    }
    
    /*
     *  TODO: move to separate handler ? 
     */
    private HashSet<Integer> godMode;
    public boolean hasGodMode(Player player) {
        if(!this.GOD_MODE_ENABLED)
            return false;
        
        if(godMode.contains(player.getEntityId()))
            return false;
        
        return true;
    }
    
    public void setGodMode(Player player, boolean enabled) {
        if(!this.GOD_MODE_ENABLED)
            return;
        
        if(enabled)
            godMode.remove(player.getEntityId());
        else
            godMode.add(player.getEntityId());
    }
    
}
