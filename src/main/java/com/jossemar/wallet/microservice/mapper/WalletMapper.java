package com.jossemar.wallet.microservice.mapper;

import com.jossemar.wallet.microservice.model.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    Wallet toModel(com.jossemar.wallet.microservice.domain.Wallet wallet);
    com.jossemar.wallet.microservice.domain.Wallet toDocument(Wallet wallet);
}
