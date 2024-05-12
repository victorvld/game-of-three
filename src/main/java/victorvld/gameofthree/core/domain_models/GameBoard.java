package victorvld.gameofthree.core.domain_models;

public record GameBoard(
        int currentNumber,
        int restNumber,
        Player playerTurn,
        GameResolution resolution) {
}
