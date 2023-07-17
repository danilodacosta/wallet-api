package com.wallet.service;

import com.wallet.entity.Wallet;

import java.util.Optional;

public interface WalletService {

    Wallet save(Wallet wallet);
    Optional<Wallet>findByName(String name);

}
