package iut.unilim.fr.back.resource;

import java.util.List;
public class ResourceSheet {
    Resource resource;

    private String name;
    private List<String> competence;
    //private List<SAE> saes;

    public static class ResourceTracking {
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
