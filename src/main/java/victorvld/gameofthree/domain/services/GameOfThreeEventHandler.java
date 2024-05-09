package victorvld.gameofthree.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import victorvld.gameofthree.controller.dto.GameBoard;
import victorvld.gameofthree.controller.dto.Message;
import victorvld.gameofthree.controller.dto.StartGameEvent;
import victorvld.gameofthree.controller.exceptions.PlayerConnectionException;
import victorvld.gameofthree.domain.entities.GameState;
import victorvld.gameofthree.domain.entities.GameStatus;
import victorvld.gameofthree.domain.entities.Player;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GameOfThreeEventHandler {
    private final Logger logger = LoggerFactory.getLogger(GameOfThreeEventHandler.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final GameState gameState;

    private final Set<Player> connectedPlayers = ConcurrentHashMap.newKeySet();

    public GameOfThreeEventHandler(SimpMessagingTemplate messagingTemplate, GameState gameState) {
        this.messagingTemplate = messagingTemplate;
        this.gameState = gameState;
    }

    public GameBoard handleStartGameEvent(StartGameEvent event) {
        if (!gameState.areBothPlayerConnected()) {
            // TODO: 09/05/2024 create my own exception and catch it in the controller and report it to the client
            throw new IllegalStateException("Not enough players to start the game");
        } else if (gameState != null && gameState.isGameStarted()) {
            // TODO: 09/05/2024 create my own exception and catch it in the controller and report it to the client
            throw new IllegalStateException("Game is already started");
        } else {
            return processStartGameEvent(event);
        }
    }

    public Message handleConnectEvent(String playerId) {
        if (this.connectedPlayers.size() == 2) {
            throw new PlayerConnectionException("Connection Error: Game is full");
        } else if (this.connectedPlayers.contains(Player.of(playerId))) {
            throw new PlayerConnectionException("Connection Error: %s is already connected".formatted(playerId));
        } else {
            connectedPlayers.add(Player.of(playerId));
            logger.info("{} connected to the game", playerId);
            return new Message("%s connected to the game".formatted(playerId));
        }
    }

    private GameBoard processStartGameEvent(StartGameEvent event) {
        var startingPlayer = Player.of(event.startingPlayer());
        var receivingPlayer = startingPlayer.opposite();
        // TODO think about it later, better to not use the setter at all.
        //  Hint: separate playersConnected from the game state.
        gameState.setLastMove("No move has been made yet");
        gameState.setCurrentTurn(receivingPlayer);
        gameState.setStartingPlayer(startingPlayer);
        gameState.setReceivingPlayer(receivingPlayer);
        gameState.setStatus(GameStatus.STARTED);
        // send message to the other player to move
        var message = "Game started by %s on mode %s".formatted(event.startingPlayer(), event.mode());
        logger.info(message);
        messagingTemplate.convertAndSend("/topic/messages/" + event.startingPlayer(), message);
        return gameState.generateBoardSnapshot();
    }
}
