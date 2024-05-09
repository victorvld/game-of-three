package victorvld.gameofthree.controller.dto;

public record StartGameEvent(
        String startingPlayer,
        Integer initialNumber,
        String mode) {
}
