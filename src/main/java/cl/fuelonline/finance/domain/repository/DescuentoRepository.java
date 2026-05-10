package cl.fuelonline.finance.domain.repository;

import cl.fuelonline.finance.domain.model.Descuento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DescuentoRepository
        extends JpaRepository<Descuento, Integer>, JpaSpecificationExecutor<Descuento> {

    List<Descuento> findAllByMarca_IdOrderByValorDescuentoDesc(Integer marcaId);
}
