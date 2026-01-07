package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.dto.admin.AdministrationDashboardDTO;
import iut.unilim.fr.back.entity.RessourceSheet;
import iut.unilim.fr.back.mapper.AdministrationDashboardMapper;
import iut.unilim.fr.back.repository.RessourceSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdministrationDashboardController {

    @Autowired
    private RessourceSheetRepository ressourceSheetRepository;

    @Autowired
    private AdministrationDashboardMapper dashboardMapper;

    /**
     * Get all resource sheets for administration dashboard
     * GET /api/v2/admin/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<List<AdministrationDashboardDTO>> getAdministrationDashboard() {
        try {
            List<RessourceSheet> resourceSheets = ressourceSheetRepository.findAll();
            List<AdministrationDashboardDTO> dtos = dashboardMapper.toDTOList(resourceSheets);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get resource sheets by institution for administration dashboard
     * GET /api/v2/admin/dashboard/institution/{institutionId}
     */
    @GetMapping("/dashboard/institution/{institutionId}")
    public ResponseEntity<List<AdministrationDashboardDTO>> getDashboardByInstitution(@PathVariable Long institutionId) {
        try {
            List<RessourceSheet> resourceSheets = ressourceSheetRepository.findAll();
            List<AdministrationDashboardDTO> allDtos = dashboardMapper.toDTOList(resourceSheets);

            // Filter by institution
            List<AdministrationDashboardDTO> filteredDtos = allDtos.stream()
                .filter(dto -> dto.getInstitutionId() != null && dto.getInstitutionId().equals(institutionId))
                .toList();

            return ResponseEntity.ok(filteredDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get resource sheets by semester for administration dashboard
     * GET /api/v2/admin/dashboard/semester/{semester}
     */
    @GetMapping("/dashboard/semester/{semester}")
    public ResponseEntity<List<AdministrationDashboardDTO>> getDashboardBySemester(@PathVariable Integer semester) {
        try {
            List<RessourceSheet> resourceSheets = ressourceSheetRepository.findAll();
            List<AdministrationDashboardDTO> allDtos = dashboardMapper.toDTOList(resourceSheets);

            // Filter by semester
            List<AdministrationDashboardDTO> filteredDtos = allDtos.stream()
                .filter(dto -> dto.getSemester() != null && dto.getSemester().equals(semester))
                .toList();

            return ResponseEntity.ok(filteredDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

