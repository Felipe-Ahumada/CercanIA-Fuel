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
    @Mapping(target = "role",        ignore = true)
    @Mapping(target = "vehicles",  ignore = true)
    @Mapping(target = "active",     constant = "true")
    User toEntity(UserCreateRequest req);

    @Mapping(target = "roleId",     source = "role.id")
    @Mapping(target = "roleName", source = "role.name")
    UserResponse toResponse(User u);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "role",       ignore = true)
    @Mapping(target = "vehicles", ignore = true)
    @Mapping(target = "active",    ignore = true)
    @Mapping(target = "rut",       ignore = true)
    void updateEntity(UserUpdateRequest req, @MappingTarget User entity);
}
