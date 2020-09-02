package fr.redstonneur1256.rednicks.commands;

import fr.redstonneur1256.rednicks.RedNicks;
import fr.redstonneur1256.rednicks.api.Permission;
import fr.redstonneur1256.rednicks.api.PermissionHolder;
import fr.redstonneur1256.rednicks.utils.I18N;
import fr.redstonneur1256.rednicks.utils.NickData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can't change name.");
            return false;
        }
        Player player = (Player) sender;
        RedNicks redNicks = RedNicks.get();
        PermissionHolder permissionHolder = redNicks.getPermissionHolder();
        NickData data = redNicks.getData(player);

        try {
            if(data.isNicked()) {
                data.removeNick();
                player.sendMessage(I18N.getMessage("removed"));
                return false;
            }

            if(args.length == 0) {
                player.sendMessage(I18N.getMessage("usage", label));
                return false;
            }

            if(args[0].equalsIgnoreCase("random")) {
                if(permissionHolder.hasPermission(player, Permission.RANDOM_NICK)) {
                    data.setRandomNick();
                    player.sendMessage(I18N.getMessage("set", data.getNickName()));
                }else {
                    player.sendMessage(I18N.getMessage("permission"));
                }
            }else {
                if(permissionHolder.hasPermission(player, Permission.CUSTOM_NICK)) {
                    String name = args[0];
                    if(name.length() > 16)
                        name = name.substring(0, 16);
                    data.setNick(name);
                    player.sendMessage(I18N.getMessage("set", name));
                }else {
                    player.sendMessage(I18N.getMessage("permission"));
                }
            }
        }catch(Exception exception) {
            exception.printStackTrace();
            player.sendMessage(I18N.getMessage("error"));
        }
        return false;
    }
}
