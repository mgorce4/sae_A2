package iut.unilim.fr.back.users;

public class Teacher extends User {

    //waiting for Ressource class
    //private List<Ressource> ressources;

    protected Teacher(String firstName, String lastName, String connexionId, String password, Institution institution) {
        super(firstName, lastName, connexionId, password, institution);
        this.status = 1;
        //this.ressources = new ArrayList<Ressource>();
    }
}
