package victorvld.gameofthree.core.services;

import victorvld.gameofthree.core.domain_models.GameBoard;
import victorvld.gameofthree.core.domain_models.GameBoardPair;
import victorvld.gameofthree.core.domain_models.GameState;
import victorvld.gameofthree.core.domain_models.Player;
import victorvld.gameofthree.core.exceptions.PlayerConnectionException;
import victorvld.gameofthree.core.exceptions.StartGameException;

import java.util.LinkedList;
import java.util.Queue;

public class GameEngine {
    private final GameState gameState;
    private final PlayerRegistry registry;

    public GameEngine(GameState gameState, PlayerRegistry registry) {
        this.gameState = gameState;
        this.registry = registry;
    }

    public GameBoard handleStartGame(Integer initialNumber, Player startingPlayer) {
        if (!registry.areBothPlayerConnected()) {
            throw new StartGameException(GameMessagesGenerator.generateNotEnoughPlayersErrorMessage());
        } else if (gameState.isGameStarted()) {
            throw new StartGameException(GameMessagesGenerator.generateGameAlreadyStartedErrorMessage());
        } else {
            return processStartGameEvent(initialNumber, startingPlayer);
        }
    }

    public String handlePlayerConnect(String playerId) {
        if (registry.areBothPlayerConnected()) {
            throw new PlayerConnectionException(GameMessagesGenerator.generateGameIsFullErrorMessage());
        } else if (registry.containsPlayer(playerId)) {
            throw new PlayerConnectionException(GameMessagesGenerator.formatPlayerAlreadyConnectedError(playerId));
        } else {
            return processPlayerConnectEvent(playerId);
        }
    }

    public GameBoardPair handlePlayerMove(Integer move) {
        GameBoard beforeSnap = this.gameState.generateBoardSnapshot();
        this.gameState.applyMove(move);
        GameBoard afterSnap = this.gameState.generateBoardSnapshot();
        Queue<String> messages = new LinkedList<>();
        messages.add(GameMessagesGenerator.formatMoveMessage(beforeSnap.playerTurn().getName(), beforeSnap.currentNumber(), move, afterSnap.currentNumber()));
        if (gameState.isGameOver()) {
            this.processGameOver(messages, afterSnap.resolution().getResult());
        }
        return new GameBoardPair(afterSnap, messages);
    }

    private String processPlayerConnectEvent(String playerId) {
        this.registry.add(Player.of(playerId));
        return GameMessagesGenerator.formatConnectMessage(playerId);
    }

    private GameBoard processStartGameEvent(Integer initialNumber, Player startingPlayer) {
        this.gameState.applyStart(initialNumber, startingPlayer);
        return this.gameState.generateBoardSnapshot();
    }

    private void processGameOver(Queue<String> messageQueue, String resolution) {
        this.resetGame();
        messageQueue.add(GameMessagesGenerator.generateResolutionMessage(resolution));
    }

    private void resetGame() {
        this.gameState.reset();
        this.registry.reset();
    }
}
