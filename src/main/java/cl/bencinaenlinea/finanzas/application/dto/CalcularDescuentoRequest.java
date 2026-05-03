package cl.bencinaenlinea.finanzas.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Solicitud para calcular el mejor descuento aplicable a una compra")
public record CalcularDescuentoRequest(

        @NotNull @Positive Integer marcaId,
        @NotNull @Positive Integer tipoCombustibleId,

        @Schema(description = "Monto bruto de la compra (sin descuento)", example = "25000")
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal montoBruto,

        @Schema(description = "IDs de tarjetas-producto que tiene el usuario. Vacio = sin tarjetas")
        List<@Positive Integer> tarjetasUsuarioIds,

        @Schema(description = "Fecha de la compra. Si es null, se usa la fecha de hoy.")
        LocalDate fecha
) {}
