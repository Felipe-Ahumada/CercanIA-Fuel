package cl.fuelonline.finance.domain.repository;

import cl.fuelonline.finance.domain.model.Descuento;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DescuentoSpecifications {

    private DescuentoSpecifications() {}

    /**
     * Descuentos activos para una marca, opcionalmente filtrados por combustible,
     * dia de la semana, fecha vigente y tarjetas del usuario.
     *
     * Reglas:
     *  - marca: siempre exigida
     *  - combustibleId null en BD significa "aplica a cualquier combustible"
     *  - diaSemana null en BD significa "aplica todos los dias"
     *  - tarjetaProducto null en BD significa "aplica a cualquier medio de pago"
     *  - Si tarjetasUsuarioIds esta vacio o null, solo se traen descuentos sin
     *    requerimiento de tarjeta (universales)
     */
    public static Specification<Descuento> aplicables(Integer marcaId,
                                                      Integer combustibleId,
                                                      Integer diaSemana,
                                                      LocalDate fecha,
                                                      Collection<Integer> tarjetasUsuarioIds) {
        return (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();

            preds.add(cb.equal(root.get("marca").get("id"), marcaId));
            preds.add(cb.lessThanOrEqualTo(root.get("fechaInicio"), fecha));
            preds.add(cb.or(
                    cb.isNull(root.get("fechaFin")),
                    cb.greaterThanOrEqualTo(root.get("fechaFin"), fecha)));

            if (combustibleId != null) {
                preds.add(cb.or(
                        cb.isNull(root.get("tipoCombustible")),
                        cb.equal(root.get("tipoCombustible").get("id"), combustibleId)));
            }

            if (diaSemana != null) {
                preds.add(cb.or(
                        cb.isNull(root.get("diaSemana")),
                        cb.equal(root.get("diaSemana"), diaSemana)));
            }

            if (tarjetasUsuarioIds == null || tarjetasUsuarioIds.isEmpty()) {
                preds.add(cb.isNull(root.get("tarjetaProducto")));
            } else {
                preds.add(cb.or(
                        cb.isNull(root.get("tarjetaProducto")),
                        root.get("tarjetaProducto").get("id").in(tarjetasUsuarioIds)));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };
    }
}
