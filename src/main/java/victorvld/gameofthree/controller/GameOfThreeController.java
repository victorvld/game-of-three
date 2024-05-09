package victorvld.gameofthree.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import victorvld.gameofthree.controller.dto.GameBoard;
import victorvld.gameofthree.controller.dto.Message;
import victorvld.gameofthree.controller.dto.MoveEvent;
import victorvld.gameofthree.controller.dto.StartGameEvent;
import victorvld.gameofthree.domain.services.GameOfThreeEventHandler;

@Controller
public class GameOfThreeController {
    private final GameOfThreeEventHandler eventHandler;

    @Autowired
    public GameOfThreeController(GameOfThreeEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @MessageMapping("/connect/{playerId}")
    @SendTo("/topic/messages")
    public Message connect(@DestinationVariable String playerId) {
        return eventHandler.handleConnectEvent(playerId);
    }

    @MessageMapping("/start")
    @SendTo("/topic/game")
    public GameBoard start(StartGameEvent event) {
        return eventHandler.handleStartGameEvent(event);
    }

    @MessageMapping("/move")
    @SendTo("/topic/game")
    public GameBoard move(MoveEvent event) {
        return eventHandler.handleMoveEvent(event);
    }

}
