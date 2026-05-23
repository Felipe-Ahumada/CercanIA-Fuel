package cl.fuelonline.chat.application.service;

import cl.fuelonline.chat.application.dto.ChatRequest;
import cl.fuelonline.chat.application.dto.ChatResponse;
import cl.fuelonline.chat.infrastructure.GeminiClient;
import cl.fuelonline.station.application.service.StationService;
import cl.fuelonline.transaction.application.service.TransactionService;
import cl.fuelonline.user.application.dto.VehicleResponse;
import cl.fuelonline.user.application.service.UserVehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final GeminiClient geminiClient;
    private final UserVehicleService userVehicleService;
    private final StationService stationService;
    private final TransactionService transactionService;

    public ChatResponse chat(UUID userId, ChatRequest request) {
        String systemPrompt = buildSystemPrompt(userId, request);
        String reply = geminiClient.generate(systemPrompt, request.prompt());
        return new ChatResponse(UUID.randomUUID().toString(), "ai", reply, Instant.now());
    }

    private String buildSystemPrompt(UUID userId, ChatRequest request) {
        var sb = new StringBuilder();

        sb.append("""
                Eres CercanIA, un asistente inteligente especializado en combustibles en Chile.
                Ayudas a los usuarios a encontrar las mejores estaciones de servicio,
                calcular ahorros con sus tarjetas bancarias y gestionar sus vehículos.
                Responde siempre en español, de forma concisa y útil.
                No inventes precios ni datos que no tengas. Si no tienes información, dilo claramente.
                IMPORTANTE: Cuando menciones estaciones de servicio, usa SIEMPRE su marca comercial
                (ej. COPEC, Shell, Aramco, Petrobras, Terpel, Abastible) y NO la razón social legal
                de la empresa. Por ejemplo, di "COPEC" en lugar de "Empresas Copec S.A.".
                """);

        // User vehicles context
        List<VehicleResponse> vehicles = userVehicleService.getMyVehicles(userId);
        if (!vehicles.isEmpty()) {
            sb.append("\n== Vehículos del usuario ==\n");
            vehicles.forEach(v -> sb.append(String.format(
                    "- %s %s (%s) — combustible: %s%n",
                    v.brandName(), v.modelName(),
                    v.licensePlate() != null ? v.licensePlate() : "sin patente",
                    v.fuelTypeName() != null ? v.fuelTypeName() : "desconocido")));
        }

        // Nearby stations context
        if (request.latitude() != null && request.longitude() != null) {
            var nearbyStations = stationService.findNearby(request.latitude(), request.longitude(), 5.0);
            if (!nearbyStations.isEmpty()) {
                sb.append("\n== Estaciones cercanas (radio 5 km) ==\n");
                nearbyStations.stream().limit(5).forEach(s -> {
                    sb.append(String.format("- %s (%s) — %s",
                            s.name(), s.brand(),
                            s.address() != null ? s.address() : "sin dirección"));
                    if (s.distanciaKm() != null) {
                        sb.append(String.format(" [%.1f km]", s.distanciaKm()));
                    }
                    if (s.prices() != null && !s.prices().isEmpty()) {
                        sb.append(" | Precios: ");
                        s.prices().forEach(p -> sb.append(String.format("%s $%s  ",
                                p.fuelTypeName(), p.price().toPlainString())));
                    }
                    sb.append("\n");
                });
            }
        }

        // Recent transactions context
        transactionService.listByUser(userId, PageRequest.of(0, 5))
                .getContent()
                .forEach(tx -> {
                    if (sb.indexOf("== Últimas cargas ==") < 0) {
                        sb.append("\n== Últimas cargas ==\n");
                    }
                    BigDecimal saved = tx.discountAmount() != null ? tx.discountAmount() : BigDecimal.ZERO;
                    String brand = tx.stationBrand() != null ? tx.stationBrand() : tx.stationName();
                    sb.append(String.format("- %s en %s: $%.0f (ahorro $%.0f)%n",
                            tx.fuelTypeName() != null ? tx.fuelTypeName() : "combustible",
                            brand, tx.grossAmount(), saved));
                });

        return sb.toString();
    }
}