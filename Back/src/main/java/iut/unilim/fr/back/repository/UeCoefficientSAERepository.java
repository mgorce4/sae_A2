package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.UeCoefficientSAE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UeCoefficientSAERepository extends JpaRepository<UeCoefficientSAE, Long> {
    List<UeCoefficientSAE> findByUe_UeNumber(Long ueNumber);
    List<UeCoefficientSAE> findBySae_IdSAE(Long idSAE);
}

