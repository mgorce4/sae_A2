package iut.unilim.fr.back.Ressource;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class RessourceSheet {
    Ressource ressource;

    private String name;
    private List<String> competence;
    //private List<SAE> saes;

    public static class RessourceTracking {
        private String pedagoFeedback;
        private String studentFeedback;
        private String improvement;
    }

    public static class PedagoContent{
        private int TD;
        private int TP;
        private int CM;
    }



}
