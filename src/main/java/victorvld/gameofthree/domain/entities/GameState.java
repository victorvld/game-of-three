package victorvld.gameofthree.domain.entities;

import victorvld.gameofthree.controller.dto.GameBoard;
import victorvld.gameofthree.controller.dto.StartGameEvent;

public final class GameState {
    private GameMode mode = GameMode.MANUAL;
    private GameStatus status = GameStatus.READY_TO_PLAY;
    private String winner;
    private Player turn;
    private Integer number;
    private Integer restNumber;
    private String lastMove;

    public GameState() {
        // Default constructor
    }

    public void apply(StartGameEvent event) {
        this.number = event.initialNumber();
        this.lastMove = "No move has been made yet";
        this.turn = Player.of(event.startingPlayer()).opposite();
        this.status = GameStatus.STARTED;
        this.winner = null;
        this.restNumber = null;
    }

    public void apply(int move) {
        this.restNumber = (number + move) % 3;
        this.number = (number + move) / 3;
        if (number == 1 && this.restNumber == 0) {
            this.status = GameStatus.FINISHED;
            this.winner = turn.getName();
            this.lastMove = "%s made a move of %s and win the game".formatted(turn.getName(), move);
        } else if (number < 2) {
            // Here we are covering cases where the number 1 can't be reached but the game cannot be finished. Therefore, we consider it a draw.
            this.status = GameStatus.FINISHED;
            this.winner = "draw";
            this.lastMove = "The game ends in a draw. No player has been able to win the game";
        } else {
            this.lastMove = "%s made a move of %s".formatted(turn.getName(), move);
            this.turn = turn.opposite();
        }
    }

    public void reset() {
        this.status = GameStatus.READY_TO_PLAY;
        this.number = null;
        this.restNumber = null;
        this.lastMove = null;
        this.turn = null;
        this.winner = null;
    }

    public GameBoard generateBoardSnapshot() {
        return new GameBoard(
                this.number,
                this.restNumber,
                this.lastMove,
                this.turn.getName(),
                mode.getMode(),
                winner
        );
    }

    public boolean isGameStarted() {
        return GameStatus.STARTED.equals(this.status);
    }

    public boolean isGameFinished() {
        return GameStatus.FINISHED.equals(this.status);
    }
}
