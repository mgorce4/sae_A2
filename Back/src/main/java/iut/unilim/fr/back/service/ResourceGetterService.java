package iut.unilim.fr.back.service;

import iut.unilim.fr.back.controller.ResourceSheetDTOController;
import iut.unilim.fr.back.dto.*;
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
    private ResourceSheetDTOController rsDTOController;

    private String fileName = "";

    private String ref;
    private String qualityReference;
    private String refUE;
    private String profRef;
    private String labelResource;
    private String department;

    private String objectiveContent;
    private final List<String> skills;

    private final List<String> saes;
    private final List<String> modalities;
    private final List<String> keywords;

    private final List<Integer> hoursPN;
    private final List<Integer> hoursStudent;

    private String pedagoContentCm;
    private String pedagoContentTd;
    private String pedagoContentTp;
    private String pedagoContentDs;

    private String pedagoTeamFeedback;
    private String studentFeedback;
    private String improvements;

    public ResourceGetterService() {
        skills = new ArrayList<>();
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
        skills.add(PLACEHOLDER);
        saes.add(PLACEHOLDER);
        modalities.add(PLACEHOLDER);

        for (int i=0; i<4; i++) {
            hoursPN.add(-1);
            hoursStudent.add(-1);
        }

        ref = PLACEHOLDER_TITLE;
        qualityReference = PLACEHOLDER_TITLE;
        refUE = PLACEHOLDER_TITLE;
        profRef = PLACEHOLDER_TITLE;
        labelResource = PLACEHOLDER_TITLE;
        department = PLACEHOLDER_TITLE;
        objectiveContent = PLACEHOLDER;
        keywords.add(PLACEHOLDER);
        pedagoContentDs = PLACEHOLDER;
        pedagoContentCm = PLACEHOLDER;
        pedagoContentTd = PLACEHOLDER;
        pedagoContentTp = PLACEHOLDER;

        pedagoTeamFeedback = PLACEHOLDER;
        studentFeedback = PLACEHOLDER;
        improvements = PLACEHOLDER;
    }

    @Transactional
    public void setValuesFromRessource(String ressourceName) {
        Optional<RessourceSheet> resultResourceSheet = Optional.empty();
        Long id;
        String label;

        Optional<Ressource> resultResource = ressourceRepository.findFirstByLabelStartingWith(ressourceName);

        if (resultResource.isPresent()) {
            resultResourceSheet = ressourceSheetRepository.findFirstByResource_IdResource(resultResource.get().getIdResource());
        } else {writeInLog("Could not get the resource sheet from the database because there is no resource with id = " + ressourceName);}

        if (resultResource.isPresent() && resultResourceSheet.isPresent()) {
            fileName = ressourceName;
            Ressource resource = resultResource.get();

            id = resource.getIdResource();

            List<ResourceSheetDTO> resourcesSheets = rsDTOController.getResourceSheetsByResourceId(id);
            ResourceSheetDTO resourceSheetDTO = resourcesSheets.getLast();
            label = resourceSheetDTO.getResourceName();
            department = resourceSheetDTO.getDepartment();

            List<SaeInfoDTO> SAELinkResources = resourceSheetDTO.getLinkedSaes();

            ref = ressourceName;
            qualityReference = "IU EN FOR 001";

            List<UeInfoDTO> UeReferences = resourceSheetDTO.getUeReferences();
            UeInfoDTO ue = UeReferences.getFirst();
            refUE = ue.getLabel();

            writeInLog("Get ressource \n"
                    + "     - id : " + id + "\n"
                    + "     - label : " + label + "\n");

            objectiveContent = resourceSheetDTO.getObjective();

            List<SkillDTO> npSkill = resourceSheetDTO.getSkills();

            skills.clear();
            if (npSkill.size() > 1) {
                for (SkillDTO programSkill : npSkill) {
                    skills.add(programSkill.getDescription());
                }
            }else {
                skills.add(npSkill.getFirst().getDescription());
            }

            profRef = resourceSheetDTO.getMainTeacher();
            labelResource = resource.getLabel() + ": " + resource.getName();

            saes.clear();
            for (SaeInfoDTO saeLinkResource : SAELinkResources) {
                if (saeLinkResource.getIsLinked()) {
                    saes.add(saeLinkResource.getLabel());
                }
            }

            List<String> keyWordsList = resourceSheetDTO.getKeywords();
            keywords.clear();
            keywords.addAll(keyWordsList);


            Terms terms = resource.getTerms();
            modalities.clear();
            modalities.add(terms.getCode());

            HoursDTO hoursDTOPN = resourceSheetDTO.getHoursPN();
            hoursPN.clear();
            hoursPN.add(hoursDTOPN.getCm());
            hoursPN.add(hoursDTOPN.getTd());
            hoursPN.add(hoursDTOPN.getTp());
            hoursPN.add(hoursDTOPN.getCm() + hoursDTOPN.getTd() + hoursDTOPN.getTp());

            HoursDTO teacherHours = resourceSheetDTO.getHoursTeacher();
            hoursStudent.clear();
            hoursStudent.add(teacherHours.getCm());
            hoursStudent.add(teacherHours.getTd());
            hoursStudent.add(teacherHours.getTp());
            hoursStudent.add(teacherHours.getCm() + hoursDTOPN.getTd() + hoursDTOPN.getTp());

            PedagogicalContentDTO pedagogicalContent = resourceSheetDTO.getPedagogicalContent();
            StringBuilder pedagoContentBuilder = new StringBuilder();
            pedagoContentCm = "";
            pedagoContentTd = "";
            pedagoContentTp = "";
            pedagoContentDs = "";


            for (PedagogicalContentDTO.ContentItemDTO contentItemDTO : pedagogicalContent.getCm()) {
                pedagoContentBuilder.append(contentItemDTO.getOrder()).append(". ").append(contentItemDTO.getContent()).append("\n");
            }
            pedagoContentCm = pedagoContentBuilder.toString();
            pedagoContentBuilder = new StringBuilder();

            for (PedagogicalContentDTO.ContentItemDTO contentItemDTO : pedagogicalContent.getTd()) {
                 pedagoContentBuilder.append(contentItemDTO.getOrder()).append(". ").append(contentItemDTO.getContent()).append("\n");
            }
            pedagoContentTd = pedagoContentBuilder.toString();
            pedagoContentBuilder = new StringBuilder();

            for (PedagogicalContentDTO.ContentItemDTO contentItemDTO : pedagogicalContent.getTp()) {
                 pedagoContentBuilder.append(contentItemDTO.getOrder()).append(". ").append(contentItemDTO.getContent()).append("\n");
            }
            pedagoContentTp = pedagoContentBuilder.toString();
            pedagoContentBuilder = new StringBuilder();

            for (PedagogicalContentDTO.ContentItemDTO contentItemDTO : pedagogicalContent.getDs()) {
                pedagoContentBuilder.append(contentItemDTO.getOrder()).append(". ").append(contentItemDTO.getContent()).append("\n");
            }
            pedagoContentDs = pedagoContentBuilder.toString();

            ResourceTrackingDTO ressourceTracking = resourceSheetDTO.getTracking();
            studentFeedback = ressourceTracking.getStudentFeedback();
            pedagoTeamFeedback = ressourceTracking.getPedagogicalFeedback();
            improvements = ressourceTracking.getImprovementSuggestions();

            writeInLog("Get from database :\n" +
                    "   - Ressource (" + labelResource + "; " + qualityReference + "; " + refUE + "; " + profRef + ";" + ")\n" +
                    "   - department(" + department + ")\n" +
                    "   - objectives(" + objectiveContent + ")\n" +
                    "   - skills(" + skills + ")\n" +
                    "   - saes(" + saes + ")\n" +
                    "   - terms(" + modalities + ")\n" +
                    "   - keywords(" + keywords + ")\n" +
                    "   - hoursStudent(" + hoursStudent +")\n" +
                    "   - hoursPN(" + hoursPN + ")\n" +
                    "   - pedagoContent( CM: "+ pedagoContentCm + "; TD: " + pedagoContentTd + "; TP: " + pedagoContentTp + ")\n" +
                    "   - terms(" + modalities + ")\n" +
                    "   - keywords(" + keywords + ")\n" +
                    "   - hoursStudent(" + hoursStudent +")\n" +
                    "   - hoursPN(" + hoursPN + ")\n" +
                    "   - pedagoContent( DS: " + pedagoContentDs + "; CM: "+ pedagoContentCm + "; TD: " + pedagoContentTd + "; TP: " + pedagoContentTp + ")\n" +
                    "   - feedBack(Student: " + studentFeedback + "; Pedagogical team: " + pedagoTeamFeedback + "; Improvements: " + improvements + ")\n");
        } else {
            writeInLog("Attempt to get from database with resource name: " + ressourceName +
                    "\n-> " + ressourceName + " not found in resources tables");
        }
    }

    public String getRef() {
        return ref;
    }
    public String getDepartment(){return department;}
    public String getNbRessource() {
        return qualityReference;
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
    public List<String> getSkills() {
        return skills;
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
    public String getPedagoContentDs(){
        return pedagoContentDs;
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
