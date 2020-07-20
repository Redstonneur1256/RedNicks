package fr.redstonneur1256.rednicks.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

public class Reflection {

    private static final String BUKKIT_PACKAGE = "org.bukkit.craftbukkit.";
    private static final String NMS_PACKAGE = "net.minecraft.server.";
    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(BUKKIT_PACKAGE.length());
    private static final Method GET_HANDLE_METHOD;
    private static final Field GAME_PROFILE_FIELD;
    private static final Constructor<?> GAME_PROFILE_CONSTRUCTOR;
    static  {
        try {
            Class<?> CRAFT_PLAYER = getBukkitClass("entity.CraftPlayer");
            Class<?> ENTITY_PLAYER = getNMSClass("EntityHuman");
            Class<?> GAME_PROFILE = getClass("com.mojang.authlib.GameProfile");

            GET_HANDLE_METHOD = CRAFT_PLAYER.getDeclaredMethod("getHandle");
            GET_HANDLE_METHOD.setAccessible(true);

            Field profileField = null;
            for (Field field : ENTITY_PLAYER.getDeclaredFields()) {
                if(field.getType().equals(GAME_PROFILE)) {
                    profileField = field;
                    break;
                }
            }
            if(profileField == null)
                throw new IllegalStateException("Cannot locate game profile field");

            GAME_PROFILE_FIELD = profileField;
            GAME_PROFILE_FIELD.setAccessible(true);

            GAME_PROFILE_CONSTRUCTOR = GAME_PROFILE.getConstructor(UUID.class, String.class);

            Field MODIFIERS_FIELD = Field.class.getDeclaredField("modifiers");
            MODIFIERS_FIELD.setAccessible(true);

            MODIFIERS_FIELD.set(GAME_PROFILE_FIELD, GAME_PROFILE_FIELD.getModifiers() & ~Modifier.FINAL);



        }catch (Exception exception) {
            exception.printStackTrace();
            throw new IllegalStateException("Cannot initialize reflection.");
        }
    }

    public static Class<?> getBukkitClass(String name) throws ClassNotFoundException {
        return getClass(BUKKIT_PACKAGE + VERSION + "." + name);
    }

    public static Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return getClass(NMS_PACKAGE + VERSION + "." + name);
    }

    public static Class<?> getClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }


    public static void setProfileName(Player player, String name) throws Exception {
        Object entityPlayer = GET_HANDLE_METHOD.invoke(player);
        Object gameProfile = GAME_PROFILE_CONSTRUCTOR.newInstance(player.getUniqueId(), name);
        GAME_PROFILE_FIELD.set(entityPlayer, gameProfile);
    }
}