package iut.unilim.fr.back.dto.admin;

import java.util.List;

public class MCCCPathDataDTO {

    private List<MCCCSaeDTO> saes;
    private List<MCCCUEDTO> ues;
    private List<MCCCResourceDTO> resources;

    public MCCCPathDataDTO() {
    }

    public MCCCPathDataDTO(List<MCCCSaeDTO> saes, List<MCCCUEDTO> ues, List<MCCCResourceDTO> resources) {
        this.saes = saes;
        this.ues = ues;
        this.resources = resources;
    }

    public List<MCCCSaeDTO> getSaes() {
        return saes;
    }

    public void setSaes(List<MCCCSaeDTO> saes) {
        this.saes = saes;
    }

    public List<MCCCUEDTO> getUes() {
        return ues;
    }

    public void setUes(List<MCCCUEDTO> ues) {
        this.ues = ues;
    }

    public List<MCCCResourceDTO> getResources() {
        return resources;
    }

    public void setResources(List<MCCCResourceDTO> resources) {
        this.resources = resources;
    }
}
