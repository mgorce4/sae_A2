package iut.unilim.fr.back.resource;

import java.util.ArrayList;

public class MCCC {
    private ArrayList<Resource> resources;
    private ArrayList<UE> UEs;
    private ArrayList<SAE> SAEs;

    public MCCC() {
        resources = new ArrayList<>();
        UEs = new ArrayList<>();
        SAEs = new ArrayList<>();
    }

    public ArrayList<Resource> getResource() {
        return resources;
    }
    public ArrayList<UE> getUE() {
        return UEs;
    }
    public ArrayList<SAE> getSAE() {
        return SAEs;
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }
    public void addUE(UE ue) {
        UEs.add(ue);
    }
    public void addSAE(SAE sae) {
        SAEs.add(sae);
    }
}
