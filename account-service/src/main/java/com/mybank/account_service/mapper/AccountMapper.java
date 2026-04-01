package com.mybank.account_service.mapper;

import com.mybank.account_service.dto.request.CreateAccountRequest;
import com.mybank.account_service.dto.request.PlaceHoldRequest;
import com.mybank.account_service.dto.request.TransferRequest;
import com.mybank.account_service.dto.response.*;
import com.mybank.account_service.entity.*;
import org.mapstruct.*;
import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "iban", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "balance", ignore = true)
    AccountEntity toEntity(CreateAccountRequest request);

    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "availableBalance", source = "initialBalance")
    @Mapping(target = "blockedBalance", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AccountBalanceEntity toBalanceEntity(CreateAccountRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "releasedAt", ignore = true)
    AccountHoldEntity toHoldEntity(PlaceHoldRequest request);

    @Mapping(target = "balance", source = "balance")
    AccountResponse toResponse(AccountEntity account);

    @Mapping(target = "totalBalance",
            expression = "java(calculateTotal(balance))")
    BalanceResponse toBalanceResponse(AccountBalanceEntity balance);

    HoldResponse toHoldResponse(AccountHoldEntity hold);

    List<AccountResponse> toResponseList(List<AccountEntity> accounts);

    List<HoldResponse> toHoldResponseList(List<AccountHoldEntity> holds);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "iban", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(CreateAccountRequest request, @MappingTarget AccountEntity entity);

    @Mapping(target = "status", constant = "SUCCESS")
    @Mapping(target = "executedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "fromAccountId", source = "fromAccountId")
    @Mapping(target = "toAccountId", source = "toAccountId")
    @Mapping(target = "amount", source = "amount")
    TransferResponse toTransferResponse(TransferRequest request);

    default BigDecimal calculateTotal(AccountBalanceEntity balance) {
        if (balance == null) return BigDecimal.ZERO;
        return balance.getAvailableBalance()
                .add(balance.getBlockedBalance());
    }
}