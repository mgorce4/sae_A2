package iut.unilim.fr.back.users;

public class Administration extends User {

    public Administration(String firstName, String lastName, String connexionId, String password, Institution institution) {
        super(firstName, lastName, connexionId, password, institution);
        this.status = 2;
    }

    public void giveTask(Task task, User user) {
        user.tasks.add(task);
    }
}
