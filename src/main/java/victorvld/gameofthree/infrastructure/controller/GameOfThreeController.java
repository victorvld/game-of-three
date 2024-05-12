package victorvld.gameofthree.infrastructure.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import victorvld.gameofthree.core.domain_models.Player;
import victorvld.gameofthree.infrastructure.controller.dto.GameBoardDto;
import victorvld.gameofthree.infrastructure.controller.dto.GameMessage;
import victorvld.gameofthree.infrastructure.controller.dto.MoveEvent;
import victorvld.gameofthree.infrastructure.controller.dto.StartGameEvent;
import victorvld.gameofthree.core.services.GameEngine;
import victorvld.gameofthree.core.services.GameMessagesGenerator;
import victorvld.gameofthree.infrastructure.controller.mapper.BoardMapper;

@Controller
public class GameOfThreeController {
    private final Logger logger = LoggerFactory.getLogger(GameOfThreeController.class);
    private final GameEngine eventHandler;
    private final NotificationSystem notificationSystem;

    private final BoardMapper mapper;

    @Autowired
    public GameOfThreeController(GameEngine eventHandler, NotificationSystem notificationSystem) {
        this.eventHandler = eventHandler;
        this.notificationSystem = notificationSystem;
        this.mapper = new BoardMapper();
    }

    @MessageMapping("/connect/{playerId}")
    @SendTo("/topic/messages")
    public GameMessage connect(@DestinationVariable String playerId) {
        var result = eventHandler.handlePlayerConnect(playerId);
        this.logger.info(result);
        return new GameMessage(result);
    }

    @MessageMapping("/start")
    @SendTo("/topic/game")
    public GameBoardDto start(StartGameEvent event) {
        var message = GameMessagesGenerator.formatStartMessage(event.startingPlayer(), event.initialNumber());
        var board = this.mapper.toDto(eventHandler.handleStartGame(event.initialNumber(), Player.of(event.startingPlayer())));
        notificationSystem.notify(message);
        return board;
    }

    @MessageMapping("/move")
    @SendTo("/topic/game")
    public GameBoardDto move(MoveEvent event) {
        var result = eventHandler.handlePlayerMove(event.move());
        notificationSystem.notify(result.getMessages());
        return this.mapper.toDto(result.getGameBoard());
    }

}
