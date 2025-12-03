package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.PedagogicalContent;
import iut.unilim.fr.back.repository.PedagogicalContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PedagogicalContentService {
    @Autowired
    private PedagogicalContentRepository pedagogicalContentRepository;

    public List<PedagogicalContent> getAllPedagogicalContents() {
        return pedagogicalContentRepository.findAll();
    }

    public Optional<PedagogicalContent> getPedagogicalContentById(Long id) {
        return pedagogicalContentRepository.findById(id);
    }

    public PedagogicalContent createPedagogicalContent(PedagogicalContent pedagogicalContent) {
        return pedagogicalContentRepository.save(pedagogicalContent);
    }

    public PedagogicalContent updatePedagogicalContent(Long id, PedagogicalContent pedagogicalContent) {
        pedagogicalContent.setIdPedagogicalContent(id);
        return pedagogicalContentRepository.save(pedagogicalContent);
    }

    public void deletePedagogicalContent(Long id) {
        pedagogicalContentRepository.deleteById(id);
    }
}

