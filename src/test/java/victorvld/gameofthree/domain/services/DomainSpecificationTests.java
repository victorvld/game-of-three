package victorvld.gameofthree.domain.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import victorvld.gameofthree.controller.dto.MoveEvent;
import victorvld.gameofthree.controller.dto.StartGameEvent;
import victorvld.gameofthree.controller.exceptions.PlayerConnectionException;
import victorvld.gameofthree.controller.exceptions.StartGameException;
import victorvld.gameofthree.domain.entities.GameState;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DomainSpecificationTests {
    private GameOfThreeEventHandler underTest;
    private final SimpMessagingTemplate mockMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);

    @BeforeEach
    void setUp() {
        underTest = new GameOfThreeEventHandler(mockMessagingTemplate, new GameState());
    }

    @Nested
    class WhenGameFull {
        @BeforeEach
        void setUp() {
            underTest.handleConnectEvent("player1");
            underTest.handleConnectEvent("player2");
        }

        @Test
        void shouldThrowStartGameException() {
            var expectedMessage = "Connection Error: Game is full";

            var exception = assertThrows(PlayerConnectionException.class, () -> underTest.handleConnectEvent("player3"));

            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Nested
    class WhenPlayerAlreadyConnected {
        @BeforeEach
        void setUp() {
            underTest.handleConnectEvent("player1");
        }

        @Test
        void shouldThrowStartGameException() {
            var expectedMessage = "Connection Error: player1 is already connected";

            var exception = assertThrows(PlayerConnectionException.class, () -> underTest.handleConnectEvent("player1"));

            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Nested
    class WhenGameNotFull {
        @Test
        void shouldProcessConnectEvent() {
            var message = underTest.handleConnectEvent("player1");

            assertEquals("player1 connected to the game", message.content());
        }
    }

    @Nested
    class WhenNotEnoughPlayers {
        @BeforeEach
        void setUp() {
            underTest.handleConnectEvent("player1");
        }

        @Test
        void shouldThrowStartGameException() {
            var event = new StartGameEvent("player1", 1, "manual");
            var expectedMessage = "Error: Not enough players to start the game";

            var exception = assertThrows(StartGameException.class, () -> underTest.handleStartGameEvent(event));

            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Nested
    class WhenGameAlreadyStarted {
        @BeforeEach
        void setUp() {
            underTest.handleConnectEvent("player1");
            underTest.handleConnectEvent("player2");
            underTest.handleStartGameEvent(new StartGameEvent("player1", 76, "manual"));
        }

        @Test
        void shouldThrowStartGameException() {
            var event = new StartGameEvent("player2", 76, "manual");
            String expectedMessage = "Error: Game is already started";

            var exception = assertThrows(StartGameException.class, () -> underTest.handleStartGameEvent(event));

            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Nested
    class WhenGameCanStart {
        @BeforeEach
        void setUp() {
            underTest.handleConnectEvent("player1");
            underTest.handleConnectEvent("player2");
        }

        @Test
        void shouldProcessStartGameEvent() {
            var event = new StartGameEvent("player1", 76, "manual");

            var gameBoard = underTest.handleStartGameEvent(event);

            assertEquals("player2", gameBoard.playerTurn());
            assertEquals(76, gameBoard.currentNumber());
            assertEquals("manual", gameBoard.gameMode());
            assertEquals("No move has been made yet", gameBoard.lastMove());
            assertNull(gameBoard.restNumber());
            assertNull(gameBoard.winner());
        }
    }

    @Nested
    class WhenPlayerMakeWinningMove {
        @BeforeEach
        void setUp() {
            underTest.handleConnectEvent("player1");
            underTest.handleConnectEvent("player2");
            underTest.handleStartGameEvent(new StartGameEvent("player1", 3, "manual"));
        }

        @Test
        void shouldProcessMoveEvent() {
            var event = new MoveEvent(0);

            var gameBoard = underTest.handleMoveEvent(event);

            assertEquals("player2", gameBoard.playerTurn());
            assertEquals(1, gameBoard.currentNumber());
            assertEquals("manual", gameBoard.gameMode());
            assertEquals("player2 made a move of 0 and win the game", gameBoard.lastMove());
            assertEquals(0, gameBoard.restNumber());
            assertEquals("player2", gameBoard.winner());
        }
    }

    @Nested
    class WhenGameEndsInDraw {
        @BeforeEach
        void setUp() {
            underTest.handleConnectEvent("player1");
            underTest.handleConnectEvent("player2");
        }

        @ParameterizedTest
        @MethodSource("drawCasesProvider")
        void shouldReportDraw(Integer initialNumber, Integer move, Integer expectedNumber) {
            var event = new MoveEvent(move);

            underTest.handleStartGameEvent(new StartGameEvent("player2", initialNumber, "manual"));
            var gameBoard = underTest.handleMoveEvent(event);

            assertEquals("player1", gameBoard.playerTurn());
            assertEquals(expectedNumber, gameBoard.currentNumber());
            assertEquals("manual", gameBoard.gameMode());
            assertEquals("The game ends in a draw. No player has been able to win the game", gameBoard.lastMove());
            assertNotEquals(0, gameBoard.restNumber());
            assertEquals("draw", gameBoard.winner());
        }

        private static Stream<Arguments> drawCasesProvider() {
            return Stream.of(
                    Arguments.of(4, 0, 1),
                    Arguments.of(4, 0, 1),
                    Arguments.of(5, 0, 1),
                    Arguments.of(5, 0, 1),
                    Arguments.of(3, 1, 1),
                    Arguments.of(3, 1, 1),
                    Arguments.of(4, 1, 1),
                    Arguments.of(4, 1, 1),
                    Arguments.of(3, -1, 0),
                    Arguments.of(5, -1, 1),
                    Arguments.of(6, -1, 1)
            );
        }
    }

    @Nested
    class WhenGameIsOnGoing {

        @BeforeEach
        void setUp() {
            underTest.handleConnectEvent("player1");
            underTest.handleConnectEvent("player2");
        }
        @ParameterizedTest
        @MethodSource("onGoingCasesProvider")
        void shouldProcessMoveEvent(Integer initialNumber, Integer move, Integer expectedNumber) {
            var event = new MoveEvent(move);

            underTest.handleStartGameEvent(new StartGameEvent("player2", initialNumber, "manual"));
            var gameBoard = underTest.handleMoveEvent(event);

            assertEquals("player2", gameBoard.playerTurn());
            assertEquals(expectedNumber, gameBoard.currentNumber());
            assertEquals("manual", gameBoard.gameMode());
            assertEquals("player1 made a move of %s".formatted(move), gameBoard.lastMove());
            assertEquals(0, gameBoard.restNumber());
            assertNull(gameBoard.winner());
        }

        private static Stream<Arguments> onGoingCasesProvider() {
            return Stream.of(
                    Arguments.of(15, 0, 5),
                    Arguments.of(16, -1, 5),
                    Arguments.of(14, 1, 5)
            );
        }
    }
}