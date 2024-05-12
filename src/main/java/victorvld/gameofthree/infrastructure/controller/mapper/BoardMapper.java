package victorvld.gameofthree.infrastructure.controller.mapper;

import victorvld.gameofthree.core.domain_models.GameBoard;
import victorvld.gameofthree.infrastructure.controller.dto.GameBoardDto;

public class BoardMapper {

    public GameBoardDto toDto(GameBoard board) {
        return new GameBoardDto(
                board.currentNumber(),
                board.restNumber(),
                board.playerTurn().getName(),
                board.resolution().getResult());
    }
}
