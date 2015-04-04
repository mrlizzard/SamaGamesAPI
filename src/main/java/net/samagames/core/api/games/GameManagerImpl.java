package net.samagames.core.api.games;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GameManager;
import net.samagames.api.games.IManagedGame;
import net.samagames.api.games.themachine.CoherenceMachine;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.games.themachine.CoherenceMachineImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class GameManagerImpl implements GameManager
{
    private final SamaGamesAPI api;

    private ArrayList<UUID> playersDisconnected;
    private HashMap<UUID, Integer> playerDisconnectTime;
    private HashMap<UUID, Integer> playerReconnectedTimers;
    private IManagedGame game;

    private boolean allowReconnect;
    private int maxReconnectTime;

    public GameManagerImpl(SamaGamesAPI api)
    {
        this.api = api;
        this.game = null;

        this.playersDisconnected = new ArrayList<>();
        this.playerDisconnectTime = new HashMap<>();
        this.playerReconnectedTimers = new HashMap<>();
    }

    @Override
    public void registerGame(IManagedGame game)
    {
        if(this.game != null)
            throw new IllegalStateException("A game is already registered!");

        this.game = game;
        APIPlugin.getApi().getJoinManager().registerHandler(new GameLoginHandler(this), 100);

        APIPlugin.log(Level.INFO, "Registered game '" + game.getGameName() + "' successfuly!");
    }

    @Override
    public void onPlayerDisconnect(Player player)
    {
        if(this.allowReconnect)
        {
            this.playersDisconnected.add(player.getUniqueId());

            this.playerReconnectedTimers.put(player.getUniqueId(), Bukkit.getScheduler().scheduleAsyncRepeatingTask(APIPlugin.getInstance(), new Runnable() {
                int before = 0;
                int now = 0;
                boolean bool = false;

                @Override
                public void run()
                {
                    if (!this.bool)
                    {
                        if(playerDisconnectTime.containsKey(player.getUniqueId()))
                            this.before = playerDisconnectTime.get(player.getUniqueId());

                        this.bool = true;
                    }

                    if (this.before == maxReconnectTime * 2 || this.now == maxReconnectTime)
                    {
                        onPlayerReconnectTimeOut(player);
                    }

                    this.before++;
                    this.now++;
                    playerDisconnectTime.put(player.getUniqueId(), before);
                }
            }, 20L, 20L));
        }
    }

    @Override
    public void onPlayerReconnect(Player player)
    {
        if(this.playerReconnectedTimers.containsKey(player.getUniqueId()))
        {
            Bukkit.getScheduler().cancelTask(this.playerReconnectedTimers.get(player.getUniqueId()));
            this.playerReconnectedTimers.remove(player.getUniqueId());
        }

        this.game.playerReconnect(player);
    }

    @Override
    public void onPlayerReconnectTimeOut(Player player)
    {
        if(this.playerReconnectedTimers.containsKey(player.getUniqueId()))
        {
            Bukkit.getScheduler().cancelTask(this.playerReconnectedTimers.get(player.getUniqueId()));
            this.playerReconnectedTimers.remove(player.getUniqueId());
        }

        this.game.playerReconnectTimeOut(player);
    }

    @Override
    public void allowReconnect(boolean flag)
    {
        this.allowReconnect = flag;
    }

    @Override
    public void setMaxReconnectTime(int minutes)
    {
        this.maxReconnectTime = minutes;
    }

    @Override
    public IManagedGame getGame()
    {
        return this.game;
    }

    @Override
    public CoherenceMachine getCoherenceMachine()
    {
        if(this.game == null)
            throw new NullPointerException("Can't get CoherenceMachine because game is null!");

        return new CoherenceMachineImpl(this.game);
    }

    @Override
    public boolean isWaited(UUID uuid)
    {
        return this.playersDisconnected.contains(uuid);
    }

    @Override
    public boolean isReconnectAllowed()
    {
        return this.allowReconnect;
    }
}