package com.zones.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

public class ExPermissions extends Permissions {

    
    private final PermissionManager manager;
    private TweakcraftUtils tcutils;

    public ExPermissions(PermissionManager manager) {
        this.manager = manager;
        setupTweakcraftUtils();
    }

    public void setupTweakcraftUtils() {
        Plugin p = Bukkit.getPluginManager().getPlugin("TweakcraftUtils");
        if(p != null && p instanceof TweakcraftUtils)
            tcutils = (TweakcraftUtils) p;
    }

    @Override
    public boolean canUse(String world, String playername, String command) {
        UUID uuid = tcutils.getUUIDResolver().getOrCreateUUID(playername);
        return manager.has(uuid, command, world);
    }

    @Override
    public boolean inGroup(String world, String playername, String group) {
        UUID uuid = tcutils.getUUIDResolver().getOrCreateUUID(playername);
        return group.equalsIgnoreCase("default") || manager.getUser(uuid).inGroup(group, true);
    }

    @Override
    public boolean isValid(String world, String group) {
        PermissionGroup g = manager.getGroup(group);
        if(g == null) return false;
        
        return true;
    }

    @Override
    public String getGroup(String world, String playername) {
        UUID uuid = tcutils.getUUIDResolver().getOrCreateUUID(playername);
        PermissionUser user = manager.getUser(uuid);
        if(user == null) return null;
        // PermissionGroup[] groups = user.getGroups(world);
        List<PermissionGroup> groups = user.getParents(world);
        return groups.size() > 0 ? groups.get(0).getName() : null;
    }

    @Override
    public List<String> getGroups(String world, String playername) {
        List<String> rt = new ArrayList<String>();
        UUID uuid = tcutils.getUUIDResolver().getOrCreateUUID(playername);
        PermissionUser user = manager.getUser(uuid);
        if(user == null) return rt;
        append(rt, user.getParents()); // Bah, i guess we have to do it ourselves again....
        return rt;
    }
    
    private List<String> append(List<String> groups, List<PermissionGroup> grp) {
        for(PermissionGroup g: grp) {
            groups.add(g.getName());
            append(groups, g.getParentGroups(null));
        }
        return groups;
    }

    @Override
    public void setGroup(String world, String playername, String group) {
        UUID uuid = tcutils.getUUIDResolver().getOrCreateUUID(playername);
        PermissionUser user = manager.getUser(uuid);
        if(user == null) return;
        for(PermissionGroup g : user.getGroups()) {
            user.removeGroup(g);
        }
        if(group == null) return;
        PermissionGroup grp = manager.getGroup(group);
        if(grp == null) return;
        user.addGroup(grp);
    }
    

    @Override
    public void addGroup(String world, String playername, String group) {
        UUID uuid = tcutils.getUUIDResolver().getOrCreateUUID(playername);
        PermissionUser user = manager.getUser(uuid);
        if(user == null) return;
        PermissionGroup g = manager.getGroup(group);
        if(g == null) return;
        user.addGroup(g);
    }

    @Override
    public void removeGroup(String world, String playername, String group) {
        UUID uuid = tcutils.getUUIDResolver().getOrCreateUUID(playername);
        PermissionUser user = manager.getUser(uuid);
        if(user == null) return;
        user.removeGroup(group, world);
    }

    @Override
    public void addPermission(String world, String playername, String node) {
        UUID uuid = tcutils.getUUIDResolver().getOrCreateUUID(playername);
        PermissionUser user = manager.getUser(uuid);
        if(user == null) return;
        user.addPermission(node, world);
    }

    @Override
    public void removePermission(String world, String playername, String node) {
        UUID uuid = tcutils.getUUIDResolver().getOrCreateUUID(playername);
        PermissionUser user = manager.getUser(uuid);
        if(user == null) return;
        user.removePermission(node, world);
    }

    @Override
    public String getName() {
        return "PermissionsEx";
    }

    @Override
    public String getPrefix(String world, String playername) {
        UUID uuid = tcutils.getUUIDResolver().getOrCreateUUID(playername);
        PermissionUser user = manager.getUser(uuid);
        if(user == null) return "";
        return user.getPrefix(world);
    }

    @Override
    public String getSuffix(String world, String playername) {
        UUID uuid = tcutils.getUUIDResolver().getOrCreateUUID(playername);
        PermissionUser user = manager.getUser(uuid);
        if(user == null) return "";
        return user.getSuffix(world);
    }

    @Override
    public String getOption(String world, String playername, String option) {
        UUID uuid = tcutils.getUUIDResolver().getOrCreateUUID(playername);
        PermissionUser user = manager.getUser(uuid);
        if(user == null) return "";
        return user.getOption(option, world);
    }

}
