package victorvld.gameofthree.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import victorvld.gameofthree.controller.dto.MoveEvent;
import victorvld.gameofthree.controller.dto.GameBoard;
import victorvld.gameofthree.controller.dto.Message;
import victorvld.gameofthree.controller.dto.StartGameEvent;
import victorvld.gameofthree.controller.exceptions.PlayerConnectionException;
import victorvld.gameofthree.controller.exceptions.StartGameException;
import victorvld.gameofthree.domain.entities.GameState;
import victorvld.gameofthree.domain.entities.GameStatus;
import victorvld.gameofthree.domain.entities.Player;

public class GameOfThreeEventHandler {
    private static final String TOPIC_MESSAGES = "/topic/messages";
    private final Logger logger = LoggerFactory.getLogger(GameOfThreeEventHandler.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final GameState gameState;

    public GameOfThreeEventHandler(SimpMessagingTemplate messagingTemplate, GameState gameState) {
        this.messagingTemplate = messagingTemplate;
        this.gameState = gameState;
    }

    public GameBoard handleStartGameEvent(StartGameEvent event) {
        if (!gameState.areBothPlayerConnected()) {
            throw new StartGameException("Error: Not enough players to start the game");
        } else if (gameState.isGameStarted()) {
            throw new StartGameException("Error: Game is already started");
        } else {
            return processStartGameEvent(event);
        }
    }

    public Message handleConnectEvent(String playerId) {
        if (gameState.areBothPlayerConnected()) {
            throw new PlayerConnectionException("Connection Error: Game is full");
        } else if (gameState.containsPlayer(playerId)) {
            throw new PlayerConnectionException("Connection Error: %s is already connected".formatted(playerId));
        } else {
            this.gameState.add(Player.of(playerId));
            this.logger.info("{} connected to the game", playerId);
            return new Message("%s connected to the game".formatted(playerId));
        }
    }

    private GameBoard processStartGameEvent(StartGameEvent event) {
        var startingPlayer = Player.of(event.startingPlayer());
        var receivingPlayer = startingPlayer.opposite();
        this.gameState.setInitialNumber(event.initialNumber());
        this.gameState.setLastMove("No move has been made yet");
        this.gameState.setCurrentTurn(receivingPlayer);
        this.gameState.setStatus(GameStatus.STARTED);
        var message = "Game started by %s on mode %s. Initial number = %s".formatted(event.startingPlayer(), event.mode(), event.initialNumber());
        this.logger.info(message);
        this.messagingTemplate.convertAndSend(TOPIC_MESSAGES, new Message(message));
        return this.gameState.generateBoardSnapshot();
    }

    public GameBoard handleMoveEvent(MoveEvent event) {
        var beforeMoveSnapshot = this.gameState.generateBoardSnapshot();
        this.gameState.applyMove(event.move());
        var afterMoveSnapshot = this.gameState.generateBoardSnapshot();
        if (this.gameState.isGameFinished()) {
            gameState.resetToReadyToStart();
            var message = "Game finished. %s won the game".formatted(this.gameState.getWinner());
            this.logger.info(message);
            this.messagingTemplate.convertAndSend(TOPIC_MESSAGES, new Message(message));
        } else {
            var message = "Move made by %s. Previous number=%s, added number=%s, resulting number=%s".formatted(
                    beforeMoveSnapshot.playerTurn(),
                    beforeMoveSnapshot.currentNumber(),
                    event.move(),
                    afterMoveSnapshot.currentNumber()
            );
            this.messagingTemplate.convertAndSend(TOPIC_MESSAGES, new Message(message));
        }
        return afterMoveSnapshot;
    }
}
