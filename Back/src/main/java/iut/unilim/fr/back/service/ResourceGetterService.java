package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.entity.UE;
import iut.unilim.fr.back.entity.UeCoefficient;
import iut.unilim.fr.back.repository.RessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static iut.unilim.fr.back.controllerBack.LogController.writeInLog;

@Service
public class ResourceGetterService {
    @Autowired
    private RessourceRepository ressourceRepository;

    private String ref;
    private String nResource;
    private String refUE;
    private String profRef;
    private String labelResource;

    private String objectiveContent;
    private List<String> competences;
    private List<String> criticalLearning;

    private List<String> saes;
    private String keyWords;
    private List<String> modalities;

    private List<Integer> hoursPN;
    private List<Integer> hoursStudent;

    private String pedagoContentCm;
    private String pedagoContentTd;
    private String pedagoContentTp;

    private String pedagoTeamFeedback;
    private String studentFeedback;
    private String improvements;

    public ResourceGetterService() {
        competences = new ArrayList<>();
        criticalLearning = new ArrayList<>();
        saes = new ArrayList<>();
        modalities = new ArrayList<>();

        hoursPN = new ArrayList<>();
        hoursStudent = new ArrayList<>();

        initializePlaceHolderValues();
    }

    private void initializePlaceHolderValues() {
        String PLACEHOLDER = "placeholder";
        competences.add(PLACEHOLDER);
        criticalLearning.add(PLACEHOLDER);
        saes.add(PLACEHOLDER);
        modalities.add(PLACEHOLDER);

        for (int i=0; i<4; i++) {
            hoursPN.add(i);
            hoursStudent.add(i);
        }

        ref = PLACEHOLDER;
        nResource = PLACEHOLDER;
        refUE = PLACEHOLDER;
        profRef = PLACEHOLDER;
        labelResource = PLACEHOLDER;
        objectiveContent = PLACEHOLDER;
        keyWords = PLACEHOLDER;
        pedagoContentCm = PLACEHOLDER;
        pedagoContentTd = PLACEHOLDER;
        pedagoContentTp = PLACEHOLDER;

        pedagoTeamFeedback = PLACEHOLDER;
        studentFeedback = PLACEHOLDER;
        improvements = PLACEHOLDER;
    }

    public void setValuesFromRessource(String ressourceName) {
        Long id;
        String label;

        Optional<Ressource> resultat = ressourceRepository.findFirstByLabelStartingWith(ressourceName + " ");

        if (resultat.isPresent()) {
            Ressource resource = resultat.get();

            id = resource.getIdRessource();
            label = resource.getLabel();

            writeInLog("Get ressource \n"
                    + "id : " + id + "\n"
                    + "label : " + label + "\n");

            ref = ressourceName;
            nResource = resource.getLabel(); // TODO : Changer pour la reference de la ressource ( 1.10... )
            Boolean isMultiCompetences = resource.getDiffMultiCompetences();
            if (!isMultiCompetences) {
                UeCoefficient ueCoefficient = resource.getUeCoefficient();
                UE ue = ueCoefficient.getUe();
                String labelUe =  ue.getLabel().split(" ")[1];
                refUE = labelUe;

                System.out.println(labelUe);
            }else {
                //todo
            }

        }
        else {writeInLog("Rien");}
    }

    public String getRef() {
        return ref;
    }
    public String getNbRessource() {
        return nResource;
    }
    public String getRefUE() {
        return refUE;
    }
    public String getProfRef() {
        return profRef;
    }
    public String getLabelResource() {
        return labelResource;
    }
    public String getObjectiveContent() {
        return objectiveContent;
    }
    public List<String> getCompetences() {
        return competences;
    }
    public List<String> getCriticalLearning() {
        return criticalLearning;
    }
    public List<String> getSaes() {
        return saes;
    }
    public String getKeyWords() {
        return keyWords;
    }
    public List<String> getModalities() {
        return modalities;
    }
    public List<Integer> getHoursPN() {
        return hoursPN;
    }
    public List<Integer> getHoursStudent() {
        return hoursStudent;
    }
    public String getPedagoContentCm() {
        return pedagoContentCm;
    }
    public String getPedagoContentTd() {
        return pedagoContentTd;
    }
    public String getPedagoContentTp(){
        return pedagoContentTp;
    }
    public String getPedagoTeamFeedback() {
        return pedagoTeamFeedback;
    }
    public String getStudentFeedback() {
        return studentFeedback;
    }
    public String getImprovements() {
        return improvements;
    }

}
