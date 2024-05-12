package victorvld.gameofthree.infrastructure.controller.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import victorvld.gameofthree.core.domain_models.GameBoard;
import victorvld.gameofthree.core.domain_models.GameResolution;
import victorvld.gameofthree.core.domain_models.Player;

class BoardMapperTest {

    @Test
    public void testToDto() {
        GameBoard board = new GameBoard(5, 10, Player.PLAYER1,  GameResolution.NOT_RESOLVED);
        BoardMapper mapper = new BoardMapper();

        var result = mapper.toDto(board);

        Assertions.assertEquals(5, result.currentNumber());
        Assertions.assertEquals(10, result.restNumber());
        Assertions.assertEquals("player1", result.playerTurn());
        Assertions.assertEquals("not_resolved", result.resolution());
    }
}
