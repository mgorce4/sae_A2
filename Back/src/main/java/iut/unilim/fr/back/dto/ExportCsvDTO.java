package iut.unilim.fr.back.dto;

import java.util.List;

public record ExportCsvDTO(
        String resourceName,
        String refUE,
        String profRef,
        String dep,
        List<String> saes,
        List<Double> hoursStudent,
        List<Double> hoursStudentInternship,
        boolean isInternship,
        String pedagoContentCm,
        String pedagoContentTd,
        String pedagoContentTp,
        String pedagoContentDs,
        String pedagoTeamFeedback,
        String studentFeedback,
        String improvement) {

        public String getLogs() {
                return "        - Resource :" + resourceName + "\n" +
                 "        - UE : " + refUE + "\n" +
                 "        - Referent teacher : " + profRef + "\n" + 
                 "        - Department : " + dep + "\n" +
                 "        - Saes : " + saes.toString() + "\n" +
                 "        - Resource is in internship : " + isInternship + "\n" +
                 "        - Hours per student : " + hoursStudent.toString() + "\n" +
                 "        - Hours per intern student : " + hoursStudentInternship.toString() + "\n" +
                 "        - Pedagogical content : " + pedagoContentDs + ", " + pedagoContentCm + ", " + pedagoContentTd + ", " + pedagoContentTp + "\n" +
                 "        - Feedbacks : " + pedagoTeamFeedback + ", " + studentFeedback + ", " + improvement + "\n`";
        }
}