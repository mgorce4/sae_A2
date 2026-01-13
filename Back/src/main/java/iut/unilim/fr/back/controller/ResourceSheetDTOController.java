package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.dto.ResourceSheetDTO;
import iut.unilim.fr.back.dto.ResourceSheetUpdateDTO;
import iut.unilim.fr.back.entity.RessourceSheet;
import iut.unilim.fr.back.mapper.ResourceSheetMapper;
import iut.unilim.fr.back.repository.RessourceSheetRepository;
import iut.unilim.fr.back.service.ResourceSheetUpdateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v2/resource-sheets")
@CrossOrigin(origins = "*")
public class ResourceSheetDTOController {

    private final RessourceSheetRepository ressourceSheetRepository;
    private final ResourceSheetMapper resourceSheetMapper;
    private final ResourceSheetUpdateService resourceSheetUpdateService;

    // Constructor injection (better than @Autowired on fields)
    public ResourceSheetDTOController(
            RessourceSheetRepository ressourceSheetRepository,
            ResourceSheetMapper resourceSheetMapper,
            ResourceSheetUpdateService resourceSheetUpdateService) {
        this.ressourceSheetRepository = ressourceSheetRepository;
        this.resourceSheetMapper = resourceSheetMapper;
        this.resourceSheetUpdateService = resourceSheetUpdateService;
    }

    /**
     * GET /api/v2/resource-sheets
     * Returns all resource sheets as DTOs
     */
    @GetMapping
    public List<ResourceSheetDTO> getAllResourceSheets() {
        List<RessourceSheet> resourceSheets = ressourceSheetRepository.findAll();
        return resourceSheets.stream()
            .map(resourceSheetMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * GET /api/v2/resource-sheets/{id}
     * Returns ONE complete resource sheet with ALL data
     * THIS IS THE MAGIC: one request, all data!
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceSheetDTO> getResourceSheetById(@PathVariable Long id) {
        Optional<RessourceSheet> resourceSheet = ressourceSheetRepository.findById(id);

        if (resourceSheet.isPresent()) {
            ResourceSheetDTO dto = resourceSheetMapper.toDTO(resourceSheet.get());
            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * GET /api/v2/resource-sheets/resource/{resourceId}
     * Returns all resource sheets for a given resource
     */
    @GetMapping("/resource/{resourceId}")
    public List<ResourceSheetDTO> getResourceSheetsByResourceId(@PathVariable Long resourceId) {
        List<RessourceSheet> resourceSheets = ressourceSheetRepository.findByResource_IdResource(resourceId);
        return resourceSheets.stream()
            .map(resourceSheetMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * PUT /api/v2/resource-sheets/{id}
     * Update a resource sheet with new data
     * Handles both creation (new data) and update (modify existing data)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateResourceSheet(@PathVariable Long id, @RequestBody ResourceSheetUpdateDTO updateDTO) {
        try {
            // Verify the resource sheet exists
            Optional<RessourceSheet> resourceSheet = ressourceSheetRepository.findById(id);
            if (!resourceSheet.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            // Update the resource sheet
            resourceSheetUpdateService.updateResourceSheet(id, updateDTO);

            // Return the updated resource sheet
            ResourceSheetDTO updatedDTO = resourceSheetMapper.toDTO(resourceSheet.get());
            return ResponseEntity.ok(updatedDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating resource sheet: " + e.getMessage());
        }
    }
}

