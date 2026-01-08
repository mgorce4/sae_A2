package iut.unilim.fr.back.service;

import iut.unilim.fr.back.dto.ResourceSheetUpdateDTO;
import iut.unilim.fr.back.entity.*;
import iut.unilim.fr.back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceSheetUpdateService {

    @Autowired
    private RessourceSheetRepository ressourceSheetRepository;

    @Autowired
    private NationalProgramObjectiveRepository nationalProgramObjectiveRepository;

    @Autowired
    private NationalProgramSkillRepository nationalProgramSkillRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private ModalitiesOfImplementationRepository modalitiesRepository;

    @Autowired
    private SAELinkResourceRepository saeLinkResourceRepository;

    @Autowired
    private TeacherHoursRepository teacherHoursRepository;

    @Autowired
    private PedagogicalContentRepository pedagogicalContentRepository;

    @Autowired
    private RessourceTrackingRepository resourceTrackingRepository;

    /**
     * Update a resource sheet with new data
     * Handles both updates (sheet exists) and creation (new data for existing sheet)
     */
    @Transactional
    public void updateResourceSheet(Long resourceSheetId, ResourceSheetUpdateDTO updateDTO) {
        // Verify the resource sheet exists
        RessourceSheet resourceSheet = ressourceSheetRepository.findById(resourceSheetId)
            .orElseThrow(() -> new RuntimeException("Resource sheet not found: " + resourceSheetId));

        // 1. Update Objective
        updateObjective(resourceSheet, updateDTO.getObjective());

        // 2. Update Skills
        updateSkills(resourceSheet, updateDTO.getSkills());

        // 3. Update Keywords
        updateKeywords(resourceSheet, updateDTO.getKeywords());

        // 4. Update Modalities
        updateModalities(resourceSheet, updateDTO.getModalities());

        // 5. Update SAE Links
        updateSaeLinks(resourceSheet, updateDTO.getLinkedSaeIds());

        // 6. Update Teacher Hours (special: goes to TeacherHours table)
        updateTeacherHours(resourceSheet, updateDTO.getTeacherHours());

        // 7. Update Pedagogical Content
        updatePedagogicalContent(resourceSheet, updateDTO.getPedagogicalContent());

        // 8. Update Resource Tracking
        updateTracking(resourceSheet, updateDTO.getTracking());
    }

    private void updateObjective(RessourceSheet resourceSheet, String objective) {
        if (objective == null) return;

        // Find existing or create new
        List<NationalProgramObjective> existingObjectives =
            nationalProgramObjectiveRepository.findByResourceSheet_IdResourceSheet(resourceSheet.getIdResourceSheet());

        if (existingObjectives.isEmpty()) {
            // Create new
            NationalProgramObjective obj = new NationalProgramObjective();
            obj.setContent(objective);
            obj.setResourceSheet(resourceSheet);
            nationalProgramObjectiveRepository.save(obj);
        } else {
            // Update existing (take first one)
            NationalProgramObjective obj = existingObjectives.get(0);
            obj.setContent(objective);
            nationalProgramObjectiveRepository.save(obj);
        }
    }

    private void updateSkills(RessourceSheet resourceSheet, List<ResourceSheetUpdateDTO.SkillUpdateDTO> skills) {
        if (skills == null) return;

        // Delete existing skills
        List<NationalProgramSkill> existingSkills =
            nationalProgramSkillRepository.findByResourceSheet_IdResourceSheet(resourceSheet.getIdResourceSheet());
        nationalProgramSkillRepository.deleteAll(existingSkills);

        // Create new skills
        for (ResourceSheetUpdateDTO.SkillUpdateDTO skillDTO : skills) {
            if (skillDTO.getLabel() != null && !skillDTO.getLabel().trim().isEmpty()) {
                NationalProgramSkill skill = new NationalProgramSkill();
                skill.setLabel(skillDTO.getLabel());
                skill.setDescription(skillDTO.getDescription() != null ? skillDTO.getDescription() : "");
                skill.setResourceSheet(resourceSheet);
                nationalProgramSkillRepository.save(skill);
            }
        }
    }

    private void updateKeywords(RessourceSheet resourceSheet, List<String> keywords) {
        if (keywords == null) return;

        // Delete existing keywords
        List<Keyword> existingKeywords = keywordRepository.findByIdResourceSheet(resourceSheet.getIdResourceSheet());
        keywordRepository.deleteAll(existingKeywords);

        // Create new keywords
        for (String keyword : keywords) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                Keyword kw = new Keyword();
                kw.setKeyword(keyword);
                kw.setIdResourceSheet(resourceSheet.getIdResourceSheet());
                keywordRepository.save(kw);
            }
        }
    }

    private void updateModalities(RessourceSheet resourceSheet, List<String> modalities) {
        if (modalities == null) return;

        // Delete existing modalities
        List<ModalitiesOfImplementation> existingModalities =
            modalitiesRepository.findByResourceSheet_IdResourceSheet(resourceSheet.getIdResourceSheet());
        modalitiesRepository.deleteAll(existingModalities);

        // Create new modalities
        for (String modality : modalities) {
            if (modality != null && !modality.trim().isEmpty()) {
                ModalitiesOfImplementation mod = new ModalitiesOfImplementation();
                mod.setModality(modality);
                mod.setResourceSheet(resourceSheet);
                modalitiesRepository.save(mod);
            }
        }
    }

    private void updateSaeLinks(RessourceSheet resourceSheet, List<Long> linkedSaeIds) {
        if (linkedSaeIds == null) return;

        Long resourceId = resourceSheet.getResource().getIdResource();

        // Get existing SAE links for this resource
        List<SAELinkResource> existingLinks = saeLinkResourceRepository.findByIdResource(resourceId);

        // Get existing SAE IDs
        List<Long> existingSaeIds = existingLinks.stream()
            .map(SAELinkResource::getIdSAE)
            .collect(Collectors.toList());

        // Find links to add (in new list but not in existing)
        List<Long> toAdd = linkedSaeIds.stream()
            .filter(id -> !existingSaeIds.contains(id))
            .collect(Collectors.toList());

        // Find links to remove (in existing but not in new list)
        List<Long> toRemove = existingSaeIds.stream()
            .filter(id -> !linkedSaeIds.contains(id))
            .collect(Collectors.toList());

        // Add new links
        for (Long saeId : toAdd) {
            SAELinkResource link = new SAELinkResource();
            link.setIdSAE(saeId);
            link.setIdResource(resourceId);
            saeLinkResourceRepository.save(link);
        }

        // Remove old links
        for (SAELinkResource link : existingLinks) {
            if (toRemove.contains(link.getIdSAE())) {
                saeLinkResourceRepository.delete(link);
            }
        }
    }

    private void updateTeacherHours(RessourceSheet resourceSheet, ResourceSheetUpdateDTO.HoursUpdateDTO hours) {
        if (hours == null) return;

        // Find existing teacher hours for this resource sheet
        List<TeacherHours> existingHours =
            teacherHoursRepository.findByResourceSheet_IdResourceSheet(resourceSheet.getIdResourceSheet());

        TeacherHours teacherHours;
        if (existingHours.isEmpty()) {
            // Create new (non-alternance hours)
            teacherHours = new TeacherHours();
            teacherHours.setResourceSheet(resourceSheet);
            teacherHours.setIsAlternance(false);
        } else {
            // Update existing (take first non-alternance)
            teacherHours = existingHours.stream()
                .filter(h -> !h.getIsAlternance())
                .findFirst()
                .orElse(existingHours.get(0));
        }

        teacherHours.setCm(hours.getCm() != null ? hours.getCm() : 0);
        teacherHours.setTd(hours.getTd() != null ? hours.getTd() : 0);
        teacherHours.setTp(hours.getTp() != null ? hours.getTp() : 0);

        teacherHoursRepository.save(teacherHours);
    }

    private void updatePedagogicalContent(RessourceSheet resourceSheet, ResourceSheetUpdateDTO.PedagogicalContentUpdateDTO content) {
        if (content == null) return;

        // Find existing or create new
        List<PedagogicalContent> existingContent =
            pedagogicalContentRepository.findByResourceSheet_IdResourceSheet(resourceSheet.getIdResourceSheet());

        PedagogicalContent pedContent;
        if (existingContent.isEmpty()) {
            pedContent = new PedagogicalContent();
            pedContent.setResourceSheet(resourceSheet);
        } else {
            pedContent = existingContent.get(0);
        }

        // Convert lists to CSV format
        pedContent.setCm(convertToCSV(content.getCm()));
        pedContent.setTd(convertToCSV(content.getTd()));
        pedContent.setTp(convertToCSV(content.getTp()));
        pedContent.setDs(convertToCSV(content.getDs()));

        pedagogicalContentRepository.save(pedContent);
    }

    private String convertToCSV(List<ResourceSheetUpdateDTO.PedagogicalContentUpdateDTO.ContentItemUpdateDTO> items) {
        if (items == null || items.isEmpty()) return null;

        return items.stream()
            .map(item -> item.getOrder() + " " + (item.getContent() != null ? item.getContent() : ""))
            .collect(Collectors.joining(","));
    }

    private void updateTracking(RessourceSheet resourceSheet, ResourceSheetUpdateDTO.ResourceTrackingUpdateDTO tracking) {
        if (tracking == null) return;

        // Find existing or create new
        List<RessourceTracking> existingTracking =
            resourceTrackingRepository.findByResourceSheet_IdResourceSheet(resourceSheet.getIdResourceSheet());

        RessourceTracking resTracking;
        if (existingTracking.isEmpty()) {
            resTracking = new RessourceTracking();
            resTracking.setResourceSheet(resourceSheet);
        } else {
            resTracking = existingTracking.get(0);
        }

        resTracking.setPedagogicalFeedback(tracking.getPedagogicalFeedback());
        resTracking.setStudentFeedback(tracking.getStudentFeedback());
        resTracking.setImprovementSuggestions(tracking.getImprovementSuggestions());

        resourceTrackingRepository.save(resTracking);
    }
}

