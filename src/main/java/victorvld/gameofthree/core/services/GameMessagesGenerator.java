package victorvld.gameofthree.core.services;

public class GameMessagesGenerator {
    public static final String CONNECTED_MESSAGE = "%s has joined the game.";
    public static final String GAME_STARTED_BY_ON_MODE_AND_NUMBER = "%s initiated the game. Initial number: %s";
    public static final String DRAW_MESSAGE = "The game concludes in a draw. Neither player has achieved victory.";
    public static final String WIN_MESSAGE = "Game over. %s has emerged victorious.";
    public static final String MOVE_MESSAGE = "%s has made a move. Previous number: %s, added number: %s, resulting number: %s. ( %s + %s ) / 3 = %s";
    public static final String ERROR_MSG_NOT_ENOUGH_PLAYERS = "Error: Insufficient players to commence the game.";
    public static final String ERROR_MSG_GAME_IS_ALREADY_STARTED = "Error: The game has already begun.";
    public static final String ERROR_MSG_ALREADY_CONNECTED = "Connection Error: %s is already connected.";
    public static final String ERROR_MSG_GAME_IS_FULL = "Connection Error: The game is already at maximum capacity.";

    private GameMessagesGenerator() {
        // Prevent instantiation
    }

    public static String formatConnectMessage(String playerId) {
        return CONNECTED_MESSAGE.formatted(playerId);
    }

    public static String formatStartMessage(String startingPlayer, Integer initialNumber) {
        return GAME_STARTED_BY_ON_MODE_AND_NUMBER.formatted(startingPlayer, initialNumber);
    }

    public static String formatMoveMessage(String player, Integer previousNumber, Integer addingNumber, Integer resultingNumber) {
        return MOVE_MESSAGE.formatted(player, previousNumber, addingNumber, resultingNumber, previousNumber, addingNumber, resultingNumber);
    }

    public static String generateResolutionMessage(String winner) {
        if (winner == null) {
            return DRAW_MESSAGE;
        } else {
            return WIN_MESSAGE.formatted(winner);
        }
    }

    public static String formatPlayerAlreadyConnectedError(String playerId) {
        return ERROR_MSG_ALREADY_CONNECTED.formatted(playerId);
    }

    public static String generateGameIsFullErrorMessage() {
        return ERROR_MSG_GAME_IS_FULL;
    }

    public static String generateNotEnoughPlayersErrorMessage() {
        return ERROR_MSG_NOT_ENOUGH_PLAYERS;
    }

    public static String generateGameAlreadyStartedErrorMessage() {
        return ERROR_MSG_GAME_IS_ALREADY_STARTED;
    }
}
