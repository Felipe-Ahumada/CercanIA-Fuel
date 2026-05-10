package cl.fuelonline.user.application.mapper;

import cl.fuelonline.user.application.dto.UserCreateRequest;
import cl.fuelonline.user.application.dto.UserResponse;
import cl.fuelonline.user.application.dto.UserUpdateRequest;
import cl.fuelonline.user.domain.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id",         ignore = true)
    @Mapping(target = "rol",        ignore = true)
    @Mapping(target = "vehiculos",  ignore = true)
    @Mapping(target = "activo",     constant = "true")
    User toEntity(UserCreateRequest req);

    @Mapping(target = "rolId",     source = "rol.id")
    @Mapping(target = "rolNombre", source = "rol.nombre")
    UserResponse toResponse(User u);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "rol",       ignore = true)
    @Mapping(target = "vehiculos", ignore = true)
    @Mapping(target = "activo",    ignore = true)
    @Mapping(target = "rut",       ignore = true)
    void updateEntity(UserUpdateRequest req, @MappingTarget User entity);
}
