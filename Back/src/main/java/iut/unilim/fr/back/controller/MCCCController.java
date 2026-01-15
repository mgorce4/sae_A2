package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.dto.admin.MCCCResourceDTO;
import iut.unilim.fr.back.dto.admin.MCCCSaeDTO;
import iut.unilim.fr.back.dto.admin.MCCCUEDTO;
import iut.unilim.fr.back.entity.Path;
import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.entity.SAE;
import iut.unilim.fr.back.entity.SAEHours;
import iut.unilim.fr.back.entity.SAELinkResource;
import iut.unilim.fr.back.entity.Terms;
import iut.unilim.fr.back.entity.UE;
import iut.unilim.fr.back.entity.UeCoefficientSAE;
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
import org.springframework.transaction.annotation.Transactional;
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
    private iut.unilim.fr.back.repository.SAEHoursRepository saeHoursRepository;

    @Autowired
    private iut.unilim.fr.back.repository.UeCoefficientSAERepository ueCoefficientSAERepository;

    @Autowired
    private iut.unilim.fr.back.repository.SAELinkResourceRepository saeLinkResourceRepository;

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

    /**
     * Get MCCC resources by institution ID
     * GET /api/v2/mccc/resources/institution/{institutionId}
     */
    @GetMapping("/resources/institution/{institutionId}")
    public ResponseEntity<List<MCCCResourceDTO>> getMCCCResourcesByInstitution(@PathVariable Long institutionId) {
        try {
            List<Ressource> resources = ressourceRepository.findAll();
            List<MCCCResourceDTO> dtos = mcccMapper.toDTOList(resources);

            // Filter by institution ID
            List<MCCCResourceDTO> filteredDtos = dtos.stream()
                .filter(dto -> dto.getInstitutionId() != null && dto.getInstitutionId().equals(institutionId))
                .toList();

            return ResponseEntity.ok(filteredDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get MCCC resources by path ID
     * GET /api/v2/mccc/resources/path/{pathId}
     */
    @GetMapping("/resources/path/{pathId}")
    public ResponseEntity<List<MCCCResourceDTO>> getMCCCResourcesByPath(@PathVariable Long pathId) {
        try {
            List<Ressource> resources = ressourceRepository.findByPathId(pathId);
            List<MCCCResourceDTO> dtos = mcccMapper.toDTOList(resources);
            return ResponseEntity.ok(dtos);
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

    /**
     * Get MCCC SAEs by path ID
     * GET /api/v2/mccc/saes/path/{pathId}
     */
    @GetMapping("/saes/path/{pathId}")
    public ResponseEntity<List<MCCCSaeDTO>> getMCCCSaesByPath(@PathVariable Long pathId) {
        try {
            List<SAE> saes = saeRepository.findByPathId(pathId);
            List<MCCCSaeDTO> dtos = mcccSaeMapper.toDTOList(saes);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new SAE with path, hours, and UE coefficients
     * POST /api/v2/mccc/saes
     */
    @PostMapping("/saes")
    @Transactional
    public ResponseEntity<?> createMCCCSAE(@RequestBody MCCCSaeDTO dto) {
        try {
            // Validate required fields
            if (dto.getPathId() == null) {
                return ResponseEntity.badRequest().body("pathId is required");
            }
            if (dto.getLabel() == null || dto.getLabel().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("label is required");
            }
            if (dto.getApogeeCode() == null || dto.getApogeeCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("apogeeCode is required");
            }
            if (dto.getSemester() == null) {
                return ResponseEntity.badRequest().body("semester is required");
            }
            if (dto.getTermsCode() == null || dto.getTermsCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("termsCode is required");
            }
            if (dto.getHours() == null || dto.getHours() <= 0) {
                return ResponseEntity.badRequest().body("hours is required and must be > 0");
            }
            if (dto.getUeCoefficients() == null || dto.getUeCoefficients().isEmpty()) {
                return ResponseEntity.badRequest().body("At least one UE coefficient is required");
            }

            // Get path by ID
            Path path = pathRepository.findById(dto.getPathId())
                .orElseThrow(() -> new RuntimeException("Path not found with id: " + dto.getPathId()));

            // Find or create Terms
            Terms terms = termsRepository.findFirstByCode(dto.getTermsCode())
                .orElseGet(() -> {
                    Terms newTerms = new Terms();
                    newTerms.setCode(dto.getTermsCode());
                    return termsRepository.save(newTerms);
                });

            // Create SAE
            SAE sae = new SAE();
            sae.setLabel(dto.getLabel());
            sae.setApogeeCode(dto.getApogeeCode());
            sae.setSemester(dto.getSemester());
            sae.setTerms(terms);
            sae.setPath(path);

            SAE savedSAE = saeRepository.save(sae);

            // Create SAEHours for formation initiale (has_alternance = false)
            SAEHours hoursInitiale = new SAEHours();
            hoursInitiale.setHours(dto.getHours());
            hoursInitiale.setSae(savedSAE);
            hoursInitiale.setHasAlternance(false);
            saeHoursRepository.save(hoursInitiale);

            // Create SAEHours for alternance if provided
            if (dto.getHoursAlternance() != null && dto.getHoursAlternance() > 0) {
                SAEHours hoursAlternance = new SAEHours();
                hoursAlternance.setHours(dto.getHoursAlternance());
                hoursAlternance.setSae(savedSAE);
                hoursAlternance.setHasAlternance(true);
                saeHoursRepository.save(hoursAlternance);
            }

            // Create UE coefficients
            for (MCCCSaeDTO.UECoefficientDTO ueCoefDto : dto.getUeCoefficients()) {
                // Check if ueId is provided (preferred method)
                if (ueCoefDto.getUeId() != null) {
                    // Find UE by ID - most reliable method
                    UE ue = ueRepository.findById(ueCoefDto.getUeId())
                        .orElseThrow(() -> new RuntimeException("UE not found with id: " + ueCoefDto.getUeId()));

                    UeCoefficientSAE ueCoefficientSAE = new UeCoefficientSAE();
                    ueCoefficientSAE.setUe(ue);
                    ueCoefficientSAE.setSae(savedSAE);
                    ueCoefficientSAE.setCoefficient(ueCoefDto.getCoefficient());
                    ueCoefficientSAERepository.save(ueCoefficientSAE);
                    } else if (ueCoefDto.getUeLabel() != null && !ueCoefDto.getUeLabel().trim().isEmpty()) {
                    // Fallback: Find UE by label, semester and path (for backwards compatibility)
                    Optional<UE> ueOpt = ueRepository.findByLabelAndSemesterAndPath_IdPath(
                        ueCoefDto.getUeLabel(),
                        dto.getSemester(),
                        dto.getPathId()
                    );

                    if (ueOpt.isEmpty()) {
                        throw new RuntimeException("UE not found with label: " + ueCoefDto.getUeLabel() +
                            " for semester " + dto.getSemester() + " and path " + dto.getPathId());
                    }

                    UE ue = ueOpt.get();

                    UeCoefficientSAE ueCoefficientSAE = new UeCoefficientSAE();
                    ueCoefficientSAE.setUe(ue);
                    ueCoefficientSAE.setSae(savedSAE);
                    ueCoefficientSAE.setCoefficient(ueCoefDto.getCoefficient());
                    ueCoefficientSAERepository.save(ueCoefficientSAE);
                }
            }


            // Reload SAE from database to ensure all relationships are loaded
            SAE reloadedSAE = saeRepository.findById(savedSAE.getIdSAE())
                .orElseThrow(() -> new RuntimeException("Failed to reload saved SAE"));

            // Convert to DTO and return
            MCCCSaeDTO resultDto = mcccSaeMapper.toDTO(reloadedSAE);
            return ResponseEntity.ok(resultDto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating SAE: " + e.getMessage());
        }
    }

    /**
     * Update an existing SAE
     * PUT /api/v2/mccc/saes/{id}
     */
    @PutMapping("/saes/{id}")
    @Transactional
    public ResponseEntity<?> updateMCCCSAE(@PathVariable Long id, @RequestBody MCCCSaeDTO dto) {
        try {
            boolean exists = saeRepository.existsById(id);

            if (!exists) {
                saeRepository.findAll().forEach(s -> System.out.println("  - SAE ID: " + s.getIdSAE() + ", Label: " + s.getLabel()));
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("SAE not found with id: " + id);
            }

            // Find existing SAE
            SAE existingSAE = saeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SAE not found with id: " + id));

            // IMPORTANT: Delete child records FIRST before updating the parent
            // Delete existing hours
            List<SAEHours> existingHours = saeHoursRepository.findBySae_IdSAE(id);
            saeHoursRepository.deleteAll(existingHours);

            // Delete existing UE coefficients
            List<UeCoefficientSAE> existingCoefficients = ueCoefficientSAERepository.findBySae_IdSAE(id);
            ueCoefficientSAERepository.deleteAll(existingCoefficients);

            // Now update basic fields
            existingSAE.setLabel(dto.getLabel());
            existingSAE.setApogeeCode(dto.getApogeeCode());
            existingSAE.setSemester(dto.getSemester());

            // Update Terms
            if (dto.getTermsCode() != null && !dto.getTermsCode().trim().isEmpty()) {
                Terms terms = termsRepository.findFirstByCode(dto.getTermsCode())
                    .orElseGet(() -> {
                        Terms newTerms = new Terms();
                        newTerms.setCode(dto.getTermsCode());
                        return termsRepository.save(newTerms);
                    });
                existingSAE.setTerms(terms);
            }

            // Update Path if pathId is provided
            if (dto.getPathId() != null) {
                Path path = pathRepository.findById(dto.getPathId())
                    .orElseThrow(() -> new RuntimeException("Path not found with id: " + dto.getPathId()));
                existingSAE.setPath(path);
            }

            SAE updatedSAE = saeRepository.save(existingSAE);

            // Create new hours for formation initiale
            if (dto.getHours() != null && dto.getHours() > 0) {
                SAEHours hoursInitiale = new SAEHours();
                hoursInitiale.setHours(dto.getHours());
                hoursInitiale.setSae(updatedSAE);
                hoursInitiale.setHasAlternance(false);
                saeHoursRepository.save(hoursInitiale);
            }

            // Create new hours for alternance if provided
            if (dto.getHoursAlternance() != null && dto.getHoursAlternance() > 0) {
                SAEHours hoursAlternance = new SAEHours();
                hoursAlternance.setHours(dto.getHoursAlternance());
                hoursAlternance.setSae(updatedSAE);
                hoursAlternance.setHasAlternance(true);
                saeHoursRepository.save(hoursAlternance);
            }

            // Create new UE coefficients
            if (dto.getUeCoefficients() != null) {
                for (MCCCSaeDTO.UECoefficientDTO ueCoefDto : dto.getUeCoefficients()) {
                    // Check if ueId is provided (preferred method)
                    if (ueCoefDto.getUeId() != null) {
                        // Find UE by ID - most reliable method
                        UE ue = ueRepository.findById(ueCoefDto.getUeId())
                            .orElseThrow(() -> new RuntimeException("UE not found with id: " + ueCoefDto.getUeId()));

                        UeCoefficientSAE ueCoefficientSAE = new UeCoefficientSAE();
                        ueCoefficientSAE.setUe(ue);
                        ueCoefficientSAE.setSae(updatedSAE);
                        ueCoefficientSAE.setCoefficient(ueCoefDto.getCoefficient());
                        ueCoefficientSAERepository.save(ueCoefficientSAE);
                    } else if (ueCoefDto.getUeLabel() != null && !ueCoefDto.getUeLabel().trim().isEmpty()) {
                        // Fallback: Find UE by label, semester and path (for backwards compatibility)
                        Optional<UE> ueOpt = ueRepository.findByLabelAndSemesterAndPath_IdPath(
                            ueCoefDto.getUeLabel(),
                            dto.getSemester(),
                            dto.getPathId()
                        );

                        if (!ueOpt.isPresent()) {
                            throw new RuntimeException("UE not found with label: " + ueCoefDto.getUeLabel() +
                                " for semester " + dto.getSemester() + " and path " + dto.getPathId());
                        }

                        UE ue = ueOpt.get();

                        UeCoefficientSAE ueCoefficientSAE = new UeCoefficientSAE();
                        ueCoefficientSAE.setUe(ue);
                        ueCoefficientSAE.setSae(updatedSAE);
                        ueCoefficientSAE.setCoefficient(ueCoefDto.getCoefficient());
                        ueCoefficientSAERepository.save(ueCoefficientSAE);
                    }
                    // Skip if neither ueId nor ueLabel is provided
                }
            }


            // Reload SAE from database to ensure all relationships are loaded
            SAE reloadedSAE = saeRepository.findById(updatedSAE.getIdSAE())
                .orElseThrow(() -> new RuntimeException("Failed to reload updated SAE"));

            // Convert to DTO and return
            MCCCSaeDTO resultDto = mcccSaeMapper.toDTO(reloadedSAE);
            return ResponseEntity.ok(resultDto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating SAE: " + e.getMessage());
        }
    }

    /**
     * Delete a SAE by ID
     * DELETE /api/v2/mccc/saes/{id}
     */
    @DeleteMapping("/saes/{id}")
    @Transactional
    public ResponseEntity<?> deleteMCCCSAE(@PathVariable Long id) {
        try {

            // Vérifier si la SAÉ existe
            if (!saeRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            // Delete related SAE-Resource links
            List<SAELinkResource> saeLinks = saeLinkResourceRepository.findByIdSAE(id);
            saeLinkResourceRepository.deleteAll(saeLinks);

            // Delete related SAEHours
            List<SAEHours> saeHours = saeHoursRepository.findBySae_IdSAE(id);
            saeHoursRepository.deleteAll(saeHours);

            // Delete related UE coefficients
            List<UeCoefficientSAE> ueCoefficients = ueCoefficientSAERepository.findBySae_IdSAE(id);
            ueCoefficientSAERepository.deleteAll(ueCoefficients);

            // Flush to ensure all deletions are sent to DB
            saeRepository.flush();

            // Reload the SAE to avoid OptimisticLockingException
            SAE saeToDelete = saeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SAE not found after deleting relations"));

            // Delete the SAE
            saeRepository.delete(saeToDelete);
            saeRepository.flush();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting SAE: " + e.getMessage());
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
     * Get MCCC UEs by path ID
     * GET /api/v2/mccc/ues/path/{pathId}
     */
    @GetMapping("/ues/path/{pathId}")
    public ResponseEntity<List<MCCCUEDTO>> getMCCCUEsByPath(@PathVariable Long pathId) {
        try {
            List<UE> ues = ueRepository.findByPath_IdPath(pathId);
            List<MCCCUEDTO> dtos = mcccUEMapper.toDTOList(ues);
            return ResponseEntity.ok(dtos);
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

            // Get path by ID if provided, otherwise use fallback
            Path path;


            if (dto.getPathId() != null) {
                // Use the provided path ID - direct lookup
                path = pathRepository.findById(dto.getPathId())
                    .orElseThrow(() -> new RuntimeException("Path not found with id: " + dto.getPathId()));

                // Verify the path belongs to the user's institution
                if (!path.getInstitution().getIdInstitution().equals(user.getInstitution().getIdInstitution())) {
                    return ResponseEntity.badRequest()
                        .body("Le parcours spécifié n'appartient pas à votre institution");
                }

                } else {
                Long institutionId = user.getInstitution().getIdInstitution();
                path = pathRepository.findByInstitution_IdInstitution(institutionId)
                    .stream()
                    .findFirst()
                    .orElseGet(() -> {
                        Path newPath = new Path();
                        newPath.setNumber(1);
                        newPath.setName("Default Path - " + user.getInstitution().getName());
                        newPath.setInstitution(user.getInstitution());
                        return pathRepository.save(newPath);
                    });
            }

            // Find or create Terms
            Terms terms = null;
            if (dto.getTermsCode() != null && !dto.getTermsCode().trim().isEmpty()) {
                terms = termsRepository.findFirstByCode(dto.getTermsCode())
                    .orElseGet(() -> {
                        Terms newTerms = new Terms();
                        newTerms.setCode(dto.getTermsCode());
                        return termsRepository.save(newTerms);
                    });
            }

            // Vérifier si une UE avec le même label existe déjà dans ce semestre et ce path
            Optional<UE> existingUE = ueRepository.findByLabelAndSemesterAndPath_IdPath(
                dto.getLabel(),
                dto.getSemester(),
                path.getIdPath()
            );

            if (existingUE.isPresent()) {
                return ResponseEntity.badRequest()
                    .body("Une UE avec le même numéro existe déjà dans ce semestre pour ce parcours.");
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

            MCCCUEDTO resultDto = mcccUEMapper.toDTO(savedUE);
            return ResponseEntity.ok(resultDto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating UE: " + e.getMessage());
        }
    }

    /**
     * Update an existing UE with Terms support
     * PUT /api/v2/mccc/ues/{id}
     */
    @PutMapping("/ues/{id}")
    public ResponseEntity<?> updateMCCCUE(@PathVariable Long id, @RequestBody MCCCUEDTO dto) {
        try {
            // Find existing UE
            UE existingUE = ueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UE not found with id: " + id));

            // Vérifier si le label, semestre ou path a changé
            boolean labelChanged = !existingUE.getLabel().equals(dto.getLabel());
            boolean semesterChanged = !existingUE.getSemester().equals(dto.getSemester());

            // Si label ou semestre a changé, vérifier qu'il n'y a pas de doublon
            if (labelChanged || semesterChanged) {
                Long pathId = existingUE.getPath() != null ? existingUE.getPath().getIdPath() : null;

                // Si pathId est fourni dans le DTO, l'utiliser pour la validation
                if (dto.getPathId() != null) {
                    pathId = dto.getPathId();
                }

                if (pathId != null) {
                    Optional<UE> duplicateUE = ueRepository.findByLabelAndSemesterAndPath_IdPath(
                        dto.getLabel(),
                        dto.getSemester(),
                        pathId
                    );

                    // Vérifier que ce n'est pas la même UE qu'on est en train de modifier
                    if (duplicateUE.isPresent() && !duplicateUE.get().getUeNumber().equals(id)) {
                        return ResponseEntity.badRequest()
                            .body("Une UE avec le même numéro existe déjà dans ce semestre pour ce parcours.");
                    }
                }
            }

            // Update basic fields
            existingUE.setEuApogeeCode(dto.getEuApogeeCode());
            existingUE.setLabel(dto.getLabel());
            existingUE.setName(dto.getName());
            existingUE.setCompetenceLevel(dto.getCompetenceLevel());
            existingUE.setSemester(dto.getSemester());

            // Update Terms - Only if termsCode changed
            // On vérifie si le code a changé pour éviter de chercher inutilement
            String currentTermsCode = existingUE.getTerms() != null ? existingUE.getTerms().getCode() : null;
            String newTermsCode = dto.getTermsCode();

            // Si le code a changé ou si on passe de null à une valeur ou vice-versa
            if ((currentTermsCode == null && newTermsCode != null && !newTermsCode.trim().isEmpty()) ||
                (currentTermsCode != null && newTermsCode == null) ||
                (currentTermsCode != null && newTermsCode != null && !currentTermsCode.equals(newTermsCode))) {

                if (newTermsCode != null && !newTermsCode.trim().isEmpty()) {
                    // Chercher le premier Terms avec ce code
                    Terms terms = termsRepository.findFirstByCode(newTermsCode)
                        .orElseGet(() -> {
                            Terms newTerms = new Terms();
                            newTerms.setCode(newTermsCode);
                            return termsRepository.save(newTerms);
                        });
                    existingUE.setTerms(terms);
                } else {
                    existingUE.setTerms(null);
                }
            }
            // Sinon, on garde le Terms actuel (même objet, même ID)

            // Update Path if pathId is provided
            if (dto.getPathId() != null) {
                // Get user to verify institution
                if (dto.getUserId() != null) {
                    UserSyncadia user = userSyncadiaRepository.findById(dto.getUserId()).orElse(null);
                    if (user != null && user.getInstitution() != null) {
                        Path path = pathRepository.findById(dto.getPathId())
                            .orElseThrow(() -> new RuntimeException("Path not found with id: " + dto.getPathId()));

                        // Verify the path belongs to the user's institution
                        if (!path.getInstitution().getIdInstitution().equals(user.getInstitution().getIdInstitution())) {
                            return ResponseEntity.badRequest()
                                .body("Le parcours spécifié n'appartient pas à votre institution");
                        }

                        existingUE.setPath(path);
                    }
                }
            }

            UE updatedUE = ueRepository.save(existingUE);

            // Convert to DTO and return
            MCCCUEDTO resultDto = mcccUEMapper.toDTO(updatedUE);
            return ResponseEntity.ok(resultDto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating UE: " + e.getMessage());
        }
    }

    /**
     * Delete a UE by ID
     * DELETE /api/v2/mccc/ues/{id}
     */
    @DeleteMapping("/ues/{id}")
    public ResponseEntity<?> deleteMCCCUE(@PathVariable Long id) {
        try {
            // Vérifier si l'UE existe
            if (!ueRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            // Supprimer l'UE
            ueRepository.deleteById(id);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting UE: " + e.getMessage());
        }
    }
}

