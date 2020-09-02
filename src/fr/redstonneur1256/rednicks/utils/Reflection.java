package fr.redstonneur1256.rednicks.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

public class Reflection {

    private static final String bukkitPackage = "org.bukkit.craftbukkit.";
    private static final String nmsPackage = "net.minecraft.server.";
    private static final String version = Bukkit.getServer().getClass().getPackage().getName().substring(bukkitPackage.length());
    private static final Method getHandleMethod;
    private static final Field gameProfileField;
    private static final Constructor<?> gameProfileConstructor;

    static {
        try {
            Class<?> craftPlayer = getBukkitClass("entity.CraftPlayer");
            Class<?> entityPlayer = getNMSClass("EntityHuman");
            Class<?> gameProfile = null;// = getClass("com.mojang.authlib.GameProfile"); Can be in another package like net.minecraft.server.libs.com....

            getHandleMethod = craftPlayer.getDeclaredMethod("getHandle");
            getHandleMethod.setAccessible(true);

            Field profileField = null;
            for(Field field : entityPlayer.getDeclaredFields()) {
                if(field.getType().getSimpleName().equalsIgnoreCase("GameProfile")) {
                    profileField = field;
                    gameProfile = field.getType();
                    break;
                }
            }
            if(profileField == null)
                throw new IllegalStateException("Cannot locate game profile field");

            gameProfileField = profileField;
            gameProfileField.setAccessible(true);

            gameProfileConstructor = gameProfile.getConstructor(UUID.class, String.class);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);

            modifiersField.set(gameProfileField, gameProfileField.getModifiers() & ~Modifier.FINAL);

        }catch(Exception exception) {
            exception.printStackTrace();
            throw new IllegalStateException("Cannot initialize reflection.");
        }
    }

    public static Class<?> getBukkitClass(String name) throws ClassNotFoundException {
        return getClass(bukkitPackage + version + "." + name);
    }

    public static Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return getClass(nmsPackage + version + "." + name);
    }

    public static Class<?> getClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }


    public static void setProfileName(Player player, String name) throws Exception {
        Object entityPlayer = getHandleMethod.invoke(player);
        Object gameProfile = gameProfileConstructor.newInstance(player.getUniqueId(), name);
        gameProfileField.set(entityPlayer, gameProfile);
    }
}