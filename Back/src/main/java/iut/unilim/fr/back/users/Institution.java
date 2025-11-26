package iut.unilim.fr.back.users;

import java.util.Objects;

public record Institution(String name, String location) {

    public Institution {
        Objects.requireNonNull(name, "Name must be non-null");
        Objects.requireNonNull(location, "The location must be non-null.");
    }

    //getters are fluent
}
