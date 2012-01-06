package com.zones;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.zones.commands.ZoneCommand;
import com.zones.commands.ZoneCommandMap;
import com.zones.listeners.*;
import com.zones.permissions.Permissions;
import com.zones.permissions.PermissionsResolver;
import com.zones.persistence.Database;
import com.zones.util.FileUtil;
import com.zones.util.ZoneUtil;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author Meaglin
 *
 */
public class Zones extends JavaPlugin implements CommandExecutor {

    public static final int                 Rev             = 116;
    public static final Logger              log             = Logger.getLogger("Minecraft");
    private final ZonesPlayerListener       playerListener  = new ZonesPlayerListener(this);
    private final ZonesBlockListener        blockListener   = new ZonesBlockListener(this);
    private final ZonesEntityListener       entityListener  = new ZonesEntityListener(this);
    private final ZonesVehicleListener      vehicleListener = new ZonesVehicleListener(this);
    private final ZonesWeatherListener      weatherListener = new ZonesWeatherListener(this);

    private final ZoneCommandMap            commandMap      = new ZoneCommandMap(this);

    private WorldEditPlugin                 worldedit;
    private Permissions                     permissionsManager;

    private final HashMap<Long, WorldManager> worlds        = new HashMap<Long, WorldManager>(2);
    private final ZoneManager               zoneManager     = new ZoneManager(this);
    private Database                        database        = null;
    private final ZoneUtil                  util            = new ZoneUtil(this);
    
    public static final boolean             debugEnabled    = false;
    
    public Zones() {
    }

    /**
     * Register used events.
     */
    private void registerEvents() {
        
        registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Priority.Normal);
        registerEvent(Event.Type.BLOCK_FROMTO, blockListener, Priority.Normal);
        registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal);
        registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal);
        registerEvent(Event.Type.LEAVES_DECAY, blockListener, Priority.Normal);
        registerEvent(Event.Type.BLOCK_PHYSICS, blockListener, Priority.Normal);
        registerEvent(Event.Type.BLOCK_BURN, blockListener, Priority.Normal);
        registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Normal);
        registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal);
        
        registerEvent(Event.Type.BLOCK_FORM, blockListener, Priority.Normal);
        registerEvent(Event.Type.BLOCK_SPREAD, blockListener, Priority.Normal);
        registerEvent(Event.Type.BLOCK_FADE, blockListener, Priority.Normal);

        registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal);
        registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal);
        registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Priority.Normal);
        registerEvent(Event.Type.ENTITY_INTERACT, entityListener, Priority.Normal);
        registerEvent(Event.Type.FOOD_LEVEL_CHANGE, entityListener, Priority.Normal);
        registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Normal);
        registerEvent(Event.Type.PAINTING_PLACE, entityListener, Priority.Normal);
        registerEvent(Event.Type.PAINTING_BREAK, entityListener, Priority.Normal);
        registerEvent(Event.Type.ENDERMAN_PICKUP, entityListener, Priority.Normal);
        registerEvent(Event.Type.ENDERMAN_PLACE, entityListener, Priority.Normal);

        registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_PORTAL, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_BUCKET_FILL, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Normal);
        registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Normal);

        registerEvent(Event.Type.VEHICLE_DAMAGE, vehicleListener, Priority.Normal);
        registerEvent(Event.Type.VEHICLE_MOVE, vehicleListener, Priority.Normal);
        
        registerEvent(Event.Type.WEATHER_CHANGE, weatherListener, Priority.Normal);
        registerEvent(Event.Type.THUNDER_CHANGE, weatherListener, Priority.Normal);
    }

    public void registerWorldEdit() {
        if(worldedit == null) {
            Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldEdit");
            if(plugin != null)
                worldedit = (WorldEditPlugin) plugin;
        }
    }

    /**
     * Register an event.
     * 
     * @param type
     * @param listener
     * @param priority
     */
    private void registerEvent(Event.Type type, Listener listener, Priority priority) {
        getServer().getPluginManager().registerEvent(type, listener, priority, this);
    }
    
    @Override
    public void onDisable() {
        log.info("[Zones] plugin disabled!");
    }

    @Override
    public void onEnable() {
        //log.info("[Zones] Rev " + Rev + "  Loading...");
        
        File configFile = new File(getDataFolder().getPath()+"/"+ZonesConfig.ZONES_CONFIG_FILE);
        if(!configFile.exists()) {
            getDataFolder().mkdirs();
            if(FileUtil.copyFile(Zones.class.getResourceAsStream("/com/zones/config/Zones.properties"), configFile)) {
                log.info("[Zones] Missing configuration file restored.");                
            } else {
                log.info("[Zones] Error while restorting configuration file.");
            }       
        }  
        database = new Database(this);
        resolvePermissions();
        //setupDatabase();
        ZonesConfig.load(configFile);
        commandMap.load();
        loadWorlds();
        registerEvents();
        if(ZonesConfig.WORLDEDIT_ENABLED) {
            //log.info("[Zones] Loading worldedit support...");
            registerWorldEdit();
        }
        log.info("[Zones] Rev " + Rev + " Loaded " + getZoneManager().getZoneCount()  + " zones in " + worlds.size() + " worlds, WorlEditSupport:" + ZonesConfig.WORLDEDIT_ENABLED + " Permissions:" + getPermissions().getName() + ".");
        
    }
    
    private void resolvePermissions() {
        permissionsManager = PermissionsResolver.resolve(this);
        //log.info("[Zones] Using " + permissionsManager.getName() + " for permissions managing.");
    }
    
    private void loadWorlds() {
        try {
            worlds.clear();
            for(World world : getServer().getWorlds())
                worlds.put(world.getUID().getLeastSignificantBits(),new WorldManager(this,world));
        } catch(Throwable t) {
            log.warning("[Zones] Error loading worlds.");
            t.printStackTrace();
        }
    }
    
    
    public Permissions getPermissions() {
        return permissionsManager;
    }

    public WorldEditPlugin getWorldEdit() {
        return worldedit;
    }
    
    public WorldManager getWorldManager(Player p) { return getWorldManager(p.getWorld()); }
    public WorldManager getWorldManager(Location l) { return getWorldManager(l.getWorld()); }
    
    /*
     * It's more efficient to do the null call instead of using containskey since it has more underlying calls.
     */
    public WorldManager getWorldManager(World world) {
        WorldManager wm = worlds.get(world.getUID().getLeastSignificantBits());
        if(wm == null) {
            wm = new WorldManager(this,world);
            worlds.put(world.getUID().getLeastSignificantBits(), wm);
        }
        return wm;
    }
    
    public Collection<WorldManager> getWorlds() {
        return worlds.values();
    }

    protected WorldManager getWorldManager(String world) {
        World w = getServer().getWorld(world);
        if(w == null) {
            log.warning("[Zones] Trying to find world '" + world + "' which doesn't exist !");
            return null;
        }
        WorldManager wm = worlds.get(w.getUID().getLeastSignificantBits());
        if(wm == null) {
            wm = new WorldManager(this,w);
            worlds.put(w.getUID().getLeastSignificantBits(), wm);
        }
        
        return wm;
    }
    
    public ZoneManager getZoneManager() {
        return zoneManager;
    }
    
    public boolean reload() {
        return reloadZonesConfig() && reloadZones();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ZoneCommand cmd = commandMap.getCommand(command.getName());
        if(cmd != null) {
            boolean rt = cmd.execute(sender, label, args);
            String arg = "";
            for(int i = 0;i < args.length; i++) arg += args[i] + " ";
            log.info("[Zones] " + sender.getName() + " issued " + cmd.getName() + " with args: " + arg + "!");
            return rt;
            //return cmd.execute(sender, label, args);
        }
        return false;
    }

    public boolean reloadZones() {
        try {
            for(WorldManager w : worlds.values())
                w.loadRegions();
            
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean reloadZonesConfig() {
        try {
            ZonesConfig.load(new File(getDataFolder().getPath()+"/"+ZonesConfig.ZONES_CONFIG_FILE));
            for(WorldManager w : worlds.values())
                w.loadConfig();
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    public Database getMysqlDatabase() {
        return database;
    }
    
    public ZoneUtil getUtils() {
        return util;
    }
    
    public ZoneUtil getApi() {
        return getUtils();
    }
    
}

