package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.Path;
import iut.unilim.fr.back.repository.PathRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PathService {
    @Autowired
    private PathRepository pathRepository;

    public List<Path> getAllPaths() {
        return pathRepository.findAll();
    }

    public Optional<Path> getPathById(Long id) {
        return pathRepository.findById(id);
    }

    public Path createPath(Path path) {
        return pathRepository.save(path);
    }

    public Path updatePath(Long id, Path path) {
        path.setIdPath(id);
        return pathRepository.save(path);
    }

    public void deletePath(Long id) {
        pathRepository.deleteById(id);
    }
}

