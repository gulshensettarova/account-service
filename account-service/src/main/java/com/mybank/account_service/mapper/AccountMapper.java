package com.mybank.account_service.mapper;

import com.mybank.account_service.dto.request.CreateAccountRequest;
import com.mybank.account_service.dto.response.AccountResponse;
import com.mybank.account_service.entity.AccountEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "accountId", source = "id")
    @Mapping(target = "owner",     source = "customerId")
    @Mapping(target = "balance",   source = "balance")
    @Mapping(target = "createdDate", source = "createdAt", dateFormat = "dd-MM-yyyy")
    AccountResponse toResponse(AccountEntity account);

    @Mapping(target = "balance",  ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    AccountEntity toEntity(CreateAccountRequest request);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version",   ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    void updateEntity(CreateAccountRequest request, @MappingTarget AccountEntity entity
    );
}