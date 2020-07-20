package fr.redstonneur1256.rednicks.api;

import org.bukkit.entity.Player;

public interface PermissionHolder {

    /**
     * @param player the player who want to change his nick
     * @param permission the wanted permission
     * @return if he can do the action
     */
    boolean hasPermission(Player player, Permission permission);

}
