package victorvld.gameofthree.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import victorvld.gameofthree.controller.dto.MoveEvent;
import victorvld.gameofthree.controller.dto.GameBoard;
import victorvld.gameofthree.controller.dto.GameMessage;
import victorvld.gameofthree.controller.dto.StartGameEvent;
import victorvld.gameofthree.controller.exceptions.PlayerConnectionException;
import victorvld.gameofthree.controller.exceptions.StartGameException;
import victorvld.gameofthree.domain.entities.GameState;
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

    public GameMessage handleConnectEvent(String playerId) {
        if (gameState.areBothPlayerConnected()) {
            throw new PlayerConnectionException("Connection Error: Game is full");
        } else if (gameState.containsPlayer(playerId)) {
            throw new PlayerConnectionException("Connection Error: %s is already connected".formatted(playerId));
        } else {
            this.gameState.add(Player.of(playerId));
            this.logger.info("{} connected to the game", playerId);
            return new GameMessage("%s connected to the game".formatted(playerId));
        }
    }

    private GameBoard processStartGameEvent(StartGameEvent event) {
        this.gameState.apply(event);
        var message = "Game started by %s on mode %s. Initial number = %s".formatted(event.startingPlayer(),
                event.mode(), event.initialNumber());
        this.logger.info(message);
        this.messagingTemplate.convertAndSend(TOPIC_MESSAGES, new GameMessage(message));
        return this.gameState.generateBoardSnapshot();
    }

    public GameBoard handleMoveEvent(MoveEvent event) {
        var beforeSnap = this.gameState.generateBoardSnapshot();
        this.gameState.apply(event.move());
        var afterSnap = this.gameState.generateBoardSnapshot();
        this.handleReporting("Move made by %s. Previous number=%s, added number=%s, resulting number=%s"
                .formatted(beforeSnap.playerTurn(), beforeSnap.currentNumber(), event.move(), afterSnap.currentNumber()));
        if (gameState.isGameFinished() && afterSnap.currentNumber() == 1 && afterSnap.restNumber() == 0) {
            gameState.reset();
            this.handleReporting("Game finished. %s won the game".formatted(beforeSnap.playerTurn()));
        } else if (gameState.isGameFinished() && afterSnap.currentNumber() < 2) {
            gameState.reset();
            this.handleReporting("The game ends in a draw. No player has been able to win the game");
        }
        return afterSnap;
    }

    private void handleReporting(String message) {
        this.logger.info(message);
        this.messagingTemplate.convertAndSend(TOPIC_MESSAGES, new GameMessage(message));
    }
}
