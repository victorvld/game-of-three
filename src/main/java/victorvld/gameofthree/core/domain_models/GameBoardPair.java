package victorvld.gameofthree.core.domain_models;

import java.util.Queue;

public class GameBoardPair {

    private final GameBoard gameBoard;
    private final Queue<String> messages;

    public GameBoardPair(GameBoard gameBoard, Queue<String> messageQueue) {
        this.gameBoard = gameBoard;
        this.messages = messageQueue;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public Queue<String> getMessages() {
        return messages;
    }
}
