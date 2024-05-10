package victorvld.gameofthree.controller.dto;

public record GameBoard(Integer currentNumber, Integer restNumber, String lastMove, String playerTurn, String gameMode, String winner) {

}
