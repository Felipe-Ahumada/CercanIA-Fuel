package cl.fuelonline.finance.domain.repository;

import cl.fuelonline.finance.domain.model.Discount;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DiscountSpecifications {

    private DiscountSpecifications() {}

    /**
     * Descuentos activos para una brand, opcionalmente filtrados por combustible,
     * dia de la semana, date vigente y tarjetas del user.
     *
     * Reglas:
     *  - brand: siempre exigida
     *  - combustibleId null en BD significa "aplica a cualquier combustible"
     *  - dayOfWeek null en BD significa "aplica todos los dias"
     *  - cardProduct null en BD significa "aplica a cualquier medio de pago"
     *  - Si userCardIds esta empty o null, solo se traen descuentos sin
     *    requerimiento de tarjeta (universales)
     */
    public static Specification<Discount> applicable(Integer brandId,
                                                      Integer combustibleId,
                                                      Integer dayOfWeek,
                                                      LocalDate date,
                                                      Collection<Integer> userCardIds) {
        return (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();

            preds.add(cb.equal(root.get("brand").get("id"), brandId));
            preds.add(cb.lessThanOrEqualTo(root.get("startDate"), date));
            preds.add(cb.or(
                    cb.isNull(root.get("endDate")),
                    cb.greaterThanOrEqualTo(root.get("endDate"), date)));

            if (combustibleId != null) {
                preds.add(cb.or(
                        cb.isNull(root.get("fuelType")),
                        cb.equal(root.get("fuelType").get("id"), combustibleId)));
            }

            if (dayOfWeek != null) {
                preds.add(cb.or(
                        cb.isNull(root.get("dayOfWeek")),
                        cb.equal(root.get("dayOfWeek"), dayOfWeek)));
            }

            if (userCardIds == null || userCardIds.isEmpty()) {
                preds.add(cb.isNull(root.get("cardProduct")));
            } else {
                preds.add(cb.or(
                        cb.isNull(root.get("cardProduct")),
                        root.get("cardProduct").get("id").in(userCardIds)));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };
    }
}
