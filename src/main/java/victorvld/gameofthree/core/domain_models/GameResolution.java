package victorvld.gameofthree.core.domain_models;

public enum GameResolution {
    PLAYER1_WIN("player1"),
    PLAYER2_WIN("player2"),
    DRAW("draw"),
    NOT_RESOLVED("not_resolved"),
    NOT_INITIATED("not_initiated");

    private final String result;

    GameResolution(String name) {
        this.result = name;
    }

    public String getResult() {
        return result;
    }

    public static GameResolution of(String name) {
        for (GameResolution value : GameResolution.values()) {
            if (value.getResult().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
}
