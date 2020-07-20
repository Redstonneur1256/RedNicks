package fr.redstonneur1256.rednicks.base;

import fr.redstonneur1256.rednicks.api.Permission;
import fr.redstonneur1256.rednicks.api.PermissionHolder;
import org.bukkit.entity.Player;

public class DefaultPermissionHolder implements PermissionHolder {

    @Override
    public boolean hasPermission(Player player, Permission permission) {
        return player.hasPermission(permission.getCode());
    }

}
