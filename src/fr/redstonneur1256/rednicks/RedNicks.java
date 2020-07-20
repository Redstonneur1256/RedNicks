package fr.redstonneur1256.rednicks;

import fr.redstonneur1256.rednicks.api.PermissionHolder;
import fr.redstonneur1256.rednicks.base.DefaultPermissionHolder;
import fr.redstonneur1256.rednicks.commands.NickCommand;
import fr.redstonneur1256.rednicks.utils.I18N;
import fr.redstonneur1256.rednicks.utils.Metrics;
import fr.redstonneur1256.rednicks.utils.NickData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class RedNicks extends JavaPlugin {

    private static RedNicks instance;
    private Map<UUID, NickData> dataMap;
    private PermissionHolder permissionHolder;
    private List<String> randomNames;
    public RedNicks() {
        if(instance != null) {
            throw new IllegalStateException("Multiple instances of RedNicks.");
        }
        RedNicks.instance = this;
        this.dataMap = new HashMap<>();
        this.permissionHolder = new DefaultPermissionHolder();
        this.randomNames = new ArrayList<>();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages/en.yml", false);
        saveResource("messages/fr.yml", false);
        saveResource("defaultNames.txt", false);

        FileConfiguration config = getConfig();

        ConsoleCommandSender console = Bukkit.getConsoleSender();
        try {
            File namesFile = new File(getDataFolder(), config.getString("randomNames", "defaultNames.txt"));
            BufferedReader reader = new BufferedReader(new FileReader(namesFile));
            String line;
            List<String> names = new ArrayList<>();
            while((line = reader.readLine()) != null) {
                line = line.trim();
                if(!line.isEmpty())
                    names.add(line);
            }
            reader.close();
            randomNames.addAll(names);
            if(randomNames.isEmpty()) {
                console.sendMessage(ChatColor.RED + "No random names found, try deleting the 'randomNames.txt' file and reload");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            Pattern specialCharacters = Pattern.compile("$*[^\\W]*$");

            List<String> invalid = new ArrayList<>();
            for (String name : randomNames) {
                int length = name.length();
                boolean isInvalid =
                        !specialCharacters.matcher(name).matches() ||
                        length < 3 ||
                        length > 16;
                if(isInvalid) {
                    invalid.add(name);
                    System.out.println(specialCharacters.matcher(name).matches());
                }
            }
            for (String name : invalid) {
                console.sendMessage("§cRemoved '" + name + "' because its invalid.");
            }
            randomNames.removeAll(invalid);

            if(!invalid.isEmpty()) { // The list was modifier
                PrintWriter writer = new PrintWriter(new FileOutputStream(namesFile), true);
                for (String randomName : randomNames) {
                    writer.println(randomName);
                }
                writer.close();
            }



        } catch (Exception e) {
            e.printStackTrace();
            console.sendMessage(ChatColor.RED + "Impossible to load random names, disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        I18N.loadMessages(new File(getDataFolder(), config.getString("messagesFile", "messages/en.yml")));

        getCommand("nick").setExecutor(new NickCommand());
        getServer().getPluginManager().registerEvents(new Listeners(), this);

        Metrics metrics = new Metrics(this, 8231);
    }

    @Override
    public void onDisable() {
        for (NickData data : dataMap.values()) {
            try {
                data.removeNick();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Get a player nick data to get his name
     * @return the data
     */
    public NickData getData(Player player) {
        UUID uuid = player.getUniqueId();
        NickData data = dataMap.get(uuid);
        if(data == null) {
            dataMap.put(uuid, data = new NickData(player));
        }
        return data;
    }

    /**
     * Remove the data from the cache and remove nickname when player disconnect
     * @param uuid the player UUID
     */
    protected void removeData(UUID uuid) {
        NickData data = dataMap.remove(uuid);
        if(data != null) {
            try {
                data.removeNick();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Check if an user name is online
     * @param name the name or nick to check
     * @return if hes online
     */
    public boolean isUsed(String name) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .anyMatch(player -> player.getName().equalsIgnoreCase(name))
                ||
                dataMap.values()
                        .stream()
                        .anyMatch(data -> name.equals(data.getNickName()))
                ;
    }

    /**
     * Get a player uuid by his name ignoring the nick.
     * @param realName The player real name
     * @return the uuid of player if its found, else null
     */
    public UUID getUUID(String realName) {
        for (NickData data : dataMap.values()) {
            if(data.getRealName().equalsIgnoreCase(realName)) {
                return data.getUuid();
            }
        }
        return null;
    }

    /**
     * Get a player by his name ignoring the nick.
     * @param realName The player real name
     * @return the player if its found, else null
     * @see RedNicks#getUUID(String)
     */
    public Player getPlayer(String realName) {
        UUID uuid = getUUID(realName);
        return uuid == null ? null : Bukkit.getPlayer(uuid);
    }

    /**
     * Get the plugin instance
     * @return the plugin
     */
    public static RedNicks get() { return instance; }

    /**
     * @return the current permission holder used for plugin.
     */
    public PermissionHolder getPermissionHolder() { return permissionHolder; }

    /**
     * Set the permission holder for the plugin
     * @param permissionHolder the new permission holder
     */
    public void setPermissionHolder(PermissionHolder permissionHolder) { this.permissionHolder = permissionHolder; }

    /**
     * @return the list of random names available.
     */
    public List<String> getRandomNames() { return randomNames; }
}
