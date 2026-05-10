package cl.fuelonline.transaction.application.service;

import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.station.domain.model.FuelType;
import cl.fuelonline.station.domain.repository.StationRepository;
import cl.fuelonline.station.domain.repository.FuelTypeRepository;
import cl.fuelonline.finance.domain.model.Discount;
import cl.fuelonline.finance.domain.model.CardProduct;
import cl.fuelonline.finance.domain.repository.DiscountRepository;
import cl.fuelonline.finance.domain.repository.CardProductRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.transaction.application.dto.ExpenseSummaryResponse;
import cl.fuelonline.transaction.application.dto.TransactionCreateRequest;
import cl.fuelonline.transaction.application.dto.TransactionResponse;
import cl.fuelonline.transaction.application.exception.InvalidTransactionException;
import cl.fuelonline.transaction.application.mapper.TransactionMapper;
import cl.fuelonline.transaction.domain.model.Transaction;
import cl.fuelonline.transaction.domain.repository.TransactionRepository;
import cl.fuelonline.user.domain.model.User;
import cl.fuelonline.user.domain.model.Vehicle;
import cl.fuelonline.user.domain.repository.UserRepository;
import cl.fuelonline.user.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final StationRepository stationRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final CardProductRepository cardProductRepository;
    private final DiscountRepository discountRepository;
    private final TransactionMapper mapper;

    public TransactionResponse findById(UUID id) {
        return mapper.toResponse(get(id));
    }

    public Page<TransactionResponse> listByUser(UUID userId, Pageable pageable) {
        return transactionRepository
                .findAllByUser_IdOrderByTransactionDateDesc(userId, pageable)
                .map(mapper::toResponse);
    }

    public Page<TransactionResponse> listByUserBetween(UUID userId,
                                                           LocalDate desde,
                                                           LocalDate hasta,
                                                           Pageable pageable) {
        LocalDateTime start = desde.atStartOfDay();
        LocalDateTime endTime = hasta.atTime(LocalTime.MAX);
        return transactionRepository
                .findAllByUser_IdAndTransactionDateBetweenOrderByTransactionDateDesc(
                        userId, start, endTime, pageable)
                .map(mapper::toResponse);
    }

    public ExpenseSummaryResponse expenseSummary(UUID userId, LocalDate desde, LocalDate hasta) {
        LocalDateTime start = desde.atStartOfDay();
        LocalDateTime endTime = hasta.atTime(LocalTime.MAX);
        BigDecimal total   = transactionRepository.sumTotalSpent(userId, start, endTime);
        BigDecimal savings  = transactionRepository.sumTotalSaved(userId, start, endTime);
        BigDecimal liters  = transactionRepository.sumTotalLiters(userId, start, endTime);
        long fills = transactionRepository
                .findAllByUser_IdAndTransactionDateBetweenOrderByTransactionDateDesc(
                        userId, start, endTime, Pageable.unpaged())
                .getTotalElements();
        return new ExpenseSummaryResponse(desde, hasta, total, savings, liters, fills);
    }

    @Transactional
    public TransactionResponse register(TransactionCreateRequest req) {
        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + req.userId()));

        Vehicle vehicle = vehicleRepository.findById(req.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + req.vehicleId()));

        if (!vehicle.getUser().getId().equals(user.getId())) {
            throw new InvalidTransactionException("The vehicle does not belong to the specified user");
        }

        Station station = stationRepository.findById(req.stationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + req.stationId()));

        FuelType fuelType = fuelTypeRepository.findById(req.fuelTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fuel type not found: " + req.fuelTypeId()));

        CardProduct tarjeta = null;
        if (req.cardProductId() != null) {
            tarjeta = cardProductRepository.findById(req.cardProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Card product not found: " + req.cardProductId()));
        }

        Discount discount = null;
        if (req.discountId() != null) {
            discount = discountRepository.findById(req.discountId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Discount not found: " + req.discountId()));
        }

        BigDecimal grossAmount = req.unitPrice()
                .multiply(req.liters())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal discountAmount = req.discountAmount() != null
                ? req.discountAmount().setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        if (discountAmount.compareTo(grossAmount) > 0) {
            throw new InvalidTransactionException("El discount no puede ser mayor al monto bruto");
        }

        BigDecimal finalAmount = grossAmount.subtract(discountAmount);

        Transaction entity = Transaction.builder()
                .user(user)
                .vehicle(vehicle)
                .station(station)
                .fuelType(fuelType)
                .cardProduct(tarjeta)
                .discount(discount)
                .unitPrice(req.unitPrice())
                .liters(req.liters())
                .grossAmount(grossAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .transactionDate(req.transactionDate() != null
                        ? req.transactionDate()
                        : LocalDateTime.now())
                .notes(req.notes())
                .build();

        return mapper.toResponse(transactionRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        Transaction t = get(id);
        transactionRepository.delete(t);
    }

    private Transaction get(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));
    }
}
