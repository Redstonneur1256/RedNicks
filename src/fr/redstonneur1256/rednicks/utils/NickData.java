package fr.redstonneur1256.rednicks.utils;

import fr.redstonneur1256.rednicks.RedNicks;
import fr.redstonneur1256.rednicks.api.NickChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class NickData {

    private final String realName;
    private final UUID uuid;
    private String nickName;
    public NickData(Player player) {
        this.realName = player.getName();
        this.uuid = player.getUniqueId();
        this.nickName = null;
    }

    /**
     * Set the player nickname
     * @param name the new name or null to reset it
     * @throws Exception if the nick change has failed
     */
    public void setNick(String name) throws Exception {
        Player player = Bukkit.getPlayer(uuid);

        if(name == null)
            name = realName;

        String oldName = player.getName();
        boolean willChangeName = !oldName.equals(name);


        Reflection.setProfileName(player, name);

        Bukkit.getPluginManager().callEvent(new NickChangeEvent(player, this, oldName, name));

        this.nickName = name;

        if(willChangeName) {
            player.setPlayerListName(name);

            List<Player> canSee = Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(online -> online.canSee(player))
                    .collect(Collectors.toList());
            canSee.remove(player);

            canSee.forEach(online -> online.hidePlayer(player));
            canSee.forEach(online -> online.showPlayer(player));
        }

    }

    /**
     * Remove the player nickname
     * @see NickData#setNick(String)
     * @throws Exception if the nick change has failed
     */
    public void removeNick() throws Exception {
        setNick(null);
    }

    /**
     * Set the player nick to a random nick.
     * @see RedNicks#getRandomNames()
     * @see NickData#setNick(String)
     * @throws IllegalStateException if no free random name is available
     * @throws Exception if the nick change failed
     */
    public void setRandomNick() throws Exception {
        String name;
        RedNicks plugin = RedNicks.get();
        List<String> names = plugin.getRandomNames();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int attempt = 0;
        do {
            attempt ++; // Do not stuck server on infinite loop.
            name = names.get(random.nextInt(names.size()));
        } while (plugin.isUsed(name) && attempt < 1000);
        if(attempt == 1000) {
            throw new IllegalStateException("Cannot find available name.");
        }
        setNick(name);
    }

    public boolean isNicked() {
        return nickName != null && !nickName.equals(realName);
    }

    public String getRealName() { return realName; }
    public UUID getUuid() { return uuid; }
    public String getNickName() { return nickName; }

}
