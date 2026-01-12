package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.dto.admin.MCCCResourceDTO;
import iut.unilim.fr.back.dto.admin.MCCCSaeDTO;
import iut.unilim.fr.back.dto.admin.MCCCUEDTO;
import iut.unilim.fr.back.entity.Path;
import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.entity.SAE;
import iut.unilim.fr.back.entity.Terms;
import iut.unilim.fr.back.entity.UE;
import iut.unilim.fr.back.entity.UserSyncadia;
import iut.unilim.fr.back.mapper.MCCCMapper;
import iut.unilim.fr.back.mapper.MCCCSaeMapper;
import iut.unilim.fr.back.mapper.MCCCUEMapper;
import iut.unilim.fr.back.repository.PathRepository;
import iut.unilim.fr.back.repository.RessourceRepository;
import iut.unilim.fr.back.repository.SAERepository;
import iut.unilim.fr.back.repository.TermsRepository;
import iut.unilim.fr.back.repository.UERepository;
import iut.unilim.fr.back.repository.UserSyncadiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/mccc")
@CrossOrigin(origins = "*")
public class MCCCController {

    @Autowired
    private RessourceRepository ressourceRepository;

    @Autowired
    private SAERepository saeRepository;

    @Autowired
    private UERepository ueRepository;

    @Autowired
    private TermsRepository termsRepository;

    @Autowired
    private PathRepository pathRepository;

    @Autowired
    private UserSyncadiaRepository userSyncadiaRepository;

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
            List<SAE> saes = saeRepository.findBySemester(semester);
            List<MCCCSaeDTO> dtos = mcccSaeMapper.toDTOList(saes);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get MCCC SAEs by institution ID
     * GET /api/v2/mccc/saes/institution/{institutionId}
     */
    @GetMapping("/saes/institution/{institutionId}")
    public ResponseEntity<List<MCCCSaeDTO>> getMCCCSaesByInstitution(@PathVariable Long institutionId) {
        try {
            List<SAE> saes = saeRepository.findAll();
            List<MCCCSaeDTO> dtos = mcccSaeMapper.toDTOList(saes);

            // Filter by institution ID
            List<MCCCSaeDTO> filteredDtos = dtos.stream()
                .filter(dto -> dto.getInstitutionId() != null && dto.getInstitutionId().equals(institutionId))
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
            List<UE> ues = ueRepository.findByCompetenceLevel(level);
            List<MCCCUEDTO> dtos = mcccUEMapper.toDTOList(ues);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get MCCC UEs by semester
     * GET /api/v2/mccc/ues/semester/{semester}
     */
    @GetMapping("/ues/semester/{semester}")
    public ResponseEntity<List<MCCCUEDTO>> getMCCCUEsBySemester(@PathVariable Integer semester) {
        try {
            List<UE> ues = ueRepository.findBySemester(semester);
            List<MCCCUEDTO> dtos = mcccUEMapper.toDTOList(ues);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get MCCC UEs by institution ID
     * GET /api/v2/mccc/ues/institution/{institutionId}
     */
    @GetMapping("/ues/institution/{institutionId}")
    public ResponseEntity<List<MCCCUEDTO>> getMCCCUEsByInstitution(@PathVariable Long institutionId) {
        try {
            List<UE> ues = ueRepository.findAll();
            List<MCCCUEDTO> dtos = mcccUEMapper.toDTOList(ues);

            // Filter by institution ID
            List<MCCCUEDTO> filteredDtos = dtos.stream()
                .filter(dto -> dto.getInstitutionId() != null && dto.getInstitutionId().equals(institutionId))
                .toList();

            return ResponseEntity.ok(filteredDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new UE with Terms support
     * POST /api/v2/mccc/ues
     */
    @PostMapping("/ues")
    public ResponseEntity<?> createMCCCUE(@RequestBody MCCCUEDTO dto) {
        try {
            // Validate required fields
            if (dto.getUserId() == null) {
                return ResponseEntity.badRequest().body("userId is required");
            }

            // Get the user
            UserSyncadia user = userSyncadiaRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

            if (user.getInstitution() == null) {
                return ResponseEntity.badRequest().body("User has no institution");
            }

            // Get or create path for institution
            Long institutionId = user.getInstitution().getIdInstitution();
            Path path = pathRepository.findByInstitution_IdInstitution(institutionId)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    Path newPath = new Path();
                    newPath.setNumber(1);
                    newPath.setName("Default Path - " + user.getInstitution().getName());
                    newPath.setInstitution(user.getInstitution());
                    return pathRepository.save(newPath);
                });

            // Find or create Terms
            Terms terms = null;
            if (dto.getTermsCode() != null && !dto.getTermsCode().trim().isEmpty()) {
                terms = termsRepository.findByCode(dto.getTermsCode())
                    .orElseGet(() -> {
                        Terms newTerms = new Terms();
                        newTerms.setCode(dto.getTermsCode());
                        return termsRepository.save(newTerms);
                    });
            }

            // Create UE
            UE ue = new UE();
            ue.setEuApogeeCode(dto.getEuApogeeCode());
            ue.setLabel(dto.getLabel());
            ue.setName(dto.getName());
            ue.setCompetenceLevel(dto.getCompetenceLevel());
            ue.setSemester(dto.getSemester());
            ue.setPath(path);
            ue.setTerms(terms);

            UE savedUE = ueRepository.save(ue);

            // Convert to DTO and return
            MCCCUEDTO resultDto = mcccUEMapper.toDTO(savedUE);
            return ResponseEntity.ok(resultDto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating UE: " + e.getMessage());
        }
    }
}

