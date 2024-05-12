package victorvld.gameofthree.infrastructure.controller.dto;

public record StartGameEvent(
        String startingPlayer,
        int initialNumber) {
}
