package fr.redstonneur1256.rednicks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        RedNicks redNicks = RedNicks.get();
        redNicks.getData(event.getPlayer()); // Load his data.
        RedNicks.getPlugin(RedNicks.class).getData(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        RedNicks redNicks = RedNicks.get();
        redNicks.removeData(event.getPlayer().getUniqueId());
    }

}
