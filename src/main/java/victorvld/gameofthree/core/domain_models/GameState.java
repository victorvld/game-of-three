package victorvld.gameofthree.core.domain_models;

public final class GameState {
    private GameResolution resolution = GameResolution.NOT_INITIATED;
    private Player turn;
    private int number;
    private int restNumber;
    private final GameRules gameRules;

    public GameState() {
        gameRules = new GameRules(this);
    }

    public void applyStart(Integer initialNumber, Player startingPlayer) {
        this.number = initialNumber;
        this.turn = startingPlayer.opposite();
        this.resolution = GameResolution.NOT_RESOLVED;
        this.restNumber = 0;
    }

    public void applyMove(int move) {
        gameRules.calculateNewNumbers(move);
        gameRules.resolveNewGameStatus();
    }

    public void reset() {
        this.number = 0;
        this.restNumber = 0;
        this.turn = null;
        this.resolution = GameResolution.NOT_INITIATED;
    }

    public GameBoard generateBoardSnapshot() {
        return new GameBoard(
                this.number,
                this.restNumber,
                this.turn,
                this.resolution
        );
    }

    public boolean isGameStarted() {
        return !GameResolution.NOT_INITIATED.equals(this.resolution);
    }

    public boolean isGameOver() {
        return GameResolution.NOT_RESOLVED != this.resolution && GameResolution.NOT_INITIATED != this.resolution;
    }

    private record GameRules(GameState gameState) {

        private void calculateNewNumbers(int move) {
            this.gameState.restNumber = (this.gameState.number + move) % 3;
            this.gameState.number = (this.gameState.number + move) / 3;
        }

        private void resolveNewGameStatus() {
            if (isGameWon()) {
                finishGameWithWinner();
            } else if (isDraw()) {
                finishGameAsDraw();
            } else {
                switchTurn();
            }
        }

        private boolean isGameWon() {
            return this.gameState.number == 1 && this.gameState.restNumber == 0;
        }

        private void finishGameWithWinner() {
            this.gameState.resolution = GameResolution.of(this.gameState.turn.getName());
        }

        private boolean isDraw() {
            return this.gameState.number < 2 && this.gameState.restNumber != 0;
        }

        private void finishGameAsDraw() {
            this.gameState.resolution = GameResolution.DRAW;
        }

        private void switchTurn() {
            this.gameState.turn = gameState.turn.opposite();
        }
    }
}
