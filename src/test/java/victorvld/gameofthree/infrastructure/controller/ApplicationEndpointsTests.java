package victorvld.gameofthree.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import victorvld.gameofthree.core.domain_models.GameBoard;
import victorvld.gameofthree.infrastructure.controller.doubles.MessageChannelMock;
import victorvld.gameofthree.infrastructure.controller.doubles.PrincipalMock;
import victorvld.gameofthree.core.domain_models.GameBoardPair;
import victorvld.gameofthree.core.domain_models.GameResolution;
import victorvld.gameofthree.core.domain_models.Player;
import victorvld.gameofthree.infrastructure.controller.dto.GameBoardDto;
import victorvld.gameofthree.infrastructure.controller.dto.GameMessage;
import victorvld.gameofthree.infrastructure.controller.dto.MoveEvent;
import victorvld.gameofthree.infrastructure.controller.dto.StartGameEvent;
import victorvld.gameofthree.core.services.GameEngine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationEndpointsTests {
    private final GameEngine gameEngine = Mockito.mock(GameEngine.class);
    private final NotificationSystem notificationSystem = Mockito.mock(NotificationSystem.class);
    private final MessageChannelMock clientInboundChannel = new MessageChannelMock();
    private MessageChannelMock clientOutboundChannel;
    private TestAnnotationMethodHandler annotationMethodHandler;

    @BeforeEach
    public void setup() {
        this.clientOutboundChannel = new MessageChannelMock();
        this.annotationMethodHandler = new TestAnnotationMethodHandler(
                this.clientOutboundChannel, clientInboundChannel,
                new SimpMessagingTemplate(this.clientOutboundChannel));
        this.annotationMethodHandler.registerHandler(new GameOfThreeController(this.gameEngine, this.notificationSystem));
        this.annotationMethodHandler.setDestinationPrefixes(List.of("/app", "/topic"));
        this.annotationMethodHandler.setMessageConverter(new MappingJackson2MessageConverter());
        this.annotationMethodHandler.setApplicationContext(new StaticApplicationContext());
        this.annotationMethodHandler.afterPropertiesSet();
    }

    @Test
    void connectEndpointTest() throws Exception {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
        headers.setDestination("/app/connect/player1");
        headers.setSessionId("0");
        headers.setUser(new PrincipalMock("user"));
        headers.setSessionAttributes(new HashMap<>());
        byte[] payload = new byte[0];
        Message<byte[]> message = MessageBuilder.withPayload(payload).setHeaders(headers).build();
        when(gameEngine.handlePlayerConnect("player1"))
                .thenReturn("Connected to player1");

        this.annotationMethodHandler.handleMessage(message);

        verify(gameEngine, times(1)).handlePlayerConnect("player1");
        List<Message<?>> messages = this.clientOutboundChannel.getMessages();
        assertEquals(1, messages.size());
        Message<?> reply = messages.get(0);
        assertEquals("/topic/messages", reply.getHeaders().get("simpDestination"));
        assertEquals("Connected to player1", ((GameMessage) reply.getPayload()).content());
    }

    @Test
    void startEndpointTest() throws Exception {
        StartGameEvent request = new StartGameEvent("player1", 20);
        GameBoard serviceResponse = new GameBoard(20, 0, Player.of("player1"), GameResolution.NOT_RESOLVED);
        GameBoardDto response = new GameBoardDto(20, 0, "player1", "not_resolved");
        byte[] payload = new ObjectMapper().writeValueAsBytes(request);
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
        headers.setDestination("/app/start");
        headers.setSessionId("0");
        headers.setUser(new PrincipalMock("user"));
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(payload).setHeaders(headers).build();
        when(gameEngine.handleStartGame(20, Player.of("player1"))).thenReturn(serviceResponse);

        this.annotationMethodHandler.handleMessage(message);

        verify(gameEngine, times(1)).handleStartGame(20, Player.of("player1"));
        List<Message<?>> messages = this.clientOutboundChannel.getMessages();
        assertEquals(1, messages.size());
        Message<?> reply = messages.get(0);
        assertEquals("/topic/game", reply.getHeaders().get("simpDestination"));
        assertEquals(response, reply.getPayload());
    }

    @Test
    void moveEndpointTest() throws Exception {
        MoveEvent request = new MoveEvent(5);
        GameBoard serviceResponse = new GameBoard(20, 0, Player.of("player1"), GameResolution.NOT_RESOLVED);
        GameBoardDto response = new GameBoardDto(20, 0, "player1", "not_resolved");
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
        headers.setDestination("/app/move");
        headers.setSessionId("0");
        headers.setUser(new PrincipalMock("user"));
        headers.setSessionAttributes(new HashMap<>());
        byte[] payload = new ObjectMapper().writeValueAsBytes(request);
        Message<byte[]> message = MessageBuilder.withPayload(payload).setHeaders(headers).build();
        when(gameEngine.handlePlayerMove(5)).thenReturn(new GameBoardPair(serviceResponse, new LinkedList<>()));

        this.annotationMethodHandler.handleMessage(message);

        verify(gameEngine, times(1)).handlePlayerMove(5);
        List<Message<?>> messages = this.clientOutboundChannel.getMessages();
        assertEquals(1, messages.size());
        Message<?> reply = messages.get(0);
        assertEquals("/topic/game", reply.getHeaders().get("simpDestination"));
        assertEquals(response, reply.getPayload());
    }

    /**
     * An extension of {@link SimpAnnotationMethodMessageHandler} that exposes a (public)
     * method for manually registering a controller, rather than having it
     * auto-discovered in the Spring ApplicationContext.
     */
    private static class TestAnnotationMethodHandler extends SimpAnnotationMethodMessageHandler {

        public TestAnnotationMethodHandler(SubscribableChannel inChannel, MessageChannel outChannel,
                                           SimpMessageSendingOperations brokerTemplate) {

            super(inChannel, outChannel, brokerTemplate);
        }

        public void registerHandler(Object handler) {
            super.detectHandlerMethods(handler);
        }
    }
}
