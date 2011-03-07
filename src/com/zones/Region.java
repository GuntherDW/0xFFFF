package com.zones;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Region {

    private ArrayList<ZoneType> _zones;
    private int                 x, y;

    public Region(int x, int y) {
        this.x = x;
        this.y = y;
        _zones = new ArrayList<ZoneType>();
    }

    public void addZone(ZoneType zone) {
        if (_zones.contains(zone))
            return;

        _zones.add(zone);
    }

    public void removeZone(ZoneType zone) {
        for (int i = 0; i < _zones.size(); i++) {
            if (_zones.get(i).getId() == zone.getId())
                _zones.remove(i);
        }
    }

    public ArrayList<ZoneType> getZones() {
        return _zones;
    }

    public void revalidateZones(Player character) {
        for (ZoneType z : getZones()) {
            if (z != null)
                z.revalidateInZone(character);
        }
    }

    public ZoneType getActiveZone(Player player) {
        Location loc = player.getLocation();
        return getActiveZone(loc.getX(), loc.getY(), loc.getZ());
    }

    public ZoneType getActiveZone(double x, double y, double z) {
        return getActiveZone(World.toInt(x), World.toInt(y), World.toInt(z));
    }

    public ZoneType getActiveZone(int x, int y, int z) {
        ZoneType primary = null;

        for (ZoneType zone : getZones())
            if (zone.isInsideZone(x, y, z) && (primary == null || primary.getZone().getSize() > zone.getZone().getSize()))
                primary = zone;

        return primary;
    }

    public ArrayList<ZoneType> getActiveZones(Player player) {
        Location loc = player.getLocation();
        return getActiveZones(loc.getX(), loc.getY(), loc.getZ());
    }

    public ArrayList<ZoneType> getActiveZones(double x, double y, double z) {
        return getActiveZones(World.toInt(x), World.toInt(y), World.toInt(z));
    }

    public ArrayList<ZoneType> getActiveZones(int x, int y, int z) {
        ArrayList<ZoneType> zones = new ArrayList<ZoneType>();

        for (ZoneType zone : getZones())
            if (zone.isInsideZone(x, y, z))
                zones.add(zone);

        return zones;
    }

    public ArrayList<ZoneType> getAdminZones(Player player) {
        ArrayList<ZoneType> zones = new ArrayList<ZoneType>();

        for (ZoneType zone : getZones())
            if (zone.canAdministrate(player) && zone.isInsideZone(player))
                zones.add(zone);

        return zones;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void revalidateZones(Player player, int ax, int ay, int az) {
        for (ZoneType z : getZones()) {
            if (z != null)
                z.revalidateInZone(player, ax, ay, az);
        }
    }
}
