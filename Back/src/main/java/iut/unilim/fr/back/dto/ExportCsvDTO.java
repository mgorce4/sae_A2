package iut.unilim.fr.back.dto;

import java.util.List;

public record ExportCsvDTO(
        String resourceName,
        String refUE,
        String profRef,
        String dep,
        List<String> saes,
        List<Integer> hoursStudent,
        List<Integer> hoursStudentInternship,
        boolean isInternship,
        String pedagoContentCm,
        String pedagoContentTd,
        String pedagoContentTp,
        String pedagoContentDs,
        String pedagoTeamFeedback,
        String studentFeedback,
        String improvement) {}