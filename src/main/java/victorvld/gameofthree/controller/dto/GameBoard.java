package victorvld.gameofthree.controller.dto;

public record GameBoard(Integer currentNumber, String lastMove, String playerTurn, String gameMode, String winner) {

}
