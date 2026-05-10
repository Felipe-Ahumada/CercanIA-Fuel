package cl.fuelonline.finance.api;

import cl.fuelonline.finance.application.dto.BankCreateRequest;
import cl.fuelonline.finance.application.dto.BankResponse;
import cl.fuelonline.finance.application.dto.BankUpdateRequest;
import cl.fuelonline.finance.application.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/bancos")
@RequiredArgsConstructor
@Tag(name = "Banks", description = "Catalog of card-issuing banks")
public class BankController {

    private final BankService bankService;

    @GetMapping
    @Operation(summary = "List banks (paged)")
    public Page<BankResponse> list(@ParameterObject Pageable pageable) {
        return bankService.list(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener bank por ID")
    public BankResponse find(@PathVariable Integer id) {
        return bankService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create a new bank")
    public ResponseEntity<BankResponse> create(@Valid @RequestBody BankCreateRequest req,
                                               UriComponentsBuilder uriBuilder) {
        BankResponse created = bankService.create(req);
        URI location = uriBuilder.path("/api/v1/bancos/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar bank")
    public BankResponse update(@PathVariable Integer id,
                                    @Valid @RequestBody BankUpdateRequest req) {
        return bankService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Borrado logico del bank")
    public void delete(@PathVariable Integer id) {
        bankService.delete(id);
    }
}
