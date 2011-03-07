package com.zones;

import com.zones.listeners.ZonesBlockListener;
import com.zones.listeners.ZonesEntityListener;
import com.zones.listeners.ZonesPlayerListener;
import com.zones.listeners.ZonesVehicleListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Zones extends JavaPlugin implements CommandExecutor {

    public static final int            Rev             = 31;
    protected static final Logger      log             = Logger.getLogger("Minecraft");
    private final ZonesPlayerListener  playerListener  = new ZonesPlayerListener(this);
    private final ZonesBlockListener   blockListener   = new ZonesBlockListener(this);
    private final ZonesEntityListener  entityListener  = new ZonesEntityListener(this);
    private final ZonesVehicleListener vehicleListener = new ZonesVehicleListener(this);
    public static final int            pilonHeight     = 4;
    // snow
    public static final int            pilonType       = 80;
    // stick
    public static final int            toolType        = 280;

    public Zones() {
        log.info("[Zones]Rev " + Rev + "  Loading...");
        
        if(!(new File(ZonesConfig.ZONES_CONFIG_FILE)).exists()) {
            try {
            InputStream input = Zones.class.getResourceAsStream("/com/zones/config/Zones.properties");

            //For Overwrite the file.
            OutputStream output = new FileOutputStream(new File(ZonesConfig.ZONES_CONFIG_FILE));

            byte[] buf = new byte[1024];
            int len;
            while ((len = input.read(buf)) > 0){
              output.write(buf, 0, len);
            }
            input.close();
            output.close();
            
            
            } catch (Exception e) {
                log.info("[Zones]Error while restorting configuration file.");
                e.printStackTrace();
            }
            log.info("[Zones]Missing configuration file restored.");
            log.info("-----------");
            log.info("Zones will NOT finish loading since it has to be configured first to be able to load properly!");
            log.info("-----------");            
        } else {    
            ZonesConfig.load();
            ZoneManager.getInstance();
            ZoneManager.getInstance().load(this);
            log.info("[Zones]finished Loading.");
        }
    }

    /**
     * Register used events.
     */
    private void registerEvents() {

        registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.High);
        registerEvent(Event.Type.BLOCK_FLOW, blockListener, Priority.High);
        registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.High);
        registerEvent(Event.Type.BLOCK_INTERACT, blockListener, Priority.High);
        registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener, Priority.High);
        registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.High);

        registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.High);
        registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.High);
        registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.High);

        registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.High);
        registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.High);
        registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.High);
        registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.High);
        registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.High);
        registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal);

        registerEvent(Event.Type.VEHICLE_DAMAGE, vehicleListener, Priority.High);
        registerEvent(Event.Type.VEHICLE_MOVE, vehicleListener, Priority.High);
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

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(ZonesConfig.DATABASE_URL + "?autoReconnect=true&user=" + ZonesConfig.DATABASE_LOGIN + "&password=" + ZonesConfig.DATABASE_PASSWORD);
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Unable to retreive connection", ex);
        }
        return null;
    }

    @Override
    public void onDisable() {
        log.info("[Zones]plugin disabled!");
    }

    @Override
    public void onEnable() {
        registerEvents();
        log.info("[Zones]plugin enabled.");
    }
}
