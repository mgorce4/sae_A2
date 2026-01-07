package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.dto.admin.MCCCResourceDTO;
import iut.unilim.fr.back.dto.admin.MCCCSaeDTO;
import iut.unilim.fr.back.dto.admin.MCCCUEDTO;
import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.entity.SAE;
import iut.unilim.fr.back.entity.UE;
import iut.unilim.fr.back.mapper.MCCCMapper;
import iut.unilim.fr.back.mapper.MCCCSaeMapper;
import iut.unilim.fr.back.mapper.MCCCUEMapper;
import iut.unilim.fr.back.repository.RessourceRepository;
import iut.unilim.fr.back.repository.SAERepository;
import iut.unilim.fr.back.repository.UERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/mccc")
@CrossOrigin(origins = "http://localhost:3000")
public class MCCCController {

    @Autowired
    private RessourceRepository ressourceRepository;

    @Autowired
    private SAERepository saeRepository;

    @Autowired
    private UERepository ueRepository;

    @Autowired
    private MCCCMapper mcccMapper;

    @Autowired
    private MCCCSaeMapper mcccSaeMapper;

    @Autowired
    private MCCCUEMapper mcccUEMapper;

    /**
     * Get all MCCC resources
     * GET /api/v2/mccc/resources
     */
    @GetMapping("/resources")
    public ResponseEntity<List<MCCCResourceDTO>> getAllMCCCResources() {
        try {
            List<Ressource> resources = ressourceRepository.findAll();
            List<MCCCResourceDTO> dtos = mcccMapper.toDTOList(resources);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get MCCC resources by semester
     * GET /api/v2/mccc/resources/semester/{semester}
     */
    @GetMapping("/resources/semester/{semester}")
    public ResponseEntity<List<MCCCResourceDTO>> getMCCCResourcesBySemester(@PathVariable Integer semester) {
        try {
            List<Ressource> resources = ressourceRepository.findBySemester(semester);
            List<MCCCResourceDTO> dtos = mcccMapper.toDTOList(resources);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a single MCCC resource by ID
     * GET /api/v2/mccc/resources/{id}
     */
    @GetMapping("/resources/{id}")
    public ResponseEntity<MCCCResourceDTO> getMCCCResourceById(@PathVariable Long id) {
        try {
            Optional<Ressource> resourceOpt = ressourceRepository.findById(id);
            if (resourceOpt.isPresent()) {
                MCCCResourceDTO dto = mcccMapper.toDTO(resourceOpt.get());
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== SAE ENDPOINTS ====================

    /**
     * Get all MCCC SAEs
     * GET /api/v2/mccc/saes
     */
    @GetMapping("/saes")
    public ResponseEntity<List<MCCCSaeDTO>> getAllMCCCSaes() {
        try {
            List<SAE> saes = saeRepository.findAll();
            List<MCCCSaeDTO> dtos = mcccSaeMapper.toDTOList(saes);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a single MCCC SAE by ID
     * GET /api/v2/mccc/saes/{id}
     */
    @GetMapping("/saes/{id}")
    public ResponseEntity<MCCCSaeDTO> getMCCCSaeById(@PathVariable Long id) {
        try {
            Optional<SAE> saeOpt = saeRepository.findById(id);
            if (saeOpt.isPresent()) {
                MCCCSaeDTO dto = mcccSaeMapper.toDTO(saeOpt.get());
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get MCCC SAEs by semester
     * GET /api/v2/mccc/saes/semester/{semester}
     */
    @GetMapping("/saes/semester/{semester}")
    public ResponseEntity<List<MCCCSaeDTO>> getMCCCSaesBySemester(@PathVariable Integer semester) {
        try {
            List<SAE> saes = saeRepository.findAll();
            List<MCCCSaeDTO> allDtos = mcccSaeMapper.toDTOList(saes);

            // Filter by semester
            List<MCCCSaeDTO> filteredDtos = allDtos.stream()
                .filter(dto -> dto.getSemester() != null && dto.getSemester().equals(semester))
                .toList();

            return ResponseEntity.ok(filteredDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== UE ENDPOINTS ====================

    /**
     * Get all MCCC UEs
     * GET /api/v2/mccc/ues
     */
    @GetMapping("/ues")
    public ResponseEntity<List<MCCCUEDTO>> getAllMCCCUEs() {
        try {
            List<UE> ues = ueRepository.findAll();
            List<MCCCUEDTO> dtos = mcccUEMapper.toDTOList(ues);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a single MCCC UE by ID
     * GET /api/v2/mccc/ues/{id}
     */
    @GetMapping("/ues/{id}")
    public ResponseEntity<MCCCUEDTO> getMCCCUEById(@PathVariable Long id) {
        try {
            Optional<UE> ueOpt = ueRepository.findById(id);
            if (ueOpt.isPresent()) {
                MCCCUEDTO dto = mcccUEMapper.toDTO(ueOpt.get());
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get MCCC UEs by competence level
     * GET /api/v2/mccc/ues/level/{level}
     */
    @GetMapping("/ues/level/{level}")
    public ResponseEntity<List<MCCCUEDTO>> getMCCCUEsByLevel(@PathVariable Integer level) {
        try {
            List<UE> ues = ueRepository.findAll();
            List<MCCCUEDTO> allDtos = mcccUEMapper.toDTOList(ues);

            // Filter by competence level
            List<MCCCUEDTO> filteredDtos = allDtos.stream()
                .filter(dto -> dto.getCompetenceLevel() != null && dto.getCompetenceLevel().equals(level))
                .toList();

            return ResponseEntity.ok(filteredDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

