package victorvld.gameofthree.domain.entities;

import victorvld.gameofthree.controller.dto.GameBoard;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GameState {
    private final Set<Player> players = ConcurrentHashMap.newKeySet();
    private GameMode mode = GameMode.MANUAL;
    private GameStatus status = GameStatus.READY_TO_PLAY;
    private String winner;
    private Player currentTurn;
    private Integer number;
    private String lastMove;

    public GameState() {
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void setCurrentTurn(Player player) {
        this.currentTurn = player;
    }

    public void setLastMove(String lastMove) {
        this.lastMove = lastMove;
    }

    public void setInitialNumber(Integer integer) {
        this.number = integer;
    }

    public GameBoard generateBoardSnapshot() {
        return new GameBoard(
                this.number,
                this.lastMove,
                this.currentTurn.getName(),
                mode.getMode(),
                winner
        );
    }

    public boolean areBothPlayerConnected() {
        return this.players.size() == 2;
    }

    public boolean isGameStarted() {
        return GameStatus.STARTED.equals(this.status);
    }

    public void add(Player player) {
        this.players.add(player);
    }

    public boolean containsPlayer(String playerId) {
        return players.contains(Player.of(playerId));
    }

    public void applyMove(int move) {
        this.number = (number + move) / 3;
        if (number == 1) {
            this.status = GameStatus.FINISHED;
            this.winner = currentTurn.getName();
            this.lastMove = "%s made a move of %s and win the game".formatted(currentTurn.getName(), move);
            this.players.clear();
        } else {
            this.lastMove = "%s made a move of %s".formatted(currentTurn.getName(), move);
            this.currentTurn = currentTurn.opposite();
        }
    }

    public boolean isGameFinished() {
        return GameStatus.FINISHED.equals(this.status);
    }

    public String getWinner() {
        return winner;
    }

    public void resetToReadyToStart() {
        this.status = GameStatus.READY_TO_PLAY;
        this.number = null;
        this.lastMove = null;
        this.currentTurn = null;
        this.winner = null;
    }
}
