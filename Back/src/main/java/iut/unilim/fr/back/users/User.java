package iut.unilim.fr.back.users;

import java.util.ArrayList;
import java.util.List;

public abstract class User {

    protected String firstName;
    protected String lastName;

    protected int status;

    protected String connectionId;
    protected String password;

    protected List<Task> tasks;
    protected Institution institution;

    //Constructor without status for the children class
    protected User (String firstName, String lastName, String connexionId, String password, Institution institution) {
        this. firstName =  firstName;
        this.lastName = lastName;
        this.connectionId = connexionId;
        this.password = password;
        this.tasks = new ArrayList<>();
        this.institution = institution;
    }

    //getters and setters

    public String firstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String lastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int status() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String connectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Task> tasks() {
        return tasks;
    }

    public Institution institution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }
}
