package victorvld.gameofthree.domain.entities;

import victorvld.gameofthree.controller.dto.GameBoard;
import victorvld.gameofthree.controller.dto.StartGameEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GameState {
    private final Set<Player> players = ConcurrentHashMap.newKeySet();
    private GameMode mode = GameMode.MANUAL;
    private GameStatus status = GameStatus.READY_TO_PLAY;
    private String winner;
    private Player currentTurn;
    private Integer number;
    private Integer restNumber;
    private String lastMove;

    public GameState() {
    }

    public GameBoard generateBoardSnapshot() {
        return new GameBoard(
                this.number,
                this.restNumber,
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

    public void apply(StartGameEvent event) {
        var receivingPlayer = Player.of(event.startingPlayer()).opposite();
        this.number = event.initialNumber();
        this.lastMove = "No move has been made yet";
        this.currentTurn = receivingPlayer;
        this.status = GameStatus.STARTED;
        this.winner = null;
        this.restNumber = null;
    }

    public void apply(int move) {
        this.restNumber = (number + move) % 3;
        this.number = (number + move) / 3;
        if (number == 1 && this.restNumber == 0) {
            this.status = GameStatus.FINISHED;
            this.winner = currentTurn.getName();
            this.lastMove = "%s made a move of %s and win the game".formatted(currentTurn.getName(), move);
            this.players.clear();
        } else if (number < 2) {
            // Here we are covering cases where the number 1 can't be reached but the game cannot be finished. Therefore, we consider it a draw.
            this.status = GameStatus.FINISHED;
            this.winner = "draw";
            this.lastMove = "The game ends in a draw. No player has been able to win the game";
            this.players.clear();
        } else {
            // TODO: 10/05/2024 Write a test for this branch
            this.lastMove = "%s made a move of %s".formatted(currentTurn.getName(), move);
            this.currentTurn = currentTurn.opposite();
        }
    }

    public void reset() {
        this.status = GameStatus.READY_TO_PLAY;
        this.number = null;
        this.restNumber = null;
        this.lastMove = null;
        this.currentTurn = null;
        this.winner = null;
    }

    public boolean isGameFinished() {
        return GameStatus.FINISHED.equals(this.status);
    }
}
