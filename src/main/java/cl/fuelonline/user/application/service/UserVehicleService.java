package cl.fuelonline.user.application.service;

import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.catalog.domain.model.FuelType;
import cl.fuelonline.catalog.domain.repository.FuelTypeRepository;
import cl.fuelonline.user.application.dto.VehicleCreateRequest;
import cl.fuelonline.user.application.dto.VehicleResponse;
import cl.fuelonline.user.application.exception.UserAlreadyExistsException;
import cl.fuelonline.user.application.mapper.VehicleMapper;
import cl.fuelonline.user.domain.model.User;
import cl.fuelonline.user.domain.model.Vehicle;
import cl.fuelonline.user.domain.model.VehicleModel;
import cl.fuelonline.user.domain.repository.UserRepository;
import cl.fuelonline.user.domain.repository.VehicleModelRepository;
import cl.fuelonline.user.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserVehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final UserRepository userRepository;
    private final VehicleMapper vehicleMapper;

    public List<VehicleResponse> getMyVehicles(UUID userId) {
        return vehicleRepository.findAllByUser_Id(userId).stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    @Transactional
    public VehicleResponse addVehicle(UUID userId, VehicleCreateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        VehicleModel model = vehicleModelRepository.findById(req.vehicleModelId())
                .orElseThrow(() -> new ResourceNotFoundException("VehicleModel not found: " + req.vehicleModelId()));
        FuelType fuelType = fuelTypeRepository.findById(req.fuelTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("FuelType not found: " + req.fuelTypeId()));

        if (vehicleRepository.existsByLicensePlateIgnoreCase(req.licensePlate())) {
            throw new UserAlreadyExistsException("License plate already registered: " + req.licensePlate());
        }

        Vehicle vehicle = Vehicle.builder()
                .user(user)
                .model(model)
                .fuelType(fuelType)
                .licensePlate(req.licensePlate().toUpperCase().trim())
                .year(req.year())
                .build();

        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(UUID userId, UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + vehicleId));
        if (!vehicle.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Vehicle does not belong to the user");
        }
        vehicle.setActive(false);
    }
}