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
            resultResourceSheet = ressourceSheetRepository.findFirstByRessource_IdRessource(resultResource.get().getIdRessource());
        }

        if (resultResource.isPresent() && resultResourceSheet.isPresent()) {
            Ressource resource = resultResource.get();
            RessourceSheet resourceSheet = resultResourceSheet.get();
            HoursPerStudent hoursPerStudent = resource.getHoursPerStudent();

            id = resource.getIdRessource();
            label = resource.getLabel();

            writeInLog("Get ressource \n"
                    + "id : " + id + "\n"
                    + "label : " + label + "\n");

            ref = ressourceName;
            String resC = resource.getLabel().split(" ")[0];
            nResource = resC.split("R")[1];// TODO : Changer pour la reference de la ressource ( 1.10... ) -> Should work
            Boolean isMultiCompetences = resource.getDiffMultiCompetences();
            if (!isMultiCompetences) {
                UeCoefficient ueCoefficient = resource.getUeCoefficient();
                UE ue = ueCoefficient.getUe();
                String labelUe = ue.getLabel().split(" ")[0];
                refUE = labelUe;

                // TODO: PN -> competences.add();
            } else {
                //todo : multi comp
            }
            UserSyncadia userReferent = resourceSheet.getUser();
            profRef = userReferent.getFirstname() + " " + userReferent.getLastname();
            labelResource = resource.getLabel();

            String[] ressourceSheetSaes = resourceSheet.getSae().split(",");
            saes.clear();
            saes.addAll(Arrays.asList(ressourceSheetSaes));
            // TODO: PN -> keyWord

            Terms terms = resource.getTerms();
            modalities.clear();
            modalities.add(terms.getCode());

            Integer hoursCM = hoursPerStudent.getCm();
            Integer hoursTd  = hoursPerStudent.getTd();
            Integer hoursTp  = hoursPerStudent.getTp();
            hoursStudent.clear();
            hoursStudent.add(hoursCM);
            hoursStudent.add(hoursTd);
            hoursStudent.add(hoursTp);
            hoursStudent.add(hoursTp + hoursTd +  hoursCM);

            PedagogicalContent pedagoContent = resourceSheet.getPedagogicalContent();
            pedagoContentCm = pedagoContent.getCm();
            pedagoContentTd = pedagoContent.getTd();
            pedagoContentTp = pedagoContent.getTp();

            RessourceTracking ressourceTracking = resourceSheet.getRessourceTracking();
            studentFeedback = ressourceTracking.getStudentFeedback();
            pedagoTeamFeedback = ressourceTracking.getPedagogicalFeedback();
            improvements = ressourceTracking.getImprovementSuggestions();

            writeInLog("Get from database :\n"
                + "Ressource [" + labelResource + "; " + nResource + "; " + refUE + "; " + profRef + "; " + "], saes[ " + Arrays.toString(ressourceSheetSaes) + "], terms[ " + modalities.toString() + "] hoursStudent[ " + hoursStudent.toString() +"], pedagoContent[" + pedagoContentCm + "; " + pedagoContentTd + "; " + pedagoContentTp + "], feedBack[" + studentFeedback + "; " +pedagoTeamFeedback + "; " + improvements+ "]\n");
        } else {
            writeInLog("Rien");
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
