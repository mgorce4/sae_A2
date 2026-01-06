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
    @Autowired
    private TeacherHoursRepository teacherHoursRepository;
    @Autowired
    private NationalProgramObjectiveRepository nationalProgramObjectiveRepository;
    @Autowired
    private NationalProgramSkillRepository nationalProgramSkillRepository;

    private String fileName = "";

    private String ref;
    private String qualityReference;
    private String refUE;
    private String pedagoContentDs;
    private String profRef;
    private String labelResource;

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
            RessourceSheet resourceSheet = resultResourceSheet.get();

            id = resource.getIdResource();
            label = resource.getLabel();

            List<MainTeacherForResource> mainTeacherForResource = mainTeacherForResourceRepository.findByIdResource(id);
            List<SAELinkResource> SAELinkResources = saeLinkResourceRepository.findByIdResource(id);

            writeInLog("Get ressource \n"
                    + "     - id : " + id + "\n"
                    + "     - label : " + label + "\n");

            ref = ressourceName;
            qualityReference = "IU EN FOR 001";

            List<UeCoefficient> ueCoefficient = ueCoefficientRepository.findByResource_IdResource(id);
            UE ue = ueCoefficient.getFirst().getUe();
            refUE = ue.getLabel();

            List<NationalProgramObjective> npObjectives = nationalProgramObjectiveRepository.findByResourceSheet_IdResourceSheet(id);
            Boolean isMultiCompetences = resource.getDiffMultiCompetences();

            if (!npObjectives.isEmpty()) {
                if (isMultiCompetences) {
                    objectiveContent = "";
                    for (NationalProgramObjective npObjective : npObjectives) {
                        objectiveContent += npObjective.getContent() + ", ";
                    }
                } else {
                    objectiveContent = npObjectives.getFirst().getContent();
                }
            }

            List<NationalProgramSkill> npSkill = nationalProgramSkillRepository.findByResourceSheet_IdResourceSheet(id);

            skills.clear();
            if (npSkill.size() > 1) {
                for (NationalProgramSkill programSkill : npSkill) {
                    skills.add(programSkill.getDescription());
                }
            }else {
                skills.add(npSkill.getFirst().getDescription());
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
            hoursPN.clear();
            hoursPN.add(hoursPerStudent.getCm());
            hoursPN.add(hoursPerStudent.getTd());
            hoursPN.add(hoursPerStudent.getTp());
            hoursPN.add(hoursPerStudent.getCm() + hoursPerStudent.getTd() + hoursPerStudent.getTp());

            TeacherHours teacherHours = teacherHoursRepository.findByResourceSheet_IdResourceSheet(id).getFirst();
            hoursStudent.clear();
            hoursStudent.add(teacherHours.getCm());
            hoursStudent.add(teacherHours.getTd());
            hoursStudent.add(teacherHours.getTp());
            hoursStudent.add(teacherHours.getCm() + hoursPerStudent.getTd() + hoursPerStudent.getTp());

            PedagogicalContent pedagogicalContent = pedagogicalContentRepository.findByResourceSheet_IdResourceSheet(id).getFirst();
            pedagoContentCm = pedagogicalContent.getCm();
            pedagoContentTd = pedagogicalContent.getTd();
            pedagoContentTp = pedagogicalContent.getTp();


            RessourceTracking ressourceTracking = resourceTrackingRepository.findByResourceSheet_IdResourceSheet(resourceSheet.getIdResourceSheet()).getFirst();
            studentFeedback = ressourceTracking.getStudentFeedback();
            pedagoTeamFeedback = ressourceTracking.getPedagogicalFeedback();
            improvements = ressourceTracking.getImprovementSuggestions();

            writeInLog("Get from database :\n"
                + "- Ressource (" + labelResource + "; " + qualityReference + "; " + refUE + "; " + profRef + ";" + ")\n" +
                    "   - objectives(" + objectiveContent + ")\n" +
                    "   - skills(" + skills.toString() + ")\n" +
                    "   - saes(" + saes + ")\n" +
                    "   - terms(" + modalities.toString() + ")\n" +
                    "   - keywords(" + keywords.toString() + ")\n" +
                    "   - hoursStudent(" + hoursStudent.toString() +")\n" +
                    "   - hoursPN(" + hoursPN.toString() + ")\n" +
                    "   - pedagoContent( CM: "+ pedagoContentCm + "; TD: " + pedagoContentTd + "; TP: " + pedagoContentTp + ")\n" +
                    "   - feedBack(Student: " + studentFeedback + "; Pedagogical team: " + pedagoTeamFeedback + "; Improvements: " + improvements + ")\n");
        } else {
            writeInLog("Attempt to get from database with resource name: " + ressourceName +
                    "\n-> " + ressourceName + " not found in resources tables");
        }
    }

    public String getRef() {
        return ref;
    }
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
