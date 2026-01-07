package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.dto.admin.MCCCResourceDTO;
import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.mapper.MCCCMapper;
import iut.unilim.fr.back.repository.RessourceRepository;
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
    private MCCCMapper mcccMapper;

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
}

