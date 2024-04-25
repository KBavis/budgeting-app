package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mappings({
            @Mapping(target = "target.name", source = "source.name"),
            @Mapping(target = "target.username", source = "source.username"),
            @Mapping(target = "target.password", source = "source.password"),
            @Mapping(target = "target.profileImage", source = "source.profileImage"),
            @Mapping(target = "target.linkToken", source = "source.linkToken"),
            @Mapping(target = "target.failedLoginAttempts", source = "source.failedLoginAttempts"),
            @Mapping(target = "target.lockoutEndTime", source = "source.lockoutEndTime"),
            @Mapping(target = "categories", ignore = true),
            @Mapping(target = "incomes", ignore = true),
            @Mapping(target = "accounts", ignore = true),
            @Mapping(target = "authorities", ignore = true)
    })
    void updateUserProfile(@MappingTarget User target, User source);
}
