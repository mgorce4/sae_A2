package iut.unilim.fr.back.Ressource;

import java.util.ArrayList;

public class MCCC {
    private ArrayList<Ressource> ressources;
    private ArrayList<UE> UEs;
    private ArrayList<SAE> SAEs;

    public MCCC() {
        ressources = new ArrayList<>();
        UEs = new ArrayList<>();
        SAEs = new ArrayList<>();
    }

    public ArrayList<Ressource> getRessource() {
        return ressources;
    }
    public ArrayList<UE> getUE() {
        return UEs;
    }
    public ArrayList<SAE> getSAE() {
        return SAEs;
    }

    public void addRessource(Ressource ressource) {
        ressources.add(ressource);
    }
    public void addUE(UE ue) {
        UEs.add(ue);
    }
    public void addSAE(SAE sae) {
        SAEs.add(sae);
    }
}
