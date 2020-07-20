package fr.redstonneur1256.rednicks.api;

import fr.redstonneur1256.rednicks.utils.NickData;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class NickChangeEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private NickData data;
    private String previousName;
    private String newName;
    public NickChangeEvent(Player player, NickData data, String previousName, String newName) {
        super(player);
        this.data = data;
        this.previousName = previousName;
        this.newName = newName;
    }

    public NickData getData() { return data; }
    public String getPreviousName() { return previousName; }
    public String getNewName() { return newName; }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}