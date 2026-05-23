package cl.fuelonline.catalog.application.service;

import cl.fuelonline.catalog.application.dto.BrandResponse;
import cl.fuelonline.catalog.domain.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;

    public List<BrandResponse> listAll() {
        return brandRepository.findAll().stream()
                .map(b -> new BrandResponse(b.getId(), b.getName()))
                .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
                .toList();
    }
}
