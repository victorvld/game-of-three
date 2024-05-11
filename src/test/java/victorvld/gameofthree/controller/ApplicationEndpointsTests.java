package victorvld.gameofthree.controller;

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
import victorvld.gameofthree.controller.doubles.MessageChannelMock;
import victorvld.gameofthree.controller.doubles.PrincipalMock;
import victorvld.gameofthree.controller.dto.GameBoard;
import victorvld.gameofthree.controller.dto.GameMessage;
import victorvld.gameofthree.controller.dto.MoveEvent;
import victorvld.gameofthree.controller.dto.StartGameEvent;
import victorvld.gameofthree.domain.services.GameOfThreeEventHandler;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationEndpointsTests {
    private final GameOfThreeEventHandler eventHandler = Mockito.mock(GameOfThreeEventHandler.class);
    private final MessageChannelMock clientInboundChannel = new MessageChannelMock();
    private MessageChannelMock clientOutboundChannel;
    private TestAnnotationMethodHandler annotationMethodHandler;

    @BeforeEach
    public void setup() {
        this.clientOutboundChannel = new MessageChannelMock();
        this.annotationMethodHandler = new TestAnnotationMethodHandler(
                this.clientOutboundChannel, clientInboundChannel,
                new SimpMessagingTemplate(this.clientOutboundChannel));
        this.annotationMethodHandler.registerHandler(new GameOfThreeController(this.eventHandler));
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
        when(eventHandler.handleConnectEvent("player1"))
                .thenReturn(new GameMessage("Connected to player1"));

        this.annotationMethodHandler.handleMessage(message);

        verify(eventHandler, times(1)).handleConnectEvent("player1");
        List<Message<?>> messages = this.clientOutboundChannel.getMessages();
        assertEquals(1, messages.size());
        Message<?> reply = messages.get(0);
        assertEquals("/topic/messages", reply.getHeaders().get("simpDestination"));
        assertEquals("Connected to player1", ((GameMessage) reply.getPayload()).content());
    }

    @Test
    void startEndpointTest() throws Exception {
        StartGameEvent request = new StartGameEvent("player1", 20, "manual");
        GameBoard response = new GameBoard(20, 0,"lastMove", "manual", "player1", "none");
        byte[] payload = new ObjectMapper().writeValueAsBytes(request);
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
        headers.setDestination("/app/start");
        headers.setSessionId("0");
        headers.setUser(new PrincipalMock("user"));
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(payload).setHeaders(headers).build();
        when(eventHandler.handleStartGameEvent(request)).thenReturn(response);

        this.annotationMethodHandler.handleMessage(message);

        verify(eventHandler, times(1)).handleStartGameEvent(request);
        List<Message<?>> messages = this.clientOutboundChannel.getMessages();
        assertEquals(1, messages.size());
        Message<?> reply = messages.get(0);
        assertEquals("/topic/game", reply.getHeaders().get("simpDestination"));
        assertEquals(response, reply.getPayload());
    }

    @Test
    void moveEndpointTest() throws Exception {
        MoveEvent request = new MoveEvent(5);
        GameBoard response = new GameBoard(20, 0,"lastMove", "manual", "player1", "none");
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
        headers.setDestination("/app/move");
        headers.setSessionId("0");
        headers.setUser(new PrincipalMock("user"));
        headers.setSessionAttributes(new HashMap<>());
        byte[] payload = new ObjectMapper().writeValueAsBytes(request);
        Message<byte[]> message = MessageBuilder.withPayload(payload).setHeaders(headers).build();
        when(eventHandler.handleMoveEvent(request)).thenReturn(response);

        this.annotationMethodHandler.handleMessage(message);

        verify(eventHandler, times(1)).handleMoveEvent(request);
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
