package victorvld.gameofthree.infrastructure.controller.doubles;

import java.security.Principal;

public record PrincipalMock(String name) implements Principal {
    @Override
    public String getName() {
        return this.name;
    }
}
