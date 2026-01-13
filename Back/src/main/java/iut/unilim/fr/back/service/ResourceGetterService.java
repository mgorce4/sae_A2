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

import static iut.unilim.fr.back.controllerBack.LogController.writeInPdfLog;

@Service
public class ResourceGetterService {
    @Autowired
    private RessourceRepository ressourceRepository;
    @Autowired
    private RessourceSheetRepository  ressourceSheetRepository;

    @Autowired
    private ResourceSheetDTOController rsDTOController;

    private String resourceName = "";

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
    private final List<Integer> hoursPNInternship;
    private final List<Integer> hoursStudentInternship;
    private boolean isAlternance;

    private String pedagoContentCm;
    private String pedagoContentTd;
    private String pedagoContentTp;
    private String pedagoContentDs;

    private String pedagoTeamFeedback;
    private String studentFeedback;
    private String improvements;

    public static final String PLACEHOLDER = "Aucun contenue pour cette catégorie";

    public ResourceGetterService() {
        skills = new ArrayList<>();
        saes = new ArrayList<>();
        modalities = new ArrayList<>();
        keywords = new ArrayList<>();

        hoursPN = new ArrayList<>();
        hoursPNInternship = new ArrayList<>();
        hoursStudent = new ArrayList<>();
        hoursStudentInternship = new ArrayList<>();

        initializePlaceHolderValues();
    }

    private void initializePlaceHolderValues() {
        String PLACEHOLDER_TITLE = "Aucun";
        int PLACEHOLDER_HOURS = -1;
        int NB_ELEMENTS_HOURS = 4;

        skills.add(PLACEHOLDER);
        saes.add(PLACEHOLDER);
        modalities.add(PLACEHOLDER);

        for (int i=0; i<NB_ELEMENTS_HOURS; i++) {
            hoursPN.add(PLACEHOLDER_HOURS);
            hoursStudent.add(PLACEHOLDER_HOURS);
        }
        isAlternance = false;

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
    public void setValuesFromResource(String resourceName) {
        this.resourceName = "";
        initializePlaceHolderValues();
        int multi_skill_limit = 1;

        Optional<RessourceSheet> resultResourceSheet = Optional.empty();
        Long id;
        String label;


        Optional<Ressource> resultResource = ressourceRepository.findFirstByLabelStartingWith(resourceName);

        if (resultResource.isPresent()) {
            resultResourceSheet = ressourceSheetRepository.findFirstByResource_IdResource(resultResource.get().getIdResource());
        } else {
            writeInPdfLog("Could not get the resource sheet from the database because there is no resource with id = " + resourceName);}

        if (resultResource.isPresent() && resultResourceSheet.isPresent()) {
            this.resourceName = resourceName;
            Ressource resource = resultResource.get();

            id = resource.getIdResource();

            List<ResourceSheetDTO> resourcesSheets = rsDTOController.getResourceSheetsByResourceId(id);
            ResourceSheetDTO resourceSheetDTO = resourcesSheets.getLast();
            label = resourceSheetDTO.getResourceName();
            department = resourceSheetDTO.getDepartment();

            List<SaeInfoDTO> SAELinkResources = resourceSheetDTO.getLinkedSaes();

            ref = resourceName;
            qualityReference = "IU EN FOR 001";

            List<UeInfoDTO> UeReferences = resourceSheetDTO.getUeReferences();
            UeInfoDTO ue = UeReferences.getFirst();
            refUE = ue.getLabel();

            writeInPdfLog("Get resource \n"
                    + "     - id : " + id + "\n"
                    + "     - label : " + label );

            objectiveContent = resourceSheetDTO.getObjective();

            List<SkillDTO> npSkill = resourceSheetDTO.getSkills();

            skills.clear();
            if (npSkill.size() > multi_skill_limit) {
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


            modalities.clear();
            modalities.addAll(resourceSheetDTO.getModalities());

            HoursDTO hoursDTOTeacherInternship = resourceSheetDTO.getHoursTeacherAlternance();

            if (hoursDTOTeacherInternship != null && hoursDTOTeacherInternship.getHasAlternance()) {
                HoursDTO hoursDTOInternship = resourceSheetDTO.getHoursTeacherAlternance();
                setHoursDTO(hoursDTOInternship, hoursStudentInternship);
                HoursDTO hoursDTOPNInternship = resourceSheetDTO.getHoursPNAlternance();
                System.out.println(hoursDTOPNInternship);
                setHoursDTO(hoursDTOPNInternship, hoursPNInternship);
                isAlternance = true;
            }

            HoursDTO hoursDTOPN = resourceSheetDTO.getHoursPN();
            HoursDTO hoursDTOStudent = resourceSheetDTO.getHoursTeacher();
            setHoursDTO(hoursDTOPN, hoursPN);
            setHoursDTO(hoursDTOStudent, hoursStudent);

            PedagogicalContentDTO pedagogicalContent = resourceSheetDTO.getPedagogicalContent();
            StringBuilder pedagoContentBuilder;


            pedagoContentBuilder = createPedagoContent(pedagogicalContent.getCm());
            pedagoContentCm = pedagoContentBuilder.toString();

            pedagoContentBuilder = createPedagoContent(pedagogicalContent.getTd());
            pedagoContentTd = pedagoContentBuilder.toString();

            pedagoContentBuilder = createPedagoContent(pedagogicalContent.getTp());
            pedagoContentTp = pedagoContentBuilder.toString();

            pedagoContentBuilder = createPedagoContent(pedagogicalContent.getDs());
            pedagoContentDs = pedagoContentBuilder.toString();

            ResourceTrackingDTO resourceTracking = resourceSheetDTO.getTracking();
            studentFeedback = resourceTracking.getStudentFeedback();
            pedagoTeamFeedback = resourceTracking.getPedagogicalFeedback();
            improvements = resourceTracking.getImprovementSuggestions();
            String internshipLogContent = "";
            if (isAlternance) {
                internshipLogContent = "   - hoursPnInternship( " + hoursPNInternship + ")\n" +
                                    "   - hoursStudentInternship( " + hoursStudentInternship + ")\n";
            }

            writeInPdfLog("Get from database :\n" +
                    "   - Resource (" + labelResource + "; " + qualityReference + "; " + refUE + "; " + profRef + ";" + ")\n" +
                    "   - department(" + department + ")\n" +
                    "   - objectives(" + objectiveContent + ")\n" +
                    "   - skills(" + skills + ")\n" +
                    "   - saes(" + saes + ")\n" +
                    "   - terms(" + modalities + ")\n" +
                    "   - keywords(" + keywords + ")\n" +
                    "   - hoursStudent(" + hoursStudent +")\n" +
                    "   - hoursNP(" + hoursPN + ")\n" +
                    internshipLogContent +
                    "   - pedagoContent( DS: " + pedagoContentDs + "; CM: "+ pedagoContentCm + "; TD: " + pedagoContentTd + "; TP: " + pedagoContentTp + ")\n" +
                    "   - feedBack(Student: " + studentFeedback + "; Pedagogical team: " + pedagoTeamFeedback + "; Improvements: " + improvements + ")");
        } else {
            writeInPdfLog("Attempt to get from database with resource name: " + resourceName +
                    "\n-> " + resourceName + " not found in resources tables");
        }

    }

    public static void setHoursDTO(HoursDTO hoursDTO, List<Integer> hours) {
        hours.clear();
        hours.add(hoursDTO.getCm());
        hours.add(hoursDTO.getTd());
        hours.add(hoursDTO.getTp());
        hours.add(hoursDTO.getTotal());
    }

    private StringBuilder createPedagoContent(List<PedagogicalContentDTO.ContentItemDTO> pedagogicalContent) {
        StringBuilder pedagoContentBuilder = new StringBuilder();
        if (!pedagogicalContent.isEmpty()) {
            for (PedagogicalContentDTO.ContentItemDTO contentItemDTO : pedagogicalContent) {
                pedagoContentBuilder.append(contentItemDTO.getOrder()).append(". ").append(contentItemDTO.getContent()).append("\n");
            }
        }
        else {
            pedagoContentBuilder.append(PLACEHOLDER);
        }
        return pedagoContentBuilder;
    }

    public boolean isAlternance() {
        return isAlternance;
    }

    public String getRef() {
        return ref;
    }
    public String getDepartment(){return department;}
    public String qualityReference() {
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
    public List<Integer> getHoursPNInternship() {
        return hoursPNInternship;
    }
    public List<Integer> getHoursStudentInternship() {
        return hoursStudentInternship;
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

    public String getResourceName() {
        return resourceName;
    }

}
