package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.*;
import iut.unilim.fr.back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static iut.unilim.fr.back.controllerBack.LogController.writeInLog;

@Service
public class ResourceGetterService {
    @Autowired
    private RessourceRepository ressourceRepository;
    @Autowired
    private RessourceSheetRepository  ressourceSheetRepository;
    @Autowired
    private SAELinkResourceRepository saeLinkResourceRepository;
    @Autowired
    private MainTeacherForResourceRepository mainTeacherForResourceRepository;
    @Autowired
    private UeCoefficientRepository ueCoefficientRepository;
    @Autowired
    private HoursPerStudentRepository hoursPerStudentRepository;
    @Autowired
    private PedagogicalContentRepository pedagogicalContentRepository;
    @Autowired
    private RessourceTrackingRepository resourceTrackingRepository;
    @Autowired
    private KeywordRepository keywordRepository;

    private String fileName = "";

    private String ref;
    private String nResource;
    private String refUE;
    private String profRef;
    private String labelResource;

    private String objectiveContent;
    private List<String> competences;

    private List<String> saes;
    private List<String> modalities;
    private List<String> keywords;

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
        keywords = new ArrayList<>();

        hoursPN = new ArrayList<>();
        hoursStudent = new ArrayList<>();

        initializePlaceHolderValues();
    }

    private void initializePlaceHolderValues() {
        String PLACEHOLDER = "No content for that category";
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
        keywords.add(PLACEHOLDER);
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

        Optional<Ressource> resultResource = ressourceRepository.findFirstByLabelStartingWith(ressourceName);

        if (resultResource.isPresent()) {
            resultResourceSheet = ressourceSheetRepository.findFirstByResource_IdResource(resultResource.get().getIdResource());
        } else {writeInLog("Could not get the resource sheet from the database because there is no resource with id = " + ressourceName);}

        if (resultResource.isPresent() && resultResourceSheet.isPresent()) {
            fileName = ressourceName;
            Ressource resource = resultResource.get();
            RessourceSheet resourceSheet = resultResourceSheet.get();

            id = resource.getIdResource();
            label = resource.getLabel();

            List<MainTeacherForResource> mainTeacherForResource = mainTeacherForResourceRepository.findByIdResource(id);
            List<SAELinkResource> SAELinkResources = saeLinkResourceRepository.findByIdResource(id);

            writeInLog("Get ressource \n"
                    + "id : " + id + "\n"
                    + "label : " + label + "\n");

            ref = ressourceName;
            String resC = resource.getLabel().split(" ")[0];
            nResource = resC.split("R")[1];// TODO : Changer pour la reference de la ressource ( 1.10... ) -> Should work
            Boolean isMultiCompetences = resource.getDiffMultiCompetences();
            if (!isMultiCompetences) {
                List<UeCoefficient> ueCoefficient = ueCoefficientRepository.findByResource_IdResource(id);
                UE ue = ueCoefficient.getFirst().getUe();
                refUE = ue.getLabel();
                // TODO: PN -> competences.add();
            } else {
                //todo : multi comp
            }
            
            // User and SAE info removed as they're not in the new schema
            UserSyncadia referent = mainTeacherForResource.getFirst().getUser();
            profRef = referent.getFirstname() + " " + referent.getLastname();
            labelResource = resource.getLabel() + ": " + resource.getName();

            saes.clear();
            for (SAELinkResource saeLinkResource : SAELinkResources) {
                SAE sae = saeLinkResource.getSae();
                saes.add(sae.getLabel());
            }

            List<Keyword> keyWordsList = keywordRepository.findByIdResourceSheet(resourceSheet.getIdResourceSheet());
            keywords.clear();
            for (Keyword keyword : keyWordsList) {
                keywords.add(keyword.getKeyword());
            }


            Terms terms = resource.getTerms();
            modalities.clear();
            modalities.add(terms.getCode());

            HoursPerStudent hoursPerStudent = hoursPerStudentRepository.findByResource_IdResource(id).getFirst();
            hoursStudent.clear();
            hoursStudent.add(hoursPerStudent.getCm());
            hoursStudent.add(hoursPerStudent.getTd());
            hoursStudent.add(hoursPerStudent.getTp());
            hoursStudent.add(hoursPerStudent.getCm() + hoursPerStudent.getTd() + hoursPerStudent.getTp());

            PedagogicalContent pedagogicalContent = pedagogicalContentRepository.findByResourceSheet_IdResourceSheet(resourceSheet.getIdResourceSheet()).getFirst();
            pedagoContentCm = pedagogicalContent.getCm();
            pedagoContentTd = pedagogicalContent.getTd();
            pedagoContentTp = pedagogicalContent.getTp();


            RessourceTracking ressourceTracking = resourceTrackingRepository.findByResourceSheet_IdResourceSheet(resourceSheet.getIdResourceSheet()).getFirst();
            studentFeedback = ressourceTracking.getStudentFeedback();
            pedagoTeamFeedback = ressourceTracking.getPedagogicalFeedback();
            improvements = ressourceTracking.getImprovementSuggestions();

            writeInLog("Get from database :\n"
                + "- Ressource (" + labelResource + "; " + nResource + "; " + refUE + "; " + profRef + ";" + ")\n" +
                    "- saes(" + saes + ")\n" +
                    "- terms(" + modalities.toString() + ")\n" +
                    "- keywords(" + keywords.toString() + ")\n" +
                    "- hoursStudent(" + hoursStudent.toString() +")\n" +
                    "- pedagoContent( CM: "+ pedagoContentCm + "; TD: " + pedagoContentTd + "; TP: " + pedagoContentTp + ")\n" +
                    "- feedBack(Student: " + studentFeedback + "; Pedagogical team: " + pedagoTeamFeedback + "; Improvements: " + improvements + ")\n");
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
    public List<String> getKeyWords() {
        return keywords;
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

    public String getFileName() {
        return fileName;
    }

}
