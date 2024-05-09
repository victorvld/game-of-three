package victorvld.gameofthree.domain.entities;

import victorvld.gameofthree.controller.dto.GameBoard;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GameState {
    private final Set<Player> players = ConcurrentHashMap.newKeySet();
    private GameMode mode;
    private GameStatus status;
    private Player startingPlayer;
    private Player receivingPlayer;
    private Player currentTurn;
    private Integer number;
    private String lastMove;

    public GameState() {
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setTurn(Player player) {
        this.currentTurn = player;
    }

    public void setLastMove(String lastMove) {
        this.lastMove = lastMove;
    }

    public boolean areBothPlayerConnected() {
        return this.players.size() == 2;
    }

    public GameBoard generateBoardSnapshot() {
        return new GameBoard(
                String.valueOf(this.number),
                this.lastMove,
                this.currentTurn.getName(),
                mode.getMode());
    }

    public void setStartingPlayer(Player startingPlayer) {
        this.startingPlayer = startingPlayer;
    }

    public void setReceivingPlayer(Player receivingPlayer) {
    }

    public void setCurrentTurn(Player player) {
        this.currentTurn = player;
    }

    public boolean isGameStarted() {
        return GameStatus.STARTED.equals(this.status);
    }
}
