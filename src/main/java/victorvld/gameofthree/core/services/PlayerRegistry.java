package victorvld.gameofthree.core.services;

import victorvld.gameofthree.core.domain_models.Player;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerRegistry {
    private final Set<Player> players = ConcurrentHashMap.newKeySet();

    public boolean areBothPlayerConnected() {
        return this.players.size() == 2;
    }

    public void add(Player player) {
        this.players.add(player);
    }

    public boolean containsPlayer(String playerId) {
        return players.contains(Player.of(playerId));
    }

    public void reset() {
        this.players.clear();
    }
}
