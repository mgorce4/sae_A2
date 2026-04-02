package iut.unilim.fr.back.users;

import iut.unilim.fr.back.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends User {

    protected Teacher(String firstName, String lastName, String connexionId, String password, Institution institution) {
        super(firstName, lastName, connexionId, password, institution);
        this.status = 1;
        List<Resource> resources = new ArrayList<>();
    }
}
