package victorvld.gameofthree.core.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import victorvld.gameofthree.core.domain_models.GameResolution;
import victorvld.gameofthree.core.domain_models.GameState;
import victorvld.gameofthree.core.domain_models.Player;
import victorvld.gameofthree.core.exceptions.PlayerConnectionException;
import victorvld.gameofthree.core.exceptions.StartGameException;
import victorvld.gameofthree.infrastructure.controller.dto.StartGameEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DomainSpecificationTests {
    private GameEngine underTest;

    @BeforeEach
    void setUp() {
        underTest = new GameEngine(new GameState(), new PlayerRegistry());
    }

    @Nested
    class WhenGameFull {
        @BeforeEach
        void setUp() {
            underTest.handlePlayerConnect("player1");
            underTest.handlePlayerConnect("player2");
        }

        @Test
        void shouldThrowStartGameException() {
            var exception = assertThrows(PlayerConnectionException.class, () -> underTest.handlePlayerConnect("player3"));

            assertEquals(GameMessagesGenerator.generateGameIsFullErrorMessage(), exception.getMessage());
        }
    }

    @Nested
    class WhenPlayerAlreadyConnected {

        private final String playerId = "player1";

        @BeforeEach
        void setUp() {
            underTest.handlePlayerConnect(playerId);
        }

        @Test
        void shouldThrowStartGameException() {
            var exception = assertThrows(PlayerConnectionException.class, () -> underTest.handlePlayerConnect("player1"));

            assertEquals(GameMessagesGenerator.formatPlayerAlreadyConnectedError(playerId), exception.getMessage());
        }
    }

    @Nested
    class WhenGameNotFull {
        @Test
        void shouldProcessConnectEvent() {
            var playerId = "player1";
            var message = underTest.handlePlayerConnect(playerId);

            assertEquals(GameMessagesGenerator.formatConnectMessage(playerId), message);
        }
    }

    @Nested
    class WhenNotEnoughPlayers {
        @BeforeEach
        void setUp() {
            underTest.handlePlayerConnect("player1");
        }

        @Test
        void shouldThrowStartGameException() {
            var event = new StartGameEvent("player1", 1);

            var exception = assertThrows(StartGameException.class, () -> underTest.handleStartGame(1, Player.of("player1")));

            assertEquals(GameMessagesGenerator.generateNotEnoughPlayersErrorMessage(), exception.getMessage());
        }
    }

    @Nested
    class WhenGameAlreadyStarted {
        @BeforeEach
        void setUp() {
            underTest.handlePlayerConnect("player1");
            underTest.handlePlayerConnect("player2");
            underTest.handleStartGame(76, Player.of("player1"));
        }

        @Test
        void shouldThrowStartGameException() {
            var exception = assertThrows(StartGameException.class, () -> underTest.handleStartGame(1, Player.of("player1")));

            assertEquals(GameMessagesGenerator.generateGameAlreadyStartedErrorMessage(), exception.getMessage());
        }
    }

    @Nested
    class WhenGameCanStart {
        @BeforeEach
        void setUp() {
            underTest.handlePlayerConnect("player1");
            underTest.handlePlayerConnect("player2");
        }

        @Test
        void shouldProcessStartGameEvent() {
            var gameBoard = underTest.handleStartGame(76, Player.of("player1"));

            assertEquals(Player.PLAYER2, gameBoard.playerTurn());
            assertEquals(76, gameBoard.currentNumber());
            assertEquals(0, gameBoard.restNumber());
            assertEquals(GameResolution.NOT_RESOLVED, gameBoard.resolution());
        }
    }

    @Nested
    class WhenPlayerMakeWinningMove {
        @BeforeEach
        void setUp() {
            underTest.handlePlayerConnect("player1");
            underTest.handlePlayerConnect("player2");
            underTest.handleStartGame(3, Player.of("player1"));
        }

        @Test
        void shouldProcessMoveEvent() {
            var expectedMessages = new LinkedList<>(List.of(
                    "player2 has made a move. Previous number: 3, added number: 0, resulting number: 1. ( 3 + 0 ) / 3 = 1",
                    "Game over. player2 has emerged victorious."));

            var boardPair = underTest.handlePlayerMove(0);

            assertEquals(Player.PLAYER2, boardPair.getGameBoard().playerTurn());
            assertEquals(1, boardPair.getGameBoard().currentNumber());
            assertEquals(0, boardPair.getGameBoard().restNumber());
            assertEquals(GameResolution.PLAYER2_WIN, boardPair.getGameBoard().resolution());
            assertEquals(expectedMessages, boardPair.getMessages());
        }
    }

    @Nested
    class WhenGameEndsInDraw {
        @BeforeEach
        void setUp() {
            underTest.handlePlayerConnect("player1");
            underTest.handlePlayerConnect("player2");
        }

        @ParameterizedTest
        @MethodSource("drawCasesProvider")
        void shouldReportDraw(Integer initialNumber, Integer move, Integer expectedNumber, LinkedList<String> messages) {
            underTest.handleStartGame(initialNumber, Player.of("player2"));
            var boardPair = underTest.handlePlayerMove(move);

            assertEquals(Player.PLAYER1, boardPair.getGameBoard().playerTurn());
            assertEquals(expectedNumber, boardPair.getGameBoard().currentNumber());
            assertNotEquals(0, boardPair.getGameBoard().restNumber());
            assertEquals(GameResolution.DRAW, boardPair.getGameBoard().resolution());
            assertEquals(messages, boardPair.getMessages());
        }

        private static Stream<Arguments> drawCasesProvider() {
            return Stream.of(
                    Arguments.of(4, 0, 1, new LinkedList<>(List.of("player1 has made a move. Previous number: 4, added number: 0, resulting number: 1. ( 4 + 0 ) / 3 = 1", "Game over. draw has emerged victorious."))),
                    Arguments.of(5, 0, 1, new LinkedList<>(List.of("player1 has made a move. Previous number: 5, added number: 0, resulting number: 1. ( 5 + 0 ) / 3 = 1", "Game over. draw has emerged victorious."))),
                    Arguments.of(3, 1, 1, new LinkedList<>(List.of("player1 has made a move. Previous number: 3, added number: 1, resulting number: 1. ( 3 + 1 ) / 3 = 1", "Game over. draw has emerged victorious."))),
                    Arguments.of(4, 1, 1, new LinkedList<>(List.of("player1 has made a move. Previous number: 4, added number: 1, resulting number: 1. ( 4 + 1 ) / 3 = 1", "Game over. draw has emerged victorious."))),
                    Arguments.of(5, -1, 1, new LinkedList<>(List.of("player1 has made a move. Previous number: 5, added number: -1, resulting number: 1. ( 5 + -1 ) / 3 = 1", "Game over. draw has emerged victorious."))),
                    Arguments.of(3, -1, 0, new LinkedList<>(List.of("player1 has made a move. Previous number: 3, added number: -1, resulting number: 0. ( 3 + -1 ) / 3 = 0", "Game over. draw has emerged victorious."))),
                    Arguments.of(6, -1, 1, new LinkedList<>(List.of("player1 has made a move. Previous number: 6, added number: -1, resulting number: 1. ( 6 + -1 ) / 3 = 1", "Game over. draw has emerged victorious.")))
            );
        }
    }

    @Nested
    class WhenGameIsOnGoing {

        @BeforeEach
        void setUp() {
            underTest.handlePlayerConnect("player1");
            underTest.handlePlayerConnect("player2");
        }

        @ParameterizedTest
        @MethodSource("onGoingCasesProvider")
        void shouldProcessMoveEvent(Integer initialNumber, Integer move, Integer expectedNumber, String expectedMessage) {
            underTest.handleStartGame(initialNumber, Player.of("player2"));
            var boardPair = underTest.handlePlayerMove(move);

            assertEquals(Player.PLAYER2, boardPair.getGameBoard().playerTurn());
            assertEquals(expectedNumber, boardPair.getGameBoard().currentNumber());
            assertEquals(0, boardPair.getGameBoard().restNumber());
            assertEquals(GameResolution.NOT_RESOLVED, boardPair.getGameBoard().resolution());
            assertEquals(expectedMessage, boardPair.getMessages().remove());
        }

        private static Stream<Arguments> onGoingCasesProvider() {
            return Stream.of(
                    Arguments.of(15, 0, 5, "player1 has made a move. Previous number: 15, added number: 0, resulting number: 5. ( 15 + 0 ) / 3 = 5"),
                    Arguments.of(16, -1, 5, "player1 has made a move. Previous number: 16, added number: -1, resulting number: 5. ( 16 + -1 ) / 3 = 5"),
                    Arguments.of(14, 1, 5, "player1 has made a move. Previous number: 14, added number: 1, resulting number: 5. ( 14 + 1 ) / 3 = 5")
            );
        }
    }
}