package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.*;
import iut.unilim.fr.back.repository.HoursPerStudentRepository;
import iut.unilim.fr.back.repository.RessourceRepository;
import iut.unilim.fr.back.repository.RessourceSheetRepository;
import iut.unilim.fr.back.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static iut.unilim.fr.back.controllerBack.LogController.writeInLog;

@Service
public class ResourceGetterService {
    @Autowired
    private RessourceRepository ressourceRepository;
    @Autowired
    private RessourceSheetRepository  ressourceSheetRepository;

    private String fileName = "";

    private String ref;
    private String nResource;
    private String refUE;
    private String profRef;
    private String labelResource;

    private String objectiveContent;
    private List<String> competences;

    private List<String> saes;
    private String keyWords;
    private List<String> modalities;

    private List<Integer> hoursPN;
    private List<Integer> hoursStudent;

    private String pedagoContentCm;
    private String pedagoContentTd;
    private String pedagoContentTp;
    private String pedagoContentDs;

    private String pedagoTeamFeedback;
    private String studentFeedback;
    private String improvements;

    public ResourceGetterService() {
        competences = new ArrayList<>();
        saes = new ArrayList<>();
        modalities = new ArrayList<>();

        hoursPN = new ArrayList<>();
        hoursStudent = new ArrayList<>();

        initializePlaceHolderValues();
    }

    private void initializePlaceHolderValues() {
        String PLACEHOLDER = "No resource for that category";
        String PLACEHOLDER_TITLE = "None";
        competences.add(PLACEHOLDER);
        saes.add(PLACEHOLDER);
        modalities.add(PLACEHOLDER);

        for (int i=0; i<4; i++) {
            hoursPN.add(-1);
            hoursStudent.add(-1);
        }

        ref = PLACEHOLDER_TITLE;
        nResource = PLACEHOLDER_TITLE;
        refUE = PLACEHOLDER_TITLE;
        profRef = PLACEHOLDER_TITLE;
        labelResource = PLACEHOLDER_TITLE;
        objectiveContent = PLACEHOLDER;
        keyWords = PLACEHOLDER;
        pedagoContentCm = PLACEHOLDER;
        pedagoContentTd = PLACEHOLDER;
        pedagoContentTp = PLACEHOLDER;

        pedagoTeamFeedback = PLACEHOLDER;
        studentFeedback = PLACEHOLDER;
        improvements = PLACEHOLDER;
    }

    @Transactional
    public void setValuesFromRessource(String ressourceName) {
        Optional<RessourceSheet> resultResourceSheet = null;
        Long id;
        String label;

        Optional<Ressource> resultResource = ressourceRepository.findFirstByLabelStartingWith(ressourceName + " ");

        if (resultResource.isPresent()) {
            resultResourceSheet = ressourceSheetRepository.findFirstByResource_IdResource(resultResource.get().getIdResource());
        } else {writeInLog("Could not get the resource sheet from the database because there is no resource with id = " + ressourceName);}

        if (resultResource.isPresent() && resultResourceSheet.isPresent()) {
            fileName = ressourceName;
            Ressource resource = resultResource.get();
            RessourceSheet resourceSheet = resultResourceSheet.get();

            id = resource.getIdResource();
            label = resource.getLabel();

            writeInLog("Get ressource \n"
                    + "id : " + id + "\n"
                    + "label : " + label + "\n");

            ref = ressourceName;
            String resC = resource.getLabel().split(" ")[0];
            nResource = resC.split("R")[1];// TODO : Changer pour la reference de la ressource ( 1.10... ) -> Should work
            Boolean isMultiCompetences = resource.getDiffMultiCompetences();
            if (!isMultiCompetences) {
                // TODO: Handle UE coefficients
                refUE = ""; // Temporarily empty
                // TODO: PN -> competences.add();
            } else {
                //todo : multi comp
            }
            
            // User and SAE info removed as they're not in the new schema
            profRef = ""; // TODO: Get from MAIN_TEACHER_FOR_RESOURCE or TEACHERS_FOR_RESOURCE
            labelResource = resource.getLabel();

            saes.clear();
            // TODO: Get SAEs from SAE_LINK_RESOURCE table
            
            // TODO: PN -> keyWord

            Terms terms = resource.getTerms();
            modalities.clear();
            modalities.add(terms.getCode());

            // Hours info removed as it's not directly in Ressource anymore
            hoursStudent.clear();
            hoursStudent.add(0); // TODO: Get from HOURS_PER_STUDENT
            hoursStudent.add(0);
            hoursStudent.add(0);
            hoursStudent.add(0);

            // PedagogicalContent and RessourceTracking removed as they're not directly linked
            pedagoContentCm = ""; // TODO: Get from PEDAGOGICAL_CONTENT
            pedagoContentTd = "";
            pedagoContentTp = "";
            pedagoContentDs = "";

            studentFeedback = ""; // TODO: Get from RESOURCE_TRACKING
            pedagoTeamFeedback = "";
            improvements = "";

            writeInLog("Get from database :\n"
                + "- Ressource (" + labelResource + "; " + nResource + "; " + refUE + "; " + profRef + ";" + ")\n- saes(TBD)\n- terms(" + modalities.toString() + ")\n-  hoursStudent(" + hoursStudent.toString() +")\n- pedagoContent(TBD)\n- feedBack(TBD)\n");
        } else {
            writeInLog("Attempt to get from database with resource name: " + ressourceName +
                    "\n-> " + ressourceName + " not found in resources tables");
        }
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
    public String getPedagoContentDs(){
        return pedagoContentDs;
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

    public String getFileName() {
        return fileName;
    }

}
