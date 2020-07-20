package fr.redstonneur1256.rednicks.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class I18N {

    private static YamlConfiguration configuration;

    /**
     * Load the messages
     * @param file the messages file
     */
    public static void loadMessages(File file) {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Get a message by his key
     * @param key the message key
     * @param arguments the arguments, replacing {0}, {1} ect in the message
     * @return the message formatted with arguments
     */
    public static String getMessage(String key, Object... arguments) {
        String string = configuration.getString(key, "Missing message \"" + key + "\", please contact admins.");
        for (int i = 0; i < arguments.length; i++) {
            string = string.replace("{" + i + "}", String.valueOf(arguments[i]));
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
