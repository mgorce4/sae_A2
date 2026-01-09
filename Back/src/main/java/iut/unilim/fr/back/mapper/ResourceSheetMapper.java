package iut.unilim.fr.back.mapper;

import iut.unilim.fr.back.dto.*;
import iut.unilim.fr.back.dto.PedagogicalContentDTO.ContentItemDTO;
import iut.unilim.fr.back.entity.*;
import iut.unilim.fr.back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ResourceSheetMapper {

    @Autowired
    private UeCoefficientRepository ueCoefficientRepository;

    @Autowired
    private MainTeacherForResourceRepository mainTeacherForResourceRepository;

    @Autowired
    private TeachersForResourceRepository teachersForResourceRepository;

    @Autowired
    private NationalProgramObjectiveRepository nationalProgramObjectiveRepository;

    @Autowired
    private NationalProgramSkillRepository nationalProgramSkillRepository;

    @Autowired
    private SAERepository saeRepository;

    @Autowired
    private SAELinkResourceRepository saeLinkResourceRepository;

    @Autowired
    private RessourceRepository ressourceRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private ModalitiesOfImplementationRepository modalitiesRepository;

    @Autowired
    private HoursPerStudentRepository hoursPerStudentRepository;

    @Autowired
    private TeacherHoursRepository teacherHoursRepository;

    @Autowired
    private PedagogicalContentRepository pedagogicalContentRepository;

    @Autowired
    private RessourceTrackingRepository resourceTrackingRepository;

    /**
     * Converts a RessourceSheet to a complete ResourceSheetDTO
     * ALL data is loaded in a single operation
     */
    public ResourceSheetDTO toDTO(RessourceSheet resourceSheet) {
        ResourceSheetDTO dto = new ResourceSheetDTO();

        // Basic information
        dto.setId(resourceSheet.getIdResourceSheet());
        dto.setYear(resourceSheet.getYear());

        Ressource resource = resourceSheet.getResource();
        if (resource == null) {
            return dto; // Returns an empty DTO if no resource
        }

        // Resource information
        dto.setResourceId(resource.getIdResource());
        dto.setResourceName(resource.getName());
        dto.setResourceLabel(resource.getLabel());
        dto.setResourceApogeeCode(resource.getApogeeCode());
        dto.setQualityReference("IU EN FOR 001"); // Fixed value
        dto.setSemester(resource.getSemester());
        dto.setDiffMultiCompetences(resource.getDiffMultiCompetences());

        // Department (via main teacher's institution)
        dto.setDepartment(getDepartment(resource.getIdResource()));
        dto.setInstitutionId(getInstitutionId(resource.getIdResource()));

        // Main teacher and teachers
        dto.setMainTeacher(getMainTeacher(resource.getIdResource()));
        dto.setTeachers(getTeachers(resource.getIdResource()));

        // UE and coefficients
        dto.setUeReferences(getUeReferences(resource.getIdResource()));

        // Objectives
        dto.setObjective(getObjective(resourceSheet.getIdResourceSheet()));

        // Skills
        dto.setSkills(getSkills(resourceSheet.getIdResourceSheet()));

        // Linked SAEs
        dto.setLinkedSaes(getLinkedSaes(resource));

        // Keywords
        dto.setKeywords(getKeywords(resourceSheet.getIdResourceSheet()));

        // Modalities
        dto.setModalities(getModalities(resourceSheet.getIdResourceSheet()));

        // PN hours (formation initiale)
        dto.setHoursPN(getHoursPN(resource.getIdResource(), false));

        // PN hours (alternance)
        dto.setHoursPNAlternance(getHoursPN(resource.getIdResource(), true));

        // Teacher hours (formation initiale)
        dto.setHoursTeacher(getHoursTeacher(resourceSheet.getIdResourceSheet()));

        // Teacher hours (alternance)
        dto.setHoursTeacherAlternance(getHoursTeacherAlternance(resourceSheet.getIdResourceSheet()));

        // Pedagogical content
        dto.setPedagogicalContent(getPedagogicalContent(resourceSheet.getIdResourceSheet()));

        // Tracking
        dto.setTracking(getTracking(resourceSheet.getIdResourceSheet()));

        return dto;
    }

    // ===== PRIVATE METHODS TO RETRIEVE DATA =====

    private String getDepartment(Long resourceId) {
        List<MainTeacherForResource> mainTeachers = mainTeacherForResourceRepository.findByIdResource(resourceId);
        if (!mainTeachers.isEmpty()) {
            UserSyncadia user = mainTeachers.get(0).getUser();
            if (user != null && user.getInstitution() != null) {
                return user.getInstitution().getName();
            }
        }
        return null;
    }

    private Long getInstitutionId(Long resourceId) {
        List<MainTeacherForResource> mainTeachers = mainTeacherForResourceRepository.findByIdResource(resourceId);
        if (!mainTeachers.isEmpty()) {
            UserSyncadia user = mainTeachers.get(0).getUser();
            if (user != null && user.getInstitution() != null) {
                return user.getInstitution().getIdInstitution();
            }
        }
        return null;
    }

    private String getMainTeacher(Long resourceId) {
        List<MainTeacherForResource> mainTeachers = mainTeacherForResourceRepository.findByIdResource(resourceId);
        if (!mainTeachers.isEmpty()) {
            UserSyncadia user = mainTeachers.get(0).getUser();
            if (user != null) {
                return user.getFirstname() + " " + user.getLastname();
            }
        }
        return null;
    }

    private List<String> getTeachers(Long resourceId) {
        List<TeachersForResource> teachers = teachersForResourceRepository.findByIdResource(resourceId);
        return teachers.stream()
            .map(t -> {
                UserSyncadia user = t.getUser();
                if (user != null) {
                    return user.getFirstname() + " " + user.getLastname();
                }
                return null;
            })
            .filter(name -> name != null)
            .collect(Collectors.toList());
    }

    private List<UeInfoDTO> getUeReferences(Long resourceId) {
        List<UeCoefficient> coefficients = ueCoefficientRepository.findByResource_IdResource(resourceId);
        return coefficients.stream()
            .map(coef -> {
                UE ue = coef.getUe();
                if (ue != null) {
                    return new UeInfoDTO(
                        ue.getUeNumber(),
                        ue.getLabel(),
                        ue.getName(),
                        coef.getCoefficient(),
                        ue.getCompetenceLevel()
                    );
                }
                return null;
            })
            .filter(ue -> ue != null)
            .collect(Collectors.toList());
    }

    private String getObjective(Long resourceSheetId) {
        List<NationalProgramObjective> objectives = nationalProgramObjectiveRepository.findByResourceSheet_IdResourceSheet(resourceSheetId);
        if (!objectives.isEmpty()) {
            // If multiple objectives, concatenate them
            return objectives.stream()
                .map(NationalProgramObjective::getContent)
                .collect(Collectors.joining(", "));
        }
        return null;
    }

    private List<SkillDTO> getSkills(Long resourceSheetId) {
        List<NationalProgramSkill> skills = nationalProgramSkillRepository.findByResourceSheet_IdResourceSheet(resourceSheetId);
        return skills.stream()
            .map(skill -> new SkillDTO(
                skill.getIdSkill(),
                skill.getLabel(),
                skill.getDescription()
            ))
            .collect(Collectors.toList());
    }

    private List<SaeInfoDTO> getLinkedSaes(Ressource resource) {
        // Get SAEs linked to this specific resource
        List<SAELinkResource> linkedSaes = saeLinkResourceRepository.findByIdResource(resource.getIdResource());
        List<Long> linkedSaeIds = linkedSaes.stream()
            .map(SAELinkResource::getIdSAE)
            .collect(Collectors.toList());

        // Get ALL SAEs from the same semester (using semester field directly from SAE entity)
        List<SAE> allSaes = new ArrayList<>();
        if (resource.getSemester() != null) {
            allSaes = saeRepository.findBySemester(resource.getSemester());
        }

        // Create DTOs with isLinked property
        // isLinked = true only if the SAE is in linkedSaeIds for THIS resource
        return allSaes.stream()
            .map(sae -> new SaeInfoDTO(
                sae.getIdSAE(),
                sae.getLabel(),
                sae.getApogeeCode(),
                linkedSaeIds.contains(sae.getIdSAE())  // Only true for SAEs linked to THIS resource
            ))
            .collect(Collectors.toList());
    }

    private List<String> getKeywords(Long resourceSheetId) {
        List<Keyword> keywords = keywordRepository.findByIdResourceSheet(resourceSheetId);
        return keywords.stream()
            .map(Keyword::getKeyword)
            .collect(Collectors.toList());
    }

    private List<String> getModalities(Long resourceSheetId) {
        List<ModalitiesOfImplementation> modalities = modalitiesRepository.findByResourceSheet_IdResourceSheet(resourceSheetId);
        return modalities.stream()
            .map(ModalitiesOfImplementation::getModality)
            .collect(Collectors.toList());
    }

    private HoursDTO getHoursPN(Long resourceId, boolean isAlternance) {
        List<HoursPerStudent> hoursList = hoursPerStudentRepository.findByResource_IdResource(resourceId);

        // Filter by has_alternance field
        HoursPerStudent hours = hoursList.stream()
            .filter(h -> h.getHasAlternance() != null && h.getHasAlternance() == isAlternance)
            .findFirst()
            .orElse(null);

        if (hours != null) {
            return new HoursDTO(
                hours.getCm() != null ? hours.getCm() : 0,
                hours.getTd() != null ? hours.getTd() : 0,
                hours.getTp() != null ? hours.getTp() : 0,
                hours.getHasAlternance()
            );
        }
        return new HoursDTO(0, 0, 0, isAlternance);
    }

    private HoursDTO getHoursTeacher(Long resourceSheetId) {
        List<TeacherHours> hoursList = teacherHoursRepository.findByResourceSheet_IdResourceSheet(resourceSheetId);

        // Find non-alternance hours (formation initiale)
        TeacherHours nonAlternanceHours = hoursList.stream()
            .filter(h -> h.getIsAlternance() != null && !h.getIsAlternance())
            .findFirst()
            .orElse(null);

        if (nonAlternanceHours != null) {
            return new HoursDTO(
                nonAlternanceHours.getCm() != null ? nonAlternanceHours.getCm() : 0,
                nonAlternanceHours.getTd() != null ? nonAlternanceHours.getTd() : 0,
                nonAlternanceHours.getTp() != null ? nonAlternanceHours.getTp() : 0,
                false
            );
        }
        return null;
    }

    private HoursDTO getHoursTeacherAlternance(Long resourceSheetId) {
        List<TeacherHours> hoursList = teacherHoursRepository.findByResourceSheet_IdResourceSheet(resourceSheetId);

        // Find alternance hours
        TeacherHours alternanceHours = hoursList.stream()
            .filter(h -> h.getIsAlternance() != null && h.getIsAlternance())
            .findFirst()
            .orElse(null);

        if (alternanceHours != null) {
            return new HoursDTO(
                alternanceHours.getCm() != null ? alternanceHours.getCm() : 0,
                alternanceHours.getTd() != null ? alternanceHours.getTd() : 0,
                alternanceHours.getTp() != null ? alternanceHours.getTp() : 0,
                true
            );
        }
        return null;
    }

    private PedagogicalContentDTO getPedagogicalContent(Long resourceSheetId) {
        PedagogicalContentDTO dto = new PedagogicalContentDTO();

        List<PedagogicalContent> contents = pedagogicalContentRepository.findByResourceSheet_IdResourceSheet(resourceSheetId);
        if (!contents.isEmpty()) {
            PedagogicalContent content = contents.get(0);

            // Parse CSV for CM, TD, TP, DS
            dto.setCm(parseCSVContent(content.getCm()));
            dto.setTd(parseCSVContent(content.getTd()));
            dto.setTp(parseCSVContent(content.getTp()));
            dto.setDs(parseCSVContent(content.getDs()));
        }

        return dto;
    }

    /**
     * Parses CSV of format "1 Content 1,2 Content 2,3 Content 3"
     * into a list of ContentItemDTO
     */
    private List<ContentItemDTO> parseCSVContent(String csv) {
        List<ContentItemDTO> items = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) {
            return items;
        }

        String[] parts = csv.split(",");
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                // Format: "1 Item content"
                int spaceIndex = part.indexOf(' ');
                if (spaceIndex > 0) {
                    try {
                        Integer order = Integer.parseInt(part.substring(0, spaceIndex));
                        String content = part.substring(spaceIndex + 1).trim();
                        items.add(new ContentItemDTO(order, content));
                    } catch (NumberFormatException e) {
                        // If parsing fails, add all content
                        items.add(new ContentItemDTO(items.size() + 1, part));
                    }
                } else {
                    items.add(new ContentItemDTO(items.size() + 1, part));
                }
            }
        }

        return items;
    }

    private ResourceTrackingDTO getTracking(Long resourceSheetId) {
        List<RessourceTracking> trackingList = resourceTrackingRepository.findByResourceSheet_IdResourceSheet(resourceSheetId);
        if (!trackingList.isEmpty()) {
            RessourceTracking tracking = trackingList.get(0);
            return new ResourceTrackingDTO(
                tracking.getPedagogicalFeedback(),
                tracking.getStudentFeedback(),
                tracking.getImprovementSuggestions()
            );
        }
        return new ResourceTrackingDTO(null, null, null);
    }
}

