package net.samagames.api.network;

import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public interface IJoinManager extends Listener {

    /**
     * Register a JoinHandler which is going to be called by the manager
     * @param handler The handler which will handle join requests
     * @param priority The handler priority (0 = Lowest, please do not use priorities under 10)
     */
    public void registerHandler(IJoinHandler handler, int priority);

    public int countExpectedPlayers();
    public HashSet<UUID> getExpectedPlayers();
    public List<UUID> getModeratorsExpected();

}
